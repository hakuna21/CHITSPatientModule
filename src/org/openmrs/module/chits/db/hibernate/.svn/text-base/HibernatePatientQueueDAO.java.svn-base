package org.openmrs.module.chits.db.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants.VisitConcepts;
import org.openmrs.module.chits.PatientQueue;
import org.openmrs.module.chits.Util;
import org.openmrs.module.chits.db.PatientQueueDAO;

/**
 * Database methods for the {@link CHITSService} relating to {@link PatientQueue} records.
 */
public class HibernatePatientQueueDAO implements PatientQueueDAO {
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	public HibernatePatientQueueDAO() {
	}

	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.chits.db.PatientQueueDAO#savePatientQueue(org.openmrs.module.chits.PatientQueue)
	 */
	@Override
	public PatientQueue savePatientQueue(PatientQueue patientQueue) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(patientQueue);
		return patientQueue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.chits.db.PatientQueueDAO#getPatientQueue(java.lang.Integer)
	 */
	@Override
	public PatientQueue getPatientQueue(Integer patientQueueId) throws DAOException {
		return (PatientQueue) sessionFactory.getCurrentSession().get(PatientQueue.class, patientQueueId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.chits.db.PatientQueueDAO#deletePatientQueue(org.openmrs.module.chits.PatientQueue)
	 */
	@Override
	public void deletePatientQueue(PatientQueue patientQueue) throws DAOException {
		// load the patient queue from the DB
		patientQueue = getPatientQueue(patientQueue.getPatientQueueId());
		if (patientQueue != null) {
			// delete the patient queue
			sessionFactory.getCurrentSession().delete(patientQueue);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.chits.db.PatientQueueDAO#getQueuedPatient(org.openmrs.Patient patient)
	 */
	@Override
	public PatientQueue getQueuedPatient(Patient patient) throws DAOException {
		final Criteria crit = sessionFactory.getCurrentSession().createCriteria(PatientQueue.class);

		// include only the patients that haven't exited the queue yet
		crit.add(Expression.isNull("exitedQueue")) //
				.add(Expression.eq("patient", patient)) //
				.setMaxResults(1);

		// return the patient's queue entry (if still in the queue)
		return (PatientQueue) crit.uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.chits.db.PatientQueueDAO#getQueuedPatients(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PatientQueue> getQueuedPatients(Integer start, Integer length) throws DAOException {
		final Criteria crit = sessionFactory.getCurrentSession().createCriteria(PatientQueue.class);

		// include only the patients that haven't exited the queue yet
		crit.add(Expression.isNull("exitedQueue")) //
				.setFirstResult(start != null ? start : 0) //
				.setMaxResults(length != null ? length : Util.getMaximumSearchResults());

		// return the sublist
		return crit.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.chits.db.PatientQueueDAO#getAllQueuedPatients()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PatientQueue> getAllQueuedPatients() throws DAOException {
		final Criteria crit = sessionFactory.getCurrentSession().createCriteria(PatientQueue.class);

		// return the all matches
		return crit.list();
	}

	/**
	 * Calculate average consult times (in seconds)
	 * 
	 * @return The average consult time, or null if no data is available.
	 * @throws DAOException
	 *             if a DAO error occurs.
	 */
	@Override
	public Double getAverageConsultTime() throws DAOException {
		final Number avgTime = (Number) sessionFactory.getCurrentSession().createSQLQuery( //
				"SELECT AVG(1 * TIMEDIFF(consult_end, consult_start)) AS MINS" + //
						"  FROM chits_patient_queue pq " + //
						" WHERE consult_end IS NOT NULL " + //
						"   AND encounter_id IS NOT NULL " + //
						"   AND consult_start IS NOT NULL" //
		).addScalar("MINS", Hibernate.DOUBLE).uniqueResult();

		if (avgTime != null) {
			// convert the hour and minute components to seconds
			final int hhmmss = avgTime.intValue();

			// get seconds component
			int avgSeconds = hhmmss % 100;

			// get minutes component
			avgSeconds += 60 * ((hhmmss / 100) % 100);

			// get hours component
			avgSeconds += 60 * 60 * ((hhmmss / 10000) % 100);

			// return value in seconds
			return new Double(avgSeconds);
		}

		// no available cnosult times
		return null;
	}

	/**
	 * Get the number of queued patients in the database.
	 * 
	 * @return The number of queued patients in the database.
	 */
	@Override
	public int getQueuedPatientsCount() throws DAOException {
		final Criteria crit = sessionFactory.getCurrentSession().createCriteria(PatientQueue.class);

		// include only the patients that haven't exited the queue yet
		crit.add(Expression.isNull("exitedQueue")).setProjection(Projections.rowCount());

		// return the sublist
		return ((Number) crit.uniqueResult()).intValue();
	}

	@Override
	public void save(Object entity) throws DAOException {
		if (entity != null) {
			sessionFactory.getCurrentSession().save(entity);
		}
	}

	@Override
	public void purge(Object entity) throws DAOException {
		if (entity != null) {
			sessionFactory.getCurrentSession().delete(entity);
		}
	}

	@Override
	public int nextNotesNumber(Person person) throws DAOException {
		final Concept notesNumberConcept = Context.getConceptService().getConcept(VisitConcepts.NOTES_NUMBER.getConceptId());
		final Number maxNotesNumber = (Number) sessionFactory.getCurrentSession().createQuery( //
				"SELECT MAX(o.valueNumeric) " //
						+ " FROM Obs o " //
						+ "WHERE o.person = :person " //
						+ "  AND o.concept = :notesNumberConcept " //
						+ "  AND o.voided != true ") //
				.setParameter("person", person) //
				.setParameter("notesNumberConcept", notesNumberConcept) //
				.uniqueResult();

		// return the next available notes number for this patient
		return maxNotesNumber != null ? maxNotesNumber.intValue() + 1 : 1;
	}

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
	@Override
	public void mergePatients(Patient preferred, Patient notPreferred) {
		final PatientQueue preferredInQueue = getQueuedPatient(preferred);
		final PatientQueue notPreferredInQueue = getQueuedPatient(notPreferred);

		if (notPreferredInQueue != null && preferredInQueue != null) {
			// both preferred and not preferred patients currently in the queue, so just end the consult for the 'notPreferred' one
			Context.getService(CHITSService.class).endPatientConsult(Context.getEncounterService(), Context.getConceptService(), Context.getUserService(),
					notPreferredInQueue);
		}

		// change any other references to the 'notPreferred' patient in the queue to the 'preferred' patient
		sessionFactory.getCurrentSession().createQuery("" //
				+ "UPDATE PatientQueue " //
				+ "   SET patient = :preferred " //
				+ " WHERE patient = :notPreferred") //
				.setParameter("preferred", preferred) //
				.setParameter("notPreferred", notPreferred)//
				.executeUpdate();
	}

	/**
	 * Obtains the last (latest) encounter bean in the database for the given patient.
	 * 
	 * @param patient
	 *            The patient to get the latest encounter of
	 * @return The latest {@link Encounter} primary key on record of the given patient based on the encounter timestamp.
	 */
	@Override
	public Integer getLatestEncounterId(Patient patient) {
		final Number maxEncounterId = (Number) sessionFactory.getCurrentSession().createQuery( //
				"SELECT MAX(e.encounterId) " //
						+ " FROM Encounter e " //
						+ "WHERE e.patient = :patient" //
						+ "  AND (e.voided IS NULL OR e.voided = false)") //
				.setParameter("patient", patient) //
				.uniqueResult();

		// send back the primary key of the latest encounter
		return (maxEncounterId != null) ? maxEncounterId.intValue() : null;
	}
}
