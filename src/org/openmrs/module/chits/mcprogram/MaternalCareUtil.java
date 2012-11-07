package org.openmrs.module.chits.mcprogram;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.ObsService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.Defaulter;
import org.openmrs.module.chits.Defaulter.DueServiceInfo.ServiceType;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDangerSignsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDeliveryReportConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCIERecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMaternityStage;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPatientConsultStatus;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumVisitRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPregnancyOutcomeConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPrenatalVisitRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MaternalCareProgramStates;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidDateAdministeredConcepts;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Contains utility methods for managing and / or querying the maternal care program for a patient.
 * 
 * @author Bren
 */
public class MaternalCareUtil {
	/** Logger for this class */
	protected static final Log log = LogFactory.getLog(MaternalCareUtil.class);

	/**
	 * Returns whether the patient's state has been marked as closed in the maternal care program.
	 * 
	 * @param patient
	 *            The patient of which to check if the program state is closed
	 * @return If the program state of the given patient is closed
	 */
	public static boolean isProgramClosedFor(Patient patient) {
		// program is closed if the patient possesses the 'closed' state
		final PatientProgram program = Functions.getActivePatientProgram(patient, ProgramConcepts.MATERNALCARE);

		// program is closed if there is no active program or the active program already has the 'ended' state.
		return program == null || Functions.findPatientProgramState(program, MaternalCareProgramStates.ENDED) != null;
	}

	/**
	 * Returns if the patient is currently enrolled in the maternal care program and has not yet delivered the baby)
	 * 
	 * @param patient
	 *            The patient to check the maternal care program status of.
	 * @return true if the patient is currently enrolled in the maternal care program but has not yet delivered the baby.
	 */
	public static boolean isCurrentlyEnrolledAndBabyNotYetDelivered(Patient patient) {
		// Determine if patient is active in the maternal care program
		final Obs mcObs = MaternalCareUtil.getObsForActiveMaternalCareProgram(patient);
		if (mcObs != null) {
			if (Functions.observation(mcObs, MCDeliveryReportConcepts.DELIVERY_REPORT) != null) {
				// baby has been delivered already!
				return false;
			}

			// patient enrolled in program but baby has not yet been delivered
			return true;
		}

		// patient not currently active in program
		return false;
	}

	/**
	 * Checks the eligibility for enrollment into the maternal care program returning true if they are met, false otherwise.
	 * 
	 * @param patient
	 *            The patient to check for eligibility
	 * @return true if the patient can enroll in the maternal care program
	 */
	public static boolean canEnrollInMaternalCare(Patient patient) {
		// only females can enroll in the maternal care program, and only if they are currently not already enrolled
		final boolean isFemale = "F".equalsIgnoreCase(patient.getGender());
		final boolean isOfAge = patient.getAge() != null && patient.getAge() >= 9;
		final boolean isEnrolledInMaternalCare = Functions.getActivePatientProgram(patient, ProgramConcepts.MATERNALCARE) != null;

		// allow only females not already enrolled in maternal care to enroll
		return isFemale && isOfAge && !isEnrolledInMaternalCare;
	}

