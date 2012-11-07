package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Edits the birth plan chart in the patient's current active maternal care program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/editBirthPlanChart.form")
public class EditBirthPlanChartController extends BaseUpdateMaternalCarePatientConsultDataController {
	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// do pre-initialization via superclass
		final MaternalCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		if (form.getPatient() != null) {
			// initialize the main Maternal Care Program Observation
			final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(form.getPatient());
			form.setMcProgramObs(mcProgramObs);

			// this is the maternal care program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		}

		// return the patient
		return form;
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form, //
			BindingResult errors) {
		// add an error message if the birth plan chart is already read-only
		if (form.getMcProgramObs().isBirthPlanChartReadOnly()) {
			final String errorKey = "chits.program.MATERNALCARE.birth.plan.chart.read.only";
			errors.reject(errorKey);
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, errorKey);
		}

		// dispatch to superclass
		return super.handleSubmission(httpSession, request, model, form, errors);
	}

	/**
	 * Perform other non-standard validation on form fields.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, MaternalCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "mcProgramObs.birthPlanChart.observationMap", errors);
		PatientConsultEntryFormValidator.validateObservationMap(form, "mcProgramObs.birthPlanChart.mothersNeeds.observationMap", errors);
		PatientConsultEntryFormValidator.validateObservationMap(form, "mcProgramObs.birthPlanChart.childsNeeds.observationMap", errors);
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// add the birth plan chart to the encounter for processing
		setUpdatedAndAddToEncounter(form, form.getMcProgramObs().getBirthPlanChart().getObs());

		// ensure all observations refer to the correct patient
		form.getMcProgramObs().getBirthPlanChart().storePersonAndAudit(form.getPatient());
	}

	/**
	 * The version object is the current birth plan chart entry being edited / created.
	 */
	@Override
	protected Auditable getVersionObject(MaternalCareConsultEntryForm form) {
		// any changes to the "birth plan chart" entry being edited indicates a concurrent update
		return form.getMcProgramObs().getBirthPlanChart().getObs();
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/maternalcare/ajaxEditBirthPlanChart";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// no need to redirect, the rendering page should close the dialog
		return "/module/chits/consults/maternalcare/ajaxEditBirthPlanChart";
	}
}
