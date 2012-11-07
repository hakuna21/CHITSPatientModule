package org.openmrs.module.chits.web.controller.admin;

import java.io.File;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.installer.ThemeInstaller;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Theme installer controller.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/installTheme")
public class InstallThemeController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * This method will extract the theme stored in '/openmrs-chits-theme.zip' into the root context folder.
	 * 
	 * @param httpRequest
	 *            The request instance
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String installTheme(HttpSession httpSession) {
		// user must be an administrator
		if (!Context.hasPrivilege(PrivilegeConstants.VIEW_ADMIN_FUNCTIONS)) {
			throw new APIAuthenticationException("Privilege required: " + PrivilegeConstants.VIEW_ADMIN_FUNCTIONS);
		}

		// get context root
		final File contextRootPath = new File(httpSession.getServletContext().getRealPath("/"));

		try {
			// perform the theme installation
			final int filesUpdated = new ThemeInstaller().doInstall(contextRootPath);

			if (filesUpdated == 0) {
				// nothing changes performed
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.Theme.nothing.installed");
			} else {
				// files were changed
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.Theme.installed.please.restart");
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { Integer.toString(filesUpdated) });
			}
		} catch (Exception ex) {
			log.error("Theme installation failed", ex);

			// indicate the error
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, new Object[] { ex.getMessage() });
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Theme.installation.failed");
		}

		// send to the admin page
		return "redirect:/admin/index.htm";
	}
}
