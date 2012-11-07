package org.openmrs.module.chits.web.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import liquibase.csv.CSVReader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptNameType;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Upload concepts from a CSV file.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/concepts/uploadConcepts.form")
public class UploadConceptsController implements Constants {
	/** Delimiter to use for splitting multi-valued cell values */
	private static final String MULTI_VALUED_CELL_DELIMITER = "\\s*[\\r\\n]+\\s*";

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the Patient service */
	protected ConceptService conceptService;

	/** Utility class for managing concepts */
	protected ConceptUtilFactory conceptUtilFactory;

	/** CHITS service for force-saving concepts with changed datatype */
	protected CHITSService chitsService;

	/** The pattern to use for extracting the UUID from the 'Mappings' column */
	public final static Pattern UUID_PATTERN = Pattern.compile("^\\s*(?:OpenMRS)?\\s*UUID:\\s*([^\\s]+)\\s*$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

	/** Captures the trailing numeric value */
	private final static Pattern NUMERIC_VALUE = Pattern.compile("([\\d\\.]+)\\s*$");

	/** two-letter locale suffix; including the brackets! */
	private final static Pattern TWO_LETTER_LOCALE_SUFFIX_WITH_BRACKETS = Pattern.compile("\\s+(\\[[a-zA-Z]{2,2}\\])$");

	/** Tagalog Locale */
	private static final Locale TAGALOG = new Locale("tl");

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
		return getInputPage();
	}

	protected String getInputPage() {
		return "/module/chits/admin/concepts/upload";
	}

	protected String getSuccessPage() {
		return "redirect:/module/chits/admin/concepts/uploadConcepts.form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			@ModelAttribute("form") UploadFileForm form, //
			BindingResult errors) {
		// reset counters
		final Counters counters = new Counters();

		// was a file uploaded?
		if (form.getFile() == null || form.getFile().isEmpty() || form.getFile().getOriginalFilename() == null) {
			errors.rejectValue("file", "chits.admin.concepts.error.csv.file.required");
		} else {
			InputStream source = null;
			try {
				// open an input stream
				source = form.getFile().getInputStream();

				// process the concepts file
				final CSVReader reader = new CSVReader(new InputStreamReader(source));

				// verify the header part: expecting at least: "Name", "SIGN/SYMPTOMS" or "DIAGNOSIS"
				int rowNumber = 1;

				// setup CSV utility for the header
				final CSVUtil csvUtil = new CSVUtil(reader.readNext());

				// validate header
				if (!csvUtil.containsHeader("Name")) {
					// header is either missing or not valid!
					errors.rejectValue("file", "chits.admin.concepts.format.error", //
							new Object[] { form.getFile().getOriginalFilename() }, //
							"chits.admin.concepts.format.error");

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

					// persist any and all changes
					Context.flushSession();

					// clear the hibernate session so all entities will be re-loaded
					Context.clearSession();

					// update session information about processed data
					updateSessionData(httpSession, counters, rowNumber);
				}
			} catch (IOException ioe) {
				// add error message
				log.error("Error processing concepts", ioe);
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, new Object[] { ioe.getMessage() });
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.admin.concepts.uploaded.error");

				// send back to input page upon error
				return getInputPage();
			} finally {
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
			return getInputPage();
		}

		// add success message
		setupSuccessMessage(httpSession, form, counters);

		// redirect after successful submission
		return getSuccessPage();
	}

