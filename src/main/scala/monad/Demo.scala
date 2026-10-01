package monad

/** Side-by-side demonstration of three approaches to navigating nested data. */
object Demo:
  
  def main(args: Array[String]): Unit =
    println("=" * 70)
    println("Part A: Navigation Through Nested, Possibly-Missing Data")
    println("=" * 70)
    
    testNavigation()
    testAggregate()
    
    println("\n" + "=" * 70)
    println("Part B: Custom Domain Monads (Pack[T], Crate[T])")
    println("=" * 70)
    
    demonstrateDomainMonads()
    
    println("\n" + "=" * 70)
    println("Summary:")
    println("  Java: Deep nesting, null checks at every level")
    println("  Exception: Try-catch, cleaner but expensive")
    println("  Monad: Single for-comprehension, Option.flatMap/map composition")
    println("=" * 70)
  
  def testNavigation(): Unit =
    println("\n--- Navigation: crate → pack(0) → bottle(1) → drink.name ---")
    
    // Shared data
    val beer = Drink("Beer", 500)
    val wine = Drink("Wine", 750)
    
    // Java style
    val javaPack0 = JavaPack(Array(Bottle(Some(beer)), Bottle(Some(wine)), null))
    val javaCrate = JavaCrate(Array(javaPack0, null))
    val javaResult = JavaStyle.getDrinkName(javaCrate, 0, 1)
    
    // Exception style
    val exPack0 = ExceptionPack(Array(Bottle(Some(beer)), Bottle(Some(wine))))
    val exCrate = ExceptionCrate(Array(exPack0))
    val exResult = ExceptionStyle.getDrinkName(exCrate, 0, 1)
    
    // Monad style
    val monadPack0 = MonadicPack(Array(Bottle(Some(beer)), Bottle(Some(wine))))
    val monadCrate = MonadicCrate(Array(monadPack0))
    val monadResult = MonadStyle.getDrinkName(monadCrate, 0, 1)
    
    println(s"  Java Style:      ${javaResult}")
    println(s"  Exception Style: ${exResult}")
    println(s"  Monad Style:     ${monadResult}")
  
  def testAggregate(): Unit =
    println("\n--- Aggregate: Total Volume of All Full Bottles ---")
    
    val beer = Drink("Beer", 500)
    val wine = Drink("Wine", 750)
    val water = Drink("Water", 330)
    
    // Java style
    val javaPack1 = JavaPack(Array(Bottle(Some(beer)), Bottle(Some(wine))))
    val javaPack2 = JavaPack(Array(Bottle(None), Bottle(Some(water))))
    val javaCrate = JavaCrate(Array(javaPack1, javaPack2))
    val javaTotal = JavaStyle.totalVolume(javaCrate)
    
    // Exception style
    val exPack1 = ExceptionPack(Array(Bottle(Some(beer)), Bottle(Some(wine))))
    val exPack2 = ExceptionPack(Array(Bottle(None), Bottle(Some(water))))
    val exCrate = ExceptionCrate(Array(exPack1, exPack2))
    val exTotal = ExceptionStyle.totalVolume(exCrate)
    
    // Monad style
    val monadPack1 = MonadicPack(Array(Bottle(Some(beer)), Bottle(Some(wine))))
    val monadPack2 = MonadicPack(Array(Bottle(None), Bottle(Some(water))))
    val monadCrate = MonadicCrate(Array(monadPack1, monadPack2))
    val monadTotal = MonadStyle.totalVolume(monadCrate)
    
    println(s"  Java Style:      ${javaTotal}ml")
    println(s"  Exception Style: ${exTotal}ml")
    println(s"  Monad Style:     ${monadTotal}ml")
  
  def demonstrateDomainMonads(): Unit =
    println("\n--- Domain Monads: Pack[T].flatMap, Crate[T].flatMap ---")
    
    val pack = Pack(List(1, 2, 3))
    val doubled = pack.map(_ * 2)
    println(s"  Pack.map: $pack -> $doubled")
    
    val expanded = pack.flatMap(x => Pack(List(x, x * 10)))
    println(s"  Pack.flatMap: $pack -> $expanded")
    
    val crate = Crate(List(Pack(List(1, 2)), Pack(List(3))))
    val flattened = crate.flatten
    println(s"  Crate.flatten: ${crate.bottles} -> $flattened")
    
    // For-comprehension using domain monads
    val result = for
      x <- pack
      y <- Pack(List(x, x + 1))
    yield y * 2
    println(s"  For-comprehension: $result")
