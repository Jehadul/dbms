package com.nazdaq.dbbackup.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nazdaq.dbbackup.model.DatabaseConnection;


@Repository("DbConnectionDao")
@Transactional
public class DbConnectionDaoImpl implements DbConnectionDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void addDatabaseConnection(DatabaseConnection databaseConnection) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(databaseConnection);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DatabaseConnection> listDatabaseConnections() {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		return (List<DatabaseConnection>) session.createCriteria(DatabaseConnection.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@Override
	public DatabaseConnection getDatabaseConnection(Integer id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		return (DatabaseConnection) session.get(DatabaseConnection.class, id);
	}

	@Override
	public void deleteDatabaseConnection(DatabaseConnection databaseConnection) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		session.delete(databaseConnection);
	}

	@Override
	public DatabaseConnection getDatabaseConnectionByIpAddress(String ipAddress) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		try {
			Query query = session.createQuery("from DatabaseConnection where ipAddress = :ipAddress");
			query.setString("ipAddress", ipAddress);
			return (DatabaseConnection) query.list().get(0);

		} catch (Exception e) {
			System.out.print(e.getMessage());
			return new DatabaseConnection();
		}
	}

}
