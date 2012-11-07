package org.openmrs.module.chits;

import org.openmrs.Encounter;
import org.openmrs.Patient;

/**
 * Contains the patient and consult (encounter) historical information.
 * 
 * @author Bren
 */
public class PatientHistoricalConsultForm {
	/** The patient to use for the form */
	private Patient patient;

	/** The patient's historical encounters */
	private Encounter encounter;

	/** The Patient's queue record (in case the encounter being viewed is the current one) */
	private PatientQueue patientQueue;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public PatientQueue getPatientQueue() {
		return patientQueue;
	}

	public void setPatientQueue(PatientQueue patientQueue) {
		this.patientQueue = patientQueue;
	}
}
