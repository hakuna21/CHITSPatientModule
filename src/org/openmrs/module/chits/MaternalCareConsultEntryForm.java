package org.openmrs.module.chits;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.chits.Constants.VisitConcepts;
import org.openmrs.module.chits.mcprogram.DewormingServiceRecord;
import org.openmrs.module.chits.mcprogram.InternalExaminationRecord;
import org.openmrs.module.chits.mcprogram.IronSupplementationServiceRecord;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDeliveryReportConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMedicalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCRegistrationPage;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MaternalCareProgramStates;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil.TetanusServiceInfo;
import org.openmrs.module.chits.mcprogram.ObstetricHistory;
import org.openmrs.module.chits.mcprogram.ObstetricHistoryDetail;
import org.openmrs.module.chits.mcprogram.PatientConsultStatus;
import org.openmrs.module.chits.mcprogram.PostPartumVisitRecord;
import org.openmrs.module.chits.mcprogram.PostpartumInternalExaminationRecord;
import org.openmrs.module.chits.mcprogram.PrenatalVisitRecord;
import org.openmrs.module.chits.mcprogram.TetanusServiceRecord;
import org.openmrs.module.chits.mcprogram.VitaminAServiceRecord;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Consult entry form for the maternal care program.
 * 
 * @author Bren
 */
public class MaternalCareConsultEntryForm extends PatientConsultEntryForm {
	/** Decimal format: when entering numeric data, limit all fields to a maximum of 4 decimal places */
	public static final DecimalFormat FMT = new DecimalFormat("0.####");

	/** Flag indicates if the maternal care program is already concluded for this patient */
	private Boolean programConcluded;

	/** The current registration page to display or submitted */
	private MCRegistrationPage page;

	/** When editing or creating a new obstetric history detail entry, this bean will contain the data */
	private ObstetricHistoryDetail obstetricHistoryDetail;

	/** When editing or creating a new prenatal visit record entry, this bean will contain the data */
	private PrenatalVisitRecord prenatalVisitRecord;

	/** When editing or creating a new internal examination record entry, this bean will contain the data */
	private InternalExaminationRecord internalExaminationRecord;

	/** When editing or creating a new post-partum internal examination record entry, this bean will contain the data */
	private PostpartumInternalExaminationRecord postpartumInternalExaminationRecord;

	/** When editing or creating a new post-partum visit record entry, this bean will contain the data */
	private PostPartumVisitRecord postPartumVisitRecord;

	/** When editing the patient consult status, this bean will contain the data */
	private PatientConsultStatus patientConsultStatus;

	/** When adding / viewing a tetanus toxoid service record, this bean will contain the data */
	private TetanusServiceRecord tetanusServiceRecord;

	/** When adding / viewing a vitamin a service record, this bean will contain the data */
	private VitaminAServiceRecord vitaminAServiceRecord;

	/** When adding / viewing an iron supplementation service record, this bean will contain the data */
	private IronSupplementationServiceRecord ironSupplementationServiceRecord;

	/** When adding / viewing a deworming service record, this bean will contain the data */
	private DewormingServiceRecord dewormingServiceRecord;

	/** The main maternal care program observation group for the current maternal program record */
	private MaternalCareProgramObs mcProgramObs;

	public MCRegistrationPage getPage() {
		return page;
	}

	public void setPage(MCRegistrationPage page) {
		this.page = page;
	}

	public boolean isProgramConcluded() {
		if (programConcluded == null) {
			programConcluded = mcProgramObs.getPatientProgram().getDateCompleted() != null;
		}

		return programConcluded;
	}

	/**
	 * 1.4.4 Returning to ACTIVE status: If coming from an ADMITTED status, the status of the Delivery Record is checked. If it has not yet been updated (no
	 * delivery date is entered), the form is displayed (refer to Section 2.1.1)
	 * 
	 * @return
	 */
	public boolean isDeliveryReportNeeded() {
		if (isProgramConcluded()) {
			// program already concluded, no need for delivery report
			return false;
		}

		final List<PatientConsultStatus> statuses = mcProgramObs.getAllPatientConsultStatus();
		if (statuses.size() > 1) {
			final PatientConsultStatus previous = statuses.get(statuses.size() - 2);
			final PatientConsultStatus current = statuses.get(statuses.size() - 1);

			// check if status was 'active' and coming from 'admitted'
			if (current.getStatus() == MaternalCareProgramStates.ACTIVE //
					&& previous.getStatus() == MaternalCareProgramStates.ADMITTED) {
				// if no delivery report has been created yet, then we need to ask the user to create one
				if (Functions.observation(mcProgramObs.getObs(), MCDeliveryReportConcepts.DELIVERY_REPORT) == null) {
					// no delivery report created yet: we need to pop-up the delivery report dialog
					return true;
				}
			}
		}

		// delivery report doesn't need to be displayed
		return false;
	}

