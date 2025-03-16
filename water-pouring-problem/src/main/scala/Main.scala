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
      // endState is the newsest state of current Path
      def extend(move: Move): Path =
        Path(move :: history, Move.apply(move, endState))
      override def toString(): String =
        s"${history.reverse.mkString(" ")} --> $endState"
    }

    val empty: State = full.map(x => 0)
    val start = Path(Nil, empty)

    def pathsFrom(
        paths: List[Path],
        explored: Set[State]
    ): LazyList[List[Path]] = {
      val frontier = {
        for (
          path <- paths; move <- moves; next = path.extend(move)
          if !explored.contains(next.endState)
        )
          yield next
      }
      paths #:: pathsFrom(frontier, explored ++ frontier.map(_.endState))
    }

    def solutions(target: Int): LazyList[Path] = {
      for (
        paths <- pathsFrom(List(start), Set(empty));
        path <- paths
        if path.endState.contains(target)
      )
        yield path
    }
  }

  val problem = new Pouring(Vector(4, 7))
  println(problem.solutions(6).head)
}
