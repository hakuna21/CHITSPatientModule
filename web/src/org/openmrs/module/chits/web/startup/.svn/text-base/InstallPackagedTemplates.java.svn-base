package org.openmrs.module.chits.web.startup;

import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.chits.OpenSessionIfNeeded;
import org.openmrs.module.chits.UploadFileForm;
import org.openmrs.module.chits.web.DummyHttpSession;
import org.openmrs.module.chits.web.controller.admin.InstallPackagedTemplatesController;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.WebConstants;
import org.springframework.validation.MapBindingResult;

class InstallPackagedTemplates extends OpenSessionIfNeeded {
	/** Global property name that indicates the currently installed CHITS templates version */
	private static final String GP_TEMPLATES_VERSION = "chits.templates.version";

	/** Admin service needed to determine if upgrading is necessary */
	private final AdministrationService adminService;

	/** Controller to use for upgrading the chits notes templates */
	private final InstallPackagedTemplatesController templatesInstaller;

	InstallPackagedTemplates(AdministrationService adminService, InstallPackagedTemplatesController templatesInstaller) {
		super(PrivilegeConstants.MANAGE_GLOBAL_PROPERTIES, //
				PrivilegeConstants.VIEW_USERS, //
				PrivilegeConstants.VIEW_PATIENTS, //
				PrivilegeConstants.VIEW_CONCEPTS, //
				PrivilegeConstants.VIEW_CONCEPT_CLASSES, //
				PrivilegeConstants.VIEW_CONCEPT_DATATYPES, //
				PrivilegeConstants.VIEW_OBS, //
				PrivilegeConstants.MANAGE_CONCEPTS);
		this.adminService = adminService;
		this.templatesInstaller = templatesInstaller;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected void execute() {
		// is upgrading necessary?
		GlobalProperty gpTemplatesVersion = adminService.getGlobalPropertyObject(GP_TEMPLATES_VERSION);
		if (gpTemplatesVersion == null) {
			gpTemplatesVersion = new GlobalProperty(GP_TEMPLATES_VERSION);
			gpTemplatesVersion.setUuid(UUID.randomUUID().toString());
			gpTemplatesVersion.setDescription("Version ID of the InstallPackagedTemplatesController used for determining "
					+ "if an upgrade is necessary during startup");
		}

		if (InstallPackagedTemplatesController.VERSION.equals(gpTemplatesVersion.getPropertyValue())) {
			// no upgrade necessary
			log.info("Notes templates upgrade not necessary.");
		} else {
			// upgrade necessary!
			log.info("Upgrading notes templates...");
			final HttpSession dummySession = new DummyHttpSession();
			try {
				templatesInstaller.showForm(dummySession, new UploadFileForm(), new MapBindingResult(new HashMap(), "errors"));
			} catch (Exception ex) {
				log.error("Error installing templates", ex);
			}

			if (dummySession.getAttribute(WebConstants.OPENMRS_MSG_ATTR) != null) {
				// after the successful upgrade, update the global property
				gpTemplatesVersion.setPropertyValue(InstallPackagedTemplatesController.VERSION);
				adminService.saveGlobalProperty(gpTemplatesVersion);

				// completed...
				log.info("Notes templates upgraded successfully.");
			} else {
				log.warn("Notes templates not upgraded successfully.");
			}
		}
	}
}