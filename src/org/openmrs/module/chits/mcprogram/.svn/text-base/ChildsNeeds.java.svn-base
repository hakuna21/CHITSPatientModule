package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCChildsNeedsConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates the Child's Needs data.
 * 
 * @author Bren
 */
public class ChildsNeeds extends GroupObs {
	public ChildsNeeds() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCChildsNeedsConcepts.CHILDS_NEEDS, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public ChildsNeeds(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCChildsNeedsConcepts.values());
	}
}
