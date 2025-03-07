from pyspark.sql import SparkSession
from pyspark.sql.functions import concat, lit
from pyspark.sql import SparkSession
from pyspark.sql.functions import concat, lit
from pyspark.sql import SparkSession
from pyspark.sql.functions import concat, lit
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
from tqdm import tqdm
import numpy as np
from transformers import T5Tokenizer, T5ForConditionalGeneration, AdamW
import torch
import mlflow
from sacrebleu import corpus_bleu
from rouge import Rouge


spark = SparkSession.builder.appName("arXivTitles").getOrCreate()
# mlflow.set_tracking_uri("file:/home/cvuser/uyen/project/project_python/mlflow")

BATCH_SIZE = 8
PATIENCE = 5

data = (
    spark.read.option("header", True)
    .option("inferSchema", True)
    .option("multiLine", True)
    .option("quote", '"')
    .option("escape", '"')
    .csv("ML-Arxiv-Papers.csv")
    .drop("Unnamed: 0", "_c0")
)

# Format data for T5
data = data.withColumn("t5_input", concat(lit("summarize: "), data["abstract"]))
data = data.withColumn("t5_output", data["title"])
# Split data into train, validation, and test sets
train_data, val_data, test_data = data.randomSplit([0.8, 0.1, 0.1], seed=42)

# Convert to RDD
train_rdd = train_data.select("t5_input", "t5_output").rdd
val_rdd = val_data.select("t5_input", "t5_output").rdd
test_rdd = test_data.select("t5_input", "t5_output").rdd

# T5 model setup
tokenizer = AutoTokenizer.from_pretrained("t5-small")
model = AutoModelForSeq2SeqLM.from_pretrained("t5-small")
device = torch.device("cuda")
model.to(device)
optimizer = AdamW(model.parameters(), lr=5e-5)


# Function to process a batch
def process_batch(model, tokenizer, batch_inputs, batch_outputs, device, train=True):
    inputs = tokenizer(
        batch_inputs, return_tensors="pt", padding=True, truncation=True, max_length=512
    )
    labels = tokenizer(
        batch_outputs,
        return_tensors="pt",
        padding=True,
        truncation=True,
        max_length=512,
    )["input_ids"]

    input_ids = inputs["input_ids"].to(device)
    attention_mask = inputs["attention_mask"].to(device)
    labels = labels.to(device)

    outputs = model(input_ids=input_ids, attention_mask=attention_mask, labels=labels)
    loss = outputs.loss

    if train:
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()

    return loss.item(), outputs.logits


# Function to train the model
def train_model(model, train_rdd, tokenizer, device):
    model.train()
    total_loss = 0
    train_predictions, train_targets = [], []

    train_batches = train_rdd.collect()
    for i in range(0, len(train_batches), BATCH_SIZE):
        batch = train_batches[i : i + BATCH_SIZE]
        batch_inputs = [t5_input for t5_input, _ in batch]
        batch_outputs = [t5_output for _, t5_output in batch]

        loss, logits = process_batch(
            model, tokenizer, batch_inputs, batch_outputs, device, train=True
        )
        total_loss += loss

        predictions = tokenizer.batch_decode(
            logits.argmax(-1), skip_special_tokens=True
        )
        train_predictions.extend(predictions)
        train_targets.extend(batch_outputs)

    avg_loss = total_loss / (len(train_batches) / BATCH_SIZE)
    return avg_loss, train_predictions, train_targets


# Function to evaluate the model (validation and testing)
def evaluate_model(model, data_rdd, tokenizer, device):
    model.eval()
    total_loss = 0
    all_predictions, all_targets = [], []

    data_batches = data_rdd.collect()
    for i in tqdm(range(0, len(data_batches), BATCH_SIZE)):
        batch = data_batches[i : i + BATCH_SIZE]
        batch_inputs = [t5_input for t5_input, _ in batch]
        batch_outputs = [t5_output for _, t5_output in batch]

        with torch.no_grad():
            loss, logits = process_batch(
                model, tokenizer, batch_inputs, batch_outputs, device, train=False
            )

        total_loss += loss
        predictions = tokenizer.batch_decode(
            logits.argmax(-1), skip_special_tokens=True
        )
        all_predictions.extend(predictions)
        all_targets.extend(batch_outputs)

    avg_loss = total_loss / (len(data_batches) / BATCH_SIZE)
    bleu_score = corpus_bleu(all_predictions, [all_targets]).score
    rouge_scores = Rouge().get_scores(all_predictions, all_targets)
    avg_rouge_l = sum([score["rouge-l"]["f"] for score in rouge_scores]) / len(
        rouge_scores
    )

    return avg_loss, bleu_score, avg_rouge_l


# MLflow tracking with early stopping
best_val_loss = np.inf
epochs_no_improve = 0  # Counter for early stopping

with mlflow.start_run():
    for epoch in tqdm(range(100)):  # Max epochs (will stop early if needed)
        print(f"Epoch {epoch + 1}")

        # Training phase
        train_loss, train_predictions, train_targets = train_model(
            model, train_rdd, tokenizer, device
        )
        bleu_score = corpus_bleu(train_predictions, [train_targets]).score
        rouge_scores = Rouge().get_scores(train_predictions, train_targets)
        avg_rouge_l = sum([score["rouge-l"]["f"] for score in rouge_scores]) / len(
            rouge_scores
        )

        mlflow.log_metrics(
            {
                "train_loss": train_loss,
                "train_bleu": bleu_score,
                "train_rouge_l": avg_rouge_l,
            },
            step=epoch,
        )

        # Validation phase
        val_loss, val_bleu, val_rouge_l = evaluate_model(
            model, val_rdd, tokenizer, device
        )
        mlflow.log_metrics(
            {"val_loss": val_loss, "val_bleu": val_bleu, "val_rouge_l": val_rouge_l},
            step=epoch,
        )

        # Early stopping logic
        if val_loss < best_val_loss:
            best_val_loss = val_loss
            epochs_no_improve = 0
            # Save the best model
            mlflow.pytorch.log_model(model, "best_model")
        else:
            epochs_no_improve += 1

        print(
            f"Validation Loss: {val_loss:.4f} | Best Loss: {best_val_loss:.4f} | No Improvement: {epochs_no_improve}/{PATIENCE}"
        )

        if epochs_no_improve >= PATIENCE:
            print("Early stopping triggered. Stopping training.")
            break  # Stop training

    # Testing phase after training
    test_loss, test_bleu, test_rouge_l = evaluate_model(
        model, test_rdd, tokenizer, device
    )
    mlflow.log_metrics(
        {"test_loss": test_loss, "test_bleu": test_bleu, "test_rouge_l": test_rouge_l}
    )

    # Log the final model
    mlflow.pytorch.log_model(model, "final_model")

# Close Spark session
spark.stop()
