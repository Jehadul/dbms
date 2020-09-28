package com.nazdaq.dbbackup.util;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestMain {

	private static String ip = "203.83.178.230";
	private static String port = "3306";
	private static String database = "hrms";
	private static String user = "admin";
	private static String pass = "passw0rd";
	//private static String path = "/home/nazdaq/DATABASE/";
	private static String path = "D:\\";

	public static void export() {
		Date dateNow = new Date();
		SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat("yyyyMMdd");
		String date_to_string = dateformatyyyyMMdd.format(dateNow);
		String ss = "test_backup.sql";
		String fullName = path + " " + date_to_string + " " + ss;
		String dumpCommand = "/usr/bin/mysqldump " + database + " -h " + ip + " -u "
				+ user + " -p" + pass;
		Runtime rt = Runtime.getRuntime();
		File test = new File(fullName);
		PrintStream ps;
		try {
			//Process child = rt.exec(dumpCommand);
			//Process child = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", dumpCommand });
			Process child = Runtime.getRuntime().exec(new String[] { "ssh", "nazdaq@203.83.178.230", "df -h" });
			ps = new PrintStream(test);
			InputStream in = child.getInputStream();
			int ch;
			while ((ch = in.read()) != -1) {
				ps.write(ch);
				System.out.write(ch); //to view it by console
			}

			InputStream err = child.getErrorStream();
			while ((ch = err.read()) != -1) {
				System.out.write(ch);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		export();
	}

}
