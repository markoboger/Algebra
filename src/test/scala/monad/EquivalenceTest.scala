package monad

import munit.FunSuite

/** Tests proving all three styles produce identical results for totalVolume. */
class EquivalenceTest extends FunSuite:
  
  val beer = Drink("Beer", 500)
  val wine = Drink("Wine", 750)
  val water = Drink("Water", 330)
  
  test("all three compute same total volume - happy path"):
    // Java: null for empty bottle
    val javaPack1 = JavaPack(List(JavaBottle(beer), JavaBottle(wine)))
    val javaPack2 = JavaPack(List(JavaBottle(null), JavaBottle(water)))
    val javaCrate = JavaCrate(List(javaPack1, javaPack2))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception: throws for empty bottle
    val exPack1 = ExceptionPack(List(ExceptionBottle(Some(beer)), ExceptionBottle(Some(wine))))
    val exPack2 = ExceptionPack(List(ExceptionBottle(None), ExceptionBottle(Some(water))))
    val exCrate = ExceptionCrate(List(exPack1, exPack2))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad: Option for empty bottle
    val monadPack1 = Pack(List(MonadBottle(Some(beer)), MonadBottle(Some(wine))))
    val monadPack2 = Pack(List(MonadBottle(None), MonadBottle(Some(water))))
    val monadCrate = Crate(List(monadPack1, monadPack2))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    val expected = 500 + 750 + 330 // beer + wine + water
    assertEquals(javaTotal, expected)
    assertEquals(exTotal, expected)
    assertEquals(monadTotal, expected)
  
  test("all three handle all empty bottles"):
    // Java: all null drinks
    val javaPack = JavaPack(List(JavaBottle(null), JavaBottle(null)))
    val javaCrate = JavaCrate(List(javaPack))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception: all throw
    val exPack = ExceptionPack(List(ExceptionBottle(None), ExceptionBottle(None)))
    val exCrate = ExceptionCrate(List(exPack))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad: all None
    val monadPack = Pack(List(MonadBottle(None), MonadBottle(None)))
    val monadCrate = Crate(List(monadPack))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    assertEquals(javaTotal, 0)
    assertEquals(exTotal, 0)
    assertEquals(monadTotal, 0)
  
  test("all three handle pack with no bottles"):
    // Java: empty pack
    val javaPack = JavaPack(List())
    val javaCrate = JavaCrate(List(javaPack))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception: empty pack
    val exPack = ExceptionPack(List())
    val exCrate = ExceptionCrate(List(exPack))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad: empty pack
    val monadPack = Pack(List.empty[MonadBottle])
    val monadCrate = Crate(List(monadPack))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    assertEquals(javaTotal, 0)
    assertEquals(exTotal, 0)
    assertEquals(monadTotal, 0)
  
  test("all three handle empty crate"):
    // Java: no packs
    val javaCrate = JavaCrate(List())
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception: no packs
    val exCrate = ExceptionCrate(List())
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad: no packs
    val monadCrate = Crate(List.empty[Pack[MonadBottle]])
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    assertEquals(javaTotal, 0)
    assertEquals(exTotal, 0)
    assertEquals(monadTotal, 0)
