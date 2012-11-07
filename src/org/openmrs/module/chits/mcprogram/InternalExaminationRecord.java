package org.openmrs.module.chits.mcprogram;

import java.util.Date;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCIERecordConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates a single internal examination record either based on an existing record or containing new observations.
 * 
 * @author Bren
 */
public class InternalExaminationRecord extends GroupObs {
	public InternalExaminationRecord() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCIERecordConcepts.INTERNAL_EXAMINATION, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public InternalExaminationRecord(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCIERecordConcepts.values());
	}

	/**
	 * Convenience method to extract the visit date from the observation map.
	 * 
	 * @return The visit date
	 */
	public Date getVisitDate() {
		final Obs visitDateObs = getMember(MCIERecordConcepts.VISIT_DATE);
		return visitDateObs != null ? visitDateObs.getValueDatetime() : null;
	}
}
