package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDangerSignsConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates the danger signs.
 * 
 * @author Bren
 */
public class DangerSigns extends GroupObs {
	public DangerSigns() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCDangerSignsConcepts.DANGER_SIGNS, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public DangerSigns(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCDangerSignsConcepts.values());
	}
}
