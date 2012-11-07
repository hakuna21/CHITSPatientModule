package org.openmrs.module.chits.webservices.rest.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;
import org.openmrs.module.chits.webservices.rest.resource.DefaulterResource;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Rest service controller for searching for defaulters by barangay code (i.e., patients in a given barangay that have overdue services).
 * <p>
 * Sample URL: <code>http://localhost:8080/openmrs/ws/rest/chits/v1/defaulters?barangayCode=137602023</code>
 * 
 * @author Bren
 */
@Controller
@RequestMapping("/rest/chits/v1/defaulters")
public class CHITSDefaultersController extends BaseRestController {
	public DefaulterResource getResource() {
		return Context.getService(RestService.class).getResource(DefaulterResource.class);
	}

	/**
	 * Search patients by barangay code.
	 */
	@ResponseBody
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "barangayCode" })
	public SimpleObject search(@RequestParam("barangayCode") String barangayCode, HttpServletRequest request, HttpServletResponse response)
			throws ResponseException {
		final RequestContext context = RestUtil.getRequestContext(request);
		context.setRepresentation(Representation.REF);
		return getResource().searchByBarangayCode(barangayCode, context).toSimpleObject();
	}
}