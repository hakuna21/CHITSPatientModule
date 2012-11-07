package org.openmrs.module.chits;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import liquibase.csv.CSVReader;

import org.junit.Test;
import org.openmrs.module.chits.AgeBasedClassifier.Type;

public class ClassificationTest {
	@Test
	public void AdultClassifications() {
		getClassification(Type.BMI, 19 * 12 + 1, 15.9);
		getClassification(Type.BMI, 19 * 12 + 1, 16.00);
		getClassification(Type.BMI, 19 * 12 + 1, 18.49);
		getClassification(Type.BMI, 19 * 12 + 1, 18.50);
		getClassification(Type.BMI, 19 * 12 + 1, 24.99);
		getClassification(Type.BMI, 19 * 12 + 1, 25.00);
		getClassification(Type.BMI, 19 * 12 + 1, 29.99);
		getClassification(Type.BMI, 19 * 12 + 1, 30.00);
		getClassification(Type.BMI, 19 * 12 + 1, 34.99);
		getClassification(Type.BMI, 19 * 12 + 1, 35.00);
		getClassification(Type.BMI, 19 * 12 + 1, 39.99);
		getClassification(Type.BMI, 19 * 12 + 1, 40.00);

		getClassification(Type.MALE_WH_RATIO, 20 * 12, 0.9599);
		getClassification(Type.MALE_WH_RATIO, 20 * 12, 0.9600);
		getClassification(Type.MALE_WH_RATIO, 20 * 12, 0.9999);
		getClassification(Type.MALE_WH_RATIO, 20 * 12, 1.0000);

		getClassification(Type.FEMALE_WH_RATIO, 20 * 12, 0.8099);
		getClassification(Type.FEMALE_WH_RATIO, 20 * 12, 0.8100);
		getClassification(Type.FEMALE_WH_RATIO, 20 * 12, 0.8499);
		getClassification(Type.FEMALE_WH_RATIO, 20 * 12, 0.8500);
	}

	@Test
	public void testChildCareClassifications() {
		getClassification(Type.FEMALE_WFA_0TO5, 36, 0.0);
		getClassification(Type.FEMALE_WFA_0TO5, 36, 9.6);
		getClassification(Type.FEMALE_WFA_0TO5, 36, 9.69);
		getClassification(Type.FEMALE_WFA_0TO5, 36, 9.7);
		getClassification(Type.FEMALE_WFA_0TO5, 36, 10.7);
		getClassification(Type.FEMALE_WFA_0TO5, 36, 10.79);
		getClassification(Type.FEMALE_WFA_0TO5, 36, 10.8);
		getClassification(Type.FEMALE_WFA_0TO5, 36, 18.1);
		getClassification(Type.FEMALE_WFA_0TO5, 36, 18.19);
		getClassification(Type.FEMALE_WFA_0TO5, 36, 18.2);

		getClassification(Type.MALE_WFA_0TO5, 36, 0.0);
		getClassification(Type.MALE_WFA_0TO5, 36, 10.0);
		getClassification(Type.MALE_WFA_0TO5, 36, 10.09);
		getClassification(Type.MALE_WFA_0TO5, 36, 10.1);
		getClassification(Type.MALE_WFA_0TO5, 36, 11.2);
		getClassification(Type.MALE_WFA_0TO5, 36, 11.29);
		getClassification(Type.MALE_WFA_0TO5, 36, 11.3);
		getClassification(Type.MALE_WFA_0TO5, 36, 18.3);
		getClassification(Type.MALE_WFA_0TO5, 36, 18.39);
		getClassification(Type.MALE_WFA_0TO5, 36, 18.4);

		getClassification(Type.MALE_WFL_0TO2, 500, 2.69);
		getClassification(Type.MALE_WFL_0TO2, 500, 2.70);
	}

