package org.openmrs.module.chits.web.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import liquibase.csv.CSVReader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.CSVUtil;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.ConceptUtilFactory;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.UploadFileForm;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Upload ICD10 codes from a CSV file.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/icd10/uploadICD10Codes.form")
public class UploadICD10CodesController implements Constants {
	/** ICD10 CSV header for ICD10 diagnosis codes */
	private static final String DIAGNOSIS = "DIAGNOSIS";

	/** ICD10 CSV header for ICD10 symptom codes */
	private static final String SIGN_SYMPTOMS = "SIGN/SYMPTOMS";

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the Patient service */
	protected ConceptService conceptService;

	/** The 'Concept Source' entity bean representing the 'ICD10' mappings */
	protected ConceptSource icd10ConceptSource;

	/** Utility class for managing concepts */
	protected ConceptUtilFactory conceptUtilFactory;

	/** CHITS service for force-saving concepts with changed datatype */
	protected CHITSService chitsService;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public Object formBackingObject() throws ServletException {
		return new UploadFileForm();
	}

	/**
	 * This method will start the patient's consult.
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm() {
		// send back to the patient consult page
		return "/module/chits/admin/icd10/upload";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") UploadFileForm form, //
			BindingResult errors) {
		// reset counters
		final Counters counters = new Counters();

		// was a file uploaded?
		if (form.getFile() == null || form.getFile().isEmpty() || form.getFile().getOriginalFilename() == null) {
			errors.rejectValue("file", "chits.admin.icd10.error.csv.file.required");
		} else {
			InputStream source = null;
			try {
				// open an input stream
				source = form.getFile().getInputStream();

				// process the ICD10 file
				final CSVReader reader = new CSVReader(new InputStreamReader(source));

				// verify the header part: expecting: "CODE", "SIGN/SYMPTOMS" or "DIAGNOSIS"
				int rowNumber = 1;

				// setup CSV utility for the header
				final CSVUtil csvUtil = new CSVUtil(reader.readNext());

				String className = null;
				String datatypeName = null;
				String valueHeader = null;
				if (csvUtil.containsHeader(SIGN_SYMPTOMS)) {
					valueHeader = SIGN_SYMPTOMS;
					className = ICD10.SYMPTOM_CONCEPT_CLASS;
					datatypeName = ICD10.CONCEPT_DATATYPE;
				} else if (csvUtil.containsHeader(DIAGNOSIS)) {
					valueHeader = DIAGNOSIS;
					className = ICD10.DIAGNOSIS_CONCEPT_CLASS;
					datatypeName = ICD10.CONCEPT_DATATYPE;
				}

				// validate header
				if (!csvUtil.containsHeader("CODE") || valueHeader == null || className == null || datatypeName == null) {
					// header is either missing or not valid!
					errors.rejectValue("file", "chits.admin.icd10.format.error", //
							new Object[] { form.getFile().getOriginalFilename() }, //
							"chits.admin.icd10.format.error");

					// send back to input page upon error
					return "/module/chits/admin/icd10/upload";
				}

				// set the concept source for ICD10 codes
				icd10ConceptSource = conceptService.getConceptSourceByName("ICD10");

				// initialize a new ConceptUtil instance
				final ConceptUtil conceptUtil = conceptUtilFactory.newInstance();

				while (csvUtil.nextRow(reader.readNext()) != null) {
					rowNumber++;

					// get the ICD10 code and value
					final String icd10Code = csvUtil.get("CODE");
					final String value = csvUtil.get(valueHeader);

					if (StringUtils.isEmpty(icd10Code) || StringUtils.isEmpty(value)) {
						log.info("Skipped row #" + rowNumber + " (not enough data)");
						counters.skipped++;
					} else {
						// use value as the description
						log.info("Processing row #" + rowNumber + "; icd10=" + icd10Code + "; value=" + value);
						try {
							processICD10(conceptUtil, counters, icd10Code, value, value, className, datatypeName);
						} catch (Exception ex) {
							log.error("Error processing row# " + rowNumber, ex);
							counters.errors++;
						}

						// update session information about processed data
						updateSessionData(httpSession, counters, rowNumber);
					}
				}
			} catch (IOException ioe) {
				// add error message
				log.error("Error processing ICD10 codes", ioe);
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, new Object[] { ioe.getMessage() });
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.admin.icd10.uploaded.error");

				// send back to input page upon error
				return "/module/chits/admin/icd10/upload";
			} finally {
				// clear session information
				updateSessionData(httpSession, counters, null);

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
			return "/module/chits/admin/icd10/upload";
		}

		// add success message
		// store the 'patient created' message
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { form.getFile().getOriginalFilename(), counters.added, counters.updated,
				counters.unchanged, counters.skipped, counters.errors });
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.icd10.uploaded.success");

		// redirect after successful submission
		return "redirect:/module/chits/admin/icd10/uploadICD10Codes.form";
	}

	/**
	 * Adds the row number to the session data map.
	 * 
	 * @param session
	 * @param rowNumber
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void updateSessionData(HttpSession session, Counters counters, Integer rowNumber) {
		Map sessionData = (Map) session.getAttribute(Constants.SESSION_DATA_KEY);
		if (sessionData == null) {
			sessionData = new LinkedHashMap();
			session.setAttribute(Constants.SESSION_DATA_KEY, sessionData);
		}

		if (rowNumber != null) {
			sessionData.put("uploadICD10CodesInfo", rowNumber + " (" + counters.toString() + ")");
		} else {
			sessionData.remove("uploadICD10CodesInfo");
		}
	}

	/**
	 * Create or update the symptom/diagnosis concept mapped to an ICD10 code.
	 * 
	 * @param icd10Code
	 *            The ICD10 code to map the symptom/diagnosis concept to
	 * @param conceptName
	 *            The concept name
	 * @param description
	 *            A description of the concept
	 */
	private void processICD10(ConceptUtil conceptUtil, Counters counters, String icd10Code, String conceptName, String description, String className,
			String datatypeName) {
		Concept concept = conceptService.getConceptByName(conceptName);
		if (concept != null) {
			// check if any changes are needed
			boolean changed = false;

			// update 'changed' flag if any of the attributes need to be updated
			changed = conceptUtil.updateConceptIfNeeded(concept, description, className, datatypeName);

			// is there an ICD10 mapping?
			boolean icd10MapFound = false;
			for (ConceptMap cm : concept.getConceptMappings()) {
				if (icd10ConceptSource.equals(cm.getSource())) {
					icd10MapFound = true;
					if (!icd10Code.equals(cm.getSourceCode())) {
						cm.setSourceCode(icd10Code);
						cm.setChangedBy(Context.getAuthenticatedUser());
						cm.setDateChanged(new Date());
						changed = true;
					}
				}
			}

			if (!icd10MapFound) {
				// create a mapping for the ICD10 code
				addIcd10Map(concept, conceptName, icd10Code);

				// need to save the concept since it has changed
				changed = true;
			}

			if (changed) {
				// update the concept in the database
				concept.setChangedBy(Context.getAuthenticatedUser());
				concept.setDateChanged(new Date());
				chitsService.saveConceptForcingDatatype(concept);
			}

			if (changed) {
				// track the number of concepts that are updated
				log.info("Updated record...");
				counters.updated++;
			} else {
				// track the number of concepts that are unchanged
				log.info("Record is up-to-date.");
				counters.unchanged++;
			}
		} else {
			// need to create a new concept...
			concept = conceptUtil.newConcept(conceptName, description, icd10Code, className, datatypeName);

			// save the diagnosis / symptom concept
			concept = conceptService.saveConcept(concept);

			// create a mapping for the ICD10 code
			addIcd10Map(concept, conceptName, icd10Code);

			// save the concept again to save the ICD10 mapping
			conceptService.saveConcept(concept);

			// track the number of concepts created
			log.info("Created record...");
			counters.added++;
		}
	}

