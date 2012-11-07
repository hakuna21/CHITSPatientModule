package org.openmrs.module.chits.webservices.rest.resource;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.CachedProgramConceptId;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.BreastFeedingConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServiceTypes;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServicesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareVaccinesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.DewormingServiceConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.FerrousSulfateServiceConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.VaccinationConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.VitaminAServiceConcepts;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.controller.genconsults.StartPatientConsultController;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.module.chits.webservices.rest.resource.ECCDEncounterRecordResource.ECCDEncounterRecord;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

/**
 * Concrete {@link EncounterRecordResource} for the ECCD module.
 * 
 * @author Bren
 */
@Resource("ECCDEncounterRecord")
@Handler(supports = { ECCDEncounterRecord.class }, order = -10)
public class ECCDEncounterRecordResource extends EncounterRecordResource<ECCDEncounterRecord> {
	/**
	 * This resource is for the ECCD program.
	 */
	@Override
	protected CachedProgramConceptId getProgram() {
		return ProgramConcepts.CHILDCARE;
	}

	@PropertyGetter("version")
	public static Long getVersion(EncounterRecord delegate) {
		return new Long(delegate.getVersion());
	}

	@PropertySetter("version")
	public static void setVersion(EncounterRecord delegate, Number version) {
		delegate.setVersion(version != null ? version.longValue() : 0);
	}

	/**
	 * Populates the given patient's {@link EncounterRecord}. If the patient is no longer enrolled in the program, the 'enrolled' flag should be set to 'false'.
	 * 
	 * @param patient
	 *            The patient to fill-in the record of
	 * @param record
	 *            The record to fill-in
	 */
	@Override
	protected ECCDEncounterRecord load(Patient patient) {
		// prepare the ECCD record for this patient
		final ECCDEncounterRecord record = new ECCDEncounterRecord();

		// get record data storage map
		final Map<String, String> data = record.getRecord();

		if (!Functions.isInProgram(patient, ProgramConcepts.CHILDCARE)) {
			// indicate that the patient is not (or no longer) enrolled
			record.setEnrolled(false);

			// don't populate any more data
			return record;
		}

		// set the 'enrolled' flag
		record.setEnrolled(true);

		// store the antigens administered
		final List<Obs> antigensAdministered = Functions.observations(patient, VaccinationConcepts.ANTIGEN);
		for (CachedConceptId antigen : ChildCareVaccinesConcepts.values()) {
			if (antigen == ChildCareVaccinesConcepts.OTHERS) {
				// don't include the 'others'
				continue;
			}

			// if antigen has been administered, add the date administered to the map
			@SuppressWarnings("unchecked")
			final List<Obs> matches = Functions.filterByCodedValue(antigensAdministered, antigen);
			if (!matches.isEmpty()) {
				// get the last entry (latest)
				final Obs antigenObs = matches.get(matches.size() - 1);

				// get the 'date administered'
				final Obs dateAdministered = Functions.observation(antigenObs.getObsGroup(), VaccinationConcepts.DATE_ADMINISTERED);

				// sanity check: 'dateAdministered' should have a valid value
				if (dateAdministered != null && dateAdministered.getValueDatetime() != null) {
					// add the entry to the record data
					data.put(antigen.getConceptName(), Context.getDateFormat().format(dateAdministered.getValueDatetime()));
				}
			}
		}

		// store breast feeding information
		for (CachedConceptId month : BreastFeedingConcepts.values()) {
			if (month == BreastFeedingConcepts.BREASTFEEDING_INFO) {
				// skip the group parent concept
				continue;
			}

			final Obs breastFedOnMonthObs = Functions.observation(patient, month);
			if (breastFedOnMonthObs != null && breastFedOnMonthObs.getValueNumeric() != null) {
				if (breastFedOnMonthObs.getValueNumeric() == 0) {
					// not exclusively breast fed on this month
					data.put(month.getConceptName(), "false");
				} else {
					// exclusively breast fed on this month
					data.put(month.getConceptName(), "true");
				}
			}
		}

		// send back the populated record
		return record;
	}

