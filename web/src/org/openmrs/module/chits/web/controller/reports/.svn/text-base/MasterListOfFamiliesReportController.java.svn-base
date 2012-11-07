package org.openmrs.module.chits.web.controller.reports;

import java.util.List;

import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.FamilyFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Master family list report form.
 */
@Controller
@RequestMapping(value = "/module/chits/reports/masterListOfFamiliesReport.form")
public class MasterListOfFamiliesReportController extends MasterListReportController {
	/** Auto-wired CHITS service */
	private CHITSService chitsService;

	@Override
	protected String getView() {
		return "/module/chits/reports/ajaxMasterListOfFamiliesReport";
	}

	@Override
	protected boolean generateReport(ModelMap model) {
		// load newly created families
		final List<FamilyFolder> families = chitsService.getAllFamilyFolders();

		// store in model
		model.put("folders", families);

		// return true if data was found
		return !families.isEmpty();
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}
}