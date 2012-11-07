package org.openmrs.module.chits.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Util;
import org.openmrs.web.controller.PortletController;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Patient queue portlet controller for displaying the patient queue.
 * 
 * @author Bren
 */
public class PatientQueuePortletController extends PortletController {
	/** Autowired CHITS service */
	private CHITSService chitsService;

	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		// store the patient queue
		model.put("patientQueueEntries", chitsService.getQueuedPatients(0, Util.getMaximumSearchResults()));
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}
}
