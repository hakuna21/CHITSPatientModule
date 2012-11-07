package org.openmrs.module.chits.mcprogram;

import java.util.Date;

import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumEventsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumVisitRecordConcepts;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Encapsulates a single post partum visit record either based on an existing record or containing new observations.
 * 
 * @author Bren
 */
public class PostPartumVisitRecord extends GroupObs {
	/** Events checklist Danger signs associated with the post-partum visit record */
	private final PostPartumEvents postPartumEvents;

	public PostPartumVisitRecord() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCPostPartumVisitRecordConcepts.POSTPARTUM_VISIT_RECORD, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public PostPartumVisitRecord(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// load the postpartum events checklist
		final Obs postPartumEventsObs = Functions.observation(obs, MCPostPartumEventsConcepts.POSTPARTUM_CHECKLIST);
		if (postPartumEventsObs == null) {
			// initialize 'blank danger signs' and add as a group member
			postPartumEvents = new PostPartumEvents();
			getObs().addGroupMember(postPartumEvents.getObs());
		} else {
			// initialize with existing 'danger signs' observation which is already a member of this observation
			postPartumEvents = new PostPartumEvents(postPartumEventsObs);
		}

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCPostPartumVisitRecordConcepts.values());
	}

	@Override
	public void storePersonAndAudit(Person person) {
		// dispatch to self
		super.storePersonAndAudit(person);

		// dispatch to member elements
		postPartumEvents.storePersonAndAudit(person);
	}

	/**
	 * Convenience method to extract the visit date from the obseration map.
	 * 
	 * @return The visit date
	 */
	public Date getVisitDate() {
		final Obs visitDateObs = getMember(MCPostPartumVisitRecordConcepts.VISIT_DATE);
		return visitDateObs != null ? visitDateObs.getValueDatetime() : null;
	}

	public PostPartumEvents getPostPartumEvents() {
		return postPartumEvents;
	}
}
