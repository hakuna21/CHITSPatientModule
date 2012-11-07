package org.openmrs.module.chits.db;

import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.PatientQueue;

/**
 * Database methods for the {@link CHITSService} relating to {@link PatientQueue} records.
 */
public interface PatientQueueDAO {
	/**
	 * Inserts or updates a {@link PatientQueue} record in the database.
	 * 
	 * @param patientQueue
	 *            The record to insert or update into the database
	 * @return The updated PatientQueue record (e.g., with the 'primary key' value set for 'inserted' records)
	 * @throws DAOException
	 *             If any database errors occur.
	 */
	public PatientQueue savePatientQueue(PatientQueue patientQueue) throws DAOException;

	/**
	 * Loads a {@link PatientQueue} record given the patient queue id.
	 * 
	 * @param patientQueueId
	 *            The primary key id of the patient queue record to load.
	 * @return The patient queue record with the given id
	 * @throws DAOException
	 *             If any database errors occur.
	 */
	public PatientQueue getPatientQueue(Integer patientQueueId) throws DAOException;

	/**
	 * Delete a patient queue entry.
	 * 
	 * @param patientQueue
	 *            The patient queue entry to delete
	 * @throws DAOException
	 *             If any database errors occur.
	 */
	public void deletePatientQueue(PatientQueue patientQueue) throws DAOException;

	/**
	 * Returns all patients in the queue that have not yet exited the queue, ordered by ascending 'enteredQueue' value starting from the 'start' row and
	 * including a maximum of 'length' records.
	 * 
	 * @param start
	 *            The start row to return
	 * @param length
	 *            The maximum number of results to return
	 * @return All patients in the queue that have not yet exited the queue.
	 * @throws DAOException
	 *             If any database errors occur.
	 */
	public List<PatientQueue> getQueuedPatients(Integer start, Integer length) throws DAOException;

	/**
	 * Returns all patients in the queue that have not yet exited the queue, ordered by ascending 'enteredQueue' value.
	 * 
	 * @return All patients in the queue that have not yet exited the queue.
	 * @throws DAOException
	 *             If any database errors occur.
	 */
	public List<PatientQueue> getAllQueuedPatients() throws DAOException;

	/**
	 * Returns the {@link PatientQueue} entry for the given {@link Patient} if the patient is still in the queue, or null if the patient is not in the queue.
	 * 
	 * @param patient
	 *            The {@link Patient} to search for in the queue
	 * @return The {@link PatientQueue} entry for this {@link Patient} if still in the queue, or null if the {@link Patient} is no longer in the queue.
	 * @throws DAOException
	 *             If any database errors occur.
	 */
	public PatientQueue getQueuedPatient(Patient patient) throws DAOException;

	/**
	 * Calculate average consult times.
	 * 
	 * @return The average consult time, or null if no data is available.
	 * @throws DAOException
	 *             if a DAO error occurs.
	 */
	public Double getAverageConsultTime() throws DAOException;

	/**
	 * Get the number of queued patients in the database.
	 * 
	 * @return The number of queued patients in the database.
	 */
	public int getQueuedPatientsCount() throws DAOException;

	/**
	 * Saves an entity bean
	 * 
	 * @param entity
	 *            The entity bean to save
	 * @throws DAOException
	 */
	public void save(Object entity) throws DAOException;

	/**
	 * Purges an entity bean
	 * 
	 * @param entity
	 *            The entity bean to purge
	 * @throws DAOException
	 */
	public void purge(Object entity) throws DAOException;

	/**
	 * Obtains the next available notes number for use for this patient's encounters.
	 * 
	 * @param person
	 *            The person to get the next available notes number for
	 * @return The next available 'notes' number for this patient.
	 * @throws DAOException
	 */
	public int nextNotesNumber(Person person) throws DAOException;

	/**
	 * Merge the two patients such that references to the notPreferred {@link Patient} in the queue refer to the preferred patient.
	 * <p>
	 * NOTE: This currently does not re-order the notes numbers.
	 * 
	 * @param preferred
	 *            The Patient to change the references in the queue to.
	 * @param notPreferred
	 *            The Patient to change the references in the queue from.
	 */
	public void mergePatients(Patient preferred, Patient notPreferred);

	/**
	 * Obtains the last (latest) encounter bean in the database for the given patient.
	 * 
	 * @param patient
	 *            The patient to get the latest encounter of
	 * @return The latest {@link Encounter} primary key on record of the given patient based on the encounter timestamp.
	 */
	public Integer getLatestEncounterId(Patient patient);
}
