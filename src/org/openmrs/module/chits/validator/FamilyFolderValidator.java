package org.openmrs.module.chits.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chits.FamilyFolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates {@link FamilyFolder} instances.
 * 
 * @author Bren
 */
public class FamilyFolderValidator implements Validator {
	/** Logger instance */
	private Log log = LogFactory.getLog(getClass());
	
	/** The bean path prefix to prepend to all bean variables */
	private final String prefix;

	public FamilyFolderValidator() {
		this("");
	}
	
	public FamilyFolderValidator(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Returns whether or not this validator supports validating a given class.
	 * 
	 * @param c
	 *            The class to check for support.
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean supports(Class c) {
		if (log.isDebugEnabled())
			log.debug(this.getClass().getName() + ".supports: " + c.getName());
		return FamilyFolder.class.isAssignableFrom(c);
	}

	/**
	 * Validates the given {@link FamilyFolder}.
	 * 
	 * @param obj
	 *            The {@link FamilyFolder} to validate.
	 * @param errors
	 *            Errors
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 * @should fail validation if name is blank
	 */
	@Override
	public void validate(Object obj, Errors errors) {
		if (log.isDebugEnabled()) {
			log.debug(this.getClass().getName() + ".validate...");
		}

		// we only validate FamilyFolder beans
		final FamilyFolder folder = (FamilyFolder) obj;

		// Make sure they choose a gender
		if (StringUtils.isBlank(folder.getName())) {
			errors.rejectValue(prefix + "name", "chits.FamilyFolder.name.required");
		}

		if (StringUtils.isBlank(folder.getCityCode())) {
			errors.rejectValue(prefix + "cityCode", "chits.FamilyFolder.city.required");
		}

		if (StringUtils.isBlank(folder.getBarangayCode())) {
			errors.rejectValue(prefix + "barangayCode", "chits.FamilyFolder.barangay.required");
		}

		if (folder.getNotes() != null && folder.getNotes().length() > 255) {
			// truncate notes to maximum length
			folder.setNotes(folder.getNotes().substring(0, 255));
		}
	}
}
