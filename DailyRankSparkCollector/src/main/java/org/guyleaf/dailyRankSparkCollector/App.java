package org.guyleaf.dailyRankSparkCollector;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.mongodb.spark.MongoSpark;
import org.apache.spark.sql.expressions.WindowSpec;
import org.apache.spark.sql.expressions.Window;

import static org.apache.spark.sql.functions.*;

public class App {
    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("It should have categoryId & currentDate as arguments");
        }

        String categoryId = args[0];
        String currentDate = args[1];

        System.out.println("#################################### Input Info ####################################");
        System.out.printf("id: %s, date: %s\n", categoryId, currentDate);
        System.out.println("######################################## End #######################################");

        // Map<String, String> env = System.getenv();
        SparkSession spark = SparkSession.builder().master("spark://spark-master:7077")
                .appName("DailyRankSparkCollector")
                .config("spark.mongodb.input.uri",
                        "mongodb://api_web:cloudyoutube@mongodb:27017/youtube.hourlyRank?authSource=youtube")
                .config("spark.mongodb.output.uri",
                        "mongodb://api_web:cloudyoutube@mongodb:27017/youtube.dailyRank?authSource=youtube")
                .config("spark.driver.memory", "2g").config("spark.scheduler.mode", "FAIR").getOrCreate();

        spark.sparkContext().setLocalProperty("spark.scheduler.pool", "crawler");

        // Create a JavaSparkContext using the SparkSession's SparkContext object
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        Dataset<Row> ds = MongoSpark.load(jsc).toDF();

        Dataset<Row> flattened = ds
                .filter(ds.col("categoryId").equalTo(categoryId).and(ds.col("collectedDate").equalTo(currentDate)))
                .select(ds.col("collectedHour"),
                        posexplode(ds.col("videoRanks")).as(new String[] { "rank", "videoId" }));

        // flattened.show();

        Dataset<Row> statistics = flattened.groupBy(flattened.col("videoId"), flattened.col("rank")).count().cache();

        // statistics.show();

        WindowSpec first_spec = Window.partitionBy("rank").orderBy(desc("count"));

        WindowSpec second_spec = Window.partitionBy("videoId");

        Dataset<Row> highestCountForEachRank = statistics.withColumn("order", dense_rank().over(first_spec));

        Dataset<Row> highestRankForEachVideo = statistics.withColumn("highestRank", min("rank").over(second_spec))
                .filter("rank = highestRank").drop("rank").withColumnRenamed("count", "highestRankCount");

        Dataset<Row> totalCountForEachVideo = statistics.groupBy("videoId").agg(sum("count").as("totalCount"));

        Dataset<Row> table = highestCountForEachRank.join(highestRankForEachVideo, "videoId")
                .join(totalCountForEachVideo, "videoId").cache();

        Dataset<Row> rank = table.filter("order = 1").orderBy("rank").coalesce(1).dropDuplicates("videoId");

        Dataset<Row> diff = table.join(rank, table.col("rank").equalTo(rank.col("rank")), "leftanti").orderBy("rank");

        rank = rank.select("videoId", "rank", "count");

        // keep consecutive rank part for each loop
        if (!diff.isEmpty()) {

            WindowSpec rule = Window.partitionBy("rank").orderBy(desc("count"), asc("highestRank"),
                    desc("highestRankCount"), desc("totalCount"));

            Dataset<Row> result;
            Row first;
            while (!diff.isEmpty()) {
                first = diff.first();
                rank = rank.filter(rank.col("rank").lt(first.getInt(1)));

                // delete duplicated videoId and rank which is already in rank dataset
                table = table.join(rank, rank.col("videoId").equalTo(table.col("videoId"))
                        .or(rank.col("rank").equalTo(table.col("rank"))), "leftanti").drop("order");
                table = table.withColumn("order", dense_rank().over(rule));

                result = table.filter("order = 1").orderBy("rank").coalesce(1).dropDuplicates("videoId").drop("order");

                first = result.first();
                // rearrange rank
                result = result
                        .withColumn("new_rank", row_number().over(Window.orderBy("rank")).plus(first.getInt(1) - 1))
                        .drop("rank").withColumnRenamed("new_rank", "rank").cache();
                rank = rank.union(result.select(result.col("videoId"), result.col("rank"), result.col("count")))
                        .cache();

                diff = table.join(result, result.col("videoId").equalTo(table.col("videoId"))
                        .or(result.col("rank").equalTo(table.col("rank"))), "leftanti");
            }
        }

        rank = rank.withColumn("categoryId", lit(categoryId)).withColumn("collectedDate", lit(currentDate)).cache();

        rank = rank.withColumn("videoRanks",
                collect_list("videoId").over(Window.partitionBy("categoryId").orderBy("rank")));

        rank.createOrReplaceTempView("rankV");
        rank = spark.sql("SELECT categoryId, collectedDate, videoRanks FROM rankV ORDER BY rank DESC limit 1");

        MongoSpark.save(rank);

        jsc.close();
    }
}
