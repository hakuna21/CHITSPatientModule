package org.openmrs.module.chits.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.openmrs.Obs;

/**
 * Utility taglib for writing out a patient's coded answer.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class CodedAnswerTag extends BaseAgeSensitiveObservationTag {
	/** The observation containing the answer to display */
	private Obs obs;

	public CodedAnswerTag() {
		// by default, age tags don't show classification!
		setShowClassification(Boolean.FALSE);
	}

	/**
	 * Render the coded answer value.
	 */
	@Override
	protected void writeNumericValue(JspWriter out) throws IOException {
		if (obs != null && obs.getValueCoded() != null) {
			if (obs.getValueCoded().getName() != null) {
				out.write(obs.getValueCoded().getName().getName());
			} else {
				out.write("#" + obs.getValueCoded().getConceptId());
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
