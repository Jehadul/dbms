package com.nazdaq.dbbackup.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class MySqlLinuxBackup {
	/*public static void main(String[] arg) throws IOException {
		Date dateNow = new Date();
		SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");
		String date_to_string = dateformatyyyyMMdd.format(dateNow);
		String filePath = "/home/nazdaq/temp/203.83.178.230_hrms_28_09_2017_01_24_39";
		
		String userHost = "nazdaq@203.83.178.230";
		String password = "nazdaq";
		Integer linPort = 22;
		
		String command = "rm "+filePath;
		MySqlLinuxBackup m = new MySqlLinuxBackup();
		m.linuxMachineFileDelete(password, userHost, linPort, command);
		

		
	}*/
	
	public boolean fileWriteToLocal(String filePath, String fileName, String writeValue){
		boolean res = false;
			try{
			
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath+fileName));
			out.write( writeValue );
			out.close();
			res = true;
		}catch(IOException e){
			res = false;
			e.printStackTrace();
		}
			
		return res;
	}
	
	
	
	public boolean linuxMachineFileUpload(String linPassword, String userHost, Integer linPort, String command){
		boolean result = false;
		try {
			JSch jsch = new JSch();
			String user = userHost.substring(0, userHost.indexOf('@'));
			userHost = userHost.substring(userHost.indexOf('@') + 1);
			Session session = jsch.getSession(user, userHost, linPort);
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.setPassword(linPassword);
			session.connect();
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
			result = true;
		} catch (Exception e) {
			result = false;
			System.out.println(e);
		}
		
		return result;
	}	
	
	public String readFileFromLinux(String str_Host, String str_Username, String str_Password, int int_Port, String str_FileDirectory, String str_FileName)
	  {
	    JSch obj_JSch = new JSch();
	    Session obj_Session = null;
	    StringBuilder obj_StringBuilder = new StringBuilder();
	    try
	    {
	      obj_Session = obj_JSch.getSession(str_Username, str_Host);
	      obj_Session.setPort(int_Port);
	      obj_Session.setPassword(str_Password);
	      Properties obj_Properties = new Properties();
	      obj_Properties.put("StrictHostKeyChecking", "no");
	      obj_Session.setConfig(obj_Properties);
	      obj_Session.connect();
	      Channel obj_Channel = obj_Session.openChannel("sftp");
	      obj_Channel.connect();
	      ChannelSftp obj_SFTPChannel = (ChannelSftp) obj_Channel;
	      obj_SFTPChannel.cd(str_FileDirectory);
	      InputStream obj_InputStream = obj_SFTPChannel.get(str_FileName);
	      char[] ch_Buffer = new char[0x10000];
	      Reader obj_Reader = new InputStreamReader(obj_InputStream, "UTF-8");
	      int int_Line = 0;
	      do
	      {
	        int_Line = obj_Reader.read(ch_Buffer, 0, ch_Buffer.length);
	        if (int_Line > 0)
	        { obj_StringBuilder.append(ch_Buffer, 0, int_Line);}
	      }
	      while (int_Line >= 0);
	      obj_Reader.close();
	      obj_InputStream.close();
	      obj_SFTPChannel.exit();
	      obj_Channel.disconnect();
	      obj_Session.disconnect();
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	    return obj_StringBuilder.toString();
	}
	
	public boolean linuxMachineFileDelete(String linUserName, String linPassword, String host, Integer linPort, String filePathWithName){
		boolean result = false;
		String userHost = linUserName+"@"+host;
		String command = "rm "+ filePathWithName;
		try {
			JSch jsch = new JSch();
			String user = userHost.substring(0, userHost.indexOf('@'));
			userHost = userHost.substring(userHost.indexOf('@') + 1);
			Session session = jsch.getSession(user, userHost, linPort);
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.setPassword(linPassword);
			session.connect();
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
			result = true;
		} catch (Exception e) {
			result = false;
			System.out.println(e);
		}
		
		return result;
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
			passwd = message; // enter the password for the machine you want
									// to connect.
			return true;
		}

		public void showMessage(String message) {

		}

	}
}
