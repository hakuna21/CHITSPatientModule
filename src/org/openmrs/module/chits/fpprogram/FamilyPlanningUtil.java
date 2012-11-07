package org.openmrs.module.chits.fpprogram;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPClientTypeConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyInformationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyPlanningMethodConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFemaleMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMaleMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMedicalHistoryConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPObstetricHistoryConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPhysicalExaminationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPRiskFactorsConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPServiceDeliveryRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Contains utility methods for managing and / or querying the family planning program for a patient.
 * 
 * @author Bren
 */
public class FamilyPlanningUtil {
	/** Logger for this class */
	protected static final Log log = LogFactory.getLog(FamilyPlanningUtil.class);

	/**
	 * Checks the eligibility for enrollment into the family planning program returning true if they are met, false otherwise.
	 * <p>
	 * The family planning eligibility rules:
	 * <ul>
	 * <li>For males: at least 15 years of age
	 * <li>For females: at least 9 years of age and not enrolled in maternity care program (or if enrolled in maternity care program, has already delivered the
	 * baby)
	 * </ul>
	 * 
	 * @param patient
	 *            The patient to check for eligibility
	 * @return true if the patient can enroll in the family planning program
	 */
	public static boolean canEnrollInFamilyPlanning(Patient patient) {
		// can't enroll to family planning if already enrolled
		final boolean isEnrolledInFamilyPlanning = Functions.getActivePatientProgram(patient, ProgramConcepts.FAMILYPLANNING) != null;

		// can't re-enroll a patient into family planning if previously enrolled
		final boolean previouslyEnrolledInFamilyPlanning = Functions.getLatestPatientProgram(patient, ProgramConcepts.FAMILYPLANNING) != null;

		if (isEnrolledInFamilyPlanning || previouslyEnrolledInFamilyPlanning) {
			// already currently enrolled or already concluded, so can't enroll into family planning program
			return false;
		}

		if ("M".equalsIgnoreCase(patient.getGender())) {
			// male patient: is minimum age met?
			return patient.getAge() != null && patient.getAge() >= FamilyPlanningConstants.MIN_AGE_MALE;
		} else if ("F".equalsIgnoreCase(patient.getGender())) {
			// female patient: is minimum age met?
			if (patient.getAge() != null && patient.getAge() >= FamilyPlanningConstants.MIN_AGE_FEMALE) {
				// ensure patient is not enrolled in the maternity care program (or if enrolled in maternity care program, has already delivered the baby)
				return !MaternalCareUtil.isCurrentlyEnrolledAndBabyNotYetDelivered(patient);
			}
		}

		// patient not eligible for family planning program otherwise
		return false;
	}

	/**
	 * Returns true if the patient was previously enrolled in the family planning program which has subsequently been concluded / closed.
	 * 
	 * @param patient
	 *            The patient of which to check if the program state is closed
	 * @return If the program state of the given patient is closed
	 */
	public static boolean isProgramConcludedFor(Patient patient) {
		if (patient == null) {
			return false;
		}

		final boolean isEnrolledInFamilyPlanning = Functions.getActivePatientProgram(patient, ProgramConcepts.FAMILYPLANNING) != null;
		final boolean previouslyEnrolledInFamilyPlanning = Functions.getLatestPatientProgram(patient, ProgramConcepts.FAMILYPLANNING) != null;

		// program is closed if the patient possesses the 'closed' state
		return !isEnrolledInFamilyPlanning && previouslyEnrolledInFamilyPlanning;
	}

