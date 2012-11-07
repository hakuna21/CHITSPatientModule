package org.openmrs.module.chits.web.patch;

import org.openmrs.api.AdministrationService;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.Constants.AuditConcepts;
import org.openmrs.module.chits.patch.BasePatch;
import org.openmrs.util.PrivilegeConstants;

/**
 * Defines the audit concepts.
 * 
 * @author Bren
 */
public class AuditConceptsPatch extends BasePatch {
	/** The patch ID for this patch */
	private static final String PATCH_ID = "audit.concepts.1.0";

	/** Concept util for managing concepts */
	private final ConceptUtil conceptUtil;

	public AuditConceptsPatch(AdministrationService adminService, ConceptUtil conceptUtil) {
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
		// define audit concepts
		conceptUtil.loadOrCreateDateConceptQuestion(AuditConcepts.CREATED_BY);
		conceptUtil.loadOrCreateDateConceptQuestion(AuditConcepts.MODIFIED_BY);
	}
}