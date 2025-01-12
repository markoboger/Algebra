import spire.algebra.Group

case class Rotation(angle: Double) {
  def normalized: Rotation = {
    val normalizedAngle = ((angle % 360) + 360) % 360
    Rotation(normalizedAngle)
  }
}

given RotationGroup: Group[Rotation] with {
  override def empty: Rotation = Rotation(0.0)
  override def combine(a: Rotation, b: Rotation): Rotation = Rotation(
    a.angle + b.angle
  ).normalized
  override def inverse(a: Rotation): Rotation = Rotation(-a.angle).normalized
}

object RotationGroupExample {
  def main(args: Array[String]): Unit = {
    import spire.syntax.group.* // Enables |+| and |-| syntax

    val r1 = Rotation(45.0)
    val r2 = Rotation(100.0)
    val r3 = Rotation(-50.0)

    // Use the group operations
    val sum = r1 |+| r2 // Add angles
    val diff = r1 |-| r3 // Subtract angles
    val identity = summon[Group[Rotation]].empty // Zero rotation
    val inverse = summon[Group[Rotation]].inverse(r1) // Inverse of r1

    println(s"Sum: ${sum.angle} degrees")
    println(s"Difference: ${diff.angle} degrees")
    println(s"Identity: ${identity.angle} degrees")
    println(s"Inverse of r1: ${inverse.angle} degrees")
  }
}
