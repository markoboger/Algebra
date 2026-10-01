package monad

/** Shared domain model for all three implementations.
 *
 * Domain: A beverage inventory system
 * - Bottle: individual container (can be full or empty)
 * - Pack: collection of bottles
 * - Crate: collection of packs
 *
 * Design principle: Keep the domain types simple and pure.
 * Each implementation (Java/Exception/Monad) handles missing data differently.
 */

/** A bottle that is either full or empty. */
case class Bottle(full: Boolean):
  def consume(): Bottle = Bottle(full = false)
  override def toString: String = if full then "B" else "b"

object Bottle:
  def apply(): Bottle = Bottle(full = true)

