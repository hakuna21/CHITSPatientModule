package org.openmrs.module.chits.db.hibernate;

import org.hibernate.SessionFactory;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.chits.HouseholdInformation;
import org.openmrs.module.chits.db.HouseholdInformationDAO;

/**
 * Database implementation methods for the {@link HouseholdInformationDAO}
 */
public class HibernateHouseholdInformationDAO implements HouseholdInformationDAO {
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	public HibernateHouseholdInformationDAO() {
	}

	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see org.openmrs.notification.db.HouseholdInformationDAO#saveHouseholdInformation(org.openmrs.notification.HouseholdInformation)
	 */
	public HouseholdInformation saveHouseholdInformation(HouseholdInformation householdInformation) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(householdInformation);
		return householdInformation;
	}

	/**
	 * @see org.openmrs.notification.db.HouseholdInformationDAO#getHouseholdInformation(java.lang.Integer)
	 */
	public HouseholdInformation getHouseholdInformation(Integer householdInformationId) throws DAOException {
		return (HouseholdInformation) sessionFactory.getCurrentSession().get(HouseholdInformation.class, householdInformationId);
	}

	/**
	 * @see org.openmrs.notification.db.HouseholdInformationDAO#deleteHouseholdInformation(org.openmrs.notification.HouseholdInformation)
	 */
	public void deleteHouseholdInformation(HouseholdInformation householdInformation) throws DAOException {
		// load the household information from the DB
		householdInformation = getHouseholdInformation(householdInformation.getHouseholdInformationId());
		if (householdInformation != null) {
			// be sure to detach all members
			householdInformation.getFamilyFolders().clear();

			// then we can delete the record itself
			sessionFactory.getCurrentSession().delete(householdInformation);
		}
	}
}
