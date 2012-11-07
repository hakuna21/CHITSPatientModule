package org.openmrs.module.chits.webservices.rest.resource;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.CachedProgramConceptId;
import org.openmrs.module.chits.webservices.rest.resource.EncounterRecordResource.EncounterRecord;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Supports loading and updating of an encounter record record. Concrete subclasses must implement the actual loading and saving of the record.
 * <p>
 * This class implements various validation and rules, including:
 * <ul>
 * <li>Updating of record for a patient not enrolled in the program (or with the program already closed) will be ignored.
 * <li>Existing data (such as vaccination and breast feeding information) will not be updated.
 * </ul>
 * 
 * @author Bren
 */
public abstract class EncounterRecordResource<T extends EncounterRecord> extends DataDelegatingCrudResource<T> {
	/** The patient service */
	private PatientService patientService;

	/** The CHITS service */
	private CHITSService chitsService;

	/** The encounter service */
	private EncounterService encounterService;

	/**
	 * Returns the program that this resource represents to be used for filtering the results of
	 * {@link #searchVisitsByBarangayCode(String, Long, Long, RequestContext)}.
	 * <p>
	 * This method may return null.
	 * 
	 * @return
	 */
	protected abstract CachedProgramConceptId getProgram();

	/**
	 * Loads the the given patient's {@link EncounterRecord}. If the patient is no longer enrolled in the program, the 'enrolled' flag should be set to 'false',
	 * otherwise should be set to 'true'.
	 * 
	 * @param patient
	 *            The patient to fill-in the recor dof
	 * @param record
	 *            The record to fill-in
	 */
	protected abstract T load(Patient patient);

	/**
	 * Saves the encounter record into the database and updates the encRecord to reflect the changes.
	 * 
	 * @param encRecord
	 *            Going in, this contains the values to be persisted. Going out, this should reflect the updated values.
	 * @throws ParseException
	 *             in case the data contains unparseable data
	 */
	protected abstract void persist(Patient patient, T encRecord) throws ParseException;

	/**
	 * Loads the encounter record for the patient with the given UUID.
	 */
	@Override
	public T getByUniqueId(String uuid) {
		// load the patient by UUID
		final Patient patient = getPatientService().getPatientByUuid(uuid);
		if (patient != null) {
			// Load and return the encounter record for the patient
			final T record = load(patient);

			// store patient's UUID as the record's UUID
			record.setUuid(patient.getUuid());

			// store 'version' information in the 'dateChanged' field based on the last encounter timestamp
			record.setDateChanged(getLastEncounterTimestamp(patient));

			// send back the populated record
			return record;
		}

		// no patient with the given UUID
		return null;
	}

	/**
	 * Saves the encounter record and returns the updated version.
	 * <p>
	 * This method handles a 'ParseException' by the persist() method in case the data contains unparseable data.
	 * 
	 * @throws IllegalArgumentException
	 *             If the data is unparseable
	 */
	@Override
	protected T save(T encRecord) throws IllegalArgumentException {
		try {
			final Patient patient = getPatientService().getPatientByUuid(encRecord.getUuid());
			if (patient != null) {
				// invoke subclass to update the encounter record
				persist(patient, encRecord);

				// update the record's 'version' with the 'dateChanged' value
				encRecord.setDateChanged(getLastEncounterTimestamp(patient));
			}

			// return the updated record
			return encRecord;
		} catch (ParseException pe) {
			// wrap parse errors as runtime 'IllegalArgumentException'
			throw new IllegalArgumentException("Invalid data: " + pe.getMessage());
		}
	}

	/**
	 * This method searches for the latest encounter of the patient and returns the dateChanged value of that encounter (if found otherwise returns epoch '0').
	 * <p>
	 * This would primarily be used for version information and concurrency checking.
	 * 
	 * @return The timestamp of the last encounter of the patient.
	 */
	protected Date getLastEncounterTimestamp(Patient patient) {
		// get the latest encounter of this patient
		final Encounter enc = getCHITSService().getLatestEncounter(patient);

		// return the timestamp value of the encounter, if available; otherwise, return epoch '0'
		if (enc != null) {
			if (enc.getDateChanged() != null) {
				return enc.getDateChanged();
			} else if (enc.getDateCreated() != null) {
				return enc.getDateCreated();
			} else if (enc.getEncounterDatetime() != null) {
				return enc.getEncounterDatetime();
			}
		}

		// no available data...
		return new Date(0);
	}

	/**
	 * Returns the attributes that should be exposed by the delegate. This only includes attributes of the {@link EncounterRecord}; if subclasses add
	 * attributes, then the resource subclass should add the properties by overriding this method.
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		final DelegatingResourceDescription desc = new DelegatingResourceDescription();

		// patient ID for disp
		desc.addProperty("uuid");

		// program enrollment status
		desc.addProperty("enrolled");

		// use the 'dateChanged' for concurrency locking via a 'version' property
		desc.addProperty("version");

		// the record contains the key-value mappings of the concept-value data
		desc.addProperty("record");

		// return the fully-built resource description
		return desc;
	}

	/**
	 * Directly creating a new Encounter record is not supported because it would be tantamount to creating a new patient since they share the same UUIDs: use
	 * the update method passing the patient's UUID.
	 * <p>
	 * This method will throw an {@link UnsupportedOperationException}
	 */
	@Override
	protected T newDelegate() {
		// directly creating new encounter records is not supported (only updating is)
		throw new UnsupportedOperationException("Creating new encounter records is not supported, use the update method passing the patient's UUID");
	}

