package org.openmrs.module.chits.web.controller.eccdprogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSPatientSearchService;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.StateUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareProgramStates;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Submits the childcare registration form.
 */
@Controller("ChildCareSubmitRegistrationController")
@RequestMapping(value = "/module/chits/consults/submitChildCareRegistration.form")
public class SubmitRegistrationController extends UpdateDeliveryInformationController implements Constants {
	/** Auto-wired patient program workflow service */
	protected ProgramWorkflowService programWorkflowService;

	/** Indirectly auto-wired RelationshipUtil class */
	protected RelationshipUtil relationshipUtil;

	/** Auto-wired person service */
	protected PersonService personService;

	/** Auto-wired patient service */
	protected PatientService patientService;

	/** Delivery information concepts that can be edited */
	private static Collection<CachedConceptId> REGISTRATION_CONCEPTS = Arrays.asList( //
			new CachedConceptId[] { ChildCareConcepts.BIRTH_LENGTH, //
					ChildCareConcepts.BIRTH_WEIGHT, //
					ChildCareConcepts.DELIVERY_LOCATION, //
					ChildCareConcepts.METHOD_OF_DELIVERY, //
					ChildCareConcepts.GESTATIONAL_AGE, //
					ChildCareConcepts.BIRTH_ORDER, //
					ChildCareConcepts.CHILDCARE_REMARKS, //
					ChildCareConcepts.DOB_REGISTRATION //
			});

	@Override
	protected Collection<CachedConceptId> getConcepts() {
		// The concepts that this controller creates / updates
		return REGISTRATION_CONCEPTS;
	}

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public ChildCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		final ChildCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// attach the associated folders of this patient
		final List<FamilyFolder> familyFolders = new ArrayList<FamilyFolder>();
		model.addAttribute("familyFolders", familyFolders);

		// add parents' information
		if (form.getPatient() != null) {
			// store the patient's parent records (if any)
			final Patient mother = relationshipUtil.getPatientMotherOrCreteNew(form.getPatient().getPatientId());
			form.setMother(mother);
			fillInParentRegistrationAttributes(mother, true);

			final Patient father = relationshipUtil.getPatientFatherOrCreteNew(form.getPatient().getPatientId());
			form.setFather(father);
			fillInParentRegistrationAttributes(father, false);

			// if available, store the 'registered' state's start_date into the form so that the user can change the registration data
			final PatientState registeredState = Functions.getPatientState(form.getPatient(), ProgramConcepts.CHILDCARE, ChildCareProgramStates.REGISTERED);
			if (registeredState != null) {
				// store start_date into the form so that the user can change the registration data
				form.setTimestampDate(Context.getDateFormat().format(registeredState.getStartDate()));
			}

			// set the patient's associated family folders
			familyFolders.addAll(chitsService.getFamilyFoldersOf(form.getPatient().getPatientId()));
		}

