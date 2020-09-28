package com.nazdaq.dbbackup.util;

import com.jcraft.jsch.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Exec {
	public static void main(String[] arg) {
		SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
		String now = sd.format(new Date());
		try {
			JSch jsch = new JSch();

			String host = null;
			if (arg.length > 0) {
				host = arg[0];
			} else {
				host = "nazdaq@203.83.178.230"; // enter username and ipaddress for
												// machine you need to connect
			}
			String user = host.substring(0, host.indexOf('@'));
			host = host.substring(host.indexOf('@') + 1);

			Session session = jsch.getSession(user, host, 22);

			// username and password will be given via UserInfo interface.
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();

			//String command = "grep 'INFO' filepath"; // enter any command you
														// need to execute
			
			String command = "/usr/bin/mysqldump " + "hrms" + " -h " + "203.83.178.230" + " -u "
					+ "admin" + " -p" + "passw0rd"+ " > /home/nazdaq/DATABASE/BACKUP/"+now+".sql";

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			channel.setInputStream(null);

			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("exit-status: "
							+ channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
	
	public String linuxMachineFileUpload(){
		return null;
	}

	public static class MyUserInfo implements UserInfo {
		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			str = "Yes";
			return true;
		}

		String passwd;

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			passwd = "nazdaq"; // enter the password for the machine you want
									// to connect.
			return true;
		}

		public void showMessage(String message) {

		}

	}
}
