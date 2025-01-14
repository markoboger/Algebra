// Monoid, Group and AbelianGroup are algebraic structures that only use one operation, and its inverse in the case of Group and AbelianGroup.
// We will now look at algebraic structures that use two operations.
// We will take complex numbers as an example, and add the multiplication operation.

// The Complex numbers with +, - and * form a Ring. Adding / would make it a Field.

import spire.algebra._
import spire.implicits._

case class Complex1(real: Double, imag: Double)

object Complex1 {
  // Define a Ring instance for Complex[Double]
  given Ring[Complex1] with {
    override def zero: Complex1 = Complex1(0.0, 0.0)
    override def one: Complex1 = Complex1(1.0, 0.0)
    override def plus(x: Complex1, y: Complex1): Complex1 =
      Complex1(x.real + y.real, x.imag + y.imag)

    override def negate(x: Complex1): Complex1 =
      Complex1(-x.real, -x.imag)

    override def times(x: Complex1, y: Complex1): Complex1 =
      Complex1(
        x.real * y.real - x.imag * y.imag,
        x.real * y.imag + x.imag * y.real
      )
    override def fromInt(n: Int): Complex1 =
      Complex1(n.toDouble, 0.0)
  }
}

import Complex1.given

val c1 = Complex1(3.0, 4.0) // 3 + 4i
val c2 = Complex1(1.0, -2.0) // 1 - 2i

// Basic operations
c1 + c2
c1 - c2
c1 * c2

// Now we will add / and make it a Field.

case class Complex(real: Double, imag: Double)

object Complex {
  // Define a Field instance for Complex[Double]
  given Field[Complex] with {
    override def zero: Complex = Complex(0.0, 0.0)
    override def one: Complex = Complex(1.0, 0.0)
    override def plus(x: Complex, y: Complex): Complex =
      Complex(x.real + y.real, x.imag + y.imag)
    override def negate(x: Complex): Complex =
      Complex(-x.real, -x.imag)
    override def times(x: Complex, y: Complex): Complex =
      Complex(
        x.real * y.real - x.imag * y.imag,
        x.real * y.imag + x.imag * y.real
      )
    override def div(x: Complex, y: Complex): Complex = {
      val denominator = y.real * y.real + y.imag * y.imag
      Complex(
        (x.real * y.real + x.imag * y.imag) / denominator,
        (x.imag * y.real - x.real * y.imag) / denominator
      )
    }
    override def fromInt(n: Int): Complex =
      Complex(n.toDouble, 0.0)
  }
}

val c3 = Complex(3.0, 4.0) // 3 + 4i
val c4 = Complex(1.0, -2.0) // 1 - 2i
// Basic operations
c3 + c4
c3 - c4
c3 * c4
c3 / c4
