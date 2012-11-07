package org.openmrs.module.chits;

import java.text.DecimalFormat;

import org.openmrs.Patient;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPRegistrationPage;
import org.openmrs.module.chits.fpprogram.FamilyPlanningMethod;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.fpprogram.ServiceDeliveryRecord;

/**
 * Consult entry form for the family planning program.
 * 
 * @author Bren
 */
public class FamilyPlanningConsultEntryForm extends PatientConsultEntryForm {
	/** Decimal format: when entering numeric data, limit all fields to a maximum of 4 decimal places */
	public static final DecimalFormat FMT = new DecimalFormat("0.####");

	/** The patient's partner */
	private Patient partner;

	/** If specified, user is changing the partner reference */
	private Integer newPartnerId;

	/** If set, then no partner is specified */
	private boolean partnerNotSpecified;

	/** Flag indicates if the family planning program is already concluded for this patient */
	private Boolean programConcluded;

	/** The current registration page to display or submitted */
	private FPRegistrationPage page;

	/** When adding / viewing a family planning method record, this bean will contain the data */
	private FamilyPlanningMethod familyPlanningMethod;

	/** When adding / viewing a service delivery record, this bean will contain the data */
	private ServiceDeliveryRecord serviceDeliveryRecord;

	/** The main family planning program observation group for the current family planning record */
	private FamilyPlanningProgramObs fpProgramObs;

	public Patient getPartner() {
		return partner;
	}

	public void setPartner(Patient partner) {
		this.partner = partner;
	}

	public Integer getNewPartnerId() {
		return newPartnerId;
	}

	public void setNewPartnerId(Integer newPartnerId) {
		this.newPartnerId = newPartnerId;
	}

	public boolean isPartnerNotSpecified() {
		return partnerNotSpecified;
	}

	public void setPartnerNotSpecified(boolean partnerNotSpecified) {
		this.partnerNotSpecified = partnerNotSpecified;
	}

	public FPRegistrationPage getPage() {
		return page;
	}

	public void setPage(FPRegistrationPage page) {
		this.page = page;
	}

	public boolean isProgramConcluded() {
		if (programConcluded == null) {
			programConcluded = fpProgramObs.getPatientProgram().getDateCompleted() != null;
		}

		return programConcluded;
	}

	public void setProgramConcluded(boolean programConcluded) {
		this.programConcluded = programConcluded;
	}

	public FamilyPlanningProgramObs getFpProgramObs() {
		return fpProgramObs;
	}

	public void setFpProgramObs(FamilyPlanningProgramObs fpProgramObs) {
		this.fpProgramObs = fpProgramObs;

		// also store into the parent observation map for cascade saving
		getObservationMap().put(fpProgramObs.getObs().getConcept().getId(), fpProgramObs.getObs());
	}

	public FamilyPlanningMethod getFamilyPlanningMethod() {
		return familyPlanningMethod;
	}

	public void setFamilyPlanningMethod(FamilyPlanningMethod familyPlanningMethod) {
		this.familyPlanningMethod = familyPlanningMethod;
	}

	public ServiceDeliveryRecord getServiceDeliveryRecord() {
		return serviceDeliveryRecord;
	}

	public void setServiceDeliveryRecord(ServiceDeliveryRecord serviceDeliveryRecord) {
		this.serviceDeliveryRecord = serviceDeliveryRecord;
	}
}
