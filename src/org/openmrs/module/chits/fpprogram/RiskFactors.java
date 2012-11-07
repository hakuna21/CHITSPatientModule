package org.openmrs.module.chits.fpprogram;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPRiskFactorsConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Risk factors first entered during registration.
 * 
 * @author Bren
 */
public class RiskFactors extends GroupObs {
	public RiskFactors() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(FPRiskFactorsConcepts.RISK_FACTORS, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public RiskFactors(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(FPRiskFactorsConcepts.values());
	}
}
