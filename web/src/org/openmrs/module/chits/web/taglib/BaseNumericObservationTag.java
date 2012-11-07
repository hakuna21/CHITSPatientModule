package org.openmrs.module.chits.web.taglib;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Util;

/**
 * Utility taglib for writing out the numeric value of an observation.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public abstract class BaseNumericObservationTag extends BodyTagSupport {
	/**
	 * Log
	 */
	protected final Log log = LogFactory.getLog(getClass());

	/** Formatter for inches can supply up to one decimal point */
	protected static final DecimalFormat INCHES_FMT = new DecimalFormat("0.#");

	/** Formatter for centimeters can supply up to one decimal point */
	protected static final DecimalFormat CM_FMT = new DecimalFormat("0.#");

	/** Formatter for pounds can does not display decimal points */
	protected static final DecimalFormat LBS_FMT = new DecimalFormat("0");

	/** Formatter for kilograms can display up to one decimal point */
	protected static final DecimalFormat KGS_FMT = new DecimalFormat("0.##");

	/** Formatter for centigrade values */
	protected static final DecimalFormat TEMP_C_FMT = new DecimalFormat("0.0#");

	/** Formatter for fahrenheit values */
	protected static final DecimalFormat TEMP_F_FMT = new DecimalFormat("0.0");

	/** By default, the 'Date Taken' field is shown. */
	protected Boolean showDateTaken = Boolean.TRUE;

	/** By default, show the elapsed amount of time since this observation was taken */
	protected Boolean showElapsedSinceTaken = Boolean.TRUE;

	/**
	 * The Observation with representing a centimeter value.
	 */
	protected Obs obs;

	/** Concrete classes should implement how the value is written to output */
	protected abstract void writeNumericValue(JspWriter out, double value) throws IOException;

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
			if (obs != null) {
				try {
					final double cm = (obs.getValueNumeric() != null) ? obs.getValueNumeric() : Double.parseDouble(obs.getValueText());
					writeNumericValue(out, cm);
				} catch (NumberFormatException nfe) {
					out.write("[Invalid Data: ");
					out.write(obs.getValueText());
					out.write("]");
				}

				if (showDateTaken != null && showDateTaken && obs.getObsDatetime() != null) {
					out.write(" <span class=\"obsTaken\">");
					out.write("Taken ");
					out.write(Context.getDateFormat().format(obs.getObsDatetime()));
					out.write("</span>");
				}

				if (showElapsedSinceTaken != null && showElapsedSinceTaken && obs.getObsDatetime() != null) {
					out.write(" <span class=\"obsElapsedSince\">[");
					out.write(Util.describeAge(obs.getObsDatetime()));
					out.write(" ago]</span>");
				}
			} else {
				out.write(noEnteredDataText);
			}
		} catch (IOException e) {
			log.error("Unable to generate chart servlet url", e);
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
	 * @return the obs
	 */
	public Obs getObs() {
		return obs;
	}

	/**
	 * @param obs
	 *            the obs to set
	 */
	public void setObs(Obs obs) {
		this.obs = obs;
	}

	/**
	 * @return the showDateTaken
	 */
	public Boolean isShowDateTaken() {
		return showDateTaken;
	}

	/**
	 * @param showDateTaken
	 *            the showDateTaken to set
	 */
	public void setShowDateTaken(Boolean showDateTaken) {
		this.showDateTaken = showDateTaken;
	}

	/**
	 * @return the showDateTaken
	 */
	public Boolean getShowDateTaken() {
		return showDateTaken;
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
}
