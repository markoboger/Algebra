package monad

/** Exception-style: accessors throw domain exceptions, client uses try-catch. */

class NoSuchPackException extends Exception("Pack not found")
class NoSuchBottleException extends Exception("Bottle not found")
class EmptyBottleException extends Exception("Bottle is empty")

class ExceptionCrate(packs: Array[ExceptionPack]):
  def pack(index: Int): ExceptionPack =
    if index >= 0 && index < packs.length then packs(index)
    else throw NoSuchPackException()
  
  def allPacks: Array[ExceptionPack] = packs

class ExceptionPack(bottles: Array[Bottle]):
  def bottle(index: Int): Bottle =
    if index >= 0 && index < bottles.length then bottles(index)
    else throw NoSuchBottleException()
  
  def allBottles: Array[Bottle] = bottles

object ExceptionStyle:
  
  /** Navigate: crate → pack(i) → bottle(j) → drink.name
   *
   * Single try block with multiple catch clauses for different failure modes.
   */
  def getDrinkName(crate: ExceptionCrate, packIdx: Int, bottleIdx: Int): String | Null =
    try
      val pack = crate.pack(packIdx)
      val bottle = pack.bottle(bottleIdx)
      bottle.content match
        case Some(drink) => drink.name
        case None => throw EmptyBottleException()
    catch
      case _: NoSuchPackException => null
      case _: NoSuchBottleException => null
      case _: EmptyBottleException => null
  
  /** Aggregate: total volume using indexed access with exceptions. */
  def totalVolume(crate: ExceptionCrate): Int =
    var total = 0
    var packIdx = 0
    try
      while true do  // Will throw when out of packs
        val pack = crate.pack(packIdx)
        var bottleIdx = 0
        try
          while true do  // Will throw when out of bottles
            val bottle = pack.bottle(bottleIdx)
            bottle.content match
              case Some(drink) => total += drink.volumeMl
              case None => ()
            bottleIdx += 1
        catch
          case _: NoSuchBottleException => ()  // End of bottles in this pack
        packIdx += 1
    catch
      case _: NoSuchPackException => ()  // End of packs
    total
