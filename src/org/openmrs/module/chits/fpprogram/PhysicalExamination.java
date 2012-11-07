package org.openmrs.module.chits.fpprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPhysicalExaminationConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Physical examination first entered during registration.
 * 
 * @author Bren
 */
public class PhysicalExamination extends GroupObs {
	public PhysicalExamination() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(FPPhysicalExaminationConcepts.PHYSICAL_EXAMINATION, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public PhysicalExamination(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(FPPhysicalExaminationConcepts.values());
	}
}
