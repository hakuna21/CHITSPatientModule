package org.openmrs.module.chits;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.Interval;
import org.joda.time.Period;

/**
 * Utility methods for processing dates.
 * 
 * @author Bren
 */
public class DateUtil {
	/**
	 * Returns the same date value as passed but with the time component set to 00:00:00.000
	 * 
	 * @param date
	 *            The date to strip the time component of
	 * @return The date with the time component stripped of.
	 */
	public static Date stripTime(Date date) {
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		// send back the same date with the time component stripped of
		return c.getTime();
	}

	/**
	 * Returns midnight of the given date (which technically, is the following day).
	 * 
	 * @param date
	 *            The date to get the midnight timestamp of
	 * @return The date at midnight
	 */
	public static Date midnight(Date date) {
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		// send back the same date with the time component stripped of
		return c.getTime();
	}
	
	/**
	 * Returns the number of days between the two given timestamps. A negative number will be returned if the 'from' timestamp' is after the 'to' timestamp.
	 * 
	 * @param from
	 *            From this date
	 * @param to
	 *            Up to this date
	 * @return The number of days between the two given timestamps.
	 */
	public static int daysBetween(Date from, Date to) {
		// get the number of days between the two timestamps
		return (int) ((to.getTime() - from.getTime()) / TimeUnit.HOURS.toMillis(24));
	}

	/**
	 * Returns the number of months between the two given timestamps. A negative number will be returned if the 'from' timestamp' is after the 'to' timestamp.
	 * 
	 * @param from
	 *            From this date
	 * @param to
	 *            Up to this date
	 * @return The number of months between the two given timestamps.
	 */
	public static int monthsBetween(Date from, Date to) {
		final boolean negative;
		final Period period;
		if (from.before(to)) {
			period = new Interval(from.getTime(), to.getTime()).toPeriod();
			negative = false;
		} else {
			period = new Interval(to.getTime(), from.getTime()).toPeriod();
			negative = true;
		}

		// calculate the number of months
		final int months = period.getYears() * 12 + period.getMonths();
		return negative ? -months : months;
	}

	/**
	 * Returns the number of years between the two given timestamps. A negative number will be returned if the 'from' timestamp' is after the 'to' timestamp.
	 * 
	 * @param from
	 *            From this date
	 * @param to
	 *            Up to this date
	 * @return The number of years between the two given timestamps.
	 */
	public static int yearsBetween(Date from, Date to) {
		final boolean negative;
		final Period period;
		if (from.before(to)) {
			period = new Interval(from.getTime(), to.getTime()).toPeriod();
			negative = false;
		} else {
			period = new Interval(to.getTime(), from.getTime()).toPeriod();
			negative = true;
		}

		// calculate the number of years
		return negative ? -period.getYears() : period.getYears();
	}
}
