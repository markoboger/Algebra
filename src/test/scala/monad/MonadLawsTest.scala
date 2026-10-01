package monad

import munit.FunSuite

/** Tests verifying monad laws for Pack and Crate.
 *
 * The three monad laws must hold:
 * 1. Left identity: pure(a).flatMap(f) ≡ f(a)
 * 2. Right identity: m.flatMap(pure) ≡ m  
 * 3. Associativity: m.flatMap(f).flatMap(g) ≡ m.flatMap(x => f(x).flatMap(g))
 *
 * We test with explicit values representing common cases.
 */
class MonadLawsTest extends FunSuite:
  
  import MonadStyle.*
  
  // Test helpers
  val f = (x: Int) => Pack(List(x, x * 2))
  val g = (x: Int) => Pack(List(x * 3))
  val h = (x: Int) => Pack(List(x + 1, x - 1))
  
  // Pack monad laws
  
  test("Pack left identity: pure(a).flatMap(f) = f(a)"):
    val testValues = List(0, 1, 42, -5, 100)
    
    for a <- testValues do
      val lhs = Pack.pure(a).flatMap(f)
      val rhs = f(a)
      assertEquals(lhs, rhs, s"Failed for value $a")
  
  test("Pack right identity: m.flatMap(pure) = m"):
    val testPacks = List(
      Pack(List(1, 2, 3)),
      Pack(List(42)),
      Pack(List()),
      Pack(List(0, -1, 5, 10))
    )
    
    for pack <- testPacks do
      assertEquals(pack.flatMap(Pack.pure), pack)
  
  test("Pack associativity: m.flatMap(f).flatMap(g) = m.flatMap(x => f(x).flatMap(g))"):
    val testPacks = List(
      Pack(List(1, 2)),
      Pack(List(5)),
      Pack(List(0, 10, 20))
    )
    
    for pack <- testPacks do
      val lhs = pack.flatMap(f).flatMap(g)
      val rhs = pack.flatMap(x => f(x).flatMap(g))
      assertEquals(lhs, rhs)
  
  // Crate monad laws
  
  test("Crate left identity: pure(a).flatMap(f) = Crate(List(f(a)))"):
    val testValues = List(0, 1, 42)
    
    for a <- testValues do
      val lhs = Crate.pure(a).flatMap(f)
      val rhs = Crate(List(f(a)))
      assertEquals(lhs, rhs, s"Failed for value $a")
  
  test("Crate right identity: m.flatMap(pure) = m"):
    val testCrates = List(
      Crate(List(Pack(List(1, 2)), Pack(List(3)))),
      Crate(List(Pack(List(42)))),
      Crate(List()),
      Crate(List(Pack(List()), Pack(List(1))))
    )
    
    for crate <- testCrates do
      assertEquals(crate.flatMap(Pack.pure), crate)
  
  test("Crate associativity: m.flatMap(f).flatMap(g) = m.flatMap(x => f(x).flatMap(g))"):
    val testCrates = List(
      Crate(List(Pack(List(1, 2)))),
      Crate(List(Pack(List(5)), Pack(List(10)))),
      Crate(List(Pack(List(0))))
    )
    
    for crate <- testCrates do
      val lhs = crate.flatMap(f).flatMap(g)
      val rhs = crate.flatMap(x => f(x).flatMap(g))
      assertEquals(lhs, rhs)
  
  // Functor laws (prerequisite for monad)
  
  test("Pack functor identity: map(id) = id"):
    val testPacks = List(
      Pack(List(1, 2, 3)),
      Pack(List()),
      Pack(List(42))
    )
    
    for pack <- testPacks do
      assertEquals(pack.map(identity), pack)
  
  test("Pack functor composition: map(f . g) = map(g).map(f)"):
    val testPacks = List(
      Pack(List(1, 2, 3)),
      Pack(List(5)),
      Pack(List(0, 10))
    )
    val funF = (x: Int) => x * 2
    val funG = (x: Int) => x + 1
    
    for pack <- testPacks do
      val lhs = pack.map(x => funF(funG(x)))
      val rhs = pack.map(funG).map(funF)
      assertEquals(lhs, rhs)
  
  test("Crate functor identity: map(id) = id"):
    val testCrates = List(
      Crate(List(Pack(List(1, 2)), Pack(List(3)))),
      Crate(List()),
      Crate(List(Pack(List(42))))
    )
    
    for crate <- testCrates do
      assertEquals(crate.map(identity), crate)
  
  test("Crate functor composition: map(f . g) = map(g).map(f)"):
    val testCrates = List(
      Crate(List(Pack(List(1, 2)))),
      Crate(List(Pack(List(5)), Pack(List(10))))
    )
    val funF = (x: Int) => x * 2
    val funG = (x: Int) => x + 1
    
    for crate <- testCrates do
      val lhs = crate.map(x => funF(funG(x)))
      val rhs = crate.map(funG).map(funF)
      assertEquals(lhs, rhs)
  
  // Additional monad tests with Option
  
  test("for-comprehension over Pack with Option"):
    val pack = Pack(List(Some(1): Option[Int], None, Some(3): Option[Int]))
    val result = for
      opt <- pack.bottles
      n <- opt
    yield n * 2
    
    assertEquals(result, List(2, 6))
  
  test("for-comprehension over Crate with Option"):
    val crate = Crate(List(
      Pack(List(Some(1): Option[Int], None)),
      Pack(List(Some(3): Option[Int]))
    ))
    
    val result = for
      pack <- crate.packs
      opt <- pack.bottles
      n <- opt
    yield n * 2
    
    assertEquals(result, List(2, 6))