	/**
	 * Defines or updates the family planning concepts used by the family planning module.
	 * 
	 * @param cu
	 */
	public static void defineOrUpdateFamilyPlanningConcepts(ConceptUtil cu) {
		// setup the family planning program
		cu.defineOrUpdateProgram(ProgramConcepts.FAMILYPLANNING);

		// define the 'Partner/Partner' relationship
		cu.defineOrUpdateRelationshipType(Constants.PARTNER_RELATIONSHIP_NAME, "The relationship type name for the general 'partner' definition "
				+ "(used in primarily by the family planning module, but logically could also indicate a patient's spouse");

		// define page 1 concepts
		Concept set = cu.loadOrCreateConvenienceSet(FPFamilyInformationConcepts.FAMILY_INFORMATION, "Parent observation for the family information form");

		// define page 2 concepts
		cu.loadOrCreateTextConceptQuestion(FPMedicalHistoryConcepts.ALLERGIES);
		cu.loadOrCreateTextConceptQuestion(FPMedicalHistoryConcepts.DRUG_INTAKE);

		// define page 3 concepts
		set = cu.loadOrCreateConvenienceSet(FPRiskFactorsConcepts.RISK_FACTORS, "Parent observation for the risk factors form");
		cu.pairSetMemberIfNeeded(set, cu.loadOrCreateConcept(FPRiskFactorsConcepts.SOCIAL_HYGIENE_CLINIC, "", "", "Misc", "Boolean"));
		cu.pairSetMemberIfNeeded(set, cu.loadOrCreateDateConceptQuestion(FPRiskFactorsConcepts.DATE_REFERRED));

		// define page 4 concepts
		set = cu.loadOrCreateConvenienceSet(FPObstetricHistoryConcepts.OBSTETRIC_HISTORY, "Parent observation for the obstetrical history form");

		// define page 5 concepts
		cu.loadOrCreateConcept(FPPhysicalExaminationConcepts.ENLARGED_LYMPH_NODES, "", "", "Misc", "Boolean");

		// define page 6 concepts: no custom concepts required

		// define page 7 concepts
		Concept fpClientType;
		set = cu.loadOrCreateConvenienceSet(FPFamilyPlanningMethodConcepts.FAMILY_PLANNING_METHOD, "Parent observation for the family planning method form");
		cu.pairSetMemberIfNeeded(set, fpClientType = cu.loadOrCreateConceptQuestion(FPFamilyPlanningMethodConcepts.CLIENT_TYPE));
		cu.pairSetMemberIfNeeded(set, cu.loadOrCreateDateConceptQuestion(FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT));
		cu.pairSetMemberIfNeeded(set, cu.loadOrCreateDateConceptQuestion(FPFamilyPlanningMethodConcepts.DATE_OF_NEXT_SERVICE));
		cu.pairSetMemberIfNeeded(set, cu.loadOrCreateDateConceptQuestion(FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT));
		cu.pairSetMemberIfNeeded(set, cu.loadOrCreateTextConceptQuestion(FPFamilyPlanningMethodConcepts.DROPOUT_REASON));

		final Concept fpMaleMethod = cu.loadOrCreateConvenienceSet(FPMethodOptions.MALES, "Family planning methods applicable to males");
		final Concept fpFemaleMethod = cu.loadOrCreateConvenienceSet(FPMethodOptions.FEMALES, "Family planning methods applicable to females");

		// define available answers for the family planning method selected for males
		for (FPMaleMethodOptions answer : FPMaleMethodOptions.values()) {
			final Concept answerConcept = cu.loadOrCreateConceptAnswer(fpMaleMethod, answer, answer.getConceptName() //
					+ " (" + fpMaleMethod.getName().toString() + ")");
			cu.pairSetMemberIfNeeded(fpMaleMethod, answerConcept);
			cu.pairQuestionAndAnswerIfNeeded(set, answerConcept);
		}

		// define available answers for the family planning method selected for females
		for (FPFemaleMethodOptions answer : FPFemaleMethodOptions.values()) {
			final Concept answerConcept = cu.loadOrCreateConceptAnswer(fpFemaleMethod, answer, answer.getConceptName() //
					+ " (" + fpFemaleMethod.getName().toString() + ")");
			cu.pairSetMemberIfNeeded(fpFemaleMethod, answerConcept);
			cu.pairQuestionAndAnswerIfNeeded(set, answerConcept);
		}

		// group the natural non-permanent methods into a convenience set
		cu.loadOrCreateConvenienceSet(FPMethodOptions.NATURAL, "Natural family planning methods");
		cu.pairSetMemberIfNeeded(FPMethodOptions.NATURAL, FPFemaleMethodOptions.NFP_BB);
		cu.pairSetMemberIfNeeded(FPMethodOptions.NATURAL, FPFemaleMethodOptions.NFP_CM);
		cu.pairSetMemberIfNeeded(FPMethodOptions.NATURAL, FPFemaleMethodOptions.NFP_LAM);
		cu.pairSetMemberIfNeeded(FPMethodOptions.NATURAL, FPFemaleMethodOptions.NFP_SDM);
		cu.pairSetMemberIfNeeded(FPMethodOptions.NATURAL, FPFemaleMethodOptions.NFP_STM);

		// group the natural artificial non-permanent methods into a convenience set
		cu.loadOrCreateConvenienceSet(FPMethodOptions.ARTIFICIAL_NONPERM, "Non-permanent artificial family planning methods");
		cu.pairSetMemberIfNeeded(FPMethodOptions.ARTIFICIAL_NONPERM, FPMaleMethodOptions.CONDOM);
		cu.pairSetMemberIfNeeded(FPMethodOptions.ARTIFICIAL_NONPERM, FPFemaleMethodOptions.CONDOM);
		cu.pairSetMemberIfNeeded(FPMethodOptions.ARTIFICIAL_NONPERM, FPFemaleMethodOptions.PILLS);
		cu.pairSetMemberIfNeeded(FPMethodOptions.ARTIFICIAL_NONPERM, FPFemaleMethodOptions.INJ);
		cu.pairSetMemberIfNeeded(FPMethodOptions.ARTIFICIAL_NONPERM, FPFemaleMethodOptions.IUD);

		// group the natural artificial permanent methods into a convenience set
		cu.loadOrCreateConvenienceSet(FPMethodOptions.ARTIFICIAL_PERM, "Permanent artificial family planning methods");
		cu.pairSetMemberIfNeeded(FPMethodOptions.ARTIFICIAL_PERM, FPFemaleMethodOptions.FSTRL_BTL);
		cu.pairSetMemberIfNeeded(FPMethodOptions.ARTIFICIAL_PERM, FPMaleMethodOptions.VASECTOMY);

		for (FPClientTypeConcepts answer : FPClientTypeConcepts.values()) {
			// define available answers for the client type question
			cu.loadOrCreateConceptAnswer(fpClientType, answer, answer.getConceptName() + "(" + fpClientType.getName().toString() + ")");
		}

		// define service delivery concepts
		cu.loadOrCreateConvenienceSet(FPServiceDeliveryRecordConcepts.SERVICE_DELIVERY_RECORD, "Family Planning Service Delivery Record");
	}

