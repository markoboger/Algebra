package monad

import munit.FunSuite

/** Tests proving all three implementations handle identical scenarios
 * and produce identical results.
 */
class EquivalenceTest extends FunSuite:
  
  test("all three count same in complete crate"):
    // Java style
    val javaPacks = List(
      JavaPack(List(Bottle(), Bottle(), Bottle())),
      JavaPack(List(Bottle(), Bottle()))
    )
    val javaCrate = JavaCrate(javaPacks)
    val javaCount = JavaStyle.countFullBottles(javaCrate)
    
    // Exception style
    val exPacks = List(
      ExceptionPack(List(Bottle(), Bottle(), Bottle())),
      ExceptionPack(List(Bottle(), Bottle()))
    )
    val exCrate = ExceptionCrate(exPacks)
    val exCount = ExceptionStyle.countFullBottles(exCrate)
    
    // Monad style
    val monadPacks = List(
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], Some(Bottle()): Option[Bottle], Some(Bottle()): Option[Bottle])),
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], Some(Bottle()): Option[Bottle]))
    )
    val monadCrate = MonadStyle.Crate(monadPacks)
    val monadCount = MonadStyle.countFullBottles(monadCrate)
    
    assertEquals(javaCount, 5)
    assertEquals(exCount, 5)
    assertEquals(monadCount, 5)
  
  test("all three count same with missing bottles"):
    // Java style
    val javaPacks = List(
      JavaPack(List(Bottle(), null, Bottle(), null)),
      JavaPack(List(Bottle(), Bottle(), null))
    )
    val javaCrate = JavaCrate(javaPacks)
    val javaCount = JavaStyle.countFullBottles(javaCrate)
    
    // Exception style
    val exPacks = List(
      ExceptionPack(List(Bottle(), null, Bottle(), null)),
      ExceptionPack(List(Bottle(), Bottle(), null))
    )
    val exCrate = ExceptionCrate(exPacks)
    val exCount = ExceptionStyle.countFullBottles(exCrate)
    
    // Monad style
    val monadPacks = List(
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], None, Some(Bottle()), None)),
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], Some(Bottle()), None))
    )
    val monadCrate = MonadStyle.Crate(monadPacks)
    val monadCount = MonadStyle.countFullBottles(monadCrate)
    
    assertEquals(javaCount, 4)
    assertEquals(exCount, 4)
    assertEquals(monadCount, 4)
  
  test("all three count same with missing pack"):
    // Java style
    val javaPacks = List(
      JavaPack(List(Bottle(), Bottle())),
      null,
      JavaPack(List(Bottle()))
    )
    val javaCrate = JavaCrate(javaPacks)
    val javaCount = JavaStyle.countFullBottles(javaCrate)
    
    // Exception style
    val exPacks = List(
      ExceptionPack(List(Bottle(), Bottle())),
      null,
      ExceptionPack(List(Bottle()))
    )
    val exCrate = ExceptionCrate(exPacks)
    val exCount = ExceptionStyle.countFullBottles(exCrate)
    
    // Monad style: empty pack represents missing pack
    val monadPacks = List(
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], Some(Bottle()))),
      MonadStyle.Pack(List()),
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle]))
    )
    val monadCrate = MonadStyle.Crate(monadPacks)
    val monadCount = MonadStyle.countFullBottles(monadCrate)
    
    assertEquals(javaCount, 3)
    assertEquals(exCount, 3)
    assertEquals(monadCount, 3)
  
  test("all three count same with mix of full and empty bottles"):
    // Java style
    val javaPacks = List(
      JavaPack(List(Bottle(), Bottle(false), Bottle())),
      JavaPack(List(Bottle(false), Bottle()))
    )
    val javaCrate = JavaCrate(javaPacks)
    val javaCount = JavaStyle.countFullBottles(javaCrate)
    
    // Exception style
    val exPacks = List(
      ExceptionPack(List(Bottle(), Bottle(false), Bottle())),
      ExceptionPack(List(Bottle(false), Bottle()))
    )
    val exCrate = ExceptionCrate(exPacks)
    val exCount = ExceptionStyle.countFullBottles(exCrate)
    
    // Monad style
    val monadPacks = List(
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], Some(Bottle(false)): Option[Bottle], Some(Bottle()): Option[Bottle])),
      MonadStyle.Pack(List(Some(Bottle(false)): Option[Bottle], Some(Bottle()): Option[Bottle]))
    )
    val monadCrate = MonadStyle.Crate(monadPacks)
    val monadCount = MonadStyle.countFullBottles(monadCrate)
    
    assertEquals(javaCount, 3)
    assertEquals(exCount, 3)
    assertEquals(monadCount, 3)
  
  test("all three handle null crate"):
    val javaCount = JavaStyle.countFullBottles(null)
    
    // Exception style needs an actual object (can't have null crate)
    val exCrate = ExceptionCrate(null)
    val exCount = ExceptionStyle.countFullBottles(exCrate)
    
    // Monad style: empty crate (type annotation needed)
    val monadCrate = MonadStyle.Crate(List[MonadStyle.Pack[Option[Bottle]]]())
    val monadCount = MonadStyle.countFullBottles(monadCrate)
    
    assertEquals(javaCount, 0)
    assertEquals(exCount, 0)
    assertEquals(monadCount, 0)
  
  test("all three produce same consumed bottles"):
    // Java style
    val javaPacks = List(
      JavaPack(List(Bottle(), null, Bottle()))
    )
    val javaCrate = JavaCrate(javaPacks)
    val javaConsumed = JavaStyle.consumeAll(javaCrate)
    
    // Exception style
    val exPacks = List(
      ExceptionPack(List(Bottle(), null, Bottle()))
    )
    val exCrate = ExceptionCrate(exPacks)
    val exConsumed = ExceptionStyle.consumeAll(exCrate)
    
    // Monad style
    val monadPacks = List(
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], None, Some(Bottle())))
    )
    val monadCrate = MonadStyle.Crate(monadPacks)
    val monadConsumed = MonadStyle.consumeAll(monadCrate)
    
    assertEquals(javaConsumed.length, 2)
    assertEquals(exConsumed.length, 2)
    assertEquals(monadConsumed.length, 2)
    
    assert(javaConsumed.forall(!_.full))
    assert(exConsumed.forall(!_.full))
    assert(monadConsumed.forall(!_.full))

