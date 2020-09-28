package com.nazdaq.dbbackup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nazdaq.dbbackup.dao.DbConnectionDao;
import com.nazdaq.dbbackup.model.DatabaseConnection;


@Service("DbConnectionService")
public class DbConnectionServiceImpl implements DbConnectionService {

	@Autowired
	DbConnectionDao dbConnectionDao;

	
	@Override
	public void addDatabaseConnection(DatabaseConnection databaseConnection) {
		// TODO Auto-generated method stub
		dbConnectionDao.addDatabaseConnection(databaseConnection);
	}

	@Override
	public List<DatabaseConnection> listDatabaseConnections() {
		// TODO Auto-generated method stub
		return dbConnectionDao.listDatabaseConnections();
	}

	@Override
	public DatabaseConnection getDatabaseConnection(Integer id) {
		// TODO Auto-generated method stub
		return dbConnectionDao.getDatabaseConnection(id);
	}

	@Override
	public void deleteDatabaseConnection(DatabaseConnection databaseConnection) {
		// TODO Auto-generated method stub 
		dbConnectionDao.deleteDatabaseConnection(databaseConnection);
	}

	@Override
	public DatabaseConnection getDatabaseConnectionByIpAddress(String ipAddress) {
		// TODO Auto-generated method stub
		return dbConnectionDao.getDatabaseConnectionByIpAddress(ipAddress);
	}

}
