package monad

/** Demonstration of three approaches to handling missing data.
 *
 * Run this to see side-by-side comparison of:
 * 1. Java-style imperative null checks
 * 2. Exception-based error handling
 * 3. Monad-based functional composition
 *
 * All three handle the same test cases to enable fair comparison.
 */
object Demo:
  
  def main(args: Array[String]): Unit =
    println("=" * 70)
    println("Monad Teaching Example: Three Approaches to Handling Missing Data")
    println("=" * 70)
    
    // Test Case 1: Perfect crate (all bottles present and full)
    println("\n--- Test Case 1: Perfect Crate ---")
    testPerfectCrate()
    
    // Test Case 2: Crate with some missing bottles
    println("\n--- Test Case 2: Crate with Missing Bottles ---")
    testMissingBottles()
    
    // Test Case 3: Completely null crate
    println("\n--- Test Case 3: Null Crate ---")
    testNullCrate()
    
    println("\n" + "=" * 70)
    println("Summary:")
    println("- Java Style: Deeply nested null checks, verbose, error-prone")
    println("- Exception Style: Flatter code, but exceptions are expensive")
    println("- Monad Style: Flat for-comprehensions, type-safe, composable")
    println("=" * 70)
  
  /** Test with a perfect crate (no missing data). */
  def testPerfectCrate(): Unit =
    import Model.*
    
    // Create test data for imperative/exception styles
    val pack1 = Pack(List(fullBottle, fullBottle, fullBottle))
    val pack2 = Pack(List(fullBottle, fullBottle))
    val crate = Crate(List(pack1, pack2))
    
    // Create equivalent monadic data
    val mPack1 = MonadicPack(List(Some(fullBottle), Some(fullBottle), Some(fullBottle)))
    val mPack2 = MonadicPack(List(Some(fullBottle), Some(fullBottle)))
    val mCrate = MonadicCrate(List(Some(mPack1), Some(mPack2)))
    
    println("\nJava Style (null checks):")
    val count1 = JavaStyle.countFullBottles(crate)
    println(s"  Full bottles: $count1")
    
    println("\nException Style:")
    val count2 = ExceptionStyle.countFullBottles(crate)
    println(s"  Full bottles: $count2")
    
    println("\nMonad Style:")
    val count3 = MonadStyle.countFullBottles(mCrate)
    println(s"  Full bottles: $count3")
  
  /** Test with some missing bottles (null in imperative, None in monadic). */
  def testMissingBottles(): Unit =
    import Model.*
    
    // Create test data with nulls for imperative/exception styles
    val pack3 = Pack(List(fullBottle, null, fullBottle))
    val pack4 = Pack(List(null, fullBottle))
    val crate = Crate(List(pack3, pack4))
    
    // Create equivalent monadic data with None
    val mPack3 = MonadicPack(List(Some(fullBottle), None, Some(fullBottle)))
    val mPack4 = MonadicPack(List(None, Some(fullBottle)))
    val mCrate = MonadicCrate(List(Some(mPack3), Some(mPack4)))
    
    println("\nJava Style (null checks):")
    val count1 = JavaStyle.countFullBottles(crate)
    println(s"  Full bottles: $count1")
    
    println("\nException Style:")
    val count2 = ExceptionStyle.countFullBottles(crate)
    println(s"  Full bottles: $count2")
    
    println("\nMonad Style:")
    val count3 = MonadStyle.countFullBottles(mCrate)
    println(s"  Full bottles: $count3")
  
  /** Test with completely missing crate (null). */
  def testNullCrate(): Unit =
    println("\nJava Style (null checks):")
    val count1 = JavaStyle.countFullBottles(null)
    println(s"  Full bottles: $count1")
    
    println("\nException Style:")
    val count2 = ExceptionStyle.countFullBottles(null)
    println(s"  Full bottles: $count2")
    
    println("\nMonad Style:")
    val mCrate = MonadicCrate[Bottle](List())  // Empty crate
    val count3 = MonadStyle.countFullBottles(mCrate)
    println(s"  Full bottles: $count3")
