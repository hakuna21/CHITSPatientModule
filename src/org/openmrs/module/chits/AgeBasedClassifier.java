package org.openmrs.module.chits;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import liquibase.csv.CSVReader;

/**
 * Utility class for classifying various data points (such as BMI, Waist/Hip Ratio, etc.) based on age (in months) of the patient.
 * 
 * @author Bren
 */
public class AgeBasedClassifier {
	/** BMI classifications by age (in months) */
	private static final SortedMap<Integer, SortedMap<Double, String>> bmiByAgeAndValue = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Child Care: Weight for age classification for males 0 to 5 y/o */
	private static final SortedMap<Integer, SortedMap<Double, String>> maleWeightForAge0To5 = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Child Care: Weight for age classification for females 0 to 5 y/o */
	private static final SortedMap<Integer, SortedMap<Double, String>> femaleWeightForAge0To5 = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Child Care: BMI for age classification for males 5 to 19 y/o */
	private static final SortedMap<Integer, SortedMap<Double, String>> maleBmiForAge5To19 = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Child Care: BMI for age classification for females 5 to 19 y/o */
	private static final SortedMap<Integer, SortedMap<Double, String>> femaleBmiForAge5To19 = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Child Care: Weight for age classification for males 0 to 5 y/o */
	private static final SortedMap<Integer, SortedMap<Double, String>> maleHeightForAge0To5 = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Child Care: Height for age classification for females 0 to 5 y/o */
	private static final SortedMap<Integer, SortedMap<Double, String>> femaleHeightForAge0To5 = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Child Care: Height for age classification for males 0 to 2 y/o */
	private static final SortedMap<Integer, SortedMap<Double, String>> maleWeightForLength0To2 = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Child Care: Weight for length classification for females 0 to 2 y/o */
	private static final SortedMap<Integer, SortedMap<Double, String>> femaleWeightForLength0To2 = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Child Care: Weight for length classification for males 2 to 5 y/o */
	private static final SortedMap<Integer, SortedMap<Double, String>> maleWeightForLength2To5 = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Child Care: Weight for length classification for males 2 to 5 y/o */
	private static final SortedMap<Integer, SortedMap<Double, String>> femaleWeightForLength2To5 = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Male waist to hip ratio classifications by age (in months) */
	private static final SortedMap<Integer, SortedMap<Double, String>> maleWHRatio = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Female waist to hip ratio classifications by age (in months) */
	private static final SortedMap<Integer, SortedMap<Double, String>> femaleWHRatio = new TreeMap<Integer, SortedMap<Double, String>>();

	/** Value returned if there is no available data for the parameters */
	public static final String OUT_OF_RANGE = "[Out Of Range]";

	/**
	 * The various types of datapoints that can be classified by this utility class.
	 * 
	 * @author Bren
	 */
	public enum Type {
		/** Child care classifications */
		MALE_WFA_0TO5(maleWeightForAge0To5), //
		FEMALE_WFA_0TO5(femaleWeightForAge0To5), //
		MALE_BMIFA_5TO19(maleBmiForAge5To19), //
		FEMALE_BMIFA_5TO19(femaleBmiForAge5To19), //
		MALE_HFA_0TO5(maleHeightForAge0To5), //
		FEMALE_HFA_0TO5(femaleHeightForAge0To5), //
		MALE_WFL_0TO2(maleWeightForLength0To2), //
		FEMALE_WFL_0TO2(femaleWeightForLength0To2), //
		MALE_WFL_2TO5(maleWeightForLength2To5), //
		FEMALE_WFL_2TO5(femaleWeightForLength2To5), //

		/** Adult classifications */
		BMI(bmiByAgeAndValue), //
		MALE_WH_RATIO(maleWHRatio), //
		FEMALE_WH_RATIO(femaleWHRatio);

		private final SortedMap<Integer, SortedMap<Double, String>> groupings;

		private Type(SortedMap<Integer, SortedMap<Double, String>> groupings) {
			this.groupings = groupings;
		}

		public SortedMap<Integer, SortedMap<Double, String>> getGroupings() {
			return groupings;
		}
	}

