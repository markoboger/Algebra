package monad

import munit.ScalaCheckSuite
import org.scalacheck.Prop.*
import org.scalacheck.{Arbitrary, Gen}

/** Property-based tests for monad laws with ScalaCheck. */
class MonadLawsTest extends ScalaCheckSuite:
  
  // Generators
  implicit val arbPack: Arbitrary[Pack[Int]] = Arbitrary(
    Gen.listOf(Gen.choose(1, 100)).map(Pack(_))
  )
  
  implicit val arbCrate: Arbitrary[Crate[Int]] = Arbitrary(
    Gen.listOf(arbPack.arbitrary).map(Crate(_))
  )
  
  implicit val arbMaybe: Arbitrary[Maybe[Int]] = Arbitrary(
    Gen.oneOf(
      Gen.const(Nothing),
      Gen.choose(1, 100).map(Just(_))
    )
  )
  
  // Pack[T] monad laws
  
  property("Pack left identity"):
    forAll { (n: Int) =>
      val f = (x: Int) => Pack(List(x, x * 2))
      Pack.pure(n).flatMap(f) == f(n)
    }
  
  property("Pack right identity"):
    forAll { (pack: Pack[Int]) =>
      pack.flatMap(Pack.pure) == pack
    }
  
  property("Pack associativity"):
    forAll { (pack: Pack[Int]) =>
      val f = (x: Int) => Pack(List(x, x + 1))
      val g = (x: Int) => Pack(List(x * 2))
      pack.flatMap(f).flatMap(g) == pack.flatMap(x => f(x).flatMap(g))
    }
  
  property("Pack functor identity"):
    forAll { (pack: Pack[Int]) =>
      pack.map(identity) == pack
    }
  
  property("Pack functor composition"):
    forAll { (pack: Pack[Int]) =>
      val f = (x: Int) => x * 2
      val g = (x: Int) => x + 1
      pack.map(x => f(g(x))) == pack.map(g).map(f)
    }
  
  // Crate[T] monad laws
  
  property("Crate left identity"):
    forAll { (n: Int) =>
      val f = (x: Int) => Pack(List(x, x * 2))
      Crate.pure(n).flatMap(f) == Crate(List(f(n)))
    }
  
  property("Crate right identity"):
    forAll { (crate: Crate[Int]) =>
      crate.flatMap(Pack.pure) == crate
    }
  
  property("Crate associativity"):
    forAll { (crate: Crate[Int]) =>
      val f = (x: Int) => Pack(List(x, x + 1))
      val g = (x: Int) => Pack(List(x * 2))
      crate.flatMap(f).flatMap(g) == crate.flatMap(x => f(x).flatMap(g))
    }
  
  property("Crate functor identity"):
    forAll { (crate: Crate[Int]) =>
      crate.map(identity) == crate
    }
  
  property("Crate functor composition"):
    forAll { (crate: Crate[Int]) =>
      val f = (x: Int) => x * 2
      val g = (x: Int) => x + 1
      crate.map(x => f(g(x))) == crate.map(g).map(f)
    }
  
  // Maybe[T] monad laws
  
  property("Maybe left identity"):
    forAll { (n: Int) =>
      val f = (x: Int) => if x % 2 == 0 then Just(x) else Nothing
      Maybe.pure(n).flatMap(f) == f(n)
    }
  
  property("Maybe right identity"):
    forAll { (m: Maybe[Int]) =>
      m.flatMap(Maybe.pure) == m
    }
  
  property("Maybe associativity"):
    forAll { (m: Maybe[Int]) =>
      val f = (x: Int) => if x > 50 then Just(x) else Nothing
      val g = (x: Int) => Just(x * 2)
      m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
    }
  
  property("Maybe functor identity"):
    forAll { (m: Maybe[Int]) =>
      m.map(identity) == m
    }
  
  property("Maybe functor composition"):
    forAll { (m: Maybe[Int]) =>
      val f = (x: Int) => x * 2
      val g = (x: Int) => x + 1
      m.map(x => f(g(x))) == m.map(g).map(f)
    }
