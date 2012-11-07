package org.openmrs.module.chits.db;

import org.openmrs.api.db.DAOException;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.HouseholdInformation;

/**
 * Database methods for the {@link CHITSService}.
 */
public interface HouseholdInformationDAO {
	/** Load the household information record with the given primary key ID */
	public HouseholdInformation getHouseholdInformation(Integer householdInformationId) throws DAOException;

	/** Save the given household information */
	public HouseholdInformation saveHouseholdInformation(HouseholdInformation householdInformation) throws DAOException;

	/** Delete the given household information */
	public void deleteHouseholdInformation(HouseholdInformation householdInformation) throws DAOException;
}