	/**
	 * Initializes default classifications. In the future, these values might be loaded from a configuration file or database.
	 */
	static {
		try {
			// load adult BMI classifications
			final String[] bmiHeaders = new String[] { "Underweight (from)", "Normal (from)", "Overweight (from)", "Obese Class I (from)",
					"Obese Class II (from)", "Obese Class III" };
			loadClassifications("BMI_for_adults.csv", null, bmiByAgeAndValue, bmiHeaders);

			// load female waist-to-hip ratio classifications
			final String[] w2hHeaders = new String[] { "Low/Pear (from)", "Moderate/Avocado (from)", "High/Apple" };
			loadClassifications("W2H_ratio_for_female_adults.csv", null, femaleWHRatio, w2hHeaders);

			// load male waist-to-hip ratio classifications
			loadClassifications("W2H_ratio_for_male_adults.csv", null, maleWHRatio, w2hHeaders);

			// load male and female BMI for ages 5 to 19
			final String[] bmiForAges5To19Headers = new String[] { "Thinness (from)", "Normal (from)", "Overweight (from)", "Obese" };
			loadClassifications("BMI_for_age_boys_5_to_19.csv", "Severe Thinness", maleBmiForAge5To19, bmiForAges5To19Headers);
			loadClassifications("BMI_for_age_girls_5_to_19.csv", "Severe Thinness", femaleBmiForAge5To19, bmiForAges5To19Headers);

			// load male and female height for age for ages 0 to 5
			final String[] heightForAgeHeaders = new String[] { "Stunted (from)", "Normal (from)", "Tall" };
			loadClassifications("height_for_age_boys_0_to_5.csv", "Severely Stunted", maleHeightForAge0To5, heightForAgeHeaders);
			loadClassifications("height_for_age_girls_0_to_5.csv", "Severely Stunted", femaleHeightForAge0To5, heightForAgeHeaders);

			// load male and female weight for age for ages 0 to 5
			final String[] weightForAgeHeaders = new String[] { "Underweight (from)", "Normal (from)", "Overweight" };
			loadClassifications("weight_for_age_boys_0_to_5.csv", "Severely Underweight", maleWeightForAge0To5, weightForAgeHeaders);
			loadClassifications("weight_for_age_girls_0_to_5.csv", "Severely Underweight", femaleWeightForAge0To5, weightForAgeHeaders);

			// load male and female weight for length for ages 0 to 2
			loadWeightForLength("weight_for_length_boys_0_to_2.csv", maleWeightForLength0To2);
			loadWeightForLength("weight_for_length_girls_0_to_2.csv", femaleWeightForLength0To2);

			// load male and female weight for length for ages 2 to 5
			loadWeightForLength("weight_for_length_boys_2_to_5.csv", maleWeightForLength2To5);
			loadWeightForLength("weight_for_length_girls_2_to_5.csv", femaleWeightForLength2To5);
		} catch (IOException ioe) {
			throw new IllegalStateException("Error initializing weight for age data", ioe);
		}
	}

	private static void storeClassification(SortedMap<Integer, SortedMap<Double, String>> data, int ageInMonths, double lowerRange, String classification) {
		SortedMap<Double, String> rangeMap = data.get(ageInMonths);
		if (rangeMap == null) {
			rangeMap = new TreeMap<Double, String>();
			data.put(ageInMonths, rangeMap);
		}

		rangeMap.put(lowerRange, classification);
	}

	/**
	 * Loads a "BMI For Age" CSV file with these headers:
	 * 
	 * <PRE>
	 * label,AGE (months),Severe Thinness,Thinness (from),Thinness (to),Normal (from),Normal (to),Overweight (from),Overweight (to),Obesity
	 * </PRE>
	 * 
	 * @param resourcePath
	 * @param bmiForAge
	 * @throws IOException
	 */
	private static void loadClassifications(String resourcePath, String aboveZeroClassification, SortedMap<Integer, SortedMap<Double, String>> classifications,
			String... classLowerLimitHeaders) throws IOException {
		// process the drugs file
		final InputStream source = AgeBasedClassifier.class.getResourceAsStream(resourcePath);
		try {
			final CSVReader reader = new CSVReader(new InputStreamReader(source));
			final CSVUtil csvUtil = new CSVUtil(reader.readNext());
			while (csvUtil.nextRow(reader.readNext()) != null) {
				final int ageInMonths = Integer.parseInt(csvUtil.get("AGE (months)"));

				if (aboveZeroClassification != null) {
					// store the lower limit classificaiton at 0.0
					storeClassification(classifications, ageInMonths, 0.0, aboveZeroClassification);
				}

				for (String classLowerLimitHeader : classLowerLimitHeaders) {
					// parse out the classification lwoer limit value
					final Double classLowerLimit = Double.parseDouble(csvUtil.get(classLowerLimitHeader));

					// strip characters following the opening parenthesis (if any) from the classification name
					final String classification = (classLowerLimitHeader.contains("(") ? classLowerLimitHeader.substring(0,
							classLowerLimitHeader.indexOf('(') - 1) : classLowerLimitHeader).trim();

					// store the classificaiton
					storeClassification(classifications, ageInMonths, classLowerLimit, classification);
				}
			}
		} finally {
			source.close();
		}
	}

