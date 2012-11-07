package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPatientConsultStatus;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MaternalCareProgramStates;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Encapsulates the patient consult status.
 * 
 * @author Bren
 */
public class PatientConsultStatus extends GroupObs {
	/** The status */
	private MaternalCareProgramStates status;

	public PatientConsultStatus() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCPatientConsultStatus.PATIENT_CONSULT_STATUS, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public PatientConsultStatus(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCPatientConsultStatus.values());

		// setup the 'status' based on the current observation value, if any
		final Obs statusObs = getMember(MCPatientConsultStatus.STATUS);
		if (statusObs != null && statusObs.getValueCoded() != null) {
			for (MaternalCareProgramStates test : MaternalCareProgramStates.values()) {
				if (statusObs.getValueCoded().getConceptId().equals(test.getConceptId())) {
					status = test;
				}
			}
		}
	}

	public MaternalCareProgramStates getStatus() {
		return status;
	}

	public void setStatus(MaternalCareProgramStates status) {
		this.status = status;

		// simultaneously set the observation's coded value for the given status
		getMember(MCPatientConsultStatus.STATUS).setValueCoded(status != null ? Functions.concept(status) : null);
		PatientConsultEntryFormValidator.setValueCodedIntoValueText(getMember(MCPatientConsultStatus.STATUS));
	}
}
