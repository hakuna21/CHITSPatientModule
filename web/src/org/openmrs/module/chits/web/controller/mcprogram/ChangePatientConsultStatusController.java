package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.StateUtil;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPatientConsultStatus;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MaternalCareProgramStates;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.mcprogram.PatientConsultStatus;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Changes the patient consult status for the patient's current active maternal care program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/changePatientConsultStatus.form")
public class ChangePatientConsultStatusController extends BaseUpdateMaternalCarePatientConsultDataController {
	/** Auto-wired patient program workflow service */
	protected ProgramWorkflowService programWorkflowService;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// initialize the form
		final MaternalCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		if (form.getPatient() != null) {
			// initialize the main Maternal Care Program Observation
			final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(form.getPatient());
			form.setMcProgramObs(mcProgramObs);

			// prepare a blank patient consult status with the current date
			final PatientConsultStatus patientConsultStatus = new PatientConsultStatus();
			form.setPatientConsultStatus(patientConsultStatus);
			patientConsultStatus.getMember(MCPatientConsultStatus.DATE_OF_CHANGE).setValueText(Context.getDateFormat().format(new Date()));

			// this is the maternal care program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		} else {
			// invalid view request
			throw new APIAuthenticationException("chits.program.MATERNALCARE.not.enrolled");
		}

		// return the patient
		return form;
	}

	/**
	 * Perform validation
	 */
	@Override
	protected void postProcess(HttpServletRequest request, MaternalCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "patientConsultStatus.observationMap", errors);

		// validate required fields
		PatientConsultEntryFormValidator.validateRequiredFields(form, "patientConsultStatus.observationMap", errors, //
				MCPatientConsultStatus.DATE_OF_CHANGE);

		// validate date value must be after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateValidPastDates(form, "patientConsultStatus.observationMap", errors, //
				MCPatientConsultStatus.DATE_OF_CHANGE);

		// NOTE: we use an 'enum' for the status in the form
		if (form.getPatientConsultStatus().getStatus() == null) {
			errors.rejectValue("patientConsultStatus.status", "chits.error.required.field");
		}
	}

	@Override
	protected void beforeSave(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// use current timestamp for marking dates
		final Date now = new Date();

		// add the consult status record to the encounter for processing
		setUpdatedAndAddToEncounter(form, form.getPatientConsultStatus().getObs());

		// NOTE: Just add directly to the mc program observation as a group member for directly storing
		// all consult status records (i.e., no need for an "overall" consult status record like the
		// overall "obstetric history" record which contains "obstetric history detail" members"
		form.getMcProgramObs().getObs().addGroupMember(form.getPatientConsultStatus().getObs());

		// ensure all observations refer to the correct patient
		form.getPatientConsultStatus().storePersonAndAudit(form.getPatient());

		// simultaneously add the state to the patient program
		StateUtil.addState(form.getPatient(), ProgramConcepts.MATERNALCARE, //
				form.getPatientConsultStatus().getStatus(), now, null);

		// update the active program timestamp to save the new state (and prevent concurrent updates)
		final PatientProgram patientProgram = Functions.getActivePatientProgram(form.getPatient(), ProgramConcepts.MATERNALCARE);
		setUpdated(patientProgram);

		if (MaternalCareProgramStates.ENDED == form.getPatientConsultStatus().getStatus()) {
			// set the 'datecompleted' field of the patient program
			patientProgram.setDateCompleted(now);
			
			// use a more appropriate message for ended programs
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.program.MATERNALCARE.program.ended");
		}

		// update the patient program
		programWorkflowService.savePatientProgram(patientProgram);
	}

	/**
	 * The version object is the current active patient program when submitting registration information.
	 */
	@Override
	protected Auditable getVersionObject(MaternalCareConsultEntryForm form) {
		// the version object is the latest administered service in this encounter
		return Functions.getActivePatientProgram(form.getPatient(), ProgramConcepts.MATERNALCARE);
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/maternalcare/ajaxChangePatientConsultStatus";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		if (MaternalCareUtil.isProgramClosedFor(Context.getPatientService().getPatient(patientId))) {
			// program already closed, send ajax page to redirect to view consult page
			return viewPatientConsultsController.ajaxRedirect(patientId);
		} else {
			// send back to the consult status page
			return "/module/chits/consults/maternalcare/ajaxChangePatientConsultStatus";
		}
	}

	@Autowired
	public void setProgramWorkflowService(ProgramWorkflowService programWorkflowService) {
		this.programWorkflowService = programWorkflowService;
	}
}
