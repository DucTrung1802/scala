object Main extends App {
  type Glass = Int
  type State = Vector[Int]

  class Pouring(full: State) {

    sealed trait Move
    case class Empty(glass: Glass) extends Move
    case class Fill(glass: Glass) extends Move
    case class Pour(from: Glass, to: Glass) extends Move

    object Move {
      def apply(move: Move, state: State): State = move match {
        case Empty(glass) => state.updated(glass, 0)
        case Fill(glass)  => state.updated(glass, full(glass))
        case Pour(from, to) =>
          val amount = math.min(state(from), full(to) - state(to))
          state
            .updated(from, state(from) - amount)
            .updated(to, state(to) + amount)
      }
    }

    val glasses: Range = 0 until (full.length)
    val moves: List[Move] =
      (glasses.map(this.Empty.apply)
        ++ glasses.map(this.Fill.apply)
        ++ glasses.flatMap(g1 =>
          glasses.withFilter(g2 => g1 != g2).map(g2 => Pour(g1, g2))
        )).toList

    case class Path(history: List[Move], endState: State) {
      def extend(move: Move): Path =
        Path(move :: history, Move.apply(move, endState))
      override def toString(): String =
        s"${history.reverse.mkString(" ")} --> $endState"
    }
  }
}
