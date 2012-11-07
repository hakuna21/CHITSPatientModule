package org.openmrs.module.chits.web.startup;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.chits.OpenSessionIfNeeded;
import org.openmrs.module.chits.web.DummyHttpSession;
import org.openmrs.module.chits.web.controller.admin.UpgradeCHITSConceptsController;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.WebConstants;

class UpgradeCHITSConcepts extends OpenSessionIfNeeded {
	/** Global property name that indicates the currently installed CHITS concepts version */
	private static final String GP_CONCEPTS_VERSION = "chits.concepts.version";

	/** Admin service needed to determine if upgrading is necessary */
	private final AdministrationService adminService;

	/** Controller to use for upgrading the chits concepts */
	private final UpgradeCHITSConceptsController conceptsUpgrader;

	UpgradeCHITSConcepts(AdministrationService adminService, UpgradeCHITSConceptsController conceptsUpgrader) {
		super(concat(UpgradeCHITSConceptsController.healthworkerPrivileges(), //
				PrivilegeConstants.ADD_PERSONS, //
				PrivilegeConstants.VIEW_ADMIN_FUNCTIONS, //
				PrivilegeConstants.VIEW_PERSON_ATTRIBUTE_TYPES, //
				PrivilegeConstants.VIEW_PROGRAMS, //
				PrivilegeConstants.VIEW_USERS, //
				PrivilegeConstants.MANAGE_PERSON_ATTRIBUTE_TYPES, //
				PrivilegeConstants.MANAGE_ROLES, //
				PrivilegeConstants.MANAGE_GLOBAL_PROPERTIES, //
				PrivilegeConstants.MANAGE_CONCEPTS, //
				PrivilegeConstants.MANAGE_CONCEPT_SOURCES, //
				PrivilegeConstants.MANAGE_CONCEPT_CLASSES, //
				PrivilegeConstants.MANAGE_CONCEPT_DATATYPES, //
				PrivilegeConstants.MANAGE_PROGRAMS, //
				PrivilegeConstants.MANAGE_RELATIONSHIP_TYPES, //
				PrivilegeConstants.VIEW_CONCEPTS, //
				PrivilegeConstants.VIEW_CONCEPT_SOURCES, //
				PrivilegeConstants.VIEW_CONCEPT_CLASSES, //
				PrivilegeConstants.VIEW_CONCEPT_DATATYPES, //
				PrivilegeConstants.VIEW_OBS, //
				PrivilegeConstants.VIEW_RELATIONSHIP_TYPES));
		this.adminService = adminService;
		this.conceptsUpgrader = conceptsUpgrader;
	}

	@Override
	protected void execute() {
		// is upgrading necessary?
		GlobalProperty gpConceptsVersion = adminService.getGlobalPropertyObject(GP_CONCEPTS_VERSION);
		if (gpConceptsVersion == null) {
			gpConceptsVersion = new GlobalProperty(GP_CONCEPTS_VERSION);
			gpConceptsVersion.setUuid(UUID.randomUUID().toString());
			gpConceptsVersion.setDescription("Version ID of the UpgradeCHITSConceptsController used for determining "
					+ "if an upgrade is necessary during startup");
		}

		if (UpgradeCHITSConceptsController.VERSION.equals(gpConceptsVersion.getPropertyValue())) {
			// no upgrade necessary
			log.info("Concepts upgrade not necessary.");
		} else {
			// upgrade necessary!
			log.info("Upgrading concepts...");
			final HttpSession dummySession = new DummyHttpSession();
			try {
				conceptsUpgrader.installCHITSConcepts(dummySession);
			} catch (Exception ex) {
				log.error("Error upgrading concepts", ex);
			}

			if (dummySession.getAttribute(WebConstants.OPENMRS_MSG_ATTR) != null) {
				// after the successful upgrade, update the global property
				gpConceptsVersion.setPropertyValue(UpgradeCHITSConceptsController.VERSION);
				adminService.saveGlobalProperty(gpConceptsVersion);

				// completed...
				log.info("Concepts upgraded successfully.");
			} else {
				log.warn("Concepts not upgraded successfully.");
			}
		}
	}

	/**
	 * Concatenates a the varargs 'otherPrivs' with the 'privs' array and returns an array containing all elements from both
	 * 
	 * @param privs
	 * @param otherPrivs
	 * @return
	 */
	private static String[] concat(String[] privs, String... otherPrivs) {
		if (otherPrivs == null) {
			return privs;
		} else {
			final String[] concatenated = new String[privs.length + otherPrivs.length];
			for (int i = 0; i < privs.length; i++) {
				concatenated[i] = privs[i];
			}

			for (int i = 0; i < otherPrivs.length; i++) {
				concatenated[privs.length + i] = otherPrivs[i];
			}

			return concatenated;
		}
	}
}