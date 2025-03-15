object Main extends App {
  def expr = {
    val x = { println("x"); 1 } // Returns 1
    lazy val y = { println("y"); 2 } // Returns 2
    def z = { println("z"); 3 } // Returns 3
    z + y + x + z + y + x // Evaluates to sum of numbers
  }

  println(expr)
  // Side effects
  // x  // x is evaluated after declare as a val
  // z  // z is evaluated when it is called
  // y  // y is evaluated when it is called the first time, and then not be evaluated anymore
  // z  // z is evaluated when it is called, again
  // 12
}
