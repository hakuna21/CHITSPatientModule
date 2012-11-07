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
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPRiskFactorsConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.fpprogram.RiskFactors;
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
 * Updates the risk factors of the currently active family planning program (this will automatically cause the previous risk factors entry to become archived).
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateFamilyPlanningRiskFactors.form")
public class UpdateRiskFactorsController extends BaseUpdateFamilyPlanningPatientConsultDataController {
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

			// initialize the risk factors models
			initFormBackingObject(form);

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	/**
	 * Initializes the form with a fresh risk factors bean copying any previous risk factors data.
	 * 
	 * @param form
	 */
	protected void initFormBackingObject(FamilyPlanningConsultEntryForm form) {
		// store a blank risk factors bean into the form for submission
		final RiskFactors newRF = new RiskFactors();
		form.getFpProgramObs().setRiskFactors(newRF);
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
			// initialize the risk factors bean
			final RiskFactors newRF = form.getFpProgramObs().getRiskFactors();

			// get the latest risk factors from the model
			final Obs oldRFObs = Functions.observation(form.getFpProgramObs().getObs(), FPRiskFactorsConcepts.RISK_FACTORS);
			if (oldRFObs != null) {
				final RiskFactors oldRF = new RiskFactors(oldRFObs);

				// if old risk factors is available, pre-populate the new bean with its data
				if (oldRF != null && !ObsUtil.isNewObs(oldRF.getObs())) {
					ObsUtil.shallowCopy(oldRF, newRF);
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
		final GroupObs grp = form.getFpProgramObs().getRiskFactors();
		if ("F".equalsIgnoreCase(form.getPatient().getGender())) {
			// risk factors for females
			setNonTrueObsToFalse(grp.getObs(), //
					// STI RISKS
					FPRiskFactorsConcepts.MULTIPLE_PARTNERS, FPRiskFactorsConcepts.VAGINAL_DISCHARGE, //
					FPRiskFactorsConcepts.VAGINAL_ITCHING, FPRiskFactorsConcepts.BURNING_SENSATION, //
					FPRiskFactorsConcepts.HISTORY_OF_STI_TREATMENT);
		} else {
			// risk factors for males
			setNonTrueObsToFalse(grp.getObs(), //
					// STI RISKS
					FPRiskFactorsConcepts.MULTIPLE_PARTNERS, FPRiskFactorsConcepts.BURNING_SENSATION, //
					FPRiskFactorsConcepts.GENITAL_SORES, FPRiskFactorsConcepts.PENILE_DISCHARGE, //
					FPRiskFactorsConcepts.GENITAL_SWELLING, FPRiskFactorsConcepts.HISTORY_OF_STI_TREATMENT);
		}

		// common risk factors
		setNonTrueObsToFalse(grp.getObs(), //
				// RISKS FOR VIOLENCE
				FPRiskFactorsConcepts.DOMESTIC_VIOLENCE, FPRiskFactorsConcepts.UNPLEASANT_RELATINOSHIP, //
				FPRiskFactorsConcepts.PARTNER_DISAPPROVAL_VISIT, FPRiskFactorsConcepts.PARTNER_DISAPPROVAL_FP, //
				// Referred to , //
				FPRiskFactorsConcepts.DSWD, FPRiskFactorsConcepts.WCPU, FPRiskFactorsConcepts.NGO, //
				FPRiskFactorsConcepts.SOCIAL_HYGIENE_CLINIC, FPRiskFactorsConcepts.OTHERS);

		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "fpProgramObs.riskFactors.observationMap", errors);

		// validate date values that must be after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateValidPastDates(form, "fpProgramObs.riskFactors.observationMap", errors, //
				FPRiskFactorsConcepts.DATE_REFERRED);

		// if any of the 'referred to...' options are ticked, then the 'date referred' is also required
		if (Functions.trueConcept().equals(grp.getMember(FPRiskFactorsConcepts.DSWD).getValueCoded())
				|| Functions.trueConcept().equals(grp.getMember(FPRiskFactorsConcepts.WCPU).getValueCoded())
				|| Functions.trueConcept().equals(grp.getMember(FPRiskFactorsConcepts.NGO).getValueCoded())
				|| Functions.trueConcept().equals(grp.getMember(FPRiskFactorsConcepts.SOCIAL_HYGIENE_CLINIC).getValueCoded())
				|| Functions.trueConcept().equals(grp.getMember(FPRiskFactorsConcepts.OTHERS).getValueCoded())) {
			// 'Date Referred' is required since at least one of the 'referred to...' options is ticked
			PatientConsultEntryFormValidator.validateRequiredFields(form, "fpProgramObs.riskFactors.observationMap", errors, //
					FPRiskFactorsConcepts.DATE_REFERRED); //
		}

		// if 'Others' is ticked then the corresponding text value is required
		if (Functions.trueConcept().equals(grp.getMember(FPRiskFactorsConcepts.OTHERS).getValueCoded())) {
			if (StringUtils.isEmpty(grp.getMember(FPRiskFactorsConcepts.OTHERS).getValueText())) {
				errors.rejectValue("fpProgramObs.riskFactors.observationMap[" + FPRiskFactorsConcepts.OTHERS.getConceptId() + "].valueText",
						"chits.error.required.field");
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
		// all risk factors records
		form.getFpProgramObs().getObs().addGroupMember(form.getFpProgramObs().getRiskFactors().getObs());

		// add the risk factors and family planning observation to the encounter for processing
		setUpdatedAndAddToEncounter(form, //
				form.getFpProgramObs().getObs(), //
				form.getFpProgramObs().getRiskFactors().getObs());

		// ensure all observations refer to the correct patient and add audit information
		form.getFpProgramObs().getRiskFactors().storePersonAndAudit(form.getPatient());
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
		return "/module/chits/consults/familyplanning/registration/fragmentRiskFactors";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		return "/module/chits/consults/familyplanning/registration/fragmentRiskFactors";
	}
}
