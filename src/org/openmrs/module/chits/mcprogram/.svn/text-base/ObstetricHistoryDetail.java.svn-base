package org.openmrs.module.chits.mcprogram;

import java.util.List;

import org.openmrs.Obs;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPregnancyOutcomeConcepts;
import org.openmrs.module.chits.obs.RepeatingObs;

/**
 * Encapsulates a single obstetric history detail record either based on an existing record or containing new observations.
 * <p>
 * This class extends {@link RepeatingObs} since it contains a repeating set of pregnancy outcome observations.
 * 
 * @author Bren
 */
public class ObstetricHistoryDetail extends RepeatingObs<PregnancyOutcome> {
	public ObstetricHistoryDetail() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCObstetricHistoryDetailsConcepts.OBSTETRIC_HISTORY_DETAILS, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public ObstetricHistoryDetail(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCObstetricHistoryDetailsConcepts.values());
	}

	/**
	 * Synonym for 'children': 'outcomes'.
	 * 
	 * @return The children of this concept
	 */
	public List<PregnancyOutcome> getOutcomes() {
		return super.getChildren();
	}

	/**
	 * Return the homogenous children concept type.
	 */
	@Override
	public CachedConceptId getChildrenConcept() {
		return MCPregnancyOutcomeConcepts.PREGNANCY_OUTCOME;
	}
}
