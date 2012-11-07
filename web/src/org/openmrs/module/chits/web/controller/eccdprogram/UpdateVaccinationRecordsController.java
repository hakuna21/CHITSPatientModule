package org.openmrs.module.chits.web.controller.eccdprogram;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Auditable;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.ChildCareConsultEntryForm.VaccinationRecord;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareVaccinesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.VaccinationConcepts;
import org.openmrs.module.chits.propertyeditor.VaccinationRecordEditor;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator.DateValidationType;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Submits the childcare registration form.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateChildCareVaccinationRecords.form")
public class UpdateVaccinationRecordsController extends BaseUpdateChildCarePatientConsultDataController implements Constants {
	/** Auto-wired user service */
	protected UserService userService;

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		super.initBinder(request, binder);

		// add the VaccinationRecordEditor to support auto-growing a default value of this type
		binder.registerCustomEditor(ChildCareConsultEntryForm.VaccinationRecord.class, new VaccinationRecordEditor());
	}

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public ChildCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		final ChildCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// get all mandatory child care vaccinations
		final List<Concept> unadministeredAntigens = Functions.members(VaccinationConcepts.CHILDCARE_VACCINATION);

		// remove all vaccinations that have already been administered
		final List<Obs> vaccinations = Functions.observations(form.getPatient(), VaccinationConcepts.CHILDCARE_VACCINATION);
		for (Obs vaccinationGroup : vaccinations) {
			final Obs antigenObs = Functions.observation(vaccinationGroup, VaccinationConcepts.ANTIGEN);
			if (antigenObs != null) {
				final boolean isOtherVaccine = ChildCareVaccinesConcepts.OTHERS.getConceptId().equals(antigenObs.getValueCoded().getConceptId());

				// don't remove the 'others' vaccine because it can always be added
				if (!isOtherVaccine) {
					// this vaccination has already been administered
					unadministeredAntigens.remove(antigenObs.getValueCoded());
				}
			}
		}

		// get available health facilities
		final List<Concept> healthFacilities = Functions.answers(VaccinationConcepts.HEALTH_FACILITY);

		// create a new record for whatever antigens have not yet been administered
		for (Concept antigen : unadministeredAntigens) {
			final Obs antigenObs = newObs(VaccinationConcepts.ANTIGEN, form.getPatient());
			antigenObs.setValueCoded(antigen);

			// create a record to be used to indicate if the antigen was administered
			final VaccinationRecord vr = new VaccinationRecord();
			vr.setInclude(false);
			vr.setAntigen(antigenObs);
			vr.setHealthFacility(newObs(VaccinationConcepts.HEALTH_FACILITY, form.getPatient()));
			vr.setDateAdministered(newObs(VaccinationConcepts.DATE_ADMINISTERED, form.getPatient()));

			// by default, set the health facility to the first available (i.e., the default)
			if (!healthFacilities.isEmpty()) {
				vr.getHealthFacility().setValueCoded(healthFacilities.get(0));
			}

			// add this as one of the antigens
			form.getVaccinationRecords().put(antigen, vr);
		}

		// by default, the 'administeredBy' field should be the currently logged-in user
		form.setAdministeredBy(Context.getAuthenticatedUser());

		// this is the child care program
		form.setProgram(ProgramConcepts.CHILDCARE);

		// always include currently logged in user even if not a healthworker!
		final Set<User> healthWorkers = new LinkedHashSet<User>();
		healthWorkers.add(Context.getAuthenticatedUser());
		model.put("healthWorkers", healthWorkers);

		// add all other health workers
		final Role healthWorkerRole = userService.getRole(Constants.HEALTHWORKER_ROLE);
		if (healthWorkerRole != null) {
			healthWorkers.addAll(userService.getUsersByRole(healthWorkerRole));
		}

		return form;
	}

	@Override
	protected void postProcess(HttpServletRequest request, ChildCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		for (VaccinationRecord vr : form.getVaccinationRecords().values()) {
			if (!vr.isInclude()) {
				continue;
			}

			// get the 'prefix' of the field for vaccination record entries of the current antigen
			final String fieldPrefix = "vaccinationRecords[" + vr.getAntigen().getValueCoded().getConceptId() + "].";

			// validate this vaccination record:
			try {
				// is date valid?
				final Date dateAdministered = Context.getDateFormat().parse(vr.getDateAdministered().getValueText());
				vr.getDateAdministered().setValueDatetime(dateAdministered);

				// validate the date
				PatientConsultEntryFormValidator.validateDateValue(form, vr.getDateAdministered(), fieldPrefix + "dateAdministered.valueText", errors,
						DateValidationType.MUST_NOT_BE_IN_FUTURE, DateValidationType.ON_OR_AFTER_BIRTHDATE);
			} catch (Exception ex) {
				errors.rejectValue(fieldPrefix + "dateAdministered.valueText", "chits.error.consult.invalid.date");
			}

			// ensure health facility is specified
			if (vr.getHealthFacility().getValueCoded() == null) {
				errors.rejectValue(fieldPrefix + "healthFacility.valueCoded", "chits.error.health.facility.required");
			} else {
				// store the health facility coded name into the 'value text'
				PatientConsultEntryFormValidator.setValueCodedIntoValueText(vr.getHealthFacility());
			}

			// store the antigen name into the 'value text' for non-OTHER antigens
			if (!ChildCareVaccinesConcepts.OTHERS.getConceptId().equals(vr.getAntigen().getValueCoded().getConceptId())) {
				PatientConsultEntryFormValidator.setValueCodedIntoValueText(vr.getAntigen());
			} else {
				// is the antigen specified?
				if (StringUtils.isEmpty(vr.getAntigen().getValueText())) {
					errors.rejectValue(fieldPrefix + "antigen.valueText", "chits.error.antigen.name.required");
				}
			}

			// make sure the 'administered by' field was set
			if (form.getAdministeredBy() == null) {
				errors.rejectValue("administeredBy", "chits.error.administered.by.required");
			}
		}
	}

	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, ChildCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// Add the vaccination parent obs records to include into the 'obsToSave'
		for (VaccinationRecord vr : form.getVaccinationRecords().values()) {
			if (!vr.isInclude()) {
				continue;
			}

			// create an observation group to contain all the information for one antigen
			final Obs vaccinationGroup = newObs(VaccinationConcepts.CHILDCARE_VACCINATION, form.getPatient());
			vaccinationGroup.setValueText(vaccinationGroup.getConcept().getName().getName());

			// store the 'administered by' field here
			vaccinationGroup.setCreator(form.getAdministeredBy());

			// add the vaccination group to the 'obsToSave'
			obsToSave.add(vaccinationGroup);

			// add the vaccination data to the parent vaccination record
			vaccinationGroup.addGroupMember(vr.getAntigen());
			vaccinationGroup.addGroupMember(vr.getDateAdministered());
			vaccinationGroup.addGroupMember(vr.getHealthFacility());

			// add all data to the 'obsToSave'
			obsToSave.addAll(vaccinationGroup.getGroupMembers());

			// add the vaccination group to the encounter for cascade saving
			enc.addObs(vaccinationGroup);
		}
	}

	/**
	 * The version object is the encounter instance
	 */
	@Override
	protected Auditable getVersionObject(ChildCareConsultEntryForm form) {
		// use the encounter instance as the version object since it's tricky using the vaccination obs group record because there could be several with
		// timestamps manually entered
		return form.getEncounter();
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// return full page
		return "/module/chits/consults/childcare/ajaxUpdateChildCareVaccinationRecords";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/updateChildCareVaccinationRecords.form?patientId=" + patientId;
	}

	@Autowired
	protected void setUserService(UserService userService) {
		this.userService = userService;
	}
}
