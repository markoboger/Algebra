package ring

import spire.algebra._
import spire.implicits._

case class Complex(real: Double, imag: Double)

object Complex {
  // Define a Ring instance for Complex[Double]
  given Ring[Complex] with {
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

    override def fromInt(n: Int): Complex =
      Complex(n.toDouble, 0.0)
  }
}

object ComplexRingExample {
  def main(args: Array[String]): Unit = {
    import Complex.given

    val c1 = Complex(3.0, 4.0) // 3 + 4i
    val c2 = Complex(1.0, -2.0) // 1 - 2i

    // Basic operations
    val sum = c1 + c2
    val difference = c1 - c2
    val product = c1 * c2

    println(s"Sum: $sum") // Output: Sum: Complex(4.0, 2.0)
    println(s"Difference: $difference") // Output: Difference: Complex(2.0, 6.0)
    println(s"Product: $product") // Output: Product: Complex(11.0, -2.0)
  }
}
