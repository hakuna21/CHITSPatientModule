package org.openmrs.module.chits;

import java.io.Serializable;

import org.openmrs.User;

/**
 * Pairs a user to an authorized barangay code (for syncing purposes)
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class UserBarangay implements Serializable {
	/** Primary key id */
	private Integer userBarangayId;

	/** The user reference */
	private User user;

	/** User is authorized to this barangay code */
	private String barangayCode;

	public Integer getUserBarangayId() {
		return userBarangayId;
	}

	public void setUserBarangayId(Integer userBarangayId) {
		this.userBarangayId = userBarangayId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getBarangayCode() {
		return barangayCode;
	}

	public void setBarangayCode(String barangayCode) {
		this.barangayCode = barangayCode;
	}
}
