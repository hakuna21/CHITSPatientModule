package org.openmrs.module.chits.web.controller.fpprogram;

import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientQueue;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.StateUtil;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPClientTypeConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyInformationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyPlanningMethodConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFemaleMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMedicalHistoryConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPObstetricHistoryConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPelvicExaminationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPhysicalExaminationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPRegistrationPage;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPRiskFactorsConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FamilyPlanningProgramStates;
import org.openmrs.module.chits.fpprogram.FamilyPlanningMethod;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.controller.genconsults.EnrollInProgramController;
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
 * Submits the family planning registration form.
 */
@Controller("FamilyPlanningSubmitRegistrationController")
@RequestMapping(value = "/module/chits/consults/submitFamilyPlanningRegistration.form")
public class SubmitRegistrationController extends BaseUpdateFamilyPlanningPatientConsultDataController {
	/** Auto-wired patient program workflow service */
	protected ProgramWorkflowService programWorkflowService;

	/** Auto-wired patient service */
	protected PatientService patientService;

	/** Auto-wired Person service */
	protected PersonService personService;

	/** Auto-wired controller that can be re-used for family information */
	protected UpdateFamilyInformationController updateFamilyInformationController;

	/** Auto-wired controller that can be re-used for medical history */
	protected UpdateMedicalHistoryController updateMedicalHistoryController;

	/** Auto-wired controller that can be re-used for risk factors */
	protected UpdateRiskFactorsController updateRiskFactorsController;

	/** Auto-wired controller that can be re-used for obstetric history */
	protected UpdateObstetricHistoryController updateObstetricHistoryController;

	/** Auto-wired controller that can be re-used for physical examination */
	protected UpdatePhysicalExaminationController updatePhysicalExaminationController;

	/** Auto-wired controller that can be re-used for pelvic examination */
	protected UpdatePelvicExaminationController updatePelvicExaminationController;

	/** Auto-wired controller that can be re-used for enrolling in a family planning method */
	protected EnrollInNewFamilyPlanningMethodController enrollInNewFamilyPlanningMethodController;

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

