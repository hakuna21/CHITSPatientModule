package org.openmrs.module.chits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.chits.Defaulter.DueServiceInfo.ServiceType;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Contains information about a defaulter (i.e., a patient that has missed or is due for a service)
 * <p>
 * This bean is sortable and sorts by descending 'daysOverdue' and ascending patient name.
 * 
 * @author Bren
 */
public class Defaulter implements Comparable<Defaulter> {
	/** The patient that has due services */
	private Patient patient;

	/** The overdue services for this patient */
	private List<DueServiceInfo> dueServices = new ArrayList<DueServiceInfo>();

	@Override
	public int compareTo(Defaulter o) {
		final int maxDaysOverdueForO = o != null && !o.dueServices.isEmpty() ? o.dueServices.get(0).getDaysOverdue() : 0;
		final int maxDaysOverdueForThis = !this.dueServices.isEmpty() ? this.dueServices.get(0).getDaysOverdue() : 0;

		if (maxDaysOverdueForO != maxDaysOverdueForThis) {
			// the larger the 'days overdue', the higher up it should be on the list
			return maxDaysOverdueForO - maxDaysOverdueForThis;
		} else {
			// compare via person names
			return patient.getPersonName().compareTo(o.patient.getPersonName());
		}
	}

	/**
	 * Adds a due service record.
	 * 
	 * @param serviceType
	 *            The ServiceType
	 * @param description
	 *            Description of the due service
	 * @param dateDue
	 *            The due date (used to calculate the 'daysOverdue')
	 */
	public void addDueService(ServiceType serviceType, String description, Date dateDue) {
		this.dueServices.add(new DueServiceInfo(serviceType, description, dateDue));
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public List<DueServiceInfo> getDueServices() {
		return dueServices;
	}

	public void setDueServices(List<DueServiceInfo> dueServices) {
		this.dueServices = dueServices;
	}

	/**
	 * Information about a due service.
	 * <p>
	 * This bean is sortable and sorts by descending 'daysOverdue'.
	 */
	public static class DueServiceInfo implements Comparable<DueServiceInfo> {
		/** The due service type associated with the due service */
		private ServiceType serviceType;

		/** A description that can be used for display to describe the service */
		private String description;

		/** The date that the service was due */
		private Date dateDue;

		/** The number of days that the service has been overdue (from the time that the due service info was generated) */
		private int daysOverdue;

		public DueServiceInfo() {
			// default constructor
		}

		/**
		 * Stores the due service info into the obs value.
		 * 
		 * @param obs
		 *            The observation to store the due service info.
		 */
		public void storeInto(Obs obs) {
			// the concept is the 'SERVICE_TYPE'
			obs.setConcept(Functions.concept(ServiceType.SERVICE_TYPE));
			
			// store the service type in the 'valueCoded' field
			obs.setValueCoded(Functions.concept(serviceType));

			// store description in the 'valueText' field
			obs.setValueText(description);

			// store the 'date due' in the 'valueDatetime' field
			obs.setValueDatetime(dateDue);

			// store the 'daysOverdue' field in the 'valueNumeric' field
			obs.setValueNumeric(new Double(daysOverdue));
		}

		/**
		 * Loads the due service information from the given obs field.
		 * 
		 * @param obs
		 *            The observation to load the due service info from.
		 * @return A {@link DueServiceInfo} bean loaded from the given observation.
		 */
		public void loadFromObs(Obs obs) {
			// load ServiceType from the observation's 'valueCoded' concept
			final Integer conceptId = obs.getValueCoded().getConceptId();
			setServiceType(null);
			for (ServiceType serviceType : ServiceType.values()) {
				if (conceptId.equals(serviceType.getConceptId())) {
					setServiceType(serviceType);
				}
			}

			// load the description from the 'valueText' field
			setDescription(obs.getValueText());

			// load the 'date due' from the 'valueDatetime'
			setDateDue(obs.getValueDatetime());

			// load the 'daysOverdue' field from the 'valueNumeric' field
			setDaysOverdue(obs.getValueNumeric() != null ? obs.getValueNumeric().intValue() : 0);
		}

		/**
		 * Service type constants representing due service information.
		 */
		public static enum ServiceType implements CachedConceptId {
			/** This represents the concept of all due service observations */
			SERVICE_TYPE("CHITS Due Service"), //
			
			/** ECCD Vaccination Service */
			ECCD_VACCINE("CHITS ECCD Vaccine Service Due, type"), //

			/** ECCD Service (Vitamin A, Deworming, or Ferrous Sulfate) */
			ECCD_SERVICE("CHITS ECCD Service Due, type"), //

			/** Maternal Care Prenatal Visit */
			MC_PRENATAL("CHITS Prenatal Service Due, type"), //

			/** Maternal Care Postnatal Visit */
			MC_POSTNATAL("CHITS Postnatal Service Due, type"), //

			/** Tetanus Shots */
			TETANUS("CHITS Tetanus Service Due, type");

			/** The cached concept name and id */
			private final CachedConceptNameId conceptNameId;

			private ServiceType(String conceptName) {
				this.conceptNameId = new CachedConceptNameId(conceptName);
			}

			@Override
			public String getConceptName() {
				return conceptNameId.getName();
			}

			@Override
			public Integer getConceptId() {
				return conceptNameId.getCachedConceptId();
			}
		}

		/**
		 * Initializes a DueServiceInfo completely.
		 * 
		 * @param serviceType
		 *            The service type
		 * @param description
		 *            Description of the due service
		 * @param dateDue
		 *            The due date (used to calculate the 'daysOverdue')
		 */
		public DueServiceInfo(ServiceType serviceType, String description, Date dateDue) {
			this.serviceType = serviceType;
			this.description = description;
			this.dateDue = dateDue;
			this.daysOverdue = DateUtil.daysBetween(dateDue, new Date());
		}

		public ServiceType getServiceType() {
			return serviceType;
		}

		public void setServiceType(ServiceType serviceType) {
			this.serviceType = serviceType;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Date getDateDue() {
			return dateDue;
		}

		public void setDateDue(Date dateDue) {
			this.dateDue = dateDue;
		}

		public int getDaysOverdue() {
			return daysOverdue;
		}

		public void setDaysOverdue(int daysOverdue) {
			this.daysOverdue = daysOverdue;
		}

		@Override
		public int compareTo(DueServiceInfo o) {
			if (o == null) {
				return -1;
			} else {
				// the larger the 'days overdue', the higher up it should be on the list
				return o.daysOverdue - this.daysOverdue;
			}
		}
	}
}