	/**
	 * Saves the encounter record into the database and updates the encRecord to reflect the changes.
	 * 
	 * @param encRecord
	 *            Going in, this contains the values to be persisted. Going out, this should reflect the updated values.
	 */
	protected void persist(Patient patient, ECCDEncounterRecord encRecord) throws ParseException {
		// get record data storage map
		final Map<String, String> data = encRecord.getRecord();

		if (!Functions.isInProgram(patient, ProgramConcepts.CHILDCARE)) {
			// indicate that the patient is not (or no longer) enrolled
			encRecord.setEnrolled(false);

			// clear out record data
			data.clear();

			// nothing more to do
			return;
		}

		// set the 'enrolled' flag
		encRecord.setEnrolled(true);

		// prepare an 'encounter' instance to save any updates
		final Encounter enc = StartPatientConsultController.newEncounter(patient);

		// add administered vaccines to encounter
		addAdministeredVaccines(enc, data);

		// add administered Vitamin A service to the encounter
		addAdministeredVitaminAService(enc, data);

		// add administered Deworming service to the encounter
		addAdministeredDewormingService(enc, data);

		// add administered Iron Supplementation service to the encounter
		addAdministeredIronSupplementationService(enc, data);

		// add breastfeeding information to encounter
		addBreastfeedingInfo(enc, data);

		// save the encounter ONLY IF there were any observations created
		if (!enc.getObs().isEmpty()) {
			getEncounterService().saveEncounter(enc);
		}

		// overwrite actual data with whatever the caller sent us
		final ECCDEncounterRecord updatedRecord = load(patient);
		data.putAll(updatedRecord.getRecord());
	}

	/**
	 * Adds the breastfeeding information to the encounter.
	 * 
	 * @param enc
	 *            The enconter to add to
	 * @param data
	 *            The data submitted by the caller
	 */
	private void addBreastfeedingInfo(final Encounter enc, final Map<String, String> data) {
		// lazy-load the breastfeeding information parent observation
		Obs breastFeedingInfoParentObs = null;
		final Patient patient = enc.getPatient();

		// store breast feeding information
		for (CachedConceptId month : BreastFeedingConcepts.values()) {
			if (month == BreastFeedingConcepts.BREASTFEEDING_INFO) {
				// skip the group parent concept
				continue;
			}

			// determine if the value was specified in the record
			final String exclusivelyBreastFedOnMonth = data.containsKey(month.getConceptName()) ? data.get(month.getConceptName()).toString() : null;
			if (exclusivelyBreastFedOnMonth == null) {
				// no data specified in record, nothing to update
				continue;
			}

			// allow update only if no observation exists yet
			Obs breastFedOnMonthObs = Functions.observation(patient, month);
			if (breastFedOnMonthObs == null) {
				// initializing the parent observation
				if (breastFeedingInfoParentObs == null) {
					breastFeedingInfoParentObs = Functions.observation(patient, BreastFeedingConcepts.BREASTFEEDING_INFO);
					if (breastFeedingInfoParentObs == null) {
						// create a new parent 'delivery information' observation group
						breastFeedingInfoParentObs = ObsUtil.newObs(Functions.concept(BreastFeedingConcepts.BREASTFEEDING_INFO), patient);
						breastFeedingInfoParentObs.setValueCoded(Functions.concept(BreastFeedingConcepts.BREASTFEEDING_INFO));
						PatientConsultEntryFormValidator.setValueCodedIntoValueText(breastFeedingInfoParentObs);
						enc.addObs(breastFeedingInfoParentObs);
					}
				}

				// setup the observation for the 'breast fed on month' and add as a group member to the parent observation and encounter
				breastFedOnMonthObs = ObsUtil.newObs(Functions.concept(month), patient);
				breastFedOnMonthObs.setValueCoded(Functions.concept(BreastFeedingConcepts.BREASTFEEDING_INFO));
				breastFedOnMonthObs.setValueNumeric("true".equalsIgnoreCase(exclusivelyBreastFedOnMonth) ? 1.0 : 0.0);
				PatientConsultEntryFormValidator.setValueCodedIntoValueText(breastFedOnMonthObs);
				enc.addObs(breastFedOnMonthObs);
				breastFeedingInfoParentObs.addGroupMember(breastFedOnMonthObs);
			}
		}
	}

