package monad

/** Java-style: pure null-based model.
 *
 * Missing elements represented as null throughout.
 */

class JavaPack(val bottles: List[Bottle | Null])

class JavaCrate(val packs: List[JavaPack | Null])

object JavaStyle:
  
  /** Aggregate: total volume of all full bottles.
   *
   * Nested for-loops with null checks at every level.
   */
  def totalVolume(crate: JavaCrate | Null): Int =
    var total = 0
    if crate != null then
      for pack <- crate.packs do
        if pack != null then
          for bottle <- pack.bottles do
            if bottle != null then
              val drink = bottle.drink
              if drink != null then
                total += drink.volumeMl
    total
