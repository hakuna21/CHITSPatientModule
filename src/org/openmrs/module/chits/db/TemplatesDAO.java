package org.openmrs.module.chits.db;

import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.chits.CHITSService;

/**
 * Database methods for the {@link CHITSService} relating to template records.
 */
public interface TemplatesDAO {
	/**
	 * Retrieves the serialized object representing the form template of the concept with the given UUID.
	 * 
	 * @param uuid
	 *            The UUID of the concept to obtain the serialized form of
	 * @return The serialized form of the concept matching the UUID, or null if no such serialized object exists.
	 * @throws DAOException
	 *             If any database errors occur
	 */
	public SerializedObject getSerializedObjectByUuid(String uuid) throws DAOException;

	/**
	 * Saves or updates the given seralized object.
	 * 
	 * @param serializedObject
	 *            The serialized object to save or update
	 * @return The saved serialized object
	 * @throws DAOException
	 *             If any database errors occur
	 */
	public SerializedObject saveSerializedObject(SerializedObject serializedObject) throws DAOException;
}
