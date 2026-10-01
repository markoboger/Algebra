package monad

import munit.FunSuite

/** Tests verifying that MonadicPack obeys the monad laws.
 *
 * The three monad laws ensure predictable composition:
 * 1. Left identity: pure(a).flatMap(f) ≡ f(a)
 * 2. Right identity: m.flatMap(pure) ≡ m
 * 3. Associativity: m.flatMap(f).flatMap(g) ≡ m.flatMap(x => f(x).flatMap(g))
 *
 * These laws guarantee that monadic code can be refactored and composed
 * without changing behavior, making for-comprehensions reliable.
 */
class MonadLawsTest extends FunSuite:
  
  import Model.*
  import MonadLaws.*
  
  test("MonadicPack obeys left identity law"):
    val bottle = fullBottle
    val f = (b: Bottle) => MonadicPack(List(Some(b), Some(b)))
    
    assert(leftIdentity(bottle, f))
  
  test("MonadicPack obeys right identity law"):
    val pack = MonadicPack(List(Some(fullBottle), None, Some(emptyBottle)))
    
    assert(rightIdentity(pack))
  
  test("MonadicPack obeys associativity law"):
    val pack = MonadicPack(List(Some(fullBottle), Some(emptyBottle)))
    val f = (b: Bottle) => MonadicPack(List(Some(b), Some(b)))
    val g = (b: Bottle) => if b.empty then MonadicPack(List(None)) else MonadicPack(List(Some(b)))
    
    assert(associativity(pack, f, g))
  
  test("MonadicPack map preserves structure"):
    val pack = MonadicPack(List(Some(fullBottle), None, Some(emptyBottle)))
    val mapped = pack.map(b => { b.consume(); b })
    
    assertEquals(mapped.bottles.length, 3)
    assertEquals(mapped.bottles(1), None)
  
  test("MonadicPack flatMap chains correctly"):
    val pack = MonadicPack(List(Some(fullBottle), Some(emptyBottle)))
    
    // FlatMap that duplicates each bottle
    val result = pack.flatMap(b => MonadicPack(List(Some(b), Some(b))))
    
    assertEquals(result.present.length, 4)
  
  test("MonadicPack withFilter removes elements"):
    val pack = MonadicPack(List(Some(fullBottle), Some(emptyBottle), None, Some(fullBottle)))
    val filtered = pack.withFilter(b => !b.empty)
    
    assertEquals(filtered.present.length, 2)
    assert(filtered.present.forall(!_.empty))
  
  test("MonadicCrate flatMap enables nested iteration"):
    val mPack1 = MonadicPack(List(Some(fullBottle), Some(fullBottle)))
    val mPack2 = MonadicPack(List(Some(emptyBottle)))
    val crate = MonadicCrate(List(Some(mPack1), None, Some(mPack2)))
    
    assertEquals(crate.allBottles.length, 3)
    assertEquals(crate.presentPacks.length, 2)
  
  test("for-comprehension over MonadicCrate works correctly"):
    val mPack1 = MonadicPack(List(Some(fullBottle), None, Some(fullBottle)))
    val mPack2 = MonadicPack(List(Some(emptyBottle), Some(fullBottle)))
    val crate = MonadicCrate(List(Some(mPack1), Some(mPack2)))
    
    val fullBottles = for
      pack <- crate.presentPacks
      bottle <- pack.present
      if !bottle.empty
    yield bottle
    
    assertEquals(fullBottles.length, 3)
  
  test("functor law: map(id) == id"):
    val pack = MonadicPack(List(Some(fullBottle), None, Some(emptyBottle)))
    val identity: Bottle => Bottle = b => b
    
    assertEquals(pack.map(identity).present, pack.present)
  
  test("functor law: map(f . g) == map(f) . map(g)"):
    val pack = MonadicPack(List(Some(fullBottle), Some(fullBottle)))
    val f = (b: Bottle) => { b.consume(); b }
    val g = (b: Bottle) => b
    
    val composed = pack.map(b => f(g(b)))
    val chained = pack.map(g).map(f)
    
    // Both should have same structure (all empty after consuming)
    assertEquals(composed.present.map(_.empty), chained.present.map(_.empty))