	@Test
	public void testRaw() throws IOException {
		testRawImpl(Type.BMI, "BMI_for_adults.csv");
		testRawImpl(Type.MALE_BMIFA_5TO19, "BMI_for_age_boys_5_to_19.csv");
		testRawImpl(Type.FEMALE_BMIFA_5TO19, "BMI_for_age_girls_5_to_19.csv");
		testRawImpl(Type.MALE_HFA_0TO5, "height_for_age_boys_0_to_5.csv");
		testRawImpl(Type.FEMALE_HFA_0TO5, "height_for_age_girls_0_to_5.csv");
		testRawImpl(Type.MALE_WH_RATIO, "W2H_ratio_for_male_adults.csv");
		testRawImpl(Type.FEMALE_WH_RATIO, "W2H_ratio_for_female_adults.csv");
		testRawImpl(Type.MALE_WFA_0TO5, "weight_for_age_boys_0_to_5.csv");
		testRawImpl(Type.FEMALE_WFA_0TO5, "weight_for_age_girls_0_to_5.csv");
		testRawImpl(Type.MALE_WFL_0TO2, "weight_for_length_boys_0_to_2.csv");
		testRawImpl(Type.FEMALE_WFL_0TO2, "weight_for_length_girls_0_to_2.csv");
		testRawImpl(Type.MALE_WFL_2TO5, "weight_for_length_boys_2_to_5.csv");
		testRawImpl(Type.FEMALE_WFL_2TO5, "weight_for_length_girls_2_to_5.csv");
	}

	private void testRawImpl(Type classificationType, String csv) throws IOException {
		final InputStream source = AgeBasedClassifier.class.getResourceAsStream(csv);
		try {
			final CSVReader reader = new CSVReader(new InputStreamReader(source));
			final CSVUtil csvUtil = new CSVUtil(reader.readNext());

			String ageHeader = "";
			boolean lengthBased = false;
			if (csvUtil.containsHeader("AGE (months)")) {
				// age based classification
				ageHeader = "AGE (months)";
			} else if (csvUtil.containsHeader("cm")) {
				// length based classification
				lengthBased = true;
				ageHeader = "cm";
			} else {
				throw new UnsupportedOperationException("Headers uknown for: " + csv);
			}

			Map<String, Double> previousGroupValues = new HashMap<String, Double>();

			int lastAgeInMonths = -1;
			while (csvUtil.nextRow(reader.readNext()) != null) {
				final int ageInMonths;
				if (lengthBased) {
					ageInMonths = (int) (10.0 * Double.parseDouble(csvUtil.get(ageHeader)));
				} else {
					ageInMonths = Integer.parseInt(csvUtil.get(ageHeader));
				}

				if (ageInMonths <= lastAgeInMonths) {
					throw new IllegalStateException("Subsequent (pseudo) age value (" + ageHeader + ") less than (or equal to) previous (pseudo) value ("
							+ lastAgeInMonths + ") for: " + csv);
				}

				double lastValue = -0.1;
				for (final String classificationHeader : csvUtil.getHeaders()) {
					if (classificationHeader.equalsIgnoreCase(ageHeader) || "label".equalsIgnoreCase(classificationHeader.trim())) {
						continue;
					}

					// trim out the "(from)" or "(to)" parts from the classification
					final String expectedClassification = (classificationHeader.contains("(") ? classificationHeader.substring(0,
							classificationHeader.indexOf('(') - 1) : classificationHeader).trim();
					final double value = Double.parseDouble(csvUtil.get(classificationHeader));
					final String actualClassification = AgeBasedClassifier.getClassification(classificationType, ageInMonths, value);

					System.out.println(ageInMonths + "/" + value + " expected: " + expectedClassification + ", actual: " + actualClassification);

					if (value <= lastValue) {
						throw new IllegalStateException("Subsequent classification value (" + value
								+ ") less than (or equal to) previous classification value (" + lastValue + ") when testing with value: " + value
								+ " using (pseudo) age: " + ageInMonths + " for: " + csv);
					}

					if (!expectedClassification.equals(actualClassification)) {
						System.out.println("Value for " + classificationHeader + ": " + csvUtil.get(classificationHeader));
						throw new IllegalStateException("Expected: " + expectedClassification + ", Actual: " + actualClassification
								+ " when testing with value: " + value + " using (pseudo) age: " + ageInMonths + " for: " + csv);
					}

					if (previousGroupValues.containsKey(classificationHeader)) {
						final Double previousGroupValue = previousGroupValues.get(classificationHeader);
						if (value < previousGroupValue) {
							throw new IllegalStateException("Subsequent classification group with value (" + value
									+ ") less than previous classification group value (" + previousGroupValue + ") when testing with value: " + value
									+ " using (pseudo) age: " + ageInMonths + " for: " + csv);
						}
					}

					lastValue = value;
					previousGroupValues.put(classificationHeader, value);
				}

				lastAgeInMonths = ageInMonths;
			}
		} finally {
			source.close();
		}
	}

	private void getClassification(Type type, int ageInMonths, double value) {
		System.out.println(ageInMonths + "/" + value + ": " + AgeBasedClassifier.getClassification(type, ageInMonths, value));
	}
}
