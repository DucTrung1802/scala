object Main extends App {
  abstract class Nat {
    def isZero: Boolean
    def predecessor: Nat
    def successor: Nat
    def +(that: Nat): Nat
    def -(that: Nat): Nat
  }

  object Zero extends Nat {
    def isZero: Boolean = true
    def predecessor: Nat = throw new UnsupportedOperationException(
      "Zero has no predecessor"
    )
    def successor: Nat = new Succ(this)
    def +(that: Nat): Nat = that
    def -(that: Nat): Nat =
      if (that.isZero) this
      else
        throw new UnsupportedOperationException("Negative result not allowed")
    override def toString: String = "Zero"
  }

  class Succ(n: Nat) extends Nat {
    def isZero: Boolean = false
    def predecessor: Nat = n
    def successor: Nat = new Succ(this)
    def +(that: Nat): Nat = new Succ(n + that)
    def -(that: Nat): Nat =
      if (that.isZero) this
      else n - that.predecessor
    override def toString: String = s"Succ(${n.toString})"
  }

  // Example usage
  val zero = Zero
  val one = zero.successor
  val two = one.successor
  val three = two.successor

  println(two + one) // Prints Succ(Succ(Succ(Zero))) (three)
  println(three - one) // Prints Succ(Succ(Zero)) (two)
  println(zero + three) // Prints Succ(Succ(Succ(Zero))) (three)
  println(three - three) // Prints Zero
}
