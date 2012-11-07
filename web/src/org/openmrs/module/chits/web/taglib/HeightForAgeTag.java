package org.openmrs.module.chits.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.openmrs.Obs;
import org.openmrs.module.chits.AgeBasedClassifier;
import org.openmrs.module.chits.AgeBasedClassifier.Type;

/**
 * Child Care Tag: utility taglib for writing the "Height For Age" classification given a patient's age (in months) and height (in centimeters).
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class HeightForAgeTag extends BaseAgeSensitiveObservationTag {
	/**
	 * The child's height
	 */
	protected Obs height;

	/**
	 * Render the height value.
	 */
	@Override
	protected void writeNumericValue(JspWriter out) throws IOException {
		if (height != null) {
			// write out height value
			out.write(Double.toString(height.getValueNumeric()));
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Render height for age classification.
	 */
	@Override
	protected void writeClassificationValue(JspWriter out) throws IOException {
		if (height != null && birthdate != null) {
			// is this for a male or female patient?
			final boolean male = "M".equalsIgnoreCase(height.getEncounter().getPatient().getGender());

			// write out the height for age classification
			final int ageInMonths = calculateAgeInMonths(height);
			out.write(AgeBasedClassifier.getClassification(male ? Type.MALE_HFA_0TO5 : Type.FEMALE_HFA_0TO5, ageInMonths, height.getValueNumeric(), true));
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Returns the height observation
	 * 
	 * @return The height observation
	 */
	public Obs getHeight() {
		return height;
	}

	/**
	 * Sets the height and birhtdate from the given observation
	 * 
	 * @param height
	 *            The height observation
	 */
	public void setHeight(Obs height) {
		this.height = height;
		if (height != null && height.getPerson() != null) {
			super.setBirthdate(height.getPerson().getBirthdate());
		}
	}
}
