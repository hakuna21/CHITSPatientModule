package org.openmrs.module.chits.web.controller.fpprogram;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.StateUtil;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FamilyPlanningProgramStates;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Un-enrolls the patient from the family planning program information by setting the date of completion in the currently active patient program.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/unenrollFromFamilyPlanningProgram.form")
public class UnenrollFromFamilyPlanningProgramController extends BaseUpdateFamilyPlanningPatientConsultDataController {
	/** Auto-wired program workflow service */
	private ProgramWorkflowService programWorkflowService;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public FamilyPlanningConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId) throws ServletException {
		// do pre-initialization via superclass
		final FamilyPlanningConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// add parents' information
		final Patient patient = form.getPatient();
		if (patient != null) {
			// initialize the main Family Planning Program Observation
			final FamilyPlanningProgramObs fpProgramObs = new FamilyPlanningProgramObs(patient);
			form.setFpProgramObs(fpProgramObs);

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form, //
			BindingResult errors) {
		// get current active patient program
		final PatientProgram patientProgram = form.getFpProgramObs() != null ? form.getFpProgramObs().getPatientProgram() : null;

		if (patientProgram != null && patientProgram.getDateCompleted() == null) {
			// add 'ENDED' state
			final Date today = new Date();
			StateUtil.addState(form.getPatient(), ProgramConcepts.FAMILYPLANNING, FamilyPlanningProgramStates.CLOSED, null, today);

			// set 'date completed' to mark the program as 'concluded'
			patientProgram.setDateCompleted(today);

			// save the patient program to complete the un-enrollment
			programWorkflowService.savePatientProgram(patientProgram);

			// add a success message
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.program.FAMILYPLANNING.program.unenrolled");
		} else {
			// patient not enrolled in family planning?
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.FAMILYPLANNING.not.enrolled");
		}

		// redirect to the 'view patient consults' controller
		return getReloadPath(request, form.getPatient() != null ? form.getPatient().getPatientId() : null);
	}

	/**
	 * The version object is the family program observation
	 */
	@Override
	protected Auditable getVersionObject(FamilyPlanningConsultEntryForm form) {
		// use the family program observation so that any changes will trigger a concurrency update error
		return form.getFpProgramObs().getObs();
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// send back to view patient consults
		return viewPatientConsultsController.redirect(Integer.valueOf(request.getParameter("patientId")));
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// send back to view patient consults
		return viewPatientConsultsController.redirect(patientId);
	}

	@Autowired
	public void setProgramWorkflowService(ProgramWorkflowService programWorkflowService) {
		this.programWorkflowService = programWorkflowService;
	}
}
