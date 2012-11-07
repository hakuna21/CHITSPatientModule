package org.openmrs.module.chits.web.controller.eccdprogram;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.NewbornScreeningConcepts;
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
@RequestMapping(value = "/module/chits/consults/updateChildCareNewbornScreeningResults.form")
public class UpdateNewbornScreeningResultsController extends UpdateDeliveryInformationController implements Constants {
	/** Delivery information concepts that can be edited */
	private static Collection<CachedConceptId> NEWBORN_SCREENING_CONCEPTS = Arrays.asList( //
			new CachedConceptId[] { ChildCareConcepts.CHILDCARE_REMARKS });

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

		// store all the newborn results with boolean flags all defaulting to false
		for (Concept finding : Functions.answers(NewbornScreeningConcepts.RESULTS)) {
			form.getNewbornScreeningResults().put(finding, Boolean.FALSE);
		}

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
		// store actual newborn screening findings
		final Obs deliveryInfo = Functions.observation(form.getPatient(), ChildCareConcepts.DELIVERY_INFORMATION);
		for (Obs finding : Functions.observations(deliveryInfo, NewbornScreeningConcepts.RESULTS)) {
			// flag as a positive finding
			form.getNewbornScreeningResults().put(finding.getValueCoded(), Boolean.TRUE);
		}

		// dispatch to superclass
		return super.showForm(request, httpSession, model, form);
	}

	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, ChildCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// remove old results that were un-ticked, and track which ones are new
		final Map<Concept, Boolean> newFindings = new LinkedHashMap<Concept, Boolean>(form.getNewbornScreeningResults());
		for (Obs oldFindingObs : Functions.observations(form.getPatient(), NewbornScreeningConcepts.RESULTS)) {
			final Concept oldFindingConcept = oldFindingObs.getValueCoded();
			if (Boolean.TRUE.equals(newFindings.get(oldFindingConcept))) {
				// retain this finding (remove it from the 'newFindings' so that we know not create a new observation for it
				newFindings.remove(oldFindingConcept);
			} else {
				// this finding needs to be removed
				obsToPurge.add(oldFindingObs);
			}
		}

		// any new observations need to be added
		for (Concept findingConcept : newFindings.keySet()) {
			if (Boolean.TRUE.equals(newFindings.get(findingConcept))) {
				// need to create an observation finding for this
				final Obs newFinding = newObs(NewbornScreeningConcepts.RESULTS, form.getPatient());
				newFinding.setValueCoded(findingConcept);

				// since the newborn screening observations are not part of the regular PatientConsultEntryFormValidator,
				// we set the valueText manually (this is for manual database viewing convenience only)
				PatientConsultEntryFormValidator.setValueCodedIntoValueText(newFinding);

				// add this to the 'obsToSave' for saving
				obsToSave.add(newFinding);
			}
		}

		// perform regular processing (this will also add our new findings to the 'Delivery Information' obs
		super.preProcessEncounterObservations(request, form, enc, obsToSave, obsToPurge);
	}

	@Override
	protected Collection<CachedConceptId> getConcepts() {
		return NEWBORN_SCREENING_CONCEPTS;
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// return full page
		return "/module/chits/consults/childcare/ajaxUpdateChildCareNewbornScreeningResults";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/updateChildCareNewbornScreeningResults.form?patientId=" + patientId;
	}
}
