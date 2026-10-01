package monad

import munit.FunSuite

/** Tests proving all three styles produce identical results for totalVolume. */
class EquivalenceTest extends FunSuite:
  
  val beer = Drink("Beer", 500)
  val wine = Drink("Wine", 750)
  val water = Drink("Water", 330)
  
  test("all three compute same total volume - happy path"):
    // Java
    val javaPack1 = JavaPack(List(Bottle(beer), Bottle(wine)))
    val javaPack2 = JavaPack(List(Bottle(null), Bottle(water)))
    val javaCrate = JavaCrate(List(javaPack1, javaPack2))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception
    val exPack1 = ExceptionPack(List(Bottle(beer), Bottle(wine)))
    val exPack2 = ExceptionPack(List(Bottle(null), Bottle(water)))
    val exCrate = ExceptionCrate(List(exPack1, exPack2))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad
    val monadPack1 = Pack(List(Bottle(beer), Bottle(wine)))
    val monadPack2 = Pack(List(Bottle(null), Bottle(water)))
    val monadCrate = Crate(List(monadPack1, monadPack2))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    val expected = 500 + 750 + 330 // beer + wine + water
    assertEquals(javaTotal, expected)
    assertEquals(exTotal, expected)
    assertEquals(monadTotal, expected)
  
  test("all three handle empty bottles"):
    // Java: null drinks
    val javaPack = JavaPack(List(Bottle(null), Bottle(null)))
    val javaCrate = JavaCrate(List(javaPack))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception: null drinks
    val exPack = ExceptionPack(List(Bottle(null), Bottle(null)))
    val exCrate = ExceptionCrate(List(exPack))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad: null drinks
    val monadPack = Pack(List(Bottle(null), Bottle(null)))
    val monadCrate = Crate(List(monadPack))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    assertEquals(javaTotal, 0)
    assertEquals(exTotal, 0)
    assertEquals(monadTotal, 0)
  
  test("all three handle missing packs"):
    // Java: null packs
    val javaCrate = JavaCrate(List(null, null))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception: empty pack list
    val exCrate = ExceptionCrate(List())
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad: empty pack list
    val monadCrate = Crate(List.empty[Pack[Bottle]])
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    assertEquals(javaTotal, 0)
    assertEquals(exTotal, 0)
    assertEquals(monadTotal, 0)
  
  test("all three handle empty crate"):
    // Java: empty crate
    val javaCrate = JavaCrate(List())
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception: empty crate
    val exCrate = ExceptionCrate(List())
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad: empty crate
    val monadCrate = Crate(List.empty[Pack[Bottle]])
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    assertEquals(javaTotal, 0)
    assertEquals(exTotal, 0)
    assertEquals(monadTotal, 0)
  
  test("all three handle null crate (Java only)"):
    // Java: null crate
    val javaTotal = JavaStyle.totalVolume(null)
    
    assertEquals(javaTotal, 0)
  
  test("all three handle mix of null bottles"):
    // Java: null bottles in list
    val javaPack = JavaPack(List(Bottle(beer), null, Bottle(wine)))
    val javaCrate = JavaCrate(List(javaPack))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception: only existing bottles (no null bottles in list)
    val exPack = ExceptionPack(List(Bottle(beer), Bottle(wine)))
    val exCrate = ExceptionCrate(List(exPack))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad: only existing bottles (no null bottles in list)
    val monadPack = Pack(List(Bottle(beer), Bottle(wine)))
    val monadCrate = Crate(List(monadPack))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    val expected = 500 + 750 // beer + wine
    assertEquals(javaTotal, expected)
    assertEquals(exTotal, expected)
    assertEquals(monadTotal, expected)
