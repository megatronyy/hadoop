package quwb.hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Sort {
	//ÿ�м�¼��һ����������Text�ı�ת��ΪIntWritable���ͣ���Ϊmap��key
	public static class Map extends Mapper<Object, Text, IntWritable, IntWritable>{
		private static IntWritable data = new IntWritable();
		
		//ʵ��map����
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			String line = value.toString();
			data.set(Integer.parseInt(line));
			context.write(data, new IntWritable(1));
		}
	}
	
	//reduce֮ǰhadoop��ܻ����shuffle���������ֱ�����key���ɡ�
	public static class Reduce extends Reducer<IntWritable, IntWritable, IntWritable, Text>{
		//ʵ��reduce����
		public void reduce(IntWritable key, Iterable<IntWritable> values, Context context)
			throws IOException,  InterruptedException{
			for(IntWritable v : values){
				context.write(key, new Text(""));
			}
		}
	}
	
	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		//ָ��JobTracker��ַ
		conf.set("mapred.job.tracker", "Master.Hadoop:9001");
		if(args.length != 2){
			System.err.println("Usage: Data Sort <in> <out>");
			System.exit(2);
		}
		
		System.out.println(args[0]);
		System.out.println(args[1]);
		
		Job job = Job.getInstance(conf, "Data Sort");
		job.setJarByClass(Sort.class);
		
		//����Map��Reduce������
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		
		//�����������
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(IntWritable.class);
		
		//������������Ŀ¼
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}
