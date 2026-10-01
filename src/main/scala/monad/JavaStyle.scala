package monad

/** Java-style: pure null-based model. */

case class JavaBottle(drink: Drink | Null)

class JavaPack(val bottles: List[JavaBottle]):
  def isEmpty: Boolean = bottles.isEmpty

class JavaCrate(val packs: List[JavaPack]):
  def isEmpty: Boolean = packs.isEmpty

object JavaStyle:
  
  /** Aggregate: total volume of all full bottles.
   *
   * Nested for-loops with null checks.
   */
  def totalVolume(crate: JavaCrate): Int =
    var total = 0
    for pack <- crate.packs do
      for bottle <- pack.bottles do
        val drink = bottle.drink
        if drink != null then
          total += drink.volumeMl
    total
