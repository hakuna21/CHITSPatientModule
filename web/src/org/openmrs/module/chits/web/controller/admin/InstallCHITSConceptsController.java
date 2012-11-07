package org.openmrs.module.chits.web.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.ConceptUtilFactory;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.BreastFeedingConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServiceTypes;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServicesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareVaccinesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.MethodOfDeliveryConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.NewbornScreeningConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.NewbornScreeningInformation;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.NewbornScreeningResults;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.VaccinationConcepts;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * CHITS Concepts installer controller.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/installChitsConcepts")
public class InstallCHITSConceptsController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Utility class for managing concepts - gets set when the concept service is autowired */
	private ConceptUtilFactory conceptUtilFactory;

	/** Auto-wired user service */
	private UserService userService;

	/**
	 * This method will setup the chits concepts required for the CHITS module proper execution.
	 * 
	 * @param httpRequest
	 *            The request instance
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String installCHITSConcepts(HttpSession httpSession) {
		// user must be an administrator
		if (!Context.hasPrivilege(PrivilegeConstants.VIEW_ADMIN_FUNCTIONS)) {
			throw new APIAuthenticationException("Privilege required: " + PrivilegeConstants.VIEW_ADMIN_FUNCTIONS);
		}

		try {
			// initialize a new ConceptUtil instance
			final ConceptUtil conceptUtil = conceptUtilFactory.newInstance();

			// setup roles
			setupRoles();

			// setup the concepts
			setupConcepts(conceptUtil);

			// concepts successfully installed!
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.concepts.install.completed");
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS,
					new Object[] { conceptUtil.getAdded(), conceptUtil.getModified(), conceptUtil.getUnchanged() });
		} catch (Exception ex) {
			log.error("Concepts installation failed", ex);

			// indicate the error
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, new Object[] { ex.getMessage() });
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.concepts.install.failed");
		}

		// send to the admin page
		return "redirect:/admin/index.htm";
	}

	/**
	 * Setup roles needed by CHITS, if needed.
	 */
	protected void setupRoles() {
		boolean needsSaving = false;
		Role healthWorkerRole = userService.getRole(Constants.HEALTHWORKER_ROLE);
		if (healthWorkerRole == null) {
			// prep the healthworker role
			healthWorkerRole = new Role();
			healthWorkerRole.setCreator(Context.getAuthenticatedUser());
			healthWorkerRole.setDateCreated(new Date());
			healthWorkerRole.setUuid(UUID.randomUUID().toString());
			needsSaving = true;
		} else {
			needsSaving |= !Constants.HEALTHWORKER_ROLE.equals(healthWorkerRole.getDescription());
			needsSaving |= !Constants.HEALTHWORKER_ROLE.equals(healthWorkerRole.getName());
			needsSaving |= !Constants.HEALTHWORKER_ROLE.equals(healthWorkerRole.getRole());
			needsSaving |= !Boolean.FALSE.equals(healthWorkerRole.getRetired());
		}

		// prepare the healthworker privileges
		final String[] privileges = healthworkerPrivileges();

		// add the privileges to the healthworker role
		for (String privilege : privileges) {
			if (!healthWorkerRole.hasPrivilege(privilege)) {
				final Privilege priv = userService.getPrivilege(privilege);
				if (priv == null) {
					log.error("Privilege does not exist: '" + privilege + "'");
				} else {
					healthWorkerRole.addPrivilege(priv);
					needsSaving = true;
				}
			}
		}

		if (needsSaving) {
			log.info("Saving / Updating '" + Constants.HEALTHWORKER_ROLE + "' role and privileges");

			// update role attributes
			healthWorkerRole.setDescription(Constants.HEALTHWORKER_ROLE);
			healthWorkerRole.setName(Constants.HEALTHWORKER_ROLE);
			healthWorkerRole.setRole(Constants.HEALTHWORKER_ROLE);
			healthWorkerRole.setRetired(Boolean.FALSE);

			// save and cascade privileges
			userService.saveRole(healthWorkerRole);
		}
	}

	public static String[] healthworkerPrivileges() {
		// add privileges to the healthworker role!
		final List<String> privileges = new ArrayList<String>();
		privileges.add("Add Patient Identifiers");
		privileges.add("Add Patient Programs");
		privileges.add("Add Patients");
		privileges.add("Add People");
		privileges.add("Add Encounters");
		privileges.add("Add Orders");
		privileges.add("Add Relationships");
		privileges.add("Delete Relationships");
		privileges.add("Delete Observations");
		privileges.add("Edit Orders");
		privileges.add("Edit Patient Identifiers");
		privileges.add("Edit Patient Programs");
		privileges.add("Edit Patients");
		privileges.add("Edit People");
		privileges.add("Edit Relationships");
		privileges.add("Edit Encounters");
		privileges.add("Patient Dashboard - View Demographics Section");
		privileges.add("Patient Dashboard - View Encounters Section");
		privileges.add("Patient Dashboard - View Forms Section");
		privileges.add("Patient Dashboard - View Overview Section");
		privileges.add("Patient Dashboard - View Patient Summary");
		privileges.add("Patient Dashboard - View Regimen Section");
		privileges.add("Purge Orders");
		privileges.add("Purge Encounters");
		privileges.add("View Concepts");
		privileges.add("View Observations");
		privileges.add("View Orders");
		privileges.add("View Patient Cohorts");
		privileges.add("View Patient Identifiers");
		privileges.add("View Patient Programs");
		privileges.add("View Patients");
		privileges.add("View People");
		privileges.add("View Relationship Types");
		privileges.add("View Relationships");
		privileges.add("View Users");
		privileges.add("View Encounters");
		privileges.add("View Programs");

		// return as an array
		return privileges.toArray(new String[privileges.size()]);
	}

	/**
	 * Sets up the concepts required for proper operation of the CHITS module.
	 */
	protected void setupConcepts(ConceptUtil conceptUtil) {
		// define generic 'Status' values
		for (CachedConceptId status : StatusConcepts.values()) {
			conceptUtil.loadOrCreateConceptAnswer(null, status, status.getConceptName());
		}

		// verify that the civil status is correctly populated
		final Concept civilStatusConcept = conceptUtil.loadOrCreateConceptQuestion(CivilStatusConcepts.CIVIL_STATUS,
				"More detailed description of the following encounter form question: \"Are you currently married or living with a partner?\"");
		conceptUtil.loadOrCreateConceptAnswer(civilStatusConcept, CivilStatusConcepts.SINGLE, "Not Married.");
		conceptUtil.loadOrCreateConceptAnswer(civilStatusConcept, CivilStatusConcepts.MARRIED, "Wedded to another person.");
		conceptUtil.loadOrCreateConceptAnswer(civilStatusConcept, CivilStatusConcepts.SEPARATED, "Separated from partner.");
		conceptUtil.loadOrCreateConceptAnswer(civilStatusConcept, CivilStatusConcepts.LIVE_IN, "Live-in with partner.");
		conceptUtil.loadOrCreateConceptAnswer(civilStatusConcept, CivilStatusConcepts.WIDOWED, "Answer which describes a type of civil status.");

		// define CHITS person type attributes
		conceptUtil.defineAttributeTypeIfMissing(IdAttributes.CHITS_LPIN, "Local Patient Identification Number", null, 10.0);
		conceptUtil.defineAttributeTypeIfMissing(IdAttributes.CHITS_CRN, "Common Reference Number (UMID - Unified Multi-purpose Identification)", null, 15.0);
		conceptUtil.defineAttributeTypeIfMissing(AddressAttributes.CHITS_ADDRESS, "Address", null, 20.0);
		conceptUtil.defineAttributeTypeIfMissing(AddressAttributes.CHITS_BARANGAY, "Barangay", null, 30.0);
		conceptUtil.defineAttributeTypeIfMissing(AddressAttributes.CHITS_CITY, "City", null, 40.0);
		conceptUtil.defineAttributeTypeIfMissing(IdAttributes.CHITS_SSS, "Social Security System Identification Number", null, 50.0);
		conceptUtil.defineAttributeTypeIfMissing(IdAttributes.CHITS_GSIS, "Government Service Insurance System Number", null, 50.0);
		conceptUtil.defineAttributeTypeIfMissing(IdAttributes.CHITS_TIN, "Tax Identification Number", null, 60.0);

		// define philhealth concepts
		conceptUtil.defineAttributeTypeIfMissing(PhilhealthConcepts.CHITS_PHILHEALTH, "PHILHEALTH Number", null, 70.0);
		conceptUtil.defineAttributeTypeIfMissing(PhilhealthConcepts.CHITS_PHILHEALTH_EXPIRATION, "PHILHEALTH Expiration", null, 80.0);

		// verify that the PHILHEALTH sponsor is properly created
		final Concept philhealthSponsorConcept = conceptUtil.loadOrCreateConceptQuestion(PhilhealthSponsorConcepts.CHITS_PHILHEALTH_SPONSOR,
				"Philhealth Sponsor");
		conceptUtil.loadOrCreateConceptAnswer(philhealthSponsorConcept, PhilhealthSponsorConcepts.NATIONAL, "National (CCT beneficiary)");
		conceptUtil.loadOrCreateConceptAnswer(philhealthSponsorConcept, PhilhealthSponsorConcepts.LGU, "LGU");
		conceptUtil.loadOrCreateConceptAnswer(philhealthSponsorConcept, PhilhealthSponsorConcepts.IPP, "IPP (Individual Paying Person)");
		conceptUtil.loadOrCreateConceptAnswer(philhealthSponsorConcept, PhilhealthSponsorConcepts.EMPLOYER, "Employer.");

		// define philhealth sponsor attribute as a 'concept'
		conceptUtil.defineAttributeTypeIfMissing(PhilhealthSponsorConcepts.CHITS_PHILHEALTH_SPONSOR.getConceptName(), "PHILHEALTH Sponsor",
				philhealthSponsorConcept.getId(), 80.0);

		// verify that the anthropometric CHITS concepts uses are properly defined
		conceptUtil.loadOrCreateVitalSignNumericConceptQuestion(VisitConcepts.WEIGHT_KG, "WT", "Patient's weight in kilograms.", 0.0, 250.0, "kg");
		conceptUtil.loadOrCreateVitalSignNumericConceptQuestion(VisitConcepts.HEIGHT_CM, "HT", "Patient's height in centimeters.", 10.0, 228.0, "cm");
		conceptUtil.loadOrCreateVitalSignNumericConceptQuestion(VisitConcepts.HEAD_CIRC_CM, "HC",
				"Measurement of the largest part of the infant's head (just above the eyebrow and ears) used to determine brain growth.", 10.0, 100.0, "cm");
		conceptUtil.loadOrCreateVitalSignNumericConceptQuestion(VisitConcepts.CHEST_CIRC_CM, "CC", "Measurement of the chest circumference of the patient.",
				10.0, 160.0, "cm");
		conceptUtil.loadOrCreateVitalSignNumericConceptQuestion(VisitConcepts.WAIST_CIRC_CM, "WC", "Measurement of the waist circumference of the patient.",
				10.0, 300.0, "cm");
		conceptUtil.loadOrCreateVitalSignNumericConceptQuestion(VisitConcepts.HIP_CIRC_CM, "HC", "Measurement of the hip circumference of the patient.", 10.0,
				300.0, "cm");
		conceptUtil.loadOrCreateUnpreciseVitalSignNumericConceptQuestion(VisitConcepts.DBP, "DBP",
				"A patient's diastolic blood pressure measurement (taken with a manual cuff in either a sitting or standing position ", 0.0, 150.0, "mmHg");
		conceptUtil.loadOrCreateUnpreciseVitalSignNumericConceptQuestion(VisitConcepts.SBP, "SBP",
				"A patient's systolic blood pressure measurement (taken with a manual cuff in either a sitting or standing position)", 0.0, 250.0, "mmHg");
		conceptUtil.loadOrCreateUnpreciseVitalSignNumericConceptQuestion(VisitConcepts.PULSE, "HR",
				"Patient pulse rate, as measured with a peripheral oximeter.", 0.0, 230.0, "rate/min");
		conceptUtil.loadOrCreateUnpreciseVitalSignNumericConceptQuestion(VisitConcepts.RESPIRATORY_RATE, "RR",
				"Measured respiratory rate in breaths per minute.", 5.0, 1000.0, "breaths/min");
		conceptUtil.loadOrCreateNumericConceptQuestion(VisitConcepts.TEMPERATURE_C, "TEMP (C)", "Patient's temperature in degrees centigrade. ", 25.0, 43.0,
				"DEG C");

		// prepare the coded concept question for the 'complaint': The answers to these can be found under the uploaded ICD10 codes
		conceptUtil.loadOrCreateConceptQuestion(VisitConcepts.COMPLAINT, "Patient's chief complaint that prompted visit.");
		conceptUtil.loadOrCreateConceptQuestion(VisitConcepts.DIAGNOSIS, "The diagnosis for the patient's chief complaint that prompted visit.");

		// create convenience sets to define the template concept categories
		conceptUtil.loadOrCreateConvenienceSet(VisitNotesConceptSets.COMPLAINT_NOTES, "Doctor's notes about the patient's complaint.");
		conceptUtil.loadOrCreateConvenienceSet(VisitNotesConceptSets.HISTORY_NOTES, "Notes about the patient's history.");
		conceptUtil.loadOrCreateConvenienceSet(VisitNotesConceptSets.PHYSICAL_EXAM_NOTES, "Notes about the patient's physical exams during the visit.");
		conceptUtil.loadOrCreateConvenienceSet(VisitNotesConceptSets.DIAGNOSIS_NOTES, "The doctor's diagnosis for the patient's complaint.");
		conceptUtil.loadOrCreateConvenienceSet(VisitNotesConceptSets.TREATMENT_NOTES, "The doctor's treatment plan for the patient's complaint.");

		// ensure the viewing list is specified
		conceptUtil.defineGlobalPropertyIfNotSet(OpenmrsConstants.GLOBAL_PROPERTY_PATIENT_LISTING_ATTRIBUTES,
				"Civil Status,CHITS_ADDRESS,CHITS_BARANGAY,CHITS_CITY,CHITS_LPIN,CHITS_CRN,CHITS_PHILHEALTH");
		conceptUtil.defineGlobalPropertyIfNotSet(OpenmrsConstants.GLOBAL_PROPERTY_PATIENT_VIEWING_ATTRIBUTES,
				"Civil Status,CHITS_ADDRESS,CHITS_BARANGAY,CHITS_CITY,CHITS_LPIN,CHITS_CRN,CHITS_PHILHEALTH");

		// make sure concept source is defined for IDC10
		conceptUtil.loadOrCreateConceptSource(ICD10.CONCEPT_SOURCE_NAME, "CHITS ICD10 mappings");

		// a concept used for generating the notes number for a patient's encounter
		conceptUtil.loadOrCreateNumericConceptQuestion(VisitConcepts.NOTES_NUMBER, "Notes #",
				"A number assigned to this patient for this encounter that represents the 'Notes Number'.", 1, Integer.MAX_VALUE, "");

		// setup the child care program
		conceptUtil.defineOrUpdateProgram(ProgramConcepts.CHILDCARE);

		// setup child care concepts
		conceptUtil.loadOrCreateNumericConceptQuestion(ChildCareConcepts.BIRTH_LENGTH, "Birth length (cm)", "Birth length of child (cm)", 15.0, 75.0, "cm");
		conceptUtil.loadOrCreateNumericConceptQuestion(ChildCareConcepts.BIRTH_WEIGHT, "Birth weight (kg)", "Birth weight of child (kg)", 0.25, 12.5, "kg");
		conceptUtil.loadOrCreateConceptQuestion(ChildCareConcepts.DELIVERY_LOCATION, "Delivery location");
		conceptUtil.loadOrCreateNumericConceptQuestion(ChildCareConcepts.GESTATIONAL_AGE, "Gest. Age at Birth", "Gestational Age at Birth (wk)", 20, 60, "wk");
		conceptUtil.loadOrCreateNumericConceptQuestion(ChildCareConcepts.BIRTH_ORDER, "Birth Order", "Birth Order", 1, 15, "#");
		conceptUtil.loadOrCreateNumericConceptQuestion(ChildCareConcepts.BIRTH_ORDER, "Birth Order", "Birth Order", 1, 15, "#");
		conceptUtil.loadOrCreateTextConceptQuestion(ChildCareConcepts.CHILDCARE_REMARKS, "Remarks", "Remarks");
		conceptUtil.loadOrCreateDateConceptQuestion(ChildCareConcepts.DOB_REGISTRATION, "Date of Birth registration", "Date of Birth registration");
		conceptUtil.loadOrCreateConvenienceSet(ChildCareConcepts.DELIVERY_INFORMATION, "Child Care Delivery Information");
		final Concept methodOfDeliveryConcept = conceptUtil.loadOrCreateConceptQuestion(ChildCareConcepts.METHOD_OF_DELIVERY, "Child birth method of delivery");
		for (MethodOfDeliveryConcepts modc : MethodOfDeliveryConcepts.values()) {
			conceptUtil.loadOrCreateConceptAnswer(methodOfDeliveryConcept, modc, modc.getConceptName());
		}

		// Add new born screening results
		final Concept newbornScreeningResults = conceptUtil.loadOrCreateConceptQuestion(NewbornScreeningConcepts.RESULTS, "Newborn Screening Results");
		for (CachedConceptId result : NewbornScreeningResults.values()) {
			conceptUtil.loadOrCreateConceptAnswer(newbornScreeningResults, result, result.getConceptName());
		}

		// Add new born screening information parent concept
		conceptUtil.loadOrCreateConceptQuestion(NewbornScreeningConcepts.SCREENING_INFORMATION, "Newborn Screening Information");
		conceptUtil.loadOrCreateDateConceptQuestion(NewbornScreeningInformation.REPORT_DATE, "Newborn Screening Report Date", "Newborn Screening Report Date");
		conceptUtil.loadOrCreateDateConceptQuestion(NewbornScreeningInformation.SCREENING_DATE, "Newborn Screening Date", "Newborn Screening Date");

		// new born screening information actions
		final Concept newbornScreeningAction = conceptUtil.loadOrCreateConceptQuestion(NewbornScreeningInformation.ACTION, "Newborn Screening Action");
		conceptUtil.pairQuestionAndAnswerIfNeeded(newbornScreeningAction, StatusConcepts.CLOSED);
		conceptUtil.pairQuestionAndAnswerIfNeeded(newbornScreeningAction, StatusConcepts.PENDING);
		conceptUtil.pairQuestionAndAnswerIfNeeded(newbornScreeningAction, StatusConcepts.REFERRED);

		// verify that the occupation concepts are installed
		final Concept occupationConcept = conceptUtil.loadOrCreateConceptQuestion(OccupationConcepts.OCCUPATION, "Occupation");
		for (OccupationConcepts occupation : OccupationConcepts.values()) {
			if (occupation != OccupationConcepts.OCCUPATION) {
				conceptUtil.loadOrCreateConceptAnswer(occupationConcept, occupation, occupation.getConceptName());
			}
		}

		// verify that the education concepts are installed
		final Concept educationConcept = conceptUtil.loadOrCreateConceptQuestion(EducationConcepts.EDUCATION, "Education");
		for (EducationConcepts education : EducationConcepts.values()) {
			if (education != EducationConcepts.EDUCATION) {
				conceptUtil.loadOrCreateConceptAnswer(educationConcept, education, education.getConceptName());
			}
		}

		// define concepts for defining vaccination information
		final Concept childCareVaccination = conceptUtil.loadOrCreateConvenienceSet(VaccinationConcepts.CHILDCARE_VACCINATION, "Child Care Vaccination Record");
		conceptUtil.loadOrCreateDateConceptQuestion(VaccinationConcepts.DATE_ADMINISTERED, "Date Administered", "Date Administered");
		conceptUtil.loadOrCreateConceptQuestion(VaccinationConcepts.ANTIGEN, "Antigen");
		final Concept healthFacilityConcept = conceptUtil.loadOrCreateConceptQuestion(VaccinationConcepts.HEALTH_FACILITY, "Health Facility");

		// define default mandatory vaccines for the child care module
		for (CachedConceptId vaccineConcept : ChildCareVaccinesConcepts.values()) {
			// create a vaccine concept for each vaccine and associate with the CHILDCARE_VACCINATION group
			final Concept antigen = conceptUtil.loadOrCreateConcept(vaccineConcept, vaccineConcept.getConceptName(), vaccineConcept.getConceptName(), //
					"Drug", "Coded");
			conceptUtil.pairSetMemberIfNeeded(childCareVaccination, antigen);
		}

		// define default health facility concepts
		for (CachedConceptId healthFacility : HealthFacilityConcepts.values()) {
			// create a health facility concept for each health facility
			conceptUtil.loadOrCreateConceptAnswer(healthFacilityConcept, healthFacility, healthFacility.getConceptName());
		}

		// childcare services
		conceptUtil.loadOrCreateDateConceptQuestion(VaccinationConcepts.DATE_ADMINISTERED, "Date Administered", "Date Administered");
		conceptUtil.loadOrCreateTextConceptQuestion(ChildCareServicesConcepts.DOSAGE, "Dosage Free Text", "Dosage in free-text form");
		conceptUtil.loadOrCreateTextConceptQuestion(ChildCareServicesConcepts.REMARKS, "Remarks", "Remarks");
		final Concept childCareServiceType = conceptUtil.loadOrCreateConceptQuestion(ChildCareServicesConcepts.CHILDCARE_SERVICE_TYPE,
				"Child Care Service Type");
		for (CachedConceptId serviceType : ChildCareServiceTypes.values()) {
			conceptUtil.loadOrCreateConceptAnswer(childCareServiceType, serviceType, serviceType.getConceptName());
		}

		// breastfeeding concepts
		final Concept breastFedInfo = conceptUtil.loadOrCreateConvenienceSet(BreastFeedingConcepts.BREASTFEEDING_INFO,
				BreastFeedingConcepts.BREASTFEEDING_INFO.getConceptName());
		conceptUtil.pairSetMemberIfNeeded(breastFedInfo,
				conceptUtil.loadOrCreateConcept(BreastFeedingConcepts.M1, "M1", "month 1, exclusive breastfeeding", "Question", "Boolean"));
		conceptUtil.pairSetMemberIfNeeded(breastFedInfo,
				conceptUtil.loadOrCreateConcept(BreastFeedingConcepts.M2, "M2", "month 2, exclusive breastfeeding", "Question", "Boolean"));
		conceptUtil.pairSetMemberIfNeeded(breastFedInfo,
				conceptUtil.loadOrCreateConcept(BreastFeedingConcepts.M3, "M3", "month 3, exclusive breastfeeding", "Question", "Boolean"));
		conceptUtil.pairSetMemberIfNeeded(breastFedInfo,
				conceptUtil.loadOrCreateConcept(BreastFeedingConcepts.M4, "M4", "month 4, exclusive breastfeeding", "Question", "Boolean"));
		conceptUtil.pairSetMemberIfNeeded(breastFedInfo,
				conceptUtil.loadOrCreateConcept(BreastFeedingConcepts.M5, "M5", "month 5, exclusive breastfeeding", "Question", "Boolean"));
		conceptUtil.pairSetMemberIfNeeded(breastFedInfo,
				conceptUtil.loadOrCreateConcept(BreastFeedingConcepts.M6, "M6", "month 6, exclusive breastfeeding", "Question", "Boolean"));

		// define miscellaneous person attributes
		conceptUtil.defineAttributeTypeIfMissing(MiscAttributes.NUMBER_OF_PREGNANCIES, "Number of pregnancies", null, 100.0);
		conceptUtil.defineAttributeTypeIfMissing(MiscAttributes.OCCUPATION, "Occupation", occupationConcept.getId(), 110.0);
		conceptUtil.defineAttributeTypeIfMissing(MiscAttributes.EDUCATION, "Educational Attainment", educationConcept.getId(), 120.0);
	}

	@Autowired
	public void setConceptUtilFactory(ConceptUtilFactory conceptUtilFactory) {
		this.conceptUtilFactory = conceptUtilFactory;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
