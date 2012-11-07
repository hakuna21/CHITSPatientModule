package org.openmrs.module.chits.fpprogram.advice;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientQueue;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyPlanningMethodConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFemaleMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningMethod;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.aop.AfterReturningAdvice;

/**
 * Every time a female patient who is enrolled in the Family Planning Program is queued for a consult visit, the age of the female patient is checked. If at
 * least 50 years old, the program status of the patient is automatically set to DROPOUT.
 * <p>
 * Also intercepts patient enrollment into the maternal care program and automatically drops out from the current family planning program.
 * 
 * @author Bren
 */
public class AutomaticDropoutForQueuedPatients implements AfterReturningAdvice {
	/**
	 * Advise the {@link CHITSService#savePatientQueue(PatientQueue)} method to set any family planning program to 'dropout' when a female of 50 years or more
	 * is added to the queue.
	 */
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		if (method.getName().equals("savePatientQueue") //
				&& args != null && args.length == 1 //
				&& args[0] instanceof PatientQueue) {
			// called from CHITSService#savePatientQueue(PatientQueue): get formal arguments
			final PatientQueue patientQueue = (PatientQueue) args[0];

			// patient has entered the queue: process dropout checking
			processQueuedPatient(patientQueue);
		} else if (method.getName().equals("savePatientProgram") //
				&& args != null && args.length == 1 //
				&& args[0] instanceof PatientProgram) {
			// called from ProgramWorkflowService#savePatientProgram(PatientProgram)
			final PatientProgram patientProgram = (PatientProgram) args[0];
			final Patient patient = patientProgram.getPatient();
			if (patient != null) {
				final PatientQueue patientQueue = Context.getService(CHITSService.class).getQueuedPatient(patient);

				// patient has been enrolled into a program, perform dropout checks
				processQueuedPatient(patientQueue);
			}
		}
	}

	/**
	 * Process the patient queue for patients currently in the queue with their consults started.
	 * 
	 * @param patientQueue
	 */
	private void processQueuedPatient(PatientQueue patientQueue) {
		if (patientQueue == null || patientQueue.getEncounter() == null || patientQueue.getExitedQueue() != null) {
			// consult not yet started, or patient exited queue: do not perform dropout checking
			return;
		}

		final Patient patient = patientQueue.getPatient();
		if (patient != null) {
			// dropout from all family planning methods for female patients older the 50y/o
			if (patient.getBirthdate() != null && "F".equalsIgnoreCase(patient.getGender()) && patient.getAge() >= 50) {
				final List<Obs> familyPlanningMethods = Functions.observations(patient, FPFamilyPlanningMethodConcepts.FAMILY_PLANNING_METHOD);
				for (Obs fpMethodObs : familyPlanningMethods) {
					final FamilyPlanningMethod fpm = new FamilyPlanningMethod(fpMethodObs);
					if (!fpm.isDroppedOut()) {
						// dropout from active methods
						dropoutFromFPMethod(patientQueue, fpm, "50 y/o");
					}
				}
			} else {
				// get the latest family planning method
				final Obs fpMethodObs = Functions.observation(patient, FPFamilyPlanningMethodConcepts.FAMILY_PLANNING_METHOD);
				if (fpMethodObs == null) {
					// no family planning method to drop
					return;
				}

				final FamilyPlanningMethod fpm = new FamilyPlanningMethod(fpMethodObs);
				if (!fpm.isDroppedOut()) {
					// Dropout if the patient is enrolled under the method LAM, and the current date is 6 months past the date of enrollment to LAM
					final Obs dateOfEnrollmentObs = fpm.getMember(FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT);
					final Date dateOfEnrollment = dateOfEnrollmentObs != null ? dateOfEnrollmentObs.getValueDatetime() : null;
					final Date today = DateUtil.stripTime(new Date());
					if (Functions.concept(FPFemaleMethodOptions.NFP_LAM).equals(fpm.getObs().getValueCoded())) {
						if (dateOfEnrollment != null && DateUtil.monthsBetween(dateOfEnrollment, today) >= 6) {
							dropoutFromFPMethod(patientQueue, fpm, "6 months after birth.");
						}
					}

					// Automatic dropout of the patient is done by the system after 14 days from the date of enrollment
					// under the maternal care module, with the reason for dropping being set to "pregnancy"
					if (MaternalCareUtil.isCurrentlyEnrolledAndBabyNotYetDelivered(patient)) {
						final PatientProgram mcPatientProgram = Functions.getActivePatientProgram(patient, ProgramConcepts.MATERNALCARE);
						if (DateUtil.daysBetween(mcPatientProgram.getDateEnrolled(), today) >= 14) {
							dropoutFromFPMethod(patientQueue, fpm, "pregnancy");
						}
					}
				}
			}
		}
	}

	/**
	 * Drops out all active family planning programs for the given patient.
	 * 
	 * @param patient
	 */
	private void dropoutFromFPMethod(PatientQueue patientQueue, FamilyPlanningMethod fpMethod, String dropoutReason) {
		final Obs fpMethodObs = fpMethod.getObs();
		final Patient patient = patientQueue.getPatient();
		Obs dateOfDroputObs = Functions.observation(fpMethodObs, FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT);
		if (dateOfDroputObs == null) {
			// prepare a new observation for this
			dateOfDroputObs = ObsUtil.newObs(Functions.concept(FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT), patient);
			fpMethodObs.addGroupMember(dateOfDroputObs);
		}

		if (dateOfDroputObs.getValueDatetime() == null) {
			// this method has not yet been dropped out, so drop it out now
			Obs dropoutReasonObs = Functions.observation(fpMethodObs, FPFamilyPlanningMethodConcepts.DROPOUT_REASON);
			if (dropoutReasonObs == null) {
				// prepare a new observation for this
				dropoutReasonObs = ObsUtil.newObs(Functions.concept(FPFamilyPlanningMethodConcepts.DROPOUT_REASON), patient);
				fpMethodObs.addGroupMember(dropoutReasonObs);
			}

			// update dropout values
			final Date now = new Date();
			dateOfDroputObs.setValueDatetime(now);
			dateOfDroputObs.setValueText(Context.getDateFormat().format(now));
			dropoutReasonObs.setValueText(dropoutReason);

			// add observation to encounter for cascade saving through the queue
			Context.getService(CHITSService.class).savePatientQueue(patientQueue);
			patientQueue.getEncounter().addObs(fpMethodObs);
		}
	}
}