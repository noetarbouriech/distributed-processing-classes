package com.example.firstproject

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types._

object FileStreaming {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("FileStreamingExample")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val schema = new StructType()
      .add("id", StringType)
      .add("name", StringType)
      .add("age", IntegerType)
      .add("city", StringType)

    val streamingDF = spark.readStream
      .option("header", "false")
      .schema(schema)
      .csv("input/")

    val consoleQuery = streamingDF.writeStream
      .format("console")
      .outputMode("append") // each new file’s rows will appear in the console
      .start()

    val csvQuery = streamingDF.writeStream
      .format("csv")
      .outputMode("append")
      .option("path", "output/") // <--- Where final CSV files will be saved
      .option("checkpointLocation", "chkpoint/") // <--- Directory for checkpointing
      .start()

    spark.streams.awaitAnyTermination()

  }
}