		// add parents' information
		final Patient patient = form.getPatient();
		if (patient != null) {
			// initialize the main Family Planning Program Observation
			final FamilyPlanningProgramObs fpProgramObs = new FamilyPlanningProgramObs(patient);
			form.setFpProgramObs(fpProgramObs);

			// determine what registration page number should be displayed
			form.setPage(getRegistrationPage(fpProgramObs));

			if (form.getPage() == FPRegistrationPage.PAGE1_FAMILY_INFO) {
				// initialize model for family information
				updateFamilyInformationController.initFormBackingObject(form);
			} else if (form.getPage() == FPRegistrationPage.PAGE2_MEDICAL_HISTORY) {
				// initialize model for medical history
				updateMedicalHistoryController.initFormBackingObject(form);
			} else if (form.getPage() == FPRegistrationPage.PAGE3_RISK_FACTORS) {
				// initialize model for risk factors
				updateRiskFactorsController.initFormBackingObject(form);
			} else if (form.getPage() == FPRegistrationPage.PAGE4_OBSTETRIC_HISTORY) {
				// initialize model for obstetric history
				updateObstetricHistoryController.initFormBackingObject(form);
			} else if (form.getPage() == FPRegistrationPage.PAGE5_PHYSICAL_EXAM) {
				// initialize model for physical examination
				updatePhysicalExaminationController.initFormBackingObject(form);
			} else if (form.getPage() == FPRegistrationPage.PAGE6_PELVIC_EXAM) {
				// initialize model for pelvic examination
				updatePelvicExaminationController.initFormBackingObject(form);
			} else if (form.getPage() == FPRegistrationPage.PAGE7_PLANNING_METHOD) {
				// initialize model for family planning method enrollment
				enrollInNewFamilyPlanningMethodController.initFormBackingObject(form);
			}

			// set the 'must see physician' value
			form.getFpProgramObs().setNeedsToSeePhysician(RelationshipUtil.isMustSeePhysician(form.getPatient()));

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form) {
		// setup boolean values for display
		final Patient patient = form.getPatient();

		// setup the form (including default values for new records)
		if (patient != null) {
			switch (form.getPage()) {
			case PAGE1_FAMILY_INFO:
				if (ObsUtil.isNewObs(form.getFpProgramObs().getFamilyInformation().getObs())) {
					// initialize the form for the family information controller
					updateFamilyInformationController.showForm(request, httpSession, model, form);
				}
				break;
			case PAGE2_MEDICAL_HISTORY:
				if (ObsUtil.isNewObs(form.getFpProgramObs().getMedicalHistoryInformation().getObs())) {
					// initialize the form for the medical history controller
					updateMedicalHistoryController.showForm(request, httpSession, model, form);
				}
				break;
			case PAGE3_RISK_FACTORS:
				if (ObsUtil.isNewObs(form.getFpProgramObs().getRiskFactors().getObs())) {
					// initialize the form for the risk factors controller
					updateRiskFactorsController.showForm(request, httpSession, model, form);
				}
				break;
			case PAGE4_OBSTETRIC_HISTORY:
				if (ObsUtil.isNewObs(form.getFpProgramObs().getObstetricHistory().getObs())) {
					// initialize the form for the obstetric history controller
					updateObstetricHistoryController.showForm(request, httpSession, model, form);
				}
				break;
			case PAGE5_PHYSICAL_EXAM:
				if (ObsUtil.isNewObs(form.getFpProgramObs().getPhysicalExamination().getObs())) {
					// initialize the form for the physical examination controller
					updatePhysicalExaminationController.showForm(request, httpSession, model, form);
				}
				break;
			case PAGE6_PELVIC_EXAM:
				if (ObsUtil.isNewObs(form.getFpProgramObs().getPelvicExamination().getObs())) {
					// initialize the form for the pelvic examination controller
					updatePelvicExaminationController.showForm(request, httpSession, model, form);
				}
				break;
			case PAGE7_PLANNING_METHOD:
				final FamilyPlanningMethod fpMethod = form.getFamilyPlanningMethod();
				if (ObsUtil.isNewObs(fpMethod.getObs())) {
					// initialize the form for the new family planning method enrollment
					enrollInNewFamilyPlanningMethodController.showForm(request, httpSession, model, form);
				}
				break;
			}
		}

		// dispatch to superclass showForm
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * If there are no errors, then this method will add the 'REGISTERED' state to the patient program record.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, FamilyPlanningConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		if (form.getPage() == FPRegistrationPage.PAGE1_FAMILY_INFO) {
			// perform family information validation
			updateFamilyInformationController.postProcess(request, form, map, enc, errors);
		} else if (form.getPage() == FPRegistrationPage.PAGE2_MEDICAL_HISTORY) {
			// perform medical history validation
			updateMedicalHistoryController.postProcess(request, form, map, enc, errors);
		} else if (form.getPage() == FPRegistrationPage.PAGE3_RISK_FACTORS) {
			// perform risk factors validation
			updateRiskFactorsController.postProcess(request, form, map, enc, errors);
		} else if (form.getPage() == FPRegistrationPage.PAGE4_OBSTETRIC_HISTORY) {
			// perform obstetric history validation (use minimal required fields)
			request.setAttribute(UpdateObstetricHistoryController.MINIMAL_VALIDATION_KEY, Boolean.TRUE);
			updateObstetricHistoryController.postProcess(request, form, map, enc, errors);
		} else if (form.getPage() == FPRegistrationPage.PAGE5_PHYSICAL_EXAM) {
			// perform physical examination validation
			updatePhysicalExaminationController.postProcess(request, form, map, enc, errors);
		} else if (form.getPage() == FPRegistrationPage.PAGE6_PELVIC_EXAM) {
			// perform pelvic examination validation
			updatePelvicExaminationController.postProcess(request, form, map, enc, errors);
		} else if (form.getPage() == FPRegistrationPage.PAGE7_PLANNING_METHOD) {
			// perform family planning method enrollment validation
			enrollInNewFamilyPlanningMethodController.postProcess(request, form, map, enc, errors);
		}
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, FamilyPlanningConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		if (form.getPage() == FPRegistrationPage.PAGE1_FAMILY_INFO) {
			// add the family information observation to the encounter for processing
			updateFamilyInformationController.preProcessEncounterObservations(request, form, enc, obsToSave, obsToPurge);
		} else if (form.getPage() == FPRegistrationPage.PAGE2_MEDICAL_HISTORY) {
			// add the medical history observation to the encounter for processing
			updateMedicalHistoryController.preProcessEncounterObservations(request, form, enc, obsToSave, obsToPurge);
		} else if (form.getPage() == FPRegistrationPage.PAGE3_RISK_FACTORS) {
			// add the risk factors observation to the encounter for processing
			updateRiskFactorsController.preProcessEncounterObservations(request, form, enc, obsToSave, obsToPurge);
		} else if (form.getPage() == FPRegistrationPage.PAGE4_OBSTETRIC_HISTORY) {
			// add the obstetric history observation to the encounter for processing
			updateObstetricHistoryController.preProcessEncounterObservations(request, form, enc, obsToSave, obsToPurge);
		} else if (form.getPage() == FPRegistrationPage.PAGE5_PHYSICAL_EXAM) {
			// add the physical examination observation to the encounter for processing
			updatePhysicalExaminationController.preProcessEncounterObservations(request, form, enc, obsToSave, obsToPurge);
		} else if (form.getPage() == FPRegistrationPage.PAGE6_PELVIC_EXAM) {
			// add the physical examination observation to the encounter for processing
			updatePelvicExaminationController.preProcessEncounterObservations(request, form, enc, obsToSave, obsToPurge);
		} else if (form.getPage() == FPRegistrationPage.PAGE7_PLANNING_METHOD) {
			// add the family planning method observation to the encounter for processing
			enrollInNewFamilyPlanningMethodController.preProcessEncounterObservations(request, form, enc, obsToSave, obsToPurge);
		}
	}

