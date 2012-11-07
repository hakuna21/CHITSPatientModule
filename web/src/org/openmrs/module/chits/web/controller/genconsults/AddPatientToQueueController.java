package org.openmrs.module.chits.web.controller.genconsults;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientQueue;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Adds a patient to the queue (if not already in the queue).
 */
@Controller
@RequestMapping(value = "/module/chits/consults/addPatientToQueue.form")
public class AddPatientToQueueController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the CHITS service */
	protected CHITSService chitsService;

	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** Auto-wire the Person service */
	protected PersonService personService;

	/** Auto-wire the admin service */
	protected AdministrationService adminService;

	/** Auto-wire the patient consult controller as a dependency */
	protected StartPatientConsultController startPatientConsultController;

	/**
	 * This method will add the patient to the queue.
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String addPatientToQueue(HttpSession httpSession, //
			@RequestParam(required = false, value = "patientId") Integer patientId) {
		log.info("addPatientToQueue: " + patientId);
		// fix for double-posting in IE: synchronize on PatientQueue class
		synchronized (PatientQueue.class) {
			final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;
			PatientQueue patientQueue;
			if (patient == null || patient.getPersonName() == null) {
				// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");
			} else if ((patientQueue = chitsService.getQueuedPatient(patient)) != null) {
				// patient is already in the queue!
				final int minutesElapsed = (int) ((System.currentTimeMillis() - patientQueue.getEnteredQueue().getTime()) / 1000L / 60);
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.consult.patient.already.in.queue");
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, new Object[] { patient.getPersonName().toString(), Integer.valueOf(minutesElapsed) });
			} else {
				// add the patient to the queue
				patientQueue = new PatientQueue();
				patientQueue.setPatient(patient);
				patientQueue.setEnteredQueue(new Date());

				// NOTE: Patient will receive an 'Encounter' only when observations are added!

				// add the patient to the queue
				chitsService.savePatientQueue(patientQueue);

				if (!"true".equals(adminService.getGlobalProperty(GP_ENABLE_QUEUE_TIME))) {
					// automatically start the consult
					startPatientConsultController.startPatientConsult(httpSession, patientId, null);
				}

				// any patients added to the queue must be 'patients'
				if (RelationshipUtil.isNonPatient(patient)) {
					// remove 'non patient' flag from the patient
					RelationshipUtil.setNonPatientFlag(patient, false);
					personService.savePerson(patient);
				}

				// patient added to queue message
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.consult.patient.added.to.queue");
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { patient.getPersonName().toString() });
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
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	@Autowired
	public void setAdminService(AdministrationService adminService) {
		this.adminService = adminService;
	}

	@Autowired
	public void setStartPatientConsultController(StartPatientConsultController startPatientConsultController) {
		this.startPatientConsultController = startPatientConsultController;
	}
}