	protected void setupSuccessMessage(HttpSession httpSession, UploadFileForm form, Counters counters) {
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { form.getFile().getOriginalFilename(), counters.added, counters.updated,
				counters.unchanged, counters.skipped, counters.errors, counters.duplicates, counters.duplicateConcepts });
		if (counters.errors == 0 && counters.duplicates == 0) {
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.concepts.uploaded.success");
		} else {
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.concepts.uploaded.success.with.warnings");
		}
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
			sessionData.put("uploadConceptsInfo", rowNumber + " (" + counters.toString() + ")");
		} else {
			sessionData.remove("uploadConceptsInfo");
		}
	}

	protected void processRow(int rowNumber, CSVUtil csvUtil, Counters counters) {
		// load the concept record
		final String name = csvUtil.get("Name").trim();
		final String synonyms = csvUtil.get("Synonyms");
		final String shortName = csvUtil.get("Short Name");
		final String description = csvUtil.get("Description (form)");
		final String className = csvUtil.get("Class");
		final String datatypeName = csvUtil.get("Datatype");
		final String containedInSets = csvUtil.get("Contained in Sets (form)");
		final String answers = csvUtil.get("Answers");

		// extract the UUID from the mappings; e.g.:
		// openMRS ID: 1065
		// UUID: 1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
		// PIH: 1065
		// PIH: 1894
		// AMPATH: 1065
		// SNOMED CT: 373066001
		// PIH: 1809
		String uuid = null;
		final String mappings = csvUtil.get("Mappings");
		if (mappings != null) {
			final Matcher uuidMatcher = UUID_PATTERN.matcher(mappings);
			if (uuidMatcher.find()) {
				uuid = uuidMatcher.group(1);
			} else if (mappings.toLowerCase().contains("uuid:")) {
				log.warn("Unable to parse UUID out of: " + mappings.replaceAll("[\\r\\n]+", "<newline>"));
			}
		}

		// for numeric datatypes, get the numeric bounds
		final String numericBounds = csvUtil.get("Numeric");

		if (StringUtils.isEmpty(name)) {
			log.info("Empty row #" + rowNumber + " (no name)");
		} else {
			// use value as the description
			log.info("Processing row #" + rowNumber + "; concept name=" + name + "; uuid=" + uuid + "; synonyms=" + synonyms + "; shortName=" + shortName
					+ "; description=" + description + "; className=" + className + "; datatypeName=" + datatypeName + "; containedInSets=" + containedInSets
					+ "; answers=" + answers);

			// initialize a new ConceptUtil instance
			final ConceptUtil conceptUtil = conceptUtilFactory.newInstance();

			boolean valid = true;
			if (counters.names.contains(name.toLowerCase())) {
				log.warn("Ignoring duplicate concept '" + name + "' on row# " + rowNumber);
				counters.duplicates++;
				counters.duplicateConcepts.add(name);
				valid = false;
			}

			if (uuid != null && counters.uuidConceptNames.containsKey(uuid.toLowerCase()) //
					&& !counters.uuidConceptNames.get(uuid.toLowerCase()).equals(name.toLowerCase())) {
				log.warn("UUID \"" + uuid + "\" (" + counters.uuidConceptNames.get(uuid.toLowerCase()) + ") re-used on a different concept on row# "
						+ rowNumber + ": \"" + name + "\"");
				counters.reusedUUIDs.add(uuid);
				valid = false;
			}

			if (valid) {
				// process this record
				final String[] synonymsArr = !StringUtils.isEmpty(synonyms) ? synonyms.trim().split(MULTI_VALUED_CELL_DELIMITER) : null;
				final Concept c = processConcept(conceptUtil, counters, name.trim(), uuid, //
						synonymsArr, //
						!StringUtils.isEmpty(shortName) ? shortName.trim() : null, //
						!StringUtils.isEmpty(description) ? description.trim() : "", //
						!StringUtils.isEmpty(className) ? className.trim() : "Misc", //
						!StringUtils.isEmpty(datatypeName) ? datatypeName.trim() : "N/A", //
						!StringUtils.isEmpty(containedInSets) ? containedInSets.trim().split(MULTI_VALUED_CELL_DELIMITER) : null, //
						!StringUtils.isEmpty(answers) ? answers.trim().split(MULTI_VALUED_CELL_DELIMITER) : null, //
						!StringUtils.isEmpty(numericBounds) ? numericBounds.trim() : null);

				if (c != null) {
					// keep track of the concept names already processed
					counters.names.add(name.toLowerCase());
					if (synonymsArr != null) {
						for (String synonym : synonymsArr) {
							counters.names.add(asSynonym(synonym).synonym.toLowerCase());
						}
					}
					if (uuid != null) {
						counters.uuidConceptNames.put(uuid.toLowerCase(), name.toLowerCase());
					}
				} else {
					counters.skipped++;
					counters.skippedRows.add(rowNumber);
				}
			}
		}
	}

	/**
	 * Create or update the concepts.
	 */
	/**
	 * @param conceptUtil
	 * @param counters
	 * @param name
	 * @param uuid
	 * @param synonyms
	 * @param shortName
	 * @param description
	 * @param className
	 * @param datatypeName
	 * @param containedInSets
	 * @param answers
	 * @param numericBounds
	 * @return
	 */
	protected Concept processConcept(ConceptUtil conceptUtil, Counters counters, String name, String uuid, String[] synonyms, String shortName,
			String description, String className, String datatypeName, String[] containedInSets, String[] answers, String numericBounds) {
		// try looking up by uuid
		Concept concept = null;

		if (uuid != null) {
			concept = conceptService.getConceptByUuid(uuid);
		}

		if (concept == null) {
			// try looking up by fully specified name
			concept = chitsService.findConceptByFullySpecifiedName(name);
		}

		// DO NOT LOOK UP BY SHORT NAME SINCE SOME CONCEPT NAMES WILL CONFLICT WITH A DRUG'S CONCEPT'S SHORT NAME
		// if (concept == null && shortName != null && !shortName.equals(name)) {
		// // try looking up by short name
		// concept = conceptService.getConceptByName(shortName);
		//
		// // log warning that the short name was used to lookup the concept
		// log.warn("Concept '" + name + "' looked up by short name: '" + shortName + "'");
		// }

		// DO NOT LOOK UP BY SYNONYM SINCE THE CONCEPT DICTIONARY MAY HAVE CONFLICTS!!!
		// if (concept == null && synonyms != null) {
		// // not found by name? try finding by synonym
		// for (String synonym : synonyms) {
		// concept = conceptService.getConceptByName(asSynonym(synonym).synonym);
		// if (concept != null) {
		// // log warning that a synonym was used to lookup the concept
		// log.warn("Concept '" + name + "' looked up by synonym: '" + synonym + "'");
		//
		// // stop at first synonym found
		// break;
		// }
		// }
		// }

		if (concept != null) {
			// check if any changes are needed
			boolean changed = false;

			// purge any concept names that will conflict with this concept
			final Set<String> skipSynonymsLowered = purgeOrSkipDuplicateNames(name, uuid, concept, shortName, synonyms);
			if (skipSynonymsLowered == null) {
				// skip this record due to conflicts...
				return null;
			}

			// update 'changed' flag if any of the attributes need to be updated
			changed = conceptUtil.updateConceptIfNeeded(concept, description, className, datatypeName);

			// update name if needed
			if (!concept.getName().getName().equals(name)) {
				// update the name
				concept.getName().setName(name);
				changed = true;
			}

			if (shortName != null && !shortName.equals("") && !shortName.equalsIgnoreCase(name)) {
				ConceptName shortNameConcept = concept.getShortNameInLocale(Constants.ENGLISH);
				if (shortNameConcept == null) {
					shortNameConcept = new ConceptName();
					shortNameConcept.setConcept(concept);
					shortNameConcept.setConceptNameType(ConceptNameType.SHORT);
					shortNameConcept.setName(shortName);
					shortNameConcept.setLocale(Constants.ENGLISH);
					shortNameConcept.setLocalePreferred(Boolean.FALSE);
					shortNameConcept.setVoided(Boolean.FALSE);
					shortNameConcept.setTags(new HashSet<ConceptNameTag>());
					concept.setShortName(shortNameConcept);

					shortNameConcept.setCreator(conceptUtil.getUserForAudit());
					shortNameConcept.setDateCreated(new Date());
					shortNameConcept.setUuid(UUID.randomUUID().toString());

					changed = true;
				} else {
					if (!shortNameConcept.getName().equals(shortName)) {
						shortNameConcept.setName(shortName);
						shortNameConcept.setChangedBy(conceptUtil.getUserForAudit());
						shortNameConcept.setDateChanged(new Date());
						changed = true;
					}
				}
			}

			// update UUID if needed (and specified)
			if (uuid != null && !uuid.equals(concept.getUuid())) {
				concept.setUuid(uuid);
				changed = true;
			}

			if ("ConvSet".equalsIgnoreCase(className)) {
				// creating a convenience set...
				if (concept.isSet() == null || !concept.isSet()) {
					// should be a set!
					concept.setSet(Boolean.TRUE);
					changed = true;
				}
			}

			// check the synonyms (if any)
			if (synonyms != null) {
				final List<Synonym> toAdd = new ArrayList<Synonym>();
				for (String synonym : synonyms) {
					toAdd.add(asSynonym(synonym));
				}

				for (ConceptName synonymCN : concept.getSynonyms()) {
					final Iterator<Synonym> toAddIT = toAdd.iterator();
					boolean found = false;
					while (toAddIT.hasNext()) {
						final Synonym syn = toAddIT.next();
						final String synonym = syn.synonym;
						final Locale locale = syn.locale;

						// when considering synonyms, also include the 'name' of the concept
						if (synonym.equalsIgnoreCase(synonymCN.getName().trim()) || synonym.equalsIgnoreCase(name)) {
							// this checks out, remove from list of synonym names to add
							toAddIT.remove();
							found = true;

							// make sure the 'locale' matches
							if (!locale.equals(synonymCN.getLocale()) || synonymCN.isVoided()) {
								synonymCN.setLocale(locale);
								synonymCN.setVoided(Boolean.FALSE);
								changed = true;
							}
						}
					}

					if (!found) {
						// this is no longer a synonym... so it should be purged
						// NOTE: The concept names have a cascade type of 'all-delete-orphan' so
						// there's no need to manually delete the names
						log.info("Removing synonym: '" + synonymCN.getName() + "' for concept id: " + concept.getConceptId());
						concept.removeName(synonymCN);
						changed = true;
					}
				}

				if (!toAdd.isEmpty()) {
					for (Synonym syn : toAdd) {
						// don't include the 'name' itself for synonyms
						if (!skipSynonymsLowered.contains(syn.synonym.toLowerCase().trim()) && !name.equalsIgnoreCase(syn.synonym)
								&& (shortName == null || !shortName.equals(syn.synonym))) {
							// add the synonym to the concept
							if (conceptUtil.addSynonym(concept, syn.synonym, syn.locale) != null) {
								// synonyms added, so this is changed
								changed = true;
							}
						}
					}
				}
			}

			// NOTE: After potentially removing synonyms, save first before proceeding with pairing questions or sets
			if (changed) {
				// update the concept words attached to this concept
				conceptService.updateConceptIndex(concept);

				// update the concept in the database
				concept.setChangedBy(conceptUtil.getUserForAudit());
				concept.setDateChanged(new Date());
				chitsService.saveConceptForcingDatatype(concept);
			}

			// store lower-cased names of all answers for later use
			final List<String> answersLowered = new ArrayList<String>();
			if (answers != null) {
				double sortWeight = 1.0;
				for (String answerName : answers) {
					Concept answerConcept = conceptService.getConceptByName(answerName);
					if (answerConcept == null) {
						// create a new concept for this answer only if it doesn't exist
						answerConcept = conceptService.saveConcept(conceptUtil.newConcept(answerName, answerName, null, "Finding", "Coded"));
					}

					// pair the question concept to the answer (NOTE: Don't save since saving occurs later!)
					changed |= conceptUtil.pairQuestionAndAnswerIfNeeded(concept, answerConcept, sortWeight, false);

					// store the lower-cased version of the answer (and all synonyms) name for later use
					for (ConceptName cn : answerConcept.getNames()) {
						answersLowered.add(cn.getName().toLowerCase());
					}

					// update sort weight counter
					sortWeight += 1.0;
				}
			}

			// remove all other answers
			for (ConceptAnswer ca : new ArrayList<ConceptAnswer>(concept.getAnswers())) {
				if (!answersLowered.contains(ca.getAnswerConcept().getName().getName().toLowerCase())) {
					// this is not a valid answer to this question!
					concept.removeAnswer(ca);
					changed = true;
				}
			}

			if (changed) {
				// update the concept words attached to this concept
				conceptService.updateConceptIndex(concept);

				// update the concept in the database
				concept.setChangedBy(conceptUtil.getUserForAudit());
				concept.setDateChanged(new Date());
				chitsService.saveConceptForcingDatatype(concept);
			}

			if ("Numeric".equalsIgnoreCase(datatypeName) && !StringUtils.isEmpty(numericBounds)) {
				ConceptNumeric cn = conceptService.getConceptNumeric(concept.getConceptId());
				if (cn == null) {
					// create a new numeric concept
					cn = new ConceptNumeric(concept);
					changed = true;
				}

				changed |= applyNumericBounds(cn, numericBounds);

				if (changed) {
					// save the numeric concept as well
					chitsService.saveConceptForcingDatatype(cn);
				}
			}

			// store lower-cased names of all set names for later use
			final List<String> containedInSetsLowered = new ArrayList<String>();
			if (containedInSets != null) {
				final List<Concept> setsToSave = new ArrayList<Concept>();
				for (String setName : containedInSets) {
					final Concept setConcept = conceptUtil.loadOrCreateSet(setName, null, setName);

					// also consider 'changed' if symptom was added to any sets (but no need to save the concept again)
					if (conceptUtil.pairSetMemberIfNeeded(setConcept, concept, null, false)) {
						setsToSave.add(setConcept);
						changed |= true;
					}

					// store the lower-cased version of the set name and all synonyms for later use
					for (ConceptName cn : setConcept.getNames()) {
						containedInSetsLowered.add(cn.getName().toLowerCase());
					}
				}

				// save any sets that were modified
				if (!setsToSave.isEmpty()) {
					chitsService.saveConceptsForcingDatatype(setsToSave);
				}
			}

			// purge from all other concept sets
			final List<ConceptSet> conceptSets = new ArrayList<ConceptSet>(conceptService.getSetsContainingConcept(concept));
			for (ConceptSet setConcept : conceptSets) {
				if (!containedInSetsLowered.contains(setConcept.getConceptSet().getName().getName().toLowerCase())) {
					// this concept should not be contained in this set!
					changed |= conceptUtil.unpairSetMemberIfNeeded(setConcept.getConceptSet(), concept);
				}
			}

			if (changed) {
				// track the number of symptom concepts that are updated
				log.info("Updated record...");

				counters.updated++;
			} else {
				// track the number of symptom concepts that are unchanged
				log.info("Record is up-to-date.");
				counters.unchanged++;
			}
		} else {
			// need to create a new concept...
			concept = conceptUtil.newConcept(name, description, shortName, className, datatypeName);
			if (uuid != null) {
				// use specific UUID (if specified)
				concept.setUuid(uuid);
			}

			// save the concept to obtain an ID
			concept = conceptService.saveConcept(concept);

			// purge any concept names that will conflict with this concept
			final Set<String> skipSynonymsLowered = purgeOrSkipDuplicateNames(name, uuid, concept, shortName, synonyms);
			if (skipSynonymsLowered == null) {
				// skip this record due to conflicts...
				return null;
			}

			// add the synonyms
			if (synonyms != null && synonyms.length > 0) {
				for (String synonym : synonyms) {
					final Synonym syn = asSynonym(synonym);

					// don't include the 'name' itself for synonyms
					if (!skipSynonymsLowered.contains(syn.synonym.toLowerCase().trim()) && !name.equalsIgnoreCase(syn.synonym)
							&& (shortName == null || !shortName.equals(syn.synonym))) {
						conceptUtil.addSynonym(concept, syn.synonym, syn.locale);
					}
				}
			}

			if (answers != null) {
				double sortWeight = 1.0;
				for (String answerName : answers) {
					Concept answerConcept = conceptService.getConceptByName(answerName);
					if (answerConcept == null) {
						// create a new concept for this answer only if it doesn't exist
						answerConcept = conceptService.saveConcept(conceptUtil.newConcept(answerName, answerName, null, "Finding", "Coded"));
					}

					// pair the question concept to the answer (NOTE: Don't save since saving occurs later!)
					conceptUtil.pairQuestionAndAnswerIfNeeded(concept, answerConcept, sortWeight, false);

					// update sort weight counter
					sortWeight += 1.0;
				}
			}

			// save the concept
			concept = conceptService.saveConcept(concept);

			if ("Numeric".equalsIgnoreCase(datatypeName) && !StringUtils.isEmpty(numericBounds)) {
				// create a new numeric concept
				ConceptNumeric cn = new ConceptNumeric(concept);
				applyNumericBounds(cn, numericBounds);

				// save the numeric concept as well
				conceptService.saveConcept(cn);
			}

			// and finally, attach this to any sets needed
			if (containedInSets != null) {
				final List<Concept> setsToSave = new ArrayList<Concept>();
				int index = 0;
				for (String setName : containedInSets) {
					final Concept setConcept = conceptUtil.loadOrCreateSet(setName, null, setName);
					if (conceptUtil.pairSetMemberIfNeeded(setConcept, concept, index++, false)) {
						setsToSave.add(setConcept);
					}
				}

				// save any sets that were modified
				if (!setsToSave.isEmpty()) {
					chitsService.saveConceptsForcingDatatype(setsToSave);
				}
			}

			// track the number of symptom concepts created
			log.info("Created record...");
			counters.added++;
		}

		// send back the concept that was created / updated
		return concept;
	}

	private Set<String> purgeOrSkipDuplicateNames(String name, String uuid, Concept concept, String shortName, String[] synonyms) {
		// ensure there are no other concept names that use any of this concept's names or synonyms
		final Set<ConceptName> matchingNames = new HashSet<ConceptName>(chitsService.findMatchingConceptNames(name));
		if (shortName != null) {
			matchingNames.addAll(chitsService.findMatchingConceptNames(shortName));
		}

		if (synonyms != null) {
			for (String synonym : synonyms) {
				matchingNames.addAll(chitsService.findMatchingConceptNames(asSynonym(synonym).synonym));
			}
		}

		final Set<String> skipSynonymsLowered = new HashSet<String>();
		for (ConceptName cn : matchingNames) {
			if (!concept.equals(cn.getConcept())) {
				if (cn.isFullySpecifiedName()) {
					if (cn.getName().trim().equalsIgnoreCase(name.toLowerCase().trim())) {
						log.error("Skipping concept with UUID '" + uuid + " (" + name
								+ ") because there already is another concept using the same name as a fully specified name (" + cn.getConcept() + ")");
						return null;
					}

					log.warn("Ignoring concept \"" + name + "\"'s synonym (" + cn.getName() + ") because it is a fully specified name for: (" + cn.getConcept()
							+ ")!");

					// we can't purge this concept because it is a fully-specified name; instead, just don't add it as a synonym
					skipSynonymsLowered.add(cn.getName().toLowerCase().trim());
				} else {
					// this is a different concept using this concept's name: purge that other concept's names
					log.warn("Concept " + cn.getConcept() + " uses one of this concept's (" + concept + ") names.  Purging concept name: " + cn + " (#"
							+ cn.getConceptNameId() + ")");
					chitsService.purgeConceptName(cn);
				}
			}
		}

		return skipSynonymsLowered;
	}

	/**
	 * Applies the bounds to the numeric concept.
	 * 
	 * @param cn
	 * @param numericBounds
	 * @return
	 */
	protected boolean applyNumericBounds(ConceptNumeric cn, String numericBounds) {
		if (numericBounds == null) {
			return false;
		}

		// load numeric bounds
		Double absoluteHigh = null;
		Double criticalHigh = null;
		Double normalHigh = null;
		Double normalLow = null;
		Double criticalLow = null;
		Double absoluteLow = null;
		String units = null;
		Boolean precise = Boolean.FALSE;
		for (String property : numericBounds.toLowerCase().trim().split(MULTI_VALUED_CELL_DELIMITER)) {
			Double value = null;
			final Matcher valueMatcher = NUMERIC_VALUE.matcher(property);
			if (valueMatcher.find()) {
				try {
					value = Double.parseDouble(valueMatcher.group(1));
				} catch (NumberFormatException ex) {
					log.warn("Error parsing numeric value: " + valueMatcher.group(1), ex);
				}
			}

			if (property.startsWith("absolute high")) {
				absoluteHigh = value;
			} else if (property.startsWith("critical high")) {
				criticalHigh = value;
			} else if (property.startsWith("normal high")) {
				normalHigh = value;
			} else if (property.startsWith("normal low")) {
				normalLow = value;
			} else if (property.startsWith("critical low")) {
				criticalLow = value;
			} else if (property.startsWith("absolute low")) {
				absoluteLow = value;
			} else if (property.startsWith("precise?")) {
				precise = property.endsWith("yes") || property.endsWith("true");
			} else if (property.startsWith("units")) {
				units = property.substring("units".length()).trim();
			}
		}

		boolean changed = false;
		if (!safeEquals(absoluteHigh, cn.getHiAbsolute())) {
			cn.setHiAbsolute(absoluteHigh);
			changed = true;
		}

		if (!safeEquals(criticalHigh, cn.getHiCritical())) {
			cn.setHiCritical(criticalHigh);
			changed = true;
		}

		if (!safeEquals(normalHigh, cn.getHiNormal())) {
			cn.setHiNormal(normalHigh);
			changed = true;
		}

		if (!safeEquals(normalLow, cn.getLowNormal())) {
			cn.setLowNormal(normalLow);
			changed = true;
		}

		if (!safeEquals(criticalLow, cn.getLowCritical())) {
			cn.setLowCritical(criticalLow);
			changed = true;
		}

		if (!safeEquals(absoluteLow, cn.getLowAbsolute())) {
			cn.setLowAbsolute(absoluteLow);
			changed = true;
		}

		if (!precise.equals(cn.getPrecise())) {
			cn.setPrecise(precise);
			changed = true;
		}

		if (!safeEquals(units, cn.getUnits())) {
			cn.setUnits(units);
			changed = true;
		}

		// let caller know if anything was changed
		return changed;
	}

	/**
	 * Splits a synonym with an optional locale suffix (e.g.: 'kasal [ph]') into synonym text and a locale.
	 * 
	 * @param synonym
	 *            The synonym containing an optional language suffix
	 * @return A Synonym containing just the synonym text and the inferred locale of the synonym.
	 */
	private Synonym asSynonym(String synonym) {
		Locale locale;
		final Matcher localeSuffix = TWO_LETTER_LOCALE_SUFFIX_WITH_BRACKETS.matcher(synonym);
		if (localeSuffix.find()) {
			final String twoLetterLocaleWithBrackets = localeSuffix.group(1);
			synonym = synonym.substring(0, synonym.length() - twoLetterLocaleWithBrackets.length()).trim();
			if ("[ph]".equalsIgnoreCase(twoLetterLocaleWithBrackets)) {
				locale = TAGALOG;
			} else if (!"[en]".equalsIgnoreCase(twoLetterLocaleWithBrackets)) {
				locale = new Locale(twoLetterLocaleWithBrackets.substring(1, 3));
			} else {
				// assume 'English' locale
				locale = Constants.ENGLISH;
			}
		} else {
			// assume 'English' locale
			locale = Constants.ENGLISH;
		}

		final Synonym syn = new Synonym();
		syn.synonym = synonym;
		syn.locale = locale;

		return syn;
	}

	/**
	 * Returns true if both objects are null or if both objects are equal.
	 * 
	 * @param o1
	 *            The first object to compare
	 * @param o2
	 *            The second object to compare
	 * @return If both objects are null or equal.
	 */
	protected boolean safeEquals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
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

	protected class Counters {
		List<String> names = new ArrayList<String>();
		Set<String> duplicateConcepts = new LinkedHashSet<String>();

		Map<String, String> uuidConceptNames = new HashMap<String, String>();
		Set<String> reusedUUIDs = new LinkedHashSet<String>();

		List<Integer> skippedRows = new ArrayList<Integer>();

		// count the number of codes added, changed, unmodified, or skipped
		int added = 0;
		int updated = 0;
		int unchanged = 0;
		int skipped = 0;
		int errors = 0;
		int duplicates = 0;

		// counts the missing answers and sets
		int missingConcepts = 0;
		int unlinkedAnswers = 0, unlinkedSetMembers = 0;
		int extraAnswers = 0;

		@Override
		public String toString() {
			return "<strong>Added: </strong>" + added //
					+ ", <strong>Updated: </strong>" + updated //
					+ ", <strong>Unchanged: </strong>" + unchanged //
					+ ", <strong>Skipped: </strong>" + skipped //
					+ ", <strong>Errors: </strong>" + errors //
					+ ", <strong>Duplicates: </strong>" + duplicateConcepts //
					+ ", <string>Re-used UUIDs: </strong>" + reusedUUIDs;
		}
	}

	protected class Synonym {
		String synonym;
		Locale locale;
	}
}