	/**
	 * Defines or updates the maternal care concepts used by the maternal care module that are not defined by the official concept dictionary.
	 * 
	 * @param cu
	 */
	public static void defineOrUpdateMaternalCareConcepts(ConceptUtil cu) {
		// setup the family planning program and states
		cu.defineOrUpdateProgram(ProgramConcepts.MATERNALCARE);

		// define the ConvSet concept to serve as the parent observation for obstetric history details since there was no other suitable existing concept
		cu.loadOrCreateConvenienceSet(MCObstetricHistoryDetailsConcepts.OBSTETRIC_HISTORY_DETAILS, "Obstetric History Details, Maternal Care");

		// define the ConvSet concept to serve as the parent observation for pregnancy outcome since there was no other suitable existing concept
		cu.loadOrCreateConvenienceSet(MCPregnancyOutcomeConcepts.PREGNANCY_OUTCOME, "Pregnancy Outcome, Maternal Care");

		// define the ConvSet concept to serve as the parent observation for prenatal visit records since there was no other suitable existing concept
		cu.loadOrCreateConvenienceSet(MCPrenatalVisitRecordConcepts.PRENATAL_VISIT_RECORD, "Prenatal Visit Record, Maternal Care");

		// define the ConvSet concept to serve as the parent observation for internal examination records since there was no other suitable existing concept
		cu.loadOrCreateConvenienceSet(MCIERecordConcepts.INTERNAL_EXAMINATION, "Physical Examination, Specialized: IE");

		// define the ConvSet concept to server as the parent observation for patient consult status entries
		cu.loadOrCreateConvenienceSet(MCPatientConsultStatus.PATIENT_CONSULT_STATUS, "Patient Consult Status, Maternal Care");

		// define a generic 'consult status' to contain a coded value
		cu.loadOrCreateConcept(MCPatientConsultStatus.STATUS, "Consult Status", "The consult's status", "Misc", "Coded");

		// define 'severe headache'
		cu.loadOrCreateConcept(MCDangerSignsConcepts.SEVERE_HEADACHE, "Severe Headache", "Severe Headache", "Misc", "Coded");

		// define 'birth weight of baby'
		cu.loadOrCreateNumericConceptQuestion(MCPregnancyOutcomeConcepts.BIRTH_WEIGHT_OF_BABY_KG, 0.0, 10.0, "kg", "Question");
	}

	/**
	 * If the patient is active in the maternal care program, this method returns the {@link Obs} record with a concept of {@link ProgramConcepts#MATERNALCARE}
	 * ("CHITS Maternal Care") with UUID matching the {@link PatientProgram} record. Since females may be enrolled multiple tims in the program, the records
	 * will be grouped by the UUID of the corresponding patient program record. If no such record is found, an new one will be returned.
	 * <p>
	 * If the patient is not active in the maternal care program, this method will return null;
	 * 
	 * @return The {@link Obs} instance to be used as the parent observation for all observations under the patient's currently active maternal care program.
	 */
	public static Obs getObsForActiveMaternalCareProgram(Patient patient) {
		final PatientProgram mcPatientProgram = Functions.getActivePatientProgram(patient, ProgramConcepts.MATERNALCARE);
		if (mcPatientProgram == null) {
			// the patient is not currently active in the maternal care program!
			return null;
		}

		// store the active program obs instance for this patient
		Obs mcActiveProgramObs = null;
		for (Obs obs : Functions.observations(patient, ProgramConcepts.MATERNALCARE)) {
			if (mcPatientProgram.getUuid().equalsIgnoreCase(obs.getUuid())) {
				// this is the observation
				if (mcActiveProgramObs == null) {
					mcActiveProgramObs = obs;
				} else {
					// multiple Obs with the same UUID matching the maternal care patient program UUID ?
					log.warn("Patient (" + patient + ") possesses more than one Obs instance with UUID: " + mcPatientProgram.getUuid());
				}
			}
		}

		if (mcActiveProgramObs == null) {
			// no record created yet, so prepare a new instance
			final Concept maternalCareProgramConcept = Functions.concept(ProgramConcepts.MATERNALCARE);
			mcActiveProgramObs = PatientConsultEntryForm.newObs(maternalCareProgramConcept, patient);
			mcActiveProgramObs.setPerson(patient);

			// store the maternal care concept into the 'value coded' and 'value text' fields for DB readability
			mcActiveProgramObs.setValueCoded(Functions.concept(ProgramConcepts.MATERNALCARE));
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(mcActiveProgramObs);

			// UUID of obs must match UUID of the patient program instance
			mcActiveProgramObs.setUuid(mcPatientProgram.getUuid());
		}

		// send back the obs instance to be used for the patient's active maternal care program
		return mcActiveProgramObs;
	}

