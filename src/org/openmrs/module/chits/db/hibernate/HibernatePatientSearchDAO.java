package org.openmrs.module.chits.db.hibernate;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.PatientSearchCriteria;
import org.openmrs.module.chits.Util;
import org.openmrs.module.chits.db.PatientSearchDAO;

/**
 * Database implementation methods for searching patients.
 */
public class HibernatePatientSearchDAO implements PatientSearchDAO {
	/** Logger instance */
	private final Log log = LogFactory.getLog(getClass());

	/** If specified, all filter options will be filtered with this gender attribute */
	private String gender;

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	public HibernatePatientSearchDAO() {
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
	 * If specified, all search criteria will return results for patients of this gender only.
	 * 
	 * @param gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @see org.openmrs.api.db.PatientDAO#getCountOfPatients(String, String, List, boolean)
	 */
	@Override
	public Integer getCountOfPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes, boolean matchIdentifierExactly) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Patient.class);
		criteria.add(Expression.eq("voided", Boolean.FALSE));

		// Skip the ordering of names because H2(and i think PostgreSQL) will require one of the ordered
		// columns to be in the resultset which then contradicts with the combination of
		// (Projections.rowCount() and Criteria.uniqueResult()) that expect back only one row with one column
		criteria = new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(name, null, identifierTypes, matchIdentifierExactly, false);

		// include only the patients with the given gender (if specified)
		if (!StringUtils.isEmpty(gender)) {
			criteria.add(Expression.eq("gender", gender));
		}

		if (identifier != null && !StringUtils.isEmpty(identifier)) {
			criteria.createAlias("identifiers", "ids");
			criteria.add(Expression.eq("ids.voided", false));
			criteria.add(Expression.like("ids.identifier", identifier, MatchMode.ANYWHERE));
		}

		criteria.setProjection(Projections.countDistinct("patientId"));
		return (Integer) criteria.uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.PatientDAO#getPatients(String, String, List, boolean, int, Integer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes, boolean matchIdentifierExactly,
			Integer start, Integer length) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Patient.class);
		criteria.add(Expression.eq("voided", Boolean.FALSE));
		
		criteria = new PatientSearchCriteria(sessionFactory, criteria).prepareCriteria(name, null, identifierTypes, matchIdentifierExactly, false);
		// restricting the search to the max search results value
		if (start != null) {
			criteria.setFirstResult(start);
		}

		int limit = Util.getMaximumSearchResults();
		if (length == null || length > limit) {
			if (log.isDebugEnabled())
				log.debug("Limitng the size of the number of matching patients to " + limit);
			length = limit;
		}

		if (length != null) {
			criteria.setMaxResults(length);
		}

		// include only the patients with the given gender (if specified)
		if (!StringUtils.isEmpty(gender)) {
			criteria.add(Expression.eq("gender", gender));
		}

		if (identifier != null && !StringUtils.isEmpty(identifier)) {
			criteria.createAlias("identifiers", "ids");
			criteria.add(Expression.eq("ids.voided", false));
			criteria.add(Expression.like("ids.identifier", identifier, MatchMode.ANYWHERE));
		}

		// sort by last name, first name, then middle name
		criteria.addOrder(Order.asc("name.familyName"));
		criteria.addOrder(Order.asc("name.givenName"));
		criteria.addOrder(Order.asc("name.middleName"));
		// end of changes

		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Patient> getPatientsCreatedBetween(Date fromDate, Date toDate) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Patient.class);
		crit.add(Expression.eq("voided", Boolean.FALSE));

		if (fromDate != null) {
			crit.add(Expression.ge("dateCreated", fromDate));
		}
		if (toDate != null) {
			crit.add(Expression.le("dateCreated", toDate));
		}

		// order by increasing date
		crit.addOrder(Order.asc("dateCreated"));

		return crit.list();
	}
}
