package org.openmrs.module.chits;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class MappingsTest {
	@Test
	public void getMapping() {
		String mappings = "x\r\n UUID: 1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" //
				+ "\r\n" + "PIH: 1065" //
				+ "\r\n" + "PIH: 1894" //
				+ "\r\n" + "AMPATH: 1065" //
				+ "\r\n" + "SNOMED CT: 373066001" //
				+ "\r\n" + "PIH: 1809" //
				+ "";
		String uuid = null;
		final Pattern UUID_PATTERN = Pattern.compile("^(?:.*[\r\n]+)?\\s*UUID:\\s*([^\\r\\n\\s]+)", Pattern.DOTALL);
		Matcher m = UUID_PATTERN.matcher(mappings);
		if (m.find()) {
			uuid = m.group(1);
		}
		System.out.println(uuid);

		Assert.assertEquals("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", uuid);
	}

	@Test
	public void getSets() {
		final String[] sets = "adnexa, bimanual examination, \n genitourinary findings, \r\n ,".split("\\s*[\\r\\n,]+\\s*");
		for (String set : sets) {
			System.out.println("Set: >" + set + "<");
		}
	}

	@Test
	public void getNumericPart() {
		final Pattern numericValue = Pattern.compile("([\\d\\.]+)\\s*$");
		Matcher m = numericValue.matcher("Absolute High 23.0 ");

		String value = null;
		if (m.find()) {
			value = m.group(1);
		}

		System.out.println("Value: >" + value + "<");
	}
}
