package org.openmrs.module.chits.validator;

import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientForm;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates {@link PatientForm} instances.
 * 
 * @author Bren
 */
public class PatientValidator implements Validator, Constants {
	/** Logger instance */
	private Log log = LogFactory.getLog(getClass());

	/**
	 * Returns whether or not this validator supports validating a given class.
	 * 
	 * @param c
	 *            The class to check for support.
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean supports(Class c) {
		if (log.isDebugEnabled())
			log.debug(this.getClass().getName() + ".supports: " + c.getName());
		return PatientForm.class.isAssignableFrom(c);
	}

	/**
	 * Validates the given {@link PatientForm}.
	 * 
	 * @param obj
	 *            The {@link PatientForm} to validate.
	 * @param errors
	 *            Errors
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 * @should fail validation if name is blank
	 */
	@Override
	public void validate(Object obj, Errors errors) {
		if (log.isDebugEnabled()) {
			log.debug(this.getClass().getName() + ".validate...");
		}

		// we only validate Patient beans
		final PatientForm form = (PatientForm) obj;

		// validate 'patient' in form
		if (form.getPatient() != null) {
			if (form.isNonPatient()) {
				// perform minimal validation for non-patient records
				validateMinimalPatientFields("patient.", form.getPatient(), errors);
			} else {
				// perform full validation for patient records
				validatePatientFields("patient.", form.getPatient(), errors);
			}

			if (form.getMother() != null && form.getMother().getBirthdate() != null && form.getPatient().getBirthdate() != null) {
				// make sure mother's birthday is not after the child's birth date
				if (form.getMother().getBirthdate().after(form.getPatient().getBirthdate())) {
					// NOTE: bind the error to the selected mother if using an existing patient record for the mother, or bind it to the birth date field if
					// creating a new patient record for the mother.
					errors.rejectValue(form.isExistingMother() ? "mother.id" : "mother.birthdate", "chits.Patient.mother.birthdate.after.childs.birthdate");
				}
			}
		}

		if (form.isExistingMother()) {
			if (form.getMother() == null || form.getMother().getId() == null || form.getMother().getId() == 0) {
				if (!form.isNonPatient()) {
					// Mother is no longer required [Issue #6 of 'CHITS-OpenMRS Issue Log Sheet']
					// // for 'Patient' record types, the 'mother' field is required
					// errors.rejectValue("mother.id", "chits.Patient.mother.required");
				}
			} else {
				if (form.getMother().equals(form.getPatient())) {
					// cannot specify self as mother
					errors.rejectValue("mother.id", "chits.Patient.mother.cannot.be.self");
				} else if (isDescendantOf(form.getMother(), form.getPatient())) {
					// cannot specify a descendant as one's mother
					errors.rejectValue("mother.id", "chits.Patient.mother.cannot.be.descendant");
				}
			}
		} else {
			// validate the fields required to create a new patient record for the patient's mother
			validateMinimalPatientFields("mother.", form.getMother(), errors);
		}

		if (form.isHasPhilhealth()) {
			// validate philhealth fields
			final SimpleDateFormat fmt = Context.getDateFormat();
			final PersonAttribute phNum = form.getPatient().getAttribute(PhilhealthConcepts.CHITS_PHILHEALTH);
			final PersonAttribute phExp = form.getPatient().getAttribute(PhilhealthConcepts.CHITS_PHILHEALTH_EXPIRATION);
			final PersonAttribute phSpo = form.getPatient().getAttribute(PhilhealthSponsorConcepts.CHITS_PHILHEALTH_SPONSOR.getConceptName());

			if (phNum == null || !StringUtils.hasText(phNum.getValue())) {
				errors.rejectValue("patient.attributeMap['CHITS_PHILHEALTH']", "chits.Patient.philhealth.number.required");
			} else if (phNum.getValue().contains(" ")) {
				errors.rejectValue("patient.attributeMap['CHITS_PHILHEALTH']", "chits.Patient.philhealth.number.invalid");
			}

			if (phExp == null || !StringUtils.hasText(phExp.getValue())) {
				errors.rejectValue("patient.attributeMap['CHITS_PHILHEALTH_EXPIRATION']", "chits.Patient.philhealth.expiration.required");
			} else {
				try {
					fmt.parse(phExp.getValue());
				} catch (Exception ex) {
					errors.rejectValue("patient.attributeMap['CHITS_PHILHEALTH_EXPIRATION']", "chits.Patient.philhealth.expiration.invalid");
				}
			}

			if (phSpo == null || !StringUtils.hasText(phSpo.getValue())) {
				errors.rejectValue("patient.attributeMap['CHITS_PHILHEALTH_SPONSOR']", "chits.Patient.philhealth.sponsor.required");
			}
		}
	}

