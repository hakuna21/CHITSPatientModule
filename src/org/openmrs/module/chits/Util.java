package org.openmrs.module.chits;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;

public class Util {
	/** Logger instance */
	private static final Log log = LogFactory.getLog(Util.class);

	/**
	 * Fetch the max results value from the global properties table
	 * 
	 * @return Integer value for the person search max results global property
	 */
	public static Integer getMaximumSearchResults() {
		try {
			return Integer.valueOf(Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS,
					String.valueOf(OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE)));
		} catch (Exception e) {
			log.warn("Unable to convert the global property " + OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS
					+ "to a valid integer. Returning the default " + OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE);
		}

		return OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE;
	}

	public static String formatFolderCode(AdministrationService adminService, Integer id) {
		final DecimalFormat codeFMT = new DecimalFormat(adminService.getGlobalProperty(Constants.GP_FOLDER_FORMAT));
		return codeFMT.format(id);
	}

	public static String formatPatientId(AdministrationService adminService, Integer id) {
		final DecimalFormat codeFMT = new DecimalFormat(adminService.getGlobalProperty(Constants.GP_PATIENT_ID_FORMAT));
		return codeFMT.format(id);
	}

	/**
	 * Describe elapsed amount of time since the given date.
	 * 
	 * @param since
	 * @return A description of the amount of time elapsed since this date.
	 */
	public static String describeAge(Date since) {
		return describeAge(since, new Date());
	}

	/**
	 * Describe elapsed amount of time since the given date upto the given date.
	 * 
	 * @param since
	 * @param upto
	 *            optional
	 * @return A description of the amount of time elapsed between the two dates (or the current time if 'upto' is not specified)
	 */
	public static String describeAge(Date since, Date upto) {
		// compare against current time or the 'upto' parameter
		final Date now = upto != null ? upto : new Date();
		final StringBuilder text = new StringBuilder();

		// calculate period between the two times
		Period period;
		try {
			period = new Interval(since.getTime(), now.getTime()).toPeriod();
		} catch (IllegalArgumentException iae) {
			period = new Interval(now.getTime(), since.getTime()).toPeriod();
			text.append("-");
		}

		final int years = period.getYears();
		final int months = period.getMonths();
		final int weeks = period.getWeeks();
		final int days = period.getDays();
		final int hours = period.getHours();
		final int minutes = period.getMinutes();

		int maxFigures = 2;
		int figures = 0;
		if (years > 0 && figures < maxFigures) {
			appendPluralized(years, "year", text);

			if (++figures < maxFigures) {
				text.append(", ");
			}
		}

		if (months > 0 && figures < maxFigures) {
			appendPluralized(months, "month", text);
			if (++figures < maxFigures) {
				text.append(", ");
			}
		}

		if (weeks > 0 && figures < maxFigures) {
			appendPluralized(weeks, "week", text);
			if (++figures < maxFigures) {
				text.append(", ");
			}
		}

		if (days > 0 && figures < maxFigures) {
			appendPluralized(days, "day", text);
			if (++figures < maxFigures) {
				text.append(", ");
			}
		}

		if (hours > 0 && figures < maxFigures) {
			appendPluralized(hours, "hour", text);
			if (++figures < maxFigures) {
				text.append(", ");
			}
		}

		// this is the last figure, so also include if there are no other figures even if the value is zero
		if ((minutes > 0 && figures < maxFigures) || (figures == 0)) {
			appendPluralized(minutes, "minute", text);
			if (++figures < maxFigures) {
				text.append(", ");
			}
		}

		// strip trailing ', ' if any
		final String desc = text.toString();
		if (desc.endsWith(", ")) {
			// return the textual description without the trailing ', '
			return desc.substring(0, desc.length() - 2);
		} else {
			// return the textual description
			return desc;
		}
	}

	public static void appendPluralized(int value, String singular, StringBuilder text) {
		text.append(value);
		text.append(" ");
		text.append(singular);
		if (value != 1) {
			text.append("s");
		}
	}

	/**
	 * Converts centimeters to inches.
	 * 
	 * @param cm
	 * @return The value converted to inches
	 */
	public static double cmToInches(double cm) {
		return cm / 2.54D;
	}

	/**
	 * Converts degrees centigrade to fahrenheit.
	 * 
	 * @param centigrade
	 * @return The value in fahrenheit
	 */
	public static double centigradeToFahrenheit(double centigrade) {
		return 32 + 9.0 / 5.0 * centigrade;
	}

	/**
	 * Converts kilograms to pounds.
	 * 
	 * @param kilograms
	 * @return The value converted to pounds
	 */
	public static double kgToLbs(double kg) {
		return kg * 2.20462262D;
	}
}
