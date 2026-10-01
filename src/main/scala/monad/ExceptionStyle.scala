package monad

/** Exception-style: model throws domain exceptions, client catches at outer scope. */

class NoSuchPackException extends Exception("Pack not found")
class NoSuchBottleException extends Exception("Bottle not found")

class ExceptionPack(bottles: List[Bottle]):
  
  /** Throws NoSuchBottleException if index out of bounds. */
  def bottle(index: Int): Bottle =
    if index >= 0 && index < bottles.length then bottles(index)
    else throw NoSuchBottleException()
  
  def size: Int = bottles.length

class ExceptionCrate(packs: List[ExceptionPack]):
  
  /** Throws NoSuchPackException if index out of bounds. */
  def pack(index: Int): ExceptionPack =
    if index >= 0 && index < packs.length then packs(index)
    else throw NoSuchPackException()
  
  def size: Int = packs.length

object ExceptionStyle:
  
  /** Aggregate: total volume with exception-based iteration control.
   *
   * Uses for-loops with index ranges; exceptions signal out-of-bounds.
   */
  def totalVolume(crate: ExceptionCrate): Int =
    var total = 0
    
    try
      for packIdx <- 0 until Int.MaxValue do
        val pack = crate.pack(packIdx)         // throws when no more packs
        
        try
          for bottleIdx <- 0 until Int.MaxValue do
            val bottle = pack.bottle(bottleIdx) // throws when no more bottles
            bottle.content match
              case Some(drink) => total += drink.volumeMl
              case None => ()
        catch
          case _: NoSuchBottleException => ()   // natural loop termination
    catch
      case _: NoSuchPackException => ()         // natural loop termination
    
    total
