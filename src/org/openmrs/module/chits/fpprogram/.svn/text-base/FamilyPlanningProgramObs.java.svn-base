package org.openmrs.module.chits.fpprogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyInformationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyPlanningMethodConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMedicalHistoryConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPObstetricHistoryConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPelvicExaminationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPhysicalExaminationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPRiskFactorsConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FamilyPlanningProgramStates;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.obs.DatetimeGroupObsComparator;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * The family planning program all-encapsulating parent observation.
 * 
 * @author Bren
 */
public class FamilyPlanningProgramObs extends GroupObs {
	/** The lazy-loaded latest family planning information record */
	private FamilyInformation familyInformation;

	/** The lazy-loaded latest medical history record */
	private MedicalHistoryInformation medicalHistoryInformation;

	/** The lazy-loaded latest risk factors record */
	private RiskFactors riskFactors;

	/** The lazy-loaded latest obstetrical history record */
	private ObstetricHistory obstetricHistory;

	/** The lazy-loaded latest physical exam record */
	private PhysicalExamination physicalExamination;

	/** The lazy-loaded latest pelvic exam record */
	private PelvicExamination pelvicExamination;

	/** The lazy-loaded list of family planning methods */
	private List<FamilyPlanningMethod> familyPlanningMethods;

	/** Stores the patient program associated with the data */
	private final PatientProgram patientProgram;

	/** Flag for setting the 'will see physician' attribute on the patient */
	private boolean needsToSeePhysician;

	/** Indicates if this form is read-only */
	private final boolean readOnly;

	/**
	 * Prepares this wrapper around the observation instance representing the patient's currently active family planning program record.
	 * 
	 * @param patient
	 *            The patient to initialize the family planning program observation group over.
	 * @throws APIAuthenticationException
	 *             If the given patient is not currently active in a family planning program.
	 */
	public FamilyPlanningProgramObs(Patient patient) throws APIAuthenticationException {
		super(FamilyPlanningUtil.getObsForActiveFamilyPlanningProgramOrFail(patient));
		this.patientProgram = Functions.getActivePatientProgram(patient, ProgramConcepts.FAMILYPLANNING);

		// when attached to the currently active family planning program, the chart is updatable
		this.readOnly = false;
	}

	/**
	 * Prepares this wrapper around the observation instance representing an existing family planning patient program observation record.
	 * 
	 * @param obs
	 *            The observation corresponding to the family planning patient program.
	 * @throws APIAuthenticationException
	 *             If the given observation does not have a corresponding family planning patient program entry
	 */
	public FamilyPlanningProgramObs(Obs familyPlanningProgramObs) throws APIAuthenticationException {
		super(familyPlanningProgramObs);

		// lookup the patient program by the UUID of the observation
		this.patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(familyPlanningProgramObs.getUuid());

		// when attached to a historic family planning program observation, the chart is automatically read-only
		this.readOnly = true;

		// if patient program not found, then throw an error
		if (this.patientProgram == null) {
			throw new APIAuthenticationException("chits.program.FAMILYPLANNING.not.enrolled");
		}
	}

	/**
	 * Lazy-loaded getter for the {@link FamilyInformation} bean.
	 * 
	 * @return the {@link FamilyInformation} bean;
	 */
	public FamilyInformation getFamilyInformation() {
		if (familyInformation == null) {
			// lazy-load the family information
			this.familyInformation = loadFPMemberGroupObsImpl(FamilyInformation.class, FPFamilyInformationConcepts.FAMILY_INFORMATION);
		}

		return familyInformation;
	}

	/**
	 * Stores a family information bean into the model
	 * 
	 * @param familyInformation
	 */
	public void setFamilyInformation(FamilyInformation familyInformation) {
		this.familyInformation = familyInformation;
	}

	/**
	 * Lazy-loaded getter for the {@link MedicalHistoryInformation} bean.
	 * 
	 * @return the {@link MedicalHistoryInformation} bean;
	 */
	public MedicalHistoryInformation getMedicalHistoryInformation() {
		if (medicalHistoryInformation == null) {
			// lazy-load the family information
			this.medicalHistoryInformation = loadFPMemberGroupObsImpl(MedicalHistoryInformation.class, FPMedicalHistoryConcepts.MEDICAL_HISTORY);
		}

		return medicalHistoryInformation;
	}

