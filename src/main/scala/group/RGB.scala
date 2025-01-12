import spire.algebra.Group

case class RGB(red: Int, green: Int, blue: Int) {
  def +(that: RGB): RGB = RGBGroup.combine(this, that)
  def normalized: RGB =
    RGB(red % 256, green % 256, blue % 256)
}

given RGBGroup: Group[RGB] with {
  override def empty: RGB = RGB(0, 0, 0)
  override def combine(a: RGB, b: RGB): RGB =
    RGB(
      (a.red + b.red) % 256,
      (a.green + b.green) % 256,
      (a.blue + b.blue) % 256
    )
  override def inverse(a: RGB): RGB =
    RGB((-a.red) % 256, (-a.green) % 256, (-a.blue) % 256).normalized
}

object RGBGroupExample {
  def main(args: Array[String]): Unit = {
    import spire.syntax.group.*

    val color1 = RGB(100, 150, 200)
    val color2 = RGB(50, 120, 100)

    val combined = color1 |+| color2
    val added = color1 + color2
    val diff = color1 |-| color2
    val identity = summon[Group[RGB]].empty
    val inverse = summon[Group[RGB]].inverse(color1)

    println(s"Combined: $combined")
    println(s"Difference: $diff")
    println(s"Identity: $identity")
    println(s"Inverse of color1: $inverse")
  }
}
