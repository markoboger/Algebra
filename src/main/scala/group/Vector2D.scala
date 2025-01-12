import spire.algebra.Group

case class Vector2D(x: Double, y: Double)

given Vector2DGroup: Group[Vector2D] with {
  override def empty: Vector2D = Vector2D(0.0, 0.0)
  override def combine(a: Vector2D, b: Vector2D): Vector2D =
    Vector2D(a.x + b.x, a.y + b.y)
  override def inverse(a: Vector2D): Vector2D =
    Vector2D(-a.x, -a.y)
}

object Vector2DGroupExample {
  def main(args: Array[String]): Unit = {
    import spire.syntax.group.* // Enables |+| and |-| syntax

    val v1 = Vector2D(1.0, 2.0)
    val v2 = Vector2D(3.0, 4.0)
    val v3 = Vector2D(-1.0, -2.0)

    // Use the group operations
    val sum = v1 |+| v2 // Vector addition
    val diff = v1 |-| v3 // Vector subtraction
    val identity = summon[Group[Vector2D]].empty // Zero vector
    val inverse = summon[Group[Vector2D]].inverse(v1) // Negation of v1

    println(s"Sum: $sum")
    println(s"Difference: $diff")
    println(s"Identity: $identity")
    println(s"Inverse of v1: $inverse")
  }
}
