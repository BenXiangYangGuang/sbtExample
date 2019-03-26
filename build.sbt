name := "sbtExample"

version := "0.1"

scalaVersion := "2.12.6"
//注意sbt文件每行之间必须有空行。

val akkaVersion = "2.5.13"

libraryDependencies ++=Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.22",
  "io.spray" %% "spray-json" % "1.3.4",
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.13",
  "com.typesafe.akka" %% "akka-actor" % "2.5.13",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.22",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "0.20",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.14",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.13",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.6.0" //支持scala 和 java1.8之间的相互转化
)
