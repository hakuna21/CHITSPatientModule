package org.openmrs.module.chits.web.controller.reports;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.web.WebConstants;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Base class for reports that dump an entire (master) list, hence, require no date range parameters.
 */
public abstract class MasterListReportController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	@ModelAttribute("form")
	public ReportForm formBackingObject(ModelMap model) throws ServletException {
		// return the patient
		return new ReportForm();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") ReportForm form) {
		// prepare data for the report
		final boolean dataFound = generateReport(model);

		if (!dataFound) {
			// no report found
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.reports.no.records.found.for.criteria");
		}

		// send to report output
		return getView();
	}

	/**
	 * Generates the report and stores it into the model as required by the view
	 * 
	 * @param model
	 *            The model to contain the attributes needed by the view
	 * @return true if data for the report was found, false if no report data available
	 */
	protected abstract boolean generateReport(ModelMap model);

	/**
	 * The report view containing the report (if data has been generated) or the report parameters.
	 * 
	 * @return the view
	 */
	protected abstract String getView();
}