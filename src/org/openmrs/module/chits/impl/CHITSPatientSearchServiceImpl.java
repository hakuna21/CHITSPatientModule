package org.openmrs.module.chits.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.chits.CHITSPatientSearchService;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.db.PatientSearchDAO;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * CHITS general purpose service implementation.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class CHITSPatientSearchServiceImpl extends BaseOpenmrsService implements Serializable, CHITSPatientSearchService {
	/** Logger instance */
	private final Log log = LogFactory.getLog(getClass());

	/** The patient dao (search regardless of gender) */
	private PatientSearchDAO patientSearchDAO;

	/** The female patient dao */
	private PatientSearchDAO femalePatientSearchDAO;

	/** The male patient dao */
	private PatientSearchDAO malePatientSearchDAO;

	/** Auto-wired service */
	private PersonService personService;

	/** Empty identifier type filter */
	private static final List<PatientIdentifierType> EMPTY_IDENTIFIER_TYPE_FILTER = Collections.emptyList();

	/**
	 * Default Constructor
	 * */
	public CHITSPatientSearchServiceImpl() {
	}

	/**
	 * Method returns the minimum number of search characters
	 * 
	 * @return the value of min search characters
	 */
	private int getMinSearchCharacters() {
		// int minSearchCharacters = OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_MIN_SEARCH_CHARACTERS;
		// String minSearchCharactersStr = Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_MIN_SEARCH_CHARACTERS);
		//
		// try {
		// minSearchCharacters = Integer.valueOf(minSearchCharactersStr);
		// } catch (NumberFormatException e) {
		// // do nothing
		// }
		//
		// return minSearchCharacters;

		// don't rely on global properties anymore, the DWR calls are already configured anyway
		return 1;
	}

	@Override
	public Integer getCountOfPatients(String query) {
		if (StringUtils.isBlank(query) || query.length() < getMinSearchCharacters()) {
			return 0;
		}

		// if there is a number in the query string
		if (query.matches(".*\\d+.*")) {
			log.debug("[Identifier search] Query: " + query);
			return patientSearchDAO.getCountOfPatients(null, query, EMPTY_IDENTIFIER_TYPE_FILTER, false);
		} else {
			// there is no number in the string, search on name
			return patientSearchDAO.getCountOfPatients(query, null, EMPTY_IDENTIFIER_TYPE_FILTER, false);
		}
	}

	@Override
	public List<Patient> getPatients(String query) throws APIException {
		return getPatients(query, 0, null);
	}

	/**
	 * @see PatientService#getPatients(String, Integer, Integer)
	 */
	@Override
	public List<Patient> getPatients(String query, Integer start, Integer length) throws APIException {
		if (StringUtils.isBlank(query) || query.length() < getMinSearchCharacters()) {
			return new ArrayList<Patient>();
		}

		// if there is a number in the query string
		if (query.matches(".*\\d+.*")) {
			log.debug("[Identifier search] Query: " + query);
			return patientSearchDAO.getPatients(null, query, EMPTY_IDENTIFIER_TYPE_FILTER, false, start, length);
		} else {
			// there is no number in the string, search on name
			return patientSearchDAO.getPatients(query, null, EMPTY_IDENTIFIER_TYPE_FILTER, false, start, length);
		}
	}

	@Override
	public Integer getCountOfFemalePatients(String query) {
		if (StringUtils.isBlank(query) || query.length() < getMinSearchCharacters()) {
			return 0;
		}

		// if there is a number in the query string
		if (query.matches(".*\\d+.*")) {
			log.debug("[Identifier search] Query: " + query);
			return femalePatientSearchDAO.getCountOfPatients(null, query, EMPTY_IDENTIFIER_TYPE_FILTER, false);
		} else {
			// there is no number in the string, search on name
			return femalePatientSearchDAO.getCountOfPatients(query, null, EMPTY_IDENTIFIER_TYPE_FILTER, false);
		}
	}

	@Override
	public List<Patient> getFemalePatients(String query) throws APIException {
		return getFemalePatients(query, 0, null);
	}

	/**
	 * @see PatientService#getPatients(String, Integer, Integer)
	 */
	@Override
	public List<Patient> getFemalePatients(String query, Integer start, Integer length) throws APIException {
		if (StringUtils.isBlank(query) || query.length() < getMinSearchCharacters()) {
			return new ArrayList<Patient>();
		}

		// if there is a number in the query string
		if (query.matches(".*\\d+.*")) {
			log.debug("[Identifier search] Query: " + query);
			return femalePatientSearchDAO.getPatients(null, query, EMPTY_IDENTIFIER_TYPE_FILTER, false, start, length);
		} else {
			// there is no number in the string, search on name
			return femalePatientSearchDAO.getPatients(query, null, EMPTY_IDENTIFIER_TYPE_FILTER, false, start, length);
		}
	}

	@Override
	public Relationship getFemaleParent(Patient patient) {
		// search for patient's mother
		final RelationshipType parentRelType = personService.getRelationshipTypeByName(Constants.PARENT_RELATIONSHIP_NAME);

		// find the first female parent of this patient
		Relationship femaleParent = null;

		// find parents of this patient
		for (Relationship parent : personService.getRelationships(null, patient, parentRelType)) {
			if ("F".equals(parent.getPersonA().getGender())) {
				if (femaleParent == null) {
					femaleParent = parent;
				} else {
					log.warn("Multiple female parent/child relationships for patient with ID: " + patient.getId());
				}
			}
		}

		// return the female parent
		return femaleParent;
	}

	@Override
	public Integer getCountOfMalePatients(String query) {
		if (StringUtils.isBlank(query) || query.length() < getMinSearchCharacters()) {
			return 0;
		}

		// if there is a number in the query string
		if (query.matches(".*\\d+.*")) {
			log.debug("[Identifier search] Query: " + query);
			return malePatientSearchDAO.getCountOfPatients(null, query, EMPTY_IDENTIFIER_TYPE_FILTER, false);
		} else {
			// there is no number in the string, search on name
			return malePatientSearchDAO.getCountOfPatients(query, null, EMPTY_IDENTIFIER_TYPE_FILTER, false);
		}
	}

	@Override
	public List<Patient> getMalePatients(String query) throws APIException {
		return getMalePatients(query, 0, null);
	}

	/**
	 * @see PatientService#getPatients(String, Integer, Integer)
	 */
	@Override
	public List<Patient> getMalePatients(String query, Integer start, Integer length) throws APIException {
		if (StringUtils.isBlank(query) || query.length() < getMinSearchCharacters()) {
			return new ArrayList<Patient>();
		}

		// if there is a number in the query string
		if (query.matches(".*\\d+.*")) {
			log.debug("[Identifier search] Query: " + query);
			return malePatientSearchDAO.getPatients(null, query, EMPTY_IDENTIFIER_TYPE_FILTER, false, start, length);
		} else {
			// there is no number in the string, search on name
			return malePatientSearchDAO.getPatients(query, null, EMPTY_IDENTIFIER_TYPE_FILTER, false, start, length);
		}
	}

	@Override
	public Relationship getMaleParent(Patient patient) {
		// search for patient's father
		final RelationshipType parentRelType = personService.getRelationshipTypeByName(Constants.PARENT_RELATIONSHIP_NAME);

		// find the first male parent of this patient
		Relationship maleParent = null;

		// find parents of this patient
		for (Relationship parent : personService.getRelationships(null, patient, parentRelType)) {
			if ("M".equals(parent.getPersonA().getGender())) {
				if (maleParent == null) {
					maleParent = parent;
				} else {
					log.warn("Multiple male parent/child relationships for patient with ID: " + patient.getId());
				}
			}
		}

		// return the male parent
		return maleParent;
	}

	@Override
	public Relationship getPartner(Patient patient) {
		// search for patient's partner
		final RelationshipType partnerRelType = personService.getRelationshipTypeByName(Constants.PARTNER_RELATIONSHIP_NAME);

		// find the partner record of this patient
		Relationship partnerRelationship = null;

		// find partners of this patient
		for (Relationship relationship : personService.getRelationships(null, patient, partnerRelType)) {
			if (partnerRelationship == null) {
				partnerRelationship = relationship;
			} else {
				log.warn("Multiple partner relationships for patient with ID: " + patient.getId());
			}
		}

		if (partnerRelationship == null) {
			// test the reverse relationship
			for (Relationship relationship : personService.getRelationships(patient, null, partnerRelType)) {
				if (partnerRelationship == null) {
					partnerRelationship = relationship;
				} else {
					log.warn("Multiple partner relationships for patient with ID: " + patient.getId());
				}
			}
		}

		// return the partner patient
		return partnerRelationship;
	}

	@Override
	public List<Patient> getPatientsCreatedBetween(Date fromDate, Date toDate) {
		// dispatch to DAO
		return patientSearchDAO.getPatientsCreatedBetween(fromDate, toDate);
	}

	public void setPatientSearchDAO(PatientSearchDAO patientSearchDAO) {
		this.patientSearchDAO = patientSearchDAO;
	}

	public void setFemalePatientSearchDAO(PatientSearchDAO femalePatientSearchDAO) {
		this.femalePatientSearchDAO = femalePatientSearchDAO;
	}

	public void setMalePatientSearchDAO(PatientSearchDAO malePatientSearchDAO) {
		this.malePatientSearchDAO = malePatientSearchDAO;
	}

	@Autowired
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
}