	public void setMedicalHistoryInformation(MedicalHistoryInformation medicalHistoryInformation) {
		this.medicalHistoryInformation = medicalHistoryInformation;
	}

	/**
	 * Lazy-loaded getter for the {@link RiskFactors} bean.
	 * 
	 * @return the {@link RiskFactors} bean;
	 */
	public RiskFactors getRiskFactors() {
		if (riskFactors == null) {
			// lazy-load the family information
			this.riskFactors = loadFPMemberGroupObsImpl(RiskFactors.class, FPRiskFactorsConcepts.RISK_FACTORS);
		}

		return riskFactors;
	}

	public void setRiskFactors(RiskFactors riskFactors) {
		this.riskFactors = riskFactors;
	}

	/**
	 * Lazy-loaded getter for the {@link ObstetricHistory} bean.
	 * 
	 * @return the {@link ObstetricHistory} bean;
	 */
	public ObstetricHistory getObstetricHistory() {
		if (obstetricHistory == null) {
			// lazy-load the obstetric history observation
			this.obstetricHistory = loadFPMemberGroupObsImpl(ObstetricHistory.class, FPObstetricHistoryConcepts.OBSTETRIC_HISTORY);
		}

		return obstetricHistory;
	}

	public void setObstetricHistory(ObstetricHistory obstetricHistory) {
		this.obstetricHistory = obstetricHistory;
	}

	/**
	 * Lazy-loaded getter for the {@link PhysicalExamination} bean.
	 * 
	 * @return the {@link PhysicalExamination} bean;
	 */
	public PhysicalExamination getPhysicalExamination() {
		if (physicalExamination == null) {
			// lazy-load the family information
			this.physicalExamination = loadFPMemberGroupObsImpl(PhysicalExamination.class, FPPhysicalExaminationConcepts.PHYSICAL_EXAMINATION);
		}

		return physicalExamination;
	}

	public void setPhysicalExamination(PhysicalExamination physicalExamination) {
		this.physicalExamination = physicalExamination;
	}

	/**
	 * Lazy-loaded getter for the {@link PelvicExamination} bean.
	 * 
	 * @return the {@link PelvicExamination} bean;
	 */
	public PelvicExamination getPelvicExamination() {
		if (pelvicExamination == null) {
			// lazy-load the family information
			this.pelvicExamination = loadFPMemberGroupObsImpl(PelvicExamination.class, FPPelvicExaminationConcepts.PELVIC_EXAMINATION);
		}

		return pelvicExamination;
	}

	public void setPelvicExamination(PelvicExamination pelvicExamination) {
		this.pelvicExamination = pelvicExamination;
	}

	/**
	 * Convenience method for getting the list of all family planning methods used in this program sorted by date of enrollment.
	 * 
	 * @return family planning method records for this program sorted by enrollment date.
	 */
	public List<FamilyPlanningMethod> getFamilyPlanningMethods() {
		if (this.familyPlanningMethods == null) {
			// get the latest visit by visit date
			this.familyPlanningMethods = new ArrayList<FamilyPlanningMethod>();

			for (Obs fpMethodObs : Functions.observations(getObs(), FPFamilyPlanningMethodConcepts.FAMILY_PLANNING_METHOD)) {
				this.familyPlanningMethods.add(new FamilyPlanningMethod(fpMethodObs));
			}

			if (!this.familyPlanningMethods.isEmpty()) {
				// sort by visit date to get the latest
				Collections.sort(this.familyPlanningMethods, new DatetimeGroupObsComparator<FamilyPlanningMethod>(
						FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT));

				// ... reverse so that the order becomes latest to earliest
				Collections.reverse(this.familyPlanningMethods);
			}
		}

		// send back the sorted family planning method records
		return familyPlanningMethods;
	}

