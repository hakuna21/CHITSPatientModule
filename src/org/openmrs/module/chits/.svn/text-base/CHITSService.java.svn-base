package org.openmrs.module.chits;

import java.util.Collection;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.UserService;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.chits.audit.UserSessionInfo;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * General purpose service for CHITS specific requirements.
 * 
 * @author Bren
 */
@Transactional
public interface CHITSService extends OpenmrsService {
	/**
	 * Save the given <code>familyFolder</code> in the database
	 * 
	 * @param familyFolder
	 *            the FamilyFolder object to save
	 * @return The saved familyFolder object
	 * @throws APIException
	 */
	@Transactional(readOnly = false)
	@Authorized(PrivilegeConstants.EDIT_PATIENTS)
	public FamilyFolder saveFamilyFolder(FamilyFolder familyFolder) throws APIException;

	/**
	 * Save the given <code>householdInformation</code> in the database
	 * 
	 * @param householdInformation
	 *            the HouseholdInformation object to save
	 * @return The saved householdInformation object
	 * @throws APIException
	 */
	@Transactional(readOnly = false)
	@Authorized(PrivilegeConstants.EDIT_PATIENTS)
	public HouseholdInformation saveHouseholdInformation(HouseholdInformation householdInformation) throws APIException;

	/**
	 * Get {@link HouseholdInformation} by internal identifier
	 * 
	 * @param householdInformationId
	 *            internal HouseholdInformation identifier
	 * @return FamilyFolder with given internal identifier
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public HouseholdInformation getHouseholdInformation(Integer householdInformationId) throws APIException;

	/**
	 * Completely delete the given {@link HouseholdInformation} from the database
	 * 
	 * @param HouseholdInformation
	 *            the household information record to purge / delete
	 * @throws APIException
	 */
	@Transactional(readOnly = false)
	@Authorized(PrivilegeConstants.EDIT_PATIENTS)
	public void purgeHouseholdInformation(HouseholdInformation householdInformation) throws APIException;

	/**
	 * Get {@link FamilyFolder} by internal identifier
	 * 
	 * @param familyFolderId
	 *            internal FamilyFolder identifier
	 * @return FamilyFolder with given internal identifier
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public FamilyFolder getFamilyFolder(Integer familyFolderId) throws APIException;

	/**
	 * Get {@link FamilyFolder} by UUID
	 * 
	 * @param uuid
	 *            Unique identifier
	 * @return FamilyFolder with given uuid
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public FamilyFolder getFamilyFolderByUuid(String uuid) throws APIException;

	/**
	 * Get {@link FamilyFolder} by code.
	 * 
	 * @param code
	 *            the family folder code
	 * @return FamilyFolder with given code
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public FamilyFolder getFamilyFolderByCode(String code) throws APIException;

	/**
	 * Completely delete the given {@link FamilyFolder} from the database
	 * 
	 * @param FamilyFolder
	 *            the FamilyFolder to purge / delete
	 * @throws APIException
	 */
	@Transactional(readOnly = false)
	@Authorized(PrivilegeConstants.DELETE_PATIENTS)
	public void purgeFamilyFolder(FamilyFolder familyFolder) throws APIException;

	/**
	 * Voids the family folder with the given reason.
	 * 
	 * @param folder
	 *            The family folder to void.
	 * @param reason
	 *            The reason for voiding the family folder.
	 */
	@Transactional(readOnly = false)
	@Authorized(PrivilegeConstants.DELETE_PATIENTS)
	public FamilyFolder voidFamilyFolder(FamilyFolder folder, String reason);

