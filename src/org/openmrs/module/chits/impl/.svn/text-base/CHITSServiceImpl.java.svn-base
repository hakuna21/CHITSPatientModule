package org.openmrs.module.chits.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ConceptsLockedException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ConceptDAO;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.chits.Barangay;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.CachedProgramConceptId;
import org.openmrs.module.chits.Constants.VisitConcepts;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.HouseholdInformation;
import org.openmrs.module.chits.Municipality;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientQueue;
import org.openmrs.module.chits.UserBarangay;
import org.openmrs.module.chits.audit.UserSessionInfo;
import org.openmrs.module.chits.db.AuditDAO;
import org.openmrs.module.chits.db.CHITSConceptsDAO;
import org.openmrs.module.chits.db.FamilyFolderDAO;
import org.openmrs.module.chits.db.HouseholdInformationDAO;
import org.openmrs.module.chits.db.PatientQueueDAO;
import org.openmrs.module.chits.db.TemplatesDAO;
import org.openmrs.util.LocaleUtility;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.validator.ConceptValidator;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * CHITS general purpose service implementation.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class CHITSServiceImpl extends BaseOpenmrsService implements Serializable, CHITSService {
	/** Logger instance */
	private final Log log = LogFactory.getLog(getClass());

	/** DAO instance */
	private FamilyFolderDAO familyFolderDAO;

	/** DAO instance */
	private HouseholdInformationDAO householdInformationDAO;

	/** The Patient Queue dao */
	private PatientQueueDAO patientQueueDAO;

	/** The template dao */
	private TemplatesDAO templatesDAO;

	/** The ConceptDAO */
	private ConceptDAO conceptDAO;

	/** The CHITS ConceptsDAO */
	private CHITSConceptsDAO chitsConceptsDAO;

	/** The Audit DAO */
	private AuditDAO auditDAO;

	/** Barangay codes holder */
	private final StaticBarangayCodesHolder barangayCodesHolder = StaticBarangayCodesHolder.getInstance();

	/**
	 * Default Constructor
	 * */
	public CHITSServiceImpl() {
	}

	public void setFamilyFolderDAO(FamilyFolderDAO familyFolderDAO) {
		log.debug("Assigned DAO instance: " + familyFolderDAO);
		this.familyFolderDAO = familyFolderDAO;
	}

	public void setHouseholdInformationDAO(HouseholdInformationDAO householdInformationDAO) {
		this.householdInformationDAO = householdInformationDAO;
	}

	public void setPatientQueueDAO(PatientQueueDAO patientQueueDAO) {
		this.patientQueueDAO = patientQueueDAO;
	}

	public void setTemplatesDAO(TemplatesDAO templatesDAO) {
		this.templatesDAO = templatesDAO;
	}

	public void setConceptDAO(ConceptDAO conceptDAO) {
		this.conceptDAO = conceptDAO;
	}

	public void setChitsConceptsDAO(CHITSConceptsDAO chitsConceptsDAO) {
		this.chitsConceptsDAO = chitsConceptsDAO;
	}

	public void setAuditDAO(AuditDAO auditDAO) {
		this.auditDAO = auditDAO;
	}

	@Override
	public FamilyFolder saveFamilyFolder(FamilyFolder familyFolder) throws APIException {
		log.debug("Create a family folder " + familyFolder);

		if (familyFolder.getCreator() == null)
			familyFolder.setCreator(Context.getAuthenticatedUser());
		if (familyFolder.getDateCreated() == null)
			familyFolder.setDateCreated(new Date());

		if (familyFolder.getFamilyFolderId() != null) {
			familyFolder.setChangedBy(Context.getAuthenticatedUser());
			familyFolder.setDateChanged(new Date());
		}

		if (familyFolder.getUuid() == null) {
			familyFolder.setUuid(UUID.randomUUID().toString());
		}

		if (familyFolder.getHeadOfTheFamily() != null) {
			// ensure that the head of the family is also a member of the family
			familyFolder.addPatient(familyFolder.getHeadOfTheFamily());
		}

		// update with saved instance
		familyFolder = familyFolderDAO.saveFamilyFolder(familyFolder);

		// make sure these patients are not members of any other family folder
		final Set<FamilyFolder> otherFolders = new HashSet<FamilyFolder>();
		for (Patient patient : familyFolder.getPatients()) {
			// get patient's folders (encapsulate in a new List so that we don't use hibernate's list directly)
			for (FamilyFolder testFolder : familyFolderDAO.getFamilyFoldersOf(patient.getPatientId())) {
				if (!familyFolder.getId().equals(testFolder.getId())) {
					// this is an 'other' folder which we should disconnect from this patient
					otherFolders.add(testFolder);
				}
			}
		}

		// the 'other' family folders should be disconnected from this family folder's patients
		for (FamilyFolder otherFolder : otherFolders) {
			// remove all patients of the 'other' family folder
			for (Patient patient : familyFolder.getPatients()) {
				otherFolder.getPatients().remove(patient);
			}

			// if the 'head of the family' is no longer a member of this 'other' family folder, then unset it
			if (!otherFolder.getPatients().contains(otherFolder.getHeadOfTheFamily())) {
				// head of the family no longer a member of this family folder, so un-set it
				otherFolder.setHeadOfTheFamily(null);
			}

			// save the family folder so that it doesn't include this patient
			familyFolderDAO.saveFamilyFolder(otherFolder);
		}

		// return saved folder instance
		return familyFolder;
	}

	@Override
	public HouseholdInformation saveHouseholdInformation(HouseholdInformation householdInformation) throws APIException {
		log.debug("saveHouseholdInformation: " + householdInformation);

		if (householdInformation.getCreator() == null)
			householdInformation.setCreator(Context.getAuthenticatedUser());
		if (householdInformation.getDateCreated() == null)
			householdInformation.setDateCreated(new Date());

		if (householdInformation.getHouseholdInformationId() != null) {
			householdInformation.setChangedBy(Context.getAuthenticatedUser());
			householdInformation.setDateChanged(new Date());
		}

		if (householdInformation.getUuid() == null) {
			householdInformation.setUuid(UUID.randomUUID().toString());
		}

		// update and return the saved instance
		return householdInformationDAO.saveHouseholdInformation(householdInformation);
	}

	@Override
	public HouseholdInformation getHouseholdInformation(Integer householdInformationId) throws APIException {
		return householdInformationDAO.getHouseholdInformation(householdInformationId);
	}

	@Override
	public void purgeHouseholdInformation(HouseholdInformation householdInformation) throws APIException {
		log.debug("purgeHouseholdInformation(" + householdInformation + ")");
		householdInformationDAO.deleteHouseholdInformation(householdInformation);
	}

	@Override
	public FamilyFolder getFamilyFolder(Integer familyFolderId) throws APIException {
		return familyFolderDAO.getFamilyFolder(familyFolderId);
	}

	@Override
	public FamilyFolder getFamilyFolderByUuid(String uuid) throws APIException {
		return familyFolderDAO.getFamilyFolderByUuid(uuid);
	}

	@Override
	public FamilyFolder getFamilyFolderByCode(String code) throws APIException {
		return familyFolderDAO.getFamilyFolderByCode(code);
	}

	@Override
	public void purgeFamilyFolder(FamilyFolder familyFolder) throws APIException {
		familyFolderDAO.deleteFamilyFolder(familyFolder);
	}

	@Override
	public FamilyFolder voidFamilyFolder(FamilyFolder folder, String reason) {
		if (folder.isVoided() != null && folder.isVoided()) {
			// nothing to do
			return folder;
		} else {
			if (!org.springframework.util.StringUtils.hasText(reason)) {
				throw new APIException("Reason is required");
			}

			folder.setVoided(true);
			folder.setVoidedBy(Context.getAuthenticatedUser());
			folder.setVoidReason(reason);
			return saveFamilyFolder(folder);
		}
	}

	@Override
	public List<FamilyFolder> getAllFamilyFolders() throws APIException {
		return familyFolderDAO.getAllFamilyFolders();
	}

	@Override
	public List<FamilyFolder> getAllFamilyFoldersByBarangay(String barangayCode, Long modifiedSince, Long modifiedUpto) {
		return familyFolderDAO.getAllFamilyFoldersByBarangay(barangayCode, modifiedSince, modifiedUpto);
	}

	@Override
	public List<Patient> getAllPatientsByBarangay(String barangayCode, Long modifiedSince, Long modifiedUpto) {
		return familyFolderDAO.getAllPatientsByBarangay(barangayCode, modifiedSince, modifiedUpto);
	}

	@Override
	public List<Patient> getAllPatientVisitsByBarangay(String barangayCode, Long visitedFrom, Long visitedTo, CachedProgramConceptId program) {
		return familyFolderDAO.getAllPatientVisitsByBarangay(barangayCode, visitedFrom, visitedTo, program);
	}

	@Override
	public List<FamilyFolder> getFamilyFoldersLike(String barangayCode, String like, Integer start, Integer length) throws APIException {
		return familyFolderDAO.getFamilyFoldersLike(barangayCode, like, start, length);
	}

	@Override
	public List<FamilyFolder> getAllFamilyFoldersLike(String like) throws APIException {
		return familyFolderDAO.getAllFamilyFoldersLike(like);
	}

	@Override
	public List<FamilyFolder> getFamilyFoldersOf(Integer patientId) throws APIException {
		return familyFolderDAO.getFamilyFoldersOf(patientId);
	}

	@Override
	public Integer getFamilyFoldersCountLike(String barangayCode, String like) throws APIException {
		return familyFolderDAO.getFamilyFoldersCountLike(barangayCode, like);
	}

	@Override
	public Barangay getBarangay(String barangayCode) {
		return barangayCodesHolder.barangays.get(barangayCode);
	}

	@Override
	public Municipality getMunicipality(String municipalityCode) {
		return barangayCodesHolder.municipalities.get(municipalityCode);
	}

	/*
	 * Service methods relating to patient queues
	 */

	@Override
	public PatientQueue savePatientQueue(PatientQueue patientQueue) throws APIException {
		if (patientQueue.getNotesNumber() == null || patientQueue.getNotesNumber() < 1) {
			// update the notes number!
			final int notesNumber = patientQueueDAO.nextNotesNumber(patientQueue.getPatient());
			patientQueue.setNotesNumber(notesNumber);
		}

		return patientQueueDAO.savePatientQueue(patientQueue);
	}

	@Override
	public PatientQueue getQueuedPatient(Patient patient) throws APIException {
		return patientQueueDAO.getQueuedPatient(patient);
	}

	@Override
	public List<PatientQueue> getQueuedPatients(Integer start, Integer length) throws APIException {
		return patientQueueDAO.getQueuedPatients(start, length);
	}

	@Override
	public void endPatientConsult(EncounterService encounterService, ConceptService conceptService, UserService userService, PatientQueue patientQueue) {
		// set the patient's 'exitedQueue' value
		final Date now = new Date();

		// set timestamp of when patient exited the queue for records purposes
		patientQueue.setExitedQueue(now);

		// need to mark the end-of-consult timestamp only if consult was actually started
		if (patientQueue.getConsultStart() != null) {
			// if an encounter record was created, then the patient started consult:
			// set the end consult timestamp
			patientQueue.setConsultEnd(now);
		}

		// delete corresponding encounter if there were no observations entered
		final Encounter enc = patientQueue.getEncounter();
		if (enc != null) {
			if (enc.getObs().isEmpty()) {
				// detach and purge the unused encounter
				patientQueue.setEncounter(null);
				encounterService.purgeEncounter(enc);
			} else {
				// get currently logged-in user
				User user = Context.getAuthenticatedUser();
				if (user == null) {
					// startup process? Use the user of the encounter instance
					user = enc.getChangedBy() != null ? enc.getChangedBy() : enc.getCreator();
				}

				// add the notes number to this encounter (as an 'observation') before closing the encounter
				final Obs notesObs = ObsUtil.observationForUpdate(enc, VisitConcepts.NOTES_NUMBER, userService);

				// update the notes number for this encounter
				notesObs.setValueText(VisitConcepts.NOTES_NUMBER.getConceptName());
				notesObs.setValueNumeric(patientQueue.getNotesNumber() != null ? patientQueue.getNotesNumber().doubleValue() : null);

				// set consult start timestamp
				if (patientQueue.getConsultStart() != null) {
					// store the start of consult value
					final Obs consultStartObs = ObsUtil.observationForUpdate(enc, VisitConcepts.CONSULT_START, userService);
					consultStartObs.setValueText(consultStartObs.getConcept().getName().getName());
					consultStartObs.setValueDatetime(patientQueue.getConsultStart());
				}

				// set consult end timestamp
				if (patientQueue.getConsultEnd() != null) {
					// store the end of consult value
					final Obs consultEndObs = ObsUtil.observationForUpdate(enc, VisitConcepts.CONSULT_END, userService);
					consultEndObs.setValueText(consultEndObs.getConcept().getName().getName());
					consultEndObs.setValueDatetime(patientQueue.getConsultEnd());
				}

				// and save the encounter
				enc.setDateChanged(now);
				enc.setChangedBy(user);
				encounterService.saveEncounter(enc);
			}
		}

		// save the patient queue record
		savePatientQueue(patientQueue);
	}

	@Override
	public void purgePatientQueue(EncounterService encounterService, ConceptService conceptService, UserService userService) throws APIException {
		for (PatientQueue pq : patientQueueDAO.getAllQueuedPatients()) {
			if (pq.getPatient() != null && pq.getPatient().getPatientId() != null) {
				log.info("Purging patient queue entry with id: " + pq.getPatientQueueId());
				endPatientConsult(encounterService, conceptService, userService, pq);
			}

			// purge the patient queue record
			patientQueueDAO.deletePatientQueue(pq);
		}

		// flush all changes
		Context.flushSession();

		log.info("Patient queue has been purged.");
	}

	@Override
	public Double getAverageConsultTime() throws APIException {
		return patientQueueDAO.getAverageConsultTime();
	}

	@Override
	public int getQueuedPatientsCount() throws APIException {
		return patientQueueDAO.getQueuedPatientsCount();
	}

	@Override
	public void save(Obs obs) throws APIException {
		patientQueueDAO.save(obs);
	}

	@Override
	public void purge(Obs obs) throws APIException {
		patientQueueDAO.purge(obs);
	}

	@Override
	public SerializedObject getSerializedObjectByUuid(String uuid) throws APIException {
		return templatesDAO.getSerializedObjectByUuid(uuid);
	}

	@Override
	public SerializedObject saveSerializedObject(SerializedObject serializedObject) throws APIException {
		return templatesDAO.saveSerializedObject(serializedObject);
	}

	/**
	 * Search concept answers for matching query
	 */
	@Override
	public List<Concept> findICD10SymptomConcept(String query, int maxResults) throws APIException {
		return chitsConceptsDAO.findICD10SymptomConcepts(query, maxResults);
	}

	/**
	 * Search concept set members for matching query
	 */
	@Override
	public List<Concept> findICD10DiagnosisConcept(String query, int maxResults) throws APIException {
		return chitsConceptsDAO.findICD10DiagnosisConcepts(query, maxResults);
	}

	@Override
	public UserSessionInfo saveUserSessionInfo(UserSessionInfo userSessionInfo) throws DAOException {
		return auditDAO.saveUserSessionInfo(userSessionInfo);
	}

	@Override
	public List<UserSessionInfo> findUserSessionInfo(User user, boolean currentlyLoggedInOnly, int startRow, int maxResults) throws DAOException {
		return auditDAO.findUserSessionInfo(user, currentlyLoggedInOnly, startRow, maxResults);
	}

	@Override
	public void cleanupUserSessionInfoTable(int olderThanDays, Collection<String> excludeSessionIds) {
		auditDAO.cleanupUserSessionInfoTable(olderThanDays, excludeSessionIds);
	}

	/**
	 * COPIED FROM ConceptServiceImpl.java but removed the call to checkIfDatatypeCanBeChanged().
	 * 
	 * @param concept
	 * @return
	 * @throws APIException
	 */
	@Override
	public Concept saveConceptForcingDatatype(Concept concept) throws APIException {
		// make sure the administrator hasn't turned off concept editing
		checkIfLocked();

		return saveConceptForcingDatatypeImpl(concept);
	}

	/**
	 * Saves multiple concepts in one transaction
	 * 
	 * @return The saved concepts
	 */
	@Override
	public List<Concept> saveConceptsForcingDatatype(List<Concept> concepts) throws APIException {
		final List<Concept> savedConcepts = new ArrayList<Concept>();
		for (Concept concept : concepts) {
			savedConcepts.add(saveConceptForcingDatatype(concept));
		}

		// return the set of saved concepts
		return savedConcepts;
	}

	@Override
	public List<UserBarangay> getUserBarangays(User user) throws DAOException {
		return familyFolderDAO.getUserBarangays(user);
	}

	@Override
	public void setUserBarangayCodes(User user, List<String> barangayCodes) throws DAOException {
		final List<UserBarangay> userBarangays = familyFolderDAO.getUserBarangays(user);
		final List<UserBarangay> toPurge = new ArrayList<UserBarangay>();

		// determine what barangay codes to retain, add, and purge
		final List<String> toAdd = new ArrayList<String>(barangayCodes);
		for (UserBarangay currentUserBarangay : userBarangays) {
			final String currentBrgyCode = currentUserBarangay.getBarangayCode();
			if (toAdd.contains(currentBrgyCode)) {
				// entry already exists for this barangay; no need to modify it
				toAdd.remove(currentBrgyCode);
			} else {
				// the user is no longer authorized to this barangay
				toPurge.add(currentUserBarangay);
			}
		}

		// track if changes are made
		boolean changesMade = false;

		// purge all barangays that the user is no longer authorized to
		for (UserBarangay purge : toPurge) {
			familyFolderDAO.deleteUserBarangay(purge);
			changesMade = true;
		}

		// add an entry for all new barangays that the user is authorized to
		for (String barangayCode : toAdd) {
			final UserBarangay add = new UserBarangay();
			add.setUser(user);
			add.setBarangayCode(barangayCode);
			familyFolderDAO.saveUserBarangay(add);
			changesMade = true;
		}

		if (changesMade) {
			// 'touch' the 'User' record for auditing purposes
			user.setChangedBy(Context.getAuthenticatedUser());
			user.setDateChanged(new Date());
			Context.getUserService().saveUser(user, null);
		}
	}

	private Concept saveConceptForcingDatatypeImpl(Concept concept) throws APIException {
		// remove checking of datatype changing!
		// checkIfDatatypeCanBeChanged(concept);

		List<ConceptName> changedConceptNames = null;
		Map<String, ConceptName> uuidClonedConceptNameMap = null;

		if (concept.getConceptId() != null) {
			uuidClonedConceptNameMap = new HashMap<String, ConceptName>();
			for (ConceptName conceptName : concept.getNames()) {
				// ignore newly added names
				if (conceptName.getConceptNameId() != null) {
					ConceptName clone = cloneConceptName(conceptName);
					clone.setConceptNameId(null);
					uuidClonedConceptNameMap.put(conceptName.getUuid(), clone);

					if (hasNameChanged(conceptName)) {
						if (changedConceptNames == null)
							changedConceptNames = new ArrayList<ConceptName>();
						changedConceptNames.add(conceptName);
					} else {
						// put back the concept name id
						clone.setConceptNameId(conceptName.getConceptNameId());
						// Use the cloned version
						try {
							BeanUtils.copyProperties(conceptName, clone);
						} catch (IllegalAccessException e) {
							log.error("Error generated", e);
						} catch (InvocationTargetException e) {
							log.error("Error generated", e);
						}
					}
				}
			}
		}

		Errors errors = new BindException(concept, "concept");
		new ConceptValidator().validate(concept, errors);
		if (errors.hasErrors()) {
			log.error("Validation errors: " + errors.getAllErrors());
			throw new APIException("Validation errors found");
		}

		if (CollectionUtils.isNotEmpty(changedConceptNames)) {
			for (ConceptName changedName : changedConceptNames) {
				// void old concept name
				ConceptName nameInDB = changedName;
				nameInDB.setVoided(true);
				nameInDB.setDateVoided(new Date());
				nameInDB.setVoidedBy(Context.getAuthenticatedUser());
				nameInDB.setVoidReason(Context.getMessageSourceService().getMessage("Concept.name.voidReason.nameChanged"));

				// Make the voided name a synonym, this would help to avoid
				// having multiple fully specified or preferred
				// names in a locale incase the name is unvoided
				if (!nameInDB.isSynonym())
					nameInDB.setConceptNameType(null);
				if (nameInDB.isLocalePreferred())
					nameInDB.setLocalePreferred(false);

				// create a new concept name from the matching cloned
				// conceptName
				ConceptName clone = uuidClonedConceptNameMap.get(nameInDB.getUuid());
				clone.setUuid(UUID.randomUUID().toString());
				concept.addName(clone);
			}
		}

		// Set a preferred name for each locale for those where it isn't yet specified
		for (Locale locale : LocaleUtility.getLocalesInOrder()) {
			ConceptName possiblePreferredName = concept.getPreferredName(locale);
			if (possiblePreferredName == null || !possiblePreferredName.isLocalePreferred()) {
				if (possiblePreferredName != null)
					possiblePreferredName.setLocalePreferred(true);
				// set the first synonym as the preferred name if it has any
				else if (!CollectionUtils.isEmpty(concept.getSynonyms(locale)))
					concept.getSynonyms(locale).iterator().next().setLocalePreferred(true);
			}
		}

		// Concept conceptToReturn = getConceptDAO().saveConcept(concept);
		Concept conceptToReturn = conceptDAO.saveConcept(concept);

		// // add/remove entries in the concept_word table (used for searching)
		// this.updateConceptIndex(conceptToReturn);
		conceptDAO.updateConceptWord(concept);

		return conceptToReturn;
	}

	/**
	 * COPIED FROM ConceptServiceImpl.java:
	 * 
	 * Utility method which loads the previous version of a conceptName to check if the name property of the given conceptName has changed.
	 * 
	 * @param conceptName
	 *            to be modified
	 * @return boolean indicating change in the name property
	 */
	private boolean hasNameChanged(ConceptName conceptName) {
		String newName = conceptName.getName();
		String oldName = conceptDAO.getSavedConceptName(conceptName).getName();
		return !oldName.equalsIgnoreCase(newName);
	}

	/**
	 * COPIED FROM ConceptServiceImpl.java:
	 * 
	 * @see org.openmrs.api.ConceptService#checkIfLocked()
	 */
	private void checkIfLocked() throws ConceptsLockedException {
		String locked = Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_CONCEPTS_LOCKED, "false");
		if (locked.toLowerCase().equals("true"))
			throw new ConceptsLockedException();
	}

	/**
	 * Searches all ConceptName instances matching the given name, regardless of locale or concept name type.
	 * 
	 * @param name
	 *            The name of the concept
	 * @return All ConceptName instances matching the given name
	 */
	@Override
	public List<ConceptName> findMatchingConceptNames(String name) {
		return chitsConceptsDAO.findMatchingConceptNames(name);
	}

	/**
	 * Purges the given concept name and all related concept name tags and words.
	 */
	@Override
	public void purgeConceptName(ConceptName conceptName) {
		chitsConceptsDAO.purgeConceptName(conceptName);
	}

	/**
	 * Searches a concept by its fully specified name
	 * 
	 * @param name
	 *            The fully specified name of the concept
	 * @return The concept with the given fully specified name
	 */
	public Concept findConceptByFullySpecifiedName(String name) {
		return chitsConceptsDAO.findConceptByFullySpecifiedName(name);
	}

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
	@Override
	public void mergePatients(Patient preferred, Patient notPreferred) {
		// change patient references in the family folder table
		familyFolderDAO.mergePatients(preferred, notPreferred);

		// change patient references in the patient queue
		patientQueueDAO.mergePatients(preferred, notPreferred);
	}

	/**
	 * Obtains the last (latest) encounter bean in the database for the given patient.
	 * 
	 * @param patient
	 *            The patient to get the latest encounter of
	 * @return The latest {@link Encounter} on record of the given patient based on the encounter timestamp.
	 */
	@Override
	public Encounter getLatestEncounter(Patient patient) {
		// find primary key id of latest encounter for this patient
		final Integer encId = patientQueueDAO.getLatestEncounterId(patient);

		// return the record (if any)
		return encId != null ? Context.getEncounterService().getEncounter(encId) : null;
	}

	/**
	 * COPIED FROM ConceptServiceImpl.java:
	 * 
	 * Creates a copy of a conceptName
	 * 
	 * @param conceptName
	 *            the conceptName to be cloned
	 * @return the cloned conceptName
	 */
	private ConceptName cloneConceptName(ConceptName conceptName) {
		ConceptName copy = new ConceptName();
		try {
			copy = (ConceptName) BeanUtils.cloneBean(conceptName);
		} catch (IllegalAccessException e) {

			log.warn("Error generated", e);
		} catch (InstantiationException e) {

			log.warn("Error generated", e);
		} catch (InvocationTargetException e) {

			log.warn("Error generated", e);
		} catch (NoSuchMethodException e) {

			log.warn("Error generated", e);
		}
		return copy;
	}
}
