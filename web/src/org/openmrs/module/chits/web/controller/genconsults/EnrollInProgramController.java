package org.openmrs.module.chits.web.controller.genconsults;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/module/chits/consults/enrollInProgram.form")
public class EnrollInProgramController {
	/** Auto-wired patient service */
	protected PatientService patientService;

	/** Auto-wired program workflow service */
	protected ProgramWorkflowService programWorkflowService;

	/** Auto-wired ViewPatientConsultsController */
	protected ViewPatientConsultsController viewPatientConsultsController;

	/**
	 * Enrolls the patient in the given program. Synchronized for double-protection against being enrolled in the program twice.
	 * 
	 * @param patientId
	 *            The patient to enroll the program to
	 * @param programId
	 *            The program to enroll the patient in
	 * @return Redirects browser back to the view patient consult form
	 */
	@RequestMapping(method = RequestMethod.POST)
	public synchronized String handleSubmission(HttpSession httpSession, //
			@RequestParam(required = true, value = "patientId") Integer patientId, //
			@RequestParam(required = true, value = "program") ProgramConcepts programConcept) {
		final Patient patient = patientService.getPatient(patientId);
		final Program program = programWorkflowService.getProgram(programConcept.getProgramId());
		if (patient == null || program == null) {
			// patient or program not found!
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.programs.patient.or.program.not.found");
		} else {
			if (Functions.isInProgram(patient, programConcept)) {
				// patient already active in program!
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.programs.already.in.program");
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, new Object[] { program.getConcept().getName().getName() });
			} else {
				// enroll into the program
				enrollPatientInProgram(programWorkflowService, patient, program);

				// success!
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.programs.patient.enrolled.in.program");
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { program.getConcept().getName().getName() });
			}
		}

		// send back to the view consults page
		return viewPatientConsultsController.redirect(patientId);
	}

	/**
	 * Enrolls the patient into the program. This method assumes the patient is not already enrolled into the program!
	 * 
	 * @param patient
	 *            The patient to enroll to the program
	 * @param program
	 *            The program to enroll the patient into
	 */
	public static void enrollPatientInProgram(ProgramWorkflowService programWorkflowService, Patient patient, Program program) {
		final Date now = new Date();

		// initialize patient program member
		final PatientProgram enrollment = new PatientProgram();
		enrollment.setPatient(patient);
		enrollment.setProgram(program);
		enrollment.setDateEnrolled(now);
		enrollment.setVoided(Boolean.FALSE);

		// set standard attributes
		enrollment.setCreator(Context.getAuthenticatedUser());
		enrollment.setDateCreated(now);
		enrollment.setUuid(UUID.randomUUID().toString());

		// save the program
		programWorkflowService.savePatientProgram(enrollment);
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Autowired
	public void setProgramWorkflowService(ProgramWorkflowService programWorkflowService) {
		this.programWorkflowService = programWorkflowService;
	}

	@Autowired
	public void setViewPatientConsultsController(ViewPatientConsultsController viewPatientConsultsController) {
		this.viewPatientConsultsController = viewPatientConsultsController;
	}
}
