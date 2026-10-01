package monad

import munit.ScalaCheckSuite
import org.scalacheck.Prop.*
import org.scalacheck.{Arbitrary, Gen, Cogen}

/** Property-based tests for monad laws using ScalaCheck with generated functions. */
class MonadLawsTest extends ScalaCheckSuite:
  
  // Generators for Pack and Crate
  implicit val arbPack: Arbitrary[Pack[Int]] = Arbitrary(
    Gen.listOf(Gen.choose(1, 100)).map(Pack(_))
  )
  
  implicit val arbCrate: Arbitrary[Crate[Int]] = Arbitrary(
    Gen.listOf(Gen.choose(1, 100)).map(Crate(_))
  )
  
  // Cogen instances for generating functions
  implicit val cogenPack: Cogen[Pack[Int]] = Cogen[List[Int]].contramap(_.items)
  implicit val cogenCrate: Cogen[Crate[Int]] = Cogen[List[Int]].contramap(_.items)
  
  // Generator for functions Int => Pack[Int]
  implicit val arbIntToPack: Arbitrary[Int => Pack[Int]] = Arbitrary(
    Gen.function1[Int, Pack[Int]](arbPack.arbitrary)(Cogen[Int])
  )
  
  // Generator for functions Int => Crate[Int]
  implicit val arbIntToCrate: Arbitrary[Int => Crate[Int]] = Arbitrary(
    Gen.function1[Int, Crate[Int]](arbCrate.arbitrary)(Cogen[Int])
  )
  
  // ===== Pack[T] Monad Laws =====
  
  property("Pack: left identity"):
    forAll { (a: Int, f: Int => Pack[Int]) =>
      Pack.pure(a).flatMap(f) == f(a)
    }
  
  property("Pack: right identity"):
    forAll { (m: Pack[Int]) =>
      m.flatMap(Pack.pure) == m
    }
  
  property("Pack: associativity"):
    forAll { (m: Pack[Int], f: Int => Pack[Int], g: Int => Pack[Int]) =>
      m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
    }
  
  // ===== Crate[T] Monad Laws =====
  
  property("Crate: left identity"):
    forAll { (a: Int, f: Int => Crate[Int]) =>
      Crate.pure(a).flatMap(f) == f(a)
    }
  
  property("Crate: right identity"):
    forAll { (m: Crate[Int]) =>
      m.flatMap(Crate.pure) == m
    }
  
  property("Crate: associativity"):
    forAll { (m: Crate[Int], f: Int => Crate[Int], g: Int => Crate[Int]) =>
      m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
    }
