package org.openmrs.module.chits.fpprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPelvicExaminationConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Pelvic examination first entered during registration.
 * 
 * @author Bren
 */
public class PelvicExamination extends GroupObs {
	public PelvicExamination() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(FPPelvicExaminationConcepts.PELVIC_EXAMINATION, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public PelvicExamination(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(FPPelvicExaminationConcepts.values());
	}
}
