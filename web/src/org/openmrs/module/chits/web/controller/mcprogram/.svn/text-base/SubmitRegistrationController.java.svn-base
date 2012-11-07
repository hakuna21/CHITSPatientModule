package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.StateUtil;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMedicalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMenstrualHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPersonalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPregnancyTestResultsOptions;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCRegistrationPage;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MaternalCareProgramStates;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.mcprogram.PatientConsultStatus;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Submits the maternal care registration form.
 */
@Controller("MaternalCareSubmitRegistrationController")
@RequestMapping(value = "/module/chits/consults/submitMaternalCareRegistration.form")
public class SubmitRegistrationController extends BaseUpdateMaternalCarePatientConsultDataController {
	/** Auto-wired patient program workflow service */
	protected ProgramWorkflowService programWorkflowService;

	/** Auto-wired patient service */
	protected PatientService patientService;

	/** Auto-wired Person service */
	protected PersonService personService;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// do pre-initialization via superclass
		final MaternalCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		final Patient patient = form.getPatient();
		if (patient != null) {
			// initialize the main Maternal Care Program Observation
			final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(patient);
			form.setMcProgramObs(mcProgramObs);

			final String reqPage = request.getParameter("page");
			if (reqPage != null) {
				form.setPage(MCRegistrationPage.valueOf(reqPage));
			}

			// use the 'last menstrual period' question to determine if page 1 has already been filled-in because it is a required field
			final Obs obstetricHistory = mcProgramObs.getObstetricHistory().getObs();
			final Obs lastMenstrualPeriodObs = Functions.observation(obstetricHistory, MCObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD);
			if (form.getPage() == null || lastMenstrualPeriodObs == null || lastMenstrualPeriodObs.getValueDatetime() == null) {
				// current page should be based on the information already stored for the patient
				if (lastMenstrualPeriodObs != null && lastMenstrualPeriodObs.getValueDatetime() != null) {
					// page 1 has already been submitted, send to page 2
					form.setPage(MCRegistrationPage.PAGE2_OTHER_HISTORY);
				} else {
					// set to default page (first page)
					form.setPage(MCRegistrationPage.PAGE1_OBSTETRIC_HISTORY);
				}
			}

			// ensure the patient has the 'occupation' attribute available for storing
			if (!patient.getAttributeMap().containsKey(MiscAttributes.OCCUPATION)) {
				final PersonAttribute attrib = new PersonAttribute(personService.getPersonAttributeTypeByName(MiscAttributes.OCCUPATION), "");
				attrib.setCreator(Context.getAuthenticatedUser());
				attrib.setUuid(UUID.randomUUID().toString());
				attrib.setDateCreated(new Date());
				attrib.setPerson(patient);
				patient.getAttributes().add(attrib);

				// hack: reset the attributeMap
				patient.setAttributes(patient.getAttributes());
			}

			// set the 'must see physician' value
			form.getMcProgramObs().setNeedsToSeePhysician(RelationshipUtil.isMustSeePhysician(form.getPatient()));

			// this is the family planning program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		}

		// return the patient
		return form;
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form) {
		// setup boolean values for display
		final Patient patient = form.getPatient();

		// set default values only for new records
		final GroupObs obHistoryObs = form.getMcProgramObs().getObstetricHistory();
		if (patient != null && ObsUtil.isNewObs(obHistoryObs.getObs())) {
			// set default values for 'history' questions if previous data is available and current data has not yet been entered on this form
			final Obs obstetricHistory = obHistoryObs.getObs();
			fillInWithPreviousAnswers(patient, obstetricHistory, //
					MCObstetricHistoryConcepts.HISTORY_PREV_CSECTION, //
					MCObstetricHistoryConcepts.HISTORY_3OR_MORE_MISCARRIAGES, //
					MCObstetricHistoryConcepts.HISTORY_OF_POSTPARTUM_HEMORRHAGE);

			final Obs pregancyTestResultObs = Functions.observation(obstetricHistory, MCObstetricHistoryConcepts.PREGNANCY_TEST_RESULT);
			if (pregancyTestResultObs != null && pregancyTestResultObs.getValueCoded() == null) {
				// set default pregnancy test result to 'Positive'
				pregancyTestResultObs.setValueCoded(Functions.concept(MCPregnancyTestResultsOptions.POSITIVE));
			}

			// use previous values of GPFPAL if available...
			fillInWithPreviousAnswers(patient, obstetricHistory, //
					MCObstetricHistoryConcepts.OBSTETRIC_SCORE_GRAVIDA, //
					MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PARA, //
					MCObstetricHistoryConcepts.OBSTETRIC_SCORE_FT, //
					MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PT, //
					MCObstetricHistoryConcepts.OBSTETRIC_SCORE_AM, //
					MCObstetricHistoryConcepts.OBSTETRIC_SCORE_LC, //
					MCObstetricHistoryConcepts.RHESUS_FACTOR);
		}

		// assume 'RH Factor' is 'Unknown' if still empty
		if (obHistoryObs.getMember(MCObstetricHistoryConcepts.RHESUS_FACTOR).getValueCoded() == null) {
			// assume 'unknown'
			obHistoryObs.getMember(MCObstetricHistoryConcepts.RHESUS_FACTOR).setValueCoded(Functions.conceptByIdOrName("unknown"));
		}

		// dispatch to superclass showForm
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * If there are no errors, then this method will add the 'REGISTERED' state to the patient program record.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, MaternalCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		if (form.getPage() == MCRegistrationPage.PAGE1_OBSTETRIC_HISTORY) {
			// set boolean values to 'false' concept if null (i.e., unticked)
			final Obs obstetricHistory = form.getMcProgramObs().getObstetricHistory().getObs();
			setNonTrueObsToFalse(obstetricHistory, //
					MCObstetricHistoryConcepts.HISTORY_PREV_CSECTION, //
					MCObstetricHistoryConcepts.HISTORY_3OR_MORE_MISCARRIAGES, //
					MCObstetricHistoryConcepts.HISTORY_OF_POSTPARTUM_HEMORRHAGE);

			// perform standard validation
			PatientConsultEntryFormValidator.validateObservationMap(form, "mcProgramObs.obstetricHistory.observationMap", errors);

			// check that all required fields have been specified
			PatientConsultEntryFormValidator.validateRequiredFields(form, "mcProgramObs.obstetricHistory.observationMap", errors, //
					MCObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD, //
					// MCObstetricHistoryConcepts.PREGNANCY_TEST_RESULT, //
					// MCObstetricHistoryConcepts.PREGNANCY_TEST_DATE_PERFORMED, //
					MCObstetricHistoryConcepts.OBSTETRIC_SCORE_GRAVIDA, //
					MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PARA); //

			// validate date values that must be after the patient's birth date and not in future
			PatientConsultEntryFormValidator.validateValidPastDates(form, "mcProgramObs.obstetricHistory.observationMap", errors, //
					MCObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD, //
					MCObstetricHistoryConcepts.PREGNANCY_TEST_DATE_PERFORMED);

			// perform additional validation on 'pregnancy test date performed' if it doesn't have an error
			final String pregTestField = "mcProgramObs.obstetricHistory.observationMap["
					+ MCObstetricHistoryConcepts.PREGNANCY_TEST_DATE_PERFORMED.getConceptId() + "].valueText";
			if (!errors.hasFieldErrors(pregTestField)) {
				// perform additional validation
				final Obs lmpObs = Functions.observation(obstetricHistory, MCObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD);
				final Obs pregTestObs = Functions.observation(obstetricHistory, MCObstetricHistoryConcepts.PREGNANCY_TEST_DATE_PERFORMED);
				if (lmpObs.getValueDatetime() != null && pregTestObs.getValueDatetime() != null
						&& DateUtil.stripTime(pregTestObs.getValueDatetime()).before(DateUtil.stripTime(lmpObs.getValueDatetime()))) {
					// pregnancy test date cannot be earlier than LMP
					errors.rejectValue(pregTestField, "chits.program.MATERNALCARE.error.preg.test.earlier.than.lmp");
				}
			}
		} else if (form.getPage() == MCRegistrationPage.PAGE2_OTHER_HISTORY) {
			// set boolean values to 'false' concept if not true (i.e., null or unticked)
			setNonTrueObsToFalse(form.getMcProgramObs().getMenstrualHistory().getObs(), //
					MCMenstrualHistoryConcepts.DYSMENORRHEA);
			setNonTrueObsToFalse(form.getMcProgramObs().getPatientMedicalHistory().getObs(), //
					MCMedicalHistoryConcepts.HYPERTENSION, //
					MCMedicalHistoryConcepts.ASTHMA, //
					MCMedicalHistoryConcepts.DIABETES, //
					MCMedicalHistoryConcepts.TUBERCULOSIS, //
					MCMedicalHistoryConcepts.HEART_DISEASE, //
					MCMedicalHistoryConcepts.ALLERGY, //
					MCMedicalHistoryConcepts.STI, //
					MCMedicalHistoryConcepts.BLEEDING_DISORDERS, //
					MCMedicalHistoryConcepts.THYROID, //
					MCMedicalHistoryConcepts.OTHERS); //
			setNonTrueObsToFalse(form.getMcProgramObs().getFamilyMedicalHistory().getObs(), //
					MCMedicalHistoryConcepts.HYPERTENSION, //
					MCMedicalHistoryConcepts.ASTHMA, //
					MCMedicalHistoryConcepts.DIABETES, //
					MCMedicalHistoryConcepts.TUBERCULOSIS, //
					MCMedicalHistoryConcepts.HEART_DISEASE, //
					MCMedicalHistoryConcepts.ALLERGY, //
					MCMedicalHistoryConcepts.STI, //
					MCMedicalHistoryConcepts.BLEEDING_DISORDERS, //
					MCMedicalHistoryConcepts.THYROID, //
					MCMedicalHistoryConcepts.OTHERS); //
			setNonTrueObsToFalse(form.getMcProgramObs().getPersonalHistory().getObs(), //
					MCPersonalHistoryConcepts.SMOKING_HISTORY, //
					MCPersonalHistoryConcepts.ILLICIT_DRUG_USE, //
					MCPersonalHistoryConcepts.ALCOHOLIC_INTAKE); //

			// perform standard validation
			PatientConsultEntryFormValidator.validateObservationMap(form, "mcProgramObs.menstrualHistory.observationMap", errors);
			PatientConsultEntryFormValidator.validateObservationMap(form, "mcProgramObs.patientMedicalHistory.observationMap", errors);
			PatientConsultEntryFormValidator.validateObservationMap(form, "mcProgramObs.familyMedicalHistory.observationMap", errors);
			PatientConsultEntryFormValidator.validateObservationMap(form, "mcProgramObs.personalHistory.observationMap", errors);

			// check that all required fields have been specified
			PatientConsultEntryFormValidator.validateRequiredFields(form, "mcProgramObs.menstrualHistory.observationMap", errors, //
					MCMenstrualHistoryConcepts.GIVEN_TETANUS_DOSE);
		}
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		if (form.getPage() == MCRegistrationPage.PAGE1_OBSTETRIC_HISTORY) {
			// add the obstetric history and maternal care observation to the encounter for processing
			setUpdatedAndAddToEncounter(form, //
					form.getMcProgramObs().getObs(), //
					form.getMcProgramObs().getObstetricHistory().getObs());
		} else if (form.getPage() == MCRegistrationPage.PAGE2_OTHER_HISTORY) {
			// add the menstrual history and maternal care observation to the encounter for processing
			setUpdatedAndAddToEncounter(form, //
					form.getMcProgramObs().getObs(), //
					form.getMcProgramObs().getMenstrualHistory().getObs());
		}

		// ensure all observations refer to the correct patient
		form.getMcProgramObs().storePersonAndAudit(form.getPatient());
	}

