package org.openmrs.module.chits.web.controller.genconsults;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Patient consults (encounters) form controller designed for CHITS.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/viewPatientAnthropometricHistory.form")
public class ViewPatientAnthropometricHistoryController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** Auto-wire the Encounter service */
	protected EncounterService encounterService;

	/**
	 * This method will display the patient form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpSession httpSession, //
			ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId) {
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;

		final Map<Date, Map<Integer, Obs>> encountersByDate = new LinkedHashMap<Date, Map<Integer, Obs>>();
		if (patient != null) {
			// add the patient's encounters (by descending date)
			final List<Encounter> encounters = encounterService.getEncountersByPatient(patient);
			Collections.reverse(encounters);

			for (Encounter enc : encounters) {
				// store the observations keyed by date in a linked hash map
				encountersByDate.put(enc.getEncounterDatetime(), ConceptUtil.getObservationsAsMap(enc));
			}
		}

		// store the encounters keyed by date
		model.addAttribute("encountersByDate", encountersByDate);

		// store patient into model
		model.addAttribute("patient", patient);

		// send to the ajax page
		return "/module/chits/consults/ajaxAnthropometricHistory";
	}

	@Autowired
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}
}
