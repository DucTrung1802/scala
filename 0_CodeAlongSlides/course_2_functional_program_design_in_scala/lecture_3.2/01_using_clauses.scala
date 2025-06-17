def sort[T](xs: List[T])(using ord: Ordering[T]): List[T] = xs.sorted

@main def demoUsingClause(): Unit =
  val strings = List("banana", "apple", "cherry")
  println(sort(strings)(using Ordering.String))
  println(sort(strings)) // inferred
