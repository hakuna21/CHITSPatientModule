package org.openmrs.module.chits.web.controller.admin;

import java.text.NumberFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.audit.UserSessionInfo;
import org.openmrs.module.chits.propertyeditor.UserEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/module/chits/admin/audit/loggedInUsers.list")
public class LoggedInUsersController {
	/** CHITS Service */
	private CHITSService chitsService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		final NumberFormat nf = NumberFormat.getInstance(Context.getLocale());
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(org.openmrs.User.class, new UserEditor());
	}

	/**
	 * This method will retrieve user session data.
	 * 
	 * @param httpSession
	 *            current browser session
	 * @param model
	 *            The {@link ModelMap}
	 * @param user
	 *            Display only data for the given user (optional)
	 * @param showOnlyLoggedInUsers
	 *            if only the currently logged in users should be displayed
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getUserSessionData(HttpSession httpSession, //
			ModelMap model, //
			@RequestParam(required = false, value = "user") User user, //
			@RequestParam(required = false, value = "showOnlyLoggedInUsers") Boolean showOnlyLoggedInUsers) {
		// load logged-in users and store in model map (maximum of 100 results...)
		final List<UserSessionInfo> userSessionData = chitsService.findUserSessionInfo(user, Boolean.TRUE.equals(showOnlyLoggedInUsers), 0, 500);
		model.put("userSessionData", userSessionData);

		// send to the 'loggedInUsers.jsp' view
		return "/module/chits/admin/users/loggedInUsers";
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}
}
