package org.openmrs.module.chits.web.controller.mcprogram;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ObsService;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Maternal Care program. Displays a read-only view of historic information from a previous (currently ended) maternal care program.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/viewMaternalCareHistoryChart.form")
public class ViewMaternalCareHistoryChartController extends ViewMaternalCareController {
	/** Auto-wire the Obs service */
	protected ObsService obsService;

	/**
	 * Override superclass because it requires the patient to be currently enrolled in the maternal care program (whereas this controller does not).
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId) {
		// initialization the form
		final MaternalCareConsultEntryForm form = new MaternalCareConsultEntryForm();

		// initialize standard consult form backing object (to setup the encounters and patient queue attributes).
		viewPatientConsultsController.initPatientConsultFormBackingObject(patientId, form);

		final Patient patient = form.getPatient();
		if (patient != null) {
			// this is the maternal care program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		}

		// return the patient
		return form;
	}

	/**
	 * This method will display the maternal care history chart.
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET, params = { "maternalCareProgramObsId" })
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form, //
			@RequestParam(required = true, value = "maternalCareProgramObsId") Integer maternalCareProgramObsId) {
		final Patient patient = form.getPatient();
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to view the consults of
			return "redirect:../patients/findPatient.htm";
		}

		// load the maternal care parent record observation for display
		final Obs obs = obsService.getObs(maternalCareProgramObsId);

		// perform some sanity and security checks before allowing the user to view this observation
		if (obs != null //
				&& obs.getConcept().getConceptId().equals(ProgramConcepts.MATERNALCARE.getConceptId()) //
				&& obs.getPerson().getPersonId().equals(form.getPatient().getPersonId())) {
			// initialize the maternal care program record to use for viewing
			form.setMcProgramObs(new MaternalCareProgramObs(obs));
		} else {
			// invalid view request
			throw new APIAuthenticationException("chits.program.MATERNALCARE.not.enrolled");
		}

		// initialize standard consult form data for rendering the visits section
		viewPatientConsultsController.initPatientConsultForm(model, form, patient);

		// send to the history chart view page
		return getInputPage(request);
	}

	@Override
	protected String getInputPage(HttpServletRequest request) {
		// return the child care program fragment
		return "/module/chits/consults/maternalcare/ajaxMaternalCareHistoryChart";
	}

	@Autowired
	public void setObsService(ObsService obsService) {
		this.obsService = obsService;
	}
}
