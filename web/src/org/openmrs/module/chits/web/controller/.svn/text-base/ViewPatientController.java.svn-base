package org.openmrs.module.chits.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.module.chits.CHITSPatientSearchService;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.ConceptUtilFactory;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.PatientForm;
import org.openmrs.module.chits.PhilhealthUtil;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Patient-specific form controller. Creates the model/view etc for editing patients.
 * 
 * @see org.openmrs.web.controller.person.PersonFormController
 */
@Controller
@RequestMapping(value = "/module/chits/patients/viewPatient.form")
public class ViewPatientController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the CHITS service */
	protected CHITSService chitsService;

	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** Auto-wire the concept service */
	protected ConceptService conceptService;

	/** Auto-wire the person service */
	protected PersonService personService;

	/** Auto-wire the location service */
	protected LocationService locationService;

	/** Auto-wire the concept util */
	protected ConceptUtilFactory conceptUtilFactory;

	/** Indirectly auto-wired relationship util */
	protected RelationshipUtil relationshipUtil;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public PatientForm formBackingObject(ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;

		final PatientForm form = new PatientForm();
		form.setPatient(patient);
		form.setMother(relationshipUtil.getPatientMotherOrCreteNew(patientId));
		if (patient != null) {
			if (patient.getAttribute(PhilhealthConcepts.CHITS_PHILHEALTH) != null) {
				form.setHasPhilhealth(true);
			}

			// store patient queue record (if any)
			form.setPatientQueue(chitsService.getQueuedPatient(patient));
		}

		// return the patient
		return form;
	}

	/**
	 * This method will display the patient form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpSession httpSession, //
			ModelMap model, //
			@RequestParam(required = false, value = "familyFolderId") Integer familyFolderId, //
			@ModelAttribute("form") PatientForm form) {
		final Patient patient = form.getPatient();
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");

			// send to listing page since there is no patient to view
			return "redirect:findPatient.htm";
		} else {
			// if a 'non patient' attribute exists, then this is a non-patient!
			form.setNonPatient(RelationshipUtil.isNonPatient(patient));

			// check if flagged with '4Ps'
			form.setFourPs(RelationshipUtil.hasAttributeValue(patient, MiscAttributes.FOUR_PS));
		}

		// set the version information into the form for optimistic locking
		form.setVersion(getCurrentVersion(patient));

		// initialize the model for rendering
		initFormAndModel(form, model, patient, familyFolderId);

		// send to the form page
		return getFormPath();
	}

	/**
	 * Initializes the model for rendering
	 */
	protected void initFormAndModel(PatientForm form, ModelMap model, Patient patient, Integer familyFolderId) {
		// attach the associated folders of this patient
		final List<FamilyFolder> familyFolders = new ArrayList<FamilyFolder>();
		model.addAttribute("familyFolders", familyFolders);

		// possibly creating a new patient or changing the family folder
		if (familyFolderId != null) {
			final FamilyFolder folder = chitsService.getFamilyFolder(familyFolderId);
			if (folder != null) {
				// this new patient will belong to this folder
				familyFolders.add(folder);
			}
		} else if (patient.getPatientId() != null) {
			// viewing an existing patient; attach the patient's associated family folders
			familyFolders.addAll(chitsService.getFamilyFoldersOf(patient.getPatientId()));
		}

		// indicate if the patient is the head of the family for any of the folders
		for (FamilyFolder folder : familyFolders) {
			if (folder.getHeadOfTheFamily() != null && folder.getHeadOfTheFamily().equals(patient)) {
				// patient is the head of the family
				form.setHeadOftheFamily(true);
			}
		}

		// use minimal header
		model.addAttribute(WebConstants.OPENMRS_HEADER_USE_MINIMAL, "true");

		// calculate the philhealth status and add it to the model
		model.addAttribute("philhealthStatus", PhilhealthUtil.getPhilhealthStatus(form.getPatient(), familyFolders));
	}

	/**
	 * Returns the current version of the patient being edited.
	 * 
	 * @param patient
	 *            The {@link Patient} instantce to extract the version of.
	 * @return The version of the version object.
	 */
	protected long getCurrentVersion(Patient versionObject) {
		if (versionObject != null) {
			// use the 'dateChanged' value as the version
			return versionObject.getDateChanged() != null ? versionObject.getDateChanged().getTime() : 0;
		}

		// no available object, version is '0'
		return 0;
	}

	protected String getFormPath() {
		return "/module/chits/patients/viewPatientForm";
	}

	@Autowired
	public void setPersonPatientAndCHITSService(PersonService personService, PatientService patientService, CHITSService chitsService,
			CHITSPatientSearchService chitsPatientSearchService) {
		this.personService = personService;
		this.patientService = patientService;
		this.chitsService = chitsService;
		this.relationshipUtil = new RelationshipUtil(personService, patientService, chitsPatientSearchService);
	}

	@Autowired
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Autowired
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}

	@Autowired
	public void setConceptUtilFactory(ConceptUtilFactory conceptUtilFactory) {
		this.conceptUtilFactory = conceptUtilFactory;
	}
}