	/**
	 * Returns whether personA is a descendant (i.e., child, grandchild, great grandchild, etc.) of personB.
	 * 
	 * @param personA
	 *            The person to check if a descendant of personB
	 * @param personB
	 *            The person to check if an ancestor of A
	 * @return If personA is a descendant of personB (likewise, if personB is an ancestor of personA)
	 */
	public static boolean isDescendantOf(Person personA, Person personB) {
		final PersonService personService = Context.getPersonService();

		// search for personA's parents
		final RelationshipType parentRelType = personService.getRelationshipTypeByName(Constants.PARENT_RELATIONSHIP_NAME);

		// find parents of person A to determine if A is a descendant of B
		for (Relationship parent : personService.getRelationships(null, personA, parentRelType)) {
			if (parent.getPersonA().equals(personB)) {
				// person A's parent is personB, so personA is indeed a descendant of personB
				return true;
			} else if (isDescendantOf(parent.getPersonA(), personB)) {
				// the person A's parent's parent is personB, so person A is indeed a descendant of personB
				return true;
			}
		}

		// person A is not a descendant of person B
		return false;
	}

	private void validatePatientFields(String property, Patient patient, Errors errors) {
		validateMinimalPatientFields(property, patient, errors);

		if (patient.getBirthdate() == null) {
			errors.rejectValue(property + "birthdate", "chits.Patient.birthdate.required");
		}

		if (patient.getAttribute(CivilStatusConcepts.CIVIL_STATUS.getConceptName()) == null
				|| !StringUtils.hasText(patient.getAttribute(CivilStatusConcepts.CIVIL_STATUS.getConceptName()).getValue())) {
			errors.rejectValue(property + "attributeMap['Civil Status']", "chits.Patient.civil.status.required");
		}
	}

	public static void validateMinimalPatientFields(String property, Patient patient, Errors errors) {
		if (!StringUtils.hasText(patient.getGender())) {
			errors.rejectValue(property + "gender", "chits.Patient.gender.required");
		}

		if (patient.getBirthdate() != null && patient.getBirthdate().getTime() > System.currentTimeMillis()) {
			errors.rejectValue(property + "birthdate", "chits.Patient.birthdate.cannot.be.in.future");
		}

		if (patient.getPersonName() == null || !StringUtils.hasText(patient.getPersonName().getGivenName())) {
			errors.rejectValue(property + "personName.givenName", "chits.Patient.first.name.required");
		} else {
			// automatically capitalize first letter
			patient.getPersonName().setGivenName(StringUtils.capitalize(patient.getPersonName().getGivenName()));
		}

		if (patient.getPersonName() == null || !StringUtils.hasText(patient.getPersonName().getFamilyName())) {
			errors.rejectValue(property + "personName.familyName", "chits.Patient.family.name.required");
		} else {
			// automatically capitalize first letter
			patient.getPersonName().setFamilyName(StringUtils.capitalize(patient.getPersonName().getFamilyName()));
		}

		if (patient.getPersonName() != null && StringUtils.hasText(patient.getPersonName().getMiddleName())) {
			// automatically capitalize first letter
			patient.getPersonName().setMiddleName(StringUtils.capitalize(patient.getPersonName().getMiddleName()));
		}
	}
}