	@Override
	protected void beforeSave(HttpServletRequest request, FamilyPlanningConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		if (form.getPage() == FPRegistrationPage.PAGE1_FAMILY_INFO) {
			// invoke callback on family information controller
			updateFamilyInformationController.beforeSave(request, form, enc, obsToSave, obsToPurge);
		} else if (form.getPage() == FPRegistrationPage.PAGE7_PLANNING_METHOD) {
			// patient is now registered
			StateUtil.addState(form.getPatient(), ProgramConcepts.FAMILYPLANNING, //
					FamilyPlanningProgramStates.REGISTERED, new Date(), null);

			// perform additional steps for family planning enrollment
			enrollInNewFamilyPlanningMethodController.beforeSave(request, form, enc, obsToSave, obsToPurge);
		}

		// update the 'Must See Physician' flag
		RelationshipUtil.setMustSeePhysicianFlag(form.getPatient(), form.getFpProgramObs().isNeedsToSeePhysician());
		personService.savePerson(form.getPatient());

		// update the active program timestamp to prevent concurrent updates
		final PatientProgram patientProgram = Functions.getActivePatientProgram(form.getPatient(), ProgramConcepts.FAMILYPLANNING);
		setUpdated(patientProgram);
		programWorkflowService.savePatientProgram(patientProgram);
	}

	/**
	 * The version object is the current active patient program when submitting registration information.
	 */
	@Override
	protected Auditable getVersionObject(FamilyPlanningConsultEntryForm form) {
		// the version object is the latest administered service in this encounter
		return Functions.getActivePatientProgram(form.getPatient(), ProgramConcepts.FAMILYPLANNING);
	}

	@Autowired
	public void setProgramWorkflowService(ProgramWorkflowService programWorkflowService) {
		this.programWorkflowService = programWorkflowService;
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		if ("program-details".equals(request.getParameter("section"))) {
			// return just the tab content
			return "/module/chits/consults/familyplanning/fragmentFamilyPlanningTab";
		} else {
			// return full page
			return "/module/chits/consults/viewPatientConsultForm";
		}
	}

