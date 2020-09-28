package com.nazdaq.dbbackup.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.nazdaq.dbbackup.model.DatabaseConnection;
import com.nazdaq.dbbackup.model.DbBackupHistory;
import com.nazdaq.dbbackup.service.BackupHistoryService;
import com.nazdaq.dbbackup.service.DbConnectionService;

@ComponentScan({ "com.nazdaq.dbbackup" })
@PropertySource("classpath:common.properties")
@Configuration
@EnableScheduling
public class AutoDbBackup {

	@Autowired
	DbConnectionService dbConnectionService;

	@Autowired
	BackupHistoryService backupHistoryService;

	@Value("${local.drive.location}")
	private String localDriveLocation;

	@Value("${mysql.db.location}")
	private String mysqlDbLocation;
	
	@Value("${mysql.db.location.windows}")
	private String mysqlDbLocationWin;

	@Value("${linux.temp.backup.location}")
	private String linuxTempBackupLocation;

	@Value("${dropbox.upload.location}")
	private String dropboxUploadLocation;

	@Value("${dropbox.access.token}")
	private String dropboxAccessToken;
	
	@Value("${temp.local.drive.location}")
	private String localDriveTempLocation;
	

	public static void main(String[] a) {
		AutoDbBackup adb = new AutoDbBackup();
		System.out.println(adb.localDriveLocation);
		System.out.println(adb.dropboxAccessToken);
	}

	public void runDbBackupSchedulerToDropbox() throws SQLException, Exception {
		List<DatabaseConnection> dbConList = dbConnectionService
				.listDatabaseConnections();
		Connection con = null;
		for (DatabaseConnection dc : dbConList) {			
			// database connectivity and get database name start
			String connectionURL = "jdbc:mysql://" + dc.getIpAddress() + ":"
					+ dc.getDbPort() + "";
			Class.forName("com.mysql.jdbc.Driver");
			try{
				con = DriverManager.getConnection(connectionURL, dc.getDbUserName(), dc.getDbPassword());
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(con == null){
				continue;
			}
					
			ResultSet rs = con.getMetaData().getCatalogs();
			List<String> dbList = new ArrayList<String>();
			while (rs.next()) {
				dbList.add(rs.getString("TABLE_CAT"));
			}
			// database connectivity and get database name end

			// database backup start
			MySqlBackup mySqlBackUp = new MySqlBackup();
			MySqlLinuxBackup myLinB = new MySqlLinuxBackup();
			DbBackupHistory dbBackupHistory;
			Date now = new Date();
			for (String dbname : dbList) {
				DropboxAPI dapi = new DropboxAPI();
				dbBackupHistory = new DbBackupHistory();
				String dmpData = "", fileName = "";
				boolean response = false;
				boolean resp = false;
				String loadValue = "";
				
				if (dc.getOperatingSystem().equals("1")) {
					
					// return string data from remote windows machine
					dmpData = mySqlBackUp.getServerDumpData(dc.getIpAddress(), dc.getDbPort(), 
							dc.getDbUserName(), dc.getDbPassword(), dbname, mysqlDbLocationWin);
					
					//file name generate
					if(dmpData != null && dmpData.length() > 0){
						fileName = mySqlBackUp.dmpFileNameGenerate(dc.getIpAddress(), dbname);
					}
					
					// file write to local temp location
					if(fileName != null && fileName.length() > 0){
						resp = myLinB.fileWriteToLocal(localDriveTempLocation, fileName, dmpData);
					}
					
					// file write to dropbox from local dirve location
					if(resp){	
						response = dapi.uploadDropbox(dropboxAccessToken, localDriveTempLocation, fileName, dropboxUploadLocation, dbname, dc.getIpAddress());
					}
					
					// information save to db
					if (response) {
						
						dbBackupHistory.setLocation(dropboxUploadLocation
								+ dc.getIpAddress() + "/" + dbname + "/");
						dbBackupHistory.setActive(true);
						dbBackupHistory.setBackupType("Automatic");
						dbBackupHistory.setDbName(dbname);
						dbBackupHistory.setCreatedDate(now);
						dbBackupHistory.setIpAddress(dc.getIpAddress());
						dbBackupHistory.setCreatedBy("System Robot");
						dbBackupHistory.setFileName(fileName);
						dbBackupHistory.setStatus("Completed");
						dbBackupHistory.setOperatingSystem(dc.getOperatingSystem());
						backupHistoryService.addDbBackupHistory(dbBackupHistory);
						
						try {
	
							File file = new File(localDriveTempLocation + fileName);
							if (file.delete()) {
								System.out.println(file.getName() + " is deleted!");
							} else {
								System.out.println("Delete operation is failed.");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
				
				if (dc.getOperatingSystem().equals("2")) {
					
					// file write to remote linux machine for temp
					fileName = mySqlBackUp.backupDataWithDatabaseLinux(
							mysqlDbLocation, dc.getIpAddress(), dc.getDbPort(),
							dc.getDbUserName(), dc.getDbPassword(), dbname,
							linuxTempBackupLocation, dc.getLinPassword(), dc.getLinUserName(), dc.getLinPort());
					
					// file read from linux temp backup location
					if(fileName != null && fileName.length() > 0){
						loadValue = myLinB.readFileFromLinux(dc.getIpAddress(), dc.getLinUserName(), dc.getLinPassword(), 
						dc.getLinPort(), linuxTempBackupLocation, fileName);
					}
					
					// file write to local temp location
					if(loadValue != null && loadValue.length() > 0){
						resp = myLinB.fileWriteToLocal(localDriveTempLocation, fileName, loadValue);
					}
					
					
					
					// file write to dropbox from local dirve location
					if(resp){	
						myLinB.linuxMachineFileDelete(dc.getLinUserName(), dc.getLinPassword(), dc.getIpAddress(), dc.getLinPort(), linuxTempBackupLocation+fileName);
						response = dapi.uploadDropbox(dropboxAccessToken, localDriveTempLocation, fileName, dropboxUploadLocation, dbname, dc.getIpAddress());
					}
					
					// information save to db
					if (response) {
						
						dbBackupHistory.setLocation(dropboxUploadLocation
								+ dc.getIpAddress() + "/" + dbname + "/");
						dbBackupHistory.setActive(true);
						dbBackupHistory.setBackupType("Automatic");
						dbBackupHistory.setDbName(dbname);
						dbBackupHistory.setCreatedDate(now);
						dbBackupHistory.setIpAddress(dc.getIpAddress());
						dbBackupHistory.setCreatedBy("System Robot");
						dbBackupHistory.setFileName(fileName);
						dbBackupHistory.setStatus("Completed");
						dbBackupHistory.setOperatingSystem(dc.getOperatingSystem());
						backupHistoryService.addDbBackupHistory(dbBackupHistory);
						
						try {
	
							File file = new File(localDriveTempLocation + fileName);
							if (file.delete()) {
								System.out.println(file.getName() + " is deleted!");
							} else {
								System.out.println("Delete operation is failed.");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
			}
			// database backup end

		}
	}

}
