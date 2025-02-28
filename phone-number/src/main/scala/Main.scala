object Main extends App {
  class Coder(words: List[String]) {
    val mnemonics = Map(
      "2" -> "ABC",
      "3" -> "DEF",
      "4" -> "GHI",
      "5" -> "JLK",
      "6" -> "MNO",
      "7" -> "PQRS",
      "8" -> "TUV",
      "9" -> "WXYZ"
    )

    /** Maps a letter to the digit it represents */
    /** Map( 'A' -> '2', 'B' -> '2', 'C' -> '2', 'D' -> '3', 'E' -> '3', 'F' ->
      * '3', 'G' -> '4', 'H' -> '4', 'I' -> '4', 'J' -> '5', 'K' -> '5', 'L' ->
      * '5', 'M' -> '6', 'N' -> '6', 'O' -> '6', 'P' -> '7', 'Q' -> '7', 'R' ->
      * '7', 'S' -> '7', 'T' -> '8', 'U' -> '8', 'V' -> '8', 'W' -> '9', 'X' ->
      * '9', 'Y' -> '9', 'Z' -> '9' )
      */
    private val charCode: Map[Char, Char] = {
      val output = mnemonics.flatMap { case (digit, letters) =>
        letters.map(letter => letter -> digit.head)
      }
      output
    }

    /** Maps a word to the digit string it can represent */
    // "Hello" => "HELLO" => "43556"
    // "Scala" => "SCALA" => "72252"
    // "Good" =>"GOOD" => "4663"
    private def wordCode(word: String): String = {
      val output = word.toUpperCase.map(charCode)
      output
    }

    /** Maps a digit string to all words in the dictionary that represent it */
    /** Map(43556 -> List(Hello), 44 -> List(Hi), 72252 -> List(Scala), 433 ->
      * List(Gee), 4663 -> List(Good, Hood), 733 -> List(See))
      */
    private val wordsForNum: Map[String, List[String]] = {
      val output = words.groupBy(wordCode).withDefaultValue(Nil)
      output
    }

    /** All ways to encode a number as a list of words */
    def encode(number: String): Set[List[String]] =
      if (number.isEmpty) Set(List())
      else {
        (1 to number.length).flatMap { i =>
          val prefix = number.take(i)
          val words = wordsForNum(prefix)
          val suffixEncodings = encode(number.drop(i))
          words.flatMap(word => suffixEncodings.map(word :: _))
        }.toSet
      }
  }

  val wordsList =
    List("Hello", "Hi", "Scala", "Gee", "Good", "Hood", "See")
  val coder = new Coder(wordsList)

  // Example usage: Encode "4663" (corresponding to words in dictionary)
  println(coder.encode("4663")) // Output: Set(List("Good"), List("Hood"))
}
