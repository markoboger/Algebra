// We can define our own algebraic structures using the library Spire.
// Spire provides implementations for algebraic structures, like Monoid or Group
// Let's build our own algebraic datatype. We will something similar to a Number, but slightly different, so that our own implemenation makes sense.
// For example a class for adding Angles or Rotations.
// Rotation should have the operation addition, but values above 360° or below 0° should be normalized to the according value between 0° and 360°

// We can do that by inheriting from the class Group and implementing the abstract functions

import spire.algebra.Group
import spire.syntax.group.*

class Rotation1(val angle: Double) extends Group[Rotation1] {
  // Normalize angle to [0, 360)
  private val normalizedAngle: Double = {
    val a = angle % 360
    if (a < 0) a + 360 else a
  }

  override def toString: String = s"Rotation($normalizedAngle degrees)"
  def id: Rotation1 = new Rotation1(0.0) // No rotation is the identity
  override def combine(x: Rotation1, y: Rotation1): Rotation1 = new Rotation1(x.normalizedAngle + y.normalizedAngle)
  override def inverse(a: Rotation1): Rotation1 =
    new Rotation1(-a.normalizedAngle) // Inverse rotation is the negative of the angle
  def empty: Rotation1 = Rotation1(0)
  def |+|(that: Rotation1) = combine(this, that)
}

val r1 = Rotation1(90)
val r2 = Rotation1(180)
val r3 = Rotation1(325)

r1.combine(r1, r2)
r1 |+| r2

// Scala has another mechanism called Type Classes, that - in practice - prove to be a more elegant solution.
// Here is an implementation using Type Classes and the concept of given, which we will discuss later.

import spire.algebra.Group
import spire.syntax.group.*

case class Rotation2(angle: Double) {
  def normalized: Rotation2 = {
    val normalizedAngle = ((angle % 360) + 360) % 360
    Rotation2(normalizedAngle)
  }
}

given RotationGroup: Group[Rotation2] with {
  override def empty: Rotation2 = Rotation2(0.0)
  override def combine(a: Rotation2, b: Rotation2): Rotation2 = Rotation2(
    a.angle + b.angle
  ).normalized
  override def inverse(a: Rotation2): Rotation2 = Rotation2(-a.angle).normalized
}

val r4 = Rotation2(190)
val r5 = Rotation2(180)

r4 |+| r5
