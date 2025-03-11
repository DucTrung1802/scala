package quickcheck

import org.scalacheck.*
import Arbitrary.*
import Gen.*
import Prop.forAll
import org.w3c.dom.Node

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap:
  lazy val genHeap: Gen[H] = oneOf(
    const(empty),
    for {
      x <- arbitrary[Int]
    } yield insert(x, empty),
    for {
      x <- arbitrary[Int]
      h <- genHeap
    } yield insert(x, h)
  )
  given Arbitrary[H] = Arbitrary(genHeap)

  def getSortedSeq(h: H): List[Int] =
    if isEmpty(h) then Nil else findMin(h) :: getSortedSeq(deleteMin(h))

  property("gen1") = forAll { (h: H) =>
    val m = if isEmpty(h) then 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("sortedAfterInsertions") = forAll { (a: Int, b: Int, c: Int) =>
    val h = insert(c, insert(b, insert(a, empty)))
    getSortedSeq(h) == List(a, b, c).sorted
  }

  property("deleteMinMaintainsOrder") = forAll { (h: H) =>
    val sortedSeq = getSortedSeq(h)
    sortedSeq == sortedSeq.sorted
  }

  property("meldMin") = forAll { (h1: H, h2: H) =>
    val h = meld(h1, h2)
    if isEmpty(h1) || isEmpty(h2) then true
    else findMin(h) == Math.min(findMin(h1), findMin(h2))
  }
