package org.openmrs.module.chits.web.controller.mcprogram;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientState;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MaternalCareProgramStates;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.web.controller.genconsults.ViewPatientConsultsController;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Maternal Care program. This controller was initially copied from the {@link ViewPatientConsultsController} controller; in the future, it may need to extend
 * from that controller in order to inherit behavior for populating the other tabs.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/viewMaternalCareProgram.form")
public class ViewMaternalCareController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wired consults controller for initializing standard consults data */
	protected ViewPatientConsultsController viewPatientConsultsController;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) {
		// initialization the form
		final MaternalCareConsultEntryForm form = new MaternalCareConsultEntryForm();

		// initialize standard consult form backing object (to setup the encounters and patient queue attributes).
		viewPatientConsultsController.initPatientConsultFormBackingObject(patientId, form);

		final Patient patient = form.getPatient();
		if (patient != null) {
			// initialize the main Maternal Care Program Observation
			final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(patient);
			form.setMcProgramObs(mcProgramObs);

			// this is the maternal program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		}

		// return the patient
		return form;
	}

	/**
	 * This method will display the patient form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form) {
		final Patient patient = form.getPatient();
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to view the consults of
			return "redirect:../patients/findPatient.htm";
		}

		// initialize standard consult form data for rendering the visits section
		viewPatientConsultsController.initPatientConsultForm(model, form, patient);

		// verify that the patient meets the maternal care prerequisites before showing the form
		if (!MaternalCareUtil.maternalCarePrerequisitesMet(form)) {
			// maternal care prerequisites have not been met
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.MATERNALCARE.not.enrolled");

			// redirect using patient id
			final Integer patientId = form.getPatient() != null ? form.getPatient().getPatientId() : null;

			if ("program-details".equals(request.getParameter("section"))) {
				// use an ajax redirect to send back to the view consults page
				return viewPatientConsultsController.ajaxRedirect(patientId);
			} else {
				// send back to the view consults page
				return viewPatientConsultsController.redirect(patientId);
			}
		}

		// clear program if the patient is not enrolled in the program
		if (form.getProgram() != null && !Functions.isInProgram(patient, form.getProgram())) {
			// patient is not in this program!
			form.setProgram(null);
		} else {
			// determine what page the tab should be in, if any...
			final PatientState registeredState = Functions.getPatientState(patient, ProgramConcepts.MATERNALCARE, MaternalCareProgramStates.ACTIVE);
			if (registeredState == null) {
				// send to the 'submit registration' controller
				return "redirect:submitMaternalCareRegistration.form?patientId=" + form.getPatient().getPatientId() //
						+ "&section=" + request.getParameter("section");
			}
		}

		// send back to the page
		return getInputPage(request);
	}

	protected String getInputPage(HttpServletRequest request) {
		if ("program-details".equals(request.getParameter("section"))) {
			// return the maternal care program fragment
			return "/module/chits/consults/maternalcare/fragmentMaternalCareTab";
		} else {
			// return full page
			return "/module/chits/consults/viewPatientConsultForm";
		}
	}

	@Autowired
	public void setViewPatientConsultsController(ViewPatientConsultsController viewPatientConsultsController) {
		this.viewPatientConsultsController = viewPatientConsultsController;
	}
}
