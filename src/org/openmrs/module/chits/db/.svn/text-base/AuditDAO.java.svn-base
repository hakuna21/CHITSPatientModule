package org.openmrs.module.chits.db;

import java.util.Collection;
import java.util.List;

import org.openmrs.User;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.audit.UserSessionInfo;

/**
 * Database methods for the {@link CHITSService}.
 */
public interface AuditDAO {
	/**
	 * Save the user session audit information
	 * 
	 * @param userSessionInfo
	 *            The user session information to save
	 * @return The saved {@link UserSessionInfo} instance
	 */
	public UserSessionInfo saveUserSessionInfo(UserSessionInfo userSessionInfo) throws DAOException;

	/**
	 * Find all matching user session info
	 * 
	 * @param user
	 *            The {@link User} to save
	 * @param currentlyLoggedInOnly
	 *            if true, return only records for users currently logged in (i.e., not yet logged-out)
	 * @param startRow
	 *            Starting row number to return
	 * @param maxResults
	 *            Maximum return values
	 * @return Matching results
	 */
	public List<UserSessionInfo> findUserSessionInfo(User user, boolean currentlyLoggedInOnly, int startRow, int maxResults) throws DAOException;

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
	public void cleanupUserSessionInfoTable(int olderThanDays, Collection<String> excludeSessionIds);
}
