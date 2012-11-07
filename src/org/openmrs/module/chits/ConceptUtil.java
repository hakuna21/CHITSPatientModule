package org.openmrs.module.chits;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.ConceptSource;
import org.openmrs.Encounter;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.RelationshipType;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.util.PrivilegeConstants;

public class ConceptUtil {
	/** Logger for this class and subclasses */
	protected final static Log log = LogFactory.getLog(ConceptUtil.class);

	/** Admin Service */
	private final AdministrationService adminService;

	/** Concept service */
	private final ConceptService conceptService;

	/** User Service */
	private final UserService userService;

	/** Person Service */
	private final PersonService personService;

	/** Autowired program workflow service */
	private ProgramWorkflowService programWorkflowService;

	/** Chits service */
	private final CHITSService chitsService;

	/** Keeps track of the number of concepts added, updated, or unchanged based on the methods called on this utility class */
	private int added, modified, unchanged;

	/** The invoking user - it is assumed that an instance will be used by only one user! */
	private User user;

	public ConceptUtil(AdministrationService adminService, ConceptService conceptService, ProgramWorkflowService programWorkflowService,
			UserService userService, PersonService personService, CHITSService chitsService) {
		this.adminService = adminService;
		this.conceptService = conceptService;
		this.programWorkflowService = programWorkflowService;
		this.userService = userService;
		this.personService = personService;
		this.chitsService = chitsService;
	}

	public Concept loadOrCreateConceptQuestion(CachedConceptId code) {
		return loadOrCreateConceptQuestion(code, code.getConceptName());
	}

	public Concept loadOrCreateConceptQuestion(CachedConceptId code, String description) {
		log.info(String.format("loadOrCreateConceptQuestion(%s, %s)", code, description));

		// ensure the concept is defined
		Concept concept = conceptService.getConcept(code.getConceptName());
		if (concept == null) {
			// create a concept for this entry
			concept = newConcept(code.getConceptName(), description, null, "Question", "Coded");

			// save the concept
			conceptService.saveConcept(concept);
		} else {
			// make sure the concept attributes are up-to-date
			final boolean changed = updateConceptIfNeeded(concept, description, "Question", "Coded");

			if (changed) {
				// update the concept in the database
				concept.setChangedBy(getUserForAudit());
				concept.setDateChanged(new Date());
				chitsService.saveConceptForcingDatatype(concept);
			}
		}

		// send back the persisted concept
		return concept;
	}

	public Concept loadOrCreateConceptAnswer(Concept question, String conceptName, String description) {
		log.info(String.format("loadOrCreateConceptAnswer(%s, %s, %s)", question, conceptName, description));

		// ensure the concept is defined
		Concept answerConcept = conceptService.getConcept(conceptName);
		if (answerConcept == null) {
			// create a concept for this entry
			answerConcept = newConcept(conceptName, description, null, "Finding", "N/A");

			// save the answer concept
			answerConcept = conceptService.saveConcept(answerConcept);

			// attach this answer to the question (if specified)
			if (question != null) {
				pairQuestionAndAnswerIfNeeded(question, answerConcept);
			}
		} else {
			// make sure the concept attributes are up-to-date
			final boolean changed = updateConceptIfNeeded(answerConcept, description, "Finding", "N/A");

			if (changed) {
				// update the concept in the database
				answerConcept.setChangedBy(getUserForAudit());
				answerConcept.setDateChanged(new Date());
				answerConcept = chitsService.saveConceptForcingDatatype(answerConcept);
			}

			// make sure the answer concept is a member of the question's answers (if specified)
			if (question != null) {
				pairQuestionAndAnswerIfNeeded(question, answerConcept);
			}
		}

		return answerConcept;
	}

	public Concept loadOrCreateConceptAnswer(Concept question, CachedConceptId concept, String description) {
		return loadOrCreateConceptAnswer(question, concept.getConceptName(), description);
	}

	/**
	 * Loads or creates a convenience concept set.
	 * 
	 * @param code
	 *            The code of the convenience concept set
	 * @param description
	 *            The description of the convenience set.
	 * @return The convenience concept set
	 */
	public Concept loadOrCreateConvenienceSet(String conceptSetName, String description) {
		log.info(String.format("loadOrCreateConvenienceSet(%s, %s)", conceptSetName, description));
		return loadOrCreateSet(conceptSetName, "ConvSet", description);
	}

	public Concept loadOrCreateSet(String conceptSetName, String setClassTypeName, String description) {
		// check if concept is already defined
		Concept concept = conceptService.getConcept(conceptSetName);
		if (concept == null) {
			// create a concept for this entry
			concept = newConcept(conceptSetName, description, null, setClassTypeName != null ? setClassTypeName : "ConvSet", "N/A");

			// mark this concept as a set
			concept.setSet(Boolean.TRUE);

			// save the concept
			conceptService.saveConcept(concept);
		} else {
			boolean changed = updateConceptIfNeeded(concept, null, setClassTypeName, null);

			if (concept.isSet() == null || !concept.isSet()) {
				// concept should be a set!
				concept.setSet(Boolean.TRUE);
				changed = true;

				// track changes
				modified++;
			} else {
				unchanged++;
			}

			if (changed) {
				// update the concept in the database
				concept.setChangedBy(getUserForAudit());
				concept.setDateChanged(new Date());
				chitsService.saveConceptForcingDatatype(concept);
			}
		}

		// send back the persisted concept
		return concept;
	}

	public Concept loadOrCreateConvenienceSet(CachedConceptId code, String description) {
		return loadOrCreateConvenienceSet(code.getConceptName(), description);
	}

	public Concept getVitalSignsConceptSet() {
		return loadOrCreateConvenienceSet(Constants.VisitConcepts.VITAL_SIGNS, "Convenience set. Listing of vital signs.");
	}

	public Concept loadOrCreateUnpreciseVitalSignNumericConceptQuestion(CachedConceptId code, String shortName, String description, double absoluteLow,
			double absoluteHigh, String units) {
		return loadOrCreateNumericConceptQuestionImpl(code, shortName, description, absoluteLow, absoluteHigh, units, true, false, "Anatomy");
	}

