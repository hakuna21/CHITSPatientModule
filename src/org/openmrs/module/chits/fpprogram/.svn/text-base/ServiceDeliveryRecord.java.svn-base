package org.openmrs.module.chits.fpprogram;

import java.util.Date;

import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPServiceDeliveryRecordConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Service Delivery Record concepts (these are children of the FamilyPlanningMethod).
 * 
 * @author Bren
 */
public class ServiceDeliveryRecord extends GroupObs {
	public ServiceDeliveryRecord() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(FPServiceDeliveryRecordConcepts.SERVICE_DELIVERY_RECORD, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public ServiceDeliveryRecord(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(FPServiceDeliveryRecordConcepts.values());
	}

	public Date getDateAdministered() {
		final Obs dateAdministeredObs = getMember(FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED);
		return dateAdministeredObs != null ? dateAdministeredObs.getValueDatetime() : null;
	}
}
