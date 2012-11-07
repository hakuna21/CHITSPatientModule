package org.openmrs.module.chits.web.controller.genconsults;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.controller.BaseUpdatePatientConsultDataController;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Anthropometric data form controller for a patient in the queue
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateVitalSignsData.form")
public class UpdateVitalSignsDataController extends BaseUpdatePatientConsultDataController<PatientConsultEntryForm> implements Constants {
	/** These are the concept names that can be edited by this form */
	protected Collection<CachedConceptId> EDITABLE_CONCEPTS = Arrays.asList( //
			new CachedConceptId[] { VisitConcepts.SBP, //
					VisitConcepts.DBP, //
					VisitConcepts.PULSE, //
					VisitConcepts.RESPIRATORY_RATE, //
					VisitConcepts.TEMPERATURE_C //
			});

	@Override
	@ModelAttribute("form")
	public PatientConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId) throws ServletException {
		// dispatch initialization of form to superclass
		final PatientConsultEntryForm form = (PatientConsultEntryForm) super.formBackingObject(request, model, patientId);

		// get the encounter instance
		final Encounter enc = form.getPatientQueue() != null ? form.getPatientQueue().getEncounter() : null;

		// load the encounter observations that we need to edit into the form
		setupFormObservations(form, enc, EDITABLE_CONCEPTS);

		// when editing, set blood pressure if specified
		final Map<Integer, Obs> obsMap = form.getObservationMap();
		final String sbp = obsMap.containsKey(VisitConcepts.SBP.getConceptId()) ? obsMap.get(VisitConcepts.SBP.getConceptId()).getValueText() : null;
		final String dbp = obsMap.containsKey(VisitConcepts.DBP.getConceptId()) ? obsMap.get(VisitConcepts.DBP.getConceptId()).getValueText() : null;

		if (!StringUtils.isEmpty(sbp) || !StringUtils.isEmpty(dbp)) {
			form.setBloodPressure(sbp + "/" + dbp);
		} else {
			form.setBloodPressure("");
		}

		// send back the form with the 'blood pressure' set
		return form;
	}

	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, PatientConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		if (!obsToSave.isEmpty()) {
			// create the 'parent' observation and set all other observations to be members of it
			final Obs vitalSignsObs = newObs(VisitConcepts.VITAL_SIGNS, form.getPatient());
			vitalSignsObs.setObsDatetime(form.getTimestamp());

			// for convenience when viewing the database manually, we set the coded value and value text into the observation group parent
			vitalSignsObs.setValueCoded(conceptService.getConcept(VisitConcepts.VITAL_SIGNS.getConceptId()));
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(vitalSignsObs);

			// add all observations that will be saved into the encounter to the new 'Vital Signs' set
			for (Obs obs : obsToSave) {
				vitalSignsObs.addGroupMember(obs);
			}

			// set the vitalSignsObs to be a member of the encounter
			enc.addObs(vitalSignsObs);
		}
	}

	/**
	 * The version object is the latest vital signs parent group observation.
	 */
	@Override
	protected Auditable getVersionObject(PatientConsultEntryForm form) {
		// extract the current encounter instance
		final Encounter enc = form.getEncounter();

		if (enc != null) {
			// the version object is the latest administered service in this encounter
			return Functions.observation(enc, VisitConcepts.VITAL_SIGNS);
		}

		// no available version object
		return null;
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// send to an ajax input page
		return "/module/chits/consults/ajaxVitalSignsEditForm";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/updateVitalSignsData.form?patientId=" + patientId;
	}
}
