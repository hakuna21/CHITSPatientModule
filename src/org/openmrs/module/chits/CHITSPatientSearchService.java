package org.openmrs.module.chits;

import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.Relationship;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * General purpose service for CHITS specific requirements.
 * 
 * @author Bren
 */
@Transactional
public interface CHITSPatientSearchService extends OpenmrsService {
	/**
	 * Return the number of unvoided patients with names or patient identifiers starting with or equal to the specified text
	 * 
	 * @param query
	 *            the string to search on
	 * @return the number of patients matching the given search phrase
	 * @should return the right count when a patient has multiple matching person names
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public Integer getCountOfPatients(String query);

	/**
	 * Generic search on patients based on the given string. Implementations can use this string to search on name, identifier, etc Voided patients are not
	 * returned in search results
	 * 
	 * @param query
	 *            the string to search on
	 * @return a list of matching Patients
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<Patient> getPatients(String query) throws APIException;

	/**
	 * Generic search on patients based on the given string and returns a specific number of them from the specified starting position. Implementations can use
	 * this string to search on name, identifier, etc Voided patients are not returned in search results If start is 0 and length is not specified, then all
	 * matches are returned
	 * 
	 * @param query
	 *            the string to search on
	 * @param start
	 *            the starting index
	 * @param length
	 *            the number of patients to return
	 * @return a list of matching Patients
	 * @throws APIException
	 * @since 1.8
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<Patient> getPatients(String query, Integer start, Integer length) throws APIException;

	/**
	 * Return the number of unvoided female patients with names or patient identifiers starting with or equal to the specified text
	 * 
	 * @param query
	 *            the string to search on
	 * @return the number of female patients matching the given search phrase
	 * @should return the right count when a patient has multiple matching person names
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public Integer getCountOfFemalePatients(String query);

	/**
	 * Generic search on female patients based on the given string. Implementations can use this string to search on name, identifier, etc Voided patients are
	 * not returned in search results
	 * 
	 * @param query
	 *            the string to search on
	 * @return a list of matching female Patients
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<Patient> getFemalePatients(String query) throws APIException;

	/**
	 * Generic search on patients based on the given string and returns a specific number of them from the specified starting position. Implementations can use
	 * this string to search on name, identifier, etc Voided patients are not returned in search results If start is 0 and length is not specified, then all
	 * matches are returned
	 * 
	 * @param query
	 *            the string to search on
	 * @param start
	 *            the starting index
	 * @param length
	 *            the number of patients to return
	 * @return a list of matching Patients
	 * @throws APIException
	 * @since 1.8
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<Patient> getFemalePatients(String query, Integer start, Integer length) throws APIException;

	/**
	 * Finds the (first) female parent relationship to this patient (i.e., the patient's mother).
	 * 
	 * @param patient
	 *            The patient to find the female parent of.
	 * @return The patient's mother relationship instance, or null if not found.
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public Relationship getFemaleParent(Patient patient);

	/**
	 * Return the number of unvoided male patients with names or patient identifiers starting with or equal to the specified text
	 * 
	 * @param query
	 *            the string to search on
	 * @return the number of male patients matching the given search phrase
	 * @should return the right count when a patient has multiple matching person names
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public Integer getCountOfMalePatients(String query);

	/**
	 * Generic search on male patients based on the given string. Implementations can use this string to search on name, identifier, etc Voided patients are not
	 * returned in search results
	 * 
	 * @param query
	 *            the string to search on
	 * @return a list of matching male Patients
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<Patient> getMalePatients(String query) throws APIException;

	/**
	 * Generic search on patients based on the given string and returns a specific number of them from the specified starting position. Implementations can use
	 * this string to search on name, identifier, etc Voided patients are not returned in search results If start is 0 and length is not specified, then all
	 * matches are returned
	 * 
	 * @param query
	 *            the string to search on
	 * @param start
	 *            the starting index
	 * @param length
	 *            the number of patients to return
	 * @return a list of matching Patients
	 * @throws APIException
	 * @since 1.8
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<Patient> getMalePatients(String query, Integer start, Integer length) throws APIException;

	/**
	 * Finds the (first) male parent relationship to this patient (i.e., the patient's father).
	 * 
	 * @param patient
	 *            The patient to find the male parent of.
	 * @return The patient's father relationship instance, or null if not found.
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public Relationship getMaleParent(Patient patient);

	/**
	 * Finds the (first) partner relationship to this patient (i.e., the patient's partner or spouse).
	 * 
	 * @param patient
	 *            The patient to find the partner of.
	 * @return The patient's partner relationship instance, or null if not found.
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public Relationship getPartner(Patient patient);

	/**
	 * Returns all patients created between (inclusive) the given dates.
	 * 
	 * @param fromDate
	 *            Include patients created on or after this date
	 * @param toDate
	 *            Include patients created on or before this date
	 * @return All matching patients
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<Patient> getPatientsCreatedBetween(Date fromDate, Date toDate);
}