	/**
	 * Determines the appropriate registration page to display for the user.
	 * 
	 * @return The registration page that should be displayed
	 */
	private FPRegistrationPage getRegistrationPage(FamilyPlanningProgramObs fpProgramObs) {
		final Obs obs = fpProgramObs.getObs();

		// current page should be based on the information already stored for the patient
		if (Functions.observation(obs, FPFamilyPlanningMethodConcepts.FAMILY_PLANNING_METHOD) != null) {
			// page 7 information already submitted; set anyway to page 7 (last page)
			return FPRegistrationPage.PAGE7_PLANNING_METHOD;
		} else if (Functions.observation(obs, FPPelvicExaminationConcepts.PELVIC_EXAMINATION) != null) {
			// page 6 information already submitted; set to page 7 (page after this)
			return FPRegistrationPage.PAGE7_PLANNING_METHOD;
		} else if (Functions.observation(obs, FPPhysicalExaminationConcepts.PHYSICAL_EXAMINATION) != null) {
			// page 5 information already submitted
			if ("F".equalsIgnoreCase(obs.getPerson().getGender())) {
				// for females: set to page 6 (page after this)
				return FPRegistrationPage.PAGE6_PELVIC_EXAM;
			} else {
				// for males: set to page 7 (page after this)
				return FPRegistrationPage.PAGE7_PLANNING_METHOD;
			}
		} else if (Functions.observation(obs, FPObstetricHistoryConcepts.OBSTETRIC_HISTORY) != null) {
			// page 4 information already submitted; set to page 5 (page after this)
			return FPRegistrationPage.PAGE5_PHYSICAL_EXAM;
		} else if (Functions.observation(obs, FPRiskFactorsConcepts.RISK_FACTORS) != null) {
			// page 3 information already submitted; set to page 4 (page after this)
			return FPRegistrationPage.PAGE4_OBSTETRIC_HISTORY;
		} else if (Functions.observation(obs, FPMedicalHistoryConcepts.MEDICAL_HISTORY) != null) {
			// page 2 information already submitted; set to page 3 (page after this)
			return FPRegistrationPage.PAGE3_RISK_FACTORS;
		} else if (Functions.observation(obs, FPFamilyInformationConcepts.FAMILY_INFORMATION) != null) {
			// page 1 information already submitted; set to page 2 (page after this)
			return FPRegistrationPage.PAGE2_MEDICAL_HISTORY;
		} else {
			// no information submitted yet: set to page 1
			return FPRegistrationPage.PAGE1_FAMILY_INFO;
		}
	}

