package org.zero.apps.hadoop.remotemr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Tool;


public class WordCount extends Configured implements Tool {
 
    //Map Class.
    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
 
        static enum Counters {
 
            INPUT_WORDS
        }
 
        private final static IntWritable one = new IntWritable(1);
 
        private Text word = new Text();
 
        private boolean caseSensitive = true;
 
        private Set<String> patternsToSkip = new java.util.HashSet<String>();
 
        private long numRecords = 0;
 
        private String inputFile;
 
        public void configure(JobConf job) {
            caseSensitive = job.getBoolean("wordcount.case.sensitive", true);
            inputFile = job.get("map.input.file");
 
            if (job.getBoolean("wordcount.skip.patterns", false)) {
                Path[] patternsFiles = new Path[0];
                try {
                    patternsFiles = DistributedCache.getLocalCacheFiles(job);
                }
                catch (IOException ioe) {
                    System.err.println("Caught exception while getting cached files: " + StringUtils.stringifyException(ioe));
                }
                for (Path patternsFile : patternsFiles) {
                    parseSkipFile(patternsFile);
                }
            }
        }
 
        private void parseSkipFile(Path patternsFile) {
            try {
                BufferedReader fis = new BufferedReader(new FileReader(patternsFile.toString()));
                String pattern = null;
                while ((pattern = fis.readLine()) != null) {
                    patternsToSkip.add(pattern);
                }
            }
            catch (IOException ioe) {
                System.err.println("Caught exception while parsing the cached file '" + patternsFile + "' : "
                        + StringUtils.stringifyException(ioe));
            }
        }
 
        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            String line = (caseSensitive) ? value.toString() : value.toString().toLowerCase();
 
            for (String pattern : patternsToSkip) {
                line = line.replaceAll(pattern, "");
            }
 
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                word.set(tokenizer.nextToken());
                output.collect(word, one);
                reporter.incrCounter(Counters.INPUT_WORDS, 1);
            }
 
            if ((++numRecords % 100) == 0) {
                reporter.setStatus("Finished processing " + numRecords + " records " + "from the input file: " + inputFile);
            }
        }
    }
 
    //Reduce Class.
    public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
 
		@Override
		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
			int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            output.collect(key, new IntWritable(sum));
		}
    }
 
    //Run method.
    public int run(String[] args) throws Exception {
        JobConf conf = new JobConf(getConf(), WordCount.class);
        conf.setJobName(WordCount.class.getName());
 
        conf.setJarByClass(WordCount.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);
 
        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);
 
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
 
        // conf.setp
        conf.setJar(args[0]);
 
        FileInputFormat.setInputPaths(conf, new Path(args[1]));
        FileOutputFormat.setOutputPath(conf, new Path(args[2]));
 
        JobClient.runJob(conf);
        return 0;
    }
}