	/**
	 * Gets the current (latest) state of the patient in this family planning program.
	 * 
	 * @return The current (latest) state of the patient in the family planning program.
	 */
	public FamilyPlanningProgramStates getCurrentState() {
		if (Functions.findPatientProgramState(patientProgram, FamilyPlanningProgramStates.CLOSED) != null) {
			// program already closed
			return FamilyPlanningProgramStates.CLOSED;
		} else {
			// determine if current method is dropped
			final FamilyPlanningMethod fpm = getLatestFamilyPlanningMethod();
			if (fpm != null && fpm.isDroppedOut()) {
				return FamilyPlanningProgramStates.DROPOUT;
			}

			// current method not dropped: consider 'current' or 'new' (for learning users)
			if (Functions.findPatientProgramState(patientProgram, FamilyPlanningProgramStates.CURRENT) != null) {
				return FamilyPlanningProgramStates.CURRENT;
			} else {
				return FamilyPlanningProgramStates.NEW;
			}
		}
	}

	/**
	 * Loads the most recent (if any) observation of the member group concept type.
	 * 
	 * @param clazz
	 * @param memberGroupConcept
	 * @return
	 */
	protected <T extends GroupObs> T loadFPMemberGroupObsImpl(Class<T> clazz, CachedConceptId memberGroupConcept) {
		final Obs fpProgramObs = super.getObs();
		Obs fpGroupObs = Functions.observation(fpProgramObs, memberGroupConcept);

		if (fpGroupObs == null) {
			// create a new observation and add to the family planning program
			fpGroupObs = PatientConsultEntryForm.newObs(memberGroupConcept, ObsUtil.PATIENT_CONTEXT.get());
		}

		// encapsulate and store the member group observation using the constructor that accepts an observation
		try {
			return clazz.getConstructor(Obs.class).newInstance(fpGroupObs);
		} catch (Exception ex) {
			// invalid arguments
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * Override to propagate into lazy-loaded attributes.
	 */
	@Override
	public void storePersonAndAudit(Person person) {
		// store into internal observations
		super.storePersonAndAudit(person);

		// propagate to lazy-loaded attributes
		if (familyInformation != null) {
			familyInformation.storePersonAndAudit(person);
		}

		if (medicalHistoryInformation != null) {
			medicalHistoryInformation.storePersonAndAudit(person);
		}

		if (riskFactors != null) {
			riskFactors.storePersonAndAudit(person);
		}

		if (obstetricHistory != null) {
			obstetricHistory.storePersonAndAudit(person);
		}

		if (physicalExamination != null) {
			physicalExamination.storePersonAndAudit(person);
		}

		if (pelvicExamination != null) {
			pelvicExamination.storePersonAndAudit(person);
		}
	}

	public boolean isNeedsToSeePhysician() {
		return needsToSeePhysician;
	}

	public void setNeedsToSeePhysician(boolean needsToSeePhysician) {
		this.needsToSeePhysician = needsToSeePhysician;
	}

	public PatientProgram getPatientProgram() {
		return patientProgram;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * If enrolled in maternal care and baby has not yet been delivered, warn about being enrolled
	 * 
	 * @return if enrolled in maternal care and baby has not yet been delivered, warn about being enrolled
	 */
	public boolean isWarnAboutMaternalCareEnrollment() {
		// check if patient is enrolled in maternal care and baby has not yet been delivered
		final Patient patient = patientProgram != null ? patientProgram.getPatient() : null;

		// if enrolled in maternal care and baby has not yet been delivered, warn about being enrolled
		return patient != null && MaternalCareUtil.isCurrentlyEnrolledAndBabyNotYetDelivered(patient);
	}

	/**
	 * Returns the latest (current) family planning method (not necessarily active or not dropped out)
	 * 
	 * @return The latest (current) family planning method
	 */
	public FamilyPlanningMethod getLatestFamilyPlanningMethod() {
		final List<FamilyPlanningMethod> fpMethods = getFamilyPlanningMethods();

		// the latest is the first since that's how getFamilyPlanningMethods() arranges the records
		return !fpMethods.isEmpty() ? fpMethods.get(0) : null;
	}
}