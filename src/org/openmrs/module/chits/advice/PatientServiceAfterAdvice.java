package org.openmrs.module.chits.advice;

import java.lang.reflect.Method;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.springframework.aop.AfterReturningAdvice;

public class PatientServiceAfterAdvice implements AfterReturningAdvice {
	/**
	 * Advise the {@link PatientService#mergePatients(Patient, Patient)} method to update the chits_family_folder_patient and chits_patient_queue tables
	 * {@link Patient} record references.
	 */
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		if (!method.getName().equals("mergePatients")) {
			// this is not the 'mergePatients' method
			return;
		} else if (args == null || args.length != 2) {
			// incorrect number of arguments: expecting 2
			return;
		} else if (!(args[0] instanceof Patient)) {
			// expecting a Patient record for the first parameter
			return;
		} else if (!(args[1] instanceof Patient)) {
			// expecting a Patient record for the second parameter
			return;
		}

		// get formal arguments
		final Patient preferred = (Patient) args[0];
		final Patient notPreferred = (Patient) args[1];

		// dispatch to chits service
		Context.getService(CHITSService.class).mergePatients(preferred, notPreferred);
	}
}