package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMenstrualHistoryConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates the menstrual history observations.
 * 
 * @author Bren
 */
public class MenstrualHistory extends GroupObs {
	public MenstrualHistory() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCMenstrualHistoryConcepts.MENSTRUAL_HISTORY, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public MenstrualHistory(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCMenstrualHistoryConcepts.values());
	}
}
