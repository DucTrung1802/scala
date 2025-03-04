from pyspark.sql import SparkSession
from sparknlp.base import DocumentAssembler
from sparknlp.annotator import T5Transformer
from pyspark.ml import Pipeline

# Initialize Spark session
spark = (
    SparkSession.builder.appName("SparkNLP")
    .config("spark.driver.memory", "16G")
    .config("spark.kryoserializer.buffer.max", "1000M")
    .getOrCreate()
)

documentAssembler = DocumentAssembler().setInputCol("text").setOutputCol("document")

t5 = (
    T5Transformer.pretrained("t5_large_arxiv_title_abstract", "en")
    .setInputCols(["document"])
    .setOutputCol("output")
)

pipeline = Pipeline().setStages([documentAssembler, t5])
data = spark.createDataFrame([["I love spark-nlp"]]).toDF("text")
pipelineModel = pipeline.fit(data)
pipelineDF = pipelineModel.transform(data)
