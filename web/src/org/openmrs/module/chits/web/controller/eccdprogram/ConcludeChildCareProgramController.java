package org.openmrs.module.chits.web.controller.eccdprogram;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.StateUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareProgramStates;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.propertyeditor.PatientEditor;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Concludes the Child Care program.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/concludeChildCareProgram.form")
public class ConcludeChildCareProgramController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wired service */
	protected ProgramWorkflowService programWorkflowService;

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(org.openmrs.Patient.class, new PatientEditor());
	}

	/**
	 * Concludes the childcare program.
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			@RequestParam(required = true, value = "patient") Patient patient) {
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to view the consults of
			return "redirect:/module/chits/patients/findPatient.htm";
		}

		// get registration state of patient
		final PatientState registeredState = Functions.getPatientState(patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.REGISTERED);
		if (registeredState == null) {
			// patient not registered to child care program!
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.CHILDCARE.not.registered");

			// redirect to the view patient consults page
			return "redirect:/module/chits/consults/viewPatient.form?patientId=" + patient.getPatientId();
		} else if (Functions.getPatientState(patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.CLOSED) != null) {
			// child care program already concluded for this patient!
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.CHILDCARE.already.concluded");

			// redirect to the view patient consults page
			return "redirect:/module/chits/consults/viewPatient.form?patientId=" + patient.getPatientId();
		} else if (patient.getAge() == null || patient.getAge() < 6) {
			// child not yet of age, cannot conclude program prematurely!
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.CHILDCARE.not.yet.of.age");

			// redirect to the view child care consults page
			return "redirect:/module/chits/consults/viewChildCareProgram.form?patientId=" + patient.getPatientId();
		} else {
			// conclude the childcare program for this patient
			final Date now = new Date();

			// update the 'registered' state of the patient to include the end date of 'now'
			programWorkflowService.savePatientProgram(StateUtil.addState(patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.REGISTERED,
					registeredState.getStartDate(), now));

			// add the 'closed' state for this patient and set the date completed value
			final PatientProgram pp = StateUtil.addState(patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.CLOSED, now, now);
			pp.setDateCompleted(now);
			programWorkflowService.savePatientProgram(pp);

			// success
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.program.CHILDCARE.concluded");

			// redirect to the view childcare program
			return "redirect:/module/chits/consults/viewChildCareProgram.form?patientId=" + patient.getPatientId();
		}
	}

	@Autowired
	public void setProgramWorkflowService(ProgramWorkflowService programWorkflowService) {
		this.programWorkflowService = programWorkflowService;
	}
}
