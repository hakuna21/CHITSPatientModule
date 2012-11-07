package org.openmrs.module.chits.web.controller.reports;

import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.module.chits.CHITSPatientSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Newly Registered Patients Report form.
 */
@Controller
@RequestMapping(value = "/module/chits/reports/newlyRegisteredPatientsReport.form")
public class NewlyRegisteredPatientsReportController extends BaseDateRangeReportController {
	/** Auto-wired CHITS patient search service */
	private CHITSPatientSearchService chitsPatientSearchService;

	@Override
	protected String getView() {
		return "/module/chits/reports/ajaxNewlyRegisteredPatientsReport";
	}

	@Override
	protected boolean generateReport(ModelMap model, Date startDate, Date endDate) {
		// load newly created patients
		final List<Patient> patients = chitsPatientSearchService.getPatientsCreatedBetween(startDate, endDate);

		// store in model
		model.put("patients", patients);

		// return true if data was found
		return !patients.isEmpty();
	}

	@Autowired
	public void setChitsPatientSearchService(CHITSPatientSearchService chitsPatientSearchService) {
		this.chitsPatientSearchService = chitsPatientSearchService;
	}
}