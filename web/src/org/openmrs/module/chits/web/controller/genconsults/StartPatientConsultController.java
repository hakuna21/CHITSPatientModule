package org.openmrs.module.chits.web.controller.genconsults;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
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
 * Starts the patient's consult by setting the 'consultStart' timestamp (if not already started)
 */
@Controller
@RequestMapping(value = "/module/chits/consults/startPatientConsult.form")
public class StartPatientConsultController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the CHITS service */
	protected CHITSService chitsService;

	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** Auto-wire the Encounter service */
	protected EncounterService encounterService;

	/** Auto-wired ViewPatientConsultsController */
	protected ViewPatientConsultsController viewPatientConsultsController;

	/**
	 * This method will start the patient's consult.
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String startPatientConsult(HttpSession httpSession, //
			@RequestParam(required = false, value = "patientId") Integer patientId, //
			@RequestParam(required = false, value = "redirectPath") String redirectPath) {
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");
		} else {
			PatientQueue patientQueue = chitsService.getQueuedPatient(patient);
			if (patientQueue == null) {
				// add the patient to the queue
				patientQueue = new PatientQueue();
				patientQueue.setPatient(patient);
				patientQueue.setEnteredQueue(new Date());
			} else {
				if (patientQueue.getEncounter() != null && patientQueue.getConsultStart() != null) {
					// patient consult already started!
					final int minutesElapsed = (int) ((System.currentTimeMillis() - patientQueue.getConsultStart().getTime()) / 1000L / 60);
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.consult.patient.consult.already.started");
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS,
							new Object[] { patient.getPersonName().toString(), Integer.valueOf(minutesElapsed) });

					// send back to the patient consult page
					return viewPatientConsultsController.redirect(patientId);
				}
			}

			if (patientQueue.getEncounter() == null) {
				// since the consult is beginning, create the encounter for the patient
				final Encounter encounter = encounterService.saveEncounter(newEncounter(patient));
				patientQueue.setEncounter(encounter);
				patientQueue.setConsultStart(new Date());
			}

			// add the patient to the queue
			chitsService.savePatientQueue(patientQueue);

			// patient's consult successfully started
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.consult.patient.consult.started");
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { patient.getPersonName().toString() });
		}

		// send back to the patient consult page
		if (redirectPath != null && redirectPath.startsWith("redirect:/module/chits/")) {
			// send back to redirect location
			return redirectPath;
		} else {
			// either redirectPath not specified or invalid: send back to view patient consults page
			return viewPatientConsultsController.redirect(patientId);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpSession httpSession, //
			@RequestParam(required = false, value = "patientId") Integer patientId, //
			@RequestParam(required = false, value = "redirectPath") String redirectPath) {
		return startPatientConsult(httpSession, patientId, redirectPath);
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

	/**
	 * Initialize a new {@link Encounter} instance for the given {@link Patient} for an initial visit type.
	 * 
	 * @param patient
	 *            The {@link Patient} to create the new encounter for
	 * @return A newly instantiated {@link Encounter} instance for this patient's initial visit.
	 */
	public static Encounter newEncounter(Patient patient) {
		// determine type of encounter (adult / ped, initial / return)
		final EncounterType encType = getEncounterType(patient, true);

		// prepare an encounter at this age
		final Encounter enc = new Encounter();
		enc.setEncounterType(encType);
		enc.setPatient(patient);

		// set provider to currently logged-in user
		final Date now = new Date();
		enc.setProvider(Context.getAuthenticatedUser().getPerson());
		enc.setEncounterDatetime(now);
		enc.setDateChanged(now);
		enc.setDateCreated(now);

		// send back the encounter
		return enc;
	}

	/**
	 * Returns the appropriate {@link EncounterType} based on the patient's age and whether this is an initial or follow-up visit.
	 * 
	 * @param patient
	 *            The patient to get the encounter type for
	 * @param initial
	 *            Whether this is an initial or follow-up visit
	 * @return The {@link EncounterType} that should be used for this patient
	 */
	public static EncounterType getEncounterType(Patient patient, boolean initial) {
		final EncounterService encounterService = Context.getEncounterService();
		final boolean adult = isAdult(patient);

		// load encounter type based on adult status and wether this is an initial or follow-up consult
		final String encTypeName = (adult ? "ADULT" : "PEDS") + (initial ? "INITIAL" : "RETURN");
		return encounterService.getEncounterType(encTypeName);
	}

	/**
	 * Determine if patient is adult: this is useful for determining the encounter type.
	 * 
	 * @param patient
	 *            The patient that will be determined if an adult or not based on the age.
	 * @return true if the patient should be treated as an adult, false otherwise.
	 */
	public static boolean isAdult(Patient patient) {
		// adult if age is 16 or over
		return patient.getAge() == null || patient.getAge() >= 16;
	}

	@Autowired
	public void setViewPatientConsultsController(ViewPatientConsultsController viewPatientConsultsController) {
		this.viewPatientConsultsController = viewPatientConsultsController;
	}
}
