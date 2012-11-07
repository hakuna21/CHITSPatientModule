package org.openmrs.module.chits.web.patch;

import java.io.File;
import java.util.HashMap;

import javax.servlet.ServletContext;

import org.openmrs.api.AdministrationService;
import org.openmrs.module.chits.UploadFileForm;
import org.openmrs.module.chits.patch.BasePatch;
import org.openmrs.module.chits.web.DummyHttpSession;
import org.openmrs.module.chits.web.LocalMultipartZipFile;
import org.openmrs.module.chits.web.controller.admin.UploadConceptsController;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

/**
 * Patches the concept dictionary.
 * 
 * @author Bren
 */
public class ConceptDictionary20120702Patch extends BasePatch {
	/** Concept dictionary patch ID */
	public static final String PATCH_ID = "cd-20120702";

	/** pointer to the patch file relative to context root */
	private final static String REL_PATCH_FILE = "WEB-INF/view/module/chits/ConceptDictionary20120702.csv";

	/** Controller to use for uploading the packaged concept dictionary */
	private final UploadConceptsController uploadConceptsController;

	/** Patch file location */
	private final File patchFile;

	public ConceptDictionary20120702Patch(AdministrationService adminService, ServletContext servletContext, UploadConceptsController uploadConceptsController) {
		super(adminService, //
				PrivilegeConstants.ADD_PERSONS, //
				PrivilegeConstants.VIEW_ADMIN_FUNCTIONS, //
				PrivilegeConstants.VIEW_PERSON_ATTRIBUTE_TYPES, //
				PrivilegeConstants.VIEW_PROGRAMS, //
				PrivilegeConstants.VIEW_USERS, //
				PrivilegeConstants.MANAGE_PERSON_ATTRIBUTE_TYPES, //
				PrivilegeConstants.MANAGE_ROLES, //
				PrivilegeConstants.MANAGE_GLOBAL_PROPERTIES, //
				PrivilegeConstants.MANAGE_CONCEPTS, //
				PrivilegeConstants.MANAGE_CONCEPT_SOURCES, //
				PrivilegeConstants.MANAGE_CONCEPT_CLASSES, //
				PrivilegeConstants.MANAGE_CONCEPT_DATATYPES, //
				PrivilegeConstants.MANAGE_PROGRAMS, //
				PrivilegeConstants.VIEW_CONCEPTS, //
				PrivilegeConstants.VIEW_CONCEPT_SOURCES, //
				PrivilegeConstants.VIEW_CONCEPT_CLASSES, //
				PrivilegeConstants.VIEW_CONCEPT_DATATYPES, //
				PrivilegeConstants.VIEW_OBS);
		this.uploadConceptsController = uploadConceptsController;
		this.patchFile = new File(new File(servletContext.getRealPath("/")), REL_PATCH_FILE);
	}

	@Override
	protected String getPatchId() {
		return PATCH_ID;
	}

	@Override
	protected void applyPatchImpl() {
		// prepare a pseudo UploadFile bean pointing to the packaged concept dictionary of this patch
		final UploadFileForm form = new UploadFileForm();
		form.setFile(new LocalMultipartZipFile(patchFile));

		// prepare the results
		@SuppressWarnings("rawtypes")
		final BindingResult errors = new MapBindingResult(new HashMap(), "errors");
		uploadConceptsController.handleSubmission(new DummyHttpSession(), form, errors);
		log.info("Binding results: " + errors);
	}
}
