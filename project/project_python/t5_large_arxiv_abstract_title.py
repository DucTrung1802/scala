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
    .config("spark.sql.execution.arrow.pyspark.enabled", "false")
    .config("spark.jars.packages", "com.johnsnowlabs.nlp:spark-nlp_2.12:5.5.3")
    .getOrCreate()
)

documentAssembler = DocumentAssembler().setInputCol("text").setOutputCol("document")

t5 = T5Transformer.load("../t5_large_arxiv_abstract_title_en_5.4.2_3.0")

t5.setInputCols(["document"]).setOutputCol("output")

pipeline = Pipeline().setStages([documentAssembler, t5])
data = spark.createDataFrame([["I love spark-nlp"]]).toDF("text")
pipelineModel = pipeline.fit(data)
pipelineDF = pipelineModel.transform(data)
pipelineDF.select("output.result").show(truncate=False)
