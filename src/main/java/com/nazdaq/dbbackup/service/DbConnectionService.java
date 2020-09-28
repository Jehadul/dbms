package com.nazdaq.dbbackup.service;

import java.util.List;

import com.nazdaq.dbbackup.model.DatabaseConnection;


public interface DbConnectionService {
	public void addDatabaseConnection(DatabaseConnection databaseConnection);

	public List<DatabaseConnection>listDatabaseConnections();
	
	public DatabaseConnection getDatabaseConnection(Integer id);
	
	public void deleteDatabaseConnection(DatabaseConnection databaseConnection);
	
	public DatabaseConnection getDatabaseConnectionByIpAddress(String ipAddress);
}
