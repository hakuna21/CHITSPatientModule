package org.openmrs.module.chits.validator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.obs.GroupObs.FieldPath;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates {@link PatientConsultEntryForm} instances.
 * 
 * @author Bren
 */
public class PatientConsultEntryFormValidator implements Validator, Constants {
	/** Logger instance */
	private Log log = LogFactory.getLog(getClass());

	/** Parser for decimal / numeric numbers */
	private static final NumberFormat NUMERIC_PARSER = new DecimalFormat();

	/**
	 * Specifies the different types of date validation.
	 * 
	 * @author Bren
	 */
	public static enum DateValidationType {
		ON_OR_AFTER_BIRTHDATE, // indicates a date must be on or after the patient's birthdate
		MUST_NOT_BE_IN_FUTURE, // indicate the date cannot be in the future
		MUST_BE_IN_FUTURE; // indicates the date must be in the future
	}

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
		return PatientConsultEntryForm.class.isAssignableFrom(c);
	}

	/**
	 * Validates the given {@link PatientConsultEntryForm}.
	 * 
	 * @param obj
	 *            The {@link PatientConsultEntryForm} to validate.
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
		final PatientConsultEntryForm form = (PatientConsultEntryForm) obj;

		Obs sbp = null;
		Obs dbp = null;

		// validate 'patient' in form
		final Map<Integer, Obs> obsMap = form.getObservationMap();
		if (obsMap != null) {
			// validate all observations
			for (Integer conceptId : obsMap.keySet()) {
				final Obs obs = obsMap.get(conceptId);
				final ConceptDatatype datatype = obs.getConcept().getDatatype();
				if (datatype.isNumeric() && !StringUtils.isEmpty(obs.getValueText())) {
					// validate numeric data type
					validateNumeric(errors, null, obs);
				} else if (datatype.isCoded() || datatype.isBoolean()) {
					// coded values do not require validation; however, it may be ideal to test that the coded
					// value is a valid answer to the concept question but at this point, that form of validation
					// is not necessary since the possible values are restricted by the JSP code through drop down lists

					// set the coded concept's name into the valueText field
					setValueCodedIntoValueText(obs);
				} else if (datatype.isDate() && !StringUtils.isEmpty(obs.getValueText())) {
					// validate that the date specified is valid
					validateDate(errors, null, obs);
				}

				if (VisitConcepts.SBP.getConceptId().equals(conceptId)) {
					sbp = obs;
				} else if (VisitConcepts.DBP.getConceptId().equals(conceptId)) {
					dbp = obs;
				}
			}
		}

		// special validation for blood pressure (if present)
		if (sbp != null && dbp != null) {
			if (!StringUtils.isEmpty(sbp.getValueText()) && StringUtils.isEmpty(dbp.getValueText())) {
				// invalid if only DBP is specified
				errors.rejectValue("bloodPressure", "chits.error.invalid.blood.pressure");
			} else if (StringUtils.isEmpty(sbp.getValueText()) && !StringUtils.isEmpty(dbp.getValueText())) {
				// invalid if only SBP is specified
				errors.rejectValue("bloodPressure", "chits.error.invalid.blood.pressure");
			} else if (!StringUtils.isEmpty(form.getBloodPressure()) && form.getBloodPressure().indexOf("/") == -1) {
				// invalid if no separator between SBP and DBP values
				errors.rejectValue("bloodPressure", "chits.error.invalid.blood.pressure");
			}
		}
	}

	/**
	 * Convenience method to validate observation map using a field path to describe the spring path.
	 */
	public static void validateRequiredFields(PatientConsultEntryForm form, FieldPath path, Errors errors, CachedConceptId... requiredFields) {
		validateRequiredFields(form, path.toObsMap(), errors, requiredFields);
	}

	/**
	 * Generic validation for required fields based on observation concept datatype:
	 * <ul>
	 * <li>Coded types: valueCoded must be non-null
	 * <li>All other types: valueText must be non-empty
	 * </ul>
	 * 
	 * @param form
	 *            The form containing the observation map
	 * @param errors
	 *            For storing the errors (if any)
	 * @param requiredFields
	 *            Cached concept types of the observations that require validation
	 */
	@SuppressWarnings("unchecked")
	public static void validateRequiredFields(PatientConsultEntryForm form, String path, Errors errors, CachedConceptId... requiredFields) {
		final Map<Integer, Obs> observationMap;
		try {
			observationMap = path != null ? ((Map<Integer, Obs>) PropertyUtils.getProperty(form, path)) : form.getObservationMap();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		for (CachedConceptId requiredField : requiredFields) {
			final Integer requiredFieldConceptId = requiredField.getConceptId();
			final Obs obs = observationMap.get(requiredFieldConceptId);
			final ConceptDatatype datatype = obs.getConcept().getDatatype();
			if ((datatype.isCoded() || datatype.isBoolean()) && obs.getValueCoded() == null) {
				// value is required
				errors.rejectValue((path != null ? path : "observationMap") + "[" + requiredFieldConceptId + "].valueCoded", "chits.error.required.field");
			} else if (StringUtils.isEmpty(obs.getValueText())) {
				// value is required
				errors.rejectValue((path != null ? path : "observationMap") + "[" + requiredFieldConceptId + "].valueText", "chits.error.required.field");
			}
		}
	}

	/**
	 * Utility method to return whether an observation's value is empty based on its datatype.
	 * 
	 * @param form
	 * @param path
	 * @param errors
	 * @param requiredField
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean fieldEmpty(PatientConsultEntryForm form, String path, Errors errors, CachedConceptId requiredField) {
		final Map<Integer, Obs> observationMap;
		try {
			observationMap = path != null ? ((Map<Integer, Obs>) PropertyUtils.getProperty(form, path)) : form.getObservationMap();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		final Integer requiredFieldConceptId = requiredField.getConceptId();
		final Obs obs = observationMap.get(requiredFieldConceptId);
		final ConceptDatatype datatype = obs.getConcept().getDatatype();
		if ((datatype.isCoded() || datatype.isBoolean()) && obs.getValueCoded() == null) {
			// value is empty
			return true;
		} else if (StringUtils.isEmpty(obs.getValueText())) {
			// value is empty
			return true;
		} else {
			// unknown data type: can't determine if this field is empty!
			return false;
		}
	}

	/**
	 * Convenience method to validate observation map using a field path to describe the spring path.
	 */
	public static void validateValidPastDates(PatientConsultEntryForm form, FieldPath path, BindingResult errors, CachedConceptId... pastDateConcepts) {
		validateValidPastDates(form, path.toObsMap(), errors, pastDateConcepts);
	}

	/**
	 * Validate dates that must be in the past and after the patient's birthdate.
	 * 
	 * @param form
	 *            The form containing the observation map
	 * @param errors
	 *            For storing the errors (if any)
	 * @param pastDateConcepts
	 *            Cached concept types of the observations that require validation
	 */
	@SuppressWarnings("unchecked")
	public static void validateValidPastDates(PatientConsultEntryForm form, String path, BindingResult errors, CachedConceptId... pastDateConcepts) {
		final Map<Integer, Obs> observationMap;
		try {
			observationMap = path != null ? ((Map<Integer, Obs>) PropertyUtils.getProperty(form, path)) : form.getObservationMap();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		for (CachedConceptId pastDates : pastDateConcepts) {
			final Obs obs = observationMap.get(pastDates.getConceptId());
			final String fieldExpr = (path != null ? path : "observationMap") + "[" + pastDates.getConceptId() + "].valueText";

			// date value must not be in future and must be on or after the patient's birthdate
			PatientConsultEntryFormValidator.validateDateValue(form, obs, fieldExpr, errors, DateValidationType.MUST_NOT_BE_IN_FUTURE,
					DateValidationType.ON_OR_AFTER_BIRTHDATE);
		}
	}

	/**
	 * Sets the coded value's concept name into the 'valueText' field -- this is not a necessary step, however, it is convenient to have the name of the concept
	 * directly inside the Obs record when manually querying the database for viewing purposes, albeit less efficient in terms of storage
	 * 
	 * @param obs
	 */
	public static void setValueCodedIntoValueText(Obs obs) {
		final Concept value = obs.getValueCoded();
		if (value != null) {
			// set the value text to be the name of the concept (this is for convenience purposes only
			// so that querying the Obs table manually shows the name of the coded concept answer)
			ConceptName name = value.getName(Constants.ENGLISH, true);
			name = (name == null) ? value.getName() : name;

			if (name != null) {
				obs.setValueText(name.getName());
			} else {
				if (value.equals(Functions.trueConcept())) {
					// for some reason, we can't seem to get a name from this concept, so we'll explicitly set one
					obs.setValueText("True");
				} else if (value.equals(Functions.falseConcept())) {
					// for some reason, we can't seem to get a name from this concept, so we'll explicitly set one
					obs.setValueText("False");
				} else {
					// unable to get a usable name!
					obs.setValueText("#" + value.getConceptId());
				}
			}
		} else {
			// no coded value saved
			obs.setValueText(null);
		}
	}

	/**
	 * Validate the numeric bounds of the observation.
	 * 
	 * @param errors
	 * @param obs
	 */
	private static void validateNumeric(Errors errors, String path, Obs obs) {
		// load the numeric concept
		final ConceptNumeric cn = Context.getConceptService().getConceptNumeric(obs.getConcept().getConceptId());

		// the spring field expression
		String fieldExpr = (path != null ? path : "observationMap") + "[" + cn.getConceptId() + "].valueText";
		if (Constants.VisitConcepts.SBP.getConceptId().equals(cn.getConceptId()) || Constants.VisitConcepts.DBP.getConceptId().equals(cn.getConceptId())) {
			// special case for blood pressure:
			fieldExpr = "bloodPressure";
		}

		// parse the text value as a numeric
		try {
			// parse the user entered data as a number
			double value = NUMERIC_PARSER.parse(obs.getValueText().trim()).doubleValue();

			// store the value into the 'numeric' field
			obs.setValueNumeric(value);
		} catch (ParseException pe) {
			// value is not a valid number!
			errors.rejectValue(fieldExpr, "chits.error.invalid.value");
			return;
		}

		// If the number is higher than the absolute range, raise an error
		if (cn.getHiAbsolute() != null && cn.getHiAbsolute() < obs.getValueNumeric()) {
			errors.rejectValue(fieldExpr, "chits.error.outOfRange.high", //
					new Object[] { cn.getHiAbsolute() }, "Value must be less than or equal to {0}");
		}

		// If the number is lower than the absolute range, raise an error as well
		if (cn.getLowAbsolute() != null && cn.getLowAbsolute() > obs.getValueNumeric()) {
			errors.rejectValue(fieldExpr, "chits.error.outOfRange.low", //
					new Object[] { cn.getLowAbsolute() }, "Value must be greater than or equal to {0}");
		}
	}

	/**
	 * Validate the date value of the observation.
	 * 
	 * @param errors
	 * @param obs
	 */
	private static void validateDate(Errors errors, String path, Obs obs) {
		// load the numeric concept
		final Concept cn = obs.getConcept();

		// the spring field expression
		String fieldExpr = (path != null ? path : "observationMap") + "[" + cn.getConceptId() + "].valueText";

		// parse the text value as a date
		try {
			// parse the user entered data as a number
			String date = obs.getValueText().trim();

			// special case: treat value of 'yyyy' as valid input and set the month and day to 01/01
			if (date.matches("\\d\\d\\d\\d")) {
				date = "01/01/" + date;
			}

			final Date value = Context.getDateFormat().parse(date);

			// store the value into the 'numeric' field
			obs.setValueDatetime(value);
		} catch (ParseException pe) {
			// value is not a valid date!
			errors.rejectValue(fieldExpr, "chits.error.invalid.value");
			return;
		}
	}

	/**
	 * Validates the given dateValue of the {@link Obs} in the form's observation map corresponding to the given date concept if there are currently no other
	 * errors for that date according to the given date validation types.
	 * 
	 * @param form
	 *            The form containing the form observations map and the patient
	 * @param dateConcept
	 *            The cached concept of the Obs to lookup in the observations map
	 * @param errors
	 *            binding results
	 * @param validationTypes
	 *            The type of validations to perform
	 */
	public static void validateDateValue(PatientConsultEntryForm form, CachedConceptId dateConcept, BindingResult errors, DateValidationType... validationTypes) {
		final Integer conceptId = dateConcept.getConceptId();
		final String field = "observationMap[" + conceptId + "].valueText";
		final Obs dateObs = form.getObservationMap().get(conceptId);
		validateDateValue(form, dateObs, field, errors, validationTypes);
	}

	/**
	 * Validates the given dateValue of the {@link Obs} in field if there are currently no other errors for that date according to the given date validation
	 * types.
	 * 
	 * @param form
	 *            The form containing the form observations map and the patient
	 * @param dateObs
	 *            The observation containing the valueDate
	 * @param errors
	 *            binding results
	 * @param validationTypes
	 *            The type of validations to perform
	 */
	public static void validateDateValue(PatientConsultEntryForm form, Obs dateObs, String field, BindingResult errors, DateValidationType... validationTypes) {
		final Date value = dateObs != null ? dateObs.getValueDatetime() : null;

		if (value != null && !errors.hasFieldErrors(field) && validationTypes != null) {
			for (DateValidationType dvt : validationTypes) {
				switch (dvt) {
				case MUST_BE_IN_FUTURE:
					if (DateUtil.stripTime(value).before(DateUtil.stripTime(new Date()))) {
						errors.rejectValue(field, "chits.error.date.in.past");
					}

					break;
				case MUST_NOT_BE_IN_FUTURE:
					if (DateUtil.stripTime(value).after(DateUtil.stripTime(new Date()))) {
						errors.rejectValue(field, "chits.error.date.in.future");
					}

					break;
				case ON_OR_AFTER_BIRTHDATE:
					// verify that the date is after the patient's birthdate
					final Patient patient = form.getPatient();
					if (patient == null || patient.getBirthdate() == null || DateUtil.stripTime(value).before(DateUtil.stripTime(patient.getBirthdate()))) {
						errors.rejectValue(field, "chits.error.date.before.patients.birthdate");
					}

					break;
				}
			}
		}
	}

	/**
	 * Convenience method to validate observation map using a field path to describe the spring path.
	 */
	public static void validateObservationMap(PatientConsultEntryForm form, FieldPath path, BindingResult errors) {
		validateObservationMap(form, path.toObsMap(), errors);
	}

	@SuppressWarnings("unchecked")
	public static void validateObservationMap(PatientConsultEntryForm form, String path, BindingResult errors) {
		final Map<Integer, Obs> obsMap;
		try {
			obsMap = path != null ? ((Map<Integer, Obs>) PropertyUtils.getProperty(form, path)) : form.getObservationMap();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		// perform standard validation
		// set default observation date/time
		final Date now = new Date();
		if (obsMap != null) {
			// validate all observations
			for (Integer conceptId : obsMap.keySet()) {
				final Obs obs = obsMap.get(conceptId);
				final ConceptDatatype datatype = obs.getConcept().getDatatype();
				if (datatype.isNumeric()) {
					if (!StringUtils.isEmpty(obs.getValueText())) {
						// validate numeric data type
						validateNumeric(errors, path, obs);
					} else {
						// no data entered, so clear out the numeric value
						obs.setValueNumeric(null);
					}
				} else if (datatype.isCoded() || datatype.isBoolean()) {
					// coded values do not require validation; however, it may be ideal to test that the coded
					// value is a valid answer to the concept question but at this point, that form of validation
					// is not necessary since the possible values are restricted by the JSP code through drop down lists

					// set the coded concept's name into the valueText field
					setValueCodedIntoValueText(obs);
				} else if (datatype.isDate()) {
					if (!StringUtils.isEmpty(obs.getValueText())) {
						// validate that the date specified is valid
						validateDate(errors, path, obs);
					} else {
						// no data entered, so clear out the date value
						obs.setValueDatetime(null);
					}
				}

				// ensure that the correct patient and encounter is associated
				// with this observation and that a default date/time is set
				obs.setPerson(form.getPatient());
				obs.setEncounter(form.getEncounter());

				if (obs.getObsDatetime() == null) {
					obs.setObsDatetime(now);
				}
			}
		}
	}

	/**
	 * Cleans up the Person attributes before persisting them.
	 * 
	 * @param person
	 *            The Person to clear the attributes of
	 */
	public static void cleanAttributes(Person person) {
		Iterator<PersonAttribute> attribIT = person.getAttributes().iterator();
		while (attribIT.hasNext()) {
			final PersonAttribute attrib = attribIT.next();
			if (StringUtils.isEmpty(attrib.getValue())) {
				// blank attributes can be cleared out!
				attrib.setPerson(null);
				attribIT.remove();
				continue;
			}

			if (attrib.getId() == null) {
				// add required attribute values
				attrib.setCreator(Context.getAuthenticatedUser());
				attrib.setDateCreated(new Date());
				attrib.setPerson(person);
				attrib.setUuid(UUID.randomUUID().toString());
				attrib.setVoided(Boolean.FALSE);
			} else {
				// update attribute modification info
				attrib.setChangedBy(Context.getAuthenticatedUser());
				attrib.setDateChanged(new Date());
				attrib.setVoided(Boolean.FALSE);
			}
		}
	}
}
