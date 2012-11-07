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
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMedicalHistoryConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.fpprogram.MedicalHistoryInformation;
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
 * Updates the medical history of the currently active family planning program (this will automatically cause the previous medical history entry to become
 * archived).
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateFamilyPlanningMedicalHistory.form")
public class UpdateMedicalHistoryController extends BaseUpdateFamilyPlanningPatientConsultDataController {
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

			// initialize the medical history models
			initFormBackingObject(form);

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	/**
	 * Initializes the form with a fresh medical history bean copying any previous medical history information data.
	 * 
	 * @param form
	 */
	protected void initFormBackingObject(FamilyPlanningConsultEntryForm form) {
		// store a blank medical history bean into the form for submission
		final MedicalHistoryInformation newMH = new MedicalHistoryInformation();
		form.getFpProgramObs().setMedicalHistoryInformation(newMH);
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
			// initialize the medical history bean
			final MedicalHistoryInformation newMH = form.getFpProgramObs().getMedicalHistoryInformation();

			// get the latest medical history from the model
			final Obs oldMHObs = Functions.observation(form.getFpProgramObs().getObs(), FPMedicalHistoryConcepts.MEDICAL_HISTORY);
			if (oldMHObs != null) {
				final MedicalHistoryInformation oldMH = new MedicalHistoryInformation(oldMHObs);

				// if old medical history is available, pre-populate the new bean with its data
				if (oldMH != null && !ObsUtil.isNewObs(oldMH.getObs())) {
					ObsUtil.shallowCopy(oldMH, newMH);
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
		final GroupObs grp = form.getFpProgramObs().getMedicalHistoryInformation();
		setNonTrueObsToFalse(grp.getObs(), //
				// HEENT
				FPMedicalHistoryConcepts.SEIZURE, FPMedicalHistoryConcepts.HEADACHE, FPMedicalHistoryConcepts.BLURRING, //
				FPMedicalHistoryConcepts.YELLOWISH_CONJUNCTIVE, FPMedicalHistoryConcepts.ENLARGED_THYROID, //
				// Chest/Heart conditions
				FPMedicalHistoryConcepts.CHEST_PAIN, FPMedicalHistoryConcepts.FATIGABILITY, FPMedicalHistoryConcepts.BREAST_MASS, //
				FPMedicalHistoryConcepts.NIPPLE_BLOOD_DISCHARGE, FPMedicalHistoryConcepts.NIPPLE_PUS_DISCHARGE, //
				FPMedicalHistoryConcepts.SBP_OVER140, FPMedicalHistoryConcepts.DBP_OVER90, //
				FPMedicalHistoryConcepts.FAMILY_HISTORY_OF_STROKES_ETC, //
				// Abdomen conditions
				FPMedicalHistoryConcepts.ABDOMINAL_MASS, FPMedicalHistoryConcepts.HISTORY_OF_GALLBLADDER, //
				FPMedicalHistoryConcepts.HISTORY_OF_LIVER_DISEASE, //
				// Genital conditions
				FPMedicalHistoryConcepts.MASS_IN_UTERUS, FPMedicalHistoryConcepts.VAGINAL_DISCHARGE, //
				FPMedicalHistoryConcepts.INTERMENSTRUAL_BLEEDING, FPMedicalHistoryConcepts.POSTCOITAL_BLEEDING, //
				// Extremities conditions
				FPMedicalHistoryConcepts.SEVERE_VARICOSITIES, FPMedicalHistoryConcepts.EDEMA, //
				// Skin conditions
				FPMedicalHistoryConcepts.YELLOWISH_SKIN, //
				// Has smoking history
				FPMedicalHistoryConcepts.SMOKING_HISTORY, //
				// others
				FPMedicalHistoryConcepts.ALLERGIES, FPMedicalHistoryConcepts.DRUG_INTAKE, //
				FPMedicalHistoryConcepts.BLEEDING_TENDENCIES, FPMedicalHistoryConcepts.ANEMIA, //
				FPMedicalHistoryConcepts.DIABETES, //
				FPMedicalHistoryConcepts.HYDATIDIFORM_MOLE);

		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "fpProgramObs.medicalHistoryInformation.observationMap", errors);

		// if 'Allergies' and / or 'Drug intake' is ticked then the corresponding text value is required
		for (CachedConceptId concept : new CachedConceptId[] { FPMedicalHistoryConcepts.ALLERGIES, FPMedicalHistoryConcepts.DRUG_INTAKE }) {
			if (Functions.trueConcept().equals(grp.getMember(concept).getValueCoded())) {
				if (StringUtils.isEmpty(grp.getMember(concept).getValueText())) {
					errors.rejectValue("fpProgramObs.medicalHistoryInformation.observationMap[" + concept.getConceptId() + "].valueText",
							"chits.error.required.field");
				}
			}
		}

		if (Functions.trueConcept().equals(grp.getMember(FPMedicalHistoryConcepts.SMOKING_HISTORY).getValueCoded())) {
			if (StringUtils.isEmpty(grp.getMember(FPMedicalHistoryConcepts.SMOKING_STICKS_PER_DAY).getValueText())) {
				errors.rejectValue("fpProgramObs.medicalHistoryInformation.observationMap[" //
						+ FPMedicalHistoryConcepts.SMOKING_STICKS_PER_DAY.getConceptId() + "].valueText", "chits.error.required.field");
			}

			if (StringUtils.isEmpty(grp.getMember(FPMedicalHistoryConcepts.SMOKING_YEARS).getValueText())) {
				errors.rejectValue("fpProgramObs.medicalHistoryInformation.observationMap[" //
						+ FPMedicalHistoryConcepts.SMOKING_YEARS.getConceptId() + "].valueText", "chits.error.required.field");
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
		// all medical history records
		form.getFpProgramObs().getObs().addGroupMember(form.getFpProgramObs().getMedicalHistoryInformation().getObs());

		// add the medical history and family planning observation to the encounter for processing
		setUpdatedAndAddToEncounter(form, //
				form.getFpProgramObs().getObs(), //
				form.getFpProgramObs().getMedicalHistoryInformation().getObs());

		// ensure all observations refer to the correct patient and add audit information
		form.getFpProgramObs().getMedicalHistoryInformation().storePersonAndAudit(form.getPatient());
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
		return "/module/chits/consults/familyplanning/registration/fragmentMedicalHistory";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		return "/module/chits/consults/familyplanning/registration/fragmentMedicalHistory";
	}
}
