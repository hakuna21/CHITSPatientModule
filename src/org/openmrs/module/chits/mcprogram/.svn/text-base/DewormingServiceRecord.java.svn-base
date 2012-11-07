package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.DewormingConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCServiceRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCServiceTypes;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Encapsulates details of a Deworming service record.
 * 
 * @author Bren
 */
public class DewormingServiceRecord extends GroupObs {
	/** Stores the 'administered by' value */
	private User administeredBy;

	public DewormingServiceRecord() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCServiceRecordConcepts.SERVICE_TYPE, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public DewormingServiceRecord(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set 'administeredBy' and 'dateAdministered' for existing records only
		if (obs.getId() != null && obs.getId() != 0) {
			administeredBy = obs.getCreator();
		} else {
			// initialize new observations for Deworming
			getObs().setValueCoded(Functions.concept(MCServiceTypes.DEWORMING));
		}

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCServiceRecordConcepts.values());

		// add the 'visit type' for deworming
		super.setConcepts(DewormingConcepts.values());
	}

	public User getAdministeredBy() {
		return administeredBy;
	}

	public void setAdministeredBy(User administeredBy) {
		this.administeredBy = administeredBy;
	}
}