	/**
	 * Same as {@link #getObsForActiveMaternalCareProgram(Patient)}, but throws an {@link APIException} if the patient is not currently enrolled in the maternal
	 * care program.
	 * 
	 * @param patient
	 *            The patient to get the {@link Obs} representing the currently active program of.
	 * @return The {@link Obs} represnting the currently active maternal care program for the given patient.
	 * @throws APIAuthenticationException
	 *             If the given patient is not currently active in a maternal care program.
	 */
	public static Obs getObsForActiveMaternalCareProgramOrFail(Patient patient) throws APIAuthenticationException {
		final Obs mcObs = getObsForActiveMaternalCareProgram(patient);
		if (mcObs == null) {
			// throw new APIException(String.format("'%s' is  not enrolled in Maternal Care Program", patient.getPersonName().toString()));
			throw new APIAuthenticationException("chits.program.MATERNALCARE.not.enrolled");
		}

		// send back the maternal care program of the patient
		return mcObs;
	}

	/**
	 * Returns the observation for the patient's maternal care program on the given date.
	 * <p>
	 * If the patient was not enrolled in the maternal care program during the given date, this method will return null;
	 * 
	 * @return The {@link Obs} parent observation for all observations under the patient's maternal care program at the time.
	 */
	public static Obs getObsForMaternalCareProgram(Patient patient, Date during) {
		// get programs where patient is active based on today's date
		final ProgramWorkflowService svc = Context.getProgramWorkflowService();
		final List<PatientProgram> programs = svc.getPatientPrograms(patient, svc.getProgram(ProgramConcepts.MATERNALCARE.getProgramId()), null,
				DateUtil.midnight(during), DateUtil.stripTime(during), null, false);
		PatientProgram mcPatientProgram = null;
		for (PatientProgram program : programs) {
			if (mcPatientProgram == null) {
				// this is the program that should be considered 'active' for the patient
				mcPatientProgram = program;
			} else {
				// patient has multiple records that are 'active'... log a warning and ignore the subsequent records
				log.warn("Patient (" + patient + ") active in more than one program instance for: " + ProgramConcepts.MATERNALCARE + " on " + during);
			}
		}
		
		if (mcPatientProgram == null) {
			// patient was not enrolled in this program at this time
			return null;
		}

		// send back the obs instance to be used for the patient's maternal care program at the given time
		final ObsService obsService = Context.getObsService();
		return obsService.getObsByUuid(mcPatientProgram.getUuid());
	}
	
	/**
	 * Checks the maternal care pre-requisites and returns true if they are met before allowing access to the maternal care module, false otherwise.
	 * <p>
	 * The maternal care pre-requisites require the following to be true
	 * <ul>
	 * <li>Patient must be female
	 * <li>Patient must be enrolled in the maternal care program and have an active record
	 * </ul>
	 * 
	 * @param form
	 *            The form containing the patient queue
	 * @return true if the child care prerequisites are met for the current visit to allow access to the childcare module, false otherwise
	 */
	public static boolean maternalCarePrerequisitesMet(MaternalCareConsultEntryForm form) {
		// only females can enroll in the maternal care program, and only if they are currently not already enrolled
		final Patient patient = form.getPatient();
		final boolean isFemale = patient != null && "F".equalsIgnoreCase(patient.getGender());
		final boolean isEnrolledInMaternalCare = patient != null && Functions.getActivePatientProgram(patient, ProgramConcepts.MATERNALCARE) != null;

		return isFemale && isEnrolledInMaternalCare;
	}

	/**
	 * Gets a human-readable display value of the patient's latest obstetric score.
	 * 
	 * @param patient
	 *            The patient to get the obstetric score of
	 * @return The obstetric score in a human-readable string format
	 */
	public static String getObstetricScore(Patient patient) {
		Obs score;
		final StringBuilder text = new StringBuilder();

		// append 'gravida'
		text.append("G");
		score = Functions.observation(patient, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_GRAVIDA);
		text.append(score != null && score.getValueNumeric() != null ? Integer.toString(score.getValueNumeric().intValue()) : "-");

		// append 'para'
		text.append("P");
		score = Functions.observation(patient, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PARA);
		text.append(score != null && score.getValueNumeric() != null ? Integer.toString(score.getValueNumeric().intValue()) : "-");

		// append '# of full-term pregnancies'
		text.append("(");
		score = Functions.observation(patient, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_FT);
		text.append(score != null && score.getValueNumeric() != null ? Integer.toString(score.getValueNumeric().intValue()) : "-");

		// append '# of pregnancies'
		text.append(",");
		score = Functions.observation(patient, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PT);
		text.append(score != null && score.getValueNumeric() != null ? Integer.toString(score.getValueNumeric().intValue()) : "-");

		// append '# of abortions'
		text.append(",");
		score = Functions.observation(patient, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_AM);
		text.append(score != null && score.getValueNumeric() != null ? Integer.toString(score.getValueNumeric().intValue()) : "-");

		// append '# of children still living'
		text.append(",");
		score = Functions.observation(patient, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_LC);
		text.append(score != null && score.getValueNumeric() != null ? Integer.toString(score.getValueNumeric().intValue()) : "-");
		text.append(")");

		// send back readable form
		return text.toString();
	}

