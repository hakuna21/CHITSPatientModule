package org.openmrs.module.chits.webservices.rest.web.controller;

import javax.servlet.http.HttpSession;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/chits/v1/timestamp")
public class ServerTimestamp extends BaseRestController {
	@ResponseBody
	@RequestMapping(value = "/rest/chits/v1/timestamp", method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public Object get(HttpSession session) {
		return new SimpleObject().add("now", System.currentTimeMillis());
	}
}