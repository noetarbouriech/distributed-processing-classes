package com.example.firstproject

import org.apache.spark.sql.{SaveMode, SparkSession}

object Final {
  def main(args: Array[String]): Unit = {
    val url = "bolt://54.167.167.42"
    val username = "neo4j"
    val password = "carriages-hatch-sparks"
    val dbname = "neo4j"

    val spark = SparkSession.builder
      .master("local[*]")
      .config("neo4j.url", url)
      .config("neo4j.authentication.basic.username", username)
      .config("neo4j.authentication.basic.password", password)
      .config("neo4j.database", dbname)
      .appName("Spark App")
      .getOrCreate()

    // List types of transactions
    val readQuery =
      """
      MATCH (:Client:FirstPartyFraudster)-[]-(txn:Transaction)-[]-(c:Client)
      WHERE NOT c:FirstPartyFraudster
      UNWIND labels(txn) AS transactionType
      RETURN transactionType, count(*) AS freq
      """

    val df = spark.read
      .format("org.neo4j.spark.DataSource")
      .option("query", readQuery)
      .load()

    df.show()

  }
}
