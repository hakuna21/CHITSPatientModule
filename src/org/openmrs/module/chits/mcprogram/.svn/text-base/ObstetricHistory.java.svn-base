package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts;
import org.openmrs.module.chits.obs.RepeatingObs;

/**
 * Encapsulates a single obstetric history record either based on an existing record or containing new observations.
 * <p>
 * This class extends {@link RepeatingObs} since it contains a repeating set of obstetric history detail observations.
 * 
 * @author Bren
 */
public class ObstetricHistory extends RepeatingObs<ObstetricHistoryDetail> {
	public ObstetricHistory() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCObstetricHistoryConcepts.OBSTETRIC_HISTORY, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public ObstetricHistory(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCObstetricHistoryConcepts.values());
	}

	@Override
	public CachedConceptId getChildrenConcept() {
		return MCObstetricHistoryDetailsConcepts.OBSTETRIC_HISTORY_DETAILS;
	}
}
