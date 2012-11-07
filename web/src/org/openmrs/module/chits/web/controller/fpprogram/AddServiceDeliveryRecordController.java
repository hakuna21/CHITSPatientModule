package org.openmrs.module.chits.web.controller.fpprogram;

import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFemaleMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPServiceDeliveryRecordConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningMethod;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.fpprogram.ServiceDeliveryRecord;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.obs.GroupObs.FieldPath;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Adds a service delivery record to the patient's current active family planning program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/addServiceDeliveryRecord.form")
public class AddServiceDeliveryRecordController extends BaseUpdateFamilyPlanningPatientConsultDataController {
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

		if (form.getPatient() != null) {
			// initialize the main Family Planning Program Observation
			final FamilyPlanningProgramObs fpProgramObs = new FamilyPlanningProgramObs(form.getPatient());
			form.setFpProgramObs(fpProgramObs);

			// initialize a blank ServiceDeliveryRecord instance for entering the data
			form.setServiceDeliveryRecord(new ServiceDeliveryRecord());

			// obtain the latest family planning method
			form.setFamilyPlanningMethod(form.getFpProgramObs().getLatestFamilyPlanningMethod());

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	/**
	 * Overridden to set default options.
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form) {
		// set default values only for new records
		if (ObsUtil.isNewObs(form.getServiceDeliveryRecord().getObs())) {
			// set the 'date administered' value
			form.getServiceDeliveryRecord().getMember(FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED)
					.setValueText(Context.getDateFormat().format(new Date()));
		}

		// dispatch to regular superclass
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * Perform other non-standard validation on form fields.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, FamilyPlanningConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// build field path
		final ServiceDeliveryRecord svcRecordObs = form.getServiceDeliveryRecord();
		final FieldPath path = svcRecordObs.path("serviceDeliveryRecord");
		final FamilyPlanningMethod fpm = form.getFamilyPlanningMethod();
		if (fpm == null) {
			// if no family planning method was loaded from the form backing object, this indicates that the patient isn't enrolled yet
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.FAMILYPLANNING.not.enrolled");
			errors.reject("chits.program.FAMILYPLANNING.not.enrolled");
		} else if (fpm.isDroppedOut()) {
			// if the patient has already dropped out from this method, then we shouldn't be adding service records to it
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.FAMILYPLANNING.method.already.dropped.out");
			errors.reject("chits.program.FAMILYPLANNING.method.already.dropped.out");
		} else {
			// perform standard validation
			PatientConsultEntryFormValidator.validateObservationMap(form, path, errors);

			// check that all required fields have been specified
			PatientConsultEntryFormValidator.validateRequiredFields(form, path, errors, FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED);

			// for non-permanent methods, supply and quantity fields should be required
			if (!fpm.isPermanentMethod()) {
				if (Functions.concept(FPFemaleMethodOptions.INJ).equals(fpm.getObs().getValueCoded())) {
					// require the supply value (quantity not required) for injectables method
					PatientConsultEntryFormValidator.validateRequiredFields(form, path, errors, //
							FPServiceDeliveryRecordConcepts.SUPPLY_SOURCE);
				} else {
					// require the supply and quantity values for non-permanent methods
					PatientConsultEntryFormValidator.validateRequiredFields(form, path, errors, //
							FPServiceDeliveryRecordConcepts.SUPPLY_SOURCE, //
							FPServiceDeliveryRecordConcepts.SUPPLY_QUANTITY);
				}
			}

			// validate date value must be after the patient's birth date and not in future
			PatientConsultEntryFormValidator.validateValidPastDates(form, path, errors, FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED);

			// // Service date cannot be earlier than the enrollment date of active method, and no later than the current date
			final Date serviceDate = svcRecordObs.getDateAdministered();
			// final Date enrollmentDate = fpm.getEnrollmentDate();
			// if (serviceDate != null && enrollmentDate != null && DateUtil.stripTime(serviceDate).before(DateUtil.stripTime(enrollmentDate))) {
			// errors.rejectValue(path.to(FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED).valueText(),
			// "chits.program.FAMILYPLANNING.error.service.date.before.enrollment");
			// }

			// maternal care checks:
			if (MaternalCareUtil.isCurrentlyEnrolledAndBabyNotYetDelivered(form.getPatient())) {
				final PatientProgram mcPatientProgram = Functions.getActivePatientProgram(form.getPatient(), ProgramConcepts.MATERNALCARE);
				if (serviceDate != null && mcPatientProgram.getDateEnrolled() != null //
						&& !DateUtil.stripTime(serviceDate).before(DateUtil.stripTime(mcPatientProgram.getDateEnrolled()))) {
					errors.rejectValue(path.to(FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED).valueText(),
							"chits.program.FAMILYPLANNING.date.must.be.before.maternal.care.enrollment");
				}
			}

			// date of enrollment must be before patient's 50th birthday (for female patients)
			if ("F".equalsIgnoreCase(form.getPatient().getGender()) && serviceDate != null) {
				if (DateUtil.yearsBetween(form.getPatient().getBirthdate(), serviceDate) >= 50) {
					errors.rejectValue(path.to(FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED).valueText(),
							"chits.program.FAMILYPLANNING.date.must.be.before.female.patients.50th.birthdate");
				}
			}
		}
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, FamilyPlanningConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// add the service delivery record to the family planning method for cascade saving
		form.getFamilyPlanningMethod().addChild(form.getServiceDeliveryRecord());

		// add the family planning method record to the encounter for processing and cascade saving of the service delivery record
		setUpdatedAndAddToEncounter(form, form.getFamilyPlanningMethod().getObs());

		// NOTE: Just add directly to the FP program observation as a group member for directly storing
		// all family planning records: This step is really not necessary since the family planning method
		// should already be a member, but let's do it anyway for good measure...
		form.getFpProgramObs().getObs().addGroupMember(form.getFamilyPlanningMethod().getObs());

		// ensure all observations refer to the correct patient
		form.getServiceDeliveryRecord().storePersonAndAudit(form.getPatient());
	}

	/**
	 * The version object is the current service delivery record entry being edited / created.
	 */
	@Override
	protected Auditable getVersionObject(FamilyPlanningConsultEntryForm form) {
		// any changes to the family planning method indicates a concurrent update (since service delivery
		// records are added to this)
		final FamilyPlanningMethod fpm = form.getFamilyPlanningMethod();

		// NOTE: Perform special checking in case FPM was null for any reason
		return fpm != null ? fpm.getObs() : form.getEncounter();
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/familyplanning/ajaxAddEditServiceDeliveryRecord";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// after being saved, forward to the SetDateOfNextService controller
		return "redirect:setFamilyPlanningDateOfNextService.form?patientId=" + patientId;
	}
}
