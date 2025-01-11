import scala.math.pow

object Main extends App {
  def sum(f: Int => Int): (Int, Int) => Int = {
    def sumF(a: Int, b: Int): Int =
      if (a > b) 0
      else f(a) + sumF(a + 1, b)

    sumF
  }

  def sumInts = sum(x => x)
  def sumCubes = sum(x => x * x * x)

  assert(sumInts(4, 5) == 4 + 5)
  assert(sum(x => x)(4, 5) == 4 + 5)
  assert(sumCubes(2, 3) == pow(2, 3) + pow(3, 3))
  assert(sum(x => x * x * x)(2, 3) == pow(2, 3) + pow(3, 3))
}
