package org.openmrs.module.chits.web.controller.genconsults;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientQueue;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Ends the patient's consult by setting the 'exitedQueue' timestamp.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/endPatientConsult.form")
public class EndPatientConsultController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the CHITS service */
	protected CHITSService chitsService;

	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** Auto-wire the Encounter service */
	protected EncounterService encounterService;

	/** Auto-wire the Concept service */
	protected ConceptService conceptService;

	/** Auto-wire the User service */
	protected UserService userService;

	/**
	 * This method will end the patient's consult.
	 * <p>
	 * NOTE: This method has special checking if 'httpSession' is null since it is called from within CHITSConfig.
	 * 
	 * @param httpSession
	 *            current browser session (optional)
	 * @param patientId
	 *            The patient ID to end the consult of.
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String endPatientConsult(HttpSession httpSession, //
			@RequestParam(required = false, value = "patientId") Integer patientId) {
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");
		} else {
			final PatientQueue patientQueue = chitsService.getQueuedPatient(patient);
			if (patientQueue == null) {
				// patient was not in queue!
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.consult.patient.was.not.in.queue");
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, new Object[] { patient.getPersonName().toString() });
			} else {
				// end the patient's consult
				chitsService.endPatientConsult(encounterService, conceptService, userService, patientQueue);

				if (patientQueue.getConsultEnd() != null) {
					// patient consult successfully removed from the queue
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.consult.patient.consult.ended");
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { patient.getPersonName().toString() });
				} else {
					// patient was simply removed from the queue (consult was not ended because it wasn't started!)
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.consult.patient.removed.from.queue");
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { patient.getPersonName().toString() });
				}
			}
		}

		// send to listing page
		return "redirect:/module/chits/patients/patientQueue.htm";
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Autowired
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}

	@Autowired
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
