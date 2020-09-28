package com.nazdaq.dbbackup;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nazdaq.dbbackup.model.DatabaseConnection;
import com.nazdaq.dbbackup.model.DbBackupHistory;
import com.nazdaq.dbbackup.service.BackupHistoryService;
import com.nazdaq.dbbackup.service.DbConnectionService;
import com.nazdaq.dbbackup.util.DropboxAPI;
import com.nazdaq.dbbackup.util.MySqlBackup;
import com.nazdaq.dbbackup.util.MySqlLinuxBackup;


@Controller
@PropertySource("classpath:common.properties")
public class DatabaseBackupController {
	
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
	
	
	
	
	@RequestMapping(value = "/addDBConnection", method = RequestMethod.GET)
	public ModelAndView getDBConnectionForm (@ModelAttribute("command") DatabaseConnection databaseConnection, 
			BindingResult result){
		Map <String, Object> model = new HashMap<String, Object>();
		return new ModelAndView("addDBConnection", model);
	}
	
	@RequestMapping(value = "/manualDbConList", method = RequestMethod.GET)
	public ModelAndView getManualBackup (){
		Map <String, Object> model = new HashMap<String, Object>();
		List<DatabaseConnection> dbConList = dbConnectionService.listDatabaseConnections();
		model.put("dbConList", dbConList);
		return new ModelAndView("manualDbConList", model);
	}
	
	@RequestMapping(value = "/backupHistory", method = RequestMethod.GET)
	public ModelAndView backupHistoryList (){
		Map <String, Object> model = new HashMap<String, Object>();		
		//final File folder = new File(localDriveLocation);
		//List<String>  fileNames = listFilesForFolder(folder);
		//model.put("fileNames", fileNames);
		Date endDate = new Date();
		Date startDate = new DateTime(endDate).minusDays(30).toDate();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fromDate = sdf.format(startDate);
		String toDate = sdf.format(endDate);
		
		List<DbBackupHistory> dbBackupHistoryList = backupHistoryService.getDbBackupHistoryListByDateRange(fromDate, toDate);
		model.put("backupHistory", dbBackupHistoryList);
		//model.put("backupHistory", backupHistoryService.listDbBackupHistory());
		return new ModelAndView("backupHistory", model);
	}
	