		// return the patient
		return form;
	}

	/**
	 * If there are no errors, then this method will add the 'REGISTERED' state to the patient program record.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, ChildCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// if switching to a new mother, load the patient record
		final Patient switchToMother = form.getNewMotherId() != null ? patientService.getPatient(form.getNewMotherId()) : null;
		if (switchToMother != null) {
			// switching to an existing 'mother' record
			form.setMother(switchToMother);

			// fill in attributes of mother (in case error sends us back to the input form)
			fillInParentRegistrationAttributes(form.getMother(), true);
		} else {
			// creating a new mother record
			validate(errors, "mother.", form.getMother());
		}

		if (!form.isFatherUnknown()) {
			// if switching to a new father, load the patient record
			final Patient switchToFather = form.getNewFatherId() != null ? patientService.getPatient(form.getNewFatherId()) : null;
			if (switchToFather != null) {
				// switching to an existing 'father' record
				form.setFather(switchToFather);

				// fill in attributes of father (in case error sends us back to the input form)
				fillInParentRegistrationAttributes(form.getFather(), false);
			} else {
				// creating a new father record
				validate(errors, "father.", form.getFather());
			}
		}

		if (errors.hasErrors()) {
			// errors present, don't add the 'registered' state
			return;
		}

		// is this an update of the registration information, or a new registration?
		final boolean alreadyRegistered = Functions.getPatientState(form.getPatient(), ProgramConcepts.CHILDCARE, ChildCareProgramStates.REGISTERED) != null;

		// cascade save or update the state record by saving the patient program
		PatientProgram patientProgram = StateUtil.addState(form.getPatient(), ProgramConcepts.CHILDCARE, ChildCareProgramStates.REGISTERED,
				form.getTimestamp(), null);
		if (!alreadyRegistered) {
			// add 'see physician' state to flag the patient
			RelationshipUtil.setMustSeePhysicianFlag(form.getPatient(), true);
			personService.savePerson(form.getPatient());
		}

		if (patientProgram != null) {
			programWorkflowService.savePatientProgram(patientProgram);
		}

		// (NOTE: since we are saving from the web server, lastModifiedOn should be blank)
		form.getMother().addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(MiscAttributes.LAST_MODIFIED_ON), ""));
		form.getFather().addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(MiscAttributes.LAST_MODIFIED_ON), ""));

		// clear out the parents' attributes
		PatientConsultEntryFormValidator.cleanAttributes(form.getMother());
		PatientConsultEntryFormValidator.cleanAttributes(form.getFather());

		// for 'new' mother records, ensure the created record is a non-patient
		if (form.getMother().getId() == null || form.getMother().getId() == 0) {
			RelationshipUtil.setNonPatientFlag(form.getMother(), true);
		}

		// for 'new' father records, ensure the created record is a non-patient
		if (form.getFather().getId() == null || form.getFather().getId() == 0) {
			RelationshipUtil.setNonPatientFlag(form.getFather(), true);
		}

		// save the mother record (this also cascades the attributes)
		personService.savePerson(form.getMother());

		// make sure the patient IDs are correctly set!
		AddPatientController.formatAndSavePatientIdentifier(form.getMother());

		// save the mother relationship
		relationshipUtil.setPatientMotherRelationship(form.getPatient(), form.getMother());

		// set father relationship
		if (!form.isFatherUnknown()) {
			// save the father record (this also cascades the attributes)
			personService.savePerson(form.getFather());

			// make sure the patient IDs are correctly set!
			AddPatientController.formatAndSavePatientIdentifier(form.getFather());

			// save the father relationship
			relationshipUtil.setPatientFatherRelationship(form.getPatient(), form.getFather());
		}
	}

	/**
	 * The version object is the current active patient program when submitting registration information.
	 */
	@Override
	protected Auditable getVersionObject(ChildCareConsultEntryForm form) {
		// the version object is the latest administered service in this encounter
		return Functions.getActivePatientProgram(form.getPatient(), ProgramConcepts.CHILDCARE);
	}

	/**
	 * Makes sure the necessary parent attributes for the mother / father are filled-in.
	 * 
	 * @param father
	 */
	private void fillInParentRegistrationAttributes(Patient person, boolean forceNumberOfPregnancies) {
		final List<String> attributeNames = new ArrayList<String>();
		attributeNames.add(MiscAttributes.EDUCATION);
		attributeNames.add(MiscAttributes.OCCUPATION);

		if (forceNumberOfPregnancies || "F".equalsIgnoreCase(person.getGender())) {
			attributeNames.add(MiscAttributes.NUMBER_OF_PREGNANCIES);
		}

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

	/**
	 * Validates the patient fields
	 * 
	 * @param errors
	 * @param prefix
	 * @param person
	 */
	private void validate(BindingResult errors, String prefix, Patient person) {
		if ("F".equalsIgnoreCase(person.getGender())) {
			// validate the 'number of preganancies' attribute
			final PersonAttribute attrib = person.getAttribute(MiscAttributes.NUMBER_OF_PREGNANCIES);
			if (attrib != null && !StringUtils.isEmpty(attrib.getValue())) {
				try {
					final int noOfPreganancies = Integer.parseInt(attrib.getValue());
					if (noOfPreganancies < 1) {
						// invalid number of pregnancies
						errors.rejectValue(prefix + "attributeMap[" + MiscAttributes.NUMBER_OF_PREGNANCIES + "].value", "chits.error.gravida.minimum.1");
					}
				} catch (NumberFormatException nfe) {
					// invalid number of pregnancies
					errors.rejectValue(prefix + "attributeMap[" + MiscAttributes.NUMBER_OF_PREGNANCIES + "].value", "chits.error.invalid.value");
				}
			}
		}

		// validate other patient fields
		PatientValidator.validateMinimalPatientFields(prefix, person, errors);
	}

	@Autowired
	public void setProgramWorkflowService(ProgramWorkflowService programWorkflowService) {
		this.programWorkflowService = programWorkflowService;
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		if ("program-details".equals(request.getParameter("section"))) {
			// return just the tab content
			return "/module/chits/consults/childcare/fragmentChildCareTab";
		} else {
			// return full page
			return "/module/chits/consults/viewPatientConsultForm";
		}
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/viewChildCareProgram.form?patientId=" + patientId;
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
