package org.openmrs.module.chits.web.controller.reports;

import java.util.Date;

/**
 * Specifies report parameters.
 * 
 * @author Bren
 */
public class ReportForm {
	/** Start date of report */
	private Date startDate;

	/** End date of report */
	private Date endDate;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
