package monad

/** Exception-style: model throws for missing elements. */

class EmptyBottleException extends Exception("Bottle is empty")

case class ExceptionBottle(private val drinkOpt: Option[Drink]):
  /** Throws EmptyBottleException if bottle is empty. */
  def drink: Drink = drinkOpt match
    case Some(d) => d
    case None => throw EmptyBottleException()

class ExceptionPack(val bottles: List[ExceptionBottle]):
  def isEmpty: Boolean = bottles.isEmpty

class ExceptionCrate(val packs: List[ExceptionPack]):
  def isEmpty: Boolean = packs.isEmpty

object ExceptionStyle:
  
  /** Aggregate: total volume with exceptions for empty bottles.
   *
   * Model throws, client catches at outer scope.
   */
  def totalVolume(crate: ExceptionCrate): Int =
    var total = 0
    
    for pack <- crate.packs do
      for bottle <- pack.bottles do
        try
          val drink = bottle.drink  // may throw EmptyBottleException
          total += drink.volumeMl
        catch
          case _: EmptyBottleException => ()  // skip empty bottles
    
    total
