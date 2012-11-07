package org.openmrs.module.chits;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.BaseOpenmrsData;

/**
 * Contains household information.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class HouseholdInformation extends BaseOpenmrsData implements Serializable {
	/** Primary key id */
	private Integer householdInformationId;

	/** Access to Improved or Safe Water Supply */
	private String accessToWaterSupply;

	/** Household Toilet Facility */
	private String toiletFacility;

	/** Household Toilet Location */
	private String toiletLocation;

	/** Disposal of Solid Waste */
	private String disposalOfSolidWaste;

	/** Date First Inspected */
	private Date dateFirstInspected;

	/** Re-inspection date */
	private Date reinspectionDate;

	/** FamilyFolder members of this household */
	private Set<FamilyFolder> familyFolders;

	@Override
	public Integer getId() {
		return getHouseholdInformationId();
	}

	@Override
	public void setId(Integer id) {
		setHouseholdInformationId(id);
	}

	public Integer getHouseholdInformationId() {
		return householdInformationId;
	}

	public void setHouseholdInformationId(Integer householdInformationId) {
		this.householdInformationId = householdInformationId;
	}

	public String getAccessToWaterSupply() {
		return accessToWaterSupply;
	}

	public void setAccessToWaterSupply(String accessToWaterSupply) {
		this.accessToWaterSupply = accessToWaterSupply;
	}

	public String getToiletFacility() {
		return toiletFacility;
	}

	public void setToiletFacility(String toiletFacility) {
		this.toiletFacility = toiletFacility;
	}

	public String getToiletLocation() {
		return toiletLocation;
	}

	public void setToiletLocation(String toiletLocation) {
		this.toiletLocation = toiletLocation;
	}

	public String getDisposalOfSolidWaste() {
		return disposalOfSolidWaste;
	}

	public void setDisposalOfSolidWaste(String disposalOfSolidWaste) {
		this.disposalOfSolidWaste = disposalOfSolidWaste;
	}

	public Date getDateFirstInspected() {
		return dateFirstInspected;
	}

	public void setDateFirstInspected(Date dateFirstInspected) {
		this.dateFirstInspected = dateFirstInspected;
	}

	public Date getReinspectionDate() {
		return reinspectionDate;
	}

	public void setReinspectionDate(Date reinspectionDate) {
		this.reinspectionDate = reinspectionDate;
	}

	public Set<FamilyFolder> getFamilyFolders() {
		if (familyFolders == null) {
			familyFolders = new HashSet<FamilyFolder>();
		}

		return familyFolders;
	}

	public void setFamilyFolders(Set<FamilyFolder> familyFolders) {
		this.familyFolders = familyFolders;
	}
}
