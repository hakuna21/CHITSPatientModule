package org.openmrs.module.chits.web.controller.genconsults;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientQueue;
import org.openmrs.propertyeditor.PatientEditor;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Sets or clears the follow-up consult flag for the current visit.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/setFollowUpConsult.form")
public class SetFollowUpConsultController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wired CHITS service */
	protected CHITSService chitsService;

	/** Auto-wired encounter service */
	protected EncounterService encounterService;

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(org.openmrs.Patient.class, new PatientEditor());
	}

	/**
	 * Sets the encounter type to initial or return depending on the followUpConsult parameter.
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			@RequestParam(required = true, value = "patient") Patient patient, //
			@RequestParam(required = false, value = "followUpConsult") Boolean followUpConsult) {
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to view the consults of
			return "redirect:/module/chits/patients/findPatient.htm";
		}

		// get current patient in queue
		final PatientQueue pq = patient != null ? chitsService.getQueuedPatient(patient) : null;
		if (pq == null || pq.getEncounter() == null) {
			// patient queue not started!
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.consult.not.started");
		} else {
			// obtain the current encounter (visit) to update the encounter type
			final Encounter enc = pq.getEncounter();
			if (Boolean.TRUE.equals(followUpConsult)) {
				// this is a follow-up consult: set the encounter type to follow-up (i.e., non-initial)
				enc.setEncounterType(StartPatientConsultController.getEncounterType(patient, false));

				// set appropriate message
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.Patient.consult.set.to.follow.up");
			} else {
				// this is not a follow-up consult (i.e., initial)
				enc.setEncounterType(StartPatientConsultController.getEncounterType(patient, true));

				// set appropriate message
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.Patient.consult.set.to.initial");
			}

			// save the encounter with the changed encounter type
			enc.setDateChanged(new Date());
			enc.setChangedBy(Context.getAuthenticatedUser());
			encounterService.saveEncounter(enc);
		}

		// redirect to the visits page
		return "redirect:/module/chits/consults/viewPatient.form?patientId=" + patient.getPatientId();
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}

	@Autowired
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}
}
