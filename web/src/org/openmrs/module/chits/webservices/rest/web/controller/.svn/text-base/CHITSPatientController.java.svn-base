package org.openmrs.module.chits.webservices.rest.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;
import org.openmrs.module.chits.webservices.rest.resource.CHITSPatientResource;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/chits/v1/patient")
public class CHITSPatientController extends BaseCrudController<CHITSPatientResource> {
	public CHITSPatientResource getResource() {
		return Context.getService(RestService.class).getResource(CHITSPatientResource.class);
	}

	/**
	 * Search patients by barangay code.
	 */
	@ResponseBody
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "barangayCode", "modifiedSince", "modifiedUpto" })
	public SimpleObject search(@RequestParam("barangayCode") String barangayCode, @RequestParam("modifiedSince") Long modifiedSince,
			@RequestParam("modifiedUpto") Long modifiedUpto, HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		final RequestContext context = RestUtil.getRequestContext(request);
		context.setRepresentation(Representation.REF);
		return getResource().searchByBarangayCode(barangayCode, modifiedSince, modifiedUpto, context).toSimpleObject();
	}

	/**
	 * This is needed to prevent the default BaseRestController#handleUnknownResource() from intercepting the request.
	 */
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "v", "q" })
	@ResponseBody
	public SimpleObject search(@RequestParam("v") String version, @RequestParam("q") String query, HttpServletRequest request, HttpServletResponse response)
			throws ResponseException {
		return super.search(query, request, response);
	}
}