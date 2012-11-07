package org.openmrs.module.chits.web.controller.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.UploadFileForm;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Validates the concept dictionary checking for errors.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/concepts/validateAnswersAndSets.form")
public class ValidateAnswersAndSetsController extends UploadConceptsController {
	@Override
	protected String getInputPage() {
		return "/module/chits/admin/concepts/uploadForValidationOfAnswersAndSets";
	}

	@Override
	protected String getSuccessPage() {
		return "redirect:/module/chits/admin/concepts/validateAnswersAndSets.form";
	}

	@Override
	protected void setupSuccessMessage(HttpSession httpSession, UploadFileForm form, Counters counters) {
		final String desc = "<strong>Missing Concepts: </strong>" + counters.missingConcepts //
				+ ", <strong>Superfluous Answers: </strong>" + counters.extraAnswers //
				+ ", <strong>Unlinked Answers: </strong>" + counters.unlinkedAnswers //
				+ ", <strong>Unlinked set members: </strong>" + counters.unlinkedSetMembers;

		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.concepts.validate.answers.and.sets.success");
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { desc });
	}

	@Override
	protected Concept processConcept(ConceptUtil conceptUtil, Counters counters, String name, String uuid, String[] synonyms, String shortName,
			String description, String className, String datatypeName, String[] containedInSets, String[] answers, String numericBounds) {
		Concept c = Functions.conceptByIdOrName(name);
		if (c == null) {
			log.warn("Missing concept: " + name);
			counters.missingConcepts++;
		} else {
			if (answers != null && answers.length > 0) {
				final List<Concept> actualAnswers = new ArrayList<Concept>();
				for (ConceptAnswer ca : c.getAnswers()) {
					actualAnswers.add(ca.getAnswerConcept());
				}

				for (String answer : answers) {
					final Concept answerConcept = Functions.conceptByIdOrName(answer);
					if (answerConcept == null) {
						log.warn("Missing concept answer: " + answer);
						counters.missingConcepts++;
					} else {
						if (!actualAnswers.remove(answerConcept)) {
							log.warn("Unlinked concept answer for " + name + ": " + answer);
							counters.unlinkedAnswers++;
						}
					}
				}

				// any extra concept answers?
				for (Concept extraAnswer : actualAnswers) {
					log.warn("Unexpected answer for " + name + ": " + extraAnswer.getName());
					counters.extraAnswers++;
				}
			}

			if (containedInSets != null && containedInSets.length > 0) {
				for (String set : containedInSets) {
					final Concept setConcept = Functions.conceptByIdOrName(set);
					if (setConcept == null) {
						log.warn("Missing concept set: " + set);
						counters.missingConcepts++;
					} else {
						if (!setConcept.getSetMembers().contains(c)) {
							log.warn("Unlinked concept set member for " + set + ": " + name);
							counters.unlinkedSetMembers++;
						}
					}
				}
			}
		}

		// no need to return anything
		return null;
	}
}
