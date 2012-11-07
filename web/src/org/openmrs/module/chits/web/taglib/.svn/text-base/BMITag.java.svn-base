package org.openmrs.module.chits.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.openmrs.Obs;
import org.openmrs.module.chits.AgeBasedClassifier;
import org.openmrs.module.chits.AgeBasedClassifier.Type;

/**
 * Utility taglib for writing out the BMI value given a patient's weight, height, and age.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class BMITag extends BaseAgeSensitiveObservationTag {
	/**
	 * The weight
	 */
	protected Obs weight;

	/**
	 * The height
	 */
	protected Obs height;

	/**
	 * Render BMI.
	 */
	@Override
	protected void writeNumericValue(JspWriter out) throws IOException {
		if (weight != null && height != null && birthdate != null) {
			// calculate the BMI value
			final double bmi = calculateBMI();

			// write out BMI value
			out.write(BMI_FMT.format(bmi));
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Render BMI classification based on age.
	 */
	@Override
	protected void writeClassificationValue(JspWriter out) throws IOException {
		if (weight != null && height != null && birthdate != null) {
			// write out the BMI classification based on age
			final int ageInMonths = calculateAgeInMonths(height, weight);
			if (ageInMonths <= 5 * 12) {
				// write out the weight-for-length classification
				final String classification = WeightForLengthTag.getClassification(ageInMonths, weight, height);
				out.write(classification != null ? classification : noEnteredDataText);
			} else if (ageInMonths > 5 * 12 && ageInMonths <= 19 * 12) {
				// is this for a male or female patient?
				final boolean male = "M".equalsIgnoreCase(weight.getEncounter().getPatient().getGender());

				// Use BMI chart based on weight and age
				final double kgs = (weight.getValueNumeric() != null) ? weight.getValueNumeric() : Double.parseDouble(weight.getValueText());
				out.write(AgeBasedClassifier.getClassification(male ? Type.MALE_BMIFA_5TO19 : Type.FEMALE_BMIFA_5TO19, ageInMonths, kgs, true));
			} else if (ageInMonths > 19 * 12) {
				// calculate the BMI value
				final double bmi = calculateBMI();

				out.write(AgeBasedClassifier.getClassification(Type.BMI, ageInMonths, bmi));
			} else {
				out.write(AgeBasedClassifier.OUT_OF_RANGE);
			}

			// out.write("[#" + BMI_FMT.format(bmi) + "/" + calculateAgeInMonths(height, weight) + "]");
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Calculate the BMI value.
	 * 
	 * @return The BMI value.
	 */
	private double calculateBMI() {
		// obtain weight and height
		final double cms = (height.getValueNumeric() != null) ? height.getValueNumeric() : Double.parseDouble(height.getValueText());
		final double kgs = (weight.getValueNumeric() != null) ? weight.getValueNumeric() : Double.parseDouble(weight.getValueText());

		// calculate BMI
		final double bmi = kgs / ((cms / 100.0) * (cms / 100.0));

		// send back the BMI value
		return bmi;
	}

	/**
	 * @return the weight
	 */
	public Obs getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(Obs weight) {
		this.weight = weight;
	}

	/**
	 * @return the height
	 */
	public Obs getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(Obs height) {
		this.height = height;
	}
}
