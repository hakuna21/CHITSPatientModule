package org.openmrs.module.chits.audit;

import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.OpenSessionIfNeeded;

/**
 * Audit util singleton for managing auditing session events.
 * 
 * @author Bren
 */
public class AuditUtil implements UserSessionListener {
	/**
	 * Static holder idiom for singletons.
	 */
	private static class Holder {
		private final static AuditUtil INSTANCE = new AuditUtil();
	}

	/**
	 * Singleton: default constructor is private.
	 */
	private AuditUtil() {
		// default constructor is private
	}

	public static AuditUtil getInstance() {
		return Holder.INSTANCE;
	}

	@Override
	public void userSessionCreated(UserSessionEvent event) {
		// audit the event
		final CHITSService chitsService = Context.getService(CHITSService.class);

		// save the user session info into the database
		chitsService.saveUserSessionInfo(event.getUserSessionInfo());
	}

	@Override
	public void userSessionDestroyed(final UserSessionEvent event) {
		new OpenSessionIfNeeded() {
			protected void execute() {
				// mark the session info has having 'timed-out' if a session needed to be opened
				// (i.e., if this task was not run from within the LogoutServlet)
				event.getUserSessionInfo().setSessionTimedOut(isSessionWasOpened());

				// audit the event
				final CHITSService chitsService = Context.getService(CHITSService.class);

				// save the user session info into the database
				chitsService.saveUserSessionInfo(event.getUserSessionInfo());
			}
		}.run();
	}
}
