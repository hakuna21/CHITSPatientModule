package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumEventsConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates the post-partum events checklist.
 * 
 * @author Bren
 */
public class PostPartumEvents extends GroupObs {
	public PostPartumEvents() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCPostPartumEventsConcepts.POSTPARTUM_CHECKLIST, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public PostPartumEvents(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCPostPartumEventsConcepts.values());
	}
}