	/**
	 * Gets Maternal Care tetanus services due for the given patient.
	 * <p>
	 * Assumptions:
	 * <ul>
	 * <li>Patient has birthdate set (i.e. patient.getBirthdate() != null)
	 * <li>Patient is enrolled in the MC program (i.e., Functions.isInProgram(patient, ProgramConcepts.MATERNALCARE) == true)
	 * </ul>
	 * 
	 * @param defaulter
	 *            the patient's defaulter record to store due services in
	 */
	public static void addDueTetanusToxoidServices(Defaulter defaulter) {
		// get tetanus service information for this patient
		final Date today = new Date();
		final Patient patient = defaulter.getPatient();

		// determine tetanus service status
		final TetanusServiceInfo tetanusSI = getTetanusServiceInfo(patient);
		if (tetanusSI != null) {
			final TetanusToxoidDateAdministeredConcepts nextShot = tetanusSI.getNextService();
			if (nextShot != null && tetanusSI.getNextServiceDate() != null && today.after(DateUtil.stripTime(tetanusSI.getNextServiceDate()))) {
				// this tetanus dose is over due!
				defaulter.addDueService(ServiceType.TETANUS, nextShot + " Shot", tetanusSI.getNextServiceDate());
			}
		}
	}

	/**
	 * Gets Maternal Care prenatal services due for the given patient.
	 * <p>
	 * Assumptions:
	 * <ul>
	 * <li>Patient has birthdate set (i.e. patient.getBirthdate() != null)
	 * <li>Patient is enrolled in the MC program and program has not yet been closed (i.e., Functions.isInProgram(patient, ProgramConcepts.MATERNALCARE) ==
	 * true)
	 * <li>Delivery report is not yet present
	 * </ul>
	 * 
	 * @param defaulter
	 *            the patient's defaulter record to store due services in
	 */
	public static void addDuePrenatalServices(Defaulter defaulter) {
		// get service information for this patient
		final Date today = new Date();
		final Patient patient = defaulter.getPatient();

		try {
			// determine patient's current maternity stage
			final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(patient);
			final MCMaternityStage stage = mcProgramObs.getMaternityStageOn(today);
			if (stage == MCMaternityStage.SECOND_TRIMESTER) {
				// in second trimester: did the mother make any visits in previous or current trimester?
				final Map<MCMaternityStage, List<PrenatalVisitRecord>> visits = mcProgramObs.getLatestPrenatalVisits();
				if (visits.get(MCMaternityStage.FIRST_TRIMESTER).isEmpty() && visits.get(MCMaternityStage.SECOND_TRIMESTER).isEmpty()) {
					// should have visited before the end of the first trimester!
					defaulter.addDueService(ServiceType.MC_PRENATAL, "2nd Trimester visit", mcProgramObs.getEndOfFirstTrimester());
				}
			} else if (stage == MCMaternityStage.THIRD_TRIMESTER) {
				// in third trimester: did the mother make any visits in previous or current trimester?
				final Map<MCMaternityStage, List<PrenatalVisitRecord>> visits = mcProgramObs.getLatestPrenatalVisits();
				if (visits.get(MCMaternityStage.SECOND_TRIMESTER).isEmpty() && visits.get(MCMaternityStage.THIRD_TRIMESTER).isEmpty()) {
					// should have visited before the end of the second trimester!
					defaulter.addDueService(ServiceType.MC_PRENATAL, "3rd Trimester visit", mcProgramObs.getEndOfSecondTrimester());
				} else {
					// from 8th month to EDC, visits should be every two weeks until the expected date of delivery.
					final Calendar eigthMonth = mcProgramObs.getLastMenstrualPeriod();
					eigthMonth.add(Calendar.MONTH, 8);

					// if today is after the eight month, then visits should be every two weeks
					if (today.after(eigthMonth.getTime())) {
						// NOTE: the first entry in each list will be the 'latest' visit
						final List<PrenatalVisitRecord> thirdTrimesterVisits = visits.get(MCMaternityStage.THIRD_TRIMESTER);
						final PrenatalVisitRecord latestVisitInThirdTrimester = !thirdTrimesterVisits.isEmpty() ? thirdTrimesterVisits.get(0) : null;
						if (latestVisitInThirdTrimester == null || latestVisitInThirdTrimester.getVisitDate() == null) {
							// should have made a third trimster visit by this time
							defaulter.addDueService(ServiceType.MC_PRENATAL, "3rd Trimester visit", eigthMonth.getTime());
						} else if (DateUtil.daysBetween(latestVisitInThirdTrimester.getVisitDate(), today) >= 14) {
							// needs to do another visit two weeks after last visit
							final Calendar biWeeklyVisit = Calendar.getInstance();
							biWeeklyVisit.setTime(DateUtil.stripTime(latestVisitInThirdTrimester.getVisitDate()));
							biWeeklyVisit.add(Calendar.DATE, 14);
							defaulter.addDueService(ServiceType.MC_PRENATAL, "3rd Trimester fortnightly visit", biWeeklyVisit.getTime());
						}
					}
				}
			}
		} catch (APIAuthenticationException ex) {
			// not enrolled in MC? badly behaving caller, should not have bothered us!
		}
	}

