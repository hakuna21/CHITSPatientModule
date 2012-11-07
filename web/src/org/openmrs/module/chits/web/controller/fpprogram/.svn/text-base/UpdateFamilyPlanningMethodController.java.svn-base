package org.openmrs.module.chits.web.controller.fpprogram;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyPlanningMethodConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningMethod;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.obs.GroupObs.FieldPath;
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
 * Updates the family planing method of the currently active family planning program.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateFamilyPlanningMethod.form")
public class UpdateFamilyPlanningMethodController extends BaseUpdateFamilyPlanningPatientConsultDataController {
	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public FamilyPlanningConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// do pre-initialization via superclass
		final FamilyPlanningConsultEntryForm form = super.formBackingObject(request, model, patientId);
		request.setAttribute("form", form);

		// add parents' information
		final Patient patient = form.getPatient();
		if (patient != null) {
			// initialize the main Family Planning Program Observation
			final FamilyPlanningProgramObs fpProgramObs = new FamilyPlanningProgramObs(patient);
			form.setFpProgramObs(fpProgramObs);

			final FamilyPlanningMethod currentFPM = form.getFpProgramObs().getLatestFamilyPlanningMethod();
			if (currentFPM != null && !currentFPM.isDroppedOut()) {
				// if current method is not yet dropped out, use it for updates
				form.setFamilyPlanningMethod(currentFPM);
			} else {
				// otherwise, prepare a new blank family planning method for submission
				form.setFamilyPlanningMethod(new FamilyPlanningMethod());
			}

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	/**
	 * Special screen displayed for 'LU' client types: user needs to choose between changing the client type to acceptor or discontinuing the method.
	 */
	@RequestMapping(method = RequestMethod.POST, params = { "updateMethod" })
	public String handleSubmission(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "updateMethod") String updateMethod, //
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form, //
			BindingResult errors) {
		final FamilyPlanningMethod currentFPM = form.getFamilyPlanningMethod();
		if (currentFPM != null && !currentFPM.isDroppedOut() && "discontinue".equalsIgnoreCase(updateMethod)) {
			// set default 'date of discontinuation' to today's date
			final Obs dropoutDateObs = currentFPM.getMember(FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT);
			dropoutDateObs.setValueText(Context.getDateFormat().format(new Date()));

			// "discontinue the method" selected, forward to "FP Method Dropout Form"
			return "/module/chits/consults/familyplanning/ajaxFPMethodDropoutForm";
		} else {
			// "Change client type to acceptor" selected, forward to "FP Method Enrollment Form"
			return EnrollInNewFamilyPlanningMethodController.redirect(form.getPatient());
		}
	}

	/**
	 * Drops out of the current method and exits the dialog.
	 */
	@RequestMapping(method = RequestMethod.POST, params = { "dropout", "enrollInNew" })
	public String dropoutAndExit(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "enrollInNew") Boolean enrollInNew, //
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form, //
			BindingResult errors) {
		final FamilyPlanningMethod currentFPM = form.getFamilyPlanningMethod();
		if (currentFPM != null && !currentFPM.isDroppedOut() && !ObsUtil.isNewObs(currentFPM.getObs())) {
			// validate the values
			final FieldPath path = currentFPM.path("familyPlanningMethod");

			// perform standard validation
			PatientConsultEntryFormValidator.validateObservationMap(form, path, errors);

			// store coded 'reason' into the text field
			final Obs reasonObs = currentFPM.getMember(FPFamilyPlanningMethodConcepts.DROPOUT_REASON);
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(reasonObs);

			// validate required fields
			PatientConsultEntryFormValidator.validateRequiredFields(form, path, errors, //
					FPFamilyPlanningMethodConcepts.DROPOUT_REASON, //
					FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT);

			if (errors.hasErrors()) {
				// display a general error message
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.consult.submission.data.errors");
			} else {
				// add to encounter and set updated for cascade saving
				setUpdatedAndAddToEncounter(form, //
						form.getFpProgramObs().getObs(), //
						form.getFamilyPlanningMethod().getObs());

				// add audit information
				currentFPM.storePersonAndAudit(form.getPatient());

				// save the encounter
				encounterService.saveEncounter(form.getEncounter());

				if (!enrollInNew) {
					// display general success message to trigger the closing of the ajax dialog
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.program.FAMILYPLANNING.method.dropped.out");
				} else {
					// forward to the enrollment form to enroll a new method
					return EnrollInNewFamilyPlanningMethodController.redirect(form.getPatient());
				}
			}
		} else {
			// display a general error message
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.consult.submission.data.errors");
		}

		// send back to the dropout form so that the message gets rendered and the dialog gets closed (if there were no errors)
		return "/module/chits/consults/familyplanning/ajaxFPMethodDropoutForm";
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
		// extract the 'form'
		final FamilyPlanningConsultEntryForm form = (FamilyPlanningConsultEntryForm) request.getAttribute("form");

		// obtain current family planning method
		final FamilyPlanningMethod fpm = form.getFpProgramObs().getLatestFamilyPlanningMethod();

		if (fpm == null) {
			// not currently enrolled? Display the FP Method enrollment Form
			return EnrollInNewFamilyPlanningMethodController.redirect(form.getPatient());
		} else if (fpm.isLearningUser()) {
			// learning users: display the update form before allowing to enroll in another method
			return "/module/chits/consults/familyplanning/ajaxFPMethodUpdateForm";
		} else if (fpm.isDroppedOut()) {
			// Dropped out already and any client type except LU: Display the FP Method Enrollment Form
			return EnrollInNewFamilyPlanningMethodController.redirect(form.getPatient());
		} else {
			// set default 'date of discontinuation' to today's date
			final Obs dropoutDateObs = fpm.getMember(FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT);
			dropoutDateObs.setValueText(Context.getDateFormat().format(new Date()));

			// Using a current family planning method (and non-LU client type): display the FP Method Dropout Form first
			return "/module/chits/consults/familyplanning/ajaxFPMethodDropoutForm";
		}
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		return getInputPath(request);
	}
}
