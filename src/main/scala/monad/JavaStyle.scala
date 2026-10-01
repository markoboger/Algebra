package monad

/** Java-style: null-based accessors, deep defensive checks. */

class JavaCrate(packs: Array[JavaPack | Null]):
  
  def pack(index: Int): JavaPack | Null =
    if index >= 0 && index < packs.length then packs(index) else null
  
  def allPacks: Array[JavaPack | Null] = packs

class JavaPack(bottles: Array[Bottle | Null]):
  
  def bottle(index: Int): Bottle | Null =
    if index >= 0 && index < bottles.length then bottles(index) else null
  
  def allBottles: Array[Bottle | Null] = bottles

object JavaStyle:
  
  /** Navigate: crate → pack(i) → bottle(j) → drink.name
   *
   * Deep nesting with null checks at every level.
   */
  def getDrinkName(crate: JavaCrate | Null, packIdx: Int, bottleIdx: Int): String | Null =
    if crate != null then
      val pack = crate.pack(packIdx)
      if pack != null then
        val bottle = pack.bottle(bottleIdx)
        if bottle != null then
          val content = bottle.content
          if content.isDefined then
            content.get.name
          else null
        else null
      else null
    else null
  
  /** Aggregate: total volume of all full bottles. */
  def totalVolume(crate: JavaCrate | Null): Int =
    var total = 0
    if crate != null then
      val packs = crate.allPacks
      if packs != null then
        var i = 0
        while i < packs.length do
          val pack = packs(i)
          if pack != null then
            val bottles = pack.allBottles
            if bottles != null then
              var j = 0
              while j < bottles.length do
                val bottle = bottles(j)
                if bottle != null && bottle.content.isDefined then
                  total += bottle.content.get.volumeMl
                j += 1
          i += 1
    total
