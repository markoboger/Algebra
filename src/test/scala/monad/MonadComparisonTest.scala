package monad

import munit.FunSuite

/** Tests for the three implementations, verifying they produce identical results.
 *
 * These tests ensure that all three approaches (Java-style, Exception-style,
 * and Monad-style) handle the same test cases consistently and produce the
 * same outputs.
 */
class MonadComparisonTest extends FunSuite:
  
  import Model.*
  
  /** Create test data for imperative/exception styles. */
  def createImperativeCrate(): Crate =
    val pack1 = Pack(List(fullBottle, fullBottle, fullBottle, fullBottle))
    val pack2 = Pack(List(fullBottle, fullBottle, fullBottle))
    Crate(List(pack1, pack2))
  
  /** Create equivalent monadic crate. */
  def createMonadicCrate(): MonadicCrate[Bottle] =
    val mPack1 = MonadicPack(List(Some(fullBottle), Some(fullBottle), Some(fullBottle), Some(fullBottle)))
    val mPack2 = MonadicPack(List(Some(fullBottle), Some(fullBottle), Some(fullBottle)))
    MonadicCrate(List(Some(mPack1), Some(mPack2)))
  
  test("all three styles count the same full bottles in perfect crate"):
    val imperativeCrate = createImperativeCrate()
    val monadicCrate = createMonadicCrate()
    
    val javaCount = JavaStyle.countFullBottles(imperativeCrate)
    val exceptionCount = ExceptionStyle.countFullBottles(imperativeCrate)
    val monadCount = MonadStyle.countFullBottles(monadicCrate)
    
    assertEquals(javaCount, 7)
    assertEquals(exceptionCount, 7)
    assertEquals(monadCount, 7)
  
  test("all three styles handle missing bottles consistently"):
    // Crate with some null bottles
    val pack1 = Pack(List(fullBottle, null, fullBottle, null))
    val pack2 = Pack(List(fullBottle, fullBottle, null))
    val imperativeCrate = Crate(List(pack1, pack2))
    
    // Equivalent monadic crate with None
    val mPack1 = MonadicPack(List(Some(fullBottle), None, Some(fullBottle), None))
    val mPack2 = MonadicPack(List(Some(fullBottle), Some(fullBottle), None))
    val monadicCrate = MonadicCrate(List(Some(mPack1), Some(mPack2)))
    
    val javaCount = JavaStyle.countFullBottles(imperativeCrate)
    val exceptionCount = ExceptionStyle.countFullBottles(imperativeCrate)
    val monadCount = MonadStyle.countFullBottles(monadicCrate)
    
    assertEquals(javaCount, 4)
    assertEquals(exceptionCount, 4)
    assertEquals(monadCount, 4)
  
  test("all three styles handle completely null/empty crate"):
    val javaCount = JavaStyle.countFullBottles(null)
    val exceptionCount = ExceptionStyle.countFullBottles(null)
    val monadCount = MonadStyle.countFullBottles(MonadicCrate(List()))
    
    assertEquals(javaCount, 0)
    assertEquals(exceptionCount, 0)
    assertEquals(monadCount, 0)
  
  test("all three styles handle mix of empty and full bottles"):
    val pack1 = Pack(List(fullBottle, emptyBottle, fullBottle))
    val pack2 = Pack(List(emptyBottle, fullBottle, emptyBottle))
    val imperativeCrate = Crate(List(pack1, pack2))
    
    val mPack1 = MonadicPack(List(Some(fullBottle), Some(emptyBottle), Some(fullBottle)))
    val mPack2 = MonadicPack(List(Some(emptyBottle), Some(fullBottle), Some(emptyBottle)))
    val monadicCrate = MonadicCrate(List(Some(mPack1), Some(mPack2)))
    
    val javaCount = JavaStyle.countFullBottles(imperativeCrate)
    val exceptionCount = ExceptionStyle.countFullBottles(imperativeCrate)
    val monadCount = MonadStyle.countFullBottles(monadicCrate)
    
    assertEquals(javaCount, 3)
    assertEquals(exceptionCount, 3)
    assertEquals(monadCount, 3)
  
  test("all three styles handle missing packs"):
    // MonadicCrate with missing pack
    val mPack1 = MonadicPack(List(Some(fullBottle), Some(fullBottle)))
    val monadicCrate = MonadicCrate(List(Some(mPack1), None, Some(mPack1)))
    
    val monadCount = MonadStyle.countFullBottles(monadicCrate)
    
    // For imperative styles, we can't easily represent "missing pack" 
    // without making Pack nullable, so we just verify monadic behavior
    assertEquals(monadCount, 4)  // 2 + 0 + 2 = 4
