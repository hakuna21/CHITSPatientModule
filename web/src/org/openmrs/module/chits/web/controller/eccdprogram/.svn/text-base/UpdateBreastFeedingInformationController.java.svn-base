package org.openmrs.module.chits.web.controller.eccdprogram;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.openmrs.Auditable;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.BreastFeedingConcepts;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Submits the childcare registration form.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateChildCareBreastFeedingInformation.form")
public class UpdateBreastFeedingInformationController extends BaseUpdateChildCarePatientConsultDataController implements Constants {
	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public ChildCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		final ChildCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// store all the breastfeeding month information with blank observations (indicating no settings yet)to be filled in
		for (Concept breastFedOnMonth : Functions.members(BreastFeedingConcepts.BREASTFEEDING_INFO)) {
			// expect the 'short name' to be of the form "Mx" where 'x' is the month number
			final ConceptName shortName = breastFedOnMonth.getShortestName(Locale.ENGLISH, Boolean.FALSE);
			if (shortName != null && shortName.getName().length() <= 3) {
				form.getBreastFeedingInformation().put(breastFedOnMonth, PatientConsultEntryForm.newObs(breastFedOnMonth, form.getPatient()));
			}
		}

		if (form.getPatient() != null && form.getPatient().getBirthdate() != null) {
			final Date now = new Date();
			int ageInMonths = 0;
			if (form.getPatient().getBirthdate().getTime() < now.getTime()) {
				final Period period = new Interval(form.getPatient().getBirthdate().getTime(), now.getTime()).toPeriod();
				ageInMonths = period.getYears() * 12 + period.getMonths();
			}

			// store the 'age in months' in the model
			model.put("ageInMonths", ageInMonths);
		}

		// this is the child care program
		form.setProgram(ProgramConcepts.CHILDCARE);

		// return the patient
		return form;
	}

	/**
	 * This method will display the patient form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") ChildCareConsultEntryForm form) {
		final Patient patient = form.getPatient();
		if (patient != null) {
			// store actual newborn screening findings
			final Obs breastFedInfo = Functions.observation(patient, BreastFeedingConcepts.BREASTFEEDING_INFO);
			if (breastFedInfo != null) {
				for (Obs breastFedOnMonth : breastFedInfo.getGroupMembers()) {
					// overwrite breast feeding info with actual data
					form.getBreastFeedingInformation().put(breastFedOnMonth.getConcept(), breastFedOnMonth);
				}
			}
		}

		// dispatch to superclass
		return super.showForm(request, httpSession, model, form);
	}

	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, ChildCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// get the parent registration observation (if any) form
		Obs breastFeedingInformation = Functions.observation(form.getPatient(), BreastFeedingConcepts.BREASTFEEDING_INFO);
		if (breastFeedingInformation == null) {
			// create the parent 'delivery information' observation group
			breastFeedingInformation = newObs(BreastFeedingConcepts.BREASTFEEDING_INFO, form.getPatient());
		} else {
			// set modified time information
			setUpdated(breastFeedingInformation);
		}

		// for ease of viewing, store data in the 'coded' and 'text' fields
		breastFeedingInformation.setValueCoded(breastFeedingInformation.getConcept());
		PatientConsultEntryFormValidator.setValueCodedIntoValueText(breastFeedingInformation);

		// save this record into the encounter
		enc.addObs(breastFeedingInformation);
		obsToSave.add(breastFeedingInformation);

		// determine which are new (i.e., with blank id) and need to be saved
		for (Obs breastFedOnMonth : form.getBreastFeedingInformation().values()) {
			if (breastFedOnMonth.getObsId() == null && !StringUtils.isEmpty(breastFedOnMonth.getValueText())) {
				// this is a new record and a value was specified
				breastFedOnMonth.setValueNumeric(Boolean.valueOf(breastFedOnMonth.getValueText()) ? 1.0 : 0.0);

				// add to the 'breastFeedingInformation' group
				breastFeedingInformation.addGroupMember(breastFedOnMonth);
				obsToSave.add(breastFedOnMonth);
			}
		}
	}

	/**
	 * The version object is the latest breastfeeding information observation.
	 */
	@Override
	protected Auditable getVersionObject(ChildCareConsultEntryForm form) {
		// extract the current encounter instance
		final Encounter enc = form.getEncounter();

		if (enc != null) {
			// the version object is the latest administered service in this encounter
			return Functions.observation(enc, BreastFeedingConcepts.BREASTFEEDING_INFO);
		}

		// no available version object
		return null;
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// return full page
		return "/module/chits/consults/childcare/ajaxUpdateChildCareBreastFeedingInformation";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/updateChildCareBreastFeedingInformation.form?patientId=" + patientId;
	}
}
