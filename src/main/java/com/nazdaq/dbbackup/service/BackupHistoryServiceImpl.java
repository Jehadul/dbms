package com.nazdaq.dbbackup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nazdaq.dbbackup.dao.BackupHistoryDao;
import com.nazdaq.dbbackup.model.DbBackupHistory;



@Service("BackupHistoryService")
public class BackupHistoryServiceImpl implements BackupHistoryService{
	
	@Autowired
	BackupHistoryDao backupHistoryDao;	


	@Override
	public void addDbBackupHistory(DbBackupHistory dbBackupHistory) {
		// TODO Auto-generated method stub
		backupHistoryDao.addDbBackupHistory(dbBackupHistory);
	}

	@Override
	public List<DbBackupHistory> listDbBackupHistory() {
		// TODO Auto-generated method stub
		return backupHistoryDao.listDbBackupHistory();
	}

	@Override
	public DbBackupHistory getDbBackupHistory(Integer id) {
		// TODO Auto-generated method stub
		return backupHistoryDao.getDbBackupHistory(id);
	}

	@Override
	public void deleteDbBackupHistory(DbBackupHistory dbBackupHistory) {
		// TODO Auto-generated method stub
		backupHistoryDao.deleteDbBackupHistory(dbBackupHistory);
	}
	
	@Override
	public List<DbBackupHistory> getDbBackupHistoryListByDateRange(String fromDate, String toDate){
		return backupHistoryDao.getDbBackupHistoryListByDateRange(fromDate, toDate);
	}

	

}
