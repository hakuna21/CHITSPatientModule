package org.openmrs.module.chits.mcprogram;

import java.util.Date;

import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDeliveryReportConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Encapsulates the delivery report record either based on an existing record or containing new observations.
 * <p>
 * This class contains an {@link ObstetricHistoryDetail} record for other details.
 * 
 * @author Bren
 */
public class DeliveryReport extends GroupObs {
	/** Contains the obstetric history detail containing the pregnancy outcomes */
	private final ObstetricHistoryDetail obstetricHistoryDetail;

	public DeliveryReport() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCDeliveryReportConcepts.DELIVERY_REPORT, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public DeliveryReport(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// load the final obstetric score
		final Obs obstetricHistoryDetailObs = Functions.observation(obs, MCObstetricHistoryDetailsConcepts.OBSTETRIC_HISTORY_DETAILS);
		if (obstetricHistoryDetailObs == null) {
			// initialize blank 'obstetric history detail' and add as a group member to contain the final obsetric score and pregnancy outcomes
			obstetricHistoryDetail = new ObstetricHistoryDetail();
			getObs().addGroupMember(obstetricHistoryDetail.getObs());
		} else {
			// initialize with existing 'obstetric history detail' observation which is already a member of this observation
			obstetricHistoryDetail = new ObstetricHistoryDetail(obstetricHistoryDetailObs);
		}

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCDeliveryReportConcepts.values());
	}
	
	public Date getDeliveryDate() {
		return getMember(MCDeliveryReportConcepts.DELIVERY_DATE).getValueDatetime();
	}

	@Override
	public void storePersonAndAudit(Person person) {
		// dispatch to self
		super.storePersonAndAudit(person);

		// dispatch to member elements
		obstetricHistoryDetail.storePersonAndAudit(person);
	}

	public ObstetricHistoryDetail getObstetricHistoryDetail() {
		return obstetricHistoryDetail;
	}
}