	/**
	 * Gets Maternal Care postnatal services due for the given patient.
	 * <p>
	 * Assumptions:
	 * <ul>
	 * <li>Patient has birthdate set (i.e. patient.getBirthdate() != null)
	 * <li>Patient is enrolled in the MC program and program has not yet been closed (i.e., Functions.isInProgram(patient, ProgramConcepts.MATERNALCARE) ==
	 * true)
	 * <li>Delivery report is already present
	 * </ul>
	 * 
	 * @param defaulter
	 *            the patient's defaulter record to store due services in
	 */
	public static void addDuePostnatalServices(Defaulter defaulter) {
		// get service information for this patient
		final Date today = new Date();
		final Patient patient = defaulter.getPatient();

		try {
			// determine patient's current maternity stage
			final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(patient);

			// determine delivery date
			final Obs deliveryReportObs = Functions.observation(mcProgramObs.getObs(), MCDeliveryReportConcepts.DELIVERY_REPORT);
			final Obs deliveryDateObs = deliveryReportObs != null ? Functions.observation(deliveryReportObs, MCDeliveryReportConcepts.DELIVERY_DATE) : null;
			final Date deliveryDate = deliveryDateObs != null ? deliveryDateObs.getValueDatetime() : null;
			if (deliveryDate == null) {
				// no delivery date, so no post partum due services
				return;
			}

			// prepare to do date arithmetic based on the delivery date
			final Calendar c = Calendar.getInstance();
			c.setTime(DateUtil.stripTime(deliveryDate));

			// get record of postpartum visits
			final List<Obs> postPartumVisitsObs = Functions.observations(mcProgramObs.getObs(), MCPostPartumVisitRecordConcepts.POSTPARTUM_VISIT_RECORD);
			if (postPartumVisitsObs.isEmpty()) {
				// overdue if it's been past 5 days since the delivery date
				c.add(Calendar.DATE, 5);
				if (today.after(c.getTime())) {
					defaulter.addDueService(ServiceType.MC_POSTNATAL, "1st Postnatal visit", c.getTime());
				}
			} else if (postPartumVisitsObs.size() < 2) {
				// overdue if it's been past 6 weeks since the delivery date
				c.add(Calendar.WEEK_OF_YEAR, 6);
				if (today.after(c.getTime())) {
					defaulter.addDueService(ServiceType.MC_POSTNATAL, "2nd Postnatal visit", c.getTime());
				}
			}
		} catch (APIAuthenticationException ex) {
			// not enrolled in MC? badly behaving caller, should not have bothered us!
		}
	}

