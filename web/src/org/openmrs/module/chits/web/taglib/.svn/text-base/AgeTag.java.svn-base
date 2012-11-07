package org.openmrs.module.chits.web.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspWriter;

import org.openmrs.Obs;

/**
 * Utility taglib for writing out a patient's age.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class AgeTag extends BaseAgeSensitiveObservationTag {
	/**
	 * If specified, then the age is calculated based on this date
	 */
	private Date on;

	/**
	 * If the age to be shown should only be in months
	 */
	private Boolean monthsOnly = Boolean.FALSE;

	/**
	 * If the age to be shown should only be in weeks
	 */
	private Boolean weeksOnly = Boolean.FALSE;

	/**
	 * If the age to be shown should only be in days
	 */
	private Boolean daysOnly = Boolean.FALSE;

	public AgeTag() {
		// by default, age tags don't show classification!
		setShowClassification(Boolean.FALSE);
	}

	/**
	 * Render BMI.
	 */
	@Override
	protected void writeNumericValue(JspWriter out) throws IOException {
		if (birthdate != null) {
			// create a dummy observation representing the date of which to calculate the patient's age from
			final Obs dummyObs = new Obs();
			dummyObs.setObsDatetime(on != null ? on : new Date());

			// calculate the patient's age in months
			final int ageInMonths = calculateAgeInMonths(dummyObs);

			// write out patient's age
			if (daysOnly != null && daysOnly) {
				// print out the age in days
				final int ageInAbsoluteDays = calculateAgeInDays(dummyObs);

				if (weeksOnly != null && weeksOnly) {
					final int ageInWeeks = ageInAbsoluteDays / 7;
					final int ageInDays = ageInAbsoluteDays % 7;

					// write out weeks first
					out.write(Integer.toString(ageInWeeks));
					out.write(ageInWeeks != 1 ? " wks. " : " wk. ");

					// then write out days
					out.write(Integer.toString(ageInDays));
					out.write(ageInDays != 1 ? " days" : " day");
				} else {
					out.write(Integer.toString(ageInAbsoluteDays));
					out.write(ageInAbsoluteDays != 1 ? " days" : " day");
				}
			} else if (weeksOnly != null && weeksOnly) {
				// print out the age in weeks
				final int ageInWeeks = calculateAgeInWeeks(dummyObs);
				out.write(Integer.toString(ageInWeeks));
				out.write(ageInWeeks != 1 ? " wks." : " wk.");
			} else if (monthsOnly != null && monthsOnly) {
				// print out the age in months
				out.write(Integer.toString(ageInMonths));
				out.write(ageInMonths != 1 ? " mos." : " mo.");
			} else {
				final int years = ageInMonths / 12;
				final int months = ageInMonths % 12;
				if (years > 0) {
					out.write(Integer.toString(years));
					out.write(years != 1 ? " yrs. " : " yr. ");
				}

				out.write(Integer.toString(months));
				out.write(months != 1 ? " mos." : " mo.");
			}
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Render BMI classification based on age.
	 */
	@Override
	protected void writeClassificationValue(JspWriter out) throws IOException {
		// classify as adult, child?
		out.write("#TODO");
	}

	public Date getOn() {
		return on;
	}

	public void setOn(Date on) {
		this.on = on;
	}

	public Boolean getMonthsOnly() {
		return monthsOnly;
	}

	public void setMonthsOnly(Boolean monthsOnly) {
		this.monthsOnly = monthsOnly;
	}

	public Boolean getWeeksOnly() {
		return weeksOnly;
	}

	public void setWeeksOnly(Boolean weeksOnly) {
		this.weeksOnly = weeksOnly;
	}

	public Boolean getDaysOnly() {
		return daysOnly;
	}

	public void setDaysOnly(Boolean daysOnly) {
		this.daysOnly = daysOnly;
	}
}