	@Override
	protected void beforeSave(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		if (form.getPage() == MCRegistrationPage.PAGE2_OTHER_HISTORY) {
			// patient is now registered
			StateUtil.addState(form.getPatient(), ProgramConcepts.MATERNALCARE, //
					MaternalCareProgramStates.ACTIVE, new Date(), null);

			// store state of registration (this cannot be changed by the 'set consult status' function
			StateUtil.addState(form.getPatient(), ProgramConcepts.MATERNALCARE, //
					MaternalCareProgramStates.REGISTERED, new Date(), null);

			// add a record to mark the status change
			final PatientConsultStatus status = form.getMcProgramObs().recordStateChange(MaternalCareProgramStates.ACTIVE, new Date(), "Initial Registration.");
			status.storePersonAndAudit(form.getPatient());

			// update the 'Must See Physician' flag
			RelationshipUtil.setMustSeePhysicianFlag(form.getPatient(), form.getMcProgramObs().isNeedsToSeePhysician());
			personService.savePerson(form.getPatient());
		}

		// update the active program timestamp to prevent concurrent updates
		final PatientProgram patientProgram = Functions.getActivePatientProgram(form.getPatient(), ProgramConcepts.MATERNALCARE);
		setUpdated(patientProgram);
		programWorkflowService.savePatientProgram(patientProgram);
	}

	/**
	 * The version object is the current active patient program when submitting registration information.
	 */
	@Override
	protected Auditable getVersionObject(MaternalCareConsultEntryForm form) {
		// the version object is the latest administered service in this encounter
		return Functions.getActivePatientProgram(form.getPatient(), ProgramConcepts.MATERNALCARE);
	}

	@Autowired
	public void setProgramWorkflowService(ProgramWorkflowService programWorkflowService) {
		this.programWorkflowService = programWorkflowService;
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		if ("program-details".equals(request.getParameter("section"))) {
			// return just the tab content
			return "/module/chits/consults/maternalcare/fragmentMaternalCareTab";
		} else {
			// return full page
			return "/module/chits/consults/viewPatientConsultForm";
		}
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/viewMaternalCareProgram.form?patientId=" + patientId;
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Autowired
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
}
