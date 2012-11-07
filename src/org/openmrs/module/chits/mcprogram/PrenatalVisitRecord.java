package org.openmrs.module.chits.mcprogram;

import java.util.Date;

import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDangerSignsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMedicalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricExamination;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPrenatalVisitRecordConcepts;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Encapsulates a single prenatal visit record either based on an existing record or containing new observations.
 * 
 * @author Bren
 */
public class PrenatalVisitRecord extends GroupObs {
	/** Obstetric examination and leopold's maneuver findings */
	private final ObstetricExamination obstetricExamination;

	/** Danger signs associated with the prenatal visit record */
	private final DangerSigns dangerSigns;

	/** Report of medical conditions */
	private final PatientMedicalHistory newMedicalConditions;

	public PrenatalVisitRecord() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCPrenatalVisitRecordConcepts.PRENATAL_VISIT_RECORD, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public PrenatalVisitRecord(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// load the danger signs
		final Obs dangerSignsObs = Functions.observation(obs, MCDangerSignsConcepts.DANGER_SIGNS);
		if (dangerSignsObs == null) {
			// initialize 'blank danger signs' and add as a group member
			dangerSigns = new DangerSigns();
			getObs().addGroupMember(dangerSigns.getObs());
		} else {
			// initialize with existing 'danger signs' observation which is already a member of this observation
			dangerSigns = new DangerSigns(dangerSignsObs);
		}

		// load the new medical conditions
		final Obs newMedicalConditionsObs = Functions.observation(obs, MCMedicalHistoryConcepts.MEDICAL_HISTORY);
		if (newMedicalConditionsObs == null) {
			// initialize blank 'new medical conditions' and add as group member
			newMedicalConditions = new PatientMedicalHistory();
			getObs().addGroupMember(newMedicalConditions.getObs());
		} else {
			// initialize with existing 'new medical conditions' observation which is already a member of this observation
			newMedicalConditions = new PatientMedicalHistory(newMedicalConditionsObs);
		}

		// load the obstetric examinations record
		final Obs obstetricExaminationObs = Functions.observation(obs, MCObstetricExamination.OBSTETRIC_EXAMINATION);
		if (obstetricExaminationObs == null) {
			// initialize blank 'obstetric examination' and add as group member
			obstetricExamination = new ObstetricExamination();
			getObs().addGroupMember(obstetricExamination.getObs());
		} else {
			// initialize with existing 'obstetric examination' observation which is already a member of this observation
			obstetricExamination = new ObstetricExamination(obstetricExaminationObs);
		}

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCPrenatalVisitRecordConcepts.values());
	}

	@Override
	public void storePersonAndAudit(Person person) {
		// dispatch to self
		super.storePersonAndAudit(person);

		// dispatch to member elements
		dangerSigns.storePersonAndAudit(person);
		newMedicalConditions.storePersonAndAudit(person);
		obstetricExamination.storePersonAndAudit(person);
	}

	/**
	 * Convenience method to extract the visit date from the obseration map.
	 * 
	 * @return The visit date
	 */
	public Date getVisitDate() {
		final Obs visitDateObs = getMember(MCPrenatalVisitRecordConcepts.VISIT_DATE);
		return visitDateObs != null ? visitDateObs.getValueDatetime() : null;
	}

	public ObstetricExamination getObstetricExamination() {
		return obstetricExamination;
	}

	public DangerSigns getDangerSigns() {
		return dangerSigns;
	}

	public PatientMedicalHistory getNewMedicalConditions() {
		return newMedicalConditions;
	}
}
