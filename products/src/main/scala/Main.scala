object Main extends App {
  def product(f: Int => Int)(a: Int, b: Int): Int =
    if (a > b) 1
    else f(a) * product(f)(a + 1, b)

  assert(product(x => x * x)(1, 5) == 14400)

  def fact(n: Int): Int = product(x => x)(1, n)

  assert(fact(5) == 120)

  def mapReduce(f: Int => Int, combine: (Int, Int) => Int, zero: Int)(
      a: Int,
      b: Int
  ): Int = {
    def recur(a: Int): Int =
      if (a > b) zero
      else combine(f(a), recur(a + 1))
    recur(a)
  }

  def sum(f: Int => Int): (Int, Int) => Int = mapReduce(f, (a, b) => a + b, 0)
  def products(f: Int => Int): (Int, Int) => Int =
    mapReduce(f, (a, b) => a * b, 1)

  assert(sum(x => x * x)(2, 3) == 13)
  assert(product(x => x * x)(2, 3) == 36)
}
