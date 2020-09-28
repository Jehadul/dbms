package com.nazdaq.dbbackup.dao;

import java.util.List;

import com.nazdaq.dbbackup.model.DbBackupHistory;


public interface BackupHistoryDao {
	public void addDbBackupHistory(DbBackupHistory dbBackupHistory);

	public List<DbBackupHistory>listDbBackupHistory();
	
	public DbBackupHistory getDbBackupHistory(Integer id);
	
	public void deleteDbBackupHistory(DbBackupHistory dbBackupHistory);
	
	public List<DbBackupHistory> getDbBackupHistoryListByDateRange(String fromDate, String toDate);
}
