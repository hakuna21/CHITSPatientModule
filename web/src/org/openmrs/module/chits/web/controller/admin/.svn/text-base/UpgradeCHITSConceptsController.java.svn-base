package org.openmrs.module.chits.web.controller.admin;

import org.openmrs.Concept;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServiceSourceConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServicesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.FerrousSulfateServiceConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningUtil;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * CHITS Concepts upgrade controller.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/upgradeChitsConcepts")
public class UpgradeCHITSConceptsController extends InstallCHITSConceptsController implements Constants {
	/** Version used to check against the 'chits.concepts.version' global property to determine if this controller should be run during startup. */
	public static String VERSION = "1.1.12";

	/**
	 * Upgrades the concepts required for proper operation of the CHITS module.
	 */
	protected void setupConcepts(ConceptUtil conceptUtil) {
		// ensure roles are properly setup
		setupRoles();

		// define attribute for indicating if a patient record is not actually a 'patient'.
		conceptUtil.defineAttributeTypeIfMissing(MiscAttributes.NON_PATIENT, "Not a patient", null, 130.0);

		// define the 'must see physician' attribute
		conceptUtil.defineAttributeTypeIfMissing(MiscAttributes.SEE_PHYSICIAN, "Must See Physician", null, 140.0);

		// define attribute flag for '4Ps'.
		conceptUtil.defineAttributeTypeIfMissing(MiscAttributes.FOUR_PS, "4Ps", null, 150.0);

		// define phone number attributes
		conceptUtil.defineAttributeTypeIfMissing(PhoneAttributes.LANDLINE_NUMBER, "Telephone Number", null, 160.0);
		conceptUtil.defineAttributeTypeIfMissing(PhoneAttributes.MOBILE_NUMBER, "Mobile Number", null, 170.0);
		conceptUtil.defineAttributeTypeIfMissing(IdAttributes.LOCAL_ID, "Local ID", null, 180.0);

		// define attributes for 'created on' and 'last modified from' (for example, for storing the BB device ID)
		conceptUtil.defineAttributeTypeIfMissing(MiscAttributes.CREATED_ON, "Created On", null, 190.0);
		conceptUtil.defineAttributeTypeIfMissing(MiscAttributes.LAST_MODIFIED_ON, "Last Modified From", null, 200.0);

		// FIX configuration of 'Waist' since it was not defined as 'Numeric' in the concept dictionary file!
		conceptUtil.loadOrCreateNumericConceptQuestion(VisitConcepts.WAIST_CIRC_CM, "wc (cm)",
				"numeric input of the measurement around the body at the level of the ABDOMEN "
						+ "and just above the hip bone. The measurement is usually taken immediately after exhalation.", 10.0, 300.0, "cm");

		// add additional philhealth sponsors
		upgradePhilhealthSponsors(conceptUtil);

		// define child care service source options
		upgradeChildCareServiceSource(conceptUtil);

		// setup ferrous sulfate concepts that were not defined in the original concept dictionary
		upgradeFerrousSulfateConcepts(conceptUtil);

		// setup the maternal care concepts
		installMaternalCareProgramAndConcepts(conceptUtil);

		// setup the family planning concepts
		installFamilyPlanningProgramAndConcepts(conceptUtil);

		// setup the 'consult start / consult end' concepts
		// a concept used for generating the notes number for a patient's encounter
		conceptUtil.loadOrCreateDateConceptQuestion(VisitConcepts.CONSULT_START, "Consult Start", "The timestamp of the start of the consult");
		conceptUtil.loadOrCreateDateConceptQuestion(VisitConcepts.CONSULT_END, "Consult End", "The timestamp of the end of the consult");
	}

	/**
	 * Defines new sponsors and adds them to the philhealth sponsor answer set.
	 * 
	 * @param conceptUtil
	 */
	private void upgradePhilhealthSponsors(ConceptUtil conceptUtil) {
		final Concept philhealthSponsorConcept = conceptUtil.loadOrCreateConceptQuestion(PhilhealthSponsorConcepts.CHITS_PHILHEALTH_SPONSOR,
				"Philhealth Sponsor");
		final String[] additionalSponsors = new String[] { "BARANGAY", "MUNICIPALITY/CITY", "PROVINCE", "REGION", "NATIONAL", "IPP", "EMPLOYER", "OTHERS" };

		// define each new sponsor
		for (String sponsor : additionalSponsors) {
			conceptUtil.loadOrCreateConceptAnswer(philhealthSponsorConcept, sponsor, sponsor);
		}
	}

	/**
	 * Defines new child care service source answer set.
	 * 
	 * @param conceptUtil
	 */
	private void upgradeChildCareServiceSource(ConceptUtil conceptUtil) {
		final Concept childCareServiceSourceConcept = conceptUtil.loadOrCreateConceptQuestion(ChildCareServicesConcepts.SERVICE_SOURCE,
				"Child Care Service Source");

		// define and attach service source concept answers
		for (CachedConceptId serviceSource : ChildCareServiceSourceConcepts.values()) {
			// create a service source concept and attach to service source concept as an answer
			conceptUtil.loadOrCreateConceptAnswer(childCareServiceSourceConcept, serviceSource, serviceSource.getConceptName());
		}
	}

	/**
	 * Setup ferrous sulfate concepts that were not defined in the original concept dictionary
	 * 
	 * @param conceptUtil
	 */
	private void upgradeFerrousSulfateConcepts(ConceptUtil conceptUtil) {
		// define and attach ferrous sulfate medication concept answers
		final String[] ironDrops = new String[] { "Iron drops 15mg/0.6mL, 30mL-bottle", "Iron syrup 30mg/15mL, 120mL-bottle" };
		final Concept ferrousSulfateMedication = conceptUtil.loadOrCreateConceptQuestion(FerrousSulfateServiceConcepts.MEDICATION);
		for (String medication : ironDrops) {
			final Concept answer = conceptUtil.loadOrCreateConceptAnswer(ferrousSulfateMedication, medication, medication);
			conceptUtil.pairSetMemberIfNeeded(ferrousSulfateMedication, answer);
		}

		// define and attach ferrous sulfate remarks
		final String[] ironRemarks = new String[] { "non-routine", "therapeutic" };
		final Concept ferrousSulfateRemarks = conceptUtil.loadOrCreateConceptQuestion(FerrousSulfateServiceConcepts.REMARKS);
		for (String remarks : ironRemarks) {
			final Concept answer = conceptUtil.loadOrCreateConceptAnswer(ferrousSulfateRemarks, remarks, remarks);
			conceptUtil.pairSetMemberIfNeeded(ferrousSulfateRemarks, answer);
		}
	}

	/**
	 * Installs and sets up the family planning program and related concepts.
	 */
	private void installFamilyPlanningProgramAndConcepts(ConceptUtil conceptUtil) {
		// define or update the family planning concepts
		FamilyPlanningUtil.defineOrUpdateFamilyPlanningConcepts(conceptUtil);
	}

	/**
	 * Installs and sets up the maternal care program and related concepts.
	 */
	private void installMaternalCareProgramAndConcepts(ConceptUtil conceptUtil) {
		// setup the maternal care program concepts
		MaternalCareUtil.defineOrUpdateMaternalCareConcepts(conceptUtil);
	}
}
