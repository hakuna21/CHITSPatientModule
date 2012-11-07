package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.module.chits.CHITSPatientSearchService;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPregnancyOutcomeConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.mcprogram.ObstetricHistoryDetail;
import org.openmrs.module.chits.mcprogram.PregnancyOutcome;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.controller.AddPatientController;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Adds a new obstetric history detail record to the patient's current active maternal care program.
 * 
 * @author Bren
 */
@Controller("MaternalCareAddObstetricHistoryDetailController")
@RequestMapping(value = "/module/chits/consults/addObstetricHistoryDetail.form")
public class AddObstetricHistoryDetailController extends BaseUpdateMaternalCarePatientConsultDataController {
	/** Auto-wired user service */
	protected UserService userService;

	/** Auto-wired patient service */
	protected PatientService patientService;

	/** Auto-wired person service */
	protected PersonService personService;

	/** Auto-wired chits patient search service */
	protected CHITSPatientSearchService chitsPatientSearchService;

	/** Auto-wired relationship util */
	protected RelationshipUtil relationshipUtil;

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
			final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(form.getPatient());
			form.setMcProgramObs(mcProgramObs);

			// initialize a blank ObstetricHistoryDetail instance for entering the data
			form.setObstetricHistoryDetail(new ObstetricHistoryDetail());

			// set default value for pregnancy order
			fillInWithPreviousAnswers(patient, form.getObstetricHistoryDetail().getObs(), //
					MCObstetricHistoryDetailsConcepts.GRAVIDA);

			// this is the maternal care program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		}

