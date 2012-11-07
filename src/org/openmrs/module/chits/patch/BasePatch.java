package org.openmrs.module.chits.patch;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.chits.OpenSessionIfNeeded;

/**
 * Base class containing helper methods for checking if a patch requires application.
 * 
 * @author Bren
 */
public abstract class BasePatch extends OpenSessionIfNeeded {
	/** Logger instance */
	protected final Log log = LogFactory.getLog(getClass());

	/** Global property key of the installed patches */
	public static final String GP_INSTALLED_PATCHES = "chits.installed.patches";

	/** The patch ID delimiter used to separate patch IDs in the global property value */
	public static final String DELIM = "|";

	/** A patch ID for identifying the presense of an installed patch -- must not contain newlines or the pipe (|) symbol. */
	protected abstract String getPatchId();

	/** Applies the patch. This method will be called only if the patch has not been installed yet based on the 'chits.installed.patches' global property */
	protected abstract void applyPatchImpl();

	/** The admin service */
	protected final AdministrationService adminService;

	public BasePatch(AdministrationService adminService, String... proxyPrivileges) {
		super(proxyPrivileges);
		this.adminService = adminService;
	}

	/**
	 * Checks if the patch has already been installed
	 */
	protected boolean isPatchInstalled() {
		final String installedPatches = adminService.getGlobalPropertyValue(GP_INSTALLED_PATCHES, "");
		return installedPatches.contains(DELIM + getPatchId() + DELIM);
	}

	/**
	 * Invokes the applyPatchImpl() and then appends the patch ID to the "chits.installed.patches" global property value if the patch has not yet been
	 * installed.
	 */
	@Override
	protected final void execute() {
		if (!isPatchInstalled()) {
			// log the event
			log.info("Installing patch with ID: \"" + getPatchId() + "\"");

			// invoke patch implementation
			applyPatchImpl();

			// update the global property value
			GlobalProperty gp = adminService.getGlobalPropertyObject(GP_INSTALLED_PATCHES);
			if (gp == null) {
				gp = new GlobalProperty();
				gp.setProperty(GP_INSTALLED_PATCHES);
				gp.setDescription("Tracks the CHITS patches installed on the system.");
				gp.setUuid(UUID.randomUUID().toString());
				gp.setPropertyValue("");
			}

			// prepare the new global property patch ids string
			String patchIDs = gp.getPropertyValue();
			if (!patchIDs.startsWith(DELIM)) {
				// the patch ID must be surrounded with the delimiter
				patchIDs = patchIDs + DELIM;
			}

			// append the patch ID
			patchIDs = patchIDs + getPatchId() + DELIM;
			gp.setPropertyValue(patchIDs);

			// update the property value
			adminService.saveGlobalProperty(gp);
		} else {
			// log the event
			log.info("Patch with ID: \"" + getPatchId() + "\" already installed.");
		}
	}
}
