// Interactive Monad Example Worksheet
// Run this in a Scala REPL or worksheet environment

import monad.*
import monad.Model.*

// ===== Java Style: Null Checks =====
println("=== Java Style ===")

val pack1 = Pack(List(fullBottle, null, fullBottle))
val crate1 = Crate(List(pack1))

val count1 = JavaStyle.countFullBottles(crate1)
println(s"Full bottles: $count1")  // Should be 2

// ===== Exception Style =====
println("\n=== Exception Style ===")

val count2 = ExceptionStyle.countFullBottles(crate1)
println(s"Full bottles: $count2")  // Should be 2

// ===== Monad Style =====
println("\n=== Monad Style ===")

val mPack1 = MonadicPack(List(Some(fullBottle), None, Some(fullBottle)))
val mCrate1 = MonadicCrate(List(Some(mPack1)))

val count3 = MonadStyle.countFullBottles(mCrate1)
println(s"Full bottles: $count3")  // Should be 2

// Try a for-comprehension directly
val fullBottles = for
  pack <- mCrate1.presentPacks
  bottle <- pack.present
  if !bottle.empty
yield bottle

println(s"Collected full bottles: ${fullBottles.length}")

// ===== Monad Laws Check =====
println("\n=== Monad Laws ===")

val testBottle = fullBottle
val f = (b: Bottle) => MonadicPack(List(Some(b), Some(b)))

println(s"Left identity: ${MonadLaws.leftIdentity(testBottle, f)}")
println(s"Right identity: ${MonadLaws.rightIdentity(mPack1)}")

val g = (b: Bottle) => if b.empty then MonadicPack(List(None)) else MonadicPack(List(Some(b)))
val testPack = MonadicPack(List(Some(fullBottle), Some(emptyBottle)))
println(s"Associativity: ${MonadLaws.associativity(testPack, f, g)}")
