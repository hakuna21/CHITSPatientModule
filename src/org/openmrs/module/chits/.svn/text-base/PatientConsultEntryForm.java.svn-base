package org.openmrs.module.chits;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants.VisitConcepts;

/**
 * A form for entering patient consult data.
 * 
 * @author Bren
 */
public class PatientConsultEntryForm extends PatientConsultForm {
	/** An observation map backed by the patient queue's encounter entry */
	private Map<Integer, Obs> observationMap = new LinkedHashMap<Integer, Obs>();

	/** Special field for blood pressure which combines SYSTOLIC and DIASTOLIC blood pressure values */
	private String bloodPressure;

	/** The Date timestamp to use to store in the records (default to current timestamp) */
	private String timestampDate;

	/** The Time timestamp to use to store in the records (default to current timestamp) */
	private String timestampTime;

	/** The drug orders */
	private List<DrugOrderEntry> drugOrders = new ArrayList<DrugOrderEntry>();

	/** The timestamp to use for saving the observations */
	private Date timestamp;

	/** The version of the encounter at the time the edit page was opened (used for optimistic locking) */
	private long version;

	/**
	 * Returns a map keyed by concept name key and backed by the {@link PatientQueue}'s {@link Encounter} bean.
	 * 
	 * @return
	 */
	public Map<Integer, Obs> getObservationMap() {
		return observationMap;
	}

	/**
	 * Convenience method to add an observation to the observation map basedon its concept id.
	 * 
	 * @param obs
	 */
	public void addToObservationMap(Obs obs) {
		observationMap.put(obs.getConcept().getConceptId(), obs);
	}

	/**
	 * @param observationMap
	 *            the observationMap to set
	 */
	public void setObservationMap(Map<Integer, Obs> observationMap) {
		this.observationMap = observationMap;
	}

	/**
	 * @return the bloodPressure
	 */
	public String getBloodPressure() {
		return bloodPressure;
	}

	public String getTimestampDate() {
		return timestampDate;
	}

	public void setTimestampDate(String timestampDate) {
		this.timestampDate = timestampDate;
	}

	public String getTimestampTime() {
		return timestampTime;
	}

	public void setTimestampTime(String timestampTime) {
		this.timestampTime = timestampTime;
	}

	public List<DrugOrderEntry> getDrugOrders() {
		return drugOrders;
	}

	public void setDrugOrders(List<DrugOrderEntry> drugOrders) {
		this.drugOrders = drugOrders;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * Prepare a new pre-populated Obs instance with the given concept.
	 * <p>
	 * NOTE: The person and obsDateTime attributes are left unpopulated -- it is the caller's responsibility to populate these as they are required (non-null)
	 * fields.
	 * 
	 * @param cachedConcept
	 *            The concept to set into the Obs instance
	 * @return A pre-populated Obs instance with the given concept
	 */
	public static Obs newObs(CachedConceptId cachedConcept, Person person) {
		return newObs(Context.getConceptService().getConcept(cachedConcept.getConceptId()), person);
	}

	/**
	 * Prepare a new pre-populated Obs instance with the given concept.
	 * <p>
	 * NOTE: The person and obsDateTime attributes are left unpopulated -- it is the caller's responsibility to populate these as they are required (non-null)
	 * fields.
	 * 
	 * @param cachedConcept
	 *            The concept to set into the Obs instance
	 * @return A pre-populated Obs instance with the given concept
	 */
	public static Obs newObs(Concept concept, Person person) {
		return ObsUtil.newObs(concept, person);
	}

	/**
	 * @param bloodPressure
	 *            the bloodPressure to set
	 */
	public void setBloodPressure(String bloodPressure) {
		this.bloodPressure = bloodPressure;

		// load observation beans
		final Obs sbp = observationMap.get(VisitConcepts.SBP.getConceptId());
		final Obs dbp = observationMap.get(VisitConcepts.DBP.getConceptId());

		if (sbp != null && dbp != null) {
			if (!StringUtils.isEmpty(bloodPressure)) {
				final int separatorPos = bloodPressure.indexOf("/");
				if (separatorPos > -1) {
					sbp.setValueText(bloodPressure.substring(0, separatorPos));
					dbp.setValueText(bloodPressure.substring(separatorPos + 1));
				} else {
					sbp.setValueText(bloodPressure);
					dbp.setValueText("0");
				}
			} else {
				// blood pressure is unspecified
				sbp.setValueText("");
				dbp.setValueText("");
			}
		}
	}

	public static class DrugOrderEntry {
		private String drugId, quantity, instructions, name;

		public String getDrugId() {
			return drugId;
		}

		public void setDrugId(String drugId) {
			this.drugId = drugId;
		}

		public String getQuantity() {
			return quantity;
		}

		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}

		public String getInstructions() {
			return instructions;
		}

		public void setInstructions(String instructions) {
			this.instructions = instructions;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
