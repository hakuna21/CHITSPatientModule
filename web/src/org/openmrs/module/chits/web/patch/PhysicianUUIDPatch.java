package org.openmrs.module.chits.web.patch;

import java.util.UUID;

import org.openmrs.Concept;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.chits.patch.BasePatch;
import org.openmrs.util.PrivilegeConstants;

/**
 * Fixes the problem where 'clinical officer/doctor' used to have a synonym of 'physician' (now removed): UUID 1574AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA should be
 * mapped to 'clinical officer/doctor' but since it was previously a synonym of 'physician', the 'physician' concept (which was defined first) was given that
 * UUID.
 * <p>
 * This fix creates a random UUID for 'physician' so that the UUID of 1574AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA can be assigned to 'clinical officer/doctor', hence,
 * this patch MUST be performed BEFORE the concept dictionary upgrade.
 * 
 * @author Bren
 */
public class PhysicianUUIDPatch extends BasePatch {
	/** The patch ID for this patch */
	private static final String PATCH_ID = "physician.uuid.1.0";

	/** Concept service for managing concepts */
	private final ConceptService conceptService;

	public PhysicianUUIDPatch(AdministrationService adminService, ConceptService conceptService) {
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
	}

	@Override
	protected String getPatchId() {
		return PATCH_ID;
	}

	@Override
	protected void applyPatchImpl() {
		// load the concept with UUID '1574AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
		final Concept physicianConcept = conceptService.getConceptByUuid("1574AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

		// randomize the UUID so that it can be used by the 'clinical officer/doctor' name in the concept dictionary
		physicianConcept.setUuid(UUID.randomUUID().toString());

		// save it!
		conceptService.saveConcept(physicianConcept);
	}
}