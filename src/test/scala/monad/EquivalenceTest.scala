package monad

import munit.FunSuite

/** Tests proving all three navigation styles produce identical results. */
class EquivalenceTest extends FunSuite:
  
  val beer = Drink("Beer", 500)
  val wine = Drink("Wine", 750)
  val water = Drink("Water", 330)
  
  test("all three navigate successfully to existing drink"):
    // Java
    val javaPack = JavaPack(Array(Bottle(Some(beer)), Bottle(Some(wine))))
    val javaCrate = JavaCrate(Array(javaPack))
    val javaResult = JavaStyle.getDrinkName(javaCrate, 0, 1)
    
    // Exception
    val exPack = ExceptionPack(Array(Bottle(Some(beer)), Bottle(Some(wine))))
    val exCrate = ExceptionCrate(Array(exPack))
    val exResult = ExceptionStyle.getDrinkName(exCrate, 0, 1)
    
    // Monad
    val monadPack = Pack(List(Bottle(Some(beer)), Bottle(Some(wine))))
    val monadCrate = MonadicCrateOfPacks(Array(monadPack))
    val monadResult = MonadStyle.getDrinkName(monadCrate, 0, 1)
    
    assertEquals(javaResult, "Wine")
    assertEquals(exResult, "Wine")
    assertEquals(monadResult, Some("Wine"))
  
  test("all three handle missing pack"):
    // Java
    val javaCrate = JavaCrate(Array(null))
    val javaResult = JavaStyle.getDrinkName(javaCrate, 0, 0)
    
    // Exception
    val exCrate = ExceptionCrate(Array())
    val exResult = ExceptionStyle.getDrinkName(exCrate, 0, 0)
    
    // Monad
    val monadCrate = MonadicCrateOfPacks(Array())
    val monadResult = MonadStyle.getDrinkName(monadCrate, 0, 0)
    
    assertEquals(javaResult, null)
    assertEquals(exResult, null)
    assertEquals(monadResult, None)
  
  test("all three handle missing bottle"):
    // Java
    val javaPack = JavaPack(Array(Bottle(Some(beer))))
    val javaCrate = JavaCrate(Array(javaPack))
    val javaResult = JavaStyle.getDrinkName(javaCrate, 0, 5)
    
    // Exception
    val exPack = ExceptionPack(Array(Bottle(Some(beer))))
    val exCrate = ExceptionCrate(Array(exPack))
    val exResult = ExceptionStyle.getDrinkName(exCrate, 0, 5)
    
    // Monad
    val monadPack = Pack(List(Bottle(Some(beer))))
    val monadCrate = MonadicCrateOfPacks(Array(monadPack))
    val monadResult = MonadStyle.getDrinkName(monadCrate, 0, 5)
    
    assertEquals(javaResult, null)
    assertEquals(exResult, null)
    assertEquals(monadResult, None)
  
  test("all three handle empty bottle"):
    // Java
    val javaPack = JavaPack(Array(Bottle(None)))
    val javaCrate = JavaCrate(Array(javaPack))
    val javaResult = JavaStyle.getDrinkName(javaCrate, 0, 0)
    
    // Exception
    val exPack = ExceptionPack(Array(Bottle(None)))
    val exCrate = ExceptionCrate(Array(exPack))
    val exResult = ExceptionStyle.getDrinkName(exCrate, 0, 0)
    
    // Monad
    val monadPack = Pack(List(Bottle(None)))
    val monadCrate = MonadicCrateOfPacks(Array(monadPack))
    val monadResult = MonadStyle.getDrinkName(monadCrate, 0, 0)
    
    assertEquals(javaResult, null)
    assertEquals(exResult, null)
    assertEquals(monadResult, None)
  
  test("all three compute same total volume"):
    val bottles1 = Array(Bottle(Some(beer)), Bottle(Some(wine)))
    val bottles2 = Array(Bottle(None), Bottle(Some(water)))
    
    // Java
    val javaPack1 = JavaPack(bottles1)
    val javaPack2 = JavaPack(bottles2)
    val javaCrate = JavaCrate(Array(javaPack1, javaPack2))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception
    val exPack1 = ExceptionPack(bottles1.clone())
    val exPack2 = ExceptionPack(bottles2.clone())
    val exCrate = ExceptionCrate(Array(exPack1, exPack2))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad
    val monadPack1 = Pack(List(Bottle(Some(beer)), Bottle(Some(wine))))
    val monadPack2 = Pack(List(Bottle(None), Bottle(Some(water))))
    val monadCrate = MonadicCrateOfPacks(Array(monadPack1, monadPack2))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    val expected = 500 + 750 + 330 // beer + wine + water
    assertEquals(javaTotal, expected)
    assertEquals(exTotal, expected)
    assertEquals(monadTotal, expected)
  
  test("all three handle empty crate"):
    // Java
    val javaCrate = JavaCrate(Array())
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception
    val exCrate = ExceptionCrate(Array())
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad
    val monadCrate = MonadicCrateOfPacks(Array())
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    assertEquals(javaTotal, 0)
    assertEquals(exTotal, 0)
    assertEquals(monadTotal, 0)
