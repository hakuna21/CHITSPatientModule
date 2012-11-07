package org.openmrs.module.chits;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.module.chits.eccdprogram.ChildCareUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServiceTypes;

/**
 * Consult entry form for the child care program.
 * 
 * @author Bren
 */
public class ChildCareConsultEntryForm extends PatientConsultEntryForm {
	/** The child's mother */
	private Patient mother;

	/** The child's father */
	private Patient father;

	/** Map of newborn screening result by concept */
	private Map<Concept, Boolean> newbornScreeningResults = new LinkedHashMap<Concept, Boolean>();

	/** Map of breastfeeding records by concept */
	private Map<Concept, Obs> breastFeedingInformation = new LinkedHashMap<Concept, Obs>();

	/** Map of vaccination records by concept */
	private Map<Concept, VaccinationRecord> vaccinationRecords = new LinkedHashMap<Concept, VaccinationRecord>();

	/** Stores a new service to be saved */
	private ServiceRecord serviceRecord = new ServiceRecord();

	/** Specifies a 'User' record that identifies the person that has administered the vaccination */
	private User administeredBy;

	/** If specified, user is changing the father reference */
	private Integer newFatherId;

	/** If set, then the user is intentionally not setting the 'father' record */
	private boolean fatherUnknown;

	/** If specified, user is changing the mother reference */
	private Integer newMotherId;

	/** Flag indicates if the child care program is already concluded for this patient */
	private Boolean programConcluded;

	public Patient getMother() {
		return mother;
	}

	public void setMother(Patient mother) {
		this.mother = mother;
	}

	public Patient getFather() {
		return father;
	}

	public void setFather(Patient father) {
		this.father = father;
	}

	public Map<Concept, Boolean> getNewbornScreeningResults() {
		return newbornScreeningResults;
	}

	public void setNewbornScreeningResults(Map<Concept, Boolean> newbornScreeningResults) {
		this.newbornScreeningResults = newbornScreeningResults;
	}

	public Map<Concept, Obs> getBreastFeedingInformation() {
		return breastFeedingInformation;
	}

	public void setBreastFeedingInformation(Map<Concept, Obs> breastFeedingInformation) {
		this.breastFeedingInformation = breastFeedingInformation;
	}

	public Map<Concept, VaccinationRecord> getVaccinationRecords() {
		return vaccinationRecords;
	}

	public void setVaccinationRecords(Map<Concept, VaccinationRecord> vaccinationRecords) {
		this.vaccinationRecords = vaccinationRecords;
	}

	public User getAdministeredBy() {
		return administeredBy;
	}

	public void setAdministeredBy(User administeredBy) {
		this.administeredBy = administeredBy;
	}

	public ServiceRecord getServiceRecord() {
		return serviceRecord;
	}

	public void setServiceRecord(ServiceRecord serviceRecord) {
		this.serviceRecord = serviceRecord;
	}

	public Integer getNewFatherId() {
		return newFatherId;
	}

	public void setNewFatherId(Integer newFatherId) {
		this.newFatherId = newFatherId;
	}

	public boolean isFatherUnknown() {
		return fatherUnknown;
	}

	public void setFatherUnknown(boolean fatherUnknown) {
		this.fatherUnknown = fatherUnknown;
	}

	public Integer getNewMotherId() {
		return newMotherId;
	}

	public void setNewMotherId(Integer newMotherId) {
		this.newMotherId = newMotherId;
	}

	public boolean isProgramConcluded() {
		if (programConcluded == null) {
			programConcluded = ChildCareUtil.isProgramClosedFor(getPatient());
		}

		return programConcluded;
	}

	public void setProgramConcluded(boolean programConcluded) {
		this.programConcluded = programConcluded;
	}

	/**
	 * Contains vaccination records that can be added to the child care data.
	 * 
	 * @author Bren
	 */
	public static class VaccinationRecord {
		private boolean include;
		private Obs antigen;
		private Obs dateAdministered;
		private Obs healthFacility;

		public boolean isInclude() {
			return include;
		}

		public void setInclude(boolean include) {
			this.include = include;
		}

		public Obs getAntigen() {
			return antigen;
		}

		public void setAntigen(Obs antigen) {
			this.antigen = antigen;
		}

		public Obs getDateAdministered() {
			return dateAdministered;
		}

		public void setDateAdministered(Obs dateAdministered) {
			this.dateAdministered = dateAdministered;
		}

		public Obs getHealthFacility() {
			return healthFacility;
		}

		public void setHealthFacility(Obs healthFacility) {
			this.healthFacility = healthFacility;
		}
	}

	/**
	 * Contains a service records that can be used for adding a new service record entry.
	 * 
	 * @author Bren
	 */
	public static class ServiceRecord {
		private Obs serviceType;
		private Obs dateGiven;
		private Obs quantityOrDosage;
		private Obs remarks;
		private Obs serviceSource;

		public Obs getServiceType() {
			return serviceType;
		}

		public void setServiceType(Obs serviceType) {
			this.serviceType = serviceType;
		}

		public Obs getDateGiven() {
			return dateGiven;
		}

		public void setDateGiven(Obs dateGiven) {
			this.dateGiven = dateGiven;
		}

		public Obs getQuantityOrDosage() {
			return quantityOrDosage;
		}

		public void setQuantityOrDosage(Obs quantityOrDosage) {
			this.quantityOrDosage = quantityOrDosage;
		}

		public Obs getRemarks() {
			return remarks;
		}

		public void setRemarks(Obs remarks) {
			this.remarks = remarks;
		}

		public Obs getServiceSource() {
			return serviceSource;
		}

		public void setServiceSource(Obs serviceSource) {
			this.serviceSource = serviceSource;
		}

		public boolean isVitaminAServiceType() {
			if (serviceType != null && serviceType.getValueCoded() != null) {
				// return whether the 'service type' is vitamin a
				return ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION.getConceptId().equals(serviceType.getValueCoded().getConceptId());
			} else {
				// if service type not set, then we can indicate that this is a vitamin a service type
				return false;
			}
		}

		public boolean isDewormingServiceType() {
			if (serviceType != null && serviceType.getValueCoded() != null) {
				// return whether the 'service type' is deworming
				return ChildCareServiceTypes.DEWORMING.getConceptId().equals(serviceType.getValueCoded().getConceptId());
			} else {
				// if service type not set, then we can indicate that this is a vitamin a service type
				return false;
			}
		}

		public boolean isFerrousSulfateServiceType() {
			if (serviceType != null && serviceType.getValueCoded() != null) {
				// return whether the 'service type' is ferrous sulfate
				return ChildCareServiceTypes.FERROUS_SULFATE.getConceptId().equals(serviceType.getValueCoded().getConceptId());
			} else {
				// if service type not set, then we can indicate that this is a vitamin a service type
				return false;
			}
		}
	}
}
