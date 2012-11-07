package org.openmrs.module.chits.web.controller.reports;

import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Daily Services Report form.
 */
@Controller
@RequestMapping(value = "/module/chits/reports/dailyServicesReport.form")
public class DailyServicesReportController extends BaseDateRangeReportController {
	/** Auto-wired encounter service */
	private EncounterService encounterService;

	@Override
	protected String getView() {
		return "/module/chits/reports/ajaxDailyServicesReport";
	}

	@Override
	protected boolean generateReport(ModelMap model, Date startDate, Date endDate) {
		// load matching encounters
		final List<Encounter> encounters = encounterService.getEncounters(null, null, startDate, endDate, null, null, null, false);

		// store in model
		model.put("encounters", encounters);

		// return true if data was found
		return !encounters.isEmpty();
	}

	@Autowired
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}
}