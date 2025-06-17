object MyOrderings:
  given intOrdering: Ordering[Int] with
    def compare(x: Int, y: Int): Int =
      if x < y then -1 else if x > y then 1 else 0

import MyOrderings.given

def sort[T](xs: List[T])(using Ordering[T]): List[T] = xs.sorted

@main def demoGivenInstance(): Unit =
  val nums = List(5, 3, 7)
  println(sort(nums))
