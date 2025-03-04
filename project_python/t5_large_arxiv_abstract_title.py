from pyspark.sql import SparkSession
from sparknlp.base import DocumentAssembler
from sparknlp.annotator import T5Transformer
from pyspark.ml import Pipeline
import pickle

# Settings
T5_LARGE_ARXIV_ABSTRACT_TITLE_NAME = "t5_large_arxiv_abstract_title"

# Initialize Spark session
spark = (
    SparkSession.builder.appName("SparkNLP")
    .config("spark.driver.memory", "16G")
    .config("spark.kryoserializer.buffer.max", "1000M")
    .config("spark.jars.packages", "com.johnsnowlabs.nlp:spark-nlp_2.12:5.1.3")
    .getOrCreate()
)

documentAssembler = DocumentAssembler().setInputCol("text").setOutputCol("document")

t5 = None
try:
    with open(f"{T5_LARGE_ARXIV_ABSTRACT_TITLE_NAME}.pkl", "rb") as file:
        t5 = pickle.load(file)
except:
    print(
        f"Failed to load T5 model. Downloading {T5_LARGE_ARXIV_ABSTRACT_TITLE_NAME} model."
    )
    t5 = T5Transformer.pretrained("t5_large_arxiv_abstract_title", "en")
    with open(f"{T5_LARGE_ARXIV_ABSTRACT_TITLE_NAME}.pkl", "wb") as file:
        pickle.dump(t5, file)

t5.setInputCols(["document"]).setOutputCol("output")

pipeline = Pipeline().setStages([documentAssembler, t5])
data = spark.createDataFrame([["I love spark-nlp"]]).toDF("text")
pipelineModel = pipeline.fit(data)
pipelineDF = pipelineModel.transform(data)
