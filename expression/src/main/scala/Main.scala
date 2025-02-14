import expr._

object Main extends App {
  val expr1: Expr = Sum(Number(3), Number(4))
  println(Expr.show(expr1))
  println(Expr.eval(expr1))

  val expr2: Expr = Prod(expr1, Var("x"))
  println(Expr.show(expr2))
}
