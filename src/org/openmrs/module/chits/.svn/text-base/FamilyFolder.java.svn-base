package org.openmrs.module.chits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.module.chits.impl.StaticBarangayCodesHolder;

/**
 * Groups patients together into a family.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class FamilyFolder extends BaseOpenmrsData implements Serializable {
	/** Primary key id */
	private Integer familyFolderId;

	/** The family folder code (e.g., "HC01FAM000001") */
	private String code;

	/** Family folder name (e.g., "Reyes") */
	private String name;

	/** Family address */
	private String address;

	/** Barangay code (e.g., 45805015) */
	private String barangayCode;

	/** City code (e.g., 45805000) */
	private String cityCode;

	/** The patient record designated as the head of the family for this family folder */
	private Patient headOfTheFamily;

	/** free-form notes */
	private String notes;

	/** Patients that belong to this family */
	private Set<Patient> patients;

	/** Link to household information, if any */
	private HouseholdInformation householdInformation;

	/** Average family income (free text entry) */
	private String averageFamilyIncome;

	/**
	 * The version of the encounter at the time the edit page was opened (used for optimistic locking) <br/>
	 * NOTE: This is a transient variable!
	 */
	private long version;

	/**
	 * @return the familyFolderId
	 */
	public Integer getFamilyFolderId() {
		return familyFolderId;
	}

	/**
	 * @param familyFolderId
	 *            the familyFolderId to set
	 */
	public void setFamilyFolderId(Integer familyFolderId) {
		this.familyFolderId = familyFolderId;
	}

	@Override
	public Integer getId() {
		return getFamilyFolderId();
	}

	@Override
	public void setId(Integer id) {
		setFamilyFolderId(id);
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the barangay
	 */
	public String getBarangayCode() {
		return barangayCode;
	}

	/**
	 * @param barangay
	 *            the barangay to set
	 */
	public void setBarangayCode(String barangay) {
		this.barangayCode = barangay;
	}

	/**
	 * @return the city
	 */
	public String getCityCode() {
		return cityCode;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCityCode(String city) {
		this.cityCode = city;
	}

	/**
	 * @return the headOfTheFamily
	 */
	public Patient getHeadOfTheFamily() {
		return headOfTheFamily;
	}

	/**
	 * @param headOfTheFamily
	 *            the headOfTheFamily to set
	 */
	public void setHeadOfTheFamily(Patient headOfTheFamily) {
		this.headOfTheFamily = headOfTheFamily;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 *            the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the patients
	 */
	public Set<Patient> getPatients() {
		if (patients == null) {
			patients = new LinkedHashSet<Patient>();
		}

		return this.patients;
	}

	/**
	 * Will only add {@link Patient}s in this list that this folder does not already have
	 * 
	 * @param patients
	 */
	public void addPatients(Collection<Patient> patients) {
		for (Patient patient : patients) {
			addPatient(patient);
		}
	}

	/**
	 * Will add this {@link Patient} if the patients doesn't contain it already
	 * 
	 * @param patient
	 */
	public void addPatient(Patient patient) {
		if (getPatients() == null) {
			this.patients = new LinkedHashSet<Patient>();
		}

		this.patients.add(patient);
	}

	/**
	 * Will remove the {@link Patient} from the list of member patients.
	 * 
	 * @param patient
	 */
	public void removePatient(Patient patient) {
		if (getPatients() == null) {
			this.patients = new LinkedHashSet<Patient>();
		}

		this.patients.remove(patient);
	}

	/**
	 * @param patients
	 *            the patients to set
	 */
	public void setPatients(Set<Patient> patients) {
		this.patients = patients;
	}

	public HouseholdInformation getHouseholdInformation() {
		return householdInformation;
	}

	public void setHouseholdInformation(HouseholdInformation householdInformation) {
		this.householdInformation = householdInformation;
	}

	public String getAverageFamilyIncome() {
		return averageFamilyIncome;
	}

	public void setAverageFamilyIncome(String averageFamilyIncome) {
		this.averageFamilyIncome = averageFamilyIncome;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * Transient field returns the barangay name for the stored barangay code.
	 * 
	 * @return the barangay name for the stored barangay code.
	 */
	public String getBarangayName() {
		final Barangay barangay = StaticBarangayCodesHolder.getInstance().barangays.get(this.getBarangayCode());
		return barangay != null ? barangay.getName() : getBarangayCode();
	}

	/**
	 * Transient field returns the city name for the stored city code.
	 * 
	 * @return the city name for the stored citycode.
	 */
	public String getCityName() {
		final Municipality municipality = StaticBarangayCodesHolder.getInstance().municipalities.get(this.getCityCode());
		return municipality != null ? municipality.getName() : getCityCode();
	}

	/**
	 * Convenience method to retrieve other family folders sharing the household
	 * 
	 * @return All 'other' family folders sharing the household information.
	 */
	public List<FamilyFolder> getFamiliesSharingHousehold() {
		final List<FamilyFolder> otherFamilies = new ArrayList<FamilyFolder>();
		if (getHouseholdInformation() != null && getHouseholdInformation().getFamilyFolders() != null) {
			// add all family folders linked to the household information
			otherFamilies.addAll(getHouseholdInformation().getFamilyFolders());

			// but don't include self...
			otherFamilies.remove(this);
		}

		return otherFamilies;
	}

	@Override
	public String toString() {
		return getCode();
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FamilyFolder) {
			final FamilyFolder ff = (FamilyFolder) obj;
			return getId() != null ? getId().equals(ff.getId()) : this == obj;
		}

		// use identity comparison
		return this == obj;
	}
}
