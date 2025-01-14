// Now lets take a look at a simpler structure than Group, the Monoid.
// A Monoid is a Group without the inverse operation.
// A Monoid is a set with a binary operation that is associative and has an identity element.
// String with the concatenation operation is a typical application of Monoid, for example.

// For the case of String, we ofcouse already have an implementation in the standard library.
// So creating a String class using inheritance from Monoid is not possible.
// But we can create a Monoid instance for String using the given keyword.

// Let's create a Monoid instance for String and use it to concatenate strings.
// The advantage is that we now guarantee or document that our operation is associative.
// Associa
import spire.algebra.Monoid

given StringMonoid: Monoid[String] with {
  override def empty: String = ""
  override def combine(a: String, b: String): String = a + b
}

import spire.syntax.monoid.* // Enables |+| syntax

val s1 = "Hello, "
val s2 = "World! "
val s3 = "How are you? "

// Use the Monoid operation |+| just to show we are not using the standard concatenation +
s1 |+| s2 |+| s3
