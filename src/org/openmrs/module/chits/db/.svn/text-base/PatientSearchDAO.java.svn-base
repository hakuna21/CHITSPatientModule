package org.openmrs.module.chits.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.db.DAOException;

/**
 * Database methods for searching patients.
 */
public interface PatientSearchDAO {
	/**
	 * @see PatientService#getCountOfPatients(String)
	 */
	public Integer getCountOfPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes, boolean matchIdentifierExactly);

	/**
	 * @see org.openmrs.api.PatientService#getPatients(String, String, List, boolean, int, Integer)
	 */
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes, boolean matchIdentifierExactly,
			Integer start, Integer length) throws DAOException;

	/**
	 * Returns all patients created between (inclusive) the given dates.
	 * 
	 * @param fromDate
	 *            Include patients created on or after this date
	 * @param toDate
	 *            Include patients created on or before this date
	 * @return All matching patients
	 */
	public List<Patient> getPatientsCreatedBetween(Date fromDate, Date toDate);
}
