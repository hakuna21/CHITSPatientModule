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
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumEventsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumVisitRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.mcprogram.PostPartumEvents;
import org.openmrs.module.chits.mcprogram.PostPartumVisitRecord;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.controller.BaseUpdatePatientConsultDataController;
import org.openmrs.module.chits.web.controller.genconsults.StartPatientConsultController;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.module.chits.webservices.rest.resource.MCPostnatalEncounterRecordResource.MCPostnatalEncounterRecord;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

/**
 * Concrete {@link EncounterRecordResource} for the MC Postnatal module.
 * 
 * @author Bren
 */
@Resource("MCPostnatalEncounterRecord")
@Handler(supports = { MCPostnatalEncounterRecord.class }, order = -10)
public class MCPostnatalEncounterRecordResource extends EncounterRecordResource<MCPostnatalEncounterRecord> {
	/**
	 * This resource is for the Maternal Care program.
	 */
	@Override
	protected CachedProgramConceptId getProgram() {
		return ProgramConcepts.MATERNALCARE;
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
	protected MCPostnatalEncounterRecord load(Patient patient) {
		// prepare the Maternal Care record for this patient
		final MCPostnatalEncounterRecord record = new MCPostnatalEncounterRecord();

		// enrollment status for postnatal forms: must be enrolled in maternal care and baby already delivered
		if (!Functions.isInProgram(patient, ProgramConcepts.MATERNALCARE) || MaternalCareUtil.isCurrentlyEnrolledAndBabyNotYetDelivered(patient)) {
			// indicate that the patient is not (or no longer) enrolled
			record.setEnrolled(false);

			// don't populate any more data
			return record;
		}

		// set the 'enrolled' flag
		record.setEnrolled(true);

		// Load Obsetric Score
		final Map<String, String> data = record.getRecord();
		data.put("obstetric-score", MaternalCareUtil.getObstetricScore(patient));

		// send back the populated record
		return record;
	}

	/**
	 * Saves the encounter record into the database and updates the encRecord to reflect the changes.
	 * 
	 * @param encRecord
	 *            Going in, this contains the values to be persisted. Going out, this should reflect the updated values.
	 */
	protected void persist(Patient patient, MCPostnatalEncounterRecord encRecord) throws ParseException {
		// get record data storage map
		final Map<String, String> data = encRecord.getRecord();

		// get the maternal care program obs
		final Obs mcProgramObs = MaternalCareUtil.getObsForActiveMaternalCareProgram(patient);
		if (mcProgramObs == null || !Functions.isInProgram(patient, ProgramConcepts.MATERNALCARE)) {
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

		// add postnatal visit to encounter
		addPostnatalRecord(enc, mcProgramObs, data);

		// add vital signs to encounter (only the blood pressure)
		MCPrenatalEncounterRecordResource.addVitalSigns(enc, data);

		// save the encounter ONLY IF there were any observations created
		if (!enc.getObs().isEmpty()) {
			getEncounterService().saveEncounter(enc);
		}

		// overwrite actual data with whatever the caller sent us
		final MCPostnatalEncounterRecord updatedRecord = load(patient);
		data.clear();
		data.putAll(updatedRecord.getRecord());
	}

	/**
	 * Add the postnatal visit to the encounter.
	 * 
	 * @param enc
	 *            The encounter to add to
	 * @param data
	 *            The data submitted by the caller
	 * @throws ParseException
	 */
	private void addPostnatalRecord(final Encounter enc, final Obs mcProgramObs, final Map<String, String> data) throws ParseException {
		// prepare patient context before utilizing the GroupObs
		final Patient patient = enc.getPatient();
		ObsUtil.PATIENT_CONTEXT.set(patient);

		// initialize a postpartum visit record
		final PostPartumVisitRecord visitRecord = new PostPartumVisitRecord();

		// store visit information
		final Obs visitDateObs = visitRecord.getMember(MCPostPartumVisitRecordConcepts.VISIT_DATE);
		visitDateObs.setValueText(data.get(MCPostPartumVisitRecordConcepts.VISIT_DATE.getConceptName()));
		visitDateObs.setValueDatetime(Context.getDateFormat().parse(visitDateObs.getValueText()));

		// set 'Visit Type' to default value
		final Obs visitTypeObs = visitRecord.getMember(MCPostPartumVisitRecordConcepts.VISIT_TYPE);
		final List<Concept> visitTypes = Functions.answers(MCPostPartumVisitRecordConcepts.VISIT_TYPE);
		if (visitTypes.size() > 0) {
			visitTypeObs.setValueCoded(visitTypes.get(0));
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(visitTypeObs);
		}

		// setup postpartum events
		final PostPartumEvents events = visitRecord.getPostPartumEvents();
		final CachedConceptId[] eventsConcepts = new CachedConceptId[] { MCPostPartumEventsConcepts.VAGINAL_INFECTION, //
				MCPostPartumEventsConcepts.VAGINAL_BLEEDING, //
				MCPostPartumEventsConcepts.FEVER_OVER_38, //
				MCPostPartumEventsConcepts.PALLOR, //
				MCPostPartumEventsConcepts.CORD_NORMAL };
		for (CachedConceptId eventsConcept : eventsConcepts) {
			// store the coded value
			events.getMember(eventsConcept).setValueCoded(
					"true".equalsIgnoreCase(data.get(eventsConcept.getConceptName())) ? Functions.trueConcept() : Functions.falseConcept());
		}

		// setup 'Breastfeeding initiated within one hour' flag
		final Obs breastFedWithinHourObs = visitRecord.getMember(MCPostPartumVisitRecordConcepts.BREASTFED_WITHIN_HOUR);
		breastFedWithinHourObs.setValueText(data.get(MCPostPartumVisitRecordConcepts.BREASTFED_WITHIN_HOUR.getConceptName()));
		breastFedWithinHourObs.setValueCoded("true".equalsIgnoreCase( //
				breastFedWithinHourObs.getValueText()) ? Functions.trueConcept() : Functions.falseConcept());

		// setup 'Baby still exlusively breastfeeding at time of consult' flag
		final Obs breastFedExclusivelyObs = visitRecord.getMember(MCPostPartumVisitRecordConcepts.BREASTFED_EXCLUSIVELY);
		breastFedExclusivelyObs.setValueText(data.get(MCPostPartumVisitRecordConcepts.BREASTFED_EXCLUSIVELY.getConceptName()));
		breastFedExclusivelyObs.setValueCoded("true".equalsIgnoreCase( //
				breastFedExclusivelyObs.getValueText()) ? Functions.trueConcept() : Functions.falseConcept());

		// update boolean coded values
		BaseUpdatePatientConsultDataController.setNonTrueObsToFalse(events.getObs(), eventsConcepts);
		BaseUpdatePatientConsultDataController.setNonTrueObsToFalse(visitRecord.getObs(), //
				MCPostPartumVisitRecordConcepts.BREASTFED_WITHIN_HOUR, //
				MCPostPartumVisitRecordConcepts.BREASTFED_EXCLUSIVELY);

		// set remarks
		final Obs remarksObs = visitRecord.getMember(MCPostPartumVisitRecordConcepts.REMARKS);
		remarksObs.setValueText(data.get(MCPostPartumVisitRecordConcepts.REMARKS.getConceptName()));

		// store audit information and make this entry a member of the current maternal program obs
		visitRecord.storePersonAndAudit(patient);
		mcProgramObs.addGroupMember(visitRecord.getObs());

		// add items for saving to encounter
		enc.addObs(visitRecord.getObs());
		enc.addObs(mcProgramObs);
	}

	/**
	 * MC Postnatal record resource (No specialized methods).
	 * <p>
	 * Here is a sample post-natal MC record in JSON format: <code><pre>
	 *      {
	 *        "baby's cord unremarkable postpartum":true,
	 *        "postpartum pallor":true,
	 *        "postpartum fever greater than 38 degrees celsius":true,
	 *        "postpartum vaginal bleeding":true,
	 *        "postpartum vaginal infection":true,
	 *        "DIASTOLIC BLOOD PRESSURE":80,
	 *        "SYSTOLIC BLOOD PRESSURE":120,
	 *        "current date":"07/06/2012",
	 *        "Breastfeeding initiated within one hour":true,
	 *        "Baby still exlusively breastfeeding at time of consult":true,
	 *        "remarks on post-partum checkup":"Blah blah blah!"
	 *      }
	 * </pre></code>
	 * 
	 * @author Bren
	 */
	public static class MCPostnatalEncounterRecord extends EncounterRecordResource.EncounterRecord {
	}
}
