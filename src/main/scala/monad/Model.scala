package monad

case class Drink(name: String, volumeMl: Int)

/** Pure null model: Bottle with nullable drink. */
case class Bottle(drink: Drink | Null)
