package org.openmrs.module.chits.web.controller.fpprogram;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Family planning program. Displays a read-only view of historic information from a previous (currently ended) family planning program.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/viewFamilyPlanningHistoryChart.form")
public class ViewFamilyPlanningHistoryChartController extends ViewFamilyPlanningController {
	/**
	 * Override superclass because it requires the patient to be currently enrolled in the family planning program (whereas this controller does not).
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public FamilyPlanningConsultEntryForm formBackingObject(ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId) {
		// initialization the form
		final FamilyPlanningConsultEntryForm form = new FamilyPlanningConsultEntryForm();

		// initialize standard consult form backing object (to setup the encounters and patient queue attributes).
		viewPatientConsultsController.initPatientConsultFormBackingObject(patientId, form);

		final Patient patient = form.getPatient();
		if (patient != null) {
			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	/**
	 * This method will display the family planning history chart.
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form) {
		final Patient patient = form.getPatient();
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to view the consults of
			return "redirect:../patients/findPatient.htm";
		}

		// load the latest family planning program observation
		final PatientProgram patientProgram = Functions.getLatestPatientProgram(form.getPatient(), ProgramConcepts.FAMILYPLANNING);

		// store the active program obs instance for this patient
		Obs fpProgramObs = null;
		for (Obs obs : Functions.observations(patient, ProgramConcepts.FAMILYPLANNING)) {
			if (patientProgram.getUuid().equalsIgnoreCase(obs.getUuid())) {
				// this is the observation
				if (fpProgramObs == null) {
					fpProgramObs = obs;
				} else {
					// multiple Obs with the same UUID matching the family planning care patient program UUID ?
					log.warn("Patient (" + patient + ") possesses more than one Obs instance with UUID: " + patientProgram.getUuid());
				}
			}
		}

		// perform some sanity and security checks before allowing the user to view this observation
		if (fpProgramObs != null //
				&& fpProgramObs.getConcept().getConceptId().equals(ProgramConcepts.FAMILYPLANNING.getConceptId()) //
				&& fpProgramObs.getPerson().getPersonId().equals(form.getPatient().getPersonId())) {
			// initialize the family planning program record to use for viewing
			form.setFpProgramObs(new FamilyPlanningProgramObs(fpProgramObs));
		} else {
			// invalid view request
			throw new APIAuthenticationException("chits.program.FAMILYPLANNING.not.enrolled");
		}

		// initialize standard consult form data for rendering the visits section
		viewPatientConsultsController.initPatientConsultForm(model, form, patient);

		// send to the history chart view page
		return getInputPage(request);
	}

	@Override
	protected String getInputPage(HttpServletRequest request) {
		// return the child care program fragment
		return "/module/chits/consults/familyplanning/ajaxFamilyPlanningHistoryChart";
	}
}
