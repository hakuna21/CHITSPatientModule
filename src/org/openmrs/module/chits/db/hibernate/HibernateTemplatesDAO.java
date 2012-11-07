package org.openmrs.module.chits.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.db.TemplatesDAO;

/**
 * Database methods for the {@link CHITSService} relating to template records.
 */
public class HibernateTemplatesDAO implements TemplatesDAO {
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	public HibernateTemplatesDAO() {
	}

	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public SerializedObject getSerializedObjectByUuid(String uuid) throws DAOException {
		SerializedObject ret = null;
		if (uuid != null) {
			Criteria c = sessionFactory.getCurrentSession().createCriteria(SerializedObject.class);
			c.add(Expression.eq("uuid", uuid));
			ret = (SerializedObject) c.uniqueResult();
		}

		return ret;
	}

	@Override
	public SerializedObject saveSerializedObject(SerializedObject serializedObject) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(serializedObject);
		return serializedObject;
	}
}
