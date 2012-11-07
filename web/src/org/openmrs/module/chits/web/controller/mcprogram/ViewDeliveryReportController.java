package org.openmrs.module.chits.web.controller.mcprogram;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
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
 * Views the delivery report record of the past or present patient maternal care program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/viewDeliveryReport.form")
public class ViewDeliveryReportController {
	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** Auto-wired Observation service */
	private ObsService obsService;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId) throws ServletException {
		// do pre-initialization via superclass
		final MaternalCareConsultEntryForm form = new MaternalCareConsultEntryForm();
		form.setPatient(patientService.getPatient(patientId));

		if (form.getPatient() != null) {
			// this is the maternal care program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		}

		// return the patient
		return form;
	}

	/**
	 * Performs maternal care pre-requisite checks before forwarding to the view form.
	 * 
	 * @param request
	 * @param httpSession
	 * @param model
	 * @param form
	 * @return
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

		// ok to proceed: dispatch to page
		return "/module/chits/consults/maternalcare/ajaxEditDeliveryReport";
	}

	@Autowired
	public void setObsService(ObsService obsService) {
		this.obsService = obsService;
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}
}
