package org.openmrs.module.chits.web.patch;

import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.VaccinationConcepts;
import org.openmrs.module.chits.patch.BasePatch;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.util.PrivilegeConstants;

/**
 * Updates the defined child care vaccinations.
 * 
 * @author Bren
 */
public class VaccineDefinitionsPatch extends BasePatch {
	/** The patch ID for this patch */
	private static final String PATCH_ID = "cc.vaccines.1.0";

	/** Concept util for managing concepts */
	private final ConceptUtil conceptUtil;

	public VaccineDefinitionsPatch(AdministrationService adminService, ConceptUtil conceptUtil) {
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
		this.conceptUtil = conceptUtil;
	}

	@Override
	protected String getPatchId() {
		return PATCH_ID;
	}

	@Override
	protected void applyPatchImpl() {
		// check if the current vaccination concepts are available
		Concept bcg = conceptUtil.loadOrCreateConvenienceSet("BCG (at birth) [EPI]", "data set which contains concepts related to BCG");
		Concept hep1 = conceptUtil.loadOrCreateConvenienceSet("Hepatitis B 1 (at birth) [EPI]", "data set which contains concepts related to HBV1");
		Concept hep2 = conceptUtil.loadOrCreateConvenienceSet("Hepatitis B 2 (6 wks) [EPI]", "data set which contains concepts related to HBV2");
		Concept hep3 = conceptUtil.loadOrCreateConvenienceSet("Hepatitis B 3 (14 wks) [EPI]", "data set which contains concepts related to HBV3");
		Concept dpt1 = conceptUtil.loadOrCreateConvenienceSet("DPT 1 (6 wks) [EPI]", "data set which contains concepts related to DPT1");
		Concept dpt2 = conceptUtil.loadOrCreateConvenienceSet("DPT 2 (10 wks) [EPI]", "data set which contains concepts related to DPT2");
		Concept dpt3 = conceptUtil.loadOrCreateConvenienceSet("DPT 3 (14 wks) [EPI]", "data set which contains concepts related to DPT3");
		Concept opv1 = conceptUtil.loadOrCreateConvenienceSet("OPV 1 (6 wks) [EPI]", "data set which contains concepts related to OPV1");
		Concept opv2 = conceptUtil.loadOrCreateConvenienceSet("OPV 2 (10 wks) [EPI]", "data set which contains concepts related to OPV2");
		Concept opv3 = conceptUtil.loadOrCreateConvenienceSet("OPV 3 (14 wks) [EPI]", "data set which contains concepts related to OPV3");
		Concept measles = conceptUtil.loadOrCreateConvenienceSet("Measles (9 mos) [EPI]", "data set which contains concepts related to MV");
		Concept others = conceptUtil.loadOrCreateConvenienceSet("Other Antigens Given",
				"data set which contains concepts related to other vaccines / antigens given ");

		// update membership in childcare vaccination set
		Concept vaccinations = Functions.concept(VaccinationConcepts.CHILDCARE_VACCINATION);

		// remove all current members
		vaccinations.getConceptSets().clear();

		// pair all of the members in proper order
		conceptUtil.pairSetMemberIfNeeded(vaccinations, bcg);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, hep1);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, hep2);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, hep3);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, dpt1);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, dpt2);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, dpt3);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, opv1);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, opv2);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, opv3);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, measles);
		conceptUtil.pairSetMemberIfNeeded(vaccinations, others);
	}
}