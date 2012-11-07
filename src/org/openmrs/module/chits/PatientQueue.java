package org.openmrs.module.chits;

import java.io.Serializable;
import java.util.Date;

import org.openmrs.Encounter;
import org.openmrs.Patient;

/**
 * Keeps a list of patients that were entered into the queue.
 * <p>
 * This queue is specific to the installation, hence, is not necessary for synchronization nor any need for creation of global UUIDs.
 * <p>
 * NOTE: The 'consultStart' should be set once the 'Encounter' has been set.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class PatientQueue implements Serializable {
	/** Primary key id */
	private Integer patientQueueId;

	/** The patient that is in the queue; must be non-null */
	private Patient patient;

	/** The patient's encounter data when added to the queue */
	private Encounter encounter;

	/** The sequentially generated notes number assigned to a patient when the patient is admitted for consult during a visit */
	private Integer notesNumber;

	/** The timestamp that the patient entered the queue; must be non-null */
	private Date enteredQueue;

	/** The timestamp that the patient's consult commenced */
	private Date consultStart;

	/** The timestamp that the patient's consult ended */
	private Date consultEnd;

	/** The timestamp that the patient exited the queue -- usually the same as the consultEnd value unless the patient dropped out of the queue */
	private Date exitedQueue;

	/**
	 * @return the patientQueueId
	 */
	public Integer getPatientQueueId() {
		return patientQueueId;
	}

	/**
	 * @param patientQueueId
	 *            the patientQueueId to set
	 */
	public void setPatientQueueId(Integer patientQueueId) {
		this.patientQueueId = patientQueueId;
	}

	/**
	 * @return the enteredQueue
	 */
	public Date getEnteredQueue() {
		return enteredQueue;
	}

	/**
	 * @param enteredQueue
	 *            the enteredQueue to set
	 */
	public void setEnteredQueue(Date enteredQueue) {
		this.enteredQueue = enteredQueue;
	}

	/**
	 * @return the consultStart
	 */
	public Date getConsultStart() {
		return consultStart;
	}

	/**
	 * @param consultStart
	 *            the consultStart to set
	 */
	public void setConsultStart(Date consultStart) {
		this.consultStart = consultStart;
	}

	/**
	 * @return the consultEnd
	 */
	public Date getConsultEnd() {
		return consultEnd;
	}

	/**
	 * @param consultEnd
	 *            the consultEnd to set
	 */
	public void setConsultEnd(Date consultEnd) {
		this.consultEnd = consultEnd;
	}

	/**
	 * @return the exitedQueue
	 */
	public Date getExitedQueue() {
		return exitedQueue;
	}

	/**
	 * @param exitedQueue
	 *            the exitedQueue to set
	 */
	public void setExitedQueue(Date exitedQueue) {
		this.exitedQueue = exitedQueue;
	}

	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * @param patient
	 *            the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * @return the encounter
	 */
	public Encounter getEncounter() {
		return encounter;
	}

	/**
	 * @param encounter
	 *            the encounter to set
	 */
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	/**
	 * @return the notesNumber
	 */
	public Integer getNotesNumber() {
		return notesNumber;
	}

	/**
	 * @param notesNumber
	 *            the notesNumber to set
	 */
	public void setNotesNumber(Integer notesNumber) {
		this.notesNumber = notesNumber;
	}

	@Override
	public String toString() {
		return getPatient() != null ? getPatient().toString() : super.toString();
	}

	@Override
	public int hashCode() {
		return getPatientQueueId() != null ? getPatientQueueId().hashCode() : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PatientQueue) {
			final PatientQueue pq = (PatientQueue) obj;
			return getPatientQueueId() != null ? getPatientQueueId().equals(pq.getPatientQueueId()) : this == obj;
		}

		// use identity comparison
		return this == obj;
	}
}
