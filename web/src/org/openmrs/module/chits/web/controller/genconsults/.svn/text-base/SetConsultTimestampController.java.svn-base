package org.openmrs.module.chits.web.controller.genconsults;

import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.web.controller.BaseUpdatePatientConsultDataController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Set consult timestamp form controller for a patient in the queue
 */
@Controller
@RequestMapping(value = "/module/chits/consults/setConsultTimestamp.form")
public class SetConsultTimestampController extends BaseUpdatePatientConsultDataController<PatientConsultEntryForm> implements Constants {
	@Override
	@ModelAttribute("form")
	public PatientConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId) throws ServletException {
		// dispatch initialization of form to superclass
		final PatientConsultEntryForm form = (PatientConsultEntryForm) super.formBackingObject(request, model, patientId);

		// set the default date / time based on the current encounter timestamp
		if (form.getPatientQueue() != null && form.getPatientQueue().getEncounter() != null) {
			final Date encTimestamp = form.getPatientQueue().getEncounter().getEncounterDatetime();
			form.setTimestampDate(Context.getDateFormat().format(encTimestamp));
			form.setTimestampTime(TIME_FMT.format(encTimestamp));
		}

		// the form is ready!
		return form;
	}

	/**
	 * The version object is the encounter itself.
	 */
	@Override
	protected Auditable getVersionObject(PatientConsultEntryForm form) {
		return form.getEncounter();
	}

	/**
	 * Update the encounter date timestamp.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, PatientConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// update the encounter datetime.
		enc.setEncounterDatetime(form.getTimestamp());
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// send to an ajax input page
		return "/module/chits/consults/ajaxSetConsultTimestampForm";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/setConsultTimestamp.form?patientId=" + patientId;
	}
}