	/**
	 * Get all family folders in the database.
	 * 
	 * @return list of all family folders
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public List<FamilyFolder> getAllFamilyFolders() throws APIException;

	/**
	 * Get all family folders in the database with the given barangay code modified within the specified dates.
	 * 
	 * @param barangayCode
	 * @param modifiedSince
	 * @return list of all family folders with the given barangay code
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public List<FamilyFolder> getAllFamilyFoldersByBarangay(String barangayCode, Long modifiedSince, Long modifiedUpto);

	/**
	 * Get all patients belonging to the given barangay and created or modified between the specified dates.
	 * 
	 * @param barangayCode
	 * @param modifiedSince
	 * @param modifiedUpto
	 * @return
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public List<Patient> getAllPatientsByBarangay(String barangayCode, Long modifiedSince, Long modifiedUpto);

	/**
	 * Get all patients belonging to the given barangay that had visits (encounters) between the specified dates.
	 * <p>
	 * Optionally filter only visits where a patient was enrolled within a given program.
	 * 
	 * @param barangayCode
	 *            The barangay code to search patients
	 * @param visitedFrom
	 *            Include patients that had visits on or after this time
	 * @param visitedTo
	 *            Include patients that had visits on or up to this time
	 * @param program
	 *            An optional {@link CachedProgramConceptId} for filtering only patient visits while the patient was currently enrolled in the given program.
	 * @return All patients that had visits between the specified dates
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public List<Patient> getAllPatientVisitsByBarangay(String barangayCode, Long visitedFrom, Long visitedTo, CachedProgramConceptId program);

	/**
	 * Get all family folders in the database containing the given text in the code or name of the family folder.
	 * 
	 * @param like
	 *            Text that the family folder's code or name should contain to be included in the result
	 * @return list of all family folders containing the text
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public List<FamilyFolder> getFamilyFoldersLike(String barangayCode, String like, Integer start, Integer length) throws APIException;

	/**
	 * Get all family folders in the database containing the given text in the code or name of the family folder.
	 * 
	 * @param like
	 *            Text that the family folder's code or name should contain to be included in the result
	 * @return list of all family folders containing the text
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public List<FamilyFolder> getAllFamilyFoldersLike(String like) throws APIException;

	/**
	 * Get all family folders for the given patient.
	 * 
	 * @param patientId
	 *            the patient's ID
	 * @return list of all family folders that the patient belongs to
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public List<FamilyFolder> getFamilyFoldersOf(Integer patientId) throws APIException;

	/**
	 * Get the number of family folders in the database containing the given text in the code or name of the family folder.
	 * 
	 * @param like
	 *            Text that the family folder's code or name should contain to be included in the result
	 * @return list of all family folders containing the text
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public Integer getFamilyFoldersCountLike(String barangayCode, String like) throws APIException;

	/**
	 * Get the barangay with the given code.
	 * 
	 * @param barangayCode
	 *            Barangay code
	 * @return The {@link Barangay} with the specified code
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public Barangay getBarangay(String barangayCode);

	/**
	 * Get the city / municipality with the given code.
	 * 
	 * @param municipalityCode
	 *            Municipality code
	 * @return The {@link Municipality} with the specified code
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public Municipality getMunicipality(String municipalityCode);

	/**
	 * Saves the patient queue;
	 * 
	 * @param patientQueue
	 *            The {@link PatientQueue} to save.
	 * @return The saved instance
	 * @throws APIException
	 */
	@Transactional(readOnly = false)
	@Authorized(PrivilegeConstants.EDIT_PATIENTS)
	public PatientQueue savePatientQueue(PatientQueue patientQueue) throws APIException;

	/**
	 * Get queued patients in the database starting at row 'start' and including up to 'length' records.
	 * 
	 * @param start
	 *            The start row to return
	 * @param length
	 *            The maximum number of results to return
	 * @return list of all queued patients
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public List<PatientQueue> getQueuedPatients(Integer start, Integer length) throws APIException;

	@Transactional
	@Authorized(PrivilegeConstants.EDIT_PATIENTS)
	public void endPatientConsult(EncounterService encounterService, ConceptService conceptService, UserService userService, PatientQueue patientQueue);

	/**
	 * Get all queued patients in the database.
	 * 
	 * @return list of all queued patients
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.EDIT_PATIENTS })
	public void purgePatientQueue(EncounterService encounterService, ConceptService conceptService, UserService userService) throws APIException;

	/**
	 * Returns the {@link PatientQueue} entry for the given {@link Patient} if the patient is still in the queue, or null if the patient is not in the queue.
	 * 
	 * @param patient
	 *            The {@link Patient} to search for in the queue
	 * @return The {@link PatientQueue} entry for this {@link Patient} if still in the queue, or null if the {@link Patient} is no longer in the queue.
	 * @throws DAOException
	 *             If any database errors occur.
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public PatientQueue getQueuedPatient(Patient patient) throws APIException;

	/**
	 * Calculate average consult times.
	 * 
	 * NOTE: NO privileges required to invoke this service method.
	 * 
	 * @return The average consult time, or null if no data is available.
	 * @throws DAOException
	 *             if a DAO error occurs.
	 */
	@Transactional(readOnly = true)
	public Double getAverageConsultTime() throws APIException;

	/**
	 * Get the number of queued patients in the database.
	 * 
	 * NOTE: NO privileges required to invoke this service method.
	 * 
	 * @return The number of queued patients in the database.
	 */
	@Transactional(readOnly = true)
	public int getQueuedPatientsCount() throws APIException;

