package org.openmrs.module.chits.web.taglib;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants.VisitConcepts;
import org.openmrs.module.chits.Util;

/**
 * Utility taglib for writing out a summary of vital signs of an encounter.
 * <p>
 * Vital signs include these concepts:
 * <ul>
 * <li>TEMPERATURE_C
 * <li>Blood Pressure (SBP/DBP)
 * <li>PULSE
 * <li>RESPIRATORY_RATE
 * </ul>
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class VitalSignsTag extends BodyTagSupport {
	/**
	 * Log
	 */
	private final Log log = LogFactory.getLog(getClass());

	/** Formatter for temperature in degrees centigrade can supply up to one decimal point */
	private static final DecimalFormat TEMP_FMT = new DecimalFormat("0.#");

	/** Formatter for displaying the time */
	private static final DateFormat TIME_FMT = new SimpleDateFormat("HH:mm");

	/** By default, show the elapsed amount of time since this observation was taken */
	private Boolean showElapsedSinceTaken = Boolean.TRUE;

	/** By default, both the date and time are shown; setting this to 'false' will not display the date */
	private Boolean showObsDate = Boolean.TRUE;

	/**
	 * The vital signs observation group
	 */
	private Obs vitalSigns;

	/** Text to display if no data has been entered */
	protected String noEnteredDataText = "no entered data";

	/**
	 * Render the value.
	 * 
	 * @return return result code
	 */
	public int doStartTag() throws JspException {
		try {
			final JspWriter out = pageContext.getOut();

			// write out the date of this encounter
			if (showObsDate == null || showObsDate) {
				out.write(Context.getDateFormat().format(vitalSigns.getObsDatetime()));
				out.write(" ");
			}

			out.write(TIME_FMT.format(vitalSigns.getObsDatetime()));
			out.write(" ");

			// get the individual vital signs
			final Obs temp = Functions.observation(vitalSigns, VisitConcepts.TEMPERATURE_C);
			final Obs sbp = Functions.observation(vitalSigns, VisitConcepts.SBP);
			final Obs dbp = Functions.observation(vitalSigns, VisitConcepts.DBP);
			final Obs hr = Functions.observation(vitalSigns, VisitConcepts.PULSE);
			final Obs rr = Functions.observation(vitalSigns, VisitConcepts.RESPIRATORY_RATE);

			// print something if any data is available
			if (temp != null || sbp != null || dbp != null || hr != null || rr != null) {
				out.write("temp: ");
				if (temp != null) {
					out.write(temp.getValueNumeric() != null ? TEMP_FMT.format(temp.getValueNumeric()) : temp.getValueText());
					out.write("&deg;C");
				} else {
					out.write("??");
				}

				out.write(", BP: ");
				if (sbp != null && dbp != null) {
					out.write(sbp.getValueNumeric() != null ? Integer.toString(sbp.getValueNumeric().intValue()) : sbp.getValueText());
					out.write("/");
					out.write(dbp.getValueNumeric() != null ? Integer.toString(dbp.getValueNumeric().intValue()) : dbp.getValueText());
				} else {
					out.write("??/??");
				}

				out.write(", HR: ");
				if (hr != null) {
					out.write(hr.getValueNumeric() != null ? Integer.toString(hr.getValueNumeric().intValue()) : hr.getValueText());
				} else {
					out.write("??");
				}

				out.write(", RR: ");
				if (rr != null) {
					out.write(rr.getValueNumeric() != null ? Integer.toString(rr.getValueNumeric().intValue()) : rr.getValueText());
				} else {
					out.write("??");
				}

				if (showElapsedSinceTaken != null && showElapsedSinceTaken && vitalSigns.getObsDatetime() != null) {
					out.write(" <span class=\"obsElapsedSince\">[");
					out.write(Util.describeAge(vitalSigns.getObsDatetime()));
					out.write(" ago]</span>");
				}
			} else {
				// otherwise, no vital sign data is available
				out.write(noEnteredDataText);
			}
		} catch (IOException e) {
			log.error("Unable to generate vital signs data", e);
		}

		return EVAL_BODY_BUFFERED;
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
	 * @return the vitalSigns
	 */
	public Obs getVitalSigns() {
		return vitalSigns;
	}

	/**
	 * @param vitalSigns
	 *            the vitalSigns to set
	 */
	public void setVitalSigns(Obs vitalSigns) {
		this.vitalSigns = vitalSigns;
	}

	/**
	 * @return the showElapsedSinceTaken
	 */
	public Boolean getShowElapsedSinceTaken() {
		return showElapsedSinceTaken;
	}

	/**
	 * @param showElapsedSinceTaken
	 *            the showElapsedSinceTaken to set
	 */
	public void setShowElapsedSinceTaken(Boolean showElapsedSinceTaken) {
		this.showElapsedSinceTaken = showElapsedSinceTaken;
	}

	public String getNoEnteredDataText() {
		return noEnteredDataText;
	}

	public void setNoEnteredDataText(String noEnteredDataText) {
		this.noEnteredDataText = noEnteredDataText;
	}

	public Boolean getShowObsDate() {
		return showObsDate;
	}

	public void setShowObsDate(Boolean showObsDate) {
		this.showObsDate = showObsDate;
	}
}