	/**
	 * Calculates the postpartum end date as 6 months past the delivery date.
	 * <p>
	 * NOTE: In the absence of a delivery date, the EDC is used.
	 * 
	 * @return The end date of the postpartum stage
	 */
	public Date getPostpartumStageEndDate() {
		Date deliveryDate = null;
		if (Functions.observation(mcProgramObs.getObs(), MCDeliveryReportConcepts.DELIVERY_REPORT) != null) {
			final Obs deliveryDateObs = mcProgramObs.getDeliveryReport().getMember(MCDeliveryReportConcepts.DELIVERY_DATE);
			if (deliveryDateObs != null) {
				deliveryDate = deliveryDateObs.getValueDatetime();
			}
		}

		if (deliveryDate == null) {
			// use EDC
			deliveryDate = mcProgramObs.getEstimatedDateOfConfinement();
		}

		if (deliveryDate != null) {
			final Calendar sixMonthsAfterDeliveryDate = Calendar.getInstance();
			sixMonthsAfterDeliveryDate.setTime(DateUtil.stripTime(deliveryDate));
			sixMonthsAfterDeliveryDate.add(Calendar.MONTH, 6);

			// return the calculated postpartum stage end date
			return sixMonthsAfterDeliveryDate.getTime();
		}

		// postpartum stage end date is not available
		return null;
	}

	/**
	 * A system prompt to conclude the program happens when it is six (6) months past the delivery date.
	 * <p>
	 * In the absence of a delivery date, the EDC is used.
	 * 
	 * @return
	 */
	public boolean isOpenSystempPromptedConclusion() {
		if (isProgramConcluded()) {
			// program already concluded, no need for system conclusion prompt
			return false;
		}

		final Date postpartumStageEndDate = getPostpartumStageEndDate();
		if (postpartumStageEndDate != null) {
			final Date today = new Date();
			if (today.after(postpartumStageEndDate)) {
				// system prompted conclusion is needed
				return true;
			}
		}

		// system prompted conclusion not yet needed
		return false;
	}

	/**
	 * Calculates the risk factors based on the maternal care observation in the form.
	 * 
	 * @return A comma-delimited list of risk factors (from A to E)
	 */
	public String getRiskFactors() {
		// store risk factors in a list
		final List<String> riskFactors = new ArrayList<String>();

		if (getPatient() != null && mcProgramObs != null) {
			// check for risk factor 'a' using age at LMP
			final Obs lmpObs = mcProgramObs.getObstetricHistory().getMember(MCObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD);
			if (lmpObs != null && lmpObs.getValueDatetime() != null && getPatient().getBirthdate() != null) {
				final int age = DateUtil.yearsBetween(getPatient().getBirthdate(), lmpObs.getValueDatetime());
				if (age < 18 || age > 35) {
					riskFactors.add("A");
				}
			}

			// check for risk factor 'b' using patient's height
			final Obs heightObs = Functions.observation(getPatient(), VisitConcepts.HEIGHT_CM);
			if (heightObs != null && heightObs.getValueNumeric() != null) {
				if (heightObs.getValueNumeric() < 145) {
					riskFactors.add("B");
				}
			}

			// check for risk factor 'c' using patient obstetric history
			final ObstetricHistory obHistory = mcProgramObs.getObstetricHistory();
			final Obs gravidaObs = obHistory.getMember(MCObstetricHistoryConcepts.OBSTETRIC_SCORE_GRAVIDA);
			if (gravidaObs != null && gravidaObs.getValueNumeric() != null) {
				if (gravidaObs.getValueNumeric() >= 4) {
					riskFactors.add("C");
				}
			}

			// check for risk factors 'd' using registration data
			final Concept yes = Functions.trueConcept();
			if (yes.equals(obHistory.getMember(MCObstetricHistoryConcepts.HISTORY_PREV_CSECTION).getValueCoded())
					|| yes.equals(obHistory.getMember(MCObstetricHistoryConcepts.HISTORY_3OR_MORE_MISCARRIAGES).getValueCoded())
					|| yes.equals(obHistory.getMember(MCObstetricHistoryConcepts.HISTORY_OF_POSTPARTUM_HEMORRHAGE).getValueCoded())) {
				riskFactors.add("D");
			}

			if (anyRiskFactorsTypeE(mcProgramObs.getPatientMedicalHistory())) {
				// check 'past' medical history
				riskFactors.add("E");
			} else {
				// check medical history observed during this pregnancy
				for (PrenatalVisitRecord visit : mcProgramObs.getPrenatalVisits()) {
					if (anyRiskFactorsTypeE(visit.getNewMedicalConditions())) {
						riskFactors.add("E");
						break;
					}
				}
			}
		}

		if (riskFactors.isEmpty()) {
			return "none";
		} else {
			final String riskFactorsText = riskFactors.toString();
			return riskFactorsText.substring(1, riskFactorsText.length() - 1);
		}
	}

