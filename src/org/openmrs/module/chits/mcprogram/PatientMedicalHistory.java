package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMedicalHistoryConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates the past / present medical history observations for the patient.
 * 
 * @author Bren
 */
public class PatientMedicalHistory extends GroupObs {
	public PatientMedicalHistory() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCMedicalHistoryConcepts.MEDICAL_HISTORY, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public PatientMedicalHistory(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCMedicalHistoryConcepts.values());
	}
}
