import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.Pipeline
import com.johnsnowlabs.nlp.DocumentAssembler
import com.johnsnowlabs.nlp.annotators.seq2seq.T5Transformer

object App {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Spark NLP Example")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val documentAssembler = new DocumentAssembler()
      .setInputCol("text")
      .setOutputCol("document")

    val t5 = T5Transformer
      .pretrained("t5_large_arxiv_abstract_title", "en")
      .setInputCols(Array("documents"))
      .setOutputCol("output")

    val pipeline = new Pipeline().setStages(Array(documentAssembler, t5))
    val data = Seq("I love spark-nlp").toDS.toDF("text")
    val pipelineModel = pipeline.fit(data)
    val pipelineDF = pipelineModel.transform(data)
  }
}
