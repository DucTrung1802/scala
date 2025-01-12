package funsets

object Main extends App:
  import FunSets.*
  println(contains(singletonSet(1), 1))
  println(contains(union(singletonSet(3), singletonSet(4)), 3))
  println(contains(intersect(singletonSet(4), singletonSet(4)), 4))
  println(contains(filter(singletonSet(2), x => x < 3), 2))
