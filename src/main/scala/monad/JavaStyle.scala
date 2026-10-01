package monad

/** Java-style: null-based accessors, deep defensive checks. */

class JavaPack(bottles: List[Bottle | Null]):
  def allBottles: List[Bottle | Null] = bottles

class JavaCrate(packs: List[JavaPack | Null]):
  def allPacks: List[JavaPack | Null] = packs

object JavaStyle:
  
  /** Aggregate: total volume of all full bottles.
   *
   * Nested for-loops with null checks at every level.
   */
  def totalVolume(crate: JavaCrate | Null): Int =
    var total = 0
    if crate != null then
      for pack <- crate.allPacks do
        if pack != null then
          for bottle <- pack.allBottles do
            if bottle != null && bottle.content.isDefined then
              total += bottle.content.get.volumeMl
    total