	@RequestMapping(value = "/saveDbConnect", method = RequestMethod.POST)
	public ModelAndView saveDbConnect (@ModelAttribute("command") DatabaseConnection databaseConnection, 
			BindingResult result) throws Exception{
		Map <String, Object> model = new HashMap<String, Object>();
		Connection con = null;
		
		if(databaseConnection.getIpAddress().equals("")
				|| databaseConnection.getDbPort().equals("")
				|| databaseConnection.getDbUserName().equals("")
				|| databaseConnection.getDbPassword().equals("")){
			
			return new ModelAndView("redirect:/addDBConnection", model);
		}
		
		DatabaseConnection dc = dbConnectionService.getDatabaseConnectionByIpAddress(databaseConnection.getIpAddress());
		
		if(dc.getId() == null){
			databaseConnection.setCreatedBy("System");
			databaseConnection.setCreatedDate(new Date());
			dbConnectionService.addDatabaseConnection(databaseConnection);
		}
		
		String  connectionURL = "jdbc:mysql://"+ databaseConnection.getIpAddress() +":"+databaseConnection.getDbPort()+"";
		
		Class.forName("com.mysql.jdbc.Driver");

		// change user and password as you need it
		try{
			con = DriverManager.getConnection (connectionURL, databaseConnection.getDbUserName(), databaseConnection.getDbPassword());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(con == null){
			return new ModelAndView("redirect:/addDBConnection");
		}
		
		ResultSet rs = con.getMetaData().getCatalogs();
		
		
		List<String> dbList = new ArrayList<String>();
		while (rs.next()) {
		    dbList.add(rs.getString("TABLE_CAT"));
		}
		model.put("dbList", dbList);
		model.put("databaseConnection", databaseConnection);
		
		return new ModelAndView("manualDbBackupList", model);
	}
	
	
	@RequestMapping(value = "/dbConnect", method = RequestMethod.GET)
	public ModelAndView dbConnect (DatabaseConnection databaseConnection) throws Exception{
		Map <String, Object> model = new HashMap<String, Object>();
		DatabaseConnection dc = dbConnectionService.getDatabaseConnection(databaseConnection.getId());
		Connection con = null;
		if(dc.getId() != null){
			String  connectionURL = "jdbc:mysql://"+ dc.getIpAddress() +":"+dc.getDbPort()+"";
			
			Class.forName("com.mysql.jdbc.Driver");

			// change user and password as you need it
			try{
				con = DriverManager.getConnection (connectionURL, dc.getDbUserName(), dc.getDbPassword());
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(con == null){
				return new ModelAndView("redirect:/manualDbConList");
			}
			

			ResultSet rs = con.getMetaData().getCatalogs();
			
			
			List<String> dbList = new ArrayList<String>();
			while (rs.next()) {
			    dbList.add(rs.getString("TABLE_CAT"));
			}
			model.put("dbList", dbList);
			model.put("databaseConnection", dc);
			
			return new ModelAndView("manualDbBackupList", model);
		}
		
		return new ModelAndView("redirect:/manualDbConList", model);
	}
	
	
	@RequestMapping(value = "/backupDatabse", method = RequestMethod.POST)
	public ModelAndView backupDatabse (@ModelAttribute("command") DatabaseConnection dc, 
			BindingResult result) throws Exception{
		Map <String, Object> model = new HashMap<String, Object>();
		DatabaseConnection dcon = dbConnectionService.getDatabaseConnection(dc.getId());
		
		MySqlBackup mySqlBackUp = new MySqlBackup();
		MySqlLinuxBackup myLinB = new MySqlLinuxBackup();
		DbBackupHistory dbBackupHistory;
		Date now = new Date();
		for(String dbname : dc.getDbNames()){
			if(dcon.getOperatingSystem().equals("1")){
				if(dc.getLocation().equals("ld")){
					dbBackupHistory = new DbBackupHistory();
					String dmpData = "", fileName = "";
					boolean response = false;
					// return string data from remote windows machine
					dmpData = mySqlBackUp.getServerDumpData(dcon.getIpAddress(), dcon.getDbPort(), 
							dcon.getDbUserName(), dcon.getDbPassword(), dbname, mysqlDbLocationWin);
					
					//file name generate
					if(dmpData != null && dmpData.length() > 0){
						fileName = mySqlBackUp.dmpFileNameGenerate(dcon.getIpAddress(), dbname);
					}					
										
					// file write to local drive
					if(fileName != null && fileName.length() > 0){						
						response = myLinB.fileWriteToLocal(localDriveLocation, fileName, dmpData);							
					}		
					
					// information save to db
					if(response){
							dbBackupHistory.setLocation(localDriveLocation);
							dbBackupHistory.setActive(true);
							dbBackupHistory.setBackupType("Manual");
							dbBackupHistory.setDbName(dbname);
							dbBackupHistory.setCreatedDate(now);
							dbBackupHistory.setIpAddress(dcon.getIpAddress());
							dbBackupHistory.setCreatedBy("Admin");
							dbBackupHistory.setFileName(fileName);
							dbBackupHistory.setStatus("Completed");
							dbBackupHistory.setOperatingSystem(dcon.getOperatingSystem());				
							backupHistoryService.addDbBackupHistory(dbBackupHistory);
					}					
					
				} else if(dc.getLocation().equals("gd")){
					
				} else {
					dbBackupHistory = new DbBackupHistory();
					DropboxAPI dapi = new DropboxAPI();
					boolean response = false;
					boolean resp = false;
					String dmpData = "", fileName = "";
					
					// return string data from remote windows machine
					dmpData = mySqlBackUp.getServerDumpData(dcon.getIpAddress(), dcon.getDbPort(), 
							dcon.getDbUserName(), dcon.getDbPassword(), dbname, mysqlDbLocationWin);
					
					//file name generate
					if(dmpData != null && dmpData.length() > 0){
						fileName = mySqlBackUp.dmpFileNameGenerate(dcon.getIpAddress(), dbname);
					}
					
					// file write to local temp location
					if(fileName != null && fileName.length() > 0){
						resp = myLinB.fileWriteToLocal(localDriveTempLocation, fileName, dmpData);
					}
					
					// file write to dropbox from local dirve location
					if(resp){						
						response = dapi.uploadDropbox(dropboxAccessToken, localDriveTempLocation, fileName, dropboxUploadLocation, dbname, dcon.getIpAddress());
					}										
					
					// information save to db
					if(response){						
						dbBackupHistory.setLocation(dropboxUploadLocation+dcon.getIpAddress()+"/"+dbname+"/");
						dbBackupHistory.setActive(true);
						dbBackupHistory.setBackupType("Manual");
						dbBackupHistory.setDbName(dbname);
						dbBackupHistory.setCreatedDate(now);
						dbBackupHistory.setIpAddress(dcon.getIpAddress());
						dbBackupHistory.setCreatedBy("Admin");
						dbBackupHistory.setFileName(fileName);
						dbBackupHistory.setStatus("Completed");
						dbBackupHistory.setOperatingSystem(dcon.getOperatingSystem());				
						backupHistoryService.addDbBackupHistory(dbBackupHistory);
						
						try{
	
				    		File file = new File(localDriveTempLocation+fileName);
				    		if(file.delete()){
				    			System.out.println(file.getName() + " is deleted!");
				    		}else{
				    			System.out.println("Delete operation is failed.");
				    		}
				    	}catch(Exception e){
				    		e.printStackTrace();
				    	}
					}
				}
			}
			if(dcon.getOperatingSystem().equals("2")){
				
				if(dc.getLocation().equals("ld")){
					dbBackupHistory = new DbBackupHistory();
					String loadValue = "";
					boolean response = false;
					// file write to remote linux machine
					String fileName = mySqlBackUp.backupDataWithDatabaseLinux(mysqlDbLocation, 
							dcon.getIpAddress(), dcon.getDbPort(), dcon.getDbUserName(), dcon.getDbPassword(), dbname, 
							linuxTempBackupLocation, dcon.getLinPassword(), dcon.getLinUserName(), dcon.getLinPort());
					
					// file read from remote linux machine
					if(fileName != null && fileName.length() > 0){
						loadValue = myLinB.readFileFromLinux(dcon.getIpAddress(), dcon.getLinUserName(), dcon.getLinPassword(), 
							dcon.getLinPort(), linuxTempBackupLocation, fileName);
					}
					
					// file write to local drive
					if(loadValue != null && loadValue.length() > 0){						
						response = myLinB.fileWriteToLocal(localDriveLocation, fileName, loadValue);							
					}		
					
					// information save to db
					if(response){
							// delete remote linux server temp file 
							myLinB.linuxMachineFileDelete(dcon.getLinUserName(), dcon.getLinPassword(), dcon.getIpAddress(), dcon.getLinPort(), linuxTempBackupLocation+fileName);
							
							dbBackupHistory.setLocation(localDriveLocation);
							dbBackupHistory.setActive(true);
							dbBackupHistory.setBackupType("Manual");
							dbBackupHistory.setDbName(dbname);
							dbBackupHistory.setCreatedDate(now);
							dbBackupHistory.setIpAddress(dcon.getIpAddress());
							dbBackupHistory.setCreatedBy("System");
							dbBackupHistory.setFileName(fileName);
							dbBackupHistory.setStatus("Completed");
							dbBackupHistory.setOperatingSystem(dcon.getOperatingSystem());				
							backupHistoryService.addDbBackupHistory(dbBackupHistory);
					}	
					
					
				} else if(dc.getLocation().equals("gd")){
					
				} else {
					dbBackupHistory = new DbBackupHistory();
					DropboxAPI dapi = new DropboxAPI();
					boolean response = false;
					boolean resp = false;
					String loadValue = "";
					
					// file write to remote linux machine for temp
					String fileName  =  mySqlBackUp.backupDataWithDatabaseLinux(mysqlDbLocation, 
							dcon.getIpAddress(), dcon.getDbPort(), dcon.getDbUserName(), dcon.getDbPassword(), dbname, 
							linuxTempBackupLocation, dcon.getLinPassword(), dcon.getLinUserName(), dcon.getLinPort());	
					
					// file read from linux temp backup location
					if(fileName != null && fileName.length() > 0){
						loadValue = myLinB.readFileFromLinux(dcon.getIpAddress(), dcon.getLinUserName(), dcon.getLinPassword(), 
							dcon.getLinPort(), linuxTempBackupLocation, fileName);
					}
					
					// file write to local temp location
					if(loadValue != null && loadValue.length() > 0){
						resp = myLinB.fileWriteToLocal(localDriveTempLocation, fileName, loadValue);
					}
					
					// file write to dropbox from local dirve location
					if(resp){						
							//response = dapi.uploadDropbox(dropboxAccessToken, dropboxTempLocation, fileName, dropboxUploadLocation, dbname, dcon.getIpAddress());
							myLinB.linuxMachineFileDelete(dcon.getLinUserName(), dcon.getLinPassword(), dcon.getIpAddress(), dcon.getLinPort(), linuxTempBackupLocation+fileName);
							response = dapi.uploadDropbox(dropboxAccessToken, localDriveTempLocation, fileName, dropboxUploadLocation, dbname, dcon.getIpAddress());
					}										
					
					// information save to db
					if(response){
						
						dbBackupHistory.setLocation(dropboxUploadLocation+dcon.getIpAddress()+"/"+dbname+"/");
						dbBackupHistory.setActive(true);
						dbBackupHistory.setBackupType("Manual");
						dbBackupHistory.setDbName(dbname);
						dbBackupHistory.setCreatedDate(now);
						dbBackupHistory.setIpAddress(dcon.getIpAddress());
						dbBackupHistory.setCreatedBy("System");
						dbBackupHistory.setFileName(fileName);
						dbBackupHistory.setStatus("Completed");
						dbBackupHistory.setOperatingSystem(dcon.getOperatingSystem());				
						backupHistoryService.addDbBackupHistory(dbBackupHistory);
						
						try{
	
				    		File file = new File(localDriveTempLocation+fileName);
				    		if(file.delete()){
				    			System.out.println(file.getName() + " is deleted!");
				    		}else{
				    			System.out.println("Delete operation is failed.");
				    		}
				    	}catch(Exception e){
				    		e.printStackTrace();
				    	}
					}
					
					
				}
			
			}
			
			
			
			
		}		
		
		return new ModelAndView("redirect:/backupHistory", model);
	}
	
	//
	@RequestMapping(value = "/removeDbConnect", method = RequestMethod.GET)
	public ModelAndView removeDbConnect (DatabaseConnection databaseConnection){
				
		dbConnectionService.deleteDatabaseConnection(databaseConnection);
		
		return new ModelAndView("redirect:/manualDbConList");
	}
	
	public List<String> listFilesForFolder(final File folder) {
		List<String> fileNames = new ArrayList<String>(); 
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            //System.out.println(fileEntry.getName());
	            fileNames.add(fileEntry.getName());
	        }
	    }
	    return fileNames; 
	}	
	

	
}
