package org.openmrs.module.chits.webservices.rest.web.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.UserBarangay;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/chits/v1/userauthorization")
public class CHITSUserAccessController {
	/** Auto-wired service */
	private CHITSService chitsService;

	/**
	 * Retrieves the user authorized entities such as the barangay codes assigned to the currently logged-in user.
	 * <p>
	 * Currently, this method returns a list of authorized barangay codes that the currently logged-in user has access to.
	 * 
	 * @return SimpleObject encapsulating the user authorized entities
	 * @throws ResponseException
	 */
	@ResponseBody
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public SimpleObject getAuthorizedEntities(HttpServletRequest request) throws ResponseException {
		// user must be logged-in!
		if (!Context.isAuthenticated()) {
			throw new APIAuthenticationException("Not logged in.");
		}

		// prepare the authorized entities
		final SimpleObject authorizedEntities = new SimpleObject();

		// Load the barangay codes accessible by this user
		final ArrayList<String> barangayCodes = new ArrayList<String>();
		for (UserBarangay userBrgy : chitsService.getUserBarangays(Context.getAuthenticatedUser())) {
			if (userBrgy.getBarangayCode() != null && !"".equals(userBrgy.getBarangayCode().trim())
					&& !barangayCodes.contains(userBrgy.getBarangayCode().trim())) {
				barangayCodes.add(userBrgy.getBarangayCode());
			}
		}

		// store authorized barangay codes for this user
		authorizedEntities.put("authorizedBarangayCodes", barangayCodes);

		// send the set of accessible entities back to the caller
		return authorizedEntities;
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}
}