	/**
	 * Checks the family planning pre-requisites and returns true if they are met before allowing access to the family planning module, false otherwise.
	 * <p>
	 * The family planning pre-requisites require the following observations to have been taken for the current visit:
	 * <ul>
	 * <li>No observations required for family planning module access
	 * </ul>
	 * <p>
	 * Prerequisites are met if the patient is enrolled in the family planning program
	 * 
	 * @param form
	 *            The form containing the patient queue
	 * @return true if the family planning prerequisites are met for the current visit to allow access to the family planning module, false otherwise
	 */
	public static boolean familyPlanningPrerequisitesMet(FamilyPlanningConsultEntryForm form) {
		// patient has to be currently enrolled in the family planning program
		final Patient patient = form.getPatient();
		final boolean isEnrolledInFamilyPlanning = patient != null && Functions.getActivePatientProgram(patient, ProgramConcepts.FAMILYPLANNING) != null;

		return isEnrolledInFamilyPlanning;
	}

	/**
	 * If the patient is active in the family planning program, this method returns the {@link Obs} record with a concept of
	 * {@link ProgramConcepts#FAMILYPLANNING} ("CHITS Family Planning") with UUID matching the {@link PatientProgram} record
	 * <p>
	 * If the patient is not active in the family planning program, this method will return null;
	 * 
	 * @return The {@link Obs} instance to be used as the parent observation for all observations under the patient's currently active family planning program.
	 */
	public static Obs getObsForActiveFamilyPlanningProgram(Patient patient) {
		final PatientProgram fpPatientProgram = Functions.getActivePatientProgram(patient, ProgramConcepts.FAMILYPLANNING);
		if (fpPatientProgram == null) {
			// the patient is not currently active in the family planning program!
			return null;
		}

		// store the active program obs instance for this patient
		Obs fpActiveProgramObs = null;
		for (Obs obs : Functions.observations(patient, ProgramConcepts.FAMILYPLANNING)) {
			if (fpPatientProgram.getUuid().equalsIgnoreCase(obs.getUuid())) {
				// this is the observation
				if (fpActiveProgramObs == null) {
					fpActiveProgramObs = obs;
				} else {
					// multiple Obs with the same UUID matching the family planning care patient program UUID ?
					log.warn("Patient (" + patient + ") possesses more than one Obs instance with UUID: " + fpPatientProgram.getUuid());
				}
			}
		}

		if (fpActiveProgramObs == null) {
			// no record created yet, so prepare a new instance
			final Concept familyPlanningProgramConcept = Functions.concept(ProgramConcepts.FAMILYPLANNING);
			fpActiveProgramObs = PatientConsultEntryForm.newObs(familyPlanningProgramConcept, patient);
			fpActiveProgramObs.setPerson(patient);

			// store the family planning concept into the 'value coded' and 'value text' fields for DB readability
			fpActiveProgramObs.setValueCoded(Functions.concept(ProgramConcepts.FAMILYPLANNING));
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(fpActiveProgramObs);

			// UUID of obs must match UUID of the patient program instance
			fpActiveProgramObs.setUuid(fpPatientProgram.getUuid());
		}

		// send back the obs instance to be used for the patient's active family planning program
		return fpActiveProgramObs;
	}

