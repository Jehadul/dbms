package com.nazdaq.dbbackup.dao;

import java.util.List;

import com.nazdaq.dbbackup.model.DatabaseConnection;

public interface DbConnectionDao {
	public void addDatabaseConnection(DatabaseConnection databaseConnection);

	public List<DatabaseConnection> listDatabaseConnections();

	public DatabaseConnection getDatabaseConnection(Integer id);

	public void deleteDatabaseConnection(DatabaseConnection databaseConnection);

	public DatabaseConnection getDatabaseConnectionByIpAddress(String ipAddress);
}
