package com.example.firstproject

import org.apache.spark.sql.{SparkSession, Dataset}
import org.apache.spark.sql.functions._

object UserTest {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("UsersTest")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val usersDF = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("http://minio.minio-dev.svc.cluster.local:9090/my-app-bucket/users.csv")

    val result = usersDF
      .filter($"age" >= 25)
      .groupBy("city")
      .agg(collect_list("name").as("names"))
      .select("city", "names")

    val resultCollected = result.collect()
    resultCollected.foreach { row =>
      val city = row.getAs[String]("city")
      val names = row.getAs[Seq[String]]("names")
      println(s"Users in $city: ${names.mkString(", ")}")
    }
    spark.stop()
  }
}
