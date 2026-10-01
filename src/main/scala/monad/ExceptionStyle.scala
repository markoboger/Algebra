package monad

/** Exception-style: model throws domain exceptions, client catches at outer scope. */

class NoSuchPackException extends Exception("Pack not found")
class NoSuchBottleException extends Exception("Bottle not found")
class EmptyBottleException extends Exception("Bottle is empty")

class ExceptionCrate(packs: Array[ExceptionPack]):
  
  /** Throws NoSuchPackException if index out of bounds. */
  def pack(index: Int): ExceptionPack =
    if index >= 0 && index < packs.length then packs(index)
    else throw NoSuchPackException()
  
  def allPacks: Array[ExceptionPack] = packs

class ExceptionPack(bottles: Array[Bottle]):
  
  /** Throws NoSuchBottleException if index out of bounds. */
  def bottle(index: Int): Bottle =
    if index >= 0 && index < bottles.length then bottles(index)
    else throw NoSuchBottleException()
  
  def allBottles: Array[Bottle] = bottles

object ExceptionStyle:
  
  /** Navigate: crate → pack(i) → bottle(j) → drink.name
   *
   * Model methods throw; client catches at natural outer boundary.
   */
  def getDrinkName(crate: ExceptionCrate, packIdx: Int, bottleIdx: Int): String | Null =
    try
      val pack = crate.pack(packIdx)           // may throw NoSuchPackException
      val bottle = pack.bottle(bottleIdx)      // may throw NoSuchBottleException
      bottle.content match
        case Some(drink) => drink.name
        case None => throw EmptyBottleException()
    catch
      case _: NoSuchPackException | _: NoSuchBottleException | _: EmptyBottleException => null
  
  /** Aggregate: total volume with exception-based iteration control. */
  def totalVolume(crate: ExceptionCrate): Int =
    var total = 0
    var packIdx = 0
    
    try
      while true do
        val pack = crate.pack(packIdx)         // throws when no more packs
        var bottleIdx = 0
        
        try
          while true do
            val bottle = pack.bottle(bottleIdx) // throws when no more bottles
            bottle.content match
              case Some(drink) => total += drink.volumeMl
              case None => ()
            bottleIdx += 1
        catch
          case _: NoSuchBottleException => ()   // natural loop termination
        
        packIdx += 1
    catch
      case _: NoSuchPackException => ()         // natural loop termination
    
    total
