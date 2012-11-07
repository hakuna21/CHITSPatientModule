package org.openmrs.module.chits.web.taglib;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.servlet.jsp.JspWriter;

import org.openmrs.Obs;

/**
 * Utility taglib for writing out a patient's numeric answer.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class NumericAnswerTag extends BaseAgeSensitiveObservationTag {
	/** The observation containing the answer to display */
	private Obs obs;

	/** Formats a numeric value */
	private NumberFormat FMT = new DecimalFormat("0.#");

	public NumericAnswerTag() {
		// by default, age tags don't show classification!
		setShowClassification(Boolean.FALSE);
	}

	/**
	 * Render the coded answer value.
	 */
	@Override
	protected void writeNumericValue(JspWriter out) throws IOException {
		if (obs != null && obs.getValueNumeric() != null) {
			out.write(FMT.format(obs.getValueNumeric()));
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

	@Override
	protected boolean requiresBirthdate() {
		return false;
	}

	public Obs getObs() {
		return obs;
	}

	public void setObs(Obs obs) {
		this.obs = obs;
	}
}
