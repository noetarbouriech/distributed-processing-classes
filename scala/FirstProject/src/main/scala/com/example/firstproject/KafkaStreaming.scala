package com.example.firstproject

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.streaming.Trigger

object KafkaStreaming {

  def main(args: Array[String]): Unit = {
    // Initialize Spark Session
    val spark = SparkSession.builder()
      .appName("KafkaStreamingExample")
      .master("local[*]") // Remove this in production
      .getOrCreate()

    import spark.implicits._

    // Set log level to avoid excessive info messages
    spark.sparkContext.setLogLevel("WARN")

    // Kafka configuration
    val topic = "test" // Replace with your Kafka topic

    // Define schema for the incoming data (adjust according to your data format)
    val schema = StructType(Seq(
      StructField("id", StringType),
      StructField("timestamp", TimestampType),
      StructField("value", DoubleType),
      StructField("category", StringType)
    ))

    // Read from Kafka
    val kafkaStream = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("kafka.client.dns.lookup", "use_all_dns_ips") // ← Key solution
      .option("subscribe", topic)
      .load()

    // Process the stream
    val processedStream = kafkaStream
      .selectExpr("CAST(value AS STRING)") // Convert binary Kafka value to string
      .select(from_json($"value", schema).as("data")) // Parse JSON with schema
      .select("data.*") // Flatten the structure
      .filter($"value" > 0) // Example filter
      .withWatermark("timestamp", "10 minutes") // Define watermark for late data
      .groupBy(
        window($"timestamp", "5 minutes"), // Create 5-minute windows
        $"category"
      )
      .agg(
        avg("value").as("avg_value"),
        count("*").as("count")
      )

    // Output the processed stream (choose one sink)
    val query = processedStream.writeStream
      .outputMode("update") // or "complete" depending on your aggregation
      .format("console") // For testing - replace with Kafka, file, etc. in production
      .option("truncate", "false")
      .trigger(Trigger.ProcessingTime("1 minute")) // Adjust as needed
      .start()

    // Wait for the stream to finish
    query.awaitTermination()
  }
}