	/**
	 * Checks if any of the 'E' risk factors are present in the group obs.
	 * 
	 * @param grp
	 * @return
	 */
	private boolean anyRiskFactorsTypeE(GroupObs grp) {
		final Concept yes = Functions.trueConcept();

		// check if any of the 'E' risk factors are present
		for (CachedConceptId rfEConceptId : new CachedConceptId[] { MCMedicalHistoryConcepts.TUBERCULOSIS, //
				MCMedicalHistoryConcepts.HEART_DISEASE, //
				MCMedicalHistoryConcepts.DIABETES, //
				MCMedicalHistoryConcepts.ASTHMA, //
				MCMedicalHistoryConcepts.THYROID }) {
			final Obs conditionObs = grp.getMember(rfEConceptId);
			if (conditionObs != null && yes.equals(conditionObs.getValueCoded())) {
				// risk factor 'E' is present
				return true;
			}
		}

		// risk factor 'E' not found in this group observation
		return false;
	}

	public void setProgramConcluded(boolean programConcluded) {
		this.programConcluded = programConcluded;
	}

	public MaternalCareProgramObs getMcProgramObs() {
		return mcProgramObs;
	}

	public void setMcProgramObs(MaternalCareProgramObs mcProgramObs) {
		this.mcProgramObs = mcProgramObs;

		// also store into the parent observation map for cascade saving
		getObservationMap().put(mcProgramObs.getObs().getConcept().getId(), mcProgramObs.getObs());
	}

	/**
	 * Gets the {@link TetanusServiceInfo} for the current patient.
	 * 
	 * @return The TetanusServiceInfo for the patient.
	 */
	public TetanusServiceInfo getTetanusServiceInfo() {
		return MaternalCareUtil.getTetanusServiceInfo(getPatient());
	}

	public ObstetricHistoryDetail getObstetricHistoryDetail() {
		return obstetricHistoryDetail;
	}

	public void setObstetricHistoryDetail(ObstetricHistoryDetail obstetricHistoryDetail) {
		this.obstetricHistoryDetail = obstetricHistoryDetail;
	}

	public PrenatalVisitRecord getPrenatalVisitRecord() {
		return prenatalVisitRecord;
	}

	public void setPrenatalVisitRecord(PrenatalVisitRecord prenatalVisitRecord) {
		this.prenatalVisitRecord = prenatalVisitRecord;
	}

	public InternalExaminationRecord getInternalExaminationRecord() {
		return internalExaminationRecord;
	}

	public void setInternalExaminationRecord(InternalExaminationRecord internalExaminationRecord) {
		this.internalExaminationRecord = internalExaminationRecord;
	}

	public PostpartumInternalExaminationRecord getPostpartumInternalExaminationRecord() {
		return postpartumInternalExaminationRecord;
	}

	public void setPostpartumInternalExaminationRecord(PostpartumInternalExaminationRecord postpartumInternalExaminationRecord) {
		this.postpartumInternalExaminationRecord = postpartumInternalExaminationRecord;
	}

	public PostPartumVisitRecord getPostPartumVisitRecord() {
		return postPartumVisitRecord;
	}

	public void setPostPartumVisitRecord(PostPartumVisitRecord postPartumVisitRecord) {
		this.postPartumVisitRecord = postPartumVisitRecord;
	}

	public PatientConsultStatus getPatientConsultStatus() {
		return patientConsultStatus;
	}

	public void setPatientConsultStatus(PatientConsultStatus patientConsultStatus) {
		this.patientConsultStatus = patientConsultStatus;
	}

	public TetanusServiceRecord getTetanusServiceRecord() {
		return tetanusServiceRecord;
	}

	public void setTetanusServiceRecord(TetanusServiceRecord tetanusServiceRecord) {
		this.tetanusServiceRecord = tetanusServiceRecord;
	}

	public VitaminAServiceRecord getVitaminAServiceRecord() {
		return vitaminAServiceRecord;
	}

	public void setVitaminAServiceRecord(VitaminAServiceRecord vitaminAServiceRecord) {
		this.vitaminAServiceRecord = vitaminAServiceRecord;
	}

	public IronSupplementationServiceRecord getIronSupplementationServiceRecord() {
		return ironSupplementationServiceRecord;
	}

	public void setIronSupplementationServiceRecord(IronSupplementationServiceRecord ironSupplementationServiceRecord) {
		this.ironSupplementationServiceRecord = ironSupplementationServiceRecord;
	}

	public DewormingServiceRecord getDewormingServiceRecord() {
		return dewormingServiceRecord;
	}

	public void setDewormingServiceRecord(DewormingServiceRecord dewormingServiceRecord) {
		this.dewormingServiceRecord = dewormingServiceRecord;
	}
}