	/**
	 * Saves an entity bean
	 * 
	 * @param entity
	 *            The entity bean to save
	 * @throws APIException
	 */
	@Authorized({ PrivilegeConstants.ADD_OBS })
	public void save(Obs obs) throws APIException;

	/**
	 * Purges an entity bean
	 * 
	 * @param entity
	 *            The entity bean to purge
	 * @throws APIException
	 */
	@Authorized({ PrivilegeConstants.DELETE_OBS })
	public void purge(Obs obs) throws APIException;

	/**
	 * Retrieves the serialized object representing the form template of the concept with the given UUID.
	 * 
	 * @param uuid
	 *            The UUID of the concept to obtain the serialized form of
	 * @return The serialized form of the concept matching the UUID, or null if no such serialized object exists.
	 * @throws DAOException
	 *             If any database errors occur
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_PATIENTS })
	public SerializedObject getSerializedObjectByUuid(String uuid) throws APIException;

	/**
	 * Saves or updates the given seralized object.
	 * 
	 * @param serializedObject
	 *            The serialized object to save or update
	 * @return The saved serialized object
	 * @throws DAOException
	 *             If any database errors occur
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.MANAGE_CONCEPTS })
	public SerializedObject saveSerializedObject(SerializedObject serializedObject) throws APIException;

	/**
	 * Clone of the {@link ConceptService#saveConcept(Concept)} method but without the additional checking of the datatype being changed while the observation
	 * is already in use.
	 * 
	 * @return The saved concept
	 */
	@Transactional
	@Authorized(PrivilegeConstants.MANAGE_CONCEPTS)
	public Concept saveConceptForcingDatatype(Concept concept) throws APIException;

	/**
	 * Saves multiple concepts in one transaction
	 * 
	 * @return The saved concepts
	 */
	@Transactional
	@Authorized(PrivilegeConstants.MANAGE_CONCEPTS)
	public List<Concept> saveConceptsForcingDatatype(List<Concept> concepts) throws APIException;

	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_CONCEPTS)
	public List<Concept> findICD10SymptomConcept(String query, int maxResults) throws DAOException;

	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_CONCEPTS)
	public List<Concept> findICD10DiagnosisConcept(String query, int maxResults) throws DAOException;

	@Transactional(readOnly = true)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public List<UserBarangay> getUserBarangays(User user) throws DAOException;

	@Transactional
	@Authorized(PrivilegeConstants.EDIT_USERS)
	public void setUserBarangayCodes(User user, List<String> barangayCodes) throws DAOException;

	@Transactional
	public UserSessionInfo saveUserSessionInfo(UserSessionInfo userSessionInfo) throws DAOException;

	@Transactional
	@Authorized(PrivilegeConstants.VIEW_USERS)
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
	@Authorized(PrivilegeConstants.EDIT_USERS)
	public void cleanupUserSessionInfoTable(int olderThanDays, Collection<String> excludeSessionIds);

	/**
	 * Searches all ConceptName instances matching the given name, regardless of locale or concept name type.
	 * 
	 * @param name
	 *            The name of the concept
	 * @return All ConceptName instances matching the given name
	 */
	public List<ConceptName> findMatchingConceptNames(String name);

	/**
	 * Purges the given concept name and all related concept name tags and words.
	 */
	@Authorized(PrivilegeConstants.MANAGE_CONCEPTS)
	public void purgeConceptName(ConceptName conceptName);

	/**
	 * Searches a concept by its fully specified name
	 * 
	 * @param name
	 *            The fully specified name of the concept
	 * @return The concept with the given fully specified name
	 */
	public Concept findConceptByFullySpecifiedName(String name);

	/**
	 * Changes references to the 'notPreferred' {@link Patient} to the 'preferred' patient in the CHITS tables (family folder and patient queue tables).
	 * <p>
	 * NOTE: This currently does not re-order the notes numbers for any merged encounters.
	 * 
	 * @param preferred
	 *            The patient to change references of the notPreferred patient to
	 * @param notPreferred
	 *            The patient to change references to the preferred patient of
	 */
	@Authorized(PrivilegeConstants.EDIT_PATIENTS)
	public void mergePatients(Patient preferred, Patient notPreferred);

	/**
	 * Obtains the last (latest) encounter bean in the database for the given patient.
	 * 
	 * @param patient
	 *            The patient to get the latest encounter of
	 * @return The latest {@link Encounter} on record of the given patient based on the encounter timestamp.
	 */
	@Transactional(readOnly = false)
	@Authorized(PrivilegeConstants.VIEW_PATIENTS)
	public Encounter getLatestEncounter(Patient patient);
}
