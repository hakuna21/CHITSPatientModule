package org.openmrs.module.chits.web.controller.genconsults;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientHistoricalConsultForm;
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
@RequestMapping(value = "/module/chits/consults/viewPatientHistoricalConsult.form")
public class ViewPatientHistoricalConsultsController implements Constants {
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

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public Object formBackingObject(ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId, //
			@RequestParam(required = true, value = "encounterId") Integer encounterId) throws ServletException {
		// prepare the patient's form
		final PatientHistoricalConsultForm form = new PatientHistoricalConsultForm();
		form.setPatient(patientService.getPatient(patientId));

		// store the historical encounter
		form.setEncounter(encounterService.getEncounter(encounterId));

		if (form.getPatient() != null) {
			// add the patient's queue information
			form.setPatientQueue(chitsService.getQueuedPatient(form.getPatient()));
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
	public String showForm(HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") PatientHistoricalConsultForm form, //
			@RequestParam(required = false, value = "section") String ajaxSection) {
		if (form.getPatient() == null || form.getPatient().getPersonName() == null || form.getEncounter() == null) {
			// patient not found; treat this as an error
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to view the consults of
			return "redirect:../patients/findPatient.htm";
		}

		// store drug orders
		final List<DrugOrder> drugOrders = new ArrayList<DrugOrder>();
		model.put("drugOrders", drugOrders);
		if (form.getEncounter() != null) {
			for (Order order : form.getEncounter().getOrders()) {
				final DrugOrder drugOrder = orderService.getOrder(order.getOrderId(), DrugOrder.class);
				if (drugOrder != null) {
					drugOrders.add(drugOrder);
				}
			}
		}

		// return page displaying historical consult information
		return "/module/chits/consults/ajaxPatientHistoricalConsultForm";
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
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}

	@Autowired
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
}