	public Concept loadOrCreateVitalSignNumericConceptQuestion(CachedConceptId code, String shortName, String description, double absoluteLow,
			double absoluteHigh, String units) {
		return loadOrCreateNumericConceptQuestionImpl(code, shortName, description, absoluteLow, absoluteHigh, units, true, true, "Anatomy");
	}

	public Concept loadOrCreateNumericConceptQuestion(CachedConceptId code, double absoluteLow, double absoluteHigh, String units, String conceptClassName) {
		return loadOrCreateNumericConceptQuestionImpl(code, null, code.getConceptName(), absoluteLow, absoluteHigh, units, false, true, conceptClassName);
	}

	public Concept loadOrCreateNumericConceptQuestion(CachedConceptId code, String shortName, String description, double absoluteLow, double absoluteHigh,
			String units) {
		return loadOrCreateNumericConceptQuestionImpl(code, shortName, description, absoluteLow, absoluteHigh, units, false, true, "Anatomy");
	}

	public ConceptSource loadOrCreateConceptSource(String hl7Code, String description) {
		log.info(String.format("loadOrCreateConceptSource(%s, %s)", hl7Code, description));

		// ensure the concept is defined
		boolean needsSaving = false;
		ConceptSource conceptSource = conceptService.getConceptSourceByName(hl7Code);
		if (conceptSource == null) {
			// create a concept for this entry
			conceptSource = new ConceptSource();
			conceptSource.setCreator(getUserForAudit());
			conceptSource.setDateCreated(new Date());
			conceptSource.setUuid(UUID.randomUUID().toString());

			// track changes
			needsSaving = true;
			added++;
		} else {
			// check if changes are needed
			boolean changed = false;
			changed |= !description.equals(conceptSource.getDescription());
			changed |= !hl7Code.equals(conceptSource.getHl7Code());
			changed |= !hl7Code.equals(conceptSource.getName());
			changed |= !Boolean.FALSE.equals(conceptSource.getRetired());

			if (changed) {
				conceptSource.setChangedBy(getUserForAudit());
				conceptSource.setDateChanged(new Date());

				// track changes
				needsSaving = true;
				modified++;
			} else {
				unchanged++;
			}
		}

		if (needsSaving) {
			// update concept source attributes
			conceptSource.setDescription(description);
			conceptSource.setHl7Code(hl7Code);
			conceptSource.setName(hl7Code);
			conceptSource.setRetired(Boolean.FALSE);

			// save the concept source
			conceptService.saveConceptSource(conceptSource);
		}

		// send back the persisted concept source
		return conceptSource;
	}

	private Concept loadOrCreateNumericConceptQuestionImpl(CachedConceptId code, String shortName, String description, Double absoluteLow, Double absoluteHigh,
			String units, boolean isVitalSign, Boolean precise, String conceptClassName) {
		log.info(String.format("loadOrCreateNumericConceptQuestionImpl(%s, %s, %s, %f, %f, %s, %s)", code, shortName, description, absoluteLow, absoluteHigh,
				Boolean.toString(isVitalSign), Boolean.toString(precise)));

		// ensure the concept is defined
		final Concept c = conceptService.getConcept(code.getConceptName());

		// load the concept as a ConceptNumeric
		ConceptNumeric concept = c != null ? conceptService.getConceptNumeric(c.getConceptId()) : null;
		boolean needsSaving = false;
		if (concept == null) {
			// create a concept for this entry
			concept = new ConceptNumeric(newConcept(code.getConceptName(), description, shortName, conceptClassName, "Numeric"));
			concept.setCreator(getUserForAudit());
			concept.setDateCreated(new Date());
			concept.setUuid(UUID.randomUUID().toString());
			needsSaving = true;
		} else {
			boolean changed = updateConceptIfNeeded(concept, description, conceptClassName, "Numeric");
			changed |= !absoluteHigh.equals(concept.getHiAbsolute());
			changed |= !absoluteLow.equals(concept.getLowAbsolute());
			changed |= !units.equals(concept.getUnits());
			changed |= !precise.equals(concept.getPrecise());
			changed |= !Boolean.FALSE.equals(concept.isRetired());

			if (changed) {
				// update the concept data
				concept.setChangedBy(getUserForAudit());
				concept.setDateChanged(new Date());

				// track changes
				needsSaving = true;
				modified++;
			} else {
				unchanged++;
			}
		}

		if (needsSaving) {
			// update other conceptNumeric parameters: units
			concept.setHiAbsolute(absoluteHigh);
			concept.setLowAbsolute(absoluteLow);
			concept.setUnits(units);
			concept.setPrecise(precise ? Boolean.TRUE : Boolean.FALSE);
			concept.setRetired(Boolean.FALSE);

			// save the concept
			chitsService.saveConceptForcingDatatype(concept);
		}

		if (isVitalSign) {
			// make sure this concept is a member of the vital signs
			pairSetMemberIfNeeded(getVitalSignsConceptSet(), concept, true);
		} else {
			// make sure this concept is NOT a member of the vital signs
			unpairSetMemberIfNeeded(getVitalSignsConceptSet(), concept);
		}

		// send back the persisted concept
		return concept;
	}

	public Concept loadOrCreateDateConceptQuestion(CachedConceptId code) {
		return loadOrCreateDateConceptQuestion(code, null, code.getConceptName());
	}

	public Concept loadOrCreateDateConceptQuestion(CachedConceptId code, String shortName, String description) {
		log.info(String.format("loadOrCreateDateConceptQuestionImpl(%s, %s, %s)", code, shortName, description));

		// ensure the concept is defined
		Concept concept = conceptService.getConcept(code.getConceptName());
		boolean needsSaving = false;
		if (concept == null) {
			// create a concept for this entry
			concept = newConcept(code.getConceptName(), description, shortName, "Misc", "Date");
			concept.setCreator(getUserForAudit());
			concept.setDateCreated(new Date());
			concept.setUuid(UUID.randomUUID().toString());
			needsSaving = true;
		} else {
			boolean changed = updateConceptIfNeeded(concept, description, "Misc", "Date");
			changed |= !Boolean.FALSE.equals(concept.isRetired());

			if (changed) {
				// update the concept data
				concept.setChangedBy(getUserForAudit());
				concept.setDateChanged(new Date());

				// track changes
				needsSaving = true;
				modified++;
			} else {
				unchanged++;
			}
		}

		if (needsSaving) {
			// update the concept in the database
			concept.setRetired(Boolean.FALSE);
			chitsService.saveConceptForcingDatatype(concept);
		}

		// send back the date concept
		return concept;
	}

