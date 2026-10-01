package monad

/** Java-style imperative approach with null checks.
 *
 * This implementation demonstrates the "bad old way" of handling missing data:
 * - Uses null to represent missing bottles, packs, or crates
 * - Requires deeply nested null checks to avoid NullPointerException
 * - Verbose and error-prone (easy to forget a check)
 * - The nesting depth matches the data structure depth
 *
 * This is the approach from slide 26, with the bug fixed:
 * the original used `crate2.packs` instead of `crate.packs` in the null check.
 */
object JavaStyle:
  
  /** Count full bottles in a crate, handling null at every level.
   *
   * Demonstrates the "pyramid of doom" that arises from defensive null checking.
   * Each level of the data structure requires an if-check, leading to deeply
   * nested code that is hard to read and maintain.
   */
  def countFullBottles(crate: Crate | Null): Int =
    var count = 0
    
    if crate != null then
      val packs = crate.packs  // Fixed: was crate2.packs in slide 26!
      if packs != null then
        packs.foreach { pack =>
          if pack != null then
            val bottles = pack.bottles
            if bottles != null then
              bottles.foreach { bottle =>
                if bottle != null then
                  if !bottle.empty then
                    count += 1
              }
        }
    
    count
  
  /** Consume all bottles in a crate, handling null at every level. */
  def consumeAll(crate: Crate | Null): Unit =
    if crate != null then
      val packs = crate.packs
      if packs != null then
        packs.foreach { pack =>
          if pack != null then
            val bottles = pack.bottles
            if bottles != null then
              bottles.foreach { bottle =>
                if bottle != null then
                  bottle.consume()
                  println(s"  Consumed: $bottle")
                else
                  println("  Found null bottle")
              }
            else
              println("  Found null bottles list")
          else
            println("  Found null pack")
        }
      else
        println("  Found null packs list")
    else
      println("  Found null crate")