	/**
	 * Loads weight for length classifications. NOTE: Instead of representing 'age', the first integer key represents 'millimeters' (centimeters * 10)!
	 * 
	 * <PRE>
	 * cm,Severely Wasted,Wasted (from),Wasted (to),Normal (from),Normal (to),Overweight (from),Overweight (to),Obese
	 * </PRE>
	 * 
	 * @param resourcePath
	 * @param weightForLength
	 * @throws IOException
	 */
	private static void loadWeightForLength(String resourcePath, SortedMap<Integer, SortedMap<Double, String>> weightForLength) throws IOException {
		// process the drugs file
		final InputStream source = AgeBasedClassifier.class.getResourceAsStream(resourcePath);
		try {
			final CSVReader reader = new CSVReader(new InputStreamReader(source));
			final CSVUtil csvUtil = new CSVUtil(reader.readNext());
			while (csvUtil.nextRow(reader.readNext()) != null) {
				// treat the 'length' converted to integer millimeters as the 'age'
				final int pseudoAgeInMonths = (int) (10.0 * Double.parseDouble(csvUtil.get("cm")));
				final Double severelyWastedLowerLimit = 0.0;
				final Double wastedLowerLimit = Double.parseDouble(csvUtil.get("Wasted (from)"));
				final Double normalLowerLimit = Double.parseDouble(csvUtil.get("Normal (from)"));
				final Double overweightLowerLimit = Double.parseDouble(csvUtil.get("Overweight (from)"));
				final Double obeseLimit = Double.parseDouble(csvUtil.get("Obese"));

				// store weight for age classifications
				storeClassification(weightForLength, pseudoAgeInMonths, severelyWastedLowerLimit, "Severely Wasted");
				storeClassification(weightForLength, pseudoAgeInMonths, wastedLowerLimit, "Wasted");
				storeClassification(weightForLength, pseudoAgeInMonths, normalLowerLimit, "Normal");
				storeClassification(weightForLength, pseudoAgeInMonths, overweightLowerLimit, "Overweight");
				storeClassification(weightForLength, pseudoAgeInMonths, obeseLimit, "Obese");
			}
		} finally {
			source.close();
		}
	}

	/**
	 * Load the textual classification for the given data type and datapoint value for a patient with the given age (in months).
	 * 
	 * @param type
	 *            The data type
	 * @param ageInMonths
	 *            The patient's age (in months)
	 * @param value
	 *            The datapoint value
	 * @return The classification given the parameters, [Out Of Range] if can't be classified.
	 */
	public static String getClassification(Type type, int ageInMonths, double value) {
		// find the closes classification
		return getClassification(type, ageInMonths, value, false);
	}

	/**
	 * Lookup the classification based on the type, age in months, and value to lookup specifying whether to lookup by the exact age or by the highest age not
	 * greater than the given age. not higher than the given age.
	 * 
	 * @param type
	 *            The classification type
	 * @param ageInMonths
	 *            Age of patient in match
	 * @param value
	 *            The value to lookup
	 * @param lookupByExactAge
	 *            If looking up by exact age or by the highest age less than the given age
	 * @return The classification if found.
	 */
	public static String getClassification(Type type, int ageInMonths, double value, boolean lookupByExactAge) {
		try {
			// determine the patient's age class
			final Integer ageClass;
			if (lookupByExactAge) {
				// find exact age class
				ageClass = Integer.valueOf(ageInMonths);
			} else {
				// find nearest age class not greater than given age
				ageClass = type.getGroupings().headMap(ageInMonths + 1).lastKey();
			}

			// get the available range of values
			final SortedMap<Double, String> rangeMap = type.getGroupings().get(ageClass);
			if (rangeMap == null) {
				throw new NoSuchElementException();
			}

			// extract the closest value
			final Double valueClass = rangeMap.headMap(value + 0.000001).lastKey();

			// and use that to get the classification
			return rangeMap.get(valueClass);
		} catch (NoSuchElementException nsee) {
			return OUT_OF_RANGE;
		}
	}
}
