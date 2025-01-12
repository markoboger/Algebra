import spire.algebra.Monoid

// Define a Monoid for String concatenation
given StringMonoid: Monoid[String] with {
  override def empty: String = ""
  override def combine(a: String, b: String): String = a + b
}

object StringMonoidExample {
  def main(args: Array[String]): Unit = {
    import spire.syntax.monoid.* // Enables |+| syntax

    val s1 = "Hello, "
    val s2 = "World!"
    val s3 = " How are you?"

    // Use the Monoid operations
    val combined = s1 |+| s2 |+| s3 // Concatenates strings
    val identity = summon[Monoid[String]].empty // Empty string

    println(s"Combined: '$combined'")
    println(s"Identity: '$identity'")
  }
}