	/**
	 * Obtain information about the patient's tetanus service records.
	 * 
	 * @param patient
	 *            The patient to check.
	 * @return Information about the patient's tetanus service.
	 */
	public static TetanusServiceInfo getTetanusServiceInfo(Patient patient) {
		final Obs tt5Obs = Functions.observation(patient, TetanusToxoidDateAdministeredConcepts.TT5);
		if (tt5Obs != null && tt5Obs.getValueDatetime() != null) {
			// no next service date
			return new TetanusServiceInfo(TetanusToxoidDateAdministeredConcepts.TT5, tt5Obs.getValueDatetime(), null);
		}

		final Obs tt4Obs = Functions.observation(patient, TetanusToxoidDateAdministeredConcepts.TT4);
		if (tt4Obs != null && tt4Obs.getValueDatetime() != null) {
			final Calendar c = Calendar.getInstance();
			c.setTime(tt4Obs.getValueDatetime());
			c.add(Calendar.YEAR, 1);

			// next service date is 1 year after tt4
			return new TetanusServiceInfo(TetanusToxoidDateAdministeredConcepts.TT4, tt4Obs.getValueDatetime(), c.getTime());
		}

		final Obs tt3Obs = Functions.observation(patient, TetanusToxoidDateAdministeredConcepts.TT3);
		if (tt3Obs != null && tt3Obs.getValueDatetime() != null) {
			final Calendar c = Calendar.getInstance();
			c.setTime(tt3Obs.getValueDatetime());
			c.add(Calendar.YEAR, 1);

			// next service date is 1 year after tt3
			return new TetanusServiceInfo(TetanusToxoidDateAdministeredConcepts.TT3, tt3Obs.getValueDatetime(), c.getTime());
		}

		final Obs tt2Obs = Functions.observation(patient, TetanusToxoidDateAdministeredConcepts.TT2);
		if (tt2Obs != null && tt2Obs.getValueDatetime() != null) {
			final Calendar c = Calendar.getInstance();
			c.setTime(tt2Obs.getValueDatetime());
			c.add(Calendar.MONTH, 6);

			// next service date is 6 months after tt2
			return new TetanusServiceInfo(TetanusToxoidDateAdministeredConcepts.TT2, tt2Obs.getValueDatetime(), c.getTime());
		}

		final Obs tt1Obs = Functions.observation(patient, TetanusToxoidDateAdministeredConcepts.TT1);
		if (tt1Obs != null && tt1Obs.getValueDatetime() != null) {
			final Calendar c = Calendar.getInstance();
			c.setTime(tt1Obs.getValueDatetime());
			c.add(Calendar.DATE, 28);

			// next service date is 28 days after tt1
			return new TetanusServiceInfo(TetanusToxoidDateAdministeredConcepts.TT1, tt1Obs.getValueDatetime(), c.getTime());
		}

		// no tetanus toxoid record on file
		return null;
	}

	/**
	 * Encapsulates information about the tetanus service.
	 * 
	 * @author Bren
	 */
	public static class TetanusServiceInfo {
		private final TetanusToxoidDateAdministeredConcepts lastService;
		private final Date lastServiceDate;
		private final Date nextServiceDate;

		public TetanusServiceInfo(TetanusToxoidDateAdministeredConcepts lastService, Date lastServiceDate, Date nextServiceDate) {
			this.lastService = lastService;
			this.lastServiceDate = lastServiceDate;
			this.nextServiceDate = nextServiceDate;
		}

		public TetanusToxoidDateAdministeredConcepts getLastService() {
			return lastService;
		}

		public TetanusToxoidDateAdministeredConcepts getNextService() {
			if (nextServiceDate != null && lastService != null) {
				final int nextOrdinal = lastService.ordinal() + 1;
				if (nextOrdinal < TetanusToxoidDateAdministeredConcepts.values().length) {
					return TetanusToxoidDateAdministeredConcepts.values()[nextOrdinal];
				}
			}

			// no next service
			return null;
		}

		public Date getLastServiceDate() {
			return lastServiceDate;
		}

		public Date getNextServiceDate() {
			return nextServiceDate;
		}
	}
}
