package com.nazdaq.dbbackup.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nazdaq.dbbackup.model.DbBackupHistory;

@Repository("BackupHistoryDao")
@Transactional
public class BackupHistoryDaoImpl implements BackupHistoryDao {
	
	@Autowired
	private SessionFactory sessionFactory;	

	@Override
	public void addDbBackupHistory(DbBackupHistory dbBackupHistory) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(dbBackupHistory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DbBackupHistory> listDbBackupHistory() {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		return (List<DbBackupHistory>) session.createCriteria(DbBackupHistory.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DbBackupHistory> getDbBackupHistoryListByDateRange(String fromDate, String toDate) {
		// TODO Auto-generated method stub
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery("from DbBackupHistory where createdDate >= '" + fromDate
					+ "' and createdDate <= '" + toDate
					+ "' order by id desc");

			return (List<DbBackupHistory>)(Object) query.list();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public DbBackupHistory getDbBackupHistory(Integer id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		return (DbBackupHistory) session.get(DbBackupHistory.class, id);
	}

	@Override
	public void deleteDbBackupHistory(DbBackupHistory dbBackupHistory) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		session.delete(dbBackupHistory);
	}

}
