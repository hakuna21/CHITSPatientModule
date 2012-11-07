package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMothersNeedsConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates the Mother's Needs data.
 * 
 * @author Bren
 */
public class MothersNeeds extends GroupObs {
	public MothersNeeds() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCMothersNeedsConcepts.MOTHERS_NEEDS, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public MothersNeeds(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCMothersNeedsConcepts.values());
	}
}