	public Concept loadOrCreateTextConceptQuestion(CachedConceptId code) {
		return loadOrCreateConcept(code, null, code.getConceptName(), "Misc", "Text");
	}

	public Concept loadOrCreateTextConceptQuestion(CachedConceptId code, String shortName, String description) {
		return loadOrCreateConcept(code, shortName, description, "Misc", "Text");
	}

	public Concept loadOrCreateConcept(CachedConceptId code, String shortName, String description, String className, String dataType) {
		log.info(String.format("loadOrCreateTextConceptQuestion(%s, %s, %s)", code, shortName, description));

		// ensure the concept is defined
		Concept concept = conceptService.getConcept(code.getConceptName());
		boolean needsSaving = false;
		if (concept == null) {
			// create a concept for this entry
			concept = newConcept(code.getConceptName(), description, shortName, className, dataType);
			concept.setCreator(getUserForAudit());
			concept.setDateCreated(new Date());
			concept.setUuid(UUID.randomUUID().toString());
			needsSaving = true;
		} else {
			boolean changed = updateConceptIfNeeded(concept, description, className, dataType);
			changed |= !Boolean.FALSE.equals(concept.isRetired());

			if (shortName != null && !shortName.trim().equals("") && !shortName.trim().equalsIgnoreCase(concept.getName().getName().trim())) {
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

					shortNameConcept.setCreator(getUserForAudit());
					shortNameConcept.setDateCreated(new Date());
					shortNameConcept.setUuid(UUID.randomUUID().toString());

					concept.setShortName(shortNameConcept);
					changed = true;
				} else {
					if (!shortNameConcept.getName().equals(shortName)) {
						shortNameConcept.setName(shortName);
						shortNameConcept.setChangedBy(getUserForAudit());
						shortNameConcept.setDateChanged(new Date());
						changed = true;
					}
				}
			}

			if (changed) {
				// update the concept data
				concept.setChangedBy(getUserForAudit());
				concept.setDateChanged(new Date());

				// track changes
				needsSaving = true;
				modified++;
			} else {
				unchanged++;
			}
		}

		if (needsSaving) {
			// update the concept in the database
			concept.setRetired(Boolean.FALSE);
			chitsService.saveConceptForcingDatatype(concept);
		}

