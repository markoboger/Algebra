package monad

/** Exception-style: model throws for genuinely missing/empty elements. */

class EmptyBottleException extends Exception("Bottle is empty")

class ExceptionPack(val bottles: List[Bottle]):
  def isEmpty: Boolean = bottles.isEmpty

class ExceptionCrate(val packs: List[ExceptionPack]):
  def isEmpty: Boolean = packs.isEmpty

object ExceptionStyle:
  
  /** Aggregate: total volume with exceptions for empty bottles.
   *
   * Iterates over real lists; throws/catches only for empty bottles.
   */
  def totalVolume(crate: ExceptionCrate): Int =
    var total = 0
    
    for pack <- crate.packs do
      for bottle <- pack.bottles do
        try
          val drink = bottle.drink
          if drink == null then
            throw EmptyBottleException()
          total += drink.volumeMl
        catch
          case _: EmptyBottleException => ()  // skip empty bottles
    
    total
