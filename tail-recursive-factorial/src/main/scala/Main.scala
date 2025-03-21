import scala.annotation.tailrec

object Main extends App {
  
  def factorial(n: Int): Int = {
    @tailrec
    def factorialAcc(current: Int, acc: Int): Int = {
      if (current == 1) acc else factorialAcc(current - 1, acc * current)
    }
    factorialAcc(n, 1)
  }

  println(factorial(5))
}
