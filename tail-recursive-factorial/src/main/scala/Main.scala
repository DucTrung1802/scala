import scala.annotation.tailrec

object Main extends App {
  @tailrec
  def factorial(n: Int, result: BigInt = 1): BigInt =
    if (n == 0) result
    else factorial(n - 1, n * result);

  println(factorial(50))
}
