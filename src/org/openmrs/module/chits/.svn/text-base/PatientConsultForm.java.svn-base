package org.openmrs.module.chits;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.Constants.VisitConcepts;

/**
 * Contains the patient and consult (encounter) information.
 * 
 * @author Bren
 */
public class PatientConsultForm {
	/** The patient to use for the form */
	private Patient patient;

	/** Program being viewed (if any) */
	private ProgramConcepts program;

	/** The patient's encounters */
	private List<Encounter> encounters;

	/** Populated if the patient is in the queue */
	private PatientQueue patientQueue;

	/** Cache of the last taken observations for use in the patient chart */
	private Map<Integer, Obs> patientChartCache;

	/** List of patient chart concepts that are displayed in the 'Patient Chart' page */
	private static final List<CachedConceptId> PATIENT_CHART_CONCEPTS = Collections.unmodifiableList( //
			Arrays.asList(new CachedConceptId[] { //
			VisitConcepts.HEIGHT_CM, //
					VisitConcepts.WEIGHT_KG, //
					VisitConcepts.WAIST_CIRC_CM, //
					VisitConcepts.HIP_CIRC_CM, //
					VisitConcepts.HEAD_CIRC_CM, //
					VisitConcepts.CHEST_CIRC_CM }));

	/**
	 * Convenience method to return the encounter in the {@link PatientQueue}, if available;
	 * 
	 * @return
	 */
	public Encounter getEncounter() {
		return patientQueue != null ? patientQueue.getEncounter() : null;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * @return the encounters
	 */
	public List<Encounter> getEncounters() {
		return encounters;
	}

	/**
	 * This method assumes that the encounters are ordered from descending date!
	 * 
	 * @param encounters
	 *            the encounters to set
	 */
	public void setEncounters(List<Encounter> encounters) {
		this.encounters = encounters;

		// clear the chart cache whenever the encounters is set
		this.patientChartCache = null;
	}

	/**
	 * @return the patientQueue
	 */
	public PatientQueue getPatientQueue() {
		return patientQueue;
	}

	/**
	 * @param patientQueue
	 *            the patientQueue to set
	 */
	public void setPatientQueue(PatientQueue patientQueue) {
		this.patientQueue = patientQueue;
	}

	public void setProgram(ProgramConcepts program) {
		this.program = program;
	}

	public ProgramConcepts getProgram() {
		return program;
	}

	/*
	 * Helper bean methods for retrieving useful information.
	 */

	/**
	 * Returns last taken observations for the "Patient Chart" which includes the following concepts:
	 * <ul>
	 * <li>HEIGHT_CM
	 * <li>WEIGHT_KG
	 * <li>WAIST_CIRC_CM
	 * <li>HIP_CIRC_CM
	 * <li>HEAD_CIRC_CM
	 * <li>CHEST_CIRC_CM
	 * </ul>
	 * 
	 * @return
	 */
	public Map<Integer, Obs> getLastTakenPatientChartObservations() {
		if (patientChartCache == null && encounters != null) {
			patientChartCache = ConceptUtil.getLastTakenObservations(encounters, PATIENT_CHART_CONCEPTS);
		}

		return patientChartCache;
	}
}
