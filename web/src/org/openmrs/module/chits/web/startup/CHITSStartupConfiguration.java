package org.openmrs.module.chits.web.startup;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.UserService;
import org.openmrs.module.chits.CHITSPatientModuleActivator;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.ConceptUtilFactory;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.web.controller.admin.InstallPackagedTemplatesController;
import org.openmrs.module.chits.web.controller.admin.UpgradeCHITSConceptsController;
import org.openmrs.module.chits.web.controller.admin.UploadConceptsController;
import org.openmrs.module.chits.web.patch.AuditConceptsPatch;
import org.openmrs.module.chits.web.patch.ConceptDictionary20120702Patch;
import org.openmrs.module.chits.web.patch.MaternalCareConceptsPatch;
import org.openmrs.module.chits.web.patch.PhysicianUUIDPatch;
import org.openmrs.module.chits.web.patch.VaccineDefinitionsPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Sets up the following startup tasks:
 * <ul>
 * <li>Registers the AuditUtilTask (@see {@link RegisterAuditUtilTask})
 * <li>Initializes application-scoped variables (@see {@link InitApplicationVariables})
 * <li>Clears the patient queue (@see {@link RemovePatientsInQueueTask})
 * <li>Cleans / purges user session data table (@see {@link PurgeOldUserSessionInfo})
 * <li>Upgrades CHITS concepts if needed (@see {@link UpgradeCHITSConcepts}
 * </ul>
 * 
 * @author Bren
 */
@Configuration
public class CHITSStartupConfiguration {
	/** Logger instance */
	protected final Log log = LogFactory.getLog(getClass());

	public CHITSStartupConfiguration() {
		// default constructor
	}

	@Autowired
	public void setupAuditUtilTask(final ServletContext servletContext) {
		try {
			// add a task to register to the user session tracker
			CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new RegisterAuditUtilTask(servletContext));
		} catch (Exception ex) {
			// this error may occur if the chits-tags.jar file has not yet been copied to the WEB-INF/lib folder
			log.warn("Error prepping RegisterAuditUtilTask", ex);
		}
	}

	@Autowired
	public void initApplicationVariablesTask(final ServletContext servletContext, final AdministrationService adminService) {
		// add a task to initialize the servlet context attributes when ready
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new InitApplicationVariables(servletContext, adminService));
	}

	@Autowired
	public void setupInstallTemplatesTask(final ServletContext servletContext, final AdministrationService adminService,
			final InstallPackagedTemplatesController templatesInstaller) {
		// add a listener that will upgrade the CHITS concepts if needed
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new InstallPackagedTemplates(adminService, templatesInstaller));
	}

	@Autowired
	public void setupRemovePatientsInQueueTask(final AdministrationService adminService, final CHITSService chitsService,
			final EncounterService encounterService, final ConceptService conceptService, final UserService userService) {
		if (Boolean.valueOf(adminService.getGlobalProperty(Constants.GP_CLEAR_QUEUE_ON_RESTART, "true"))) {
			// add a listener that will purge the patient queue when the context is refreshed
			CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(
					new RemovePatientsInQueueTask(chitsService, encounterService, conceptService, userService));
		}
	}

	@Autowired
	public void setupPurgeOldUserSessionInfoTask(final ServletContext servletContext, final CHITSService chitsService, final AdministrationService adminService) {
		// add a listener that will purge old user session data
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new PurgeOldUserSessionInfo(servletContext, chitsService, adminService));
	}

	@Autowired
	public void installV010108PatchesTask(final ServletContext servletContext, final CHITSService chitsService, final AdministrationService adminService,
			final ConceptService conceptService, final ConceptUtilFactory conceptUtilFactory, final UploadConceptsController uploadConceptsController,
			final UpgradeCHITSConceptsController conceptsUpgrader) {
		// install the patch that will randomize the UUID of the 'physician' concept that belongs to 'clinical officer/doctor'
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new PhysicianUUIDPatch(adminService, conceptService));

		// install the concept dictionary version 20120509 patch
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(
				new ConceptDictionary20120702Patch(adminService, servletContext, uploadConceptsController));

		// add a listener that will upgrade the CHITS concepts
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new UpgradeCHITSConcepts(adminService, conceptsUpgrader));

		// install the child care vaccine definitions patch
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new VaccineDefinitionsPatch(adminService, conceptUtilFactory.newInstance()));

		// install the audit concepts definitions patch
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new AuditConceptsPatch(adminService, conceptUtilFactory.newInstance()));

		// install the maternal care concept definitions patch
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(
				new MaternalCareConceptsPatch(adminService, conceptService, conceptUtilFactory.newInstance()));
	}
}