		// return the patient
		return form;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form) {
		// adds one blank pregnancy outcome for the user to fill-in if there are currently none
		if (form.getObstetricHistoryDetail() != null && form.getObstetricHistoryDetail().getOutcomes().isEmpty()) {
			// add a blank pregnancy outcome
			form.getObstetricHistoryDetail().addChild(new PregnancyOutcome());
		}

		// dispatch to superclass
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * Perform other non-standard validation on form fields.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, MaternalCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "obstetricHistoryDetail.observationMap", errors);

		// check that all required fields have been specified
		PatientConsultEntryFormValidator.validateRequiredFields(form, "obstetricHistoryDetail.observationMap", errors, //
				MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY, //
				MCObstetricHistoryDetailsConcepts.GRAVIDA, //
				MCObstetricHistoryDetailsConcepts.PLACE_OF_DELIVERY, //
				MCObstetricHistoryDetailsConcepts.DELIVERY_ASSISTANT);

		// store month component into the 'year of pregnancy' datetime value if specified
		final Obs yearOfPregnancyObs = form.getObstetricHistoryDetail().getObservationMap()
				.get(MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY.getConceptId());
		if (yearOfPregnancyObs != null && yearOfPregnancyObs.getValueDatetime() != null && yearOfPregnancyObs.getValueNumeric() != null) {
			final Calendar c = Calendar.getInstance();
			c.setTime(yearOfPregnancyObs.getValueDatetime());
			final int month = yearOfPregnancyObs.getValueNumeric().intValue();
			if (month > 0 && month <= 12) {
				// NOTE: Month is zero-based, so subtract one from the month value which is stored in the valueNumeric field
				c.set(Calendar.MONTH, month - 1);
				yearOfPregnancyObs.setValueDatetime(c.getTime());
			}
		}

		// validate date value must be after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateValidPastDates(form, "obstetricHistoryDetail.observationMap", errors, //
				MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY);

		// require at least one pregnancy outcome
		boolean hasOutcome = false;
		final List<PregnancyOutcome> children = form.getObstetricHistoryDetail().getChildren();
		for (int i = 0; i < children.size(); i++) {
			final PregnancyOutcome outcome = children.get(i);
			if (outcome != null) {
				// a pregnancy outcome was specified: validate each pregnancy outcome
				hasOutcome = true;

				// perform standard validation
				PatientConsultEntryFormValidator.validateObservationMap(form, "obstetricHistoryDetail.outcomes[" + i + "].observationMap", errors);

				// check that all required fields have been specified
				PatientConsultEntryFormValidator.validateRequiredFields(form, "obstetricHistoryDetail.outcomes[" + i + "].observationMap", errors, //
						MCPregnancyOutcomeConcepts.OUTCOME, //
						MCPregnancyOutcomeConcepts.METHOD, //
						MCPregnancyOutcomeConcepts.TERM, //
						MCPregnancyOutcomeConcepts.SEX, //
						MCPregnancyOutcomeConcepts.BIRTH_WEIGHT_OF_BABY_KG);
						// MCPregnancyOutcomeConcepts.BIRTH_WEIGHT_KG);
			}
		}

		if (!hasOutcome) {
			errors.reject("chits.program.MATERNALCARE.pregnancy.outcome.required");
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.MATERNALCARE.pregnancy.outcome.required");
		}
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// add the obstetric history and maternal care observation to the encounter for processing
		setUpdatedAndAddToEncounter(form, //
				form.getMcProgramObs().getObs(), //
				form.getMcProgramObs().getObstetricHistory().getObs());

		// mark the obstetric history deatil record's timestamp
		setUpdated(form.getObstetricHistoryDetail().getObs());

		if (form.getObstetricHistoryDetail().getObs().getObsGroup() == null) {
			// add the edited "obstetric history detail" observation to the parent "obstetric history" observation if not already attached
			// NOTE: If already attached, it could be from the 'delivery report', so don't change the parent!
			form.getMcProgramObs().getObstetricHistory().addChild(form.getObstetricHistoryDetail());
		}

		// ensure all observations refer to the correct patient
		// ##FIXME: Only the observation detail history record should be audited
		form.getMcProgramObs().storePersonAndAudit(form.getPatient());
	}

	/**
	 * Create / update records for all the pregnancy outcomes connected to each baby's patient record.
	 * <p>
	 * NOTE: At this point, it is not advisable to set the baby's birthdate sine only the year (and possibly month) is given.
	 */
	@Override
	protected void beforeSave(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		final Obs yearOfBirthObs = form.getObstetricHistoryDetail().getMember(MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY);
		String yearOfBirth = yearOfBirthObs.getValueText();
		if (yearOfBirthObs.getValueNumeric() != null && yearOfBirthObs.getValueNumeric() > 0) {
			// add the month value
			yearOfBirth = yearOfBirth + "/" + yearOfBirthObs.getValueNumeric().intValue();
		}

		// define each baby as a patient record
		// defineBabiesAsPatients(form, enc, yearOfBirth);
	}

	/**
	 * Defines the mother patient's baby records as patients in the system.
	 * 
	 * @param form
	 *            The form containing the mother patient details.
	 * @param enc
	 *            The encounter record for saving observation records
	 * @param yearOfBirth
	 *            The year that the baby was born.
	 */
	protected void defineBabiesAsPatients(MaternalCareConsultEntryForm form, Encounter enc, String yearOfBirth) {
		// count children from this delivery for generating a name
		int childNumber = 1;

		// attach all children
		for (PregnancyOutcome outcome : form.getObstetricHistoryDetail().getOutcomes()) {
			Integer babyPatientId = outcome.getObs().getValueGroupId();
			Patient babyPatient = babyPatientId != null ? patientService.getPatient(babyPatientId) : null;
			if (babyPatient == null) {
				// need to create a new patient to represent this baby
				if (Functions.conceptByIdOrName("female").equals(outcome.getMember(MCPregnancyOutcomeConcepts.SEX).getValueCoded())) {
					babyPatient = RelationshipUtil.newBlankFemalePatientWithUUID();
				} else {
					babyPatient = RelationshipUtil.newBlankMalePatientWithUUID();
				}

				// set appropriate names for the baby
				final String lastName = form.getPatient().getPersonName().getFamilyName();
				final PersonName babyName = babyPatient.getPersonName();
				babyName.setFamilyName(lastName);
				babyName.setGivenName("Child #" + (childNumber++) + ", (" + babyPatient.getGender() + ") in " + yearOfBirth + " of " + lastName);

				// by default, this should be a 'non-patient'
				RelationshipUtil.setNonPatientFlag(babyPatient, true);

				// persist the partner record
				patientService.savePatient(babyPatient);

				// make sure the patient IDs are correctly set!
				AddPatientController.formatAndSavePatientIdentifier(babyPatient);

				// update references
				babyPatientId = babyPatient.getPatientId();
			}

			// ensure proper relationship between mother and child
			relationshipUtil.setPatientMotherRelationship(babyPatient, form.getPatient());

			// ensure proper connection between pregnancy outcome and baby patient ID
			outcome.setBabyPatientId(babyPatient.getPatientId());

			// attach baby to the mother's family folder
			final List<FamilyFolder> family = chitsService.getFamilyFoldersOf(form.getPatient().getPatientId());
			for (FamilyFolder ff : family) {
				// update folder to include this baby
				ff.addPatient(babyPatient);
				chitsService.saveFamilyFolder(ff);
			}

			// important: update birth weight of child so that it is owned by the baby and not the mother!
			final Obs birthWeightObs = outcome.getMember(MCPregnancyOutcomeConcepts.BIRTH_WEIGHT_KG);
			birthWeightObs.setPerson(babyPatient);

			// since the saveEncounter() method forces the person of the member observations to be the same
			// as the encounter's, we need to detach it from the encounter; the observation should still save
			// anyway since will cascade from the parent observation
			enc.removeObs(birthWeightObs);
			birthWeightObs.setEncounter(null);
		}
	}

	/**
	 * The version object is the current active patient program when submitting registration information.
	 */
	@Override
	protected Auditable getVersionObject(MaternalCareConsultEntryForm form) {
		// any changes to the "obstetric history detail" entry being edited indicates a concurrent update
		return form.getObstetricHistoryDetail().getObs();
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/maternalcare/ajaxAddEditObstetricHistoryDetail";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// no need to redirect, the rendering page should refresh the obstetric history details section and close the dialog
		return "/module/chits/consults/maternalcare/ajaxAddEditObstetricHistoryDetail";
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Autowired
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	@Autowired
	public void setChitsPatientSearchService(CHITSPatientSearchService chitsPatientSearchService) {
		this.chitsPatientSearchService = chitsPatientSearchService;
	}

	@Autowired
	public void initRelationshipUtil(PersonService personService, PatientService patientService, CHITSPatientSearchService chitsPatientSearchService) {
		this.relationshipUtil = new RelationshipUtil(personService, patientService, chitsPatientSearchService);
	}
}
