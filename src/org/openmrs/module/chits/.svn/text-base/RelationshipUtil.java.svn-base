package org.openmrs.module.chits;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants.CivilStatusConcepts;
import org.openmrs.module.chits.Constants.IdAttributes;
import org.openmrs.module.chits.Constants.MiscAttributes;
import org.openmrs.module.chits.Constants.PhilhealthConcepts;

public class RelationshipUtil {
	private final PatientService patientService;
	private final PersonService personService;
	private final CHITSPatientSearchService chitsPatientSearchService;

	public RelationshipUtil(PersonService personService, PatientService patientService, CHITSPatientSearchService chitsService) {
		this.personService = personService;
		this.patientService = patientService;
		this.chitsPatientSearchService = chitsService;
	}

	public static Person getMother(Patient patient) {
		if (patient != null) {
			final CHITSPatientSearchService chitsPatientSearchService = Context.getService(CHITSPatientSearchService.class);
			final Relationship femaleParent = chitsPatientSearchService.getFemaleParent(patient);

			if (femaleParent != null) {
				return femaleParent.getPersonA();
			}
		}

		return null;
	}

	public Patient getPatientMotherOrCreteNew(Integer patientId) {
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;
		if (patient != null) {
			final Relationship femaleParent = chitsPatientSearchService.getFemaleParent(patient);

			if (femaleParent != null) {
				Patient motherPatient = patientService.getPatient(femaleParent.getPersonA().getId());
				if (motherPatient != null) {
					// store the patient's mother record
					return motherPatient;
				} else {
					// unlikely, but just in case no patient exists for the 'mother'...
					return new Patient(femaleParent.getPersonA());
				}
			}
		}

		return newBlankFemalePatientWithUUID();
	}

	public Patient getPatientFatherOrCreteNew(Integer patientId) {
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;
		if (patient != null) {
			final Relationship maleParent = chitsPatientSearchService.getMaleParent(patient);

			if (maleParent != null) {
				Patient fatherPatient = patientService.getPatient(maleParent.getPersonA().getId());
				if (fatherPatient != null) {
					// store the patient's father record
					return fatherPatient;
				} else {
					// unlikely, but just in case no patient exists for the 'father'...
					return new Patient(maleParent.getPersonA());
				}
			}
		}

		return newBlankMalePatientWithUUID();
	}

	/**
	 * Creates a blank patient with gender = 'F' and a randomly generated patient identifier using {@link UUID#randomUUID()}.
	 * 
	 * @return A blank patient with a gender of 'F' and the a randomly generated identifier.
	 */
	public static Patient newBlankFemalePatientWithUUID() {
		// store a blank 'mother' entity (with null ID and random UUID identifier)
		final Patient blankFemale = newBlankPatient();
		blankFemale.setGender("F");
		blankFemale.getPatientIdentifier().setIdentifier(UUID.randomUUID().toString());

		// store the blank female record
		return blankFemale;
	}

	/**
	 * Creates a blank patient with gender = 'M' and a randomly generated patient identifier using {@link UUID#randomUUID()}.
	 * 
	 * @return A blank patient with a gender of 'M' and the a randomly generated identifier.
	 */
	public static Patient newBlankMalePatientWithUUID() {
		// store a blank 'mother' entity (with null ID and random UUID identifier)
		final Patient blankMale = newBlankPatient();
		blankMale.setGender("M");
		blankMale.getPatientIdentifier().setIdentifier(UUID.randomUUID().toString());

		// store the blank male record
		return blankMale;
	}

