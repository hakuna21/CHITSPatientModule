package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPersonalHistoryConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates the personal history observations for the patient.
 * 
 * @author Bren
 */
public class PersonalHistory extends GroupObs {
	public PersonalHistory() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCPersonalHistoryConcepts.PERSONAL_HISTORY, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public PersonalHistory(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCPersonalHistoryConcepts.values());
	}
}
