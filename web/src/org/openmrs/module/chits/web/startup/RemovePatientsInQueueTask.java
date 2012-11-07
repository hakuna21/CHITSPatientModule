package org.openmrs.module.chits.web.startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.UserService;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.OpenSessionIfNeeded;
import org.openmrs.util.PrivilegeConstants;

/**
 * Clears out the patient queue (hence, also clearing the stats).
 * 
 * @author Bren
 */
class RemovePatientsInQueueTask extends OpenSessionIfNeeded {
	/** Logger */
	private Log log = LogFactory.getLog(this.getClass());

	/** Reference to chits service */
	private final CHITSService chitsService;

	/** Reference to the encounterService */
	private final EncounterService encounterService;

	/** Reference to the conceptService */
	private final ConceptService conceptService;

	/** Reference to the userService */
	private final UserService userService;

	/**
	 * Initialize dependencies.
	 * 
	 * @param servletContext
	 * @param chitsService
	 */
	RemovePatientsInQueueTask(CHITSService chitsService, EncounterService encounterService, ConceptService conceptService, UserService userService) {
		super(PrivilegeConstants.VIEW_CONCEPTS, //
				PrivilegeConstants.VIEW_PATIENTS, //
				PrivilegeConstants.VIEW_USERS, //
				PrivilegeConstants.EDIT_PATIENTS, //
				PrivilegeConstants.ADD_ENCOUNTERS, //
				PrivilegeConstants.EDIT_ENCOUNTERS, //
				PrivilegeConstants.PURGE_ENCOUNTERS);
		this.chitsService = chitsService;
		this.encounterService = encounterService;
		this.conceptService = conceptService;
		this.userService = userService;
	}

	/**
	 * Ends consult for all patients in queue and subsequently purges the patient queue entries.
	 */
	@Override
	protected void execute() {
		try {
			// end consults and purge patient queue entries
			chitsService.purgePatientQueue(encounterService, conceptService, userService);
		} catch (Exception ex) {
			log.warn("Unable to purge patient queue records!", ex);
		}
	}
}