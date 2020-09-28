package com.nazdaq.dbbackup.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 *
 * @author Taleb
 */
public class MySqlBackup {

    private int STREAM_BUFFER = 512000;
    private boolean status = false;
    //initializing the logger  
    static Logger log = Logger.getLogger(MySqlBackup.class.getName());

        public boolean backupDatabase(String host, String port, String user, String password, String db, String backupfile, String mysqlDumpExePath) {
        try {
            // Get MySQL DUMP data
            String dump = getServerDumpData(host, port, user, password, db, mysqlDumpExePath);
            //check the backup dump
            if (status) {
                byte[] data = dump.getBytes();
                // Set backup folder
                String rootpath = System.getProperty("user.dir") + backupfile;

                // See if backup folder exists
                File file = new File(rootpath);
                if (!file.isDirectory()) {
                    // Create backup folder when missing. Write access is needed.
                    file.mkdir();
                }
                // Compose full path, create a filename as you wish
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = new Date();
                String filepath = rootpath + "backup(With_DB)-" + db + "-" + host + "-(" + dateFormat.format(date) + ").sql";
                // Write SQL DUMP file
                File filedst = new File(filepath);
                FileOutputStream dest = new FileOutputStream(filedst);
                dest.write(data);
                dest.close();
                log.info("Backup created successfully for - " + db + " " + host);
            } else {
                //when status false  
                log.error("Could not create the backup for - " + db + " " + host);
            }

        } catch (Exception ex) {
            log.error(ex, ex.getCause());
        }

        return status;
    }

    public String getServerDumpData(String host, String port, String user, String password, String db, String mysqlDumpExePath) {
        StringBuilder dumpdata = new StringBuilder();
        try {
            if (host != null && user != null && password != null && db != null) {
                //generate a command
                String command[] = new String[]{"cmd.exe", "/c" , mysqlDumpExePath,
                    "--host=" + host,
                    "--port=" + (port.length() > 0 ? port : "3306"),
                    "--user=" + user,
                    "--password=" + password,
                    "--skip-comments",
                    "--databases",
                    db};

                // Run mysqldump
                ProcessBuilder pb = new ProcessBuilder(command);
                Process process = pb.start();

                InputStream in = process.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                int count;
                char[] cbuf = new char[STREAM_BUFFER];

                // Read datastream
                while ((count = br.read(cbuf, 0, STREAM_BUFFER)) != -1) {
                    dumpdata.append(cbuf, 0, count);
                }

                //set the status
                int processComplete = process.waitFor();
                if (processComplete == 0) {                   
                    status = true;
                } else {
                    status = false;
                }
                // Close
                br.close();
                in.close();
            }

        } catch (Exception ex) {
            log.error(ex, ex.getCause());
            return "";
        }
        return dumpdata.toString();
    }

    
    public static void main(String[] args) throws SQLException {
        MySqlBackup b = new MySqlBackup();
        // b.backupDatabase("10.100.3.108", "3306", "root", "root", "payroll", "/backup/", "/backup/mysqldump.exe");
        //b.backupDatabase("localhost", "3306", "root", "", "payroll", "/backup/", "/backup/mysqldump.exe");
        //  b.restoreDatabase("root", "", "backup/backup-payroll-10.100.3.108-27-09-2012.sql");
        // b.test();Desktop
       //b.backupDataWithDatabase("C:\\Program Files (x86)\\MySQL\\MySQL Server 5.0\\bin\\mysqldump.exe", "localhost", "3306", "root", "passw0rd", "hrms", "C:\\Users\\Ferdous.Islam\\Desktop\\test\\");
       b.backupAllDatabases("C:\\Program Files (x86)\\MySQL\\MySQL Server 5.0\\bin\\mysqldump.exe", "localhost", "3306", "root", "passw0rd", "C:\\Users\\Ferdous.Islam\\Desktop\\test\\");
    }

    public boolean backupDataWithOutDatabase(String dumpExePath, String host, String port, String user, String password, String database, String backupPath) {
        boolean status = false;
        try {
            Process p = null;

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            String filepath = "backup(without_DB)-" + database + "-" + host + "-(" + dateFormat.format(date) + ").sql";

            String batchCommand = "";
            if (password != "") {
                //only backup the data not included create database
                batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " --password=" + password + " " + database + " -r \"" + backupPath + "" + filepath + "\"";
            } else {
                batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " " + database + " -r \"" + backupPath + "" + filepath + "\"";
            }

            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(batchCommand);
            int processComplete = p.waitFor();

            if (processComplete == 0) {
                status = true;
                log.info("Backup created successfully for without DB " + database + " in " + host + ":" + port);
            } else {
                status = false;
                log.info("Could not create the backup for without DB " + database + " in " + host + ":" + port);
            }

        } catch (IOException ioe) {
            log.error(ioe, ioe.getCause());
        } catch (Exception e) {
            log.error(e, e.getCause());
        }
        return status;
    }

