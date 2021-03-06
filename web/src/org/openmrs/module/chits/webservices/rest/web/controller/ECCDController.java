package org.openmrs.module.chits.webservices.rest.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.chits.webservices.rest.resource.ECCDEncounterRecordResource;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Place holder class for supporting the ECCD program. Only GET and POST methods are supported to retrieve and update the ECCD record of the patient.
 * <p>
 * The UUID to use in the request should be the UUID of the patient that is enrolled in the ECCD program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping("/rest/chits/v1/programs/eccd")
public class ECCDController extends BaseCrudController<ECCDEncounterRecordResource> {
	/**
	 * Search patient visits by barangay code that fall under the ECCD program.
	 */
	@ResponseBody
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "barangayCode", "visitedFrom", "visitedTo" })
	public SimpleObject searchVisitsByBarangayCode(@RequestParam("barangayCode") String barangayCode, @RequestParam("visitedFrom") Long visitedFrom,
			@RequestParam("visitedTo") Long visitedTo, HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		final RequestContext context = RestUtil.getRequestContext(request);
		context.setRepresentation(Representation.REF);
		return getResource().searchVisitsByBarangayCode(barangayCode, visitedFrom, visitedTo, context).toSimpleObject();
	}
}