	/**
	 * Directly deleting encounter records is not supported since it would be like deleting a patient since the UUID refers to the patient's UUID.
	 * <p>
	 * This method will throw an {@link UnsupportedOperationException}
	 */
	@Override
	protected void delete(EncounterRecord patient, String reason, RequestContext paramRequestContext) throws ResponseException {
		// deleting of encounter record is not supported
		throw new UnsupportedOperationException("Delete not supported");
	}

	/**
	 * Directly purging records is not supported since it would be like purging a patient since the UUID refers to the patient's UUID.
	 * <p>
	 * This method will throw an {@link UnsupportedOperationException}
	 */
	@Override
	public void purge(EncounterRecord patient, RequestContext paramRequestContext) throws ResponseException {
		// purging of encounter records is not supported
		throw new UnsupportedOperationException("Purge not supported");
	}

	/**
	 * This method loads encounter records that have been modified between the given timestamps.
	 * <p>
	 * NOTE: This query has the potential of missing phantom records that haven't yet been committed into the database. To alleviate the possibility of missing
	 * the phantom records, subsequent synchronize operations should subtract 1 hour (for example) from the 'visitedFrom' value to pickup phantom records; the
	 * down side is that this may re-synchronize records on the mobile unit that are already up-to-date.
	 */
	public NeedsPaging<T> searchVisitsByBarangayCode(String barangayCode, Long visitedFrom, Long visitedTo, RequestContext context) {
		// get patients matching the filter criteria
		final List<Patient> visits = getCHITSService().getAllPatientVisitsByBarangay(barangayCode, visitedFrom, visitedTo, getProgram());

		// generate encounter records for each matching patient
		final List<T> records = new ArrayList<T>();
		for (Patient p : visits) {
			// load the encounter record for this patient
			final T record = load(p);

			// store patient's UUID as the record's UUID
			record.setUuid(p.getUuid());

			// store 'version' information in the 'dateChanged' field based on the last encounter timestamp
			record.setDateChanged(getLastEncounterTimestamp(p));

			// add to results
			records.add(record);
		}

		// send back the encounter records of the matching patients
		return new NeedsPaging<T>(records, context);
	}

	/**
	 * Lazy loads the {@link PatientService}
	 * 
	 * @return The {@link PatientService}
	 */
	protected PatientService getPatientService() {
		if (patientService == null) {
			patientService = Context.getPatientService();
		}

		return patientService;
	}

	/**
	 * Lazy loads the {@link CHITSService}
	 * 
	 * @return The {@link CHITSService}
	 */
	protected CHITSService getCHITSService() {
		if (chitsService == null) {
			chitsService = Context.getService(CHITSService.class);
		}

		return chitsService;
	}

	protected EncounterService getEncounterService() {
		if (encounterService == null) {
			encounterService = Context.getEncounterService();
		}

		return encounterService;
	}

	/**
	 * Encapsulates information about a patient's encounter record in a map of key-valued pairs.
	 * 
	 * This class extends the {@link BaseOpenmrsData} bean to support the {@link DataDelegatingCrudResource} base class, however, none of the attributes are
	 * actually used except the UUID.
	 * 
	 * @author Bren
	 */
	public static abstract class EncounterRecord extends BaseOpenmrsData {
		/** Key-value pairs containing encounter record information specifying concept names */
		private Map<String, String> record = new LinkedHashMap<String, String>();

		/** Flag to indicate if the patient is enrolled in the program */
		private boolean enrolled;

		/**
		 * Implemented to support the {@link BaseOpenmrsData} bean to support the {@link DataDelegatingCrudResource} base class.
		 * <p>
		 * This method always returns null.
		 */
		@Override
		public Integer getId() {
			return null;
		}

		/**
		 * Implemented to support the {@link BaseOpenmrsData} bean to support the {@link DataDelegatingCrudResource} base class.
		 * <p>
		 * This method does nothing.
		 */
		@Override
		public void setId(Integer id) {
		}

		/**
		 * Returns the record map containing the encounter record data.
		 * 
		 * @return The encounter record data
		 */
		public Map<String, String> getRecord() {
			return record;
		}

		/**
		 * Stores the encounter record data.
		 * 
		 * @param record
		 *            the encounter record data
		 */
		public void setRecord(Map<String, String> record) {
			this.record = record;
		}

		/**
		 * Indicates if the patient is enrolled in the program.
		 * 
		 * @return if the patient is enrolled in the program.
		 */
		public boolean isEnrolled() {
			return enrolled;
		}

		/**
		 * Stores the 'enrolled' status of the patient.
		 * 
		 * @param enrolled
		 *            the 'enrolled' status of the patient
		 */
		public void setEnrolled(boolean enrolled) {
			this.enrolled = enrolled;
		}

		/**
		 * Returns the version based on the 'dateChanged' value
		 * 
		 * @return The epoch in milliseconds of the 'dateChanged' attribute
		 */
		public long getVersion() {
			return getDateChanged() != null ? getDateChanged().getTime() : 0;
		}

		/**
		 * Stores the version into the 'dateChagned' value
		 * 
		 * @param version
		 *            The version representing the epoch in milliseconds of the 'dateChanged' attribute
		 */
		public void setVersion(long version) {
			final Date dateChanged = new Date(version);
			setDateChanged(dateChanged);
		}
	}
}
