// Databricks notebook source
// MAGIC %md
// MAGIC 
// MAGIC ## Overview
// MAGIC 
// MAGIC This notebook will show you how to create and query a table or DataFrame that you uploaded to DBFS. [DBFS](https://docs.databricks.com/user-guide/dbfs-databricks-file-system.html) is a Databricks File System that allows you to store data for querying inside of Databricks. This notebook assumes that you have a file already inside of DBFS that you would like to read from.
// MAGIC 
// MAGIC This notebook is written in **Python** so the default cell type is Python. However, you can use different languages by using the `%LANGUAGE` syntax. Python, Scala, SQL, and R are all supported.

// COMMAND ----------


// # File location and type
var file_location = "/FileStore/tables/nyc_tripdata-069c2.csv"
var file_type = "csv"

// # CSV options
var infer_schema = "false"
var first_row_is_header = "false"
var delimiter = ","

// # The applied options are for CSV files. For other file types, these will be ignored.
var df = spark.read.format(file_type).option("inferSchema", infer_schema).option("header", first_row_is_header).option("sep", delimiter).load(file_location)

display(df)

// COMMAND ----------


// # Create a view or table

var temp_table_name = "mytable"

df.createOrReplaceTempView(temp_table_name)

// COMMAND ----------


var df2 = df.filter(($"_c2" !== $"_c3") && $"_c5" > 2.0)
display(df2)

// COMMAND ----------


val temp = df2.groupBy($"_c3").count().sort($"count".desc,$"_c3".asc).head(5)
display(temp)

// COMMAND ----------


val temp = df2.groupBy($"_c2").count().sort($"count".desc,$"_c2".asc).head(5)
display(temp)

// COMMAND ----------


val temp = df2.groupBy($"_c2",$"_c3").count().sort($"count".desc,$"_c2".asc).head(5)
display(temp)

// COMMAND ----------


import org.apache.spark.sql.functions.max
import org.apache.spark.sql.functions._
val temp1 =  df2.filter(
    to_date(col("_c1"),"mm/dd/yyyy").geq("2019-01-01") && to_date(col("_c1"),"mm/dd/yyyy").leq("2019-01-05")).groupBy($"_c1").count();

display(temp1)

// COMMAND ----------


val temp = df2.as("df2").join(df2.as("df2copy"),$"df2._c2"===$"df2copy._c3", "inner").groupBy("df2._c2").count().orderBy(desc("count"), asc("df2._c2")).head(3)
display(temp)

// COMMAND ----------

# With this registered as a temp view, it will only be available to this particular notebook. If you'd like other users to be able to query this table, you can also create a table from the DataFrame.
# Once saved, this table will persist across cluster restarts as well as allow various users across different notebooks to query this data.
# To do so, choose your table name and uncomment the bottom line.

permanent_table_name = "mytable"

# df.write.format("parquet").saveAsTable(permanent_table_name)
