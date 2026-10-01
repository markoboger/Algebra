package monad

case class Drink(name: String, volumeMl: Int)
case class Bottle(content: Option[Drink])
