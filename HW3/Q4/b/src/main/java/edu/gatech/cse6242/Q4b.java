package edu.gatech.cse6242;
import java.util.StringTokenizer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;

public class Q4b {

  public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {
		private Text val_ = new Text();
		private Text key_ = new Text();
		public void map(Object key, Text value, Context context) throws InterruptedException, IOException {
      // StringTokenizer iter = new StringTokenizer(val_.toString());
      // String[] dataList = iter.nextToken().split("\t");
      String[] dataList = value.toString().split("\t");


      // while (iter.hasMoreTokens()) {
        
        if(dataList.length == 4){
          key_.set(dataList[2]);                // passenger count
          val_.set("1-" + dataList[3]);            // count and also recording fare
          context.write(key_ , val_);
        }
      // }
    }
  }
  
  public static class IntSumReducer extends Reducer<Text, Text, Text, Text>{
    private Text result = new Text();
    public void reduce(Text key, Iterable<Text> values, Context context) throws InterruptedException, IOException {
      double sum = 0;
      int count = 0;
			for(Text val : values){
        String vs = val.toString(); 
        String vList[] = vs.split("-", 2);
        count = count + Integer.parseInt(vList[0]);
				sum = sum + Double.parseDouble(vList[1]);
      }
      double avg = sum/count ;
      String average = Double.toString(avg);
      result.set(String.format("%,.2f", average));
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    /* TODO: Update variable below with your gtid */
    final String gtid = "mgupta313";

    Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Q4b");

		/* TODO: Needs to be implemented */
		// The first map reduce
		// finding out and in degree for every node
		job.setJarByClass(Q4b.class);
    job.setMapperClass(TokenizerMapper.class);
    // job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
