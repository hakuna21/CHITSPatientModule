package org.openmrs.module.chits;

import org.openmrs.Patient;

public class PatientForm {
	/** The patient to use for the form */
	private Patient patient;

	/** The patient's mother */
	private Patient mother;

	/** User is allowed to create a 'new' family folder instead of specifyig an existing one; this will contain the information for 'new' family folders */
	private FamilyFolder familyFolder;

	/** Populated if the patient is currently in the queue */
	private PatientQueue patientQueue;

	/**
	 * Flag used when adding / editing a patient record to indicate if an existing patient record should be used as the 'mother of patient' or if a new patient
	 * record should be created.
	 */
	private boolean existingMother;

	/**
	 * Flag used when adding / editing a patient record to indicate if an existing folder record should be used as the 'family folder of patient' or if a new
	 * patient record should be created.
	 */
	private boolean existingFolder;

	/**
	 * Indicates if this patient should be marked as the head of the family for this folder.
	 */
	private boolean headOftheFamily;

	/**
	 * Indicates if this patient has philhealth information
	 */
	private boolean hasPhilhealth;

	/**
	 * Flag indicating if this is a non-patient (e.g., if the record is just a "mother's name" record.
	 */
	private boolean nonPatient;

	/**
	 * Flag indicating if this patient is marked with '4Ps'
	 */
	private boolean fourPs;

	/** The version of the encounter at the time the edit page was opened (used for optimistic locking) */
	private long version;

	public Patient getPatient() {
		return patient;
	}

	public Patient getMother() {
		return mother;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public void setMother(Patient mother) {
		this.mother = mother;
	}

	public PatientQueue getPatientQueue() {
		return patientQueue;
	}

	public void setPatientQueue(PatientQueue patientQueue) {
		this.patientQueue = patientQueue;
	}

	public boolean isExistingMother() {
		return existingMother;
	}

	public void setExistingMother(boolean existingMother) {
		this.existingMother = existingMother;
	}

	public boolean isHasPhilhealth() {
		return hasPhilhealth;
	}

	public void setHasPhilhealth(boolean hasPhilhealth) {
		this.hasPhilhealth = hasPhilhealth;
	}

	/**
	 * @return the familyFolder
	 */
	public FamilyFolder getFamilyFolder() {
		return familyFolder;
	}

	/**
	 * @param familyFolder
	 *            the familyFolder to set
	 */
	public void setFamilyFolder(FamilyFolder familyFolder) {
		this.familyFolder = familyFolder;
	}

	/**
	 * @return the existingFolder
	 */
	public boolean isExistingFolder() {
		return existingFolder;
	}

	/**
	 * @param existingFolder
	 *            the existingFolder to set
	 */
	public void setExistingFolder(boolean existingFolder) {
		this.existingFolder = existingFolder;
	}

	/**
	 * @return the headOftheFamily
	 */
	public boolean isHeadOftheFamily() {
		return headOftheFamily;
	}

	/**
	 * @param headOftheFamily
	 *            the headOftheFamily to set
	 */
	public void setHeadOftheFamily(boolean headOftheFamily) {
		this.headOftheFamily = headOftheFamily;
	}

	public boolean isNonPatient() {
		return nonPatient;
	}

	public void setNonPatient(boolean nonPatient) {
		this.nonPatient = nonPatient;
	}

	public boolean isFourPs() {
		return fourPs;
	}

	public void setFourPs(boolean fourPs) {
		this.fourPs = fourPs;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
