package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidDateAdministeredConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidDoseType;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.mcprogram.TetanusServiceRecord;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Processes adding of tetanus service records.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/addTetanusServiceRecord.form")
public class AddTetanusServiceRecordController extends BaseUpdateMaternalCarePatientConsultDataController {
	/** Auto-wire the UserService */
	protected UserService userService;

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
		final MaternalCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// always include currently logged in user even if not a healthworker!
		final Set<User> healthWorkers = new LinkedHashSet<User>();
		healthWorkers.add(Context.getAuthenticatedUser());
		model.put("healthWorkers", healthWorkers);

		// add all other health workers
		final Role healthWorkerRole = userService.getRole(Constants.HEALTHWORKER_ROLE);
		if (healthWorkerRole != null) {
			healthWorkers.addAll(userService.getUsersByRole(healthWorkerRole));
		}

		if (form.getPatient() != null) {
			// initialize the main Maternal Care Program Observation
			final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(form.getPatient());
			form.setMcProgramObs(mcProgramObs);

			// prep a teatnus toxoid service record for entering the data
			form.setTetanusServiceRecord(new TetanusServiceRecord());

			// this is the maternal care program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		}

		return form;
	}

	/**
	 * Override to set the issuance date the current date for Vitamin A and Deworming service types.
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form) {
		// setup default for new observations
		if (form.getTetanusServiceRecord().getObs().getObsId() == null || form.getTetanusServiceRecord().getObs().getObsId() == 0) {
			// set default visit date to today's date
			final Obs dateAdministeredObs = form.getTetanusServiceRecord().getMember(TetanusToxoidRecordConcepts.DATE_ADMINISTERED);
			dateAdministeredObs.setValueDatetime(new Date());
			dateAdministeredObs.setValueText(Context.getDateFormat().format(dateAdministeredObs.getValueDatetime()));

			// by default, the 'administeredBy' field should be the currently logged-in user
			form.getTetanusServiceRecord().setAdministeredBy(Context.getAuthenticatedUser());

			final Patient patient = form.getPatient();
			if (patient != null) {
				// set default tetanus toxoid dosage based on which ones have already been administered
				if (Functions.observation(patient, TetanusToxoidDateAdministeredConcepts.TT1) == null) {
					// assume patient needs TT1
					form.getTetanusServiceRecord().getObs().setValueCoded(Functions.concept(TetanusToxoidDoseType.TT1));
				} else if (Functions.observation(patient, TetanusToxoidDateAdministeredConcepts.TT2) == null) {
					// assume patient needs TT2
					form.getTetanusServiceRecord().getObs().setValueCoded(Functions.concept(TetanusToxoidDoseType.TT2));
				} else if (Functions.observation(patient, TetanusToxoidDateAdministeredConcepts.TT3) == null) {
					// assume patient needs TT3
					form.getTetanusServiceRecord().getObs().setValueCoded(Functions.concept(TetanusToxoidDoseType.TT3));
				} else if (Functions.observation(patient, TetanusToxoidDateAdministeredConcepts.TT4) == null) {
					// assume patient needs TT4
					form.getTetanusServiceRecord().getObs().setValueCoded(Functions.concept(TetanusToxoidDoseType.TT4));
				} else if (Functions.observation(patient, TetanusToxoidDateAdministeredConcepts.TT5) == null) {
					// assume patient needs TT5
					form.getTetanusServiceRecord().getObs().setValueCoded(Functions.concept(TetanusToxoidDoseType.TT5));
				}
			}

			// set default 'visit type' to 'health facility'
			form.getTetanusServiceRecord().getMember(TetanusToxoidRecordConcepts.VISIT_TYPE)
					.setValueCoded(Functions.conceptByIdOrName("Private Health Facility"));

			// set default 'service source' to 'health facility'
			form.getTetanusServiceRecord().getMember(TetanusToxoidRecordConcepts.SERVICE_SOURCE)
					.setValueCoded(Functions.conceptByIdOrName("Local (Health Center)"));
		}

		// dispatch to superclass
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * Performs validation.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, MaternalCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "tetanusServiceRecord.observationMap", errors);

		// validate required fields
		PatientConsultEntryFormValidator.validateRequiredFields(form, "tetanusServiceRecord.observationMap", errors, //
				TetanusToxoidRecordConcepts.DATE_ADMINISTERED, //
				TetanusToxoidRecordConcepts.VISIT_TYPE, //
				TetanusToxoidRecordConcepts.SERVICE_SOURCE);

		// validate date fields
		PatientConsultEntryFormValidator.validateValidPastDates(form, "tetanusServiceRecord.observationMap", errors, //
				TetanusToxoidRecordConcepts.DATE_ADMINISTERED);

		// make sure the 'administered by' field was set
		if (form.getTetanusServiceRecord().getAdministeredBy() == null) {
			errors.rejectValue("tetanusServiceRecord.administeredBy", "chits.error.administered.by.required");
		}

		// validate transaction date in case of in case of 'referred' patient consult status
		validateTransactionDate(form, "tetanusServiceRecord", TetanusToxoidRecordConcepts.DATE_ADMINISTERED, errors);

		// ensure tetanus dose has not already been administered
		final Obs dose = form.getTetanusServiceRecord().getObs();
		if (dose != null && dose.getValueCoded() != null) {
			for (Obs obs : Functions.observations(form.getPatient(), TetanusToxoidRecordConcepts.VACCINE_TYPE)) {
				if (dose.getValueCoded().equals(obs.getValueCoded())) {
					// this dose has already been administered
					errors.rejectValue("tetanusServiceRecord.obs.valueCoded", "chits.program.MATERNALCARE.tetanus.dose.already.given");
					break;
				}
			}
		}
	}

	/**
	 * The version object is the encounter instance
	 */
	@Override
	protected Auditable getVersionObject(MaternalCareConsultEntryForm form) {
		// use the encounter instance as the version object since it's tricky using the service type record because there could be several with timestamps
		// manually entered
		return form.getEncounter();
	}

