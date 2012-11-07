package org.openmrs.module.chits.web.controller.fpprogram;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Obs;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningUtil;
import org.openmrs.module.chits.web.controller.BaseUpdatePatientConsultDataController;
import org.openmrs.web.WebConstants;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Decorator around the {@link BaseUpdatePatientConsultDataController} that intercepts POST submissions and sends back to the input page with an error if the
 * patient's family planning program has already been concluded.
 * <p>
 * Additionally, this method converts the superclass&apos; {@link PatientConsultEntryForm} form backing object to a {@link FamilyPlanningConsultEntryForm} type
 * after generifying "T" to {@link FamilyPlanningConsultEntryForm}.
 * 
 * @author Bren
 */
public abstract class BaseUpdateFamilyPlanningPatientConsultDataController extends BaseUpdatePatientConsultDataController<FamilyPlanningConsultEntryForm> {
	/**
	 * Convert the superclass&apos; {@link PatientConsultEntryForm} to a {@link FamilyPlanningConsultEntryForm} type.
	 */
	@Override
	public FamilyPlanningConsultEntryForm formBackingObject(HttpServletRequest request, ModelMap model, Integer patientId) throws ServletException {
		final PatientConsultEntryForm pForm = super.formBackingObject(request, model, patientId);

		// convert to a family planning consult entry form
		final FamilyPlanningConsultEntryForm form = new FamilyPlanningConsultEntryForm();
		try {
			PropertyUtils.copyProperties(form, pForm);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}

		// return the encapsulated form
		return form;
	}

	/**
	 * Performs family planning pre-requisite checks before forwarding to the update form.
	 * 
	 * @param request
	 * @param httpSession
	 * @param model
	 * @param form
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form) {
		// verify that the patient meets the family planning prerequisites before showing the form
		if (!FamilyPlanningUtil.familyPlanningPrerequisitesMet(form)) {
			// family planning prerequisites have not been met: redirect using patient id
			final Integer patientId = form.getPatient() != null ? form.getPatient().getPatientId() : null;

			// send back to the view consults page
			return viewPatientConsultsController.redirect(patientId);
		} else {
			// ok to proceed: dispatch to superclass
			return super.showForm(request, httpSession, model, form);
		}
	}

	/**
	 * Intercepts the submission and sends back to input page with an error if the patient's family planning program has already been concluded.
	 */
	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form, //
			BindingResult errors) {
		if (FamilyPlanningUtil.isProgramConcludedFor(form.getPatient())) {
			// family planning program already concluded, updates are not allowed!
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.FAMILYPLANNING.program.closed");

			// send back to the input page
			return getInputPath(request);
		} else if (!FamilyPlanningUtil.familyPlanningPrerequisitesMet(form)) {
			// family planning prerequisites have not been met: cannot post updates!
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.consult.submission.data.errors");

			// family planning prerequisites have not been met: redirect using patient id
			final Integer patientId = form.getPatient() != null ? form.getPatient().getPatientId() : null;

			// send back to the view consults page
			return viewPatientConsultsController.redirect(patientId);
		} else {
			// ok to proceed: dispatch to superclass
			return super.handleSubmission(httpSession, request, model, form, errors);
		}
	}

	/**
	 * Invokes setUpdated() on- and adds- the given observations to the form's encounter bean.
	 * 
	 * @param form
	 *            The form containing the encounter
	 * @param observations
	 *            The observations to invoke setUpdated() on and add to the encounter.
	 */
	protected void setUpdatedAndAddToEncounter(FamilyPlanningConsultEntryForm form, Obs... observations) {
		for (Obs obs : observations) {
			form.getEncounter().addObs(obs);
			setUpdated(obs);
		}
	}
}
