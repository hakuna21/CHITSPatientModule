package org.openmrs.module.chits;

import java.util.List;

import org.openmrs.Patient;

public class TransferMembersForm {
	/** The patients to transfer */
	private List<Patient> patients;

	/** The family folder to transfer the members to */
	private FamilyFolder transferTo;

	/** The new family folder to transfer the members to */
	private FamilyFolder newFolder;

	/** Whether to transfer to an existing folder (transferTo) or to a new folder (newFolder) */
	private boolean existingFolder;

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public FamilyFolder getTransferTo() {
		return transferTo;
	}

	public void setTransferTo(FamilyFolder transferTo) {
		this.transferTo = transferTo;
	}

	public FamilyFolder getNewFolder() {
		return newFolder;
	}

	public void setNewFolder(FamilyFolder newFolder) {
		this.newFolder = newFolder;
	}

	public boolean isExistingFolder() {
		return existingFolder;
	}

	public void setExistingFolder(boolean existingFolder) {
		this.existingFolder = existingFolder;
	}
}
