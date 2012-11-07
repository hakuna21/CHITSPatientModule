package org.openmrs.module.chits.web.controller.eccdprogram;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.eccdprogram.ChildCareUtil;
import org.openmrs.module.chits.web.controller.BaseUpdatePatientConsultDataController;
import org.openmrs.web.WebConstants;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Decorator around the {@link BaseUpdatePatientConsultDataController} that intercepts POST submissions and sends back to the input page with an error if the
 * patient's child care program has already been concluded.
 * <p>
 * Additionally, this method converts the superclass&apos; {@link PatientConsultEntryForm} form backing object to a {@link ChildCareConsultEntryForm} type after
 * generifying "T" to {@link ChildCareConsultEntryForm}.
 * 
 * @author Bren
 */
public abstract class BaseUpdateChildCarePatientConsultDataController extends BaseUpdatePatientConsultDataController<ChildCareConsultEntryForm> implements
		Constants {
	/**
	 * Convert the superclass&apos; {@link PatientConsultEntryForm} to a {@link ChildCareConsultEntryForm} type.
	 */
	@Override
	public ChildCareConsultEntryForm formBackingObject(HttpServletRequest request, ModelMap model, Integer patientId) throws ServletException {
		final PatientConsultEntryForm pForm = super.formBackingObject(request, model, patientId);

		// convert to a childcare consult entry form
		final ChildCareConsultEntryForm form = new ChildCareConsultEntryForm();
		try {
			PropertyUtils.copyProperties(form, pForm);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}

		// return the encapsulated form
		return form;
	}

	/**
	 * Performs child care pre-requisite checks before forwarding to the update form.
	 * 
	 * @param request
	 * @param httpSession
	 * @param model
	 * @param form
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") ChildCareConsultEntryForm form) {
		// verify that the patient meets the child care prerequisites before showing the form
		if (!ChildCareUtil.childCarePrerequisitesMet(form)) {
			// child care prerequisites have not been met
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.CHILDCARE.weight.and.temperature.required");

			// redirect using patient id
			final Integer patientId = form.getPatient() != null ? form.getPatient().getPatientId() : null;

			// send back to the view consults page
			return viewPatientConsultsController.redirect(patientId);
		} else {
			// ok to proceed: dispatch to superclass
			return super.showForm(request, httpSession, model, form);
		}
	}

	/**
	 * Intercepts the submission and sends back to input page with an error if the patient's child care program has already been concluded.
	 */
	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@ModelAttribute("form") ChildCareConsultEntryForm form, //
			BindingResult errors) {
		if (ChildCareUtil.isProgramClosedFor(form.getPatient())) {
			// childcare program already concluded, updates are not allowed!
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.CHILDCARE.program.closed");

			// send back to the input page
			return getInputPath(request);
		} else if (!ChildCareUtil.childCarePrerequisitesMet(form)) {
			// child care prerequisites have not been met: cannot post updates!
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.CHILDCARE.weight.and.temperature.required");

			// send back to the input page
			return getInputPath(request);
		} else {
			// ok to proceed: dispatch to superclass
			return super.handleSubmission(httpSession, request, model, form, errors);
		}
	}
}
