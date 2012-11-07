package org.openmrs.module.chits.web.controller.reports;

import java.text.NumberFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Base class for reports that require a date range as parameters
 */
public abstract class BaseDateRangeReportController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		final NumberFormat nf = NumberFormat.getInstance(Context.getLocale());
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
	}

	@ModelAttribute("form")
	public ReportForm formBackingObject(ModelMap model) throws ServletException {
		// return the patient
		return new ReportForm();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") ReportForm form) {
		// assume 'start' and 'end' date is today's date
		form.setStartDate(new Date());
		form.setEndDate(new Date());

		return getView();
	}

	@RequestMapping(method = RequestMethod.POST)
	public String generateReport(HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") ReportForm form, //
			BindingResult errors) {
		if (form.getStartDate() == null) {
			errors.rejectValue("startDate", "chits.reports.start.date.required");
		}

		if (form.getEndDate() == null) {
			errors.rejectValue("endDate", "chits.reports.end.date.required");
		}

		if (!errors.hasErrors()) {
			// prepare data for the report
			final boolean dataFound = generateReport(model, DateUtil.stripTime(form.getStartDate()), DateUtil.midnight(form.getEndDate()));

			if (!dataFound) {
				// no report found
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.reports.no.records.found.for.criteria");
			}
		}

		// send to report output
		return getView();
	}

	/**
	 * Generates the report and stores it into the model as required by the view
	 * 
	 * @param model
	 *            The model to contain the attributes needed by the view
	 * @param startDate
	 *            The start date range of the report
	 * @param endDate
	 *            The end date range of the report
	 * @return true if data for the report was found, false if no report data available
	 */
	protected abstract boolean generateReport(ModelMap model, Date startDate, Date endDate);

	/**
	 * The report view containing the report (if data has been generated) or the report parameters.
	 * 
	 * @return the view
	 */
	protected abstract String getView();
}