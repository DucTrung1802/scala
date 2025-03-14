object Main extends App {
  def abs(x: Double) = if (x >= 0) x else -x

  def sqrt(x: Double) = {
    def sqrtIter(guess: Double): Double =
      if (isGoodEnough(guess)) guess
      else sqrtIter(improve(guess))

    def isGoodEnough(guess: Double) = abs(x / guess - guess) < 1e-10

    def improve(guess: Double) = (guess + x / guess) / 2

    sqrtIter(1.0)
  }

  println(sqrt(100000000000.0))
}
