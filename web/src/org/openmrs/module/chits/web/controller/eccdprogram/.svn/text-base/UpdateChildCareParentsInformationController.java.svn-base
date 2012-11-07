package org.openmrs.module.chits.web.controller.eccdprogram;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Updates the childcare parents information.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateChildCareParentsInformation.form")
public class UpdateChildCareParentsInformationController extends SubmitRegistrationController implements Constants {
	/** Updating parents information makes no changes to any observations */
	private static Collection<CachedConceptId> EMPTY_CONCEPTS = new ArrayList<CachedConceptId>();

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") ChildCareConsultEntryForm form) {
		// if no father relationship, then assume father is not known when editing
		if (form.getFather() == null || form.getFather().getId() == null || form.getFather().getId() == 0) {
			// father is not known!
			form.setFatherUnknown(true);
		}

		// dispatch to superclass
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * We have no observations to edit; just the parents' information which is already initialized by the superclass.
	 */
	@Override
	protected Collection<CachedConceptId> getConcepts() {
		// The concepts that this controller creates / updates
		return EMPTY_CONCEPTS;
	}

	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, ChildCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// overridden to do nothing: There are no observations that need to be pre-processed!
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// return full page
		return "/module/chits/consults/childcare/ajaxUpdateChildCareParentsInformation";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/updateChildCareParentsInformation.form?patientId=" + patientId;
	}
}
