import scala.languageFeature.implicitConversions
object Main extends App {
  def from(n: Int): LazyList[Int] = n #:: from(n + 1)

  val nats = from(0)

  // println(nats.take(10).toList)

  def isPrime(n: Int): Boolean =
    if (n < 2) false
    else !(2 to Math.sqrt(n).toInt).exists(n % _ == 0)

  // (1 to 10).foreach(n => println(s"$n: ${isPrime(n)}"))

  def sieve(s: LazyList[Int]): LazyList[Int] =
    s.head #:: sieve(s.tail.filter(_ % s.head != 0))

  val primes = sieve(from(2))

  // println(primes.take(10).toList)

  def sqrtSeq(x: Double): LazyList[Double] = {
    def improve(guess: Double) = (guess + x / guess) / 2
    lazy val guesses: LazyList[Double] = 1 #:: guesses.map(improve)
    guesses
  }

  // println(sqrtSeq(324).take(12).toList)

  def isGoodEnough(guess: Double, x: Double): Boolean =
    ((guess * guess - x) / x).abs < 0.00000000000001

  println(sqrtSeq(100).filter(isGoodEnough(_, 100)).head)
}
