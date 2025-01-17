// Scala has some Number types built in. Lets begin with Integers
// Integers are whole numbers, positive or negative, without decimals.
// Integers are of type Int
// Here are some examples of Integers

val i = 17
val j = 4
i + j
i - j
i * j
i / j
i % j

// But Integers are not quite like they are defined in Mathematics. Numbers can not be infinitely large.
// The upper limit of Integers is 2^31 - 1, because Integers are stored in 32 bits.
// To get the maximum value of Integers, you can use Int.MaxValue

Int.MaxValue
val max = Int.MaxValue
max + 1

// There is also a minimum value of Integers, which is -2^31
// To get the minimum value of Integers, you can use Int.MinValue

val min = Int.MinValue
min - 1

// There is also a type called Long, which is an Integer of 64 bits.
// To create a Long, you need to add an L at the end of the number

val l = 1234567890123456789L

// We can also declare the type of a variable explicitly

val k: Long = 42

// Longs have a maximum value of 2^63 - 1

val maxlong = Long.MaxValue
val minlong = Long.MinValue

// There is also a type called Short, which is an Integer of 16 bits.
// Shorts have a maximum value of 2^15 - 1

val s = Short.MaxValue
val t = Short.MinValue

val u: Short = 42

// There is also a type called Byte, which is an Integer of 8 bits.

val byte: Byte = 42

// Finally, there is a type called BigInt, which is an Integer of arbitrary precision.
// BigInts can be as large as you need them to be.
// BigInts are not built into Scala, so you need to import them

import scala.math.BigInt

val b = BigInt("123456789012345678901234567890")
b * b

// Why don't we use BigInt all the time? Because they are slower and take up more memory than Integers.
// So we only use them when we need to.

// There are also Number types for decimals.
// The most common type for decimals is Float, which uses 32 bit to store a decimal number.
// Floats use a some of the bits to store the exponent of the number, so they can represent very large and very small numbers.
// Floats have a maximum value of 3.4028235 x 10^38 and a minimum value of 1.4 x 10^-45

val f = 3.14f
val mol: Float = 6.02214179e23
val avogadro = 6.02214179e23f

// There is also a type called Double, which uses 64 bits to store a decimal number.
// Doubles are actually the default type for decimal numbers in Scala.
// Doubles have a maximum value of 1.7976931348623157 x 10^308 and a minimum value of 4.9 x 10^-324

val d = 3.14

// There is also a type called BigDecimal, which is a decimal number of arbitrary precision.
// BigDecimals are not built into Scala, so you need to import them

import scala.math.BigDecimal

val bd = BigDecimal("3.14159265358979323846264338327950288419716939937510")
bd * bd

// Just like with BigInts, we only use BigDecimals when we need to, because they are slower and take up more memory than Doubles.

//But decimal numbers are not always exact. For example, 1/3 can not be represented exactly as a decimal number.

val third = 0.33333333
third * 3

// To do precise calculations with fractions we can use Rational numbers.
// Rationals are not built into Scala, but there is a library that implements them called Spire.
// Spire is not a library that is included in the Scala standard library, we need to load it as a dependency.
// To do so, we need to specify the path to the code of the library in our project description file called build.sbt
// We also need to specify the version of the library we want to use.
// So the complete dependency for Spire would look like this: "org.typelevel" %% "spire" % "0.18.0"
// After we have done that, we can import the library and use it.

import spire.math.Rational

val r = Rational(1, 3)
r * 3

// We can also use the infix operator / to create a Rational

val r2 = 1 / 3

val r3: Rational = 1 / 3

// Rational numbers allow us to do precise calculations with fractions.

val r4 = Rational(4, 7)
val r5 = Rational(5, 9)
r4 + r5
r4 - r5
r4 * r5
r4 / r5

// Rational numbers are not as fast as Doubles, but they are more precise.

// There is a type called Real, which is a decimal number of arbitrary precision.

import spire.math.Real
import spire.math.atan

val real = Real(3.14)

// Real numbers support trigonometric functions like sin, cos, tan, asin, acos, atan, sinh, cosh, tanh, asinh, acosh, atanh
// They also support exponential functions like exp and log
// And they support power functions like pow
// The precision of Real numbers is limited by the memory of the computer.

val PI =
  Real(16) * atan(Real(Rational(1, 5))) - Real(4) * atan(Real(Rational(1, 239)))

// Spire also has a type called Complex, which is a complex number with a Real part and an Imaginary part.
// Complex number can be formed from whole numbers - or Integers, but also from Rational or Real numbers.
import spire.math.Complex

val c = Complex(3, 4)

val c1 = Complex(Rational(2, 3), Rational(5, 7))
val c2 = Complex(3.4, 1.9)

// This is called a generic type, because it can be used with any type of number.
// In fact Complex numbers can be used with any type of number that is a subtype of Number.
// This is called polymorphism, because the same code can be used with different types of numbers.

// Subtypes of Number are Int, Long, Short, Byte, BigInt, Float, Double, BigDecimal, Rational, Real, and Complex
// so we can use Complex numbers with all of these types.

// For all of these types we have operators like +, -, *, /, %, and ^, which work the same way for all types of numbers.
// This is called operator overloading, because the same operator can be used with different types of numbers.

3 + 4 - 5 * 6 / 7

3.0 + 4.0 - 5.0 * 6.0 / 7.0

Rational(3, 1) + Rational(4, 1) - Rational(5, 1) * Rational(6, 1) / Rational(
  7,
  1
)

Real(3) + Real(4) - Real(5) * Real(6) / Real(7)

// We also have comparison operators like ==, !=, <, >, <=, and >=, which work the same way for all types of numbers.

3 == 3
3 != 4
3 < 4
3 > 4
3 <= 4

3.0 == 3.0
3.0 != 4.0
3.0 < 4.0

Rational(3, 1) == Rational(3, 1)
Rational(3, 1) != Rational(4, 1)
Rational(3, 1) < Rational(4, 1)

Real(3) == Real(3)
Real(3) != Real(4)

// We also have logical operators like &&, ||, and !, which work the same way for all types of numbers.

(3 == 3) && (4 != 5)
(3 < 4) || (5 > 6)

// From Algebra we know that the operations on numbers have certain properties.
// For example, addition is commutative, which means that a + b = b + a for all numbers a and b.
// And addition is associative, which means that (a + b) + c = a + (b + c) for all numbers a, b, and c.

// If these properties are satisfied, we say that the numbers form a group under addition.
// Spire has a type called Group, which represents a group under addition.
