package org.openmrs.module.chits.web;

import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.web.dwr.PersonListItem;

/**
 * FamilyFolderListItem for efficient {@link FamilyFolder} transmission over JSON.
 * <p>
 * This list item contains no household information.
 * 
 * @author Bren
 */
public class FamilyFolderListItem {
	private Integer familyFolderId;
	private String code;
	private String name;
	private String address;
	private String barangayCode;
	private String barangayName;
	private String cityCode;
	private String cityName;
	private String headOfTheFamily;
	private String notes;

	public FamilyFolderListItem() {
		// default constructor doesn't initialize attributes
	}

	public FamilyFolderListItem(FamilyFolder familyFolder) {
		familyFolderId = familyFolder.getFamilyFolderId();
		code = familyFolder.getCode();
		name = familyFolder.getName();
		address = familyFolder.getAddress();
		barangayCode = familyFolder.getBarangayCode();
		barangayName = familyFolder.getBarangayName();
		cityCode = familyFolder.getCityCode();
		cityName = familyFolder.getCityName();
		headOfTheFamily = familyFolder.getHeadOfTheFamily() != null ? new PersonListItem(familyFolder.getHeadOfTheFamily()).getPersonName() : null;
	}

	/**
	 * Redirects getter to the familyFolderId attribute.
	 * 
	 * @return The family folder Id
	 */
	public Integer getId() {
		return getFamilyFolderId();
	}

	/**
	 * Redirects setter to the familyFolderId attribute.
	 * 
	 * @param id
	 *            The family folder id to set
	 */
	public void setId(Integer id) {
		setFamilyFolderId(id);
	}

	public Integer getFamilyFolderId() {
		return familyFolderId;
	}

	public void setFamilyFolderId(Integer familyFolderId) {
		this.familyFolderId = familyFolderId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBarangayCode() {
		return barangayCode;
	}

	public void setBarangayCode(String barangayCode) {
		this.barangayCode = barangayCode;
	}

	public String getBarangayName() {
		return barangayName;
	}

	public void setBarangayName(String barangayName) {
		this.barangayName = barangayName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getHeadOfTheFamily() {
		return headOfTheFamily;
	}

	public void setHeadOfTheFamily(String headOfTheFamily) {
		this.headOfTheFamily = headOfTheFamily;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
