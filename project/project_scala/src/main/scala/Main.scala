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

    val data = Seq("I love spark-nlp").toDS.toDF("text")

    // Process the document separately
    val docDF = new Pipeline()
      .setStages(Array(documentAssembler))
      .fit(data)
      .transform(data)

    // Load the T5 model separately
    val t5Model = T5Transformer
      .load("../t5_large_arxiv_abstract_title_en_5.4.2_3.0")
      .setInputCols(Array("document"))
      .setOutputCol("output")
      .setLazyAnnotator(true) // Prevent Spark from serializing
      .setBatchSize(16)

    // Apply the model transformation outside of the pipeline
    val transformedDF = t5Model.transform(docDF)

    transformedDF.show(false)
  }
}
