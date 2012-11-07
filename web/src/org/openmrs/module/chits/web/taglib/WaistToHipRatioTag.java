package org.openmrs.module.chits.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.openmrs.Obs;
import org.openmrs.module.chits.AgeBasedClassifier;
import org.openmrs.module.chits.AgeBasedClassifier.Type;

/**
 * Utility taglib for writing out the hip-to-waist value given a patient's hip and waist circumference.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class WaistToHipRatioTag extends BaseAgeSensitiveObservationTag {
	/**
	 * The waist circumference
	 */
	protected Obs waistCircumference;

	/**
	 * The hip circumference
	 */
	protected Obs hipCircumference;

	/**
	 * Render hip-to-waist ratio.
	 */
	@Override
	protected void writeNumericValue(JspWriter out) throws IOException {
		if (hipCircumference != null && waistCircumference != null && birthdate != null) {
			// calculate the ratio value
			final double ratio = calculateRatio();

			// write out hip-to-waist ratio value
			out.write(RATIO_FMT.format(ratio));
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Render hip-to-waist classification based on age.
	 */
	@Override
	protected void writeClassificationValue(JspWriter out) throws IOException {
		if (hipCircumference != null && waistCircumference != null && birthdate != null) {
			// calculate the ratio value
			final double ratio = calculateRatio();

			// is this for a male or female patient?
			final boolean male = "M".equalsIgnoreCase(waistCircumference.getEncounter().getPatient().getGender());

			// write out the BMI classification based on age
			final int ageInMonths = calculateAgeInMonths(hipCircumference, waistCircumference);
			out.write(AgeBasedClassifier.getClassification(male ? Type.MALE_WH_RATIO : Type.FEMALE_WH_RATIO, ageInMonths, ratio));

			// write out the hip-to-waist ratio classification based on age (#TODO)
			// out.write("[#" + RATIO_FMT.format(ratio) + "/" + calculateAgeInMonths(hipCircumference, waistCircumference) + "]");
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Calculate the hip-to-waist value.
	 * 
	 * @return The hip-to-waist value.
	 */
	private double calculateRatio() {
		// obtain hip and waist circumference
		final double hipCirc = (hipCircumference.getValueNumeric() != null) ? hipCircumference.getValueNumeric() : Double.parseDouble(hipCircumference
				.getValueText());
		final double waistCirc = (waistCircumference.getValueNumeric() != null) ? waistCircumference.getValueNumeric() : Double.parseDouble(waistCircumference
				.getValueText());

		// calculate hip to waist ratio
		final double ratio = waistCirc / hipCirc;

		// send back the ratio value
		return ratio;
	}

	/**
	 * @return the waistCircumference
	 */
	public Obs getWaistCircumference() {
		return waistCircumference;
	}

	/**
	 * @param waistCircumference
	 *            the waistCircumference to set
	 */
	public void setWaistCircumference(Obs waistCircumference) {
		this.waistCircumference = waistCircumference;
	}

	/**
	 * @return the hipCircumference
	 */
	public Obs getHipCircumference() {
		return hipCircumference;
	}

	/**
	 * @param hipCircumference
	 *            the hipCircumference to set
	 */
	public void setHipCircumference(Obs hipCircumference) {
		this.hipCircumference = hipCircumference;
	}
}