	/**
	 * Add the administered vaccines to the encounter.
	 * 
	 * @param enc
	 *            The encounter to add to
	 * @param data
	 *            The data submitted by the caller
	 * @throws ParseException
	 */
	private void addAdministeredVaccines(final Encounter enc, final Map<String, String> data) throws ParseException {
		// persist the antigens administered
		final Patient patient = enc.getPatient();
		final List<Obs> antigensAdministered = Functions.observations(patient, VaccinationConcepts.ANTIGEN);
		for (CachedConceptId antigen : ChildCareVaccinesConcepts.values()) {
			if (antigen == ChildCareVaccinesConcepts.OTHERS) {
				// don't include the 'others'
				continue;
			}

			// get date administered for saving
			final String dateAdministered = data.containsKey(antigen.getConceptName()) ? data.get(antigen.getConceptName()).toString() : null;
			if (dateAdministered == null) {
				// no data specified in record, nothing to update
				continue;
			}

			// allow update only if antigen not yet administered
			if (Functions.filterByCodedValue(antigensAdministered, antigen).isEmpty()) {
				// store the 'creator' as the currently logged-in user (the one synching)
				final Obs vaccinationObsGroup = ObsUtil.newObs(Functions.concept(VaccinationConcepts.CHILDCARE_VACCINATION), patient);
				vaccinationObsGroup.setValueCoded(vaccinationObsGroup.getConcept());
				PatientConsultEntryFormValidator.setValueCodedIntoValueText(vaccinationObsGroup);
				vaccinationObsGroup.setCreator(Context.getAuthenticatedUser());

				// store the antigen
				final Obs antigenObs = ObsUtil.newObs(Functions.concept(VaccinationConcepts.ANTIGEN), patient);
				antigenObs.setValueCoded(Functions.concept(antigen));
				PatientConsultEntryFormValidator.setValueCodedIntoValueText(antigenObs);

				// store the 'date administered' value
				final Obs dateAdministeredObs = ObsUtil.newObs(Functions.concept(VaccinationConcepts.DATE_ADMINISTERED), patient);
				dateAdministeredObs.setValueDatetime(Context.getDateFormat().parse(dateAdministered));
				dateAdministeredObs.setValueText(dateAdministered);

				// set health facility to the default
				final List<Concept> healthFacilities = Functions.answers(VaccinationConcepts.HEALTH_FACILITY);
				final Obs healthFacilityObs = ObsUtil.newObs(Functions.concept(VaccinationConcepts.HEALTH_FACILITY), patient);
				healthFacilityObs.setValueCoded(!healthFacilities.isEmpty() ? healthFacilities.get(0) : null);
				PatientConsultEntryFormValidator.setValueCodedIntoValueText(healthFacilityObs);

				// setup the vaccination observation and add to encounter for saving
				vaccinationObsGroup.addGroupMember(antigenObs);
				vaccinationObsGroup.addGroupMember(dateAdministeredObs);
				vaccinationObsGroup.addGroupMember(healthFacilityObs);

				// UpdateVaccinationRecordsController adds all observations including the group members to the encounter
				enc.addObs(vaccinationObsGroup);
				enc.addObs(antigenObs);
				enc.addObs(dateAdministeredObs);
				enc.addObs(healthFacilityObs);
			}
		}
	}

	/**
	 * Add the administered service to the encounter.
	 * 
	 * @param enc
	 *            The encounter to add to
	 * @param data
	 *            The data submitted by the caller
	 * @throws ParseException
	 */
	private void addAdministeredVitaminAService(final Encounter enc, final Map<String, String> data) throws ParseException {
		// persist the service rendered
		final Patient patient = enc.getPatient();

		if (data.containsKey(ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION.getConceptName())) {
			// setup the service record parent observation
			final Obs serviceTypeObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.CHILDCARE_SERVICE_TYPE), patient);
			serviceTypeObs.setValueCoded(Functions.concept(ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION));
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(serviceTypeObs);

