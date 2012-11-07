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
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPhysicalExaminationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.fpprogram.PhysicalExamination;
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
 * Updates the physical examination of the currently active family planning program (this will automatically cause the previous physical examination entry to
 * become archived).
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateFamilyPlanningPhysicalExamination.form")
public class UpdatePhysicalExaminationController extends BaseUpdateFamilyPlanningPatientConsultDataController {
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

			// initialize the physical examination models
			initFormBackingObject(form);

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	/**
	 * Initializes the form with a fresh physical examination bean copying any previous physical examination data.
	 * 
	 * @param form
	 */
	protected void initFormBackingObject(FamilyPlanningConsultEntryForm form) {
		// store a blank physical examination bean into the form for submission
		final PhysicalExamination newPE = new PhysicalExamination();
		form.getFpProgramObs().setPhysicalExamination(newPE);
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
			// initialize the physical examination bean
			final PhysicalExamination newPE = form.getFpProgramObs().getPhysicalExamination();

			// get the latest physical examination from the model
			final Obs oldPEObs = Functions.observation(form.getFpProgramObs().getObs(), FPPhysicalExaminationConcepts.PHYSICAL_EXAMINATION);
			if (oldPEObs != null) {
				final PhysicalExamination oldPE = new PhysicalExamination(oldPEObs);

				// if old physical examination is available, pre-populate the new bean with its data
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
		final GroupObs grp = form.getFpProgramObs().getPhysicalExamination();
		setNonTrueObsToFalse(grp.getObs(), //
				FPPhysicalExaminationConcepts.PALE_CONJUNCTIVA, FPPhysicalExaminationConcepts.YELOWISH_CONJUNCTIVA, //
				FPPhysicalExaminationConcepts.ENLARGED_THYROID, FPPhysicalExaminationConcepts.ENLARGED_LYMPH_NODES, //
				FPPhysicalExaminationConcepts.MASS_UL_ORB, FPPhysicalExaminationConcepts.MASS_UL_IRB, //
				FPPhysicalExaminationConcepts.MASS_LL_ORB, FPPhysicalExaminationConcepts.MASS_LL_IRB, //
				FPPhysicalExaminationConcepts.MASS_UL_OLB, FPPhysicalExaminationConcepts.MASS_UL_ILB, //
				FPPhysicalExaminationConcepts.MASS_LL_OLB, FPPhysicalExaminationConcepts.MASS_LL_ILB, //
				FPPhysicalExaminationConcepts.NIPPLE_DISCHARGE_LB, FPPhysicalExaminationConcepts.NIPPLE_DISCHARGE_RB, //
				FPPhysicalExaminationConcepts.DIMPLING_LEFT, FPPhysicalExaminationConcepts.DIMPLING_RIGHT, //
				FPPhysicalExaminationConcepts.ENLARGED_AXILLARY_LEFT_LYMPH_NODES, //
				FPPhysicalExaminationConcepts.ENLARGED_AXILLARY_RIGHT_LYMPH_NODES, //
				FPPhysicalExaminationConcepts.ABNORMAL_HEART_SOUNDS, //
				FPPhysicalExaminationConcepts.ABNORMAL_BREATH_SOUNDS, //
				FPPhysicalExaminationConcepts.ENLARGED_LIVER, FPPhysicalExaminationConcepts.ABDOMINAL_MASS, //
				FPPhysicalExaminationConcepts.ABDOMINAL_TENDERNESS, FPPhysicalExaminationConcepts.EDEMA, //
				FPPhysicalExaminationConcepts.VARICOSITIES);

		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "fpProgramObs.physicalExamination.observationMap", errors);
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, FamilyPlanningConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// NOTE: Just add directly to the FP program observation as a group member for directly storing
		// all physical examination records
		form.getFpProgramObs().getObs().addGroupMember(form.getFpProgramObs().getPhysicalExamination().getObs());

		// add the physical examination and family planning observation to the encounter for processing
		setUpdatedAndAddToEncounter(form, //
				form.getFpProgramObs().getObs(), //
				form.getFpProgramObs().getPhysicalExamination().getObs());

		// ensure all observations refer to the correct patient and add audit information
		form.getFpProgramObs().getPhysicalExamination().storePersonAndAudit(form.getPatient());
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
		return "/module/chits/consults/familyplanning/registration/fragmentPhysicalExam";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		return "/module/chits/consults/familyplanning/registration/fragmentPhysicalExam";
	}
}
