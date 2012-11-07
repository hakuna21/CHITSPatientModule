package org.openmrs.module.chits.web.controller.admin;

import javax.servlet.http.HttpSession;

import org.openmrs.Concept;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.UploadFileForm;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Validates the concept dictionary checking for errors.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/concepts/validateConcepts.form")
public class ValidateConceptsController extends UploadConceptsController {
	@Override
	protected String getInputPage() {
		return "/module/chits/admin/concepts/uploadForValidation";
	}

	@Override
	protected String getSuccessPage() {
		return "redirect:/module/chits/admin/concepts/validateConcepts.form";
	}

	@Override
	protected void setupSuccessMessage(HttpSession httpSession, UploadFileForm form, Counters counters) {
		final String desc = "<strong>Skipped Rows: </strong>" + counters.skippedRows //
				+ ", <strong>Unique UUIDs: </strong>" + counters.uuidConceptNames.size() //
				+ ", <strong>Duplicates Concepts: </strong>" + counters.duplicateConcepts //
				+ ", <string>Re-used UUIDs: </strong>" + counters.reusedUUIDs;

		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.concepts.validate.success");
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { desc });
	}

	@Override
	protected Concept processConcept(ConceptUtil conceptUtil, Counters counters, String name, String uuid, String[] synonyms, String shortName,
			String description, String className, String datatypeName, String[] containedInSets, String[] answers, String numericBounds) {
		// no need to return anything
		return null;
	}
}
