package org.openmrs.module.chits;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

/**
 * Wraps a runnable object in an OpenMRS session if needed.
 * 
 * @author Bren
 */
public abstract class OpenSessionIfNeeded implements Runnable {
	/** Logger instance */
	protected final Log log = LogFactory.getLog(getClass());

	/** Proxy privileges to add, if specified */
	private final String[] proxyPrivileges;

	/** Set to true if a session was opened by this decorator */
	private boolean sessionOpened = false;

	/**
	 * Wraps this task in an OpenMRS session, if needed.
	 * 
	 * @param task
	 */
	public OpenSessionIfNeeded() {
		this.proxyPrivileges = null;
	}

	/**
	 * Wraps this task in an OpenMRS session, if needed.
	 * 
	 * @param task
	 */
	public OpenSessionIfNeeded(String... proxyPrivileges) {
		this.proxyPrivileges = proxyPrivileges;
	}

	/**
	 * If set, then a session was opened before dispatching to the wrapped task's run method.
	 * 
	 * @return true if a session was opened manually, false if there was no need to open a new session
	 */
	public boolean isSessionWasOpened() {
		return sessionOpened;
	}

	/** Executes the task */
	protected abstract void execute();

	public final void run() {
		try {
			// it's possible that a session hasn't been opened yet, so we check by retrieving the user context
			Context.getUserContext();

			// make sure a context DAO is also available
			log.info("No need to open a session...");
		} catch (APIException apie) {
			// open up a session since one does not exist yet
			log.info("Opening session...");
			Context.openSession();

			// mark for future reference
			sessionOpened = true;
		}

		try {
			if (proxyPrivileges != null) {
				for (String proxyPrivilege : proxyPrivileges) {
					Context.addProxyPrivilege(proxyPrivilege);
				}
			}

			// invoke the task
			execute();
		} catch (Throwable ex) {
			log.error("Error in invocation.", ex);
		} finally {
			try {
				if (proxyPrivileges != null) {
					for (String proxyPrivilege : proxyPrivileges) {
						Context.removeProxyPrivilege(proxyPrivilege);
					}
				}
			} finally {
				if (sessionOpened) {
					log.info("Closing session...");
					Context.closeSession();
				}
			}
		}
	}
}