			// parse out date administered
			final Obs dateAdministeredObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.DATE_ADMINISTERED), patient);
			dateAdministeredObs.setValueText(data.get(ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION.getConceptName()));
			dateAdministeredObs.setValueDatetime(Context.getDateFormat().parse(dateAdministeredObs.getValueText()));

			// parse out dosage
			final Obs dosageObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.DOSAGE), patient);
			dosageObs.setValueText(data.get(VitaminAServiceConcepts.DOSAGE.getConceptName()));
			dosageObs.setValueCoded(Functions.conceptByIdOrName(dosageObs.getValueText()));

			// parse out remarks
			final Obs remarksObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.REMARKS), patient);
			remarksObs.setValueText(data.get(VitaminAServiceConcepts.REMARKS.getConceptName()));
			remarksObs.setValueCoded(Functions.conceptByIdOrName(remarksObs.getValueText()));

			// parse out service source
			final Obs svcSourceObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.SERVICE_SOURCE), patient);
			svcSourceObs.setValueText(data.get(VitaminAServiceConcepts.SERVICE_SOURCE.getConceptName()));
			svcSourceObs.setValueCoded(Functions.conceptByIdOrName(svcSourceObs.getValueText()));

			// prep observation group
			serviceTypeObs.addGroupMember(dateAdministeredObs);
			serviceTypeObs.addGroupMember(dosageObs);
			serviceTypeObs.addGroupMember(remarksObs);
			serviceTypeObs.addGroupMember(svcSourceObs);

			// add to encounter
			enc.addObs(serviceTypeObs);
			enc.addObs(dateAdministeredObs);
			enc.addObs(dosageObs);
			enc.addObs(remarksObs);
			enc.addObs(svcSourceObs);

			// remove fields from the data record so that the client can add another service
			data.remove(ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION.getConceptName());
			data.remove(VitaminAServiceConcepts.DOSAGE.getConceptName());
			data.remove(VitaminAServiceConcepts.REMARKS.getConceptName());
			data.remove(VitaminAServiceConcepts.SERVICE_SOURCE.getConceptName());
		}
	}

	/**
	 * Add the administered service to the encounter.
	 * 
	 * @param enc
	 *            The encounter to add to
	 * @param data
	 *            The data submitted by the caller
	 * @throws ParseException
	 */
	private void addAdministeredDewormingService(final Encounter enc, final Map<String, String> data) throws ParseException {
		// persist the service rendered
		final Patient patient = enc.getPatient();

		if (data.containsKey(ChildCareServiceTypes.DEWORMING.getConceptName())) {
			// setup the service record parent observation
			final Obs serviceTypeObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.CHILDCARE_SERVICE_TYPE), patient);
			serviceTypeObs.setValueCoded(Functions.concept(ChildCareServiceTypes.DEWORMING));
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(serviceTypeObs);

			// parse out date administered
			final Obs dateAdministeredObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.DATE_ADMINISTERED), patient);
			dateAdministeredObs.setValueText(data.get(ChildCareServiceTypes.DEWORMING.getConceptName()));
			dateAdministeredObs.setValueDatetime(Context.getDateFormat().parse(dateAdministeredObs.getValueText()));

			// parse out medication
			final Obs medicationObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.DOSAGE), patient);
			medicationObs.setValueText(data.get(DewormingServiceConcepts.MEDICATION.getConceptName()));
			medicationObs.setValueCoded(Functions.conceptByIdOrName(medicationObs.getValueText()));

			// add free-text remarks
			final Obs remarksObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.REMARKS), patient);
			remarksObs.setValueText(data.get(DewormingServiceConcepts.REMARKS.getConceptName()));

			// parse out service source
			final Obs svcSourceObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.SERVICE_SOURCE), patient);
			svcSourceObs.setValueText(data.get(DewormingServiceConcepts.SERVICE_SOURCE.getConceptName()));
			svcSourceObs.setValueCoded(Functions.conceptByIdOrName(svcSourceObs.getValueText()));

			// prep observation group
			serviceTypeObs.addGroupMember(dateAdministeredObs);
			serviceTypeObs.addGroupMember(medicationObs);
			serviceTypeObs.addGroupMember(remarksObs);
			serviceTypeObs.addGroupMember(svcSourceObs);

			// add to encounter
			enc.addObs(serviceTypeObs);
			enc.addObs(dateAdministeredObs);
			enc.addObs(medicationObs);
			enc.addObs(remarksObs);
			enc.addObs(svcSourceObs);

			// remove fields from the data record so that the client can add another service
			data.remove(ChildCareServiceTypes.DEWORMING.getConceptName());
			data.remove(DewormingServiceConcepts.MEDICATION.getConceptName());
			data.remove(DewormingServiceConcepts.REMARKS.getConceptName());
			data.remove(DewormingServiceConcepts.SERVICE_SOURCE.getConceptName());
		}
	}

	/**
	 * Add the administered service to the encounter.
	 * 
	 * @param enc
	 *            The encounter to add to
	 * @param data
	 *            The data submitted by the caller
	 * @throws ParseException
	 */
	private void addAdministeredIronSupplementationService(final Encounter enc, final Map<String, String> data) throws ParseException {
		// persist the service rendered
		final Patient patient = enc.getPatient();

		if (data.containsKey(ChildCareServiceTypes.FERROUS_SULFATE.getConceptName())) {
			// setup the service record parent observation
			final Obs serviceTypeObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.CHILDCARE_SERVICE_TYPE), patient);
			serviceTypeObs.setValueCoded(Functions.concept(ChildCareServiceTypes.FERROUS_SULFATE));
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(serviceTypeObs);

			// parse out date administered
			final Obs dateAdministeredObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.DATE_ADMINISTERED), patient);
			dateAdministeredObs.setValueText(data.get(ChildCareServiceTypes.FERROUS_SULFATE.getConceptName()));
			dateAdministeredObs.setValueDatetime(Context.getDateFormat().parse(dateAdministeredObs.getValueText()));

			// parse out medication
			final Obs medicationObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.DOSAGE), patient);
			medicationObs.setValueText(data.get(FerrousSulfateServiceConcepts.MEDICATION.getConceptName()));
			medicationObs.setValueCoded(Functions.conceptByIdOrName(medicationObs.getValueText()));

			// add free-text remarks
			final Obs remarksObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.REMARKS), patient);
			remarksObs.setValueText(data.get(FerrousSulfateServiceConcepts.REMARKS.getConceptName()));

			// parse out service source
			final Obs svcSourceObs = ObsUtil.newObs(Functions.concept(ChildCareServicesConcepts.SERVICE_SOURCE), patient);
			svcSourceObs.setValueText(data.get(FerrousSulfateServiceConcepts.SERVICE_SOURCE.getConceptName()));
			svcSourceObs.setValueCoded(Functions.conceptByIdOrName(svcSourceObs.getValueText()));

			// prep observation group
			serviceTypeObs.addGroupMember(dateAdministeredObs);
			serviceTypeObs.addGroupMember(medicationObs);
			serviceTypeObs.addGroupMember(remarksObs);
			serviceTypeObs.addGroupMember(svcSourceObs);

			// add to encounter
			enc.addObs(serviceTypeObs);
			enc.addObs(dateAdministeredObs);
			enc.addObs(medicationObs);
			enc.addObs(remarksObs);
			enc.addObs(svcSourceObs);

			// remove fields from the data record so that the client can add another service
			data.remove(ChildCareServiceTypes.FERROUS_SULFATE.getConceptName());
			data.remove(FerrousSulfateServiceConcepts.MEDICATION.getConceptName());
			data.remove(FerrousSulfateServiceConcepts.REMARKS.getConceptName());
			data.remove(FerrousSulfateServiceConcepts.SERVICE_SOURCE.getConceptName());
		}
	}

	/**
	 * ECCD record resource (No specialized methods).
	 * <p>
	 * Here is a sample ECCD record in JSON format: <code><pre>
	 *      {
	 *        "Vitamin A Supplementation"        : "07/06/2012",
	 *        "Ferrous Sulfate"                  : "07/06/2012",
	 *        "Deworming"                        : "07/06/2012",
	 *        "BCG (at birth) [EPI]"             : "07/06/2012",
	 *        "Hepatitis B 2 (6 wks) [EPI]"      : "07/06/2012",
	 *        "Hepatitis B 3 (14 wks) [EPI]"     : "07/06/2012",
	 *        "Hepatitis B 1 (at birth) [EPI]"   : "07/06/2012",
	 *        "OPV 1 (6 wks) [EPI]"              : "07/06/2012",
	 *        "OPV 2 (10 wks) [EPI]"             : "07/06/2012",
	 *        "OPV 3 (14 wks) [EPI]"             : "07/06/2012",
	 *        "DPT 1 (6 wks) [EPI]"              : "07/06/2012",
	 *        "DPT 2 (10 wks) [EPI]"             : "07/06/2012",
	 *        "DPT 3 (14 wks) [EPI]"             : "07/06/2012",
	 *        "Measles (9 mos) [EPI]"            : "07/06/2012",
	 *        "month 1, exclusive breastfeeding" : "true",
	 *        "month 2, exclusive breastfeeding" : "false",
	 *        "month 3, exclusive breastfeeding" : "true",
	 *        "month 4, exclusive breastfeeding" : "false",
	 *        "month 5, exclusive breastfeeding" : "true",
	 *        "month 6, exclusive breastfeeding" : "false"
	 *      }
	 * </pre></code> This would be represented in the 'record' as a key-value-pair of strings representing the concept names and the corresponding values.
	 * 
	 * @author Bren
	 */
	public static class ECCDEncounterRecord extends EncounterRecordResource.EncounterRecord {
	}
}
