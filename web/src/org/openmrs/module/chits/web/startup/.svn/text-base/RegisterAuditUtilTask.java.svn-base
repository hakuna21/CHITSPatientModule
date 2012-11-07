package org.openmrs.module.chits.web.startup;

import javax.servlet.ServletContext;

import org.openmrs.module.chits.audit.AuditUtil;
import org.openmrs.module.chits.audit.UserSessionTracker;

/**
 * Registers the {@link AuditUtil} singleton to the {@link UserSessionTracker}.
 * 
 * @author Bren
 */
class RegisterAuditUtilTask implements Runnable {
	/** Reference for storing the user session tracker in */
	private final ServletContext servletContext;

	RegisterAuditUtilTask(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void run() {
		// store a new instance of the UserSessionTracker in the servlet context for global accessibility
		final UserSessionTracker userSessionTracker = new UserSessionTracker();
		servletContext.setAttribute(UserSessionTracker.class.getName(), userSessionTracker);

		// register the audit utility as a user session listener
		userSessionTracker.addUserSessionListener(AuditUtil.getInstance());
	}
}