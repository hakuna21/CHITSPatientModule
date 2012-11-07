package org.openmrs.module.chits.web.patch;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPregnancyOutcomeConcepts;
import org.openmrs.module.chits.patch.BasePatch;
import org.openmrs.util.PrivilegeConstants;

/**
 * Updates the defined child care vaccinations.
 * 
 * @author Bren
 */
public class MaternalCareConceptsPatch extends BasePatch {
	/** The patch ID for this patch */
	private static final String PATCH_ID = "mc.concepts.1.0";

	/** Concept util for managing concepts */
	private final ConceptUtil conceptUtil;

	/** Concept service */
	private final ConceptService conceptService;

	public MaternalCareConceptsPatch(AdministrationService adminService, ConceptService conceptService, ConceptUtil conceptUtil) {
		super(adminService, //
				PrivilegeConstants.MANAGE_GLOBAL_PROPERTIES, //
				PrivilegeConstants.MANAGE_CONCEPTS, //
				PrivilegeConstants.MANAGE_CONCEPT_SOURCES, //
				PrivilegeConstants.MANAGE_CONCEPT_CLASSES, //
				PrivilegeConstants.MANAGE_CONCEPT_DATATYPES, //
				PrivilegeConstants.VIEW_ADMIN_FUNCTIONS, //
				PrivilegeConstants.VIEW_CONCEPTS, //
				PrivilegeConstants.VIEW_CONCEPT_CLASSES, //
				PrivilegeConstants.VIEW_CONCEPT_DATATYPES, //
				PrivilegeConstants.VIEW_CONCEPT_SOURCES, //
				PrivilegeConstants.VIEW_USERS, //
				PrivilegeConstants.VIEW_OBS);
		this.conceptService = conceptService;
		this.conceptUtil = conceptUtil;
	}

	@Override
	protected String getPatchId() {
		return PATCH_ID;
	}

	@Override
	protected void applyPatchImpl() {
		// add short names to certain concepts
		setShortName("Vaginal Bleeding, symptom", "Vaginal Bleeding");

		final Concept deliveryAssistantAnswers = conceptUtil.loadOrCreateConvenienceSet(MCObstetricHistoryDetailsConcepts.DELIVERY_ASSISTANT_ANSWERS,
				"Delivery Assistant Answers");
		conceptUtil.loadOrCreateConceptAnswer(deliveryAssistantAnswers, "Doctor", "Doctor");
		conceptUtil.loadOrCreateConceptAnswer(deliveryAssistantAnswers, "Nurse", "Nurse");
		conceptUtil.loadOrCreateConceptAnswer(deliveryAssistantAnswers, "Midwife", "Midwife");
		conceptUtil.loadOrCreateConceptAnswer(deliveryAssistantAnswers, "Traditional Birth Attendant", "Traditional Birth Attendant");
		conceptUtil.loadOrCreateConceptAnswer(deliveryAssistantAnswers, "Others", "Others");

		final Concept termAnswers = conceptUtil.loadOrCreateConvenienceSet(MCPregnancyOutcomeConcepts.TERM_ANSWERS, "Pregnancy Term Answers");
		conceptUtil.loadOrCreateConceptAnswer(termAnswers, "Fulltrem", "Fulltrem");
		conceptUtil.loadOrCreateConceptAnswer(termAnswers, "Preterm", "Preterm");
		conceptUtil.loadOrCreateConceptAnswer(termAnswers, "Postterm", "Postterm");

		final Concept methodAnswers = conceptUtil.loadOrCreateConvenienceSet(MCPregnancyOutcomeConcepts.METHOD_ANSWERS, "Pregnancy Method Answers");
		conceptUtil.loadOrCreateConceptAnswer(methodAnswers, "normal spontaneous delivery", "normal spontaneous delivery");
		conceptUtil.loadOrCreateConceptAnswer(methodAnswers, "assisted vaginal delivery", "assisted vaginal delivery");
		conceptUtil.loadOrCreateConceptAnswer(methodAnswers, "breech delivery", "breech delivery");
		conceptUtil.loadOrCreateConceptAnswer(methodAnswers, "cesarean section", "cesarean section");

		final Concept outcomeAnswers = conceptUtil.loadOrCreateConvenienceSet(MCPregnancyOutcomeConcepts.OUTCOME_ANSWERS, "Pregnancy Outcome Answers");
		conceptUtil.loadOrCreateConceptAnswer(outcomeAnswers, "Livebirth", "Livebirth");
		conceptUtil.loadOrCreateConceptAnswer(outcomeAnswers, "Stillbirth", "Stillbirth");
		conceptUtil.loadOrCreateConceptAnswer(outcomeAnswers, "Abortion", "Abortion");

		final Concept sexOfBabyAnswers = conceptUtil.loadOrCreateConvenienceSet(MCPregnancyOutcomeConcepts.SEX_ANSWERS, "Sex of Baby Answers");
		conceptUtil.loadOrCreateConceptAnswer(sexOfBabyAnswers, "Male", "Male");
		conceptUtil.loadOrCreateConceptAnswer(sexOfBabyAnswers, "Female", "Female");
		conceptUtil.loadOrCreateConceptAnswer(sexOfBabyAnswers, "Unknown", "Unknown");

		// setup short names
		setShortName("basal body temperature (NFP-BBP)", "NFP-BBP");
		setShortName("cervical mucus method (NFP-CM)", "NFP-CM");
		setShortName("lactational amenorrhea (NFP-LAM)", "NFP-LAM");
		setShortName("standard days method (NFP-SDM)", "NFP-SDM");
		setShortName("sympothermal method (NFP-STM)", "NFP-STM");
		setShortName("contraceptive pills (PILLS)", "pills");
		setShortName("condom (CON)", "condom");
		setShortName("injectables (INJ)", "inj");
		setShortName("intra-uterine device (IUD)", "IUD");
		setShortName("tubal ligation (FSTR/BTL)", "FSTR/BTL");
		setShortName("vasectomy (MSTR/VASECTOMY)", "MSTR/VASECTOMY");
	}

	/**
	 * Sets theshort name of the given concept
	 * 
	 * @param conceptName
	 * @param shortName
	 */
	private void setShortName(String conceptName, String shortName) {
		final Concept concept = conceptService.getConceptByName(conceptName);

		final Date now = new Date();
		final ConceptName shortNameConcept = new ConceptName();
		shortNameConcept.setConcept(concept);
		shortNameConcept.setConceptNameType(ConceptNameType.SHORT);
		shortNameConcept.setName(shortName);
		shortNameConcept.setLocale(Constants.ENGLISH);
		shortNameConcept.setLocalePreferred(Boolean.FALSE);
		shortNameConcept.setVoided(Boolean.FALSE);
		shortNameConcept.setTags(new HashSet<ConceptNameTag>());
		shortNameConcept.setCreator(conceptUtil.getUserForAudit());
		shortNameConcept.setDateCreated(now);
		shortNameConcept.setUuid(UUID.randomUUID().toString());

		// attach and save the short name
		concept.setShortName(shortNameConcept);
		conceptService.saveConcept(concept);
	}
}