	/**
	 * Enrolls the patient in FP as new acceptor of NFP-LAM.
	 * <p>
	 * NOTE: This method is no longer used as per email from Ana (Re: Family Planning UAT, 11/5/2012 11:59 PM):
	 * 
	 * <pre>
	 * "...if the patient has given birth several times already, we cannot enroll her as a new acceptor
	 * of NFP-LAM because she has already practiced that method before.  To make it less complicated,
	 * let's just direct the user to the FP Program chart without doing anything (not enrolled; let's
	 * leave it to the healthworker to update the FP chart)."
	 * </pre>
	 * 
	 * @param patientQueue
	 *            The queue entry containing the patient and encounter entities for processing
	 * @return true if the patient was successfully enrolled in family planning, false if the patient was already enrolled previously
	 */
	public static boolean enrollPatientAsLAM(PatientQueue patientQueue) {
		// get required entities
		final Patient patient = patientQueue.getPatient();
		final ProgramWorkflowService programWorkflowService = Context.getProgramWorkflowService();
		final Program program = programWorkflowService.getProgram(ProgramConcepts.FAMILYPLANNING.getProgramId());

		// enroll in FP if not already enrolled
		final boolean isEnrolledInFamilyPlanning = Functions.getActivePatientProgram(patient, ProgramConcepts.FAMILYPLANNING) != null;
		if (!isEnrolledInFamilyPlanning) {
			// enroll the patient in family planning
			EnrollInProgramController.enrollPatientInProgram(programWorkflowService, patient, program);
		}

		// prepare a form for the family planning program
		final FamilyPlanningProgramObs fpProgramObs = new FamilyPlanningProgramObs(patient);
		
		// get the latest family planning method (if any): does patient already have active (not dropout) NFP_LAM entry?
		final Obs fpMethodObs = Functions.observation(patient, FPFamilyPlanningMethodConcepts.FAMILY_PLANNING_METHOD);
		if (fpMethodObs != null) {
			// no family planning method to drop
			final FamilyPlanningMethod fpm = new FamilyPlanningMethod(fpMethodObs);
			if (!fpm.isDroppedOut()) {
				// patient already enrolled in a family planning method and is not yet dropped out
				// so don't proceed to add new enrollment; patient not enrolled
				return false;
			}
		}

		// setup the family planning method as 'NFP-LAM' and client type as 'Acceptor'
		final FamilyPlanningMethod fpm = new FamilyPlanningMethod();
		final Date now = new Date();
		fpm.getObs().setValueCoded(Functions.concept(FPFemaleMethodOptions.NFP_LAM));
		fpm.getMember(FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT).setValueDatetime(now);
		fpm.getMember(FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT).setValueText(Context.getDateFormat().format(now));
		fpm.getMember(FPFamilyPlanningMethodConcepts.CLIENT_TYPE).setValueCoded(Functions.concept(FPClientTypeConcepts.NA));
		PatientConsultEntryFormValidator.setValueCodedIntoValueText(fpm.getMember(FPFamilyPlanningMethodConcepts.CLIENT_TYPE));
		fpm.getMember(FPFamilyPlanningMethodConcepts.REMARKS).setValueText("Automatically enrolled via Maternal Care module");

		// store the enrollment in the current program
		final Encounter enc = patientQueue.getEncounter();
		enc.addObs(fpProgramObs.getObs());
		enc.addObs(fpm.getObs());

		// now that we're saving this, add the family planning method to the parent observation
		fpProgramObs.getObs().addGroupMember(fpm.getObs());

		// ensure all observations refer to the correct patient and add audit information
		fpm.storePersonAndAudit(patient);

		// save the encounter to cascade-save the FP enrollment information
		Context.getEncounterService().saveEncounter(enc);

		// patient is now registered
		StateUtil.addState(patient, ProgramConcepts.FAMILYPLANNING, FamilyPlanningProgramStates.REGISTERED, new Date(), null);

		// save the patient program
		programWorkflowService.savePatientProgram(fpProgramObs.getPatientProgram());

		// patient successfully enrolled
		return true;
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/viewFamilyPlanningProgram.form?patientId=" + patientId;
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
	public void setUpdateFamilyInformationController(UpdateFamilyInformationController updateFamilyInformationController) {
		this.updateFamilyInformationController = updateFamilyInformationController;
	}

	@Autowired
	public void setUpdateMedicalHistoryController(UpdateMedicalHistoryController updateMedicalHistoryController) {
		this.updateMedicalHistoryController = updateMedicalHistoryController;
	}

	@Autowired
	public void setUpdateRiskFactorsController(UpdateRiskFactorsController updateRiskFactorsController) {
		this.updateRiskFactorsController = updateRiskFactorsController;
	}

	@Autowired
	public void setUpdateObstetricHistoryController(UpdateObstetricHistoryController updateObstetricHistoryController) {
		this.updateObstetricHistoryController = updateObstetricHistoryController;
	}

	@Autowired
	public void setUpdatePhysicalExaminationController(UpdatePhysicalExaminationController updatePhysicalExaminationController) {
		this.updatePhysicalExaminationController = updatePhysicalExaminationController;
	}

	@Autowired
	public void setUpdatePelvicExaminationController(UpdatePelvicExaminationController updatePelvicExaminationController) {
		this.updatePelvicExaminationController = updatePelvicExaminationController;
	}

	@Autowired
	public void setEnrollInNewFamilyPlanningMethodController(EnrollInNewFamilyPlanningMethodController enrollInNewFamilyPlanningMethodController) {
		this.enrollInNewFamilyPlanningMethodController = enrollInNewFamilyPlanningMethodController;
	}
}
