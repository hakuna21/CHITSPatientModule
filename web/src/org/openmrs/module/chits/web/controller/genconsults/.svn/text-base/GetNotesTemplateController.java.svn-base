package org.openmrs.module.chits.web.controller.genconsults;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.chits.CHITSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Get the corresponding template notes for the given conceptId.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/getNotesTemplate.form")
public class GetNotesTemplateController {
	/** Auto-wired concept service */
	protected ConceptService conceptService;

	/** Auto-wired CHITS service */
	protected CHITSService chitsService;

	/**
	 * This method will display the patient form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(ModelMap model, //
			@RequestParam(required = false, value = "conceptId") Integer conceptId) {
		final Concept concept = conceptService.getConcept(conceptId);
		if (concept != null) {
			final String uuid = concept.getUuid();
			final SerializedObject serializedObject = chitsService.getSerializedObjectByUuid(uuid);
			if (serializedObject != null) {
				model.put("serializedObject", serializedObject);
			}
		}

		// return the notes as an ajax response
		return "/module/chits/consults/ajaxNotesTemplate";
	}

	@Autowired
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}
}
