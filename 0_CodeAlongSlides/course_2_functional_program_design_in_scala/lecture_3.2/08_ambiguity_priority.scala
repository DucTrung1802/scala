trait C:
  def x: Int

given c1: C with
  def x: Int = 1

given c2: C with
  def x: Int = 2

def f(using c: C): Int = c.x

@main def demoAmbiguity(): Unit =
  // println(f) // Uncommenting will trigger ambiguity error

  def withLocalGiven(): Unit =
    given localC: C with
      def x: Int = 99
    println(f)

  withLocalGiven()