    public String backupDataWithDatabase(String dumpExePath, String host, String port, String user, String password, String database, String backupPath) {
        //boolean status = false;
    	String filepath = "";
        try {
            Process p = null;

            DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
            Date date = new Date();
            filepath = host + "_" + database + "_" + dateFormat.format(date) + ".sql";

            String batchCommand = "";
            if (password != "") {
                //Backup with database
                batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " --password=" + password + " --add-drop-database -B " + database + " -r \"" + backupPath + "" + filepath + "\"";
            } else {
                batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " --add-drop-database -B " + database + " -r \"" + backupPath + "" + filepath + "\"";
            }

            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(batchCommand);
            int processComplete = p.waitFor();


            if (processComplete == 0) {
                status = true;
                log.info("Backup created successfully for with DB " + database + " in " + host + ":" + port);
            } else {
                status = false;
                log.info("Could not create the backup for with DB " + database + " in " + host + ":" + port);
            }

        } catch (IOException ioe) {
            log.error(ioe, ioe.getCause());
        } catch (Exception e) {
            log.error(e, e.getCause());
        }
        return filepath;
    }

    public boolean backupAllDatabases(String dumpExePath, String host, String port, String user, String password, String backupPath) {
        boolean status = false;
        try {
            Process p = null;

            DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
            Date date = new Date();
            String filepath = host + "_" + dateFormat.format(date) + ".sql";

            String batchCommand = "";
            if (password != "") {
                //Backup with database
                batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " --password=" + password + " --add-drop-database -A  -r \"" + backupPath + "" + filepath + "\"";
            } else {
                batchCommand = dumpExePath + " -h " + host + " --port " + port + " -u " + user + " --add-drop-database -A  -r \"" + backupPath + "" + filepath + "\"";
            }

            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(batchCommand);
            int processComplete = p.waitFor();


            if (processComplete == 0) {
                status = true;
                log.info("Backup created successfully with All DBs in " + host + ":" + port);
            } else {
                status = false;
                log.info("Could not create the backup for All DBs in " + host + ":" + port);
            }

        } catch (IOException ioe) {
            log.error(ioe, ioe.getCause());
        } catch (Exception e) {
            log.error(e, e.getCause());
        }
        return status;
    }

    /**
     * Restore the backup into a local database
     *
     * @param dbUserName - user name
     * @param dbPassword - password
     * @param source - backup file
     * @return the status true/false
     */
    public boolean restoreDatabase(String dbUserName, String dbPassword, String source) {

        String[] executeCmd = new String[]{"mysql", "--user=" + dbUserName, "--password=" + dbPassword, "-e", "source " + source};

        Process runtimeProcess;
        try {
            runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                log.info("Backup restored successfully with " + source);
                return true;
            } else {
                log.info("Could not restore the backup " + source);
            }
        } catch (Exception ex) {
            log.error(ex, ex.getCause());
        }

        return false;

    }
    
    public String backupDataWithDatabaseLinux(String host, String port, String user, String password, String database, String linuxBackupLocation) {
        //boolean status = false;
    	String filepath = "";
        try {
            Process p = null;

            DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
            Date date = new Date();
            filepath = host + "_" + database + "_" + dateFormat.format(date) + ".sql";

            //String dumpCommand = "C:\\Program Files (x86)\\MySQL\\MySQL Server 5.0\\bin\\mysqldump.exe -h " + host + " -P "+ port +" -u " + user +" -p" + password + " " + database + " > \\ " + linuxBackupLocation + "" + filepath+ "\"";
            //String dumpCommand = "/usr/bin/mysqldump -h" + host + " -u" + user +" -p" + password + " " + database;
            String batchCommand = "ssh -f -L 3306:localhost:3306  admin@"+host+ " -N mysqldump -h " + host + " --port " + port + " -u" + user + " --password=" + password + " --add-drop-database " + database + " -r \"" + linuxBackupLocation + "" + filepath + "\"";

            //p =  Runtime.getRuntime().exec("ssh " + host);
            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(batchCommand); 
            int processComplete = p.waitFor();

            if (processComplete == 0) {
                status = true;
                log.info("Backup created successfully for with DB " + database + " in " + host + ":" + port);
            } else {
                status = false;
                log.info("Could not create the backup for with DB " + database + " in " + host + ":" + port);
            }

        } catch (IOException ioe) {
            log.error(ioe, ioe.getCause());
        } catch (Exception e) {
            log.error(e, e.getCause());
        }
        return filepath;
    }
    
    
    public String backupDataWithDatabaseLinux(String dumpExePath, String host, String port, String user, 
    		String password, String database, String backupPath, String linPassword, 
    		String linUserName, Integer linPort) {
        //boolean status = false;
    	String filepath = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
            Date date = new Date();
            filepath = host + "_" + database + "_" + dateFormat.format(date) + ".sql";
            
            String dumpCommand = dumpExePath + " " + database + " -h " + host + " -P " + port + " -u "
    				+ user + " -p" + password + " -r " + backupPath+filepath;
            
            String linHost = linUserName+"@"+host;
            
            MySqlLinuxBackup linBackup = new MySqlLinuxBackup();
            boolean result = linBackup.linuxMachineFileUpload(linPassword, linHost, linPort, dumpCommand);
            
            if(!result){
            	filepath = "";
            }
            
    		
    	} catch (Exception e) {
            log.error(e, e.getCause());
        }
        return filepath;
    }
    
    public String dmpFileNameGenerate(String host, String database){
    	String filepath = "";
    	 try {
             DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
             Date date = new Date();
             filepath = host + "_" + database + "_" + dateFormat.format(date) + ".sql";   
             
     		} catch (Exception e) {     			
             log.error(e, e.getCause());
             return filepath;
         }
         return filepath;
    }
    
    
}
