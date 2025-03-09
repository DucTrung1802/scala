import scala.util.Random

// Define a Generator trait
trait Generator[+T] {
  def generate(): T

  def map[S](f: T => S) = new Generator[S] {
    def generate(): S = f(Generator.this.generate())
  }

  def flatMap[S](f: T => Generator[S]) = new Generator[S] {
    def generate(): S = f(Generator.this.generate()).generate()
  }
}

// Integer generator (randomly generates an integer)
val integers: Generator[Int] = new Generator[Int] {
  val rand = new Random()
  def generate(): Int =
    rand.nextInt(100) // Generates a random integer from 0 to 99
}

// Boolean generator (randomly returns true or false)
val booleans = new Generator[Boolean] {
  def generate(): Boolean = integers.generate() > 0
}

// Generator for pairs of integers
def pairs[T, U](t: Generator[T], u: Generator[U]) =
  t.flatMap(x => u.map(y => (x, y)))

// Function to generate one of the given values
def oneOf[T](xs: T*): Generator[T] = new Generator[T] {
  val rand = new Random()
  def generate(): T = xs(rand.nextInt(xs.length))
}

// Function to generate a single fixed value
def single[T](x: T): Generator[T] = new Generator[T] {
  def generate(): T = x
}

// Color selection generator
val choice: Generator[String] = oneOf(
  "red",
  "green",
  "blue",
  "yellow",
  "orange",
  "purple",
  "pink",
  "brown",
  "black",
  "white",
  "gray",
  "cyan",
  "magenta"
)

println(choice.generate()) // Randomly prints one of the colors

// Recursive list generator
def lists: Generator[List[Int]] =
  for {
    isEmpty <- booleans
    list <- if (isEmpty) emptyLists else nonEmptyLists
  } yield list

// Generator for empty lists
def emptyLists: Generator[List[Int]] = single(List())

// Generator for non-empty lists
def nonEmptyLists: Generator[List[Int]] =
  for {
    head <- integers
    tail <- lists
  } yield head :: tail

println(lists.generate()) // Generates a random List[Int]

// Define Tree structure
sealed trait Tree
case class Inner(left: Tree, right: Tree) extends Tree
case class Leaf(x: Int) extends Tree

// Function to test generators
def test[T](g: Generator[T], numTimes: Int = 100)(test: T => Boolean): Unit = {
  for (_ <- 0 until numTimes) {
    val value = g.generate()
    assert(test(value), s"Test failed for $value")
  }
  println(s"Passed $numTimes tests")
}

// Corrected test case for pairs of integers
test(pairs(lists, lists)) { case (xs, ys) =>
  (xs ++ ys).length >= xs.length
}
