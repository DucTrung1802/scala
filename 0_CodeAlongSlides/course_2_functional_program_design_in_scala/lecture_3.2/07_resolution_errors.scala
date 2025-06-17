def f(using n: Int) = println(s"Received: $n")

@main def demoMissingGiven(): Unit =
  // f // Uncommenting will cause a compile error: no given Int found

  given intVal: Int = 42
  f // Now it works
