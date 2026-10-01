package monad

/** Exception-based error handling approach.
 *
 * This implementation demonstrates using exceptions for control flow:
 * - Throws exceptions when encountering missing data
 * - Uses try-catch blocks to handle errors
 * - Cleaner than null checks (no deep nesting)
 * - But exceptions are expensive and semantically wrong for "expected" missing data
 * - Mixes business logic with error handling
 *
 * This is better than null checks but still has drawbacks:
 * - Performance overhead of exception creation and stack unwinding
 * - try-catch can be far from the throw, making flow unclear
 * - Exceptions are for exceptional circumstances, not normal flow
 */

class MissingDataException(message: String) extends Exception(message)

object ExceptionStyle:
  
  /** Count full bottles in a crate, handling exceptions for each missing part.
   *
   * Instead of nested null checks, we use try-catch blocks at each level
   * to handle missing data. This allows us to continue processing even when
   * some elements are missing, similar to the null-check approach.
   */
  def countFullBottles(crate: Crate | Null): Int =
    if crate == null then
      println("  Error: Crate is null")
      return 0
    
    var count = 0
    val packs = crate.packs
    
    if packs == null then
      println("  Error: Packs list is null")
      return 0
    
    for pack <- packs do
      try
        if pack == null then
          throw MissingDataException("Pack is null")
        
        val bottles = pack.bottles
        if bottles == null then
          throw MissingDataException("Bottles list is null")
        
        for bottle <- bottles do
          try
            if bottle == null then
              throw MissingDataException("Bottle is null")
            
            if !bottle.empty then
              count += 1
          catch
            case e: MissingDataException =>
              // Skip this bottle and continue
              ()
      catch
        case e: MissingDataException =>
          // Skip this pack and continue
          ()
    
    count
  
  /** Consume all bottles in a crate, handling exceptions for missing parts. */
  def consumeAll(crate: Crate | Null): Unit =
    if crate == null then
      println("  Error: Crate is null")
      return
    
    val packs = crate.packs
    if packs == null then
      println("  Error: Packs list is null")
      return
    
    for pack <- packs do
      try
        if pack == null then
          throw MissingDataException("Pack is null")
        
        val bottles = pack.bottles
        if bottles == null then
          throw MissingDataException("Bottles list is null")
        
        for bottle <- bottles do
          try
            if bottle == null then
              throw MissingDataException("Bottle is null")
            
            bottle.consume()
            println(s"  Consumed: $bottle")
          catch
            case e: MissingDataException =>
              println(s"  Error: ${e.getMessage}")
      catch
        case e: MissingDataException =>
          println(s"  Error: ${e.getMessage}")
