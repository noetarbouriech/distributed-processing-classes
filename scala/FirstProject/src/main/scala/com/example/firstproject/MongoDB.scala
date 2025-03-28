package com.example.firstproject

import org.apache.spark.sql.SparkSession

object MongoDB {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local")
      .appName("MongoSparkConnectorIntro")
      // Unified connection URI (for both read and write)
      .config("spark.mongodb.connection.uri",
        "mongodb://my-user:qwertyuiop@localhost:27017/test.myCollection?" +
          "authSource=admin&authMechanism=SCRAM-SHA-256&replicaSet=example-mongodb")
      // Explicit database and collection configuration
      .config("spark.mongodb.database", "test")
      .config("spark.mongodb.collection", "myCollection")
      // Additional recommended settings
      .config("spark.mongodb.write.retryWrites", "true")
      .config("spark.mongodb.read.retryReads", "true")
      .getOrCreate()

    import spark.implicits._

    val df = Seq(
      ("cat", 30),
      ("dog", 20),
      ("fish", 10),
      ("elephant", 5)
    ).toDF("word", "count")

    // Write with explicit options
    df.write
      .format("mongodb")
      .mode("append")
      .option("database", "test")
      .option("collection", "myCollection")
      .save()

    // Read with explicit options
    val readDf = spark.read
      .format("mongodb")
      .option("database", "test")
      .option("collection", "myCollection")
      .load()

    readDf.show()
    spark.stop()
  }
}