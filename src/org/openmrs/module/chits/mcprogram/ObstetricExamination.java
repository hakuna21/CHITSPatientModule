package org.openmrs.module.chits.mcprogram;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricExamination;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates the Obstetric examination and leopold's maneuver findings.
 * 
 * @author Bren
 */
public class ObstetricExamination extends GroupObs {
	public ObstetricExamination() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCObstetricExamination.OBSTETRIC_EXAMINATION, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public ObstetricExamination(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCObstetricExamination.values());
	}

	/**
	 * Returns true if any of the leopold's maneuver fields were filled in.
	 * 
	 * @return True if any of the leopold's maneuver fields were filled in, false otherwise.
	 */
	public boolean isLeopoldsManeuverPerformed() {
		final Obs fundalGripObs = getMember(MCObstetricExamination.FUNDAL_GRIP);
		final Obs umbilicalGripObs = getMember(MCObstetricExamination.UMBILICAL_GRIP);
		final Obs pawlicksGripObs = getMember(MCObstetricExamination.PAWLICKS_GRIP);
		final Obs pelvicGripObs = getMember(MCObstetricExamination.PELVIC_GRIP);

		// leopold's maneuver is performed if any of the fields were entered
		return (fundalGripObs != null && !StringUtils.isEmpty(fundalGripObs.getValueText())) //
				|| (umbilicalGripObs != null && !StringUtils.isEmpty(umbilicalGripObs.getValueText())) //
				|| (pawlicksGripObs != null && !StringUtils.isEmpty(pawlicksGripObs.getValueText())) //
				|| (pelvicGripObs != null && !StringUtils.isEmpty(pelvicGripObs.getValueText()));
	}
}