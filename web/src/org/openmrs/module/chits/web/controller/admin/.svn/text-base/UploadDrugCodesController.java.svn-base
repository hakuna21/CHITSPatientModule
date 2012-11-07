package org.openmrs.module.chits.web.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import liquibase.csv.CSVReader;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CSVUtil;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.UploadFileForm;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Upload drug codes from a CSV file.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/drugs/uploadDrugCodes.form")
public class UploadDrugCodesController extends UploadConceptsController implements Constants {
	@Override
	protected String getInputPage() {
		return "/module/chits/admin/drugs/upload";
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			@ModelAttribute("form") UploadFileForm form, //
			BindingResult errors) {
		// reset counters
		final Counters counters = new Counters();

		// was a file uploaded?
		if (form.getFile() == null || form.getFile().isEmpty() || form.getFile().getOriginalFilename() == null) {
			errors.rejectValue("file", "chits.admin.drugs.error.csv.file.required");
		} else {
			InputStream source = null;
			try {
				// open an input stream
				source = form.getFile().getInputStream();

				// process the drugs file
				final CSVReader reader = new CSVReader(new InputStreamReader(source));

				// verify the header part: expecting at least: "Name", "SIGN/SYMPTOMS" or "DIAGNOSIS"
				int rowNumber = 1;

				// setup CSV utility for the header
				final CSVUtil csvUtil = new CSVUtil(reader.readNext());

				// validate header
				if (!csvUtil.containsHeader("Name")) {
					// header is either missing or not valid!
					errors.rejectValue("file", "chits.admin.drugs.format.error", //
							new Object[] { form.getFile().getOriginalFilename() }, //
							"chits.admin.drugs.format.error");

					// send back to input page upon error
					return getInputPage();
				}

				while (csvUtil.nextRow(reader.readNext()) != null) {
					rowNumber++;

					// process the next row
					try {
						processRow(rowNumber, csvUtil, counters);
					} catch (Exception ex) {
						log.error("Error processing row# " + rowNumber, ex);
						counters.errors++;
					}

					// update session information about processed data
					updateSessionData(httpSession, counters, rowNumber);
				}
			} catch (IOException ioe) {
				// add error message
				log.error("Error processing drug codes", ioe);
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, new Object[] { ioe.getMessage() });
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.admin.drugs.uploaded.error");

				// send back to input page upon error
				return getInputPage();
			} finally {
				if (source != null) {
					try {
						// cleanup!
						source.close();
					} catch (IOException ioe) {
						log.warn("Error closing uploaded file source", ioe);
					}
				}
			}
		}

		if (errors.hasErrors()) {
			// send back to input page upon error
			return getInputPage();
		}

		// add success message
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { form.getFile().getOriginalFilename(), counters.added, counters.updated,
				counters.unchanged, counters.skipped, counters.errors, counters.duplicates, counters.duplicateConcepts });
		if (counters.errors == 0 && counters.duplicates == 0) {
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.drugs.uploaded.success");
		} else {
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.drugs.uploaded.success.with.warnings");
		}

		// redirect after successful submission
		return "redirect:/module/chits/admin/drugs/uploadDrugCodes.form";
	}

	@Override
	protected Concept processConcept(ConceptUtil conceptUtil, Counters counters, String name, String uuid, String[] synonyms, String shortName,
			String description, String className, String datatypeName, String[] containedInSets, String[] answers, String numericBounds) {
		// process the concept as usual
		final Concept drugConcept = super.processConcept(conceptUtil, counters, name, uuid, synonyms, shortName, description, "Drug", "Coded", containedInSets,
				answers, numericBounds);

		// check if a "Drug" record has to be created
		final List<Drug> drugs = conceptService.getDrugsByConcept(drugConcept);
		if (drugs.isEmpty()) {
			// drug not defined for this concept, so create one
			createDrug(name, drugConcept);
			counters.updated++;
		}

		// return the created / updated concept
		return drugConcept;
	}

	/**
	 * Associates a "Drug" record with a concept.
	 * 
	 * @param name
	 * @param drugConcept
	 */
	private void createDrug(String name, Concept drugConcept) {
		final Drug drug = new Drug();
		drug.setConcept(drugConcept);
		drug.setName(StringUtils.substring(name, 0, 50));
		drug.setCombination(Boolean.FALSE);
		drug.setCreator(Context.getAuthenticatedUser());
		drug.setDateCreated(new Date());
		drug.setRetired(Boolean.FALSE);
		drug.setUuid(UUID.randomUUID().toString());
		conceptService.saveDrug(drug);
	}
}
