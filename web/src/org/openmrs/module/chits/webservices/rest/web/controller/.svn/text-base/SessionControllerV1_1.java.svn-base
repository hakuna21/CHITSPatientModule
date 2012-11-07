package org.openmrs.module.chits.webservices.rest.web.controller;

import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/rest/v1_1/session")
public class SessionControllerV1_1 extends BaseRestController {
	@ResponseBody
	@RequestMapping(value = "/rest/v1_1/session", method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public Object get(HttpSession session) {
		return new SimpleObject().add("sessionId", session.getId()).add("authenticated", Boolean.valueOf(Context.isAuthenticated()));
	}

	@ResponseBody
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.DELETE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete() {
		Context.logout();
	}
}