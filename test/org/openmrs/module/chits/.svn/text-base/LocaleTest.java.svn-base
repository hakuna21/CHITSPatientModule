package org.openmrs.module.chits;

import java.lang.reflect.Field;

import org.junit.Test;
import org.openmrs.util.LocaleUtility;

public class LocaleTest {
	@Test
	public void currentLocale() {
		System.out.println(LocaleUtility.getDefaultLocale());
	}

	@Test
	public void forceLocale() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		// default constructor: force the default locale to be en_US (openmrs hard coded this to en_GB)
		final Field localeField = LocaleUtility.class.getDeclaredField("defaultLocaleCache");
		localeField.setAccessible(true);
		localeField.set(null, LocaleUtility.fromSpecification("en_US"));

		System.out.println(LocaleUtility.getDefaultLocale());
	}

	@Test
	public void currentLocaleAgain() {
		System.out.println(LocaleUtility.getDefaultLocale());
	}
}
