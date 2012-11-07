package org.openmrs.module.chits.web.controller.admin;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.web.controller.user.UserFormController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Extend built-in UserFormController to add support for barangays.
 */
@Controller
public class CHITSUserFormController {
	/** Prepare dispatcher instance */
	private UserFormController dispatcher = new UserFormController();

	/** CHITS Service */
	private CHITSService chitsService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		dispatcher.initBinder(binder);
	}

	// the personId attribute is called person_id so that spring MVC doesn't try to bind it to the personId property of user
	@ModelAttribute("user")
	public User formBackingObject(WebRequest request, @RequestParam(required = false, value = "person_id") Integer personId) {
		return dispatcher.formBackingObject(request, personId);
	}

	@ModelAttribute("allRoles")
	public List<Role> getRoles(WebRequest request) {
		return dispatcher.getRoles(request);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(@RequestParam(required = false, value = "userId") Integer userId,
			@RequestParam(required = false, value = "createNewPerson") String createNewPerson, @ModelAttribute("user") User user, ModelMap model) {
		// dispatch to dispatcher
		final String userForm = dispatcher.showForm(userId, createNewPerson, user, model);

		if ("admin/users/userForm".equals(userForm)) {
			if (user != null && user.getUserId() != null && user.getUserId() > 0) {
				// add 'barangay' information to the model
				model.put("authorizedBarangays", chitsService.getUserBarangays(user));
			}

			// use our own version of the user form which is a clone of the core user form but with the addition of a link to the barangay assignments (this was
			// necessary since there are no existing extension points that we can reuse in the 1.8.2.0000 version of openmrs)
			return "/module/chits/coreoverrides/admin/users/userForm";
		} else {
			// unknown form, use default behavior
			return userForm;
		}
	}

	/**
	 * @should work for an example
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(WebRequest request, HttpSession httpSession, ModelMap model,
			@RequestParam(required = false, value = "action") String action, @RequestParam(required = false, value = "userFormPassword") String password,
			@RequestParam(required = false, value = "secretQuestion") String secretQuestion,
			@RequestParam(required = false, value = "secretAnswer") String secretAnswer, @RequestParam(required = false, value = "confirm") String confirm,
			@RequestParam(required = false, value = "forcePassword") Boolean forcePassword,
			@RequestParam(required = false, value = "roleStrings") String[] roles,
			@RequestParam(required = false, value = "createNewPerson") String createNewPerson, @ModelAttribute("user") User user, BindingResult errors) {
		return dispatcher.handleSubmission(request, httpSession, model, action, password, secretQuestion, secretAnswer, confirm, forcePassword, roles,
				createNewPerson, user, errors);
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}
}
