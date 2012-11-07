package org.openmrs.module.chits;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants.PhilhealthConcepts;
import org.springframework.util.StringUtils;

public class PhilhealthUtil {
	/** Logger for this class and subclasses */
	private static final Log LOG = LogFactory.getLog(PhilhealthUtil.class);

	/**
	 * 
	 * 1. All patients, regardless of age, may be registered with the PHIC, and may therefore have a PhilHealth ID.<br/>
	 * 2. The insurance coverage is active as long as the expiration date is later than the current date. In this case, the corresponding Philhealth status in
	 * the patient record is "P" (principal).<br/>
	 * 3. In the case of children (below 18 years old) and senior dependents (parents at least 60 years old) who have a Philhealth ID number but no coverage (no
	 * premiums being paid), the expiration date shall be set to the date of registration to render the coverage inactive. This will mean that the Philhealth
	 * status of the patient will be "B" (beneficiary) if the head of the family has active insurance coverage.
	 * 
	 * @param patient
	 * @param familyFolders
	 * @return
	 */
	public static String getPhilhealthStatus(Patient patient, List<FamilyFolder> familyFolders) {
		if (patient == null) {
			return "none";
		}

		final FamilyFolder folder = familyFolders.isEmpty() ? null : familyFolders.get(0);
		final PersonAttribute phNum = patient.getAttribute(PhilhealthConcepts.CHITS_PHILHEALTH);
		final PersonAttribute phExp = patient.getAttribute(PhilhealthConcepts.CHITS_PHILHEALTH_EXPIRATION);

		if (phNum != null && phExp != null && StringUtils.hasText(phNum.getValue()) && StringUtils.hasText(phExp.getValue())) {
			// has philhealth
			boolean active = false;
			try {
				if (new Date().before(Context.getDateFormat().parse(phExp.getValue()))) {
					// not yet expired
					active = true;
				}
			} catch (Exception ex) {
				LOG.warn("Error parsing Philhealth expiration date for '" + patient + "': " + phExp.getValue());
			}

			if (active) {
				return "P";
			}

			if (patient.getAge() != null && (patient.getAge() < 18 || patient.getAge() > 60)) {
				if (folder != null && folder.getHeadOfTheFamily() != null && philhealthActiveFor(folder.getHeadOfTheFamily())) {
					// Beneficiary
					return "B";
				} else {
					// Expired
					return "X";
				}
			}
		} else if (patient.getAge() != null) {
			// no philhealth...
			if (patient.getAge() < 18 || patient.getAge() > 60) {
				if (folder != null && folder.getHeadOfTheFamily() != null && philhealthActiveFor(folder.getHeadOfTheFamily())) {
					// Beneficiary
					return "B";
				}
			}
		}

		// no philhealth
		return "none";
	}

	public static boolean philhealthActiveFor(Patient patient) {
		final PersonAttribute phNum = patient.getAttribute(PhilhealthConcepts.CHITS_PHILHEALTH);
		final PersonAttribute phExp = patient.getAttribute(PhilhealthConcepts.CHITS_PHILHEALTH_EXPIRATION);

		if (phNum != null && phExp != null && StringUtils.hasText(phNum.getValue()) && StringUtils.hasText(phExp.getValue())) {
			try {
				if (new Date().before(Context.getDateFormat().parse(phExp.getValue()))) {
					// not yet expired
					return true;
				}
			} catch (Exception ex) {
				LOG.warn("Error parsing Philhealth expiration date for '" + patient + "': " + phExp.getValue());
			}
		}

		// patient's philhealth is not active
		return false;
	}
}
