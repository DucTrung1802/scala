def sort[T](xs: List[T])(using Ordering[T]): List[T] = xs.sorted

def printSorted[T: Ordering](xs: List[T]): Unit =
  println(sort(xs))

@main def demoContextBound(): Unit =
  printSorted(List(3, 1, 2))
