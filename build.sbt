ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

resolvers ++= List("Confluent" at "https://packages.confluent.io/maven/")

lazy val root = (project in file("."))
  .settings(
    name := "stbavropoc",
    idePackagePrefix := Some("com.eventloopsoftware"),
  )
  .settings(libraryDependencies ++= List(
    "dev.zio" %% "zio" % "2.0.21",
    "dev.zio" %% "zio-kafka" % "2.7.2",
    "io.confluent" % "kafka-avro-serializer" % "7.5.2",
    //    "org.apache.kafka" % "kafka-clients" % "3.6.1",
    ("org.apache.kafka" %% "kafka" % "3.6.1").cross(CrossVersion.for3Use2_13),
    "org.apache.avro" % "avro" % "1.11.3"

  ))
  .settings(excludeDependencies += "org.scala-lang.modules" % "scala-collection-compat_2.13")
  .settings(avroStringType := "String")
  .settings(Compile / avroSource := (Compile / resourceDirectory).value / "avro")