package org.openmrs.module.chits.web.startup;

import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletContext;

import org.openmrs.api.AdministrationService;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.OpenSessionIfNeeded;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.WebConstants;

/**
 * Clears the user session info table of old data.
 * 
 * @author Bren
 */
class PurgeOldUserSessionInfo extends OpenSessionIfNeeded {
	/** Needed to obtain session IDs of currently logged-in users */
	private final ServletContext servletContext;

	/** Chits service */
	private final CHITSService chitsService;

	/** Admin service */
	private final AdministrationService adminService;

	PurgeOldUserSessionInfo(ServletContext servletContext, CHITSService chitsService, AdministrationService adminService) {
		super(PrivilegeConstants.VIEW_PERSONS, PrivilegeConstants.EDIT_USERS, PrivilegeConstants.VIEW_USERS);
		this.servletContext = servletContext;
		this.chitsService = chitsService;
		this.adminService = adminService;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void execute() {
		// determine number of days to retain user session data
		int maxAgeInDays = 7;
		try {
			// load user session info retention (days) value
			maxAgeInDays = Integer.parseInt(adminService.getGlobalProperty(Constants.GP_USER_SESSION_INFO_RETENTION_DAYS, "y"));
		} catch (Exception ex) {
			log.warn("Error reading property " + Constants.GP_USER_SESSION_INFO_RETENTION_DAYS + "; using default value of " + maxAgeInDays + " days.");
		}

		// get session IDs of currently logged-in users
		final Map<String, String> currentUsers = (Map<String, String>) servletContext.getAttribute(WebConstants.CURRENT_USERS);

		// cleanup user session info table
		log.info("Purging user session data older than " + maxAgeInDays + " days...");
		chitsService.cleanupUserSessionInfoTable(maxAgeInDays, currentUsers != null ? currentUsers.keySet() : new HashSet<String>());
	}
}