	/**
	 * Add a mapping to the ICD10 code for the concept.
	 * 
	 * @param concept
	 * @param conceptName
	 * @param icd10Code
	 */
	private void addIcd10Map(Concept concept, String conceptName, String icd10Code) {
		// create a mapping for the ICD10 code
		final ConceptMap cm = new ConceptMap();
		cm.setComment(conceptName);
		cm.setConcept(concept);
		cm.setSource(icd10ConceptSource);
		cm.setSourceCode(icd10Code);
		cm.setCreator(Context.getAuthenticatedUser());
		cm.setDateCreated(new Date());
		cm.setUuid(UUID.randomUUID().toString());

		// add the concept mapping
		concept.getConceptMappings().add(cm);
	}

	@Autowired
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Autowired
	public void setConceptUtilFactory(ConceptUtilFactory conceptUtilFactory) {
		this.conceptUtilFactory = conceptUtilFactory;
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}

	class Counters {
		// count the number of codes added, changed, unmodified, or skipped
		int added = 0;
		int updated = 0;
		int unchanged = 0;
		int skipped = 0;
		int errors = 0;

		@Override
		public String toString() {
			return "<strong>Added: </strong>" + added //
					+ ", <strong>Updated: </strong>" + updated //
					+ ", <strong>Unchanged: </strong>" + unchanged //
					+ ", <strong>Skipped: </strong>" + skipped //
					+ ", <strong>Errors: </strong>" + errors;
		}
	}
}
