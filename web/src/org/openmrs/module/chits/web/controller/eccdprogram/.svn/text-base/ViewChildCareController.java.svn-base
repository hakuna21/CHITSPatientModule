package org.openmrs.module.chits.web.controller.eccdprogram;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientState;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.module.chits.CHITSPatientSearchService;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareUtil;
import org.openmrs.module.chits.eccdprogram.ServiceUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareProgramStates;
import org.openmrs.module.chits.eccdprogram.ServiceUtil.ServiceStatus;
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
 * Child Care program. This controller was initially copied from the {@link ViewPatientConsultsController} controller; in the future, it may need to extend from
 * that controller in order to inherit behavior for populating the other tabs.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/viewChildCareProgram.form")
public class ViewChildCareController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Indirectly auto-wired RelationshipUtil class */
	private RelationshipUtil relationshipUtil;

	/** Auto-wired consults controller for initializing standard consults data */
	private ViewPatientConsultsController viewPatientConsultsController;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public ChildCareConsultEntryForm formBackingObject(ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) {
		// initialization the form
		final ChildCareConsultEntryForm form = new ChildCareConsultEntryForm();

		// initialize standard consult form backing object (to setup the encounters and patient queue attributes).
		viewPatientConsultsController.initPatientConsultFormBackingObject(patientId, form);

		// add encounters
		if (form.getPatient() != null) {
			// store the patient's parent records (if any)
			final Patient mother = relationshipUtil.getPatientMotherOrCreteNew(form.getPatient().getPatientId());
			form.setMother(mother);

			final Patient father = relationshipUtil.getPatientFatherOrCreteNew(form.getPatient().getPatientId());
			form.setFather(father);
		}

		// this is the child care program
		form.setProgram(ProgramConcepts.CHILDCARE);

		// return the patient
		return form;
	}

	/**
	 * Populates the service status model
	 */
	@ModelAttribute("serviceStatus")
	public ServiceStatus getServiceStatus(@ModelAttribute("form") ChildCareConsultEntryForm form) {
		// Extract the service status information
		final ServiceStatus serviceStatus = ServiceUtil.getServiceStatus(form.getPatient());

		// and send back the model
		return serviceStatus;
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
			@ModelAttribute("form") ChildCareConsultEntryForm form) {
		final Patient patient = form.getPatient();
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to view the consults of
			return "redirect:../patients/findPatient.htm";
		}

		// initialize standard consult form data for rendering the visits section
		viewPatientConsultsController.initPatientConsultForm(model, form, patient);

		// verify that the patient meets the child care prerequisites before showing the form
		if (!ChildCareUtil.childCarePrerequisitesMet(form)) {
			// child care prerequisites have not been met
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.CHILDCARE.weight.and.temperature.required");

			// redirect using patient id
			final Integer patientId = form.getPatient() != null ? form.getPatient().getPatientId() : null;

			// send back to the view consults page
			return viewPatientConsultsController.redirect(patientId);
		}

		// clear program if the patient is not enrolled in the program
		if (form.getProgram() != null && !Functions.isInProgram(patient, form.getProgram())) {
			// patient is not in this program!
			form.setProgram(null);
		} else {
			// if available, store the 'registered' state's start_date into the form so that the user can change the registration data
			final PatientState registeredState = Functions.getPatientState(patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.REGISTERED);
			if (registeredState == null) {
				// send to the 'submit registration' controller
				return "redirect:submitChildCareRegistration.form?patientId=" + form.getPatient().getPatientId() //
						+ "&section=" + request.getParameter("section");
			}
		}

		// send back to the page
		return getInputPage(request);
	}

	protected String getInputPage(HttpServletRequest request) {
		if ("program-details".equals(request.getParameter("section"))) {
			// return the child care program fragment
			return "/module/chits/consults/childcare/fragmentChildCareTab";
		} else {
			// return full page
			return "/module/chits/consults/viewPatientConsultForm";
		}
	}

	@Autowired
	public void setViewPatientConsultsController(ViewPatientConsultsController viewPatientConsultsController) {
		this.viewPatientConsultsController = viewPatientConsultsController;
	}

	@Autowired
	public void initRelationshipUtil(PersonService personService, PatientService patientService, CHITSPatientSearchService chitsPatientSearchService) {
		this.relationshipUtil = new RelationshipUtil(personService, patientService, chitsPatientSearchService);
	}
}
