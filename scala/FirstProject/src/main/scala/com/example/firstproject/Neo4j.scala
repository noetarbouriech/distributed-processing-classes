package com.example.firstproject

import org.apache.spark.sql.{SaveMode, SparkSession}

object Neo4j {
  def main(args: Array[String]): Unit = {
    // Replace with the actual connection URI and credentials
    val url = "neo4j://localhost:7687"
    val username = "neo4j"
    val password = "qwertyuiop"
    val dbname = "neo4j"

    val spark = SparkSession.builder
      .master("local[*]")
      .config("neo4j.url", url)
      .config("neo4j.authentication.basic.username", username)
      .config("neo4j.authentication.basic.password", password)
      .config("neo4j.database", dbname)
      .appName("Spark App")
      .getOrCreate()

    // Read from Neo4j
    val readQuery =

      """
      MATCH (n)
      RETURN COUNT(n)
      """

    val df = spark.read
      .format("org.neo4j.spark.DataSource")
      .option("query", readQuery)
      .load()

    df.show()

  }
}