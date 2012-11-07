package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.DewormingServiceRecord;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.DewormingConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCServiceRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
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
 * Processes adding of deworming service records.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/addMaternalCareDewormingServiceRecord.form")
public class AddDewormingServiceRecordController extends BaseUpdateMaternalCarePatientConsultDataController {
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

			// prep a deworming service record for entering the data
			form.setDewormingServiceRecord(new DewormingServiceRecord());

			// this is the maternal care program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		}

		return form;
	}

	/**
	 * Initialize the form
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form) {
		// setup default for new observations
		if (form.getDewormingServiceRecord().getObs().getId() == null || form.getDewormingServiceRecord().getObs().getId() == 0) {
			// set default visit date to today's date
			final Obs dateAdministeredObs = form.getDewormingServiceRecord().getMember(MCServiceRecordConcepts.DATE_ADMINISTERED);
			dateAdministeredObs.setValueDatetime(new Date());
			dateAdministeredObs.setValueText(Context.getDateFormat().format(dateAdministeredObs.getValueDatetime()));

			// by default, the 'administeredBy' field should be the currently logged-in user
			form.getDewormingServiceRecord().setAdministeredBy(Context.getAuthenticatedUser());

			// set default 'visit type' to 'health facility'
			form.getDewormingServiceRecord().getMember(DewormingConcepts.VISIT_TYPE) //
					.setValueCoded(Functions.conceptByIdOrName("Private Health Facility"));

			// set default 'service source' to 'health facility'
			form.getDewormingServiceRecord().getMember(DewormingConcepts.SERVICE_SOURCE) //
					.setValueCoded(Functions.conceptByIdOrName("Local (Health Center)"));
		}

		// dispatch to superclass
		return super.showForm(request, httpSession, model, form);
	}

	@Override
	protected void postProcess(HttpServletRequest request, MaternalCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "dewormingServiceRecord.observationMap", errors);

		// validate required fields
		PatientConsultEntryFormValidator.validateRequiredFields(form, "dewormingServiceRecord.observationMap", errors, //
				MCServiceRecordConcepts.DATE_ADMINISTERED, //
				DewormingConcepts.VISIT_TYPE, //
				DewormingConcepts.SERVICE_SOURCE);

		// validate date fields
		PatientConsultEntryFormValidator.validateValidPastDates(form, "dewormingServiceRecord.observationMap", errors, //
				MCServiceRecordConcepts.DATE_ADMINISTERED);

		// make sure the 'administered by' field was set
		if (form.getDewormingServiceRecord().getAdministeredBy() == null) {
			errors.rejectValue("dewormingServiceRecord.administeredBy", "chits.error.administered.by.required");
		}

		// validate transaction date in case of in case of 'referred' patient consult status
		validateTransactionDate(form, "dewormingServiceRecord", MCServiceRecordConcepts.DATE_ADMINISTERED, errors);
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
	protected void preProcessEncounterObservations(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// store the 'administeredBy' field into the 'creator' attribute of the main observation
		form.getDewormingServiceRecord().getObs().setCreator(form.getDewormingServiceRecord().getAdministeredBy());

		// ensure all observations refer to the correct patient
		form.getDewormingServiceRecord().storePersonAndAudit(form.getPatient());

		// add the deworming service record group parent record to the encounter for cascade saving
		enc.addObs(form.getDewormingServiceRecord().getObs());
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// return full page
		return "/module/chits/consults/maternalcare/ajaxAddDewormingServiceRecord";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// return full page
		return "/module/chits/consults/maternalcare/ajaxAddDewormingServiceRecord";
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
