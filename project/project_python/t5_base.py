from pyspark.sql import SparkSession
from sparknlp.base import DocumentAssembler
from sparknlp.annotator import T5Transformer
from pyspark.ml import Pipeline
import pickle

# Settings
T5_BASE = "t5_base"

# Initialize Spark session
spark = (
    SparkSession.builder.appName("SparkNLP")
    .config("spark.driver.memory", "16G")
    .config("spark.kryoserializer.buffer.max", "1000M")
    .config("spark.jars.packages", "com.johnsnowlabs.nlp:spark-nlp_2.12:5.1.3")
    .getOrCreate()
)


document_assembler = DocumentAssembler().setInputCol("text").setOutputCol("documents")

t5 = (
    T5Transformer()
    .pretrained("t5_base")
    .setTask("summarize:")
    .setMaxOutputLength(200)
    .setInputCols(["documents"])
    .setOutputCol("summaries")
)

pipeline = Pipeline().setStages([document_assembler, t5])
results = pipeline.fit(data_df).transform(data_df)
