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
import org.openmrs.module.chits.Constants.VisitConcepts;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.mcprogram.DangerSigns;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDangerSignsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricExamination;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPrenatalVisitRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.mcprogram.ObstetricExamination;
import org.openmrs.module.chits.mcprogram.PrenatalVisitRecord;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.controller.BaseUpdatePatientConsultDataController;
import org.openmrs.module.chits.web.controller.genconsults.StartPatientConsultController;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.module.chits.webservices.rest.resource.MCPrenatalEncounterRecordResource.MCPrenatalEncounterRecord;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

/**
 * Concrete {@link EncounterRecordResource} for the MC Prenatal module.
 * 
 * @author Bren
 */
@Resource("MCPrenatalEncounterRecord")
@Handler(supports = { MCPrenatalEncounterRecord.class }, order = -10)
public class MCPrenatalEncounterRecordResource extends EncounterRecordResource<MCPrenatalEncounterRecord> {
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
	protected MCPrenatalEncounterRecord load(Patient patient) {
		// prepare the Maternal Care record for this patient
		final MCPrenatalEncounterRecord record = new MCPrenatalEncounterRecord();

		// enrollment status for postnatal forms: must be enrolled in maternal care and baby not yet delivered
		if (!Functions.isInProgram(patient, ProgramConcepts.MATERNALCARE) || !MaternalCareUtil.isCurrentlyEnrolledAndBabyNotYetDelivered(patient)) {
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
	protected void persist(Patient patient, MCPrenatalEncounterRecord encRecord) throws ParseException {
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

		// add prenatal visit to encounter
		addPrenatalRecord(enc, mcProgramObs, data);

		// add vital signs to encounter (only the blood pressure)
		addVitalSigns(enc, data);

		// save the encounter ONLY IF there were any observations created
		if (!enc.getObs().isEmpty()) {
			getEncounterService().saveEncounter(enc);
		}

		// overwrite actual data with whatever the caller sent us
		final MCPrenatalEncounterRecord updatedRecord = load(patient);
		data.clear();
		data.putAll(updatedRecord.getRecord());
	}

	/**
	 * Add the prenatal visit to the encounter.
	 * 
	 * @param enc
	 *            The encounter to add to
	 * @param data
	 *            The data submitted by the caller
	 * @throws ParseException
	 */
	private void addPrenatalRecord(final Encounter enc, final Obs mcProgramObs, final Map<String, String> data) throws ParseException {
		// prepare patient context before utilizing the GroupObs
		final Patient patient = enc.getPatient();
		ObsUtil.PATIENT_CONTEXT.set(patient);
		Object value;

		// initialize a prenatal visit record
		final PrenatalVisitRecord visitRecord = new PrenatalVisitRecord();

		// store visit information
		final Obs visitDateObs = visitRecord.getMember(MCPrenatalVisitRecordConcepts.VISIT_DATE);
		visitDateObs.setValueText(data.get(MCPrenatalVisitRecordConcepts.VISIT_DATE.getConceptName()));
		visitDateObs.setValueDatetime(Context.getDateFormat().parse(visitDateObs.getValueText()));

		// set 'Visit Type' to default value
		final Obs visitTypeObs = visitRecord.getMember(MCPrenatalVisitRecordConcepts.VISIT_TYPE);
		final List<Concept> visitTypes = Functions.answers(MCPrenatalVisitRecordConcepts.VISIT_TYPE);
		if (visitTypes.size() > 0) {
			visitTypeObs.setValueCoded(visitTypes.get(0));
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(visitTypeObs);
		}

		// store 'nutritionally at risk' flag
		final Obs nutAtRiskObs = visitRecord.getMember(MCPrenatalVisitRecordConcepts.NUTRITIONALLY_AT_RISK);
		nutAtRiskObs.setValueText(data.get(MCPrenatalVisitRecordConcepts.NUTRITIONALLY_AT_RISK.getConceptName()));
		nutAtRiskObs.setValueCoded("true".equalsIgnoreCase(nutAtRiskObs.getValueText()) ? Functions.trueConcept() : Functions.falseConcept());

		// set danger signs and new medical conditions
		final DangerSigns dangerSigns = visitRecord.getDangerSigns();
		final CachedConceptId[] dangerSignConcepts = new CachedConceptId[] { MCDangerSignsConcepts.SEVERE_HEADACHE, //
				MCDangerSignsConcepts.FEVER, //
				MCDangerSignsConcepts.DIZZINESS, //
				MCDangerSignsConcepts.BLURRING_OF_VISION, //
				MCDangerSignsConcepts.VAGINAL_BLEEDING, //
				MCDangerSignsConcepts.EDEMA };
		for (CachedConceptId dangerSignConcept : dangerSignConcepts) {
			// store the coded value
			dangerSigns.getMember(dangerSignConcept).setValueCoded(
					"true".equalsIgnoreCase(data.get(dangerSignConcept.getConceptName())) ? Functions.trueConcept() : Functions.falseConcept());
		}

		// update boolean coded values
		BaseUpdatePatientConsultDataController.setNonTrueObsToFalse(visitRecord.getObs(), MCPrenatalVisitRecordConcepts.NUTRITIONALLY_AT_RISK);
		BaseUpdatePatientConsultDataController.setNonTrueObsToFalse(dangerSigns.getObs(), dangerSignConcepts);

		// store obstetric examination
		final ObstetricExamination obExam = visitRecord.getObstetricExamination();

		// store location of fetal heart tones
		final Obs fhrLocationObs = obExam.getMember(MCObstetricExamination.FHR_LOCATION);
		fhrLocationObs.setValueText(data.get(MCObstetricExamination.FHR_LOCATION.getConceptName()));
		fhrLocationObs.setValueCoded(Functions.conceptByIdOrName(fhrLocationObs.getValueText()));

		// store fetal heart rate
		final Obs fetalHRObs = obExam.getMember(MCObstetricExamination.FHR);
		value = data.get(MCObstetricExamination.FHR.getConceptName());
		fetalHRObs.setValueText(value != null ? value.toString() : null);
		try {
			fetalHRObs.setValueNumeric(Double.parseDouble(fetalHRObs.getValueText()));
		} catch (Exception ex) {
			// value error? just ignore and let the text retain the value
		}

		// store fundic height
		final Obs fundicHeightObs = obExam.getMember(MCObstetricExamination.FUNDIC_HEIGHT);
		value = data.get(MCObstetricExamination.FUNDIC_HEIGHT.getConceptName());
		fundicHeightObs.setValueText(value != null ? value.toString() : null);
		try {
			fundicHeightObs.setValueNumeric(Double.parseDouble(fundicHeightObs.getValueText()));
		} catch (Exception ex) {
			// value error? just ignore and let the text retain the value
		}

		// store fetal presentation
		final Obs fetalPresentationObs = obExam.getMember(MCObstetricExamination.FETAL_PRESENTATION);
		fetalPresentationObs.setValueText(data.get(MCObstetricExamination.FETAL_PRESENTATION.getConceptName()));
		fetalPresentationObs.setValueCoded(Functions.conceptByIdOrName(fetalPresentationObs.getValueText()));

		// store audit information and make this entry a member of the current maternal program obs
		visitRecord.storePersonAndAudit(patient);
		mcProgramObs.addGroupMember(visitRecord.getObs());

		// add items for saving to encounter
		enc.addObs(visitRecord.getObs());
		enc.addObs(mcProgramObs);
	}

	/**
	 * Add the vital signs to the encounter.
	 * 
	 * @param enc
	 *            The encounter to add to
	 * @param data
	 *            The data submitted by the caller
	 * @throws ParseException
	 */
	static void addVitalSigns(final Encounter enc, final Map<String, String> data) throws ParseException {
		// prepare patient context before utilizing the GroupObs
		final Patient patient = enc.getPatient();

		// add blood pressure (but only if both are present)
		if (data.containsKey(VisitConcepts.SBP.getConceptName()) && data.containsKey(VisitConcepts.DBP.getConceptName())) {
			// create the 'parent' observation and set all other observations to be members of it
			final Obs vitalSignsObs = ObsUtil.newObs(Functions.concept(VisitConcepts.VITAL_SIGNS), patient);
			vitalSignsObs.setObsDatetime(Context.getDateFormat().parse(data.get(MCPrenatalVisitRecordConcepts.VISIT_DATE.getConceptName())));
			vitalSignsObs.setValueCoded(vitalSignsObs.getConcept());
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(vitalSignsObs);

			// add Systolic blood presure
			final Obs sbpObs = ObsUtil.newObs(Functions.concept(VisitConcepts.SBP), patient);
			sbpObs.setValueText(data.get(VisitConcepts.SBP.getConceptName()).toString());
			try {
				sbpObs.setValueNumeric(Double.parseDouble(sbpObs.getValueText()));
			} catch (Exception ex) {
				// value error? just ignore and let the text retain the value
			}

			// add Diastolic blood presure
			final Obs dbpObs = ObsUtil.newObs(Functions.concept(VisitConcepts.DBP), patient);
			dbpObs.setValueText(data.get(VisitConcepts.DBP.getConceptName()).toString());
			try {
				dbpObs.setValueNumeric(Double.parseDouble(dbpObs.getValueText()));
			} catch (Exception ex) {
				// value error? just ignore and let the text retain the value
			}

			// add vital signs members
			vitalSignsObs.addGroupMember(sbpObs);
			vitalSignsObs.addGroupMember(dbpObs);

			// add all to encounter
			enc.addObs(vitalSignsObs);
			enc.addObs(sbpObs);
			enc.addObs(dbpObs);
		}
	}

	/**
	 * MC Prenatal record resource (No specialized methods).
	 * <p>
	 * Here is a sample pre-natal MC record in JSON format: <code><pre>
	 *      {
	 *        "fetal presentation":"breech presentation",
	 *        "location of fetal heart tones":"right upper quadrant, location of fetal heart tones",
	 *        "fetal heart rate (beats/minute)":13,
	 *        "fundal height (cm)":12,
	 *        "DIASTOLIC BLOOD PRESSURE":80,
	 *        "SYSTOLIC BLOOD PRESSURE":120,
	 *        "nutritionally at risk":true,
	 *        "current date":"07/06/2012",
	 *        "Severe Headache":true,
	 *        "dizziness":true,
	 *        "Blurring of Vision":true,
	 *        "Vaginal Bleeding":true,
	 *        "Fever":true,
	 *        "Edema":true
	 *      }
	 * </pre></code>
	 * 
	 * @author Bren
	 */
	public static class MCPrenatalEncounterRecord extends EncounterRecordResource.EncounterRecord {
	}
}
