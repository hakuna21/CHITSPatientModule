package org.openmrs.module.chits.web.controller.genconsults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.ConceptUtilFactory;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultForm;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Patient consults (encounters) form controller designed for CHITS.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/viewPatient.form")
public class ViewPatientConsultsController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the CHITS service */
	protected CHITSService chitsService;

	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** Auto-wire the Encounter service */
	protected EncounterService encounterService;

	/** Auto-wire the Order service */
	protected OrderService orderService;

	/** Auto-wire the concept service */
	protected ConceptService conceptService;

	/** Auto-wire the person service */
	protected PersonService personService;

	/** Auto-wire the location service */
	protected LocationService locationService;

	/** Auto-wire the concept util */
	protected ConceptUtilFactory conceptUtilFactory;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public PatientConsultForm formBackingObject(ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) {
		// prepare the patient's form
		final PatientConsultForm form = new PatientConsultForm();

		// initialize standard consult form backing object (to setup the encounters and patient queue attributes).
		initPatientConsultFormBackingObject(patientId, form);

		// return the patient
		return form;
	}

	/**
	 * Initializes patient standard consult form backing object (sets up the encounters and patient queue attributes).
	 * 
	 * @param patientId
	 * @param form
	 */
	public void initPatientConsultFormBackingObject(Integer patientId, PatientConsultForm form) {
		// load the patient record
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;
		form.setPatient(patient);

		// store patient context
		ObsUtil.PATIENT_CONTEXT.set(patient);

		if (patient != null) {
			// add the patient's encounters (by descending date)
			final List<Encounter> encounters = encounterService.getEncountersByPatient(patient);
			Collections.reverse(encounters);
			form.setEncounters(encounters);

			// add the patient's queue information
			form.setPatientQueue(chitsService.getQueuedPatient(patient));
		}
	}

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
			@ModelAttribute("form") PatientConsultForm form, //
			@RequestParam(required = false, value = "section") String ajaxSection) {
		final Patient patient = form.getPatient();
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to view the consults of
			return "redirect:../patients/findPatient.htm";
		}

		// initialize standard consult form data
		initPatientConsultForm(model, form, patient);

		if ("visit-details".equals(ajaxSection)) {
			// return just the visit details section
			return "/module/chits/consults/fragmentVisitDetails";
		} else {
			// return full page
			return "/module/chits/consults/viewPatientConsultForm";
		}
	}

	/**
	 * This method will display the patient form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET, params = { "ajaxReload" })
	public String showForm(HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") PatientConsultForm form) {
		final Patient patient = form.getPatient();
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to view the consults of
			return "redirect:../patients/findPatient.htm";
		}

		// return an ajax fragment to redirect the page to the view patient consult page
		return "/module/chits/consults/ajaxReloadVisitDetails";
	}

	/**
	 * Initializes standard consult form data for rendering the standard patient consults visits section.
	 * 
	 * @param model
	 * @param form
	 * @param patient
	 */
	public void initPatientConsultForm(ModelMap model, PatientConsultForm form, final Patient patient) {
		// attach the associated folders of this patient
		final List<FamilyFolder> familyFolders = new ArrayList<FamilyFolder>();
		model.addAttribute("familyFolders", familyFolders);

		// set the patient's associated family folders
		if (patient.getPatientId() != null) {
			// viewing an existing patient; attach the patient's associated family folders
			familyFolders.addAll(chitsService.getFamilyFoldersOf(patient.getPatientId()));
		}

		// add support data to the model
		model.addAttribute(WebConstants.OPENMRS_HEADER_USE_MINIMAL, "true");

		// store drug orders
		final List<DrugOrder> drugOrders = new ArrayList<DrugOrder>();
		model.put("drugOrders", drugOrders);
		if (form.getPatientQueue() != null && form.getPatientQueue().getEncounter() != null) {
			for (Order order : form.getPatientQueue().getEncounter().getOrders()) {
				final DrugOrder drugOrder = orderService.getOrder(order.getOrderId(), DrugOrder.class);
				if (drugOrder != null) {
					drugOrders.add(drugOrder);
				}
			}
		}
	}

	/**
	 * Redirect to this controller given the patient ID.
	 * 
	 * @param patientId
	 *            The patient ID to view
	 * @return A spring view sending redirecting the request to this controller
	 */
	public String redirect(Integer patientId) {
		return "redirect:/module/chits/consults/viewPatient.form?patientId=" + (patientId != null ? patientId : 0);
	}

	/**
	 * Returns an ajax response that contains JavaScript code to redirect to the view patient consult controller given the patient ID.
	 * 
	 * @param patientId
	 *            The patient ID to view
	 * @return A spring view sending redirecting the request to this controller
	 */
	public String ajaxRedirect(Integer patientId) {
		return "redirect:/module/chits/consults/viewPatient.form?patientId=" + (patientId != null ? patientId : 0) + "&ajaxReload=true";
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
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	@Autowired
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Autowired
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	@Autowired
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}

	@Autowired
	public void setConceptUtilFactory(ConceptUtilFactory conceptUtilFactory) {
		this.conceptUtilFactory = conceptUtilFactory;
	}
}
