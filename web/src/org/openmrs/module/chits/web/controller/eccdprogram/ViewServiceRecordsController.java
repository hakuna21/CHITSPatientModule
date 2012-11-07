package org.openmrs.module.chits.web.controller.eccdprogram;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Submits the childcare registration form.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/viewChildCareServiceRecords.form")
public class ViewServiceRecordsController extends ViewChildCareController implements Constants {
	/**
	 * Override to set the version object in case the user clicks on one of the 'Add Service' buttons.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") ChildCareConsultEntryForm form) {
		// manually set the version into the form based on the encounter instance
		final Auditable versionObject = form.getEncounter();
		if (versionObject != null && versionObject.getDateChanged() != null) {
			// use version from the latest service rendered
			form.setVersion(versionObject.getDateChanged().getTime());
		}

		// dispatch regular processing to superclass
		return super.showForm(request, httpSession, model, form);
	}

	@Override
	protected String getInputPage(HttpServletRequest request) {
		// return ajax page
		return "/module/chits/consults/childcare/ajaxUpdateChildCareServiceRecords";
	}
}