		// send back the date concept
		return concept;
	}

	/**
	 * Initialize a new Concept instance.
	 * 
	 * @param code
	 * @param description
	 * @param shortName
	 * @param conceptClassName
	 * @param conceptDataTypeName
	 * @return the new Concept instance.
	 */
	public Concept newConcept(String name, String description, String shortName, String conceptClassName, String conceptDataTypeName) {
		log.info(String.format("newConcept(%s, %s, %s, %s, %s)", name, description, shortName, conceptClassName, conceptDataTypeName));

		final Concept concept = new Concept();

		final Date now = new Date();
		final ConceptName conceptName = new ConceptName();
		conceptName.setConcept(concept);
		conceptName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		conceptName.setName(name);
		conceptName.setLocale(Constants.ENGLISH);
		conceptName.setLocalePreferred(Boolean.FALSE);
		conceptName.setVoided(Boolean.FALSE);
		conceptName.setTags(new HashSet<ConceptNameTag>());
		conceptName.setCreator(getUserForAudit());
		conceptName.setDateCreated(now);
		conceptName.setUuid(UUID.randomUUID().toString());

		final ConceptName shortNameConcept = new ConceptName();
		shortNameConcept.setConcept(concept);
		shortNameConcept.setConceptNameType(ConceptNameType.SHORT);
		shortNameConcept.setName(shortName);
		shortNameConcept.setLocale(Constants.ENGLISH);
		shortNameConcept.setLocalePreferred(Boolean.FALSE);
		shortNameConcept.setVoided(Boolean.FALSE);
		shortNameConcept.setTags(new HashSet<ConceptNameTag>());
		shortNameConcept.setCreator(getUserForAudit());
		shortNameConcept.setDateCreated(now);
		shortNameConcept.setUuid(UUID.randomUUID().toString());

		// setup default description
		final ConceptDescription conceptDescription = new ConceptDescription();
		conceptDescription.setConcept(concept);
		conceptDescription.setDescription(description);
		conceptDescription.setLocale(Constants.ENGLISH);
		conceptDescription.setCreator(getUserForAudit());
		conceptDescription.setDateCreated(now);
		conceptDescription.setUuid(UUID.randomUUID().toString());

		// setup the concept
		ConceptClass cc = conceptService.getConceptClassByName(conceptClassName);
		if (cc == null) {
			log.warn("Ignoring unknown concept class: '" + conceptClassName + "' in favor of 'Misc'");
			cc = conceptService.getConceptClassByName("Misc");
		}
		concept.setConceptClass(cc);

		ConceptDatatype cd = conceptService.getConceptDatatypeByName(conceptDataTypeName);
		if (cd == null) {
			log.warn("Ignoring unknown concept datatype: '" + conceptClassName + "' in favor of 'N/A'");
			cd = conceptService.getConceptDatatypeByName("N/A");
		}

		concept.setDatatype(cd);
		concept.setFullySpecifiedName(conceptName);
		concept.addDescription(conceptDescription);
		concept.setAnswers(new HashSet<ConceptAnswer>());
		concept.setConceptMappings(new HashSet<ConceptMap>());
		concept.setSet(Boolean.FALSE);
		concept.setConceptSets(new HashSet<ConceptSet>());
		concept.addName(conceptName);
		concept.setPreferredName(conceptName);
		concept.setRetired(Boolean.FALSE);

		if (shortName != null && !shortName.trim().equals("") && !shortName.trim().equalsIgnoreCase(name.trim())) {
			// add 'short name' only if specified!
			concept.setShortName(shortNameConcept);
		}

		// create other required fields
		concept.setCreator(getUserForAudit());
		concept.setDateCreated(new Date());
		concept.setRetired(Boolean.FALSE);
		concept.setUuid(UUID.randomUUID().toString());

		// track changes
		added++;

		// send back the newly instantiated instance
		return concept;
	}

	/**
	 * Returns a mapping of concept answer IDs to {@link ConceptAnswer} beans sorted by the sort weight.
	 * 
	 * @param conceptQuestionName
	 *            The name of the concept question to get the answers of
	 * @return A {@link LinkedHashMap} of concept IDs (as strings) to {@link ConceptAnswer} beans.
	 */
	public Map<String, ConceptAnswer> getConceptAnswersById(CachedConceptId cachedConceptId) {
		// get the concept question's answers sorted by sortWeight
		final Concept conceptQuestion = conceptService.getConcept(cachedConceptId.getConceptId());
		final List<ConceptAnswer> answers = new ArrayList<ConceptAnswer>(conceptQuestion.getAnswers());
		Collections.sort(answers);

		// store the answers keyed by IDs in a LinkedHashMap to retain order
		final Map<String, ConceptAnswer> conceptAnswers = new LinkedHashMap<String, ConceptAnswer>();
		for (ConceptAnswer answer : answers) {
			conceptAnswers.put(Integer.toString(answer.getAnswerConcept().getConceptId()), answer);
		}

		// return a mapping of concept answers
		return conceptAnswers;
	}

	/**
	 * Return the last taken observations given from the set of encounters.
	 * <p>
	 * This method assumes that the encounters are ordered from descending date!
	 * 
	 * @param encounters
	 *            The set of encounters to search the last known observations of
	 * @return The last known observations for the given concept names found in the encounters.
	 */
	public static Map<Integer, Obs> getLastTakenObservations(Collection<Encounter> encounters, Collection<CachedConceptId> concepts) {
		// get mapping of last taken observations by concept name
		final Map<Integer, Obs> observations = new LinkedHashMap<Integer, Obs>();

		// search for a limited (pre-defined) set of concepts required to display in the 'Patient Chart' area
		final Set<Integer> searchConceptIds = new HashSet<Integer>();
		for (CachedConceptId ccid : concepts) {
			searchConceptIds.add(ccid.getConceptId());
		}

		for (Encounter enc : encounters) {
			for (Obs obs : enc.getAllObs()) {
				final Integer obsConceptId = obs.getConcept().getConceptId();
				if (searchConceptIds.contains(obsConceptId)) {
					Obs lastTakenObs = observations.get(obs.getConcept().getId());
					if (lastTakenObs == null || lastTakenObs.getObsDatetime().compareTo(obs.getObsDatetime()) < 0) {
						// this is the last taken measurements
						observations.put(obs.getConcept().getId(), obs);
					}
				}
			}
		}

		// return the last taken observations
		return observations;
	}

	/**
	 * Return the observations as a map keyed by concept name.
	 * <p>
	 * NOTE: Some observations may appear multiple times in an encounter, this method will only return the last observation.
	 * 
	 * @param encounter
	 *            The encounter containing the observations
	 * @return A map of the encounter observations keyed by concept name
	 */
	public static Map<Integer, Obs> getObservationsAsMap(Encounter encounter) {
		// prepare a map of observations keyed by concept name
		final Map<Integer, Obs> observations = new LinkedHashMap<Integer, Obs>();

		// store each observation in the map
		for (Obs obs : encounter.getAllObs()) {
			// store in a map of observations keyed by concept name
			observations.put(obs.getConcept().getConceptId(), obs);
		}

		// return the observations keyed by concept name
		return observations;
	}

	/**
	 * Return the enums as a map keyed by enum value
	 * 
	 * @param enumType
	 *            The enum type to convert to a map
	 * @return The enums as a map keyed by enum value.
	 */
	public static Map<String, Object> asMap(Class<?> enumType) {
		// store enums in hashmap keyed by enum value
		final Map<String, Object> enumsAsMap = new LinkedHashMap<String, Object>();
		try {
			final Object[] values = (Object[]) enumType.getMethod("values").invoke(null);
			for (Object enumEntry : values) {
				enumsAsMap.put(enumEntry.toString(), enumEntry);
			}
		} catch (Exception e) {
			log.warn("Unable to convert enum type to map: " + enumType, e);
		}

		// return the enums as a map keyed by enum value
		return enumsAsMap;
	}

	/**
	 * Stores the classes 'static final' field values as a map.
	 * 
	 * @param clazz
	 *            The class containing static field values.
	 * @return A map keyed by static field name to value.
	 */
	public static Map<String, Object> constantsAsMap(Class<?> clazz) {
		try {
			final Map<String, Object> constants = new HashMap<String, Object>();
			final int staticFinalMods = Modifier.STATIC | Modifier.FINAL;
			for (Field field : clazz.getFields()) {
				if (staticFinalMods == (field.getModifiers() & staticFinalMods)) {
					// this is a constant!
					constants.put(field.getName(), field.get(null));
				}
			}

			return constants;
		} catch (Exception e) {
			// wrap in general error
			throw new IllegalStateException("Unable to initialize class constants for: " + clazz);
		}
	}

	/**
	 * Updates a concepts description, class name, or datatype if they don't match the given parameters.
	 * <p>
	 * Additionally, this method clears the 'retired' flag if it is currently set.
	 * 
	 * @param concept
	 *            The concept to work with
	 * @param description
	 *            The description the concept should have (optional)
	 * @param conceptClassName
	 *            The class name the concept should have (optional)
	 * @param conceptDatatype
	 *            The data type the concept should have (optional)
	 * @return If the concept was changed because some of the attributes didn't match the parameters
	 */
	public boolean updateConceptIfNeeded(Concept concept, String description, String conceptClassName, String conceptDatatype) {
		log.info(String.format("updateConceptIfNeeded(%d, %s, %s, %s)", concept.getId(), description, conceptClassName, conceptDatatype));

		boolean changed = false;
		if (description != null && concept.getDescription() == null) {
			final ConceptDescription cd = new ConceptDescription(description, Context.getLocale());
			cd.setCreator(getUserForAudit());
			cd.setDateCreated(new Date());
			cd.setUuid(UUID.randomUUID().toString());

			// add a new description
			concept.addDescription(cd);
			changed = true;
		} else if (description != null && !description.equals(concept.getDescription().getDescription())) {
			concept.getDescription().setDescription(description);
			concept.getDescription().setChangedBy(getUserForAudit());
			concept.getDescription().setDateChanged(new Date());
			changed = true;
		}

		if (conceptDatatype != null && !conceptDatatype.equalsIgnoreCase(concept.getDatatype().getName())) {
			final ConceptDatatype cd = conceptService.getConceptDatatypeByName(conceptDatatype);
			if (cd == null) {
				log.warn("Ignoring unknown concept datatype: " + conceptClassName);
			} else {
				// change datatype
				concept.setDatatype(cd);
				changed = true;
			}
		}

		if (conceptClassName != null && !conceptClassName.equalsIgnoreCase(concept.getConceptClass().getName())) {
			final ConceptClass cc = conceptService.getConceptClassByName(conceptClassName);
			if (cc == null) {
				log.warn("Ignoring unknown concept class: " + conceptClassName);
			} else {
				concept.setConceptClass(cc);
				changed = true;
			}
		}

		if (concept.isRetired()) {
			// un-"retire" the concept
			concept.setRetired(Boolean.FALSE);
			changed = true;
		}

		// track changes
		if (changed) {
			modified++;
		} else {
			unchanged++;
		}

		return changed;
	}

	/**
	 * Makes sure a concept answer is a member of the question concept contains the answer concept and save the question concept if pairing was needed.
	 * 
	 * @param question
	 * @param answer
	 */
	public boolean pairQuestionAndAnswerIfNeeded(Concept question, CachedConceptId answer) {
		return pairQuestionAndAnswerIfNeeded(question, answer, true);
	}

	/**
	 * Makes sure a concept answer is a member of the question concept contains the answer concept.
	 * 
	 * @param question
	 * @param answer
	 * @param forceSave
	 */
	public boolean pairQuestionAndAnswerIfNeeded(Concept question, CachedConceptId answer, boolean forceSave) {
		return pairQuestionAndAnswerIfNeeded(question, conceptService.getConcept(answer.getConceptId()), forceSave);
	}

	/**
	 * Makes sure a concept answer is a member of the question concept contains the answer concept and save the question concept if pairing was needed.
	 * 
	 * @param question
	 * @param answer
	 */
	public boolean pairQuestionAndAnswerIfNeeded(Concept question, Concept answer) {
		return pairQuestionAndAnswerIfNeeded(question, answer, true);
	}

	public boolean pairQuestionAndAnswerIfNeeded(Concept question, Concept answer, boolean forceSave) {
		return pairQuestionAndAnswerIfNeeded(question, answer, null, forceSave);
	}

	/**
	 * Makes sure a concept answer is a member of the question concept contains the answer concept.
	 * 
	 * @param question
	 * @param answer
	 */
	public boolean pairQuestionAndAnswerIfNeeded(Concept question, Concept answer, Double sortWeight, boolean forceSave) {
		log.info(String.format("pairQuestionAndAnswerIfNeeded(%s, %s)", question.getName().getName(), answer.getName().getName()));

		// check to see if the visit complaints contains this as an answer
		boolean answerFound = false;
		ConceptAnswer matchingAnswer = null;
		for (ConceptAnswer ca : question.getAnswers()) {
			if (answer.equals(ca.getAnswerConcept())) {
				matchingAnswer = ca;
				answerFound = true;
				break;
			}
		}

		if (!answerFound) {
			// attach this answer to the question
			final ConceptAnswer conceptAnswer = new ConceptAnswer();
			conceptAnswer.setAnswerConcept(answer);
			conceptAnswer.setConcept(question);
			conceptAnswer.setCreator(getUserForAudit());
			conceptAnswer.setDateCreated(new Date());
			conceptAnswer.setUuid(UUID.randomUUID().toString());
			conceptAnswer.setSortWeight(sortWeight);
			question.addAnswer(conceptAnswer);

			if (forceSave) {
				// update the question to contain the answer
				question = chitsService.saveConceptForcingDatatype(question);
			}

			// track changes
			modified++;
		} else if (sortWeight != null && !sortWeight.equals(matchingAnswer.getSortWeight())) {
			// update the sort weight
			matchingAnswer.setSortWeight(sortWeight);

			// track changes
			modified++;
		} else {
			unchanged++;
		}

		// if answer was not found, then a change was made
		return !answerFound;
	}

	/**
	 * Makes sure the (member) concept is a member of the convenience set and save the set concept if pairing was needed.
	 * <p>
	 * NOTE: The corresponding concept for the set must already exist.
	 * 
	 * @param questionId
	 * @param answerId
	 */
	public boolean pairSetMemberIfNeeded(CachedConceptId setId, CachedConceptId memberId) {
		return pairSetMemberIfNeeded(setId, memberId, true);
	}

	/**
	 * Makes sure the (member) concept is a member of the convenience set.
	 * <p>
	 * NOTE: The corresponding concept for the set must already exist.
	 * 
	 * @param questionId
	 * @param answerId
	 * @param forceSave
	 */
	public boolean pairSetMemberIfNeeded(CachedConceptId setId, CachedConceptId memberId, boolean forceSave) {
		final Concept set = conceptService.getConcept(setId.getConceptId());
		final Concept member = conceptService.getConcept(memberId.getConceptId());
		if (set == null || member == null) {
			throw new IllegalArgumentException("One or more concepts do not exist (" + setId + " or " + memberId
					+ ").  Make sure the concepts have been created before pairing them.");
		}

		return pairSetMemberIfNeeded(set, member, forceSave);
	}

	/**
	 * Makes sure the (member) concept is a member of the convenience set and save the set if pairing was needed.
	 * 
	 * @param question
	 * @param answer
	 */
	public boolean pairSetMemberIfNeeded(Concept set, Concept member) {
		return pairSetMemberIfNeeded(set, member, true);
	}

	public boolean pairSetMemberIfNeeded(Concept set, Concept member, boolean forceSave) {
		return pairSetMemberIfNeeded(set, member, null, forceSave);
	}

	/**
	 * Makes sure the (member) concept is a member of the convenience set.
	 * 
	 * @param question
	 * @param answer
	 * @param forceSave
	 *            if the set should be saved if pairing was needed
	 */
	public boolean pairSetMemberIfNeeded(Concept set, Concept member, Integer index, boolean forceSave) {
		log.info(String.format("pairSetMemberIfNeeded(%s, %s)", set.getName().getName(), member.getName().getName()));

		boolean changed = false;
		if (set.isSet() == null || !set.isSet()) {
			// set should be a set!
			set.setSet(Boolean.TRUE);
			changed = true;
		}

		// check to see if the set contains the member
		if (!set.getSetMembers().contains(member)) {
			addSetMember(set, member, index);
			changed = true;
		} else if (index != null && set.getSetMembers().indexOf(member) != index) {
			// reposition the set member
			unpairSetMemberIfNeeded(set, member);
			addSetMember(set, member, index);
			changed = true;
		}

		if (changed) {
			if (forceSave) {
				// update the set to contain the member
				set = chitsService.saveConceptForcingDatatype(set);
			}

			// track changes
			modified++;
		} else {
			unchanged++;
		}

		// return whether the set was changed
		return changed;
	}

	public void addSetMember(Concept set, Concept member) {
		addSetMember(set, member, null);
	}

	/**
	 * Adds the given member to the set. This convenience method also sets the 'creator' and (optionally) the 'changedBy' attribute to the daemon user if there
	 * is currently no authenticated user (i.e., in case being run from startup.)
	 * 
	 * @param set
	 *            The set to add the member to
	 * @param member
	 *            The concept to add to the set
	 */
	public void addSetMember(Concept set, Concept member, Integer index) {
		// set the 'creator' attribute of the set in case we are being run from within the daemon thread
		if (index == null) {
			// add to end
			set.addSetMember(member);
		} else {
			// use the index
			set.addSetMember(member, index);
		}

		final User user = getUserForAudit();
		for (ConceptSet cs : set.getConceptSets()) {
			if (cs.getCreator() == null) {
				cs.setCreator(user);
			}

			if (cs.getDateCreated() == null) {
				cs.setDateCreated(new Date());
			}

			if (cs.getUuid() == null) {
				cs.setUuid(UUID.randomUUID().toString());
			}

			if (cs.getDateChanged() != null && cs.getChangedBy() == null) {
				cs.setChangedBy(user);
			}
		}
	}

	/**
	 * Makes sure a concept member is not a member of the question set.
	 * 
	 * @param question
	 * @param answer
	 */
	public boolean unpairSetMemberIfNeeded(Concept set, Concept member) {
		log.info(String.format("unpairSetMemberIfNeeded(%s, %s)", set.getName().getName(), member.getName().getName()));

		boolean changed = false;
		if (set.isSet() == null || !set.isSet()) {
			// set should be a set!
			set.setSet(Boolean.TRUE);
			changed = true;
		}

		// this set should not be retired
		if (set.isRetired() != null && set.isRetired()) {
			set.setRetired(Boolean.FALSE);
			changed = true;
		}

		// remove if a member of this set
		for (ConceptSet cs : set.getConceptSets()) {
			if (cs.getConcept().equals(member)) {
				changed = true;

				// NOTE: concept sets has a cascade type of 'all,delete-orphan' so there's no need to manually purge the ConceptSet
				set.getConceptSets().remove(cs);

				// break away immediately to avoid an error since we are iterating over this set!
				break;
			}
		}

		if (changed) {
			// update the set to NOT contain the member
			set = chitsService.saveConceptForcingDatatype(set);

			// track changes
			modified++;
		} else {
			unchanged++;
		}

		// return whether the set was changed
		return changed;
	}

	/**
	 * Adds a synonym to a concept and returns the new ConceptName instance.
	 */
	public ConceptName addSynonym(Concept concept, String synonym, Locale locale) {
		final Concept testSynonymConcept = conceptService.getConceptByName(synonym);
		if (testSynonymConcept != null) {
			if (concept.equals(testSynonymConcept)) {
				// no need to add the synonym
				log.info("Synonym '" + synonym + "' already associated with concept: " + concept);
			} else {
				log.warn("Synonym '" + synonym + "' already in use by another concept: " + testSynonymConcept);
			}

			// track changes
			unchanged++;

			// synonym not added!
			return null;
		} else {
			// create the synonym which as a 'null' concept name type!
			final ConceptName synonymCN = new ConceptName();
			synonymCN.setName(synonym);
			synonymCN.setConcept(concept);
			synonymCN.setLocale(locale);
			synonymCN.setConceptNameType(null);
			synonymCN.setVoided(Boolean.FALSE);
			synonymCN.setUuid(UUID.randomUUID().toString());
			synonymCN.setCreator(getUserForAudit());
			synonymCN.setDateCreated(new Date());
			concept.addName(synonymCN);

			log.info("Synonym '" + synonym + "' added to concept: " + concept);

			// track changes
			added++;

			// return the concept name we created
			return synonymCN;
		}
	}

	public void defineAttributeTypeIfMissing(String attributeName, String description, Integer foreignKey, double sortWeight) {
		// get the 'edit patients' privilege
		final Privilege editPrivilege = userService.getPrivilege(PrivilegeConstants.EDIT_PATIENTS);

		boolean needsSaving = false;
		PersonAttributeType personAttrType = personService.getPersonAttributeTypeByName(attributeName);
		if (personAttrType == null) {
			// create missing attribute types
			personAttrType = new PersonAttributeType();
			personAttrType.setCreator(getUserForAudit());
			personAttrType.setDateCreated(new Date());
			personAttrType.setUuid(UUID.randomUUID().toString());

			needsSaving = true;
			added++;
		} else {
			boolean changed = false;
			changed |= !attributeName.equals(personAttrType.getName());
			changed |= !description.equals(personAttrType.getDescription());

			if (foreignKey == null) {
				changed |= !"java.lang.String".equals(personAttrType.getFormat());
				changed |= personAttrType.getForeignKey() != null;
			} else {
				changed |= !"org.openmrs.Concept".equals(personAttrType.getFormat());
				changed |= !foreignKey.equals(personAttrType.getForeignKey());
			}

			changed |= !Boolean.FALSE.equals(personAttrType.getSearchable());
			changed |= sortWeight != personAttrType.getSortWeight();
			changed |= !editPrivilege.equals(personAttrType.getEditPrivilege());
			changed |= !Boolean.FALSE.equals(personAttrType.getRetired());

			if (changed) {
				personAttrType.setChangedBy(getUserForAudit());
				personAttrType.setDateChanged(new Date());

				needsSaving = true;
				modified++;
			} else {
				unchanged++;
			}
		}

		if (needsSaving) {
			// make sure person attributes are up-to-date
			personAttrType.setName(attributeName);
			personAttrType.setDescription(description);
			personAttrType.setFormat(foreignKey == null ? "java.lang.String" : "org.openmrs.Concept");
			personAttrType.setForeignKey(foreignKey);
			personAttrType.setSearchable(false);
			personAttrType.setSortWeight(sortWeight);
			personAttrType.setEditPrivilege(editPrivilege);
			personAttrType.setRetired(Boolean.FALSE);
			personService.savePersonAttributeType(personAttrType);
		}
	}

	public void defineGlobalPropertyIfNotSet(String name, String value) {
		GlobalProperty gp = adminService.getGlobalPropertyObject(name);
		if (gp != null) {
			final String oldValue = gp.getPropertyValue();
			if (oldValue == null || !oldValue.equals(value)) {
				log.info("Updating global property: " + name + "=" + value);
				gp.setPropertyValue(value);
				adminService.saveGlobalProperty(gp);

				// track changes
				modified++;
			} else {
				// track changes
				unchanged++;
			}
		} else {
			// define new global property
			log.info("Defining global property: " + name + "=" + value);
			gp = new GlobalProperty();
			gp.setProperty(name);
			gp.setDescription(name);
			gp.setPropertyValue(value);
			adminService.saveGlobalProperty(gp);

			// track changes
			added++;
		}
	}

	/**
	 * Defines or updates a relationship type in the database.
	 * 
	 * @param relationshipTypeName
	 *            The relationship type name (requires a single backslash separating the 'a is to b' and 'b is to a' component names; e..g, 'Parent/Child')
	 * @param description
	 *            The description of the relationship type.
	 */
	public void defineOrUpdateRelationshipType(String relationshipTypeName, String description) {
		// split the 'a is to b' and 'b is to a' component names
		final String[] parts = relationshipTypeName.split("/");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Invalid relationship type name (expected <A>/<B> format): " + relationshipTypeName);
		}

		final String aIsToB = parts[0];
		final String bIsToA = parts[1];

		RelationshipType type = personService.getRelationshipTypeByName(relationshipTypeName);
		boolean needsSaving = false;
		if (type == null) {
			// initialize a new entity for saving
			type = new RelationshipType();
			type.setCreator(getUserForAudit());
			type.setDateCreated(new Date());
			type.setUuid(UUID.randomUUID().toString());
			type.setName(relationshipTypeName);

			// track changes and flag for saving
			added++;
			needsSaving = true;
		} else {
			// check if relationship type attributes don't match
			needsSaving |= !aIsToB.equals(type.getaIsToB());
			needsSaving |= !bIsToA.equals(type.getbIsToA());
			needsSaving |= !description.equals(type.getDescription());
			needsSaving |= !Boolean.FALSE.equals(type.isPreferred());
			needsSaving |= type.getWeight() != 0;

			if (needsSaving) {
				// existing type will be modified
				type.setDateChanged(new Date());
				type.setChangedBy(getUserForAudit());

				// track changes
				modified++;
			}
		}

		if (needsSaving) {
			// update attributes
			type.setaIsToB(aIsToB);
			type.setbIsToA(bIsToA);
			type.setDescription(description);
			type.setPreferred(Boolean.FALSE);
			type.setWeight(0);

			// persist in database
			personService.saveRelationshipType(type);
		}
	}

	public void defineOrUpdateProgram(CachedProgramConceptId cachedProgramConceptId) {
		// check if program's concept is already defined
		final String programName = cachedProgramConceptId.getConceptName();
		Concept programConcept = conceptService.getConcept(programName);
		if (programConcept == null) {
			programConcept = newConcept(programName, programName, programName, "Misc", "N/A");
			conceptService.saveConcept(programConcept);
		} else if (updateConceptIfNeeded(programConcept, programName, "Misc", "N/A")) {
			conceptService.saveConcept(programConcept);
		}

		// also need to make sure that the "Chits program workflow" concept is available
		final String workflowName = ProgramConcepts.PROGRAM_WORKFLOW.getConceptName();
		Concept workflowConcept = conceptService.getConcept(workflowName);
		if (workflowConcept == null) {
			workflowConcept = newConcept(workflowName, workflowName, workflowName, "Misc", "N/A");
			conceptService.saveConcept(workflowConcept);
		} else if (updateConceptIfNeeded(workflowConcept, workflowName, "Misc", "N/A")) {
			conceptService.saveConcept(workflowConcept);
		}

		// update program if needed
		final Program program = defineOrUpdateProgram(programConcept, workflowConcept, programName, programName);

		// update program workflow states, if needed (NOTE: The defineOrUpdateProgram() call above ensures that the workflow already exists in this program)
		final ProgramWorkflow workflow = program.getWorkflowByName(workflowName);
		defineOrUpdateProgramWorkflowStates(program, workflow, cachedProgramConceptId.getStates());
	}

	private Program defineOrUpdateProgram(Concept programConcept, Concept workflowConcept, String name, String description) {
		boolean needsSaving = false;

		// check if program already defined
		Program program = programWorkflowService.getProgramByName(name);
		ProgramWorkflow workflow = null;
		if (program == null) {
			// define a new program
			program = new Program();
			program.setUuid(UUID.randomUUID().toString());
			program.setCreator(getUserForAudit());
			program.setDateCreated(new Date());

			// mark as needing to be saved
			needsSaving = true;
			added++;
		} else {
			// does program refer to the correct concept?
			boolean changed = false;
			changed |= !name.equals(program.getName());
			changed |= !description.equals(program.getDescription());
			changed |= !programConcept.equals(program.getConcept());
			changed |= !Boolean.FALSE.equals(program.getRetired());

			if (changed) {
				programConcept.setChangedBy(getUserForAudit());
				programConcept.setDateChanged(new Date());

				needsSaving = true;
				modified++;
			} else {
				unchanged++;
			}
		}

		// does workflow exist?
		final String workflowName = ProgramConcepts.PROGRAM_WORKFLOW.getConceptName();
		workflow = program.getWorkflowByName(workflowName);
		if (workflow == null) {
			// create the program workflow
			workflow = new ProgramWorkflow();
			workflow.setUuid(UUID.randomUUID().toString());
			workflow.setCreator(getUserForAudit());
			workflow.setDateCreated(new Date());
			program.addWorkflow(workflow);

			needsSaving = true;
			added++;
		} else {
			// does workflow refer to the correct concept?
			boolean changed = false;

			// check for changes needed (NOTE: 'workflow' doesn't store 'name' and 'description' directly -- instead it uses the concept name)
			// changed |= !workflowName.equals(workflow.getName());
			// changed |= !workflowName.equals(workflow.getDescription());
			changed |= !workflowConcept.equals(workflow.getConcept());
			changed |= !program.equals(workflow.getProgram());
			changed |= !Boolean.FALSE.equals(workflow.getRetired());

			if (changed) {
				workflow.setChangedBy(getUserForAudit());
				workflow.setDateChanged(new Date());

				needsSaving = true;
				modified++;
			} else {
				unchanged++;
			}
		}

		if (needsSaving) {
			// update program
			program.setName(name);
			program.setDescription(description);
			program.setConcept(programConcept);
			program.setRetired(Boolean.FALSE);

			// update workflow
			workflow.setName(workflowName);
			workflow.setDescription(workflowName);
			workflow.setConcept(workflowConcept);
			workflow.setProgram(program);
			workflow.setRetired(Boolean.FALSE);

			// save
			program = programWorkflowService.saveProgram(program);
		}

		// send back the program
		return program;
	}

	private void defineOrUpdateProgramWorkflowStates(Program program, ProgramWorkflow workflow, CachedConceptId[] states) {
		// determine the last state
		final CachedConceptId firstState = states[0];
		final CachedConceptId lastState = states[states.length - 1];

		// check each state
		boolean needsSaving = false;
		for (CachedConceptId stateId : states) {
			final Boolean initial = (stateId == firstState);
			final Boolean terminal = (stateId == lastState);

			// make sure a concept exists for this state's name
			final String stateName = stateId.getConceptName();
			Concept stateConcept = conceptService.getConcept(stateName);
			if (stateConcept == null) {
				stateConcept = newConcept(stateName, stateName, stateName, "Misc", "N/A");
				conceptService.saveConcept(stateConcept);
			} else if (updateConceptIfNeeded(stateConcept, stateName, "Misc", "N/A")) {
				conceptService.saveConcept(stateConcept);
			}

			// does workflow state exist?
			ProgramWorkflowState state = workflow.getStateByName(stateName);
			boolean stateNeedsChanging = false;
			if (state == null) {
				// create the program workflow state
				state = new ProgramWorkflowState();
				state.setUuid(UUID.randomUUID().toString());
				state.setCreator(getUserForAudit());
				state.setDateCreated(new Date());
				workflow.addState(state);

				needsSaving = stateNeedsChanging = true;
				added++;
			} else {
				// does workflow state refer to the correct concept?
				boolean changed = false;

				// check for changes needed (NOTE: 'state' doesn't store 'name' and 'description' directly -- instead it uses the concept name)
				// changed |= !stateName.equals(state.getName());
				// changed |= !stateName.equals(state.getDescription());
				changed |= !stateConcept.equals(state.getConcept());
				changed |= !Boolean.FALSE.equals(state.getRetired());
				changed |= !workflow.equals(state.getProgramWorkflow());
				changed |= !initial.equals(state.getInitial());
				changed |= !terminal.equals(state.getTerminal());

				if (changed) {
					state.setChangedBy(getUserForAudit());
					state.setDateChanged(new Date());

					needsSaving = stateNeedsChanging = true;
					modified++;
				} else {
					unchanged++;
				}
			}

			if (stateNeedsChanging) {
				// update all state fields (these will be saved later during the cascade-save of the program)
				state.setName(stateName);
				state.setDescription(stateName);
				state.setConcept(stateConcept);
				state.setRetired(Boolean.FALSE);
				state.setProgramWorkflow(workflow);
				state.setInitial(initial);
				state.setTerminal(terminal);
			}
		}

		if (needsSaving) {
			// cascade-save the workflow states by saving the program
			program = programWorkflowService.saveProgram(program);
		}
	}

	public User getUserForAudit() {
		if (user == null) {
			// get and cache the authenticated user
			user = Context.getAuthenticatedUser();
		}

		if (user == null) {
			// assume the daemon user is running
			user = userService.getUserByUuid("A4F30A1B-5EB9-11DF-A648-37A07F9C90FB");
		}

		return user;
	}

	public int getAdded() {
		return added;
	}

	public int getModified() {
		return modified;
	}

	public int getUnchanged() {
		return unchanged;
	}
}
