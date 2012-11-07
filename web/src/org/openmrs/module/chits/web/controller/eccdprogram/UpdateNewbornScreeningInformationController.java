package org.openmrs.module.chits.web.controller.eccdprogram;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.NewbornScreeningConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.NewbornScreeningInformation;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator.DateValidationType;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Updates the newborn screening information for this encounter.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateChildCareNewbornScreeningInformation.form")
public class UpdateNewbornScreeningInformationController extends BaseUpdateChildCarePatientConsultDataController implements Constants {
	/** Delivery information concepts that can be edited */
	private static Collection<CachedConceptId> SCREENING_CONCEPTS = Arrays.asList( //
			new CachedConceptId[] { NewbornScreeningInformation.ACTION, //
					NewbornScreeningInformation.REPORT_DATE, //
					NewbornScreeningInformation.SCREENING_DATE //
			});

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

		if (form.getPatientQueue() != null && form.getPatientQueue().getEncounter() != null) {
			// attempt to load the screening information from the parent observation
			final Obs screeningInfo = Functions.observation(form.getPatientQueue().getEncounter(), NewbornScreeningConcepts.SCREENING_INFORMATION);

			// setup the form's observations for editing from the observation group (NOTE: screeningInfo may be null!)
			setupFormObservations(form, screeningInfo, SCREENING_CONCEPTS);
		}

		// this is the child care program
		form.setProgram(ProgramConcepts.CHILDCARE);

		return form;
	}

	@Override
	protected void postProcess(HttpServletRequest request, ChildCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// note: value validation has already been performed by the superclass; so we simply need to check for required fields

		// verify that the action was specified
		final Obs action = form.getObservationMap().get(NewbornScreeningInformation.ACTION.getConceptId());
		if (action == null || action.getValueCoded() == null) {
			// action was not specified!
			errors.rejectValue("observationMap[" + action.getConcept().getConceptId() + "].valueCoded", //
					"chits.screening.info.action.required");
		}

		// verify that the screening date was specified
		final Integer screeningDateConceptId = NewbornScreeningInformation.SCREENING_DATE.getConceptId();
		final Obs screeningDate = form.getObservationMap().get(screeningDateConceptId);
		if (screeningDate == null || screeningDate.getValueDatetime() == null) {
			// if the action selected is "REFERRED" then the "Newborn Screening Date" should not be required
			if (!Functions.concept(StatusConcepts.REFERRED).equals(action.getValueCoded())) {
				// screening date was not specified!
				errors.rejectValue("observationMap[" + screeningDateConceptId + "].valueText", "chits.screening.info.screening.date.required");
			}
		} else {
			// screening date must be on or after the patient's birth date and not in future
			PatientConsultEntryFormValidator.validateDateValue(form, NewbornScreeningInformation.SCREENING_DATE, errors, //
					DateValidationType.MUST_NOT_BE_IN_FUTURE, DateValidationType.ON_OR_AFTER_BIRTHDATE);
		}

		final Integer reportDateConceptId = NewbornScreeningInformation.REPORT_DATE.getConceptId();
		final Obs reportDate = form.getObservationMap().get(reportDateConceptId);
		if (reportDate == null || reportDate.getValueDatetime() == null) {
			// report date was not specified!
			errors.rejectValue("observationMap[" + reportDateConceptId + "].valueText", "chits.screening.info.report.date.required");
		} else {
			// report date must be on or after the patient's birth date and not in future
			PatientConsultEntryFormValidator.validateDateValue(form, NewbornScreeningInformation.REPORT_DATE, errors, //
					DateValidationType.MUST_NOT_BE_IN_FUTURE, DateValidationType.ON_OR_AFTER_BIRTHDATE);
		}
	}

	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, ChildCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// prep the observation group to contain all the information for one screening info record
		Obs screeningInfoGroup = Functions.observation(form.getPatientQueue().getEncounter(), NewbornScreeningConcepts.SCREENING_INFORMATION);
		if (screeningInfoGroup == null) {
			screeningInfoGroup = newObs(NewbornScreeningConcepts.SCREENING_INFORMATION, form.getPatient());
		}

		// set the screening info coded value into the text for viewing convenience
		screeningInfoGroup.setValueCoded(conceptService.getConcept(NewbornScreeningConcepts.SCREENING_INFORMATION.getConceptId()));
		PatientConsultEntryFormValidator.setValueCodedIntoValueText(screeningInfoGroup);

		// add the screening information group parent record to the encounter for cascade saving
		enc.addObs(screeningInfoGroup);

		// add the screening info parent group to the 'obsToSave'
		obsToSave.add(screeningInfoGroup);

		// add the screening info data to the parent service group record
		screeningInfoGroup.addGroupMember(form.getObservationMap().get(NewbornScreeningInformation.REPORT_DATE.getConceptId()));
		screeningInfoGroup.addGroupMember(form.getObservationMap().get(NewbornScreeningInformation.SCREENING_DATE.getConceptId()));
		screeningInfoGroup.addGroupMember(form.getObservationMap().get(NewbornScreeningInformation.ACTION.getConceptId()));

		// add all data to the 'obsToSave'
		obsToSave.addAll(screeningInfoGroup.getGroupMembers());
	}

	/**
	 * The version object is the newborn screening information parent group observation.
	 */
	@Override
	protected Auditable getVersionObject(ChildCareConsultEntryForm form) {
		// extract the current encounter instance
		final Encounter enc = form.getEncounter();

		if (enc != null) {
			// the version object is the latest administered service in this encounter
			return Functions.observation(enc, NewbornScreeningConcepts.SCREENING_INFORMATION);
		}

		// no available version object
		return null;
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// return full page
		return "/module/chits/consults/childcare/ajaxUpdateChildCareNewbornScreeningInformation";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// NOTE: The reload path would only be retrieved if the operation was successful
		final HttpSession httpSession = request.getSession();

		if (httpSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR) != null) {
			// replace success message with a message specific to the screening information
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.screening.info.updated.successfully");
		}

		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/updateChildCareNewbornScreeningInformation.form?patientId=" + patientId;
	}
}