	@Override
	protected void beforeSave(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, java.util.Collection<Obs> obsToSave,
			java.util.Collection<Obs> obsToPurge) {
		// store the 'administeredBy' field into the 'creator' attribute of the main observation
		form.getTetanusServiceRecord().getObs().setCreator(form.getTetanusServiceRecord().getAdministeredBy());

		// update the concept of the 'date administered' based on the vaccine type selected
		final Obs dateAdministeredObs = form.getTetanusServiceRecord().getMember(TetanusToxoidRecordConcepts.DATE_ADMINISTERED);
		final Concept vaccineType = form.getTetanusServiceRecord().getObs().getValueCoded();
		if (vaccineType == Functions.concept(TetanusToxoidDoseType.TT1)) {
			dateAdministeredObs.setConcept(Functions.concept(TetanusToxoidDateAdministeredConcepts.TT1));
		} else if (vaccineType == Functions.concept(TetanusToxoidDoseType.TT2)) {
			dateAdministeredObs.setConcept(Functions.concept(TetanusToxoidDateAdministeredConcepts.TT2));
		} else if (vaccineType == Functions.concept(TetanusToxoidDoseType.TT3)) {
			dateAdministeredObs.setConcept(Functions.concept(TetanusToxoidDateAdministeredConcepts.TT3));
		} else if (vaccineType == Functions.concept(TetanusToxoidDoseType.TT4)) {
			dateAdministeredObs.setConcept(Functions.concept(TetanusToxoidDateAdministeredConcepts.TT4));
		} else if (vaccineType == Functions.concept(TetanusToxoidDoseType.TT5)) {
			dateAdministeredObs.setConcept(Functions.concept(TetanusToxoidDateAdministeredConcepts.TT5));
		}

		// ensure all observations refer to the correct patient
		form.getTetanusServiceRecord().storePersonAndAudit(form.getPatient());

		// add the tetanus service record group parent record to the encounter for cascade saving
		enc.addObs(form.getTetanusServiceRecord().getObs());
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// return full page
		return "/module/chits/consults/maternalcare/ajaxAddTetanusServiceRecord";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// return full page
		return "/module/chits/consults/maternalcare/ajaxAddTetanusServiceRecord";
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
