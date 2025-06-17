def merge[T](xs: List[T], ys: List[T])(using Ordering[T]): List[T] =
  sort(xs ++ ys)

def sort[T](xs: List[T])(using Ordering[T]): List[T] = xs.sorted

@main def demoAnonymousUsing(): Unit =
  val a = List(1, 3)
  val b = List(2, 4)
  println(merge(a, b))