	public static Patient newBlankPatient() {
		final PatientService patientService = Context.getPatientService();
		final PersonService personService = Context.getPersonService();
		final LocationService locationService = Context.getLocationService();

		final Patient blankPatient = new Patient();
		blankPatient.setGender("");

		if (blankPatient.getPatientIdentifier() == null) {
			// set to a unique identifier for this patient initially using a UUID
			final PatientIdentifier patientIdentifier = new PatientIdentifier();
			patientIdentifier.setIdentifierType(patientService.getPatientIdentifierTypeByName("Old Identification Number"));
			patientIdentifier.setPatient(blankPatient);
			patientIdentifier.setIdentifier("[New]");
			patientIdentifier.setLocation(locationService.getDefaultLocation());
			patientIdentifier.setUuid(UUID.randomUUID().toString());
			patientIdentifier.setCreator(Context.getAuthenticatedUser());
			patientIdentifier.setDateCreated(new Date());
			patientIdentifier.setChangedBy(Context.getAuthenticatedUser());
			patientIdentifier.setDateChanged(new Date());

			// attach the identifier to the patient and save it
			blankPatient.addIdentifier(patientIdentifier);
		}

		// give the patient a name
		if (blankPatient.getPersonName() == null) {
			final PersonName personName = newBlankPersonName();
			blankPatient.addName(personName);
		}

		// create attributes for expected fields (in case they don't exist yet)
		blankPatient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(IdAttributes.CHITS_LPIN), ""));
		blankPatient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(IdAttributes.CHITS_CRN), ""));
		blankPatient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(PhilhealthConcepts.CHITS_PHILHEALTH), ""));
		blankPatient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(IdAttributes.CHITS_SSS), ""));
		blankPatient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(IdAttributes.CHITS_GSIS), ""));
		blankPatient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(IdAttributes.CHITS_TIN), ""));
		blankPatient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(CivilStatusConcepts.CIVIL_STATUS.getConceptName()), ""));

		// return the new patient with default values
		return blankPatient;
	}

	public static PersonName newBlankPersonName() {
		final PersonName newBlankPersonName = new PersonName();
		newBlankPersonName.setFamilyName("");
		newBlankPersonName.setGivenName("");
		newBlankPersonName.setMiddleName("");
		newBlankPersonName.setPreferred(true);
		newBlankPersonName.setUuid(UUID.randomUUID().toString());
		newBlankPersonName.setCreator(Context.getAuthenticatedUser());
		newBlankPersonName.setDateCreated(new Date());

		return newBlankPersonName;
	}

	public void setPatientMotherRelationship(Patient patient, Patient mother) {
		// create a relationship between this patient and his / her mother
		Relationship femaleParent = chitsPatientSearchService.getFemaleParent(patient);
		if (femaleParent != null) {
			// update the patient's mother
			femaleParent.setPersonA(mother);
			femaleParent.setChangedBy(Context.getAuthenticatedUser());
			femaleParent.setDateChanged(new Date());
		} else {
			// create a new parent for this patient
			final RelationshipType parentRelType = personService.getRelationshipTypeByName(Constants.PARENT_RELATIONSHIP_NAME);
			femaleParent = new Relationship();
			femaleParent.setPersonA(mother);
			femaleParent.setPersonB(patient);
			femaleParent.setRelationshipType(parentRelType);
			femaleParent.setUuid(UUID.randomUUID().toString());
			femaleParent.setCreator(Context.getAuthenticatedUser());
			femaleParent.setDateCreated(new Date());
		}

		// save the female parent relationship
		personService.saveRelationship(femaleParent);
	}

	public void setPatientFatherRelationship(Patient patient, Patient father) {
		// create a relationship between this patient and his / her father
		Relationship maleParent = chitsPatientSearchService.getMaleParent(patient);
		if (maleParent != null) {
			// update the patient's father
			maleParent.setPersonA(father);
			maleParent.setChangedBy(Context.getAuthenticatedUser());
			maleParent.setDateChanged(new Date());
		} else {
			// create a new parent for this patient
			final RelationshipType parentRelType = personService.getRelationshipTypeByName(Constants.PARENT_RELATIONSHIP_NAME);
			maleParent = new Relationship();
			maleParent.setPersonA(father);
			maleParent.setPersonB(patient);
			maleParent.setRelationshipType(parentRelType);
			maleParent.setUuid(UUID.randomUUID().toString());
			maleParent.setCreator(Context.getAuthenticatedUser());
			maleParent.setDateCreated(new Date());
		}

		// save the male parent relationship
		personService.saveRelationship(maleParent);
	}

	public void setPatientPartnerRelationship(Patient patient, Patient partner) {
		// create a relationship between this patient and his / her mother
		Relationship partnerRelationship = chitsPatientSearchService.getPartner(patient);
		if (partnerRelationship != null && partner != null) {
			// update the patient's mother
			if (patient.equals(partnerRelationship.getPersonA())) {
				partnerRelationship.setPersonB(partner);
			} else {
				partnerRelationship.setPersonA(partner);
			}

			// update audit info
			partnerRelationship.setChangedBy(Context.getAuthenticatedUser());
			partnerRelationship.setDateChanged(new Date());

			// save the partner relationship
			personService.saveRelationship(partnerRelationship);
		} else if (partnerRelationship == null && partner != null) {
			// create a new partner relationship for this patient
			final RelationshipType parentRelType = personService.getRelationshipTypeByName(Constants.PARTNER_RELATIONSHIP_NAME);
			partnerRelationship = new Relationship();
			partnerRelationship.setPersonA(patient);
			partnerRelationship.setPersonB(partner);
			partnerRelationship.setRelationshipType(parentRelType);
			partnerRelationship.setUuid(UUID.randomUUID().toString());
			partnerRelationship.setCreator(Context.getAuthenticatedUser());
			partnerRelationship.setDateCreated(new Date());

			// save the partner relationship
			personService.saveRelationship(partnerRelationship);
		} else if (partnerRelationship != null) {
			// partner is null, so discard this relationship
			personService.purgeRelationship(partnerRelationship);
		}
	}

	/**
	 * Sets the NON_PATIENT attribute type into the patient if nonPatient.
	 * 
	 * @param person
	 *            The person to set / unset the non patient flag of
	 * @param nonPatient
	 *            If the 'non patient' should be set into the person or cleared
	 */
	public static void setNonPatientFlag(Person person, boolean nonPatient) {
		setAttributeValue(person, MiscAttributes.NON_PATIENT, nonPatient ? Constants.FLAG_YES : null);
	}

	/**
	 * Sets the FOUR_PS attribute type into the patient if fourPs is true, otherwise clears the attribute.
	 * 
	 * @param person
	 *            The person to set / unset the fourPs flag of
	 * @param fourPs
	 *            If the 'fourPs' flag should be set into the person or cleared
	 */
	public static void setFourPsFlag(Person person, boolean fourPs) {
		setAttributeValue(person, MiscAttributes.FOUR_PS, fourPs ? Constants.FLAG_YES : null);
	}

	/**
	 * Returns true if the non-patient flag is set for the given person record.
	 * 
	 * @param person
	 *            The person to check
	 * @return true if the non-patient flag is set for the given person record.
	 */
	public static boolean isNonPatient(Person person) {
		return hasAttributeValue(person, MiscAttributes.NON_PATIENT);
	}

	/**
	 * Sets the SEE_PHYSICIAN attribute type into the person with the given boolean value.
	 * 
	 * @param person
	 *            The person to set / unset the 'see physician' flag of
	 * @param mustSeePhysician
	 *            If the person should be flagged with 'must see physician'
	 */
	public static void setMustSeePhysicianFlag(Person person, boolean mustSeePhysician) {
		setAttributeValue(person, MiscAttributes.SEE_PHYSICIAN, mustSeePhysician ? Constants.FLAG_YES : null);
	}

	/**
	 * Returns true if the person is flagged with the 'must see physician' attribute.
	 * 
	 * @param person
	 *            The person to check
	 * @return true if the person is flagged with the 'must see physician' attribute.
	 */
	public static boolean isMustSeePhysician(Person person) {
		return hasAttributeValue(person, MiscAttributes.SEE_PHYSICIAN);
	}

	/**
	 * Sets the attribute value of the person if 'flagValue' is not empty, otherwise removes the attribute from the person
	 * 
	 * @param person
	 *            The person to set / unset the attribute value into
	 * @param attributeName
	 *            The name of the attribute to set / clear into from the person
	 * @param attributeValue
	 *            The attribute value to store; specify null or an empty string to remove the attribute from the person
	 */
	public static void setAttributeValue(Person person, String attributeName, String attributeValue) {
		final PersonAttributeType mustSeePhysicianAttributeType = Context.getPersonService().getPersonAttributeTypeByName(attributeName);
		if (!StringUtils.isEmpty(attributeValue)) {
			// make sure the attribute is present and set to the given value
			PersonAttribute attrib = person.getAttribute(attributeName);
			if (attrib == null) {
				person.addAttribute(new PersonAttribute(mustSeePhysicianAttributeType, attributeValue));
			} else if (!attributeValue.equals(attrib.getValue())) {
				attrib.setValue(attributeValue);
			}
		} else {
			// remove the attribute
			PersonAttribute attrib;
			while (null != (attrib = person.getAttribute(attributeName))) {
				person.removeAttribute(attrib);
			}
		}
	}

	/**
	 * Returns true if the person has a non-null and non-empty attribute value with the given attribute name.
	 * 
	 * @param person
	 *            The person to check
	 * @param attributeName
	 *            The name of the attribute to lookup (case sensitive!)
	 * @return true if the person is flagged with the given attribute
	 */
	public static boolean hasAttributeValue(Person person, String attributeName) {
		final PersonAttribute attrVal = person.getAttribute(attributeName);
		return attrVal != null && !StringUtils.isEmpty(attrVal.getValue());
	}

	/**
	 * Finds the Patient record representing the 'partner' of the given patient.
	 * 
	 * @param patient
	 *            The patient to search the partner of
	 * @return The partner of the patient, or null if there is none
	 */
	public Patient getPatientPartner(Patient patient) {
		final Relationship partnerRelationship = chitsPatientSearchService.getPartner(patient);

		if (partnerRelationship != null) {
			Patient partnerPatient = null;
			if (patient.getPatientId().equals(partnerRelationship.getPersonA().getId())) {
				// the partner is the patient on the 'other' side of the relationship
				partnerPatient = patientService.getPatient(partnerRelationship.getPersonB().getId());
			} else if (patient.getPatientId().equals(partnerRelationship.getPersonB().getId())) {
				// the partner is the patient on the 'other' side of the relationship
				partnerPatient = patientService.getPatient(partnerRelationship.getPersonA().getId());
			}

			if (partnerPatient != null) {
				// return the patient's partner record
				return partnerPatient;
			} else {
				// unlikely, but just in case no patient record exists for the 'partner'...
				return new Patient(partnerRelationship.getPersonA());
			}
		}

		// no partner patient found
		return null;
	}

	public Patient getPatientPartnerOrCreteNew(Integer patientId) {
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;
		if (patient != null) {
			final Patient partner = getPatientPartner(patient);
			if (partner != null) {
				return partner;
			}
		}

		// no partner found; create a blank patient to represent a partner
		if (patient != null && "F".equals(patient.getGender())) {
			// a female patient's partner is logically a male
			return newBlankMalePatientWithUUID();
		} else {
			// a male's patient's partner is logically a female
			return newBlankFemalePatientWithUUID();
		}
	}
}
