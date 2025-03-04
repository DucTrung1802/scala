import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.Pipeline
import com.johnsnowlabs.nlp.DocumentAssembler
import com.johnsnowlabs.nlp.annotators.seq2seq.T5Transformer

object App {
  def main(args: Array[String]): Unit = {
    // Create Spark Session
    val spark = SparkSession
      .builder()
      .appName("Spark NLP - T5 Transformer")
      .master("local[*]") // Use local mode
      .getOrCreate()

    import spark.implicits._

    // Define Document Assembler
    val documentAssembler = new DocumentAssembler()
      .setInputCol("text")
      .setOutputCol("document")

    // Load Pretrained T5 Transformer
    val t5 = T5Transformer
      .pretrained("t5_large_arxiv_abstract_title", "en")
      .setInputCols(Array("document"))
      .setOutputCol("output")

    // Define Pipeline
    val pipeline = new Pipeline().setStages(Array(documentAssembler, t5))

    // Sample Data
    val data = Seq("I love Spark NLP").toDS.toDF("text")

    // Fit and Transform Data
    val pipelineModel = pipeline.fit(data)
    val pipelineDF = pipelineModel.transform(data)

    // Show Results
    pipelineDF.select("text", "output.result").show(truncate = false)

    // Stop Spark Session
    spark.stop()
  }
}
