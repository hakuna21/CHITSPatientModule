package org.openmrs.module.chits.web.controller.fpprogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSPatientSearchService;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.fpprogram.FamilyInformation;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyInformationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.validator.PatientValidator;
import org.openmrs.module.chits.web.controller.AddPatientController;
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
 * Updates the family information of the currently active family planning program (this will automatically cause the previous family information entry to become
 * archived).
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateFamilyPlanningFamilyInformation.form")
public class UpdateFamilyInformationController extends BaseUpdateFamilyPlanningPatientConsultDataController {
	/** Auto-wired patient service */
	protected PatientService patientService;

	/** Auto-wired Person service */
	protected PersonService personService;

	/** Indirectly auto-wired RelationshipUtil class */
	protected RelationshipUtil relationshipUtil;

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

			// initialize the family information models
			initFormBackingObject(form);

			// this is the family planning program
			form.setProgram(ProgramConcepts.FAMILYPLANNING);
		}

		// return the patient
		return form;
	}

	/**
	 * Initializes the form with a fresh family information bean copying any previous family information data.
	 * 
	 * @param form
	 */
	protected void initFormBackingObject(FamilyPlanningConsultEntryForm form) {
		// store the patient's partner records (if any)
		final Patient partner = relationshipUtil.getPatientPartnerOrCreteNew(form.getPatient().getPatientId());
		form.setPartner(partner);

		// ensure the patient's and the partner's attributes are filled-in
		fillInPatientRegistrationAttributes(form.getPatient());
		fillInPatientRegistrationAttributes(partner);

		// store a blank family information bean into the form for submission
		final FamilyInformation newFI = new FamilyInformation();
		form.getFpProgramObs().setFamilyInformation(newFI);
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
			// initialize the family information bean
			final FamilyInformation newFI = form.getFpProgramObs().getFamilyInformation();

			// get the latest family information from the model
			final Obs oldFIObs = Functions.observation(form.getFpProgramObs().getObs(), FPFamilyInformationConcepts.FAMILY_INFORMATION);
			if (oldFIObs != null) {
				final FamilyInformation oldFI = new FamilyInformation(oldFIObs);

				// if old family information is available, pre-populate the new bean with its data
				if (oldFI != null && !ObsUtil.isNewObs(oldFI.getObs())) {
					ObsUtil.shallowCopy(oldFI, newFI);
				}
			} else {
				// set the default 'total number of children' based on maternal care obstetric history data
				fillInWithPreviousAnswerUsing(patient, newFI.getObs(), FPFamilyInformationConcepts.NUMBER_OF_CHILDREN,
						MaternalCareConstants.MCObstetricHistoryConcepts.OBSTETRIC_SCORE_LC);
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
		PatientConsultEntryFormValidator.validateObservationMap(form, "fpProgramObs.familyInformation.observationMap", errors);

		// check that all required fields have been specified
		PatientConsultEntryFormValidator.validateRequiredFields(form, "fpProgramObs.familyInformation.observationMap", errors, //
				FPFamilyInformationConcepts.NUMBER_OF_CHILDREN, //
				FPFamilyInformationConcepts.NMBR_OF_CHILDREN_DESIRED); //

		// ensure the 'average family income' is specified
		if (StringUtils.isEmpty(form.getFpProgramObs().getFamilyInformation().getFamilyFolder().getAverageFamilyIncome())) {
			errors.rejectValue("fpProgramObs.familyInformation.familyFolder.averageFamilyIncome", "chits.error.required.field");
		}

		if (!form.isPartnerNotSpecified()) {
			// if switching to a new partner, load the patient record
			final Patient switchToPartner = form.getNewPartnerId() != null ? patientService.getPatient(form.getNewPartnerId()) : null;
			if (switchToPartner != null) {
				// switching to an existing 'partner' record
				form.setPartner(switchToPartner);

				// fill in attributes of partner (in case error sends us back to the input form)
				fillInPatientRegistrationAttributes(form.getPartner());

				// make sure partner's gender is of opposite sex
				if (switchToPartner.getGender().equals(form.getPatient().getGender())) {
					errors.rejectValue("newPartnerId", "chits.program.FAMILYPLANNING.partner.must.be.of.opposite.sex");
				}
			} else {
				// creating a new partner record: set gender to be opposite that of the patient's
				if ("F".equalsIgnoreCase(form.getPatient().getGender())) {
					form.getPartner().setGender("M");
				} else {
					form.getPartner().setGender("F");
				}

				// validate other patient fields
				PatientValidator.validateMinimalPatientFields("partner.", form.getPartner(), errors);
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
		// all family planning records
		form.getFpProgramObs().getObs().addGroupMember(form.getFpProgramObs().getFamilyInformation().getObs());

		// add the family information and family planning observation to the encounter for processing
		setUpdatedAndAddToEncounter(form, //
				form.getFpProgramObs().getObs(), //
				form.getFpProgramObs().getFamilyInformation().getObs());

		// ensure all observations refer to the correct patient and add audit information
		form.getFpProgramObs().getFamilyInformation().storePersonAndAudit(form.getPatient());
	}

	@Override
	protected void beforeSave(HttpServletRequest request, FamilyPlanningConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// update patient and partner information
		final Patient patient = form.getPatient();
		final Patient partner = form.getPartner();

		// (NOTE: since we are saving from the web server, lastModifiedOn should be blank)
		patient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(MiscAttributes.LAST_MODIFIED_ON), ""));
		partner.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(MiscAttributes.LAST_MODIFIED_ON), ""));

		// clear out the patient attributes
		PatientConsultEntryFormValidator.cleanAttributes(patient);
		PatientConsultEntryFormValidator.cleanAttributes(partner);

		// for 'new' partner records, ensure the created record is a non-patient
		if (!form.isPartnerNotSpecified()) {
			if (ObsUtil.isNewEntity(partner)) {
				RelationshipUtil.setNonPatientFlag(partner, true);
			}

			// persist the partner record
			patientService.savePatient(partner);

			// make sure the patient IDs are correctly set!
			AddPatientController.formatAndSavePatientIdentifier(partner);

			// update the relationship record
			relationshipUtil.setPatientPartnerRelationship(patient, partner);
		} else {
			// detach the partner relationship
			relationshipUtil.setPatientPartnerRelationship(patient, null);
		}

		// persist the patient record
		patientService.savePatient(patient);

		// re-populate attributes for display
		fillInPatientRegistrationAttributes(patient);
		fillInPatientRegistrationAttributes(form.getPartner());
	}

	/**
	 * The version object is the family program observation
	 */
	@Override
	protected Auditable getVersionObject(FamilyPlanningConsultEntryForm form) {
		// use the family program observation so that any changes will trigger a concurrency update error
		return form.getFpProgramObs().getObs();
	}

	/**
	 * Makes sure the necessary attributes for the patient / partner are filled-in.
	 * 
	 * @param person
	 */
	private void fillInPatientRegistrationAttributes(Patient person) {
		final List<String> attributeNames = new ArrayList<String>();
		attributeNames.add(MiscAttributes.EDUCATION);
		attributeNames.add(MiscAttributes.OCCUPATION);

		for (String attributeName : attributeNames) {
			PersonAttribute attrib = person.getAttribute(attributeName);
			if (attrib == null) {
				attrib = new PersonAttribute(personService.getPersonAttributeTypeByName(attributeName), "");
				attrib.setCreator(Context.getAuthenticatedUser());
				attrib.setUuid(UUID.randomUUID().toString());
				attrib.setDateCreated(new Date());
				attrib.setPerson(person);
				person.getAttributes().add(attrib);
			}
		}

		// hack: reset the attributeMap
		person.setAttributes(person.getAttributes());
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/familyplanning/registration/fragmentFamilyInfo";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		return "/module/chits/consults/familyplanning/registration/fragmentFamilyInfo";
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Autowired
	public void initRelationshipUtil(PersonService personService, PatientService patientService, CHITSPatientSearchService chitsPatientSearchService) {
		this.personService = personService;
		this.relationshipUtil = new RelationshipUtil(personService, patientService, chitsPatientSearchService);
	}
}
