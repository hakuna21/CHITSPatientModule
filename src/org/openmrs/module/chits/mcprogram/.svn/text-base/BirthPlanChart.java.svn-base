package org.openmrs.module.chits.mcprogram;

import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCBirthPlanConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCChildsNeedsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMothersNeedsConcepts;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Encapsulates the birth plan chart either based on an existing record or containing new observations.
 * <p>
 * NOTE: The maternal care program observation should only contain one birth plan chart.
 * 
 * @author Bren
 */
public class BirthPlanChart extends GroupObs {
	/** Mother's needs group */
	private final MothersNeeds mothersNeeds;

	/** Child's needs group */
	private final ChildsNeeds childsNeeds;

	public BirthPlanChart() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCBirthPlanConcepts.BIRTH_PLAN, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public BirthPlanChart(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// load the mother's needs
		final Obs mothersNeedsObs = Functions.observation(obs, MCMothersNeedsConcepts.MOTHERS_NEEDS);
		if (mothersNeedsObs == null) {
			// initialize blank 'mothers needs' and add as a group member
			mothersNeeds = new MothersNeeds();
			getObs().addGroupMember(mothersNeeds.getObs());
		} else {
			// initialize with existing 'mothers needs' observation which is already a member of this observation
			mothersNeeds = new MothersNeeds(mothersNeedsObs);
		}

		// load the child's needs
		final Obs childsNeedsObs = Functions.observation(obs, MCChildsNeedsConcepts.CHILDS_NEEDS);
		if (childsNeedsObs == null) {
			// initialize blank 'childs needs' and add as a group member
			childsNeeds = new ChildsNeeds();
			getObs().addGroupMember(childsNeeds.getObs());
		} else {
			// initialize with existing 'childs needs' observation which is already a member of this observation
			childsNeeds = new ChildsNeeds(childsNeedsObs);
		}

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCBirthPlanConcepts.values());
	}

	@Override
	public void storePersonAndAudit(Person person) {
		// dispatch to self
		super.storePersonAndAudit(person);

		// dispatch to member elements
		mothersNeeds.storePersonAndAudit(person);
		childsNeeds.storePersonAndAudit(person);
	}

	public MothersNeeds getMothersNeeds() {
		return mothersNeeds;
	}

	public ChildsNeeds getChildsNeeds() {
		return childsNeeds;
	}
}
