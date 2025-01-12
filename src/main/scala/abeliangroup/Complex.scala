package abeliangroup

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

object ComplexAbelianGroupExample {
  def main(args: Array[String]): Unit = {
    import spire.syntax.group.* // Enables |+| and |-| syntax

    val c1 = Complex(3.0, 4.0)
    val c2 = Complex(1.0, -2.0)

    // Use the Abelian Group operations
    val sum = c1 |+| c2 // Complex addition
    val sum2 = c1 + c2
    val diff = c1 |-| c2 // Complex subtraction
    val diff2 = c1 - c2
    val identity = summon[AbGroup[Complex]].empty // 0 + 0i
    val inverse = summon[AbGroup[Complex]].inverse(c1) // -c1

    println(s"Sum: $sum")
    println(s"Difference: $diff")
    println(s"Identity: $identity")
    println(s"Inverse of c1: $inverse")
  }
}
