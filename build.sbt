scalaVersion := "3.6.2" // or use the latest stable version
name := "Numbers"
version := "0.1.0-SNAPSHOT"

// Add project dependencies here
libraryDependencies += "org.typelevel" %% "spire" % "0.18.0"
libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test
libraryDependencies += "org.scalatestplus" %% "scalacheck-1-18" % "3.2.19.0" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.18.1" % Test
