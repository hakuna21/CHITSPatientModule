package org.openmrs.module.chits.web.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.UserAuthorizedBarangaysForm;
import org.openmrs.module.chits.UserBarangay;
import org.openmrs.module.chits.propertyeditor.UserEditor;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for managing user-authorized barangays
 * 
 * @see org.openmrs.web.controller.person.PersonFormController
 */
@Controller
@RequestMapping(value = "/module/chits/admin/users/manageAuthorizedBarangays.form")
public class ManageAuthorizedBarangaysController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the CHITS service */
	protected CHITSService chitsService;

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(org.openmrs.User.class, new UserEditor());
	}

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public UserAuthorizedBarangaysForm formBackingObject() throws ServletException {
		// prepare a blank form: 'user' should be populated via the 'user' parameter
		final UserAuthorizedBarangaysForm form = new UserAuthorizedBarangaysForm();

		// return an empty form
		return form;
	}

	/**
	 * This method will display the manage authorized barangays form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @param form
	 *            The UserAuthorizedBarangaysForm backing object
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpSession httpSession, //
			@ModelAttribute("form") UserAuthorizedBarangaysForm form) {
		if (form == null || form.getUser() == null) {
			// user not found; treat this as an error
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.admin.user.not.found");

			// send back to manage users page since there is no user to view!
			return "redirect:/admin/users/users.list";
		}

		// populate the user's authorized barangays
		if (form.getUser() != null) {
			for (UserBarangay authorizedBrgy : chitsService.getUserBarangays(form.getUser())) {
				final String brgyCode = authorizedBrgy.getBarangayCode();
				form.getUserBarangayCodes().add(brgyCode);
			}
		}

		// send to the form page
		return "/module/chits/admin/users/manageBarangays";
	}

	/**
	 * Updates the user's authorized barangays.
	 * 
	 * @param httpSession
	 *            current browser session
	 * @param form
	 *            The UserAuthorizedBarangaysForm backing object
	 * @param errors
	 *            If there are form validation errors
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			@ModelAttribute("form") UserAuthorizedBarangaysForm form, //
			BindingResult errors) {
		if (form.getUser() == null || form.getUserBarangayCodes() == null) {
			// user not found; treat this as an error
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.admin.user.not.found");

			// send back to manage users page since there is no user to view!
			return "/admin/users/users.list";
		}

		// update barangay codes of the user
		chitsService.setUserBarangayCodes(form.getUser(), form.getUserBarangayCodes());

		// success: add success message
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.user.authorized.barangays.updated");

		// send back to the manage barangays page
		return "redirect:/module/chits/admin/users/manageAuthorizedBarangays.form?user=" + form.getUser().getUserId();
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}
}
