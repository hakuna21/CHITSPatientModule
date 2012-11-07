package org.openmrs.module.chits.fpprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMedicalHistoryConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Medical history information first entered during registration.
 * 
 * @author Bren
 */
public class MedicalHistoryInformation extends GroupObs {
	public MedicalHistoryInformation() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(FPMedicalHistoryConcepts.MEDICAL_HISTORY, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public MedicalHistoryInformation(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(FPMedicalHistoryConcepts.values());
	}
}
