package org.openmrs.module.chits.web.controller.mcprogram;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCRegistrationPage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Edits the patient's obstetric history information (same data as on page 1 of the registration form) by emulating the page 1 submission of the
 * SubmitRegistrationController.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/editObstetricHistory.form")
public class EditObstetricHistoryController extends SubmitRegistrationController {
	/**
	 * Emulates page 1 of the submit registration form.
	 */
	@Override
	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// dispatch to superclass
		final MaternalCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// emulate page 1 of the form
		form.setPage(MCRegistrationPage.PAGE1_OBSTETRIC_HISTORY);

		// return the form
		return form;
	}

	/**
	 * Overridden to ensure that the form is set to page 1 regardless of request params.
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form) {
		// ensure that the form is set to page 1 regardless of request params
		form.setPage(MCRegistrationPage.PAGE1_OBSTETRIC_HISTORY);

		// dispatch to superclass
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * Overridden to ensure that the form is set to page 1 regardless of request params.
	 */
	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form, //
			BindingResult errors) {
		// ensure that the form is set to page 1 regardless of request params
		form.setPage(MCRegistrationPage.PAGE1_OBSTETRIC_HISTORY);

		// dispatch to superclass
		return super.handleSubmission(httpSession, request, model, form, errors);
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// send to the ajax fragment
		return "/module/chits/consults/maternalcare/ajaxEditObstetricHistory";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// send to the ajax fragment
		return "/module/chits/consults/maternalcare/ajaxEditObstetricHistory";
	}
}
