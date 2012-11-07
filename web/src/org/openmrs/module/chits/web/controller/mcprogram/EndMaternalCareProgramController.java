package org.openmrs.module.chits.web.controller.mcprogram;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPatientConsultStatus;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCReasonForEndingMCProgram;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MaternalCareProgramStates;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Opens up the "End Maternal Care Program" form and allows the user to conclude the program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/endMaternalCareProgram.form")
public class EndMaternalCareProgramController extends ChangePatientConsultStatusController {
	/**
	 * Override to set patient consult status to 'ENDED'.
	 */
	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form, //
			BindingResult errors) {
		// set the consult status
		form.getPatientConsultStatus().setStatus(MaternalCareProgramStates.ENDED);

		// perform standard submission
		return super.handleSubmission(httpSession, request, model, form, errors);
	}

	/**
	 * Perform additional validation.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, MaternalCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// perform superclass validation on fields
		super.postProcess(request, form, map, enc, errors);

		// additionally, ensure the user has specified the 'reason for ending' the program
		PatientConsultEntryFormValidator.validateRequiredFields(form, "patientConsultStatus.observationMap", errors, //
				MCPatientConsultStatus.REASON_FOR_ENDING);

		// if the 'OTHERS' is specified for 'reason for ending', then the 'remarks' are required
		if (form.getPatientConsultStatus().getMember(MCPatientConsultStatus.REASON_FOR_ENDING).getValueCoded() == Functions
				.concept(MCReasonForEndingMCProgram.OTHER)) {
			// the 'remarks' field is required if 'OTHER' is selected
			PatientConsultEntryFormValidator.validateRequiredFields(form, "patientConsultStatus.observationMap", errors, //
					MCPatientConsultStatus.REMARKS);
		}
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/maternalcare/ajaxEndMaternalCareProgram";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		if (MaternalCareUtil.isProgramClosedFor(Context.getPatientService().getPatient(patientId))) {
			// program already closed, send ajax page to redirect to view consult page
			return viewPatientConsultsController.ajaxRedirect(patientId);
		} else {
			// send back to the consult status page
			return "/module/chits/consults/maternalcare/ajaxEndMaternalCareProgram";
		}
	}
}
