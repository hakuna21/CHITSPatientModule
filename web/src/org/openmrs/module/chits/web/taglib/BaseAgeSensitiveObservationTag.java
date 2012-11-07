package org.openmrs.module.chits.web.taglib;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.openmrs.Obs;

/**
 * Utility taglib for writing out an observation that is sensitive to the patient's age at the time the observation was taken.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public abstract class BaseAgeSensitiveObservationTag extends BodyTagSupport {
	/**
	 * Log
	 */
	protected final Log log = LogFactory.getLog(getClass());

	/** Formatter for BMI can supply up to one decimal point */
	protected static final DecimalFormat BMI_FMT = new DecimalFormat("0.#");

	/** Formatter for ratios can supply up to two decimal points */
	protected static final DecimalFormat RATIO_FMT = new DecimalFormat("0.##");

	/**
	 * The patient's birthdate
	 */
	protected Date birthdate;

	/** Whether to show the value */
	protected Boolean showValue = Boolean.TRUE;

	/** Whether to show the classification */
	protected Boolean showClassification = Boolean.TRUE;

	/** Text to display if no data has been entered */
	protected String noEnteredDataText = "no entered data";

	/**
	 * Concrete classes should implement how the value is written to output
	 * 
	 * @param out
	 *            {@link JspWriter} to write content to
	 * @throws IOException
	 */
	protected abstract void writeNumericValue(JspWriter out) throws IOException;

	/**
	 * Concrete classes should implement how the classification of the value is written to output
	 * 
	 * @param out
	 *            {@link JspWriter} to write content to
	 * @throws IOException
	 */
	protected abstract void writeClassificationValue(JspWriter out) throws IOException;

	/**
	 * Convenience method to obtain the patient's (maximum) age in months.
	 * 
	 * @return The patient's age in months at the time the given observations were taken.
	 */
	protected int calculateAgeInMonths(Obs... observations) {
		long dateTaken = Long.MIN_VALUE;

		if (observations != null) {
			for (Obs obs : observations) {
				if (obs != null && obs.getObsDatetime() != null) {
					// determine the date the observation was taken (take the greater of the two)
					dateTaken = Math.max(dateTaken, obs.getObsDatetime().getTime());
				}
			}
		}

		if (dateTaken == Long.MIN_VALUE || dateTaken < birthdate.getTime()) {
			// unable to calculate patient's age!
			return 0;
		}

		// calculate how old this person was, in months, when the observations were taken
		final Period period = new Interval(birthdate.getTime(), dateTaken).toPeriod();
		final int ageInMonths = period.getYears() * 12 + period.getMonths();

		// return the patient's age in months
		return ageInMonths;
	}

	/**
	 * Convenience method to obtain the patient's (maximum) age in days.
	 * 
	 * @return The patient's age in weeks at the time the given observations were taken.
	 */
	protected int calculateAgeInDays(Obs... observations) {
		long dateTaken = Long.MIN_VALUE;

		if (observations != null) {
			for (Obs obs : observations) {
				// determine the date the observation was taken (take the greater of the two)
				dateTaken = Math.max(dateTaken, obs.getObsDatetime().getTime());
			}
		}

		if (dateTaken == Long.MIN_VALUE || dateTaken < birthdate.getTime()) {
			// unable to calculate patient's age!
			return 0;
		}

		// calculate how old this person was, in months, when the observations were taken
		final Duration duration = new Interval(birthdate.getTime(), dateTaken).toDuration();
		final int ageInDays = (int) (duration.getStandardDays());

		// return the patient's age in weeks
		return ageInDays;
	}

	/**
	 * Convenience method to obtain the patient's (maximum) age in weeks.
	 * 
	 * @return The patient's age in weeks at the time the given observations were taken.
	 */
	protected int calculateAgeInWeeks(Obs... observations) {
		return calculateAgeInDays(observations) / 7;
	}

	/**
	 * Render observation.
	 * 
	 * @return return result code
	 */
	public int doStartTag() throws JspException {
		try {
			final JspWriter out = pageContext.getOut();
			if (birthdate != null || !requiresBirthdate()) {
				try {
					if (showValue != null && showValue) {
						writeNumericValue(out);
						if (showClassification != null && showClassification) {
							out.write(" (");
							writeClassificationValue(out);
							out.write(")");
						}
					} else if (showClassification != null && showClassification) {
						writeClassificationValue(out);
					}
				} catch (NumberFormatException nfe) {
					out.write("[Invalid Data]");
				}
			} else {
				out.write(noEnteredDataText);
			}
		} catch (IOException e) {
			log.error("Unable to generate chart servlet url", e);
		}

		return EVAL_BODY_BUFFERED;
	}

	protected boolean requiresBirthdate() {
		return true;
	}

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		try {
			if (bodyContent != null) {
				bodyContent.writeOut(bodyContent.getEnclosingWriter());
			}
		} catch (java.io.IOException e) {
			throw new JspTagException("IO Error: " + e.getMessage());
		}

		return EVAL_PAGE;
	}

	/**
	 * @return the birthdate
	 */
	public Date getBirthdate() {
		return birthdate;
	}

	/**
	 * @param birthdate
	 *            the birthdate to set
	 */
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @return the showValue
	 */
	public Boolean getShowValue() {
		return showValue;
	}

	/**
	 * @param showValue
	 *            the showValue to set
	 */
	public void setShowValue(Boolean showValue) {
		this.showValue = showValue;
	}

	/**
	 * @return the showClassification
	 */
	public Boolean getShowClassification() {
		return showClassification;
	}

	/**
	 * @param showClassification
	 *            the showClassification to set
	 */
	public void setShowClassification(Boolean showClassification) {
		this.showClassification = showClassification;
	}

	public String getNoEnteredDataText() {
		return noEnteredDataText;
	}

	public void setNoEnteredDataText(String noEnteredDataText) {
		this.noEnteredDataText = noEnteredDataText;
	}
}
