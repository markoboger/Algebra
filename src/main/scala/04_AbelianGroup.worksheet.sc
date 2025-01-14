// Now lets look at a more complex structure than Monoid, the Abelian Group.
// An Abelian Group is a Group where the operation is commutative.
// That means that the order of the operands does not matter.
// This is true for the addition of numbers, for example.
// But we will use a more complex example, the addition of Complex numbers.

import spire.algebra.AbGroup

case class Complex(real: Double, imaginary: Double) {
  def +(other: Complex): Complex = Complex(real + other.real, imaginary + other.imaginary)
  def -(other: Complex): Complex = ComplexAbelianGroup.combine(this, ComplexAbelianGroup.inverse(other))
  def unary_- : Complex = Complex(-real, -imaginary)
  override def toString: String = s"${real} + ${imaginary}i"
}

// Define the Abelian Group for Complex numbers
given ComplexAbelianGroup: AbGroup[Complex] with {
  override def empty: Complex = Complex(0.0, 0.0)
  override def combine(a: Complex, b: Complex): Complex = a + b
  override def inverse(a: Complex): Complex = -a
}

import spire.syntax.group.* // Enables |+| and |-| syntax

val c1 = Complex(3.0, 4.0)
val c2 = Complex(1.0, -2.0)

// Use the Abelian Group operations. We get the |+| operator for free.
val sum = c1 |+| c2
//but we can also use the + operator, sine we defined it in the Complex class.
val sum2 = c1 + c2
val diff = c1 |-| c2 // Complex subtraction
val diff2 = c1 - c2
