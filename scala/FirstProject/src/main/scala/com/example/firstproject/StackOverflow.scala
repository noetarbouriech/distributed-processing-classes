package com.example.firstproject

import org.apache.spark.sql.types.{IntegerType, StringType, StructType}
import org.apache.spark.sql.{Dataset, SparkSession, DataFrame}
import org.apache.spark.sql.functions.col

object StackOverflow {
  def main(args: Array[String]): Unit = {
    val csvDataFile = "data/stackoverflow.csv"

    val spark = SparkSession.builder
      .appName("Stackoverflow Application")
      .config("spark.driver.memory", "8G")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val schema = new StructType()
      .add("postTypeId", IntegerType, nullable = true)
      .add("id", IntegerType, nullable = true)
      .add("acceptedAnswer", StringType, nullable = true)  // Fixed typo in column name
      .add("parentId", IntegerType, nullable = true)
      .add("score", IntegerType, nullable = true)
      .add("tag", StringType, nullable = true)

    val df = spark.read
      .option("header", "false")
      .schema(schema)
      .csv(csvDataFile)
      .drop("acceptedAnswer")  // Consistent with the column name in schema

    println(s"\nCount of records in CSV file: ${df.count()}")
    df.printSchema()
    df.show(5)

    println(
      "\nCount tag null: " + df.filter(col("tag").isNull).count() +
        "\nCount parentId null: " + df.filter(col("parentId").isNull).count())

    // Filter posts with a score greater than 20 (consistent with the filter condition)
    val highScorePosts = df.filter(col("score") > 20)

    highScorePosts.show(5)

    // Register the DataFrame as a SQL temporary view
    df.createOrReplaceTempView("stackoverflow")

    // Query 1: Top 5 highest scores
    val top5Scores = spark.sql("SELECT id, score FROM stackoverflow ORDER BY score DESC LIMIT 5")
    top5Scores.show()

    val top5ScoresWithTag = spark.sql("""
        SELECT id, score, tag
        FROM stackoverflow
        WHERE tag IS NOT NULL
        ORDER BY score DESC
        LIMIT 5
      """)
    top5ScoresWithTag.show()

    // Query: Most frequently used tags
    val popularTags = spark.sql("""
      SELECT tag, COUNT(*) as frequency
      FROM stackoverflow
      WHERE tag IS NOT NULL
      GROUP BY tag
      ORDER BY frequency DESC
      LIMIT 10
    """)
    popularTags.show()





    spark.stop()  // Added to properly close the Spark session
  }
}

