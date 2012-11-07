package org.openmrs.module.chits.web.controller.fpprogram;

import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPObstetricHistoryConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.fpprogram.ObstetricHistory;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPregnancyOutcomeConcepts;
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
 * Updates the obstetric history of the currently active family planning program (this will automatically cause the previous obstetric history entry to become
 * archived).
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateFamilyPlanningObstetricHistory.form")
public class UpdateObstetricHistoryController extends BaseUpdateFamilyPlanningPatientConsultDataController {
	/** Request attribute indicating if minimal validation should be performed (i.e., during registration none of the fields are required) */
	public static final String MINIMAL_VALIDATION_KEY = "use.minimal.validation";
	
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

		final Patient patient = form.getPatient();
		if (patient != null) {
			// initialize the main Family Planning Program Observation
			final FamilyPlanningProgramObs fpProgramObs = new FamilyPlanningProgramObs(patient);
			form.setFpProgramObs(fpProgramObs);

			// initialize the obstetric history models
			initFormBackingObject(form);

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	/**
	 * Initializes the form with a fresh obstetric history bean copying any previous obstetric history data.
	 * 
	 * @param form
	 */
	protected void initFormBackingObject(FamilyPlanningConsultEntryForm form) {
		// store a blank obstetric history bean into the form for submission
		final ObstetricHistory newOH = new ObstetricHistory();
		form.getFpProgramObs().setObstetricHistory(newOH);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form) {
		// setup boolean values for display
		final Patient patient = form.getPatient();
		if (patient != null) {
			// initialize the obstetric history bean
			final ObstetricHistory newOH = form.getFpProgramObs().getObstetricHistory();

			// get the latest obstetric history from the model
			final Obs oldOHObs = Functions.observation(form.getFpProgramObs().getObs(), FPObstetricHistoryConcepts.OBSTETRIC_HISTORY);
			if (oldOHObs != null) {
				final ObstetricHistory oldOH = new ObstetricHistory(oldOHObs);

				// if old obstetric history is available, pre-populate the new bean with its data
				if (oldOH != null && !ObsUtil.isNewObs(oldOH.getObs())) {
					ObsUtil.shallowCopy(oldOH, newOH);
				}
			}

			// additional initialization of default values (if available)
			final Obs obScoreObs = form.getFpProgramObs().getObstetricHistory().getObs();
			if (ObsUtil.isNewObs(obScoreObs)) {
				// use previous values of GPFPAL (obstetric score) if available...
				fillInWithPreviousAnswers(patient, obScoreObs, //
						FPObstetricHistoryConcepts.OBSTETRIC_SCORE_GRAVIDA, //
						FPObstetricHistoryConcepts.OBSTETRIC_SCORE_PARA, //
						FPObstetricHistoryConcepts.OBSTETRIC_SCORE_FT, //
						FPObstetricHistoryConcepts.OBSTETRIC_SCORE_PT, //
						FPObstetricHistoryConcepts.OBSTETRIC_SCORE_AM, //
						FPObstetricHistoryConcepts.OBSTETRIC_SCORE_LC);

				// use previous values of Date of last delivery, Type of last delivery, Previous menstrual period, Last menstrual period
				fillInWithPreviousAnswers(patient, obScoreObs, //
						FPObstetricHistoryConcepts.PREVIOUS_MENSTRUAL_PERIOD, //
						FPObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD);

				// special case for 'date of last delivery': use last value from 'MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY("year of delivery")
				fillInWithPreviousAnswerUsing(patient, obScoreObs, FPObstetricHistoryConcepts.DATE_OF_LAST_DELIVERY,
						MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY);

				// special case for 'method of last delivery': use last value from 'MCPregnancyOutcomeConcepts.METHOD("method of delivery")
				fillInWithPreviousAnswerUsing(patient, obScoreObs, FPObstetricHistoryConcepts.TYPE_OF_LAST_DELIVERY, MCPregnancyOutcomeConcepts.METHOD);
			}
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
		PatientConsultEntryFormValidator.validateObservationMap(form, "fpProgramObs.obstetricHistory.observationMap", errors);

		// Validate required fields (if not minimal validation fields)
		if (!Boolean.TRUE.equals(request.getAttribute(MINIMAL_VALIDATION_KEY))) {
			// // validate all required fields
			// PatientConsultEntryFormValidator.validateRequiredFields(form, "fpProgramObs.obstetricHistory.observationMap", errors, //
			// FPObstetricHistoryConcepts.OBSTETRIC_SCORE_GRAVIDA, //
			// FPObstetricHistoryConcepts.OBSTETRIC_SCORE_PARA, //
			// FPObstetricHistoryConcepts.OBSTETRIC_SCORE_FT, //
			// FPObstetricHistoryConcepts.OBSTETRIC_SCORE_PT, //
			// FPObstetricHistoryConcepts.OBSTETRIC_SCORE_AM, //
			// FPObstetricHistoryConcepts.OBSTETRIC_SCORE_LC, //
			// FPObstetricHistoryConcepts.PREVIOUS_MENSTRUAL_PERIOD, //
			// FPObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD, //
			// FPObstetricHistoryConcepts.DURATION_OF_BLEEDING, //
			// FPObstetricHistoryConcepts.DYSMENORRHEA, //
			// FPObstetricHistoryConcepts.AMOUNT_OF_BLEEDING, //
			// FPObstetricHistoryConcepts.REGULARITY);
		}

		// validate date values that must be after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateValidPastDates(form, "fpProgramObs.obstetricHistory.observationMap", errors, //
				FPObstetricHistoryConcepts.DATE_OF_LAST_DELIVERY, //
				FPObstetricHistoryConcepts.PREVIOUS_MENSTRUAL_PERIOD, //
				FPObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD);
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, FamilyPlanningConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// NOTE: Just add directly to the FP program observation as a group member for directly storing
		// all obstetric history records
		form.getFpProgramObs().getObs().addGroupMember(form.getFpProgramObs().getObstetricHistory().getObs());

		// add the obstetric history and family planning observation to the encounter for processing
		setUpdatedAndAddToEncounter(form, //
				form.getFpProgramObs().getObs(), //
				form.getFpProgramObs().getObstetricHistory().getObs());

		// ensure all observations refer to the correct patient and add audit information
		form.getFpProgramObs().getObstetricHistory().storePersonAndAudit(form.getPatient());
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
		return "/module/chits/consults/familyplanning/registration/fragmentObstetricHistory";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		return "/module/chits/consults/familyplanning/registration/fragmentObstetricHistory";
	}
}
