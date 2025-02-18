package patmat

class HuffmanSuite extends munit.FunSuite:
  import Huffman.*

  trait TestTrees {
    val t1 = Fork(Leaf('a', 2), Leaf('b', 3), List('a', 'b'), 5)
    val t2 = Fork(
      Fork(Leaf('a', 2), Leaf('b', 3), List('a', 'b'), 5),
      Leaf('d', 4),
      List('a', 'b', 'd'),
      9
    )
  }

  test("weight of a larger tree (10pts)") {
    new TestTrees:
      assertEquals(weight(t1), 5)
  }

  test("chars of a larger tree (10pts)") {
    new TestTrees:
      assertEquals(chars(t2), List('a', 'b', 'd'))
  }

  test("string2chars hello world") {
    assertEquals(
      string2Chars("hello, world"),
      List('h', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd')
    )
  }

  test("make ordered leaf list for some frequency table (15pts)") {
    assertEquals(
      makeOrderedLeafList(List(('t', 2), ('e', 1), ('x', 3))),
      List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 3))
    )
  }

  test("combine of some leaf list (15pts)") {
    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assertEquals(
      combine(leaflist),
      List(Fork(Leaf('e', 1), Leaf('t', 2), List('e', 't'), 3), Leaf('x', 4))
    )
  }

  test("decode and encode a very short text should be identity (10pts)") {
    new TestTrees:
      assertEquals(decode(t1, encode(t1)("ab".toList)), "ab".toList)
  }

  test(
    "codeBits should return the correct bit sequence for a given character"
  ) {
    val table: CodeTable = List(
      ('a', List(0, 1, 0)),
      ('b', List(1, 0, 1)),
      ('c', List(1, 1, 0))
    )

    assert(codeBits(table)('a') == List(0, 1, 0))
    assert(codeBits(table)('b') == List(1, 0, 1))
    assert(codeBits(table)('c') == List(1, 1, 0))
  }

  test(
    "codeBits should return an empty list if the character is not in the table"
  ) {
    val table: CodeTable = List(
      ('a', List(0, 1, 0)),
      ('b', List(1, 0, 1))
    )

    assert(codeBits(table)('z') == Nil)
  }

  test("Single Leaf Node") {
    val tree = Leaf('a', 5)
    assertEquals(convert(tree), List(('a', List())))
  }

  test("Simple Fork with Two Leaves") {
    val tree = Fork(Leaf('a', 2), Leaf('b', 3), List('a', 'b'), 5)
    assertEquals(convert(tree), List(('a', List(0)), ('b', List(1))))
  }

  test("Unbalanced Tree") {
    val tree = Fork(
      Fork(Leaf('a', 1), Leaf('b', 2), List('a', 'b'), 3),
      Leaf('c', 3),
      List('a', 'b', 'c'),
      6
    )
    assertEquals(
      convert(tree),
      List(('a', List(0, 0)), ('b', List(0, 1)), ('c', List(1)))
    )
  }

  test("Larger Tree") {
    val tree = Fork(
      Fork(
        Leaf('a', 1),
        Fork(Leaf('b', 2), Leaf('c', 3), List('b', 'c'), 5),
        List('a', 'b', 'c'),
        6
      ),
      Leaf('d', 4),
      List('a', 'b', 'c', 'd'),
      10
    )
    assertEquals(
      convert(tree),
      List(
        ('a', List(0, 0)),
        ('b', List(0, 1, 0)),
        ('c', List(0, 1, 1)),
        ('d', List(1))
      )
    )
  }

  test("Tree with Only One Character Repeated") {
    val tree = Leaf('x', 10)
    assertEquals(convert(tree), List(('x', List())))
  }

  test("Deeply Nested Unbalanced Tree") {
    val tree = Fork(
      Fork(
        Fork(Leaf('p', 1), Leaf('q', 2), List('p', 'q'), 3),
        Leaf('r', 4),
        List('p', 'q', 'r'),
        7
      ),
      Leaf('s', 5),
      List('p', 'q', 'r', 's'),
      12
    )
    assertEquals(
      convert(tree),
      List(
        ('p', List(0, 0, 0)),
        ('q', List(0, 0, 1)),
        ('r', List(0, 1)),
        ('s', List(1))
      )
    )
  }

  import scala.concurrent.duration.*
  override val munitTimeout = 10.seconds
