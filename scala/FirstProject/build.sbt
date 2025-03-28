// build.sbt
ThisBuild / version := "1.0.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.18"
ThisBuild / organization := "com.example.firstproject"

lazy val root = (project in file("."))
  .settings(
    name := "FirstProject",

    // Main class settings (modern syntax)

    // Assembly plugin settings
    assembly / assemblyJarName := "app.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs_*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )

val sparkVersion = "3.5.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion ,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion ,
  "org.neo4j" %% "neo4j-connector-apache-spark" % "5.3.1_for_spark_3",
  "org.mongodb.spark" %% "mongo-spark-connector" % "10.4.1",
  "org.apache.hadoop" % "hadoop-aws" % "3.3.4",  // For S3/S3A support
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.0"
)