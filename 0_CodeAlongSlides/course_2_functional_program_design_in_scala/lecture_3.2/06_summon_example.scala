given intOrd: Ordering[Int] with
  def compare(x: Int, y: Int): Int = x.compare(y)

@main def demoSummon(): Unit =
  val ord = summon[Ordering[Int]]
  println(ord.compare(2, 5))
