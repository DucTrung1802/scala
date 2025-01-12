package funsets

class FunSetSuite extends munit.FunSuite:

  import FunSets.*

  test("contains is implemented") {
    assert(contains(x => true, 100))
  }

  trait TestSets:
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)
    val s4 = singletonSet(4)
    val s5 = singletonSet(5)

  test("singleton set contains its element") {
    new TestSets:
      assert(contains(s1, 1), "Singleton contains 1")
      assert(!contains(s1, 2), "Singleton does not contain 2")
  }

  test("union contains all elements of each set") {
    new TestSets:
      val s = union(s1, s2)
      printSet(s)
      assert(contains(s, 1), "Union contains 1")
      assert(contains(s, 2), "Union contains 2")
      assert(!contains(s, 3), "Union does not contain 3")
  }

  test("intersection contains only common elements") {
    new TestSets:
      val s = intersect(union(s1, s2), union(s2, s3))
      printSet(s)
      assert(!contains(s, 1), "Intersection does not contain 1")
      assert(contains(s, 2), "Intersection contains 2")
      assert(!contains(s, 3), "Intersection does not contain 3")
  }

  test("difference contains elements in the first set but not the second") {
    new TestSets:
      val s = diff(union(s1, s2), s1)
      printSet(s)
      assert(!contains(s, 1), "Difference does not contain 1")
      assert(contains(s, 2), "Difference contains 2")
  }

  test("forall checks if all elements satisfy a predicate") {
    new TestSets:
      val s = union(union(s1, s2), s3)
      printSet(s)
      assert(forall(s, x => x > 0), "All elements are positive")
      assert(!forall(s, x => x == 1), "Not all elements are 1")
  }

  test("exists checks if any element satisfies a predicate") {
    new TestSets:
      val s = union(union(s1, s2), s3)
      printSet(s)
      assert(exists(s, x => x == 2), "There exists an element equal to 2")
      assert(!exists(s, x => x == 4), "No element equals 4")
  }

  test("map transforms a set based on a given function") {
    new TestSets:
      val s = union(union(s1, s2), s3)
      val mapped = map(s, x => x * 2)
      printSet(s)
      printSet(mapped)
      assert(contains(mapped, 2), "Mapped set contains 2 (1*2)")
      assert(contains(mapped, 4), "Mapped set contains 4 (2*2)")
      assert(contains(mapped, 6), "Mapped set contains 6 (3*2)")
      assert(!contains(mapped, 1), "Mapped set does not contain 1")
  }

  import scala.concurrent.duration.*
  override val munitTimeout = 10.seconds
