package org.openmrs.module.chits.web.controller.genconsults;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.web.controller.BaseUpdatePatientConsultDataController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Anthropometric data form controller for a patient in the queue
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateAnthropometricData.form")
public class UpdateAnthropometricDataController extends BaseUpdatePatientConsultDataController<PatientConsultEntryForm> implements Constants {
	/** These are the concept names that can be edited by this form */
	protected Collection<CachedConceptId> EDITABLE_CONCEPTS = Arrays.asList( //
			new CachedConceptId[] { VisitConcepts.HEIGHT_CM, //
					VisitConcepts.WEIGHT_KG, //
					VisitConcepts.WAIST_CIRC_CM, //
					VisitConcepts.HIP_CIRC_CM, //
					VisitConcepts.HEAD_CIRC_CM, //
					VisitConcepts.CHEST_CIRC_CM //
			});

	@Override
	@ModelAttribute("form")
	public PatientConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId) throws ServletException {
		// dispatch initialization of form to superclass
		final PatientConsultEntryForm form = (PatientConsultEntryForm) super.formBackingObject(request, model, patientId);

		// get the encounter instance
		final Encounter enc = form.getPatientQueue() != null ? form.getPatientQueue().getEncounter() : null;

		// load the encounter observations that we need to edit into the form
		setupFormObservations(form, enc, EDITABLE_CONCEPTS);

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

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// send to an ajax input page
		return "/module/chits/consults/ajaxAnthropometricEditForm";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/updateAnthropometricData.form?patientId=" + patientId;
	}
}
