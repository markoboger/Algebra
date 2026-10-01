package monad

/** Java-style imperative approach with null checks.
 *
 * This is how a Java developer would handle missing data before Java 8 Optional.
 * Uses null to represent missing bottles/packs, requiring defensive checks
 * at every access. This is honest Java style, not exaggerated.
 */

/** Java-style data structures (using null for missing elements). */
case class JavaPack(bottles: List[Bottle | Null] | Null)
case class JavaCrate(packs: List[JavaPack | Null] | Null)

object JavaStyle:
  
  /** Count full bottles in a crate with null checks at every level.
   *
   * Fixed bug from slide 26: original used `crate2.packs` instead of `crate.packs`.
   */
  def countFullBottles(crate: JavaCrate | Null): Int =
    var count = 0
    
    if crate != null then
      val packs = crate.packs  // Fixed: was crate2.packs in slide!
      if packs != null then
        var i = 0
        while i < packs.length do
          val pack = packs(i)
          if pack != null then
            val bottles = pack.bottles
            if bottles != null then
              var j = 0
              while j < bottles.length do
                val bottle = bottles(j)
                if bottle != null && bottle.full then
                  count += 1
                j += 1
          i += 1
    
    count
  
  /** Consume all bottles in a crate with null checks. */
  def consumeAll(crate: JavaCrate | Null): List[Bottle] =
    val consumed = scala.collection.mutable.ListBuffer[Bottle]()
    
    if crate != null then
      val packs = crate.packs
      if packs != null then
        var i = 0
        while i < packs.length do
          val pack = packs(i)
          if pack != null then
            val bottles = pack.bottles
            if bottles != null then
              var j = 0
              while j < bottles.length do
                val bottle = bottles(j)
                if bottle != null then
                  consumed += bottle.consume()
                j += 1
          i += 1
    
    consumed.toList

