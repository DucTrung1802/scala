trait TailLazyList[+A] extends Seq[A] {
  override def isEmpty: Boolean

  def head: A

  def lazyTail: TailLazyList[A]

  override def apply(i: Int): A = {
    if (i == 0) head
    else lazyTail.apply(i - 1)
  }

  override def length: Int = {
    if (isEmpty) 0
    else 1 + lazyTail.length
  }

  override def iterator: Iterator[A] = new Iterator[A] {
    private var current: TailLazyList[A] = TailLazyList.this
    override def hasNext: Boolean = !current.isEmpty
    override def next(): A = {
      val result = current.head
      current = current.lazyTail
      result
    }
  }
}

case object Empty extends TailLazyList[Nothing] {
  override def isEmpty: Boolean = true
  override def head: Nothing = throw new NoSuchElementException("Empty.head")
  override def lazyTail: TailLazyList[Nothing] =
    throw new NoSuchElementException("Empty.tail")
}

final case class Cons[A](hd: A, tl: TailLazyList[A]) extends TailLazyList[A] {
  override def isEmpty: Boolean = false
  override def head: A = hd
  lazy val lazyTail: TailLazyList[A] = tl
  override def toString: String = s"LazyList($head, ?)"
}

object TailLazyList {
  def cons[T](hd: T, tl: => TailLazyList[T]): TailLazyList[T] =
    Cons(hd, tl)
  def empty[T]: TailLazyList[T] = Empty
  def lazyRange(low: Int, high: Int): TailLazyList[Int] = {
    if (low > high) Empty
    else TailLazyList.cons(low, lazyRange(low + 1, high))
  }
}

lazy val lazyList = TailLazyList.cons(
  1,
  TailLazyList.cons(2, TailLazyList.cons(3, TailLazyList.empty))
)

println(lazyList.toString())
println(lazyList.lazyTail)
println(lazyList.lazyTail.lazyTail)
println(lazyList.lazyTail.lazyTail.lazyTail)

lazy val myIterator = lazyList.iterator
println(myIterator.hasNext)
println(myIterator.next())
println(myIterator.hasNext)
println(myIterator.next())
println(myIterator.hasNext)
println(myIterator.next())
println(myIterator.hasNext)

lazy val myLazyRange = TailLazyList.lazyRange(1, 4)
println(myLazyRange.toString())

lazy val filteredList = myLazyRange.filter(_ % 2 == 0)
println(filteredList.toString())
