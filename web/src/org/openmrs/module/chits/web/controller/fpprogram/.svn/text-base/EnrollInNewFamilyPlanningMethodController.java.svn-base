package org.openmrs.module.chits.web.controller.fpprogram;

import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Auditable;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.StateUtil;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPClientTypeConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyPlanningMethodConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFemaleMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPelvicExaminationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FamilyPlanningProgramStates;
import org.openmrs.module.chits.fpprogram.FamilyPlanningMethod;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.obs.GroupObs.FieldPath;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Updates the family information of the currently active family planning program (this will automatically cause the previous family information entry to become
 * archived).
 */
@Controller
@RequestMapping(value = "/module/chits/consults/enrollInNewFamilyPlanningMethod.form")
public class EnrollInNewFamilyPlanningMethodController extends BaseUpdateFamilyPlanningPatientConsultDataController {
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

		// add parents' information
		final Patient patient = form.getPatient();
		if (patient != null) {
			// initialize the main Family Planning Program Observation
			final FamilyPlanningProgramObs fpProgramObs = new FamilyPlanningProgramObs(patient);
			form.setFpProgramObs(fpProgramObs);

			// initialize the family information models
			initFormBackingObject(form);

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	/**
	 * Initializes the form with a fresh family information bean copying any previous family information data.
	 * 
	 * @param form
	 */
	protected void initFormBackingObject(FamilyPlanningConsultEntryForm form) {
		// store a blank family planning method bean into the form for submission
		final FamilyPlanningMethod fpm = new FamilyPlanningMethod();
		form.setFamilyPlanningMethod(fpm);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form) {
		final FamilyPlanningMethod fpMethod = form.getFamilyPlanningMethod();
		if (ObsUtil.isNewObs(fpMethod.getObs())) {
			// set family planning method date of enrollment to current date
			fpMethod.getMember(FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT) //
					.setValueText(Context.getDateFormat().format(new Date()));
		}

		// dispatch to superclass showForm
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * Performs validation
	 */
	@Override
	protected void postProcess(HttpServletRequest request, FamilyPlanningConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// perform standard validation
		final FamilyPlanningMethod fpMethodObs = form.getFamilyPlanningMethod();
		final FieldPath path = fpMethodObs.path("familyPlanningMethod");
		PatientConsultEntryFormValidator.validateObservationMap(form, path, errors);

		// validate required fields
		PatientConsultEntryFormValidator.validateRequiredFields(form, path, errors, //
				FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT, //
				FPFamilyPlanningMethodConcepts.CLIENT_TYPE);

		// validate date values that must be after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateValidPastDates(form, path, errors, FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT);

		// apply business rules for 'NA' client types
		if (fpMethodObs.isNewAcceptor()) {
			// A patient cannot be a new acceptor (NA) if her/his status is "DROPOUT";
			final FamilyPlanningMethod current = form.getFpProgramObs().getLatestFamilyPlanningMethod();
			if (current != null && current.isDroppedOut()) {
				errors.rejectValue(path.to(FPFamilyPlanningMethodConcepts.CLIENT_TYPE).valueCoded(), "chits.program.FAMILYPLANNING.method.na.restrictions");
			}
		}

		// if a permanent method type is specified for the family planning method, then require the 'remarks' field to be filled in.
		final Concept fpMethodConcept = fpMethodObs.getObs().getValueCoded();

		// ensure method is specified
		if (fpMethodConcept == null) {
			// no family planning method specified
			errors.rejectValue(path.valueCoded(), "chits.error.required.field");
		} else {
			// remarks are required for permanent family planning methods
			if (Functions.members(FPMethodOptions.ARTIFICIAL_PERM).contains(fpMethodConcept)) {
				if (StringUtils.isEmpty(fpMethodObs.getMember(FPFamilyPlanningMethodConcepts.REMARKS).getValueText())) {
					errors.rejectValue(path.to(FPFamilyPlanningMethodConcepts.REMARKS).valueText(),
							"chits.program.FAMILYPLANNING.remarks.required.for.permanent.fp.methods");
				}
			}

			// females with uterine depth < 6cm or > 8cm may not select IUD
			if (Functions.concept(FPFemaleMethodOptions.IUD).equals(fpMethodConcept)) {
				final Obs uterineDepth = form.getFpProgramObs().getPelvicExamination().getMember(FPPelvicExaminationConcepts.UTERINE_DEPTH);
				if (uterineDepth != null && uterineDepth.getValueNumeric() != null) {
					final double uterineDepthVal = uterineDepth.getValueNumeric();
					if (uterineDepthVal < 6 || uterineDepthVal > 8) {
						errors.rejectValue(path.valueCoded(), "chits.program.FAMILYPLANNING.iud.not.applicable.for.uterine.depth");
					}
				}
			}

			// apply business rules for 'LU' client types
			if (fpMethodObs.isLearningUser()) {
				// LU may only be selected if the method is a natural family planning method other than LAM:
				if (Functions.concept(FPFemaleMethodOptions.NFP_LAM).equals(fpMethodConcept)
						|| !Functions.members(FPMethodOptions.NATURAL).contains(fpMethodConcept)) {
					errors.rejectValue(path.to(FPFamilyPlanningMethodConcepts.CLIENT_TYPE).valueCoded(), "chits.program.FAMILYPLANNING.method.lu.restrictions");
				}
			}
		}

		// maternal care checks:
		final Date dateOfEnrollment = fpMethodObs.getEnrollmentDate();
		if (MaternalCareUtil.isCurrentlyEnrolledAndBabyNotYetDelivered(form.getPatient())) {
			final PatientProgram mcPatientProgram = Functions.getActivePatientProgram(form.getPatient(), ProgramConcepts.MATERNALCARE);
			if (dateOfEnrollment != null && mcPatientProgram.getDateEnrolled() != null //
					&& !DateUtil.stripTime(dateOfEnrollment).before(DateUtil.stripTime(mcPatientProgram.getDateEnrolled()))) {
				errors.rejectValue(path.to(FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT).valueText(),
						"chits.program.FAMILYPLANNING.date.must.be.before.maternal.care.enrollment");
			}
		}

		// date of enrollment must be before patient's 50th birthday (for female patients)
		if ("F".equalsIgnoreCase(form.getPatient().getGender()) && dateOfEnrollment != null) {
			if (DateUtil.yearsBetween(form.getPatient().getBirthdate(), dateOfEnrollment) >= 50) {
				errors.rejectValue(path.to(FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT).valueText(),
						"chits.program.FAMILYPLANNING.date.must.be.before.female.patients.50th.birthdate");
			}
		}
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, FamilyPlanningConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		final FamilyPlanningMethod fpm = form.getFamilyPlanningMethod();
		if (ObsUtil.isNewObs(fpm.getObs()) && !fpm.isPermanentMethod()) {
			// for newly created, non-permanent family planning methods, set the default date of next service to the enrollment date
			final Obs dateOfNextServiceObs = fpm.getMember(FPFamilyPlanningMethodConcepts.DATE_OF_NEXT_SERVICE);
			final Obs dateOfEnrollmentObs = fpm.getMember(FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT);
			ObsUtil.copyValues(dateOfEnrollmentObs, dateOfNextServiceObs);
		}

		// NOTE: Just add directly to the FP program observation as a group member for directly storing
		// all family planning records
		form.getFpProgramObs().getObs().addGroupMember(form.getFamilyPlanningMethod().getObs());

		// add the family planning method and family planning observation to the encounter for processing
		setUpdatedAndAddToEncounter(form, //
				form.getFpProgramObs().getObs(), //
				form.getFamilyPlanningMethod().getObs());

		// ensure all observations refer to the correct patient and add audit information
		form.getFamilyPlanningMethod().storePersonAndAudit(form.getPatient());
	}

	@Override
	protected void beforeSave(HttpServletRequest request, FamilyPlanningConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		final Concept fpClientType = form.getFamilyPlanningMethod().getMember(FPFamilyPlanningMethodConcepts.CLIENT_TYPE).getValueCoded();
		if (Functions.concept(FPClientTypeConcepts.LU).equals(fpClientType)) {
			// If the Client Type = "LU" the program status is retained as "NEW";
			StateUtil.removeState(form.getPatient(), ProgramConcepts.FAMILYPLANNING, FamilyPlanningProgramStates.CLOSED);
			StateUtil.removeState(form.getPatient(), ProgramConcepts.FAMILYPLANNING, FamilyPlanningProgramStates.CURRENT);
			StateUtil.addState(form.getPatient(), ProgramConcepts.FAMILYPLANNING, FamilyPlanningProgramStates.NEW, new Date(), null);
		} else {
			// Otherwise, the patient’s program status will be set to "CURRENT"
			StateUtil.removeState(form.getPatient(), ProgramConcepts.FAMILYPLANNING, FamilyPlanningProgramStates.CLOSED);
			StateUtil.removeState(form.getPatient(), ProgramConcepts.FAMILYPLANNING, FamilyPlanningProgramStates.NEW);
			StateUtil.addState(form.getPatient(), ProgramConcepts.FAMILYPLANNING, FamilyPlanningProgramStates.CURRENT, new Date(), null);
		}
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
		return "/module/chits/consults/familyplanning/registration/fragmentPlanningMethod";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		return "/module/chits/consults/familyplanning/registration/fragmentPlanningMethod";
	}

	/**
	 * Redirect to this controller given the patient ID.
	 * 
	 * @param patientId
	 *            The patient ID to view
	 * @return A spring view sending redirecting the request to this controller
	 */
	public static String redirect(Patient patient) {
		return "redirect:/module/chits/consults/enrollInNewFamilyPlanningMethod.form?patientId="
				+ (patient != null && patient.getPatientId() != null ? patient.getPatientId() : 0);
	}
}
