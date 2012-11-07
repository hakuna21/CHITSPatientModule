package org.openmrs.module.chits;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.Test;

public class IntervalTest {
	@Test
	public void testInterval() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

		System.out.println(Util.describeAge(sdf.parse("19760126 12:44:21")));
		System.out.println(Util.describeAge(new Date()));
		System.out.println(Util.describeAge(new Date(System.currentTimeMillis() - 60 * 1000L)));
		System.out.println(Util.describeAge(new Date(System.currentTimeMillis() - 59 * 60 * 1000L)));
		System.out.println(Util.describeAge(new Date(System.currentTimeMillis() - (int) (3.21 * 60 * 60 * 1000L))));
		System.out.println(Util.describeAge(new Date(System.currentTimeMillis() - (int) (24.50 * 60 * 60 * 1000L))));
	}

	@Test
	public void testWeeks() {
		// calculate how old this person was, in months, when the observations were taken
		final Duration duration = new Interval(System.currentTimeMillis() - 180 * 24 * 60 * 60 * 1000L, System.currentTimeMillis()).toDuration();
		System.out.println("Standard days: " + duration.getStandardDays());
		System.out.println("Standard Weeks: " + duration.getStandardDays() / 7);
	}
}
