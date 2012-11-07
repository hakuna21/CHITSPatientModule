package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPregnancyOutcomeConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates a single pregnancy outcome record either based on an existing record or containing new observations.
 * <p>
 * NOTE: The observation parent doubles as a link to the baby's patient record using the 'valueGroupId' to store the patientId representing the baby.
 * 
 * @author Bren
 */
public class PregnancyOutcome extends GroupObs {
	public PregnancyOutcome() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCPregnancyOutcomeConcepts.PREGNANCY_OUTCOME, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public PregnancyOutcome(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCPregnancyOutcomeConcepts.values());
	}

	/**
	 * Synonym for valueGroupId retrieves the patient ID of the baby's patient record.
	 * 
	 * @return The patient ID of the baby's patient record.
	 */
	public Integer getBabyPatientId() {
		return getObs().getValueGroupId();
	}

	/**
	 * Synonym for valueGroupId stores the patient ID of the baby's patient record.
	 * 
	 * @param babyPatientId
	 *            The patient ID of the baby's patient record
	 */
	public void setBabyPatientId(Integer babyPatientId) {
		getObs().setValueGroupId(babyPatientId);
	}
}
