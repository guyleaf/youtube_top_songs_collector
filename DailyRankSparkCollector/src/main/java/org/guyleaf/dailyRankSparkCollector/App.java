package org.guyleaf.dailyRankSparkCollector;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;

import com.mongodb.spark.MongoSpark;

import java.util.Map;

public class App 
{
    public static void main( String[] args )
    {
        Map<String, String> env = System.getenv();
        SparkSession spark = SparkSession.builder()
                .master(env.get("SPARK_URL"))
                .appName("DailyRankSparkCollector")
                .config("spark.mongodb.input.uri", env.get("DB_INPUT_URI"))
                .config("spark.mongodb.output.uri", env.get("DB_OUTPUT_URI"))
                .getOrCreate();

        // Create a JavaSparkContext using the SparkSession's SparkContext object
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        Dataset<HourlyRank> ds = MongoSpark.load(jsc).toDS(HourlyRank.class);
        ds.printSchema();
        ds.show();

        jsc.close();
    }
}
