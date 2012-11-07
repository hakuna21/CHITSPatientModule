package org.openmrs.module.chits.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.openmrs.Obs;
import org.openmrs.module.chits.AgeBasedClassifier;
import org.openmrs.module.chits.AgeBasedClassifier.Type;

/**
 * Child Care Tag: utility taglib for writing the "Weight For Length" classification given a patient's weight (in kilograms) and length (in centimeters).
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class WeightForLengthTag extends BaseAgeSensitiveObservationTag {
	/**
	 * The child's weight
	 */
	protected Obs weight;

	/**
	 * The child's length
	 */
	protected Obs length;

	/**
	 * Render the length value.
	 */
	@Override
	protected void writeNumericValue(JspWriter out) throws IOException {
		if (length != null && weight != null) {
			// write out length value
			out.write(Double.toString(weight.getValueNumeric()));
			out.write("/");
			out.write(Double.toString(length.getValueNumeric()));
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Render weight for length classification.
	 */
	@Override
	protected void writeClassificationValue(JspWriter out) throws IOException {
		if (weight != null && length != null && birthdate != null) {
			// age determines which classification to use
			final int ageInMonths = calculateAgeInMonths(weight, length);

			// write out the classification
			final String classification = getClassification(ageInMonths, weight, length);
			out.write(classification != null ? classification : noEnteredDataText);
		} else {
			out.write(noEnteredDataText);
		}
	}

	/**
	 * Return the 'Weight For Length' classification
	 * 
	 * @param ageInMonths
	 *            Calculated age (in months) at the time the weight / length were taken
	 * @param weight
	 *            The weight observation
	 * @param length
	 *            The length observation
	 * @return The classification
	 */
	public static String getClassification(int ageInMonths, Obs weight, Obs length) {
		if (length != null && weight != null) {
			// is this for a male or female patient?
			final boolean male = "M".equalsIgnoreCase(length.getEncounter().getPatient().getGender());

			// special case when using weight for length classifications: use the length (in millimeters) as the "age" classification
			int lengthInMillimeters = (int) (length.getValueNumeric() * 10.0D);
			lengthInMillimeters = (lengthInMillimeters / 10) * 10 + (lengthInMillimeters % 10 >= 5 ? 5 : 0);

			if (ageInMonths < 2 * 12) {
				return AgeBasedClassifier.getClassification(male ? Type.MALE_WFL_0TO2 : Type.FEMALE_WFL_0TO2, lengthInMillimeters, weight.getValueNumeric(),
						true);
			} else if (ageInMonths < 6 * 12) {
				return AgeBasedClassifier.getClassification(male ? Type.MALE_WFL_2TO5 : Type.FEMALE_WFL_2TO5, lengthInMillimeters, weight.getValueNumeric(),
						true);
			} else {
				// out of range
				return AgeBasedClassifier.OUT_OF_RANGE;
			}
		} else {
			return null;
		}
	}

	/**
	 * Returns the length observation
	 * 
	 * @return The length observation
	 */
	public Obs getLength() {
		return length;
	}

	/**
	 * Sets the length and birhtdate from the given observation
	 * 
	 * @param length
	 *            The length observation
	 */
	public void setLength(Obs length) {
		this.length = length;
		if (length != null && length.getPerson() != null) {
			super.setBirthdate(length.getPerson().getBirthdate());
		}
	}

	public Obs getWeight() {
		return weight;
	}

	public void setWeight(Obs weight) {
		this.weight = weight;
	}
}
