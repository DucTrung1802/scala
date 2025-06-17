given Ordering[Double] with
  def compare(x: Double, y: Double): Int = x.compare(y)

def sort[T](xs: List[T])(using Ordering[T]): List[T] = xs.sorted

@main def demoAnonymousGiven(): Unit =
  val nums = List(3.2, 1.5, 2.7)
  println(sort(nums))
