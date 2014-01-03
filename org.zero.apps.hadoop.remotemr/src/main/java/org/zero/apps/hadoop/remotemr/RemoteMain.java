package org.zero.apps.hadoop.remotemr;

import java.io.File;

public class RemoteMain {
	public static void main(String[] args) throws Exception {
		ClassLoader cl = MRLauncher.class.getClassLoader();
		String jarAbsolutePath = new File("target/remotemr-0.0.1-SNAPSHOT.jar").getAbsolutePath();

		MRLauncher mrl = new MRLauncher();
		args = new String[3];

		args[0] = jarAbsolutePath;
		args[1] = "/user/dooby/pg16807.txt";
		args[2] = "/user/dooby/testoutput-1";

		mrl.invokeMR(WordCount.class.getName(), args);
	}
}
