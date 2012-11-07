package org.openmrs.module.chits.web.controller.reports;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Master patient list report form.
 */
@Controller
@RequestMapping(value = "/module/chits/reports/masterListOfPatientsReport.form")
public class MasterListOfPatientsReportController extends MasterListReportController {
	/** Auto-wired patient service */
	private PatientService patientService;

	@Override
	protected String getView() {
		return "/module/chits/reports/ajaxMasterListOfPatientsReport";
	}

	@Override
	protected boolean generateReport(ModelMap model) {
		// load newly created patients
		final List<Patient> patients = patientService.getAllPatients(false);

		// store in model
		model.put("patients", patients);

		// return true if data was found
		return !patients.isEmpty();
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}
}