	/**
	 * Same as {@link #getObsForActiveFamilyPlanningProgram(Patient)}, but throws an {@link APIException} if the patient is not currently enrolled in the family
	 * planning program.
	 * 
	 * @param patient
	 *            The patient to get the {@link Obs} representing the currently active program of.
	 * @return The {@link Obs} representing the currently active family planning program for the given patient.
	 * @throws APIAuthenticationException
	 *             If the given patient is not currently active in a family planning program.
	 */
	public static Obs getObsForActiveFamilyPlanningProgramOrFail(Patient patient) throws APIAuthenticationException {
		final Obs fpObs = getObsForActiveFamilyPlanningProgram(patient);
		if (fpObs == null) {
			// throw new APIException(String.format("'%s' is  not enrolled in Family Planning Program", patient.getPersonName().toString()));
			throw new APIAuthenticationException("chits.program.FAMILYPLANNING.not.enrolled");
		}

		// send back the family planning program of the patient
		return fpObs;
	}

	/**
	 * Calculates the number of days of the allowable extension time to the schedule of next visit before a warning is displayed.
	 * 
	 * @param fpm
	 *            The family planning method
	 * @return The number of days past the next scheduled visit before the warning is displayed
	 */
	public static int calculateFamilyPlaningMethodExtension(FamilyPlanningMethod fpm) {
		final Concept fpMethod = fpm.getObs().getValueCoded();
		if (Functions.concept(FPFemaleMethodOptions.PILLS).equals(fpMethod)) {
			// 8 days for pills
			return 8;
		} else if (Functions.concept(FPFemaleMethodOptions.INJ).equals(fpMethod)) {
			// 14 days for injectables
			return 14;
		}

		// any other method has no extension
		return 0;
	}
}
