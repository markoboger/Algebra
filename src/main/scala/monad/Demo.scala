package monad

/** Demonstration comparing three approaches side-by-side.
 *
 * All three handle identical test cases to enable fair comparison.
 */
object Demo:
  
  def main(args: Array[String]): Unit =
    println("=" * 70)
    println("Monad Teaching Example: Three Approaches to Handling Missing Data")
    println("=" * 70)
    
    testCase1()
    testCase2()
    testCase3()
    
    println("\n" + "=" * 70)
    println("Comparison:")
    println("  Java Style: Manual null checks at every level (verbose, error-prone)")
    println("  Exception Style: Domain throws, client catches (cleaner but expensive)")
    println("  Monad Style: For-comprehensions over lawful monads (flat, type-safe)")
    println("=" * 70)
  
  def testCase1(): Unit =
    println("\n--- Test Case 1: Complete Crate (no missing data) ---")
    
    // Java style
    val javaPacks = List(
      JavaPack(List(Bottle(), Bottle())),
      JavaPack(List(Bottle(), Bottle(), Bottle()))
    )
    val javaCrate = JavaCrate(javaPacks)
    val javaCount = JavaStyle.countFullBottles(javaCrate)
    
    // Exception style
    val exPacks = List(
      ExceptionPack(List(Bottle(), Bottle())),
      ExceptionPack(List(Bottle(), Bottle(), Bottle()))
    )
    val exCrate = ExceptionCrate(exPacks)
    val exCount = ExceptionStyle.countFullBottles(exCrate)
    
    // Monad style
    val monadPacks = List(
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], Some(Bottle()): Option[Bottle])),
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], Some(Bottle()): Option[Bottle], Some(Bottle()): Option[Bottle]))
    )
    val monadCrate = MonadStyle.Crate(monadPacks)
    val monadCount = MonadStyle.countFullBottles(monadCrate)
    
    println(s"  Java Style:      $javaCount full bottles")
    println(s"  Exception Style: $exCount full bottles")
    println(s"  Monad Style:     $monadCount full bottles")
  
  def testCase2(): Unit =
    println("\n--- Test Case 2: Missing Bottles ---")
    
    // Java style: null for missing bottles
    val javaPacks = List(
      JavaPack(List(Bottle(), null, Bottle())),
      JavaPack(List(null, Bottle()))
    )
    val javaCrate = JavaCrate(javaPacks)
    val javaCount = JavaStyle.countFullBottles(javaCrate)
    
    // Exception style: null causes exceptions
    val exPacks = List(
      ExceptionPack(List(Bottle(), null, Bottle())),
      ExceptionPack(List(null, Bottle()))
    )
    val exCrate = ExceptionCrate(exPacks)
    val exCount = ExceptionStyle.countFullBottles(exCrate)
    
    // Monad style: None for missing bottles
    val monadPacks = List(
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], None, Some(Bottle()))),
      MonadStyle.Pack(List(None: Option[Bottle], Some(Bottle())))
    )
    val monadCrate = MonadStyle.Crate(monadPacks)
    val monadCount = MonadStyle.countFullBottles(monadCrate)
    
    println(s"  Java Style:      $javaCount full bottles")
    println(s"  Exception Style: $exCount full bottles")
    println(s"  Monad Style:     $monadCount full bottles")
  
  def testCase3(): Unit =
    println("\n--- Test Case 3: Missing Packs ---")
    
    // Java style: null for missing pack
    val javaPacks = List(
      JavaPack(List(Bottle(), Bottle())),
      null,
      JavaPack(List(Bottle()))
    )
    val javaCrate = JavaCrate(javaPacks)
    val javaCount = JavaStyle.countFullBottles(javaCrate)
    
    // Exception style: null causes exception
    val exPacks = List(
      ExceptionPack(List(Bottle(), Bottle())),
      null,
      ExceptionPack(List(Bottle()))
    )
    val exCrate = ExceptionCrate(exPacks)
    val exCount = ExceptionStyle.countFullBottles(exCrate)
    
    // Monad style: empty pack for missing pack
    val monadPacks = List(
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle], Some(Bottle()))),
      MonadStyle.Pack(List()),  // empty pack = no bottles
      MonadStyle.Pack(List(Some(Bottle()): Option[Bottle]))
    )
    val monadCrate = MonadStyle.Crate(monadPacks)
    val monadCount = MonadStyle.countFullBottles(monadCrate)
    
    println(s"  Java Style:      $javaCount full bottles")
    println(s"  Exception Style: $exCount full bottles")
    println(s"  Monad Style:     $monadCount full bottles")

