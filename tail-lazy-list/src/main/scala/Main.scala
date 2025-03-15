object Main extends App {
  trait TailLazyList[+A] extends Seq[A] {
    override def isEmpty: Boolean

    def head: A

    def lazyTail: () => TailLazyList[A]

    override def apply(i: Int): A = {
      if (i == 0) head
      else tail.apply(i - 1)
    }

    override def length: Int = {
      if (isEmpty) 0 else 1 + tail.length
    }

    override def iterator: Iterator[A] = new Iterator[A] {
      private var current: TailLazyList[A] = TailLazyList.this
      override def hasNext: Boolean = !current.isEmpty
      override def next(): A = {
        val result = current.head
        current = current.lazyTail()
        result
      }
    }
  }
}

// case object Empty extends TailLazyList[Nothing] {
//   override def isEmpty: Boolean = true
//   override def head: Nothing = throw new NoSuchElementException("Empty.head")
//   override def tail: () => TailLazyList[Nothing] = () =>
//     throw new NoSuchElementException("Empty.tail")
// }

// final case class Cons[A](hd: A, tl: () => TailLazyList[A])
//     extends TailLazyList[A] {
//   override def isEmpty: Boolean = false
//   override def head: A = hd
//   override def tail: () => TailLazyList[A] = tl
//   override def toString: String = s"LazyList($head, ?)"
// }

// object TailLazyList {
//   def cons[T](hd: T, tl: () => TailLazyList[T]): TailLazyList[T] =
//     Cons(hd, tl)
//   def empty[T]: TailLazyList[T] = Empty
// }
// }
