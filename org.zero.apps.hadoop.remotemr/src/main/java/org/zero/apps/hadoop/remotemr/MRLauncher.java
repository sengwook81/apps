package org.zero.apps.hadoop.remotemr;

import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;

public class MRLauncher {
 
    static final String FS_DEFAULT_NAME = "hdfs://name1:9000";
 
    static final String MAPRED_JOB_TRACKER = "hdfs://name1:9001";
 
    static final String HADOOP_USER = "dooby";
 
    public int invokeMR(String jobName, String[] args) throws Exception {
 
    	System.setProperty("HADOOP_USER_NAME", HADOOP_USER);
        int res = 1;
 
        if (WordCount.class.getName().equals(jobName)) {
        	System.out.println("Launch " + Arrays.deepToString(args));
            Configuration conf = new Configuration();
            conf.set("fs.default.name", FS_DEFAULT_NAME);
            conf.set("mapred.job.tracker", MAPRED_JOB_TRACKER);
            conf.set("hadoop.job.ugi", HADOOP_USER);
 
            
            FileSystem fileSystem = FileSystem.get(conf);
            Path outputPath = new Path(args[2]);
            if(fileSystem.exists(outputPath)) {
            	fileSystem.delete(outputPath, true);
            }
            res = ToolRunner.run(conf, new WordCount(), args);
        }
 
        return res;
    }
 
}