package monad

/** Domain model: Bottles contain drinks, packs contain bottles, crates contain packs.
 * Some elements may be missing.
 */

case class Drink(name: String, volumeMl: Int)

case class Bottle(content: Option[Drink])
