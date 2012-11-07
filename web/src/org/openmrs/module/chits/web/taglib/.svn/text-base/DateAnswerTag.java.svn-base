package org.openmrs.module.chits.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;

/**
 * Utility taglib for writing out a patient's date answer.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class DateAnswerTag extends BaseAgeSensitiveObservationTag {
	/** The observation containing the answer to display */
	private Obs obs;

	public DateAnswerTag() {
		// by default, age tags don't show classification!
		setShowClassification(Boolean.FALSE);
	}

	/**
	 * Render the coded answer value.
	 */
	@Override
	protected void writeNumericValue(JspWriter out) throws IOException {
		if (obs != null && obs.getValueDatetime() != null) {
			out.write(Context.getDateFormat().format(obs.getValueDatetime()));
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
