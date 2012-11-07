package org.openmrs.module.chits.webservices.rest.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.module.chits.Constants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Encapsulates the 'sessionData' attribute (which should be a Map<? extends String, ? extends Object>) into a SimpleObject accessible through REST.
 * 
 * @author Bren
 */
@Controller
@RequestMapping("/rest/chits/session/data")
public class CHITSSessionDataController {
	/**
	 * This is needed to prevent the default BaseRestController#handleUnknownResource() from intercepting the request.
	 */
	@ResponseBody
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public SimpleObject search(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		final HttpSession session = request.getSession();

		// convert session data map into a simple object
		@SuppressWarnings("unchecked")
		final Map<? extends String, ? extends Object> sessionData = (Map<? extends String, ? extends Object>) session.getAttribute(Constants.SESSION_DATA_KEY);
		final SimpleObject so = new SimpleObject();

		if (sessionData != null) {
			so.putAll(sessionData);
		}

		// send back session data
		return so;
	}
}