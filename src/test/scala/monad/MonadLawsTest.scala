package monad

import munit.FunSuite

/** Tests verifying monad laws for Pack and Crate with explicit test cases. */
class MonadLawsTest extends FunSuite:
  
  import MonadStyle.*
  
  val f = (x: Int) => Pack(List(x, x * 2))
  val g = (x: Int) => Pack(List(x * 3))
  
  // Pack monad laws
  
  test("Pack left identity"):
    val testValues = List(1, 5, 42)
    testValues.foreach { n =>
      assertEquals(Pack.pure(n).flatMap(f), f(n))
    }
  
  test("Pack right identity"):
    val packs = List(Pack(List(1, 2)), Pack(List(42)))
    packs.foreach { pack =>
      assertEquals(pack.flatMap(Pack.pure), pack)
    }
  
  test("Pack associativity"):
    val packs = List(Pack(List(1, 2)), Pack(List(5)))
    packs.foreach { pack =>
      assertEquals(
        pack.flatMap(f).flatMap(g),
        pack.flatMap(x => f(x).flatMap(g))
      )
    }
  
  // Crate monad laws
  
  test("Crate left identity"):
    val testValues = List(1, 5, 42)
    testValues.foreach { n =>
      assertEquals(Crate.pure(n).flatMap(f), Crate(List(f(n))))
    }
  
  test("Crate right identity"):
    val crates = List(
      Crate(List(Pack(List(1, 2)))),
      Crate(List(Pack(List(5)), Pack(List(10))))
    )
    crates.foreach { crate =>
      assertEquals(crate.flatMap(Pack.pure), crate)
    }
  
  test("Crate associativity"):
    val crates = List(Crate(List(Pack(List(1, 2)))))
    crates.foreach { crate =>
      assertEquals(
        crate.flatMap(f).flatMap(g),
        crate.flatMap(x => f(x).flatMap(g))
      )
    }
  
  // Functor laws
  
  test("Pack functor identity"):
    val packs = List(Pack(List(1, 2, 3)))
    packs.foreach { pack =>
      assertEquals(pack.map(identity), pack)
    }
  
  test("Pack functor composition"):
    val mapF = (x: Int) => x * 2
    val mapG = (x: Int) => x + 1
    val packs = List(Pack(List(1, 2, 3)))
    packs.foreach { pack =>
      assertEquals(pack.map(x => mapF(mapG(x))), pack.map(mapG).map(mapF))
    }
  
  test("Crate functor identity"):
    val crates = List(Crate(List(Pack(List(1, 2)))))
    crates.foreach { crate =>
      assertEquals(crate.map(identity), crate)
    }
  
  test("Crate functor composition"):
    val mapF = (x: Int) => x * 2
    val mapG = (x: Int) => x + 1
    val crates = List(Crate(List(Pack(List(1, 2)))))
    crates.foreach { crate =>
      assertEquals(crate.map(x => mapF(mapG(x))), crate.map(mapG).map(mapF))
    }
  
  // Test that for-comprehension calls our domain methods
  
  test("for-comprehension calls Crate.foreach"):
    var called = false
    
    val crate = new Crate(List(Pack(List(1, 2)))):
      override def foreach[U](f: Int => U): Unit =
        called = true
        super.foreach(f)
    
    for x <- crate do ()
    
    assert(called, "Crate.foreach was not called")
  
  test("for-comprehension with Option correctly iterates"):
    val crate = Crate(List(Pack(List(Some(Bottle(true)), None, Some(Bottle(false))))))
    
    var count = 0
    for
      bottleOpt <- crate
      bottle <- bottleOpt
    do
      count += 1
    
    assertEquals(count, 2) // Two Some values
