package org.openmrs.module.chits.eccdprogram;

import java.util.Date;

/**
 * Describes information relating to the due date of a service.
 * 
 * @author Bren
 */
public class ServiceDueInfo {
	public enum ServiceDueInfoType {
		NOT_DUE, DUE, OVERDUE, NOT_ELIGIBLE;
	}

	/** Description of the service due date */
	private ServiceDueInfo.ServiceDueInfoType type;

	/** The date the service is supposed to be due */
	private Date dueDate;

	public ServiceDueInfo.ServiceDueInfoType getType() {
		return type;
	}

	public void setType(ServiceDueInfo.ServiceDueInfoType type) {
		this.type = type;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
}