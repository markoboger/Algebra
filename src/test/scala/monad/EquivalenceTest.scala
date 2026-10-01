package monad

import munit.FunSuite

/** Tests proving all three styles produce identical results for totalVolume. */
class EquivalenceTest extends FunSuite:
  
  val beer = Drink("Beer", 500)
  val wine = Drink("Wine", 750)
  val water = Drink("Water", 330)
  
  test("all three compute same total volume - happy path"):
    // Java
    val javaPack1 = JavaPack(List(Bottle(Some(beer)), Bottle(Some(wine))))
    val javaPack2 = JavaPack(List(Bottle(None), Bottle(Some(water))))
    val javaCrate = JavaCrate(List(javaPack1, javaPack2))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception
    val exPack1 = ExceptionPack(List(Bottle(Some(beer)), Bottle(Some(wine))))
    val exPack2 = ExceptionPack(List(Bottle(None), Bottle(Some(water))))
    val exCrate = ExceptionCrate(List(exPack1, exPack2))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad
    val monadPack1 = Pack(List(Bottle(Some(beer)), Bottle(Some(wine))))
    val monadPack2 = Pack(List(Bottle(None), Bottle(Some(water))))
    val monadCrate = Crate(List(monadPack1, monadPack2))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    val expected = 500 + 750 + 330 // beer + wine + water
    assertEquals(javaTotal, expected)
    assertEquals(exTotal, expected)
    assertEquals(monadTotal, expected)
  
  test("all three handle empty bottles"):
    // Java
    val javaPack = JavaPack(List(Bottle(None), Bottle(None)))
    val javaCrate = JavaCrate(List(javaPack))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception
    val exPack = ExceptionPack(List(Bottle(None), Bottle(None)))
    val exCrate = ExceptionCrate(List(exPack))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad
    val monadPack = Pack(List(Bottle(None), Bottle(None)))
    val monadCrate = Crate(List(monadPack))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    assertEquals(javaTotal, 0)
    assertEquals(exTotal, 0)
    assertEquals(monadTotal, 0)
  
  test("all three handle null/missing packs"):
    // Java
    val javaCrate = JavaCrate(List(null, null))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception
    val exCrate = ExceptionCrate(List())
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad
    val monadCrate = Crate(List.empty[Pack[Bottle]])
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    assertEquals(javaTotal, 0)
    assertEquals(exTotal, 0)
    assertEquals(monadTotal, 0)
  
  test("all three handle empty crate"):
    // Java
    val javaCrate = JavaCrate(List())
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception
    val exCrate = ExceptionCrate(List())
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad
    val monadCrate = Crate(List.empty[Pack[Bottle]])
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    assertEquals(javaTotal, 0)
    assertEquals(exTotal, 0)
    assertEquals(monadTotal, 0)
  
  test("all three handle mix of null bottles in Java style"):
    // Java
    val javaPack = JavaPack(List(Bottle(Some(beer)), null, Bottle(Some(wine))))
    val javaCrate = JavaCrate(List(javaPack))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception (no null bottles)
    val exPack = ExceptionPack(List(Bottle(Some(beer)), Bottle(Some(wine))))
    val exCrate = ExceptionCrate(List(exPack))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad (no null bottles)
    val monadPack = Pack(List(Bottle(Some(beer)), Bottle(Some(wine))))
    val monadCrate = Crate(List(monadPack))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    val expected = 500 + 750 // beer + wine
    assertEquals(javaTotal, expected)
    assertEquals(exTotal, expected)
    assertEquals(monadTotal, expected)
