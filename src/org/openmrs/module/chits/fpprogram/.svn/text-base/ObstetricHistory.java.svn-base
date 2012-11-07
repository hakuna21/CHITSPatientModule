package org.openmrs.module.chits.fpprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPObstetricHistoryConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Obstetric history first entered during registration.
 * 
 * @author Bren
 */
public class ObstetricHistory extends GroupObs {
	public ObstetricHistory() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(FPObstetricHistoryConcepts.OBSTETRIC_HISTORY, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public ObstetricHistory(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(FPObstetricHistoryConcepts.values());
	}
}
