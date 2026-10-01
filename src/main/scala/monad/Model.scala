package monad

/** Shared domain model for all three implementations.
 *
 * Models a simple beverage inventory system:
 * - Bottle: individual container that can be full or empty
 * - Pack: collection of bottles (e.g., a 6-pack)
 * - Crate: collection of packs (e.g., a delivery crate)
 */

/** A bottle that can be full or empty. */
case class Bottle(var empty: Boolean = false):
  /** Consume this bottle, marking it as empty. */
  def consume(): Bottle =
    empty = true
    this
    
  override def toString: String = if empty then "b" else "B"

/** A pack containing multiple bottles. */
case class Pack(bottles: List[Bottle]):
  override def toString: String = s"Pack[${bottles.mkString(",")}]"

/** A crate containing multiple packs. */
case class Crate(packs: List[Pack]):
  override def toString: String = s"Crate[${packs.mkString(";")}]"

/** Helper functions for creating test data. */
object Model:
  def fullBottle: Bottle = Bottle(empty = false)
  def emptyBottle: Bottle = Bottle(empty = true)
  
  /** Count full bottles in a list (ignoring nulls). */
  def countFull(bottles: List[Bottle | Null]): Int =
    bottles.count(b => b != null && !b.empty)
