package org.openmrs.module.chits.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.openmrs.Obs;
import org.openmrs.module.chits.AgeBasedClassifier;
import org.openmrs.module.chits.AgeBasedClassifier.Type;

/**
 * Child Care Tag: utility taglib for writing the "Weight For Age" classification given a patient's age (in months) and weight (in kilograms).
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class WeightForAgeTag extends BaseAgeSensitiveObservationTag {
	/**
	 * The child's weight
	 */
	protected Obs weight;

	/**
	 * Render the weight value.
	 */
	@Override
	protected void writeNumericValue(JspWriter out) throws IOException {
		if (weight != null) {
			// write out weight value
			out.write(Double.toString(weight.getValueNumeric()));
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Render weight for age classification.
	 */
	@Override
	protected void writeClassificationValue(JspWriter out) throws IOException {
		if (weight != null && birthdate != null) {
			// is this for a male or female patient?
			final boolean male = "M".equalsIgnoreCase(weight.getEncounter().getPatient().getGender());

			// write out the weight for age classification
			final int ageInMonths = calculateAgeInMonths(weight);
			out.write(AgeBasedClassifier.getClassification(male ? Type.MALE_WFA_0TO5 : Type.FEMALE_WFA_0TO5, ageInMonths, weight.getValueNumeric(), true));
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Returns the weight observation
	 * 
	 * @return The weight observation
	 */
	public Obs getWeight() {
		return weight;
	}

	/**
	 * Sets the weight and birhtdate from the given observation
	 * 
	 * @param weight
	 *            The weight observation
	 */
	public void setWeight(Obs weight) {
		this.weight = weight;
		if (weight != null && weight.getPerson() != null) {
			super.setBirthdate(weight.getPerson().getBirthdate());
		}
	}
}
