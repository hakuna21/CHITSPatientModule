package org.openmrs.module.chits.web.controller;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.PatientForm;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.Util;
import org.openmrs.module.chits.validator.FamilyFolderValidator;
import org.openmrs.module.chits.validator.PatientValidator;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Patient-specific form controller. Creates the model/view etc for adding a Patient.
 * 
 * @see org.openmrs.web.controller.person.PersonFormController
 */
@Controller
@RequestMapping(value = "/module/chits/patients/addPatient.form")
public class AddPatientController extends ViewPatientController {
	/** Auto-wire the administration service */
	protected AdministrationService adminService;

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		final NumberFormat nf = NumberFormat.getInstance(Context.getLocale());
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
	}

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public PatientForm formBackingObject(ModelMap model) throws ServletException {
		// create a blank new patient with no values set
		final Patient blankPatient = RelationshipUtil.newBlankPatient();

		// create the form
		final PatientForm patientForm = new PatientForm();
		patientForm.setPatient(blankPatient);
		patientForm.setMother(RelationshipUtil.newBlankFemalePatientWithUUID());
		patientForm.setFamilyFolder(new FamilyFolder());

		// for new patients, by default assume the mother's record will be looked up instead of creating a new record
		patientForm.setExistingMother(true);

		// for new patients, by default assume the folder record will be looked up instead of creating a new record
		patientForm.setExistingFolder(true);

		// initialize patient attributes for editing
		initAttributesForEdit(patientForm);

		return patientForm;
	}

	/**
	 * Store blank attribute values for all 'PhoneAttributes' to prevent auto-growing error when updating the record.
	 */
	protected void initAttributesForEdit(PatientForm form) {
		// initialize attributes necessary for submitting the form
		final Patient patient = form.getPatient();
		if (patient != null) {
			// init patient phone and mobile attributes in case they will be needed for updating
			for (String attrib : new String[] { PhoneAttributes.LANDLINE_NUMBER, PhoneAttributes.MOBILE_NUMBER, IdAttributes.LOCAL_ID }) {
				if (!patient.getAttributeMap().containsKey(attrib)) {
					// add a default blank attribute value
					patient.getAttributes().add(new PersonAttribute(personService.getPersonAttributeTypeByName(attrib), ""));
				}
			}

			// hack: reset the attributeMap
			patient.setAttributes(patient.getAttributes());
		}
	}

	protected String getFormPath() {
		return "/module/chits/patients/addEditPatientForm";
	}

	protected String getSuccessMessage() {
		return "chits.Patient.created";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			ModelMap model, //
			@RequestParam(required = false, value = "familyFolderId") Integer familyFolderId, //
			@RequestParam(required = true, value = "CHITS_CRN") String crn, //
			@RequestParam(required = true, value = "Civil Status") String civilStatus, //
			@RequestParam(required = false, value = "CHITS_PHILHEALTH") String philHealth, //
			@RequestParam(required = false, value = "CHITS_PHILHEALTH_EXPIRATION") String philHealthExpiration, //
			@RequestParam(required = false, value = "CHITS_PHILHEALTH_SPONSOR") String philHealthSponsor, //
			@RequestParam(required = true, value = "CHITS_TIN") String tin, //
			@RequestParam(required = true, value = "CHITS_SSS") String sss, //
			@RequestParam(required = true, value = "CHITS_GSIS") String gsis, //
			@ModelAttribute("form") PatientForm form, //
			BindingResult errors) {
		// get the patient
		Patient patient = form.getPatient();
		Patient mother = form.getMother();
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to add or edit
			return "redirect:findPatient.htm";
		}

		// store necessary model attributes
		super.initFormAndModel(form, model, patient, familyFolderId);

		// evict the 'mother' instance to ensure that we don't change any existing record's data since it may have been used to submit new name, birth date, or
		// other data.
		Context.evictFromSession(mother);

		// check version information
		if (getCurrentVersion(patient) != form.getVersion()) {
			// optimistic locking: version mismatch
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.error.data.concurrent.update");

			// send back to main form without any further action
			return getFormPath();
		}

		// store / update the person attributes
		patient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(IdAttributes.CHITS_CRN), crn));
		patient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(IdAttributes.CHITS_SSS), sss));
		patient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(IdAttributes.CHITS_GSIS), gsis));
		patient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(IdAttributes.CHITS_TIN), tin));
		patient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(CivilStatusConcepts.CIVIL_STATUS.getConceptName()), civilStatus));

		// NOTE: since we are saving from the web server, lastModifiedOn should be blank
		patient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(MiscAttributes.LAST_MODIFIED_ON), ""));

		if (form.isHasPhilhealth()) {
			patient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(PhilhealthConcepts.CHITS_PHILHEALTH), philHealth));
			patient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(PhilhealthConcepts.CHITS_PHILHEALTH_EXPIRATION),
					philHealthExpiration));
			patient.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(PhilhealthSponsorConcepts.CHITS_PHILHEALTH_SPONSOR
					.getConceptName()), philHealthSponsor));
		}

		if (!form.isExistingMother()) {
			// in case the patient previously had an existing 'mother', we need to create a new instance to ensure that we don't overwrite the original mother's
			// patient record
			final Patient newMother = RelationshipUtil.newBlankFemalePatientWithUUID();
			final PersonName newMotherName = newMother.getPersonName();

			// update the four fields that are specified when creating a 'new' mother record
			newMother.setBirthdate(mother.getBirthdate());
			newMotherName.setGivenName(mother.getGivenName());
			newMotherName.setFamilyName(mother.getFamilyName());

			// use the 'new' mother instance for validation
			form.setMother(mother = newMother);
		} else {
			// lookup the mother patient record for the specified ID
			if (form.getMother() != null && form.getMother().getId() != null) {
				// update the mother instance in the form to reflect the correct patient record of the mother
				form.setMother(mother = patientService.getPatient(form.getMother().getId()));
			} else {
				form.setMother(mother = null);
			}

			if (form.getMother() == null) {
				// store a blank 'mother' instance into the form in case we need to render it
				form.setMother(RelationshipUtil.newBlankFemalePatientWithUUID());

				// NOTE: let the 'mother' field stay null to indicate there is no mother
				// record since it is not required for 'Non Patient' record types
			}
		}

		// validate the patient submission
		final PatientValidator validator = new PatientValidator();
		validator.validate(form, errors);

		// validate the folder submission (for new folders only)
		if (!form.isExistingFolder()) {
			final FamilyFolderValidator folderValidator = new FamilyFolderValidator("familyFolder.");
			folderValidator.validate(form.getFamilyFolder(), errors);
		} else if (familyFolderId == null || familyFolderId == 0) {
			errors.rejectValue("existingFolder", "chits.FamilyFolder.required");
		}

		// check for errors
		if (errors.hasErrors()) {
			// send back to main form
			return getFormPath();
		}

		if (!form.isHasPhilhealth()) {
			final PersonAttribute phNum = form.getPatient().getAttribute(PhilhealthConcepts.CHITS_PHILHEALTH);
			final PersonAttribute phExp = form.getPatient().getAttribute(PhilhealthConcepts.CHITS_PHILHEALTH_EXPIRATION);
			final PersonAttribute phSpo = form.getPatient().getAttribute(PhilhealthSponsorConcepts.CHITS_PHILHEALTH_SPONSOR.getConceptName());

			if (phNum != null) {
				patient.removeAttribute(phNum);
			}

			if (phExp != null) {
				patient.removeAttribute(phExp);
			}

			if (phSpo != null) {
				patient.removeAttribute(phSpo);
			}
		}

		// perform sanity checks on the attributes
		final Date now = new Date();
		setAttributeCreatorAndDateCreated(patient, now);

		if (mother != null) {
			// update mother attributes (NOTE: since we are saving from the web server, lastModifiedOn should be blank)
			mother.addAttribute(new PersonAttribute(personService.getPersonAttributeTypeByName(MiscAttributes.LAST_MODIFIED_ON), ""));
			setAttributeCreatorAndDateCreated(mother, now);

			// creating a new patient record for the mother?
			if (!form.isExistingMother()) {
				// set mother as a 'non-patient' if creating a new record just for this patient
				RelationshipUtil.setNonPatientFlag(mother, true);

				// save and update the mother patient record into the form
				form.setMother(mother = patientService.savePatient(mother));

				// format the mother's identifier based on the primary key id
				formatAndSavePatientIdentifier(mother);
			}
		}

		// add or remove the 'non patient' attribute flag based on the user selection
		RelationshipUtil.setNonPatientFlag(patient, form.isNonPatient());

		// add or remove the '4Ps' attribute flag based on the user selection
		RelationshipUtil.setFourPsFlag(patient, form.isFourPs());

		// save the patient to the database
		form.setPatient(patient = patientService.savePatient(patient));

		// update the identifier to use the formatted primary key ID
		final PatientIdentifier patientIdentifier = formatAndSavePatientIdentifier(patient);

		// update the patient relationship with the mother (if specified!)
		if (mother != null) {
			relationshipUtil.setPatientMotherRelationship(patient, mother);
		}

		// finally, add this patient to the parent folder
		if (!form.isExistingFolder()) {
			// get the new folder record
			FamilyFolder folder = form.getFamilyFolder();

			// we are creating a new family folder record
			form.setFamilyFolder(folder = chitsService.saveFamilyFolder(folder));

			// make this patient a member of the folder
			folder.getPatients().add(form.getPatient());

			if (form.isHeadOftheFamily()) {
				// set patient as head (if need be)
				folder.setHeadOfTheFamily(patient);
			}

			// set the 'code' based on the family code format
			folder.setCode(Util.formatFolderCode(adminService, folder.getId()));

			// save again to update the code
			form.setFamilyFolder(folder = chitsService.saveFamilyFolder(folder));
		} else if (familyFolderId != null && familyFolderId != 0) {
			final FamilyFolder folder = chitsService.getFamilyFolder(familyFolderId);
			if (folder != null) {
				// add the patient to the family folder
				folder.getPatients().add(patient);

				// did we have a new 'mother' record?
				if (mother != null) {
					// for 'new' mother records, add to the same family folder (or if mother doesn't
					// belong to a family folder, join her in this folder)
					if (!form.isExistingMother() || chitsService.getFamilyFoldersOf(mother.getPatientId()).isEmpty()) {
						folder.getPatients().add(mother);
					}
				}

				if (form.isHeadOftheFamily()) {
					// set patient as head (if need be)
					folder.setHeadOfTheFamily(patient);
				}

				// save the family folder's members
				chitsService.saveFamilyFolder(folder);
			}
		} else if (mother != null && (!form.isExistingMother() || chitsService.getFamilyFoldersOf(mother.getPatientId()).isEmpty())) {
			// if the patient already belongs to a family folder, then add the mother to the same (for new mothers or for mothers that don't already belong to a
			// family folder)
			final List<FamilyFolder> patientFolders = chitsService.getFamilyFoldersOf(patient.getPatientId());
			if (!patientFolders.isEmpty()) {
				// add the mother to the patient's family folder
				patientFolders.get(0).addPatient(mother);

				if (form.isHeadOftheFamily()) {
					// set patient as head (if need be)
					patientFolders.get(0).setHeadOfTheFamily(patient);
				}

				// save the family folder to include the mother
				chitsService.saveFamilyFolder(patientFolders.get(0));
			}
		} else if (mother != null && chitsService.getFamilyFoldersOf(patient.getPatientId()).isEmpty()) {
			// if the patient doesn't belong to a family folder but the mother does, then add the patient to the same family folder
			final List<FamilyFolder> motherFolder = chitsService.getFamilyFoldersOf(mother.getPatientId());
			if (!motherFolder.isEmpty()) {
				// add the patient to the mother's family folder
				motherFolder.get(0).addPatient(patient);

				if (form.isHeadOftheFamily()) {
					// set patient as head (if need be)
					motherFolder.get(0).setHeadOfTheFamily(patient);
				}

				// save the family folder to include the patient
				chitsService.saveFamilyFolder(motherFolder.get(0));
			}
		}

		// store the 'patient created' message
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { patientIdentifier.getIdentifier() });
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, getSuccessMessage());

		// send to the folders listing page
		return "redirect:viewPatient.form?patientId=" + patient.getId();
	}

	protected void setAttributeCreatorAndDateCreated(Patient patient, final Date now) {
		final List<PersonAttribute> toRemove = new ArrayList<PersonAttribute>();
		for (PersonAttribute personAttribute : patient.getAttributes()) {
			if (personAttribute.getUuid() == null) {
				personAttribute.setUuid(UUID.randomUUID().toString());
			}

			if (personAttribute.getCreator() == null) {
				personAttribute.setCreator(Context.getAuthenticatedUser());
			}

			if (personAttribute.getDateCreated() == null) {
				personAttribute.setDateCreated(now);
			}

			if (StringUtils.isEmpty(personAttribute.getValue())) {
				// mark attributes without a value for removal
				toRemove.add(personAttribute);
			}
		}

		// remove all attributes without a non-blank value
		patient.getAttributes().removeAll(toRemove);
	}

	public static PatientIdentifier formatAndSavePatientIdentifier(Patient patient) {
		final AdministrationService adminService = Context.getAdministrationService();
		final PatientService patientService = Context.getPatientService();

		final String identifier = Util.formatPatientId(adminService, patient.getId());
		final PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
		patientIdentifier.setIdentifier(identifier);

		// sanity check: ensure no other identifiers exist with the same identifier
		for (PatientIdentifier id : new HashSet<PatientIdentifier>(patient.getIdentifiers())) {
			if (!patientIdentifier.equals(id) && identifier.equalsIgnoreCase(id.getIdentifier())) {
				// purge duplicate identifier
				patient.removeIdentifier(id);
			}
		}

		return patientService.savePatientIdentifier(patientIdentifier);
	}

	@Autowired
	public void setAdminService(AdministrationService adminService) {
		this.adminService = adminService;
	}
}
