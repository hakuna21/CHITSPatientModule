package org.openmrs.module.chits.db.hibernate;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.openmrs.User;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.chits.audit.UserSessionInfo;
import org.openmrs.module.chits.db.AuditDAO;

/**
 * Database implementation methods for the {@link AuditDAO}
 */
public class HibernateAuditDAO implements AuditDAO {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	public HibernateAuditDAO() {
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
	public UserSessionInfo saveUserSessionInfo(UserSessionInfo userSessionInfo) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(userSessionInfo);
		return userSessionInfo;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserSessionInfo> findUserSessionInfo(User user, boolean currentlyLoggedInOnly, int startRow, int maxResults) throws DAOException {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserSessionInfo.class);

		// add filter for given user
		if (user != null) {
			criteria.add(Expression.eq("user", user));
		}

		// add filter for currently logged-in users only
		if (currentlyLoggedInOnly) {
			criteria.add(Expression.isNull("logoutTimestamp"));
		}

		// set starting row number
		if (startRow >= 0) {
			criteria.setFirstResult(startRow);
		}

		// set maximum returned records
		if (maxResults >= 0) {
			criteria.setMaxResults(maxResults);
		}

		// return by descending login time
		criteria.addOrder(Order.desc("loginTimestamp"));

		// return matching records
		return criteria.list();
	}

	/**
	 * Cleans up user session data by purging all records older with logoutTimestamp more than 'olderThanDays' days old and marking all unclosed sessions as
	 * 'timed-out' and closed.
	 * <P>
	 * This cleanup code is typically run during server startup to clean up the {@link UserSessionInfo} table.
	 * 
	 * @param olderThanDays
	 *            Purge all user session data with logoutTimestamp values older than the given number of days
	 * @param excludeSessionIDs
	 *            Exclude records with these session IDs from processing. Typically these would be those sessions that are still active.
	 */
	@Override
	public void cleanupUserSessionInfoTable(int olderThanDays, Collection<String> excludeSessionIds) {
		final boolean withExcludeSessionIDs = excludeSessionIds != null && !excludeSessionIds.isEmpty();

		// mark all unclosed sessions as 'timed-out'
		final Query timedoutQuery = sessionFactory.getCurrentSession().createQuery("" //
				+ "UPDATE UserSessionInfo " //
				+ "   SET logoutTimestamp = :now, " //
				+ "       sessionTimedOut = true" //
				+ " WHERE logoutTimestamp IS NULL" //
				+ (withExcludeSessionIDs ? "   AND sessionId NOT in (:excludeSessionIds)" : "")) //
				.setParameter("now", new Date());

		if (withExcludeSessionIDs) {
			timedoutQuery.setParameterList("excludeSessionIds", excludeSessionIds);
		}

		// time-out users that didn't log off before the last server restart
		final int markedTimedOut = timedoutQuery.executeUpdate();
		log.info("User sessions marked timed-out: " + markedTimedOut);

		// purge all old user session data
		final Query purgeQuery = sessionFactory.getCurrentSession().createQuery("" //
				+ "DELETE UserSessionInfo " //
				+ " WHERE logoutTimestamp < :purgeThreshold" //
				+ (withExcludeSessionIDs ? "   AND sessionId NOT in (:excludeSessionIds)" : "")) //
				.setParameter("purgeThreshold", new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(olderThanDays)));

		if (withExcludeSessionIDs) {
			purgeQuery.setParameterList("excludeSessionIds", excludeSessionIds);
		}

		// purge all session data older than the threshold setting
		final int purged = purgeQuery.executeUpdate();
		log.info("Old user sessions purged: " + purged);
	}
}
