package org.openmrs.module.chits.web.controller.fpprogram;

import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPelvicExaminationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.fpprogram.PelvicExamination;
import org.openmrs.module.chits.obs.GroupObs;
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
 * Updates the pelvic examination of the currently active family planning program (this will automatically cause the previous pelvic examination entry to become
 * archived).
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateFamilyPlanningPelvicExamination.form")
public class UpdatePelvicExaminationController extends BaseUpdateFamilyPlanningPatientConsultDataController {
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

			// initialize the pelvic examination models
			initFormBackingObject(form);

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	/**
	 * Initializes the form with a fresh pelvic examination bean copying any previous pelvic examination data.
	 * 
	 * @param form
	 */
	protected void initFormBackingObject(FamilyPlanningConsultEntryForm form) {
		// store a blank pelvic examination bean into the form for submission
		final PelvicExamination newPE = new PelvicExamination();
		form.getFpProgramObs().setPelvicExamination(newPE);
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
			// initialize the pelvic examination bean
			final PelvicExamination newPE = form.getFpProgramObs().getPelvicExamination();

			// get the latest pelvic examination from the model
			final Obs oldPEObs = Functions.observation(form.getFpProgramObs().getObs(), FPPelvicExaminationConcepts.PELVIC_EXAMINATION);
			if (oldPEObs != null) {
				final PelvicExamination oldPE = new PelvicExamination(oldPEObs);

				// if old pelvic examination is available, pre-populate the new bean with its data
				if (oldPE != null && !ObsUtil.isNewObs(oldPE.getObs())) {
					ObsUtil.shallowCopy(oldPE, newPE);
				}
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
		// set boolean values to 'false' concept if null (i.e., unticked)
		final GroupObs grp = form.getFpProgramObs().getPelvicExamination();
		setNonTrueObsToFalse(grp.getObs(), //
				// PERINEUM
				FPPelvicExaminationConcepts.SCARS, FPPelvicExaminationConcepts.PERINEUM_WARTS, //
				FPPelvicExaminationConcepts.REDDISH, FPPelvicExaminationConcepts.PERINEUM_LACERATION, //
				// VAGINA
				FPPelvicExaminationConcepts.VAGINA_CONGESTED, FPPelvicExaminationConcepts.BARTHOLINS_CYST, //
				FPPelvicExaminationConcepts.VAGINA_WARTS, FPPelvicExaminationConcepts.SKENES_GLAND, //
				FPPelvicExaminationConcepts.VAGINAL_DISCHARGE, FPPelvicExaminationConcepts.VAGINAL_RECTOCOELE, //
				FPPelvicExaminationConcepts.CYTOCOELE, //
				// CERVIX
				FPPelvicExaminationConcepts.CERVIX_CONGESTED, FPPelvicExaminationConcepts.ERODED, //
				FPPelvicExaminationConcepts.CERVICAL_DISCHARGE, FPPelvicExaminationConcepts.POLYPS, //
				FPPelvicExaminationConcepts.CERVICAL_LACERATION, //
				// CERVIX COLOR
				FPPelvicExaminationConcepts.CERVIX_PINKISH, FPPelvicExaminationConcepts.CERVIX_BLUISH, //
				// CERVIX CONSISTENCY
				FPPelvicExaminationConcepts.CERVIX_FIRM, FPPelvicExaminationConcepts.CERVIX_SOFT, //
				// UTERUS POSITION
				FPPelvicExaminationConcepts.UTERUS_MID, FPPelvicExaminationConcepts.UTERUS_ANTEFLEXED, //
				FPPelvicExaminationConcepts.UTERUS_RETROFLEXED, //
				// UTERUS SIZE
				FPPelvicExaminationConcepts.NORMAL_UTERUS, FPPelvicExaminationConcepts.SMALL_UTERUS, //
				FPPelvicExaminationConcepts.LARGE_UTERUS, //
				// ADNEXA
				FPPelvicExaminationConcepts.NORMAL_ADNEXA, FPPelvicExaminationConcepts.ADNEXA_WITH_MASSES, //
				FPPelvicExaminationConcepts.ADNEXA_WITH_TENDERNESS); //

		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "fpProgramObs.pelvicExamination.observationMap", errors);

		// if vaginal / cervix discharge is ticked, then the corresponding text value should be specified
		for (CachedConceptId concept : new CachedConceptId[] { FPPelvicExaminationConcepts.VAGINAL_DISCHARGE, FPPelvicExaminationConcepts.CERVICAL_DISCHARGE }) {
			if (Functions.trueConcept().equals(grp.getMember(concept).getValueCoded())) {
				if (StringUtils.isEmpty(grp.getMember(concept).getValueText())) {
					errors.rejectValue("fpProgramObs.pelvicExamination.observationMap[" + concept.getConceptId() + "].valueText", "chits.error.required.field");
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
		// NOTE: Just add directly to the FP program observation as a group member for directly storing
		// all pelvic examination records
		form.getFpProgramObs().getObs().addGroupMember(form.getFpProgramObs().getPelvicExamination().getObs());

		// add the pelvic examination and family planning observation to the encounter for processing
		setUpdatedAndAddToEncounter(form, //
				form.getFpProgramObs().getObs(), //
				form.getFpProgramObs().getPelvicExamination().getObs());

		// ensure all observations refer to the correct patient and add audit information
		form.getFpProgramObs().getPelvicExamination().storePersonAndAudit(form.getPatient());
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
		return "/module/chits/consults/familyplanning/registration/fragmentPelvicExam";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		return "/module/chits/consults/familyplanning/registration/fragmentPelvicExam";
	}
}
