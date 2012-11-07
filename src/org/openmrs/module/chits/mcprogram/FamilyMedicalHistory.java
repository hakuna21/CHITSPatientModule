package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCFamilyMedicalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMedicalHistoryConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates the medical history observations for the patient's family.
 * 
 * @author Bren
 */
public class FamilyMedicalHistory extends GroupObs {
	public FamilyMedicalHistory() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCFamilyMedicalHistoryConcepts.FAMILY_MEDICAL_HISTORY, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public FamilyMedicalHistory(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts (NOTE: we re-use the MCMedicalHistoryConcepts values)
		super.setConceptsExceptFirst(MCMedicalHistoryConcepts.values());
	}
}
