package edu.gatech.cse6242;

import java.lang.Object;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Q1 {

  final String gtid = "mgupta313";
  public static class TokenizerMapper extends Mapper<Object, Text, IntWritable, Text>{

    private IntWritable pickUpId = new IntWritable();
    private Text value = new Text();
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
		int count = 1;

      while (itr.hasMoreTokens()) {
        String row = itr.nextToken();
        String items[] = row.split(",");
		count += 1;

	if (items[0] != null && !items[0].isEmpty() && Double.parseDouble(items[2]) != 0 && Double.parseDouble(items[3]) > 0) {
	   pickUpId.set(Integer.parseInt(items[0]));
	   value.set("1" + "\t" + items[3]);
	   context.write(pickUpId, value);
	}

      }

    }

  }

  public static class IntSumReducer extends Reducer<IntWritable,Text,IntWritable,Text> {
    private Text result = new Text();
	private Text finKey = new Text();

    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      double sum = 0;
      int count = 0;
      for (Text val : values) {
	String vs[] = val.toString().split("\t", 2);
	count += Integer.parseInt(vs[0]);
        sum += Double.parseDouble(vs[1].replace("\t", "").replaceAll(",", ""));
      }
      String numtrips = Integer.toString(count);
      //String totalFare = Double.toString(sum);
      String totalFare = String.format("%,.2f", sum);
      String finalString = count + "," + totalFare;
      result.set(finalString);
      context.write(key, result);
    }

  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q1");

    job.setJarByClass(Q1.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(Text.class);


    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
