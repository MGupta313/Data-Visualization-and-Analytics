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

public class Q4a {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		private IntWritable w0 = new IntWritable(-1);
		private IntWritable w1 = new IntWritable(1);
		private Text node1 = new Text();
		private Text node2 = new Text();
		public void map(Object key, Text value, Context context) throws InterruptedException, IOException {
			String dataLine = value.toString();	
			String [] lines = dataLine.split("\t");
			if(lines.length == 4){
				node1.set(lines[0]);	// pick up = -1
				node2.set(lines[1]);	// drop off = 1
				context.write(node1, w0);	// pick up = -1
				context.write(node2, w1);	// drop off = 1
			}
		}
	}

	public static class DegreeMapper extends Mapper<Object, Text, IntWritable, IntWritable>{
		private IntWritable count = new IntWritable(1);
		private IntWritable degree = new IntWritable();
		public void map(Object key, Text value, Context context) throws InterruptedException, IOException {
			String lineVal = value.toString();
			String [] lines = lineVal.split("\t");
			if(lines.length == 2){
				degree.set(Integer.parseInt(lines[1]));
				context.write(degree, count);
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, Text>{ 		
		// private IntWritable result = new IntWritable(); 		
		private Text final_val = new Text(); 		
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws InterruptedException, IOException { 			
			int sum = 0; 			
			for(IntWritable value : values){ 				
				sum = value.get() + sum; 			
			} 			
			String sumString = String.valueOf(sum); 			
			final_val.set(sumString); 			
			context.write(key, final_val); 		
		} 	
	}

	public static void main(String[] args) throws Exception {

		/* TODO: Update variable below with your gtid */
		final String gtid = "mgupta313";

		Configuration conf = new Configuration();
		Job j1 = Job.getInstance(conf, "Q4a_1");

		/* TODO: Needs to be implemented */
		// The first map reduce
		// finding out and in degree for every node
		j1.setJarByClass(Q4a.class);
		j1.setMapperClass(TokenizerMapper.class);
		// j1.setCombinerClass(IntSumReducer.class);
		j1.setReducerClass(IntSumReducer.class);
		j1.setOutputKeyClass(Text.class);
		j1.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(j1, new Path(args[0]));
		FileOutputFormat.setOutputPath(j1, new Path("temp"));
		j1.waitForCompletion(true);
		
		// now this is the 2nd map reduce
		// getting key = ( in - out ) and adding count to every key
		Job j2 = Job.getInstance(conf, "Q4a_2");
		j2.setJarByClass(Q4a.class);
		j2.setMapperClass(DegreeMapper.class);
		// j2.setCombinerClass(IntSumReducer.class);
		j2.setReducerClass(IntSumReducer.class);
		j2.setOutputKeyClass(Text.class);
		j2.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(j2, new Path("temp"));
		FileOutputFormat.setOutputPath(j2, new Path(args[1]));
		System.exit(j2.waitForCompletion(true) ? 0:1);
	}
}
