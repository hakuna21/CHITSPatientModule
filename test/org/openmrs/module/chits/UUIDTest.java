package org.openmrs.module.chits;

import java.util.regex.Matcher;

import org.junit.Test;
import org.openmrs.module.chits.web.controller.admin.UploadConceptsController;

public class UUIDTest {
	@Test
	public void testUUIDPattern() {
		tryText("OpenMRS ID: 703\r\n" + //
				" openMRS  UuID:  703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  \r\n" + //
				"AMPATH: 703\r\n" + //
				"AMPATH: 704\r\n" + //
				"SNOMED CT: 10828004\r\n" + //
				"PIH: 703\r\n" + //
				"org.openmrs.module.mdrtb: POSITIVE", true);

		tryText(" openMRS  UuID:  703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  \r\n" + //
				"AMPATH: 703\r\n" + //
				"AMPATH: 704\r\n" + //
				"SNOMED CT: 10828004\r\n" + //
				"PIH: 703\r\n" + //
				"org.openmrs.module.mdrtb: POSITIVE", true);

		tryText("openMRS  UuID:  703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  \r\n" + //
				"AMPATH: 703\r\n" + //
				"AMPATH: 704\r\n" + //
				"SNOMED CT: 10828004\r\n" + //
				"PIH: 703\r\n" + //
				"org.openmrs.module.mdrtb: POSITIVE", true);

		tryText("openMRS ID: 1065\r\n" + //
				" UUId:  1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  \r\n" + //
				"PIH: 1065\r\n" + //
				"PIH: 1894\r\n" + //
				"AMPATH: 1065\r\n" + //
				"SNOMED CT: 373066001\r\n" + //
				"PIH: 1809\r\n", true);
		tryText(" UUId:  1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  \r\n" + //
				"PIH: 1065\r\n" + //
				"PIH: 1894\r\n" + //
				"AMPATH: 1065\r\n" + //
				"SNOMED CT: 373066001\r\n" + //
				"PIH: 1809\r\n", true);

		tryText("UUId:  1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  \r\n" + //
				"PIH: 1065\r\n" + //
				"PIH: 1894\r\n" + //
				"AMPATH: 1065\r\n" + //
				"SNOMED CT: 373066001\r\n" + //
				"PIH: 1809\r\n", true);

		tryText("AMPATH: 5945 \r\n" + //
				"PIH: 3722 \r\n" + //
				"SNOMED CT: 386661006 \r\n" + //
				"PIH: 5945 \r\n" + //
				"AMPATH: 157 \r\n" + //
				"OpenMRS ID: 140238\r\n" + //
				"OpenMRS UUID: 140238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\r\n" + //
				"ICD-10-WHO: R50.9", true);

		tryText("AMPATH: 5945 \r\n" + //
				"PIH: 3722 \r\n" + //
				"SNOMED CT: 386661006 \r\n" + //
				"PIH: 5945 \r\n" + //
				"AMPATH: 157 \r\n" + //
				"OpenMRS ID: 140238\r\n" + //
				"OpenMRS UUID: 140238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\r\n", true);

		tryText("OpenMRS ID: 129372\r\n" + //
				"OpenMRS UUID: \r\n" + //
				"ICD-10-WHO: P08.2\r\n" + //
				"SNOMED CT: 16207008\r\n", false);

		tryText("OpenMRS UUID: \r\n" + //
				"ICD-10-WHO: P08.2\r\n" + //
				"SNOMED CT: 16207008\r\n", false);

		tryText("OpenMRS ID: 129372\r\n" + //
				"OpenMRS UUID: \r\n", false);

		tryText("OpenMRS ID: 129372\r\n" + //
				" UUID: \r\n" + //
				"ICD-10-WHO: P08.2\r\n" + //
				"SNOMED CT: 16207008\r\n", false);

		tryText(" UUID: \r\n" + //
				"ICD-10-WHO: P08.2\r\n" + //
				"SNOMED CT: 16207008\r\n", false);

		tryText("OpenMRS ID: 129372\r\n" + //
				" UUID: \r\n", false);

		tryText("OpenMRS ID: 129372\r\n" + //
				"UUID: \r\n" + //
				"ICD-10-WHO: P08.2\r\n" + //
				"SNOMED CT: 16207008\r\n", false);

		tryText("UUID: \r\n" + //
				"ICD-10-WHO: P08.2\r\n" + //
				"SNOMED CT: 16207008\r\n", false);

		tryText("OpenMRS ID: 129372\r\n" + //
				"UUID: \r\n", false);
	}

	private String tryText(String mappings, boolean matchExpected) {
		final Matcher uuidMatcher = UploadConceptsController.UUID_PATTERN.matcher(mappings);
		if (uuidMatcher.find()) {
			if (!matchExpected) {
				throw new IllegalArgumentException("Not expecting matched pattern: " + mappings);
			}

			final String uuid = uuidMatcher.group(1);
			System.out.println("\"" + uuid + "\"");
			return uuid;
		} else {
			if (matchExpected) {
				throw new IllegalArgumentException("Unmatched pattern: " + mappings);
			}

			return null;
		}
	}
}
