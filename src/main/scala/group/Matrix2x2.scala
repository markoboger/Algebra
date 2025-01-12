import spire.algebra.Group

case class Matrix2x2(a: Double, b: Double, c: Double, d: Double) {
  def +(other: Matrix2x2): Matrix2x2 =
    Matrix2x2(a + other.a, b + other.b, c + other.c, d + other.d)

  def unary_- : Matrix2x2 =
    Matrix2x2(-a, -b, -c, -d)
}

given Matrix2x2Group: Group[Matrix2x2] with {
  override def empty: Matrix2x2 = Matrix2x2(0.0, 0.0, 0.0, 0.0)
  override def combine(a: Matrix2x2, b: Matrix2x2): Matrix2x2 = a + b
  override def inverse(a: Matrix2x2): Matrix2x2 = -a
}

object Matrix2x2GroupExample {
  def main(args: Array[String]): Unit = {
    import spire.syntax.group.*

    val m1 = Matrix2x2(1, 2, 3, 4)
    val m2 = Matrix2x2(5, 6, 7, 8)

    val sum = m1 |+| m2
    val diff = m1 |-| m2
    val identity = summon[Group[Matrix2x2]].empty
    val inverse = summon[Group[Matrix2x2]].inverse(m1)

    println(s"Sum: $sum")
    println(s"Difference: $diff")
    println(s"Identity: $identity")
    println(s"Inverse of m1: $inverse")
  }
}
