package org.openmrs.module.chits.web.controller.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.ConceptUtilFactory;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.FormTemplateSerializer;
import org.openmrs.module.chits.UploadFileForm;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Upload note templates from a ZIP file.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/templates/uploadTemplates.form")
public class UploadTemplatesController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the Patient service */
	protected ConceptService conceptService;

	/** Auto-wire the CHITSService */
	protected CHITSService chitsService;

	/** Utility class for managing concepts */
	protected ConceptUtilFactory conceptUtilFactory;

	enum Template {
		COMPLAINT_NOTES("Templates/Complaint Templates/", VisitNotesConceptSets.COMPLAINT_NOTES), //
		HISTORY_NOTES("Templates/History Templates/", VisitNotesConceptSets.HISTORY_NOTES), //
		PHYSICAL_EXAM_NOTES("Templates/Physical Exam Templates/", VisitNotesConceptSets.PHYSICAL_EXAM_NOTES), //
		DIAGNOSIS_NOTES("Templates/Diagnosis Templates/", VisitNotesConceptSets.DIAGNOSIS_NOTES), //
		TREATMENT_NOTES("Templates/Treatment Plan Templates/", VisitNotesConceptSets.TREATMENT_NOTES);

		final String folderName;
		final VisitNotesConceptSets visitNotesConceptSets;

		private Template(String folderName, VisitNotesConceptSets visitNotesConceptSets) {
			this.folderName = folderName;
			this.visitNotesConceptSets = visitNotesConceptSets;
		}

		public String getFolderName() {
			return folderName;
		}

		public VisitNotesConceptSets getVisitNotesConceptSets() {
			return visitNotesConceptSets;
		}
	}

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
	public String showForm(HttpSession httpSession, //
			@ModelAttribute("form") UploadFileForm form, //
			BindingResult errors) {
		// send back to the patient consult page
		return "/module/chits/admin/templates/upload";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String uploadTemplates(HttpSession httpSession, //
			@ModelAttribute("form") UploadFileForm form, //
			BindingResult errors) {
		// reset counters
		final Counters counters = new Counters();

		// track which files were processed for logging later
		final List<String> processedTemplates = new ArrayList<String>();

		// was a file uploaded?
		if (form.getFile() == null || form.getFile().isEmpty() || form.getFile().getOriginalFilename() == null) {
			errors.rejectValue("file", "chits.admin.templates.error.zip.file.required");
		} else {
			ZipFile zipFile = null;
			File file = null;
			try {
				// transfer the file to somewhere useful
				file = File.createTempFile("chits-notes-", ".zip");
				form.getFile().transferTo(file);

				// open an input stream
				zipFile = new ZipFile(file);

				// process zip files...
				final Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while (entries.hasMoreElements()) {
					final ZipEntry entry = entries.nextElement();
					if (entry.isDirectory()) {
						// ignore directories!
						continue;
					}

					boolean wasProcessed = false;
					for (Template template : Template.values()) {
						if (entry.getName().startsWith(template.getFolderName()) && entry.getName().toLowerCase().endsWith(".txt")
								&& !entry.getName().substring(template.getFolderName().length()).contains("/")) {
							log.info("Processing template file: " + entry.getName());

							// get the template name without the folder part
							String templateName = entry.getName().substring(template.getFolderName().length());

							// strip off the '.txt' extension
							templateName = templateName.substring(0, templateName.length() - 4);

							if (!templateName.toLowerCase().endsWith(" Notes")) {
								// append 'Notes ' to the end of all template names to clearly distinguish them from non-template concepts
								templateName += " Notes";
							}

							// initialize a new ConceptUtil instance
							final ConceptUtil conceptUtil = conceptUtilFactory.newInstance();

							final Reader source = new InputStreamReader(zipFile.getInputStream(entry));
							try {
								// process this file since it matched a template folder name
								try {
									processTemplate(conceptUtil, counters, template, templateName, new BufferedReader(source));
								} catch (Exception ex) {
									log.error("Error processing template: " + template, ex);
									counters.errors++;
								}

								// update session information about processed data
								updateSessionData(httpSession, counters);

								// no need to check against other templates!
								break;
							} finally {
								source.close();
								processedTemplates.add(template.toString() + " / " + templateName);
								wasProcessed = true;
							}
						}
					}

					if (!wasProcessed) {
						log.info("Skipping entry: " + entry.getName());
						counters.skipped++;
					}
				}
			} catch (IOException ioe) {
				// header is either missing or not valid!
				errors.rejectValue("file", "chits.admin.templates.format.error", new Object[] { form.getFile().getOriginalFilename() },
						"The file ''{0}'' does not appear to be a valid ZIP file.");

				// add error message
				log.error("Error processing note templates in ZIP file", ioe);
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, new Object[] { ioe.getMessage() });
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.admin.templates.uploaded.error");

				// send back to input page upon error
				return "/module/chits/admin/templates/upload";
			} finally {
				// clear session data
				updateSessionData(httpSession, null);

				if (zipFile != null) {
					try {
						// cleanup!
						zipFile.close();
					} catch (IOException ioe) {
						log.warn("Error closing ZIP file on server", ioe);
					}
				}

				if (file != null) {
					// cleanup!
					if (!file.delete()) {
						log.warn("Unable to delete temporary file: " + file);
					}
				}
			}
		}

		// list all files that were processed:
		log.info("Summary list of processed templates: " + processedTemplates);

		if (errors.hasErrors()) {
			// send back to input page upon error
			return "/module/chits/admin/templates/upload";
		}

		// add success message
		// store the 'patient created' message
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { form.getFile().getOriginalFilename(), counters.added, counters.updated,
				counters.unchanged, counters.skipped, counters.errors });
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.templates.uploaded.success");

		// redirect after successful submission
		return "redirect:/module/chits/admin/templates/uploadTemplates.form";
	}

	/**
	 * Adds progress information to the session data map.
	 * 
	 * @param session
	 * @param rowNumber
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void updateSessionData(HttpSession session, Counters counters) {
		Map sessionData = (Map) session.getAttribute(Constants.SESSION_DATA_KEY);
		if (sessionData == null) {
			sessionData = new LinkedHashMap();
			session.setAttribute(Constants.SESSION_DATA_KEY, sessionData);
		}

		if (counters != null) {
			sessionData.put("uploadTemplatesInfo", counters.toString());
		} else {
			sessionData.remove("uploadTemplatesInfo");
		}
	}

	/**
	 * Creates or updates the template note concept as a member of the corresponding template
	 * 
	 * @param template
	 *            The template type
	 * @param templateConceptName
	 *            The name of the template (concept name)
	 * @param bufferedReader
	 *            Reader to load the template content
	 * @throws IOException
	 *             If any errors occur
	 */
	private void processTemplate(ConceptUtil conceptUtil, Counters counters, Template template, String templateConceptName, BufferedReader bufferedReader)
			throws IOException {
		log.info("Processing template: " + template + "; concept: " + templateConceptName);

		// read in the text content
		final StringBuilder text = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			text.append(line);

			// be kind to IE, use CRLF!
			text.append("\r\n");
		}

		// convert to string
		final String templateText = text.toString();

		// find the concept convenience set for the corresponding notes category
		final Concept convSetConceptName = conceptService.getConcept(template.getVisitNotesConceptSets().getConceptId());

		Concept templateConcept = conceptService.getConceptByName(templateConceptName);
		if (templateConcept != null) {
			// load the serialized form matching the UUID of the template concept
			final SerializedObject formTemplate = chitsService.getSerializedObjectByUuid(templateConcept.getUuid());

			boolean changed = false;
			if (formTemplate != null) {
				// check if form text still matches
				if (!templateText.equals(formTemplate.getSerializedData())) {
					// update needed!
					formTemplate.setSerializedData(templateText);
					formTemplate.setChangedBy(conceptUtil.getUserForAudit());
					formTemplate.setDateChanged(new Date());
					chitsService.saveSerializedObject(formTemplate);
					changed = true;
				}
			} else {
				// no matching form template, so create one!
				saveFormTemplate(conceptUtil, templateConceptName, templateConcept.getUuid(), templateText);
				changed = true;
			}

			if (!"Misc".equals(templateConcept.getConceptClass().getName())) {
				templateConcept.setConceptClass(conceptService.getConceptClassByName("Misc"));
				changed = true;
			}

			if (!"N/A".equals(templateConcept.getDatatype().getName())) {
				// change datatype
				templateConcept.setDatatype(conceptService.getConceptDatatypeByName("N/A"));
				changed = true;
			}

			// make sure the template is a member of the corresponding convenience set
			if (!convSetConceptName.getSetMembers().contains(templateConcept)) {
				conceptUtil.addSetMember(convSetConceptName, templateConcept);
				chitsService.saveConceptForcingDatatype(convSetConceptName);
				changed = true;
			}

			if (changed) {
				chitsService.saveConceptForcingDatatype(templateConcept);
				counters.updated++;
			} else {
				counters.unchanged++;
			}
		} else {
			// does not exist yet; so create it!
			templateConcept = conceptUtil.newConcept(templateConceptName, templateConceptName, templateConceptName, "Misc", "N/A");
			templateConcept.setUuid(UUID.randomUUID().toString());
			conceptService.saveConcept(templateConcept);

			// create the form template for
			saveFormTemplate(conceptUtil, templateConceptName, templateConcept.getUuid(), templateText);

			// add the concept to the convenience set
			conceptUtil.addSetMember(convSetConceptName, templateConcept);
			conceptService.saveConcept(convSetConceptName);

			// a template was added
			counters.added++;
		}
	}

	/**
	 * Saves the concept form template.
	 * 
	 * @param templateConceptName
	 * @param uuid
	 * @param text
	 */
	private void saveFormTemplate(ConceptUtil conceptUtil, String templateConceptName, String uuid, String text) {
		final SerializedObject formTemplate = new SerializedObject();
		formTemplate.setCreator(conceptUtil.getUserForAudit());
		formTemplate.setDateCreated(new Date());
		formTemplate.setDescription("Form template for: " + templateConceptName);
		formTemplate.setName(templateConceptName);
		formTemplate.setSerializedData(text);
		formTemplate.setType("Text");
		formTemplate.setSubtype("Form Template");
		formTemplate.setSerializationClass(FormTemplateSerializer.class);
		formTemplate.setUuid(uuid);
		formTemplate.setRetired(Boolean.FALSE);
		chitsService.saveSerializedObject(formTemplate);
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
		int errors;

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
