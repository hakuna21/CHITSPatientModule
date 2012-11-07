package org.openmrs.module.chits.webservices.rest.resource;

import java.text.ParseException;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedProgramConceptId;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidDateAdministeredConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidDoseType;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.mcprogram.TetanusServiceRecord;
import org.openmrs.module.chits.web.controller.genconsults.StartPatientConsultController;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.module.chits.webservices.rest.resource.MCTetanusServiceEncounterRecordResource.MCTetanusServiceEncounterRecord;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

/**
 * Concrete {@link EncounterRecordResource} for the MC Tetanus Toxoid Service module.
 * 
 * @author Bren
 */
@Resource("MCTetanusServiceEncounterRecord")
@Handler(supports = { MCTetanusServiceEncounterRecord.class }, order = -10)
public class MCTetanusServiceEncounterRecordResource extends EncounterRecordResource<MCTetanusServiceEncounterRecord> {
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
	protected MCTetanusServiceEncounterRecord load(Patient patient) {
		// prepare the Maternal Care record for this patient
		final MCTetanusServiceEncounterRecord record = new MCTetanusServiceEncounterRecord();

		// enrollment status for postnatal forms: must be enrolled in maternal care
		if (!Functions.isInProgram(patient, ProgramConcepts.MATERNALCARE)) {
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
	protected void persist(Patient patient, MCTetanusServiceEncounterRecord encRecord) throws ParseException {
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

		// add tetanus toxoid service record to encounter
		addTetanusRecord(enc, mcProgramObs, data);

		// save the encounter ONLY IF there were any observations created
		if (!enc.getObs().isEmpty()) {
			getEncounterService().saveEncounter(enc);
		}

		// overwrite actual data with whatever the caller sent us
		final MCTetanusServiceEncounterRecord updatedRecord = load(patient);
		data.clear();
		data.putAll(updatedRecord.getRecord());
	}

	/**
	 * Add the tetanus toxoid service record to the encounter.
	 * 
	 * @param enc
	 *            The encounter to add to
	 * @param data
	 *            The data submitted by the caller
	 * @throws ParseException
	 */
	private void addTetanusRecord(final Encounter enc, final Obs mcProgramObs, final Map<String, String> data) throws ParseException {
		// prepare patient context before utilizing the GroupObs
		final Patient patient = enc.getPatient();
		ObsUtil.PATIENT_CONTEXT.set(patient);

		// initialize a tetanu toxoid visit record
		final TetanusServiceRecord serviceRecord = new TetanusServiceRecord();

		// store the vaccine type in the parent observation
		final Obs vaccineObs = serviceRecord.getObs();
		vaccineObs.setValueText(data.get(TetanusToxoidRecordConcepts.VACCINE_TYPE.getConceptName()));
		vaccineObs.setValueCoded(Functions.conceptByIdOrName(vaccineObs.getValueText()));
		if (vaccineObs.getValueCoded() == null) {
			// invalid or no such vaccine type! skip this record without further action
			return;
		}

		// store visit type
		final Obs visitTypeObs = serviceRecord.getMember(TetanusToxoidRecordConcepts.VISIT_TYPE);
		visitTypeObs.setValueText(data.get(TetanusToxoidRecordConcepts.VISIT_TYPE.getConceptName()));
		visitTypeObs.setValueCoded(Functions.conceptByIdOrName(visitTypeObs.getValueText()));

		// store date given
		final Obs dateAdministeredObs = serviceRecord.getMember(TetanusToxoidRecordConcepts.DATE_ADMINISTERED);
		dateAdministeredObs.setValueText(data.get(TetanusToxoidRecordConcepts.DATE_ADMINISTERED.getConceptName()));
		dateAdministeredObs.setValueDatetime(Context.getDateFormat().parse(dateAdministeredObs.getValueText()));

		// store service source
		final Obs serviceSourceObs = serviceRecord.getMember(TetanusToxoidRecordConcepts.SERVICE_SOURCE);
		serviceSourceObs.setValueText(data.get(TetanusToxoidRecordConcepts.SERVICE_SOURCE.getConceptName()));
		serviceSourceObs.setValueCoded(Functions.conceptByIdOrName(serviceSourceObs.getValueText()));

		// store remarks
		final Obs remarksObs = serviceRecord.getMember(TetanusToxoidRecordConcepts.REMARKS);
		remarksObs.setValueText(data.get(TetanusToxoidRecordConcepts.REMARKS.getConceptName()));

		// update the concept of the 'date administered' based on the vaccine type selected
		final Concept vaccineType = vaccineObs.getValueCoded();
		if (vaccineType == Functions.concept(TetanusToxoidDoseType.TT1)) {
			dateAdministeredObs.setConcept(Functions.concept(TetanusToxoidDateAdministeredConcepts.TT1));
		} else if (vaccineType == Functions.concept(TetanusToxoidDoseType.TT2)) {
			dateAdministeredObs.setConcept(Functions.concept(TetanusToxoidDateAdministeredConcepts.TT2));
		} else if (vaccineType == Functions.concept(TetanusToxoidDoseType.TT3)) {
			dateAdministeredObs.setConcept(Functions.concept(TetanusToxoidDateAdministeredConcepts.TT3));
		} else if (vaccineType == Functions.concept(TetanusToxoidDoseType.TT4)) {
			dateAdministeredObs.setConcept(Functions.concept(TetanusToxoidDateAdministeredConcepts.TT4));
		} else if (vaccineType == Functions.concept(TetanusToxoidDoseType.TT5)) {
			dateAdministeredObs.setConcept(Functions.concept(TetanusToxoidDateAdministeredConcepts.TT5));
		}

		// store audit information and make this entry a member of the current maternal program obs
		serviceRecord.storePersonAndAudit(patient);
		mcProgramObs.addGroupMember(serviceRecord.getObs());

		// add items for saving to encounter
		enc.addObs(serviceRecord.getObs());
		enc.addObs(mcProgramObs);
	}

	/**
	 * MC Tetanus Toxoid Service record resource (No specialized methods).
	 * <p>
	 * Here is a sample pre-natal MC record in JSON format: <code><pre>
	 *      {
	 *        "vaccine type, tetanus vaccination, maternal care services": "tetanus toxoid 1",
	 *        "tetanus toxoid first dose, date administered": "01/01/1999",
	 *        "visit type, tetanus vaccination, maternal care services": "home",
	 *        "service source, tetanus vaccination, maternal care services": "Buying at Own Expense",
	 *        "remarks, tetanus vaccination, maternal care services": "Tetanus shots"
	 *      }
	 * </pre></code>
	 * 
	 * @author Bren
	 */
	public static class MCTetanusServiceEncounterRecord extends EncounterRecordResource.EncounterRecord {
	}
}
