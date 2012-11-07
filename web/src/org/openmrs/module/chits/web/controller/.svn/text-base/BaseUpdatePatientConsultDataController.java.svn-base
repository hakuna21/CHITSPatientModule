package org.openmrs.module.chits.web.controller;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Auditable;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.OpenmrsData;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.propertyeditor.UserEditor;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.controller.genconsults.ViewPatientConsultsController;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Generic data form controller for editing a patient consult form.
 * <p>
 * NOTE: This controller does not set-up the form object to be able to render the visits section (@see {@link ViewPatientConsultsController}).
 * <p>
 * Instead, it only populates the patientQueue and encounters attributes of the {@link PatientConsultEntryForm} via the
 * {@link ViewPatientConsultsController#initPatientConsultFormBackingObject(Integer, org.openmrs.module.chits.PatientConsultForm)} method.
 */
public abstract class BaseUpdatePatientConsultDataController<T extends PatientConsultEntryForm> implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Decimal format: when entering numeric data, limit all fields to a maximum of 4 decimal places */
	public static final DecimalFormat FMT = new DecimalFormat("0.####");

	/** Auto-wired consults controller for initializing standard consults form backing object */
	protected ViewPatientConsultsController viewPatientConsultsController;

	/** Auto-wired CHITS service */
	protected CHITSService chitsService;

	/** Auto-wired concept service */
	protected ConceptService conceptService;

	/** Auto-wired encounter service */
	protected EncounterService encounterService;

	/**
	 * Returns a path to reload the input page.
	 * 
	 * @return The path to reload the input page.
	 */
	protected abstract String getInputPath(HttpServletRequest request);

	/**
	 * Returns a path to redirect to for reloading the page after a successful POST.
	 * 
	 * @param patientId
	 *            The patient ID in case needed for the redirect page
	 * @return The path to redirect to for reloading the page after a successful POST.
	 */
	protected abstract String getReloadPath(HttpServletRequest request, Integer patientId);

	/** Time format */
	protected static final DateFormat TIME_FMT = new SimpleDateFormat("hh:mma");

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		final NumberFormat nf = NumberFormat.getInstance(Context.getLocale());
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(org.openmrs.Concept.class, new ConceptEditor());
		binder.registerCustomEditor(org.openmrs.User.class, new UserEditor());
	}

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * <p>
	 * NOTE: Subclasses are expected to override this method to store the concepts to be edited into the form.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	@SuppressWarnings("unchecked")
	public T formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId) throws ServletException {
		// prepare the patient's form
		final PatientConsultEntryForm form = new PatientConsultEntryForm();

		// initialize standard consults form backing object (to setup the encounters and patient queue attributes).
		viewPatientConsultsController.initPatientConsultFormBackingObject(patientId, form);

		// set the default date / time for the form
		final Date now = new Date();
		form.setTimestampDate(Context.getDateFormat().format(now));
		form.setTimestampTime(TIME_FMT.format(now));

		// if an encounter is available, use the encounters date as default
		if (form.getPatientQueue() != null && form.getPatientQueue().getEncounter() != null) {
			form.setTimestampDate(Context.getDateFormat().format(form.getPatientQueue().getEncounter().getEncounterDatetime()));
		}

		if (form.getPatient() != null) {
			// attach the associated family folders of this patient
			final List<FamilyFolder> familyFolders = new ArrayList<FamilyFolder>();
			model.addAttribute("familyFolders", familyFolders);

			// set the patient's associated family folders
			familyFolders.addAll(chitsService.getFamilyFoldersOf(form.getPatient().getPatientId()));
		}

		// return the patient
		return (T) form;
	}

	/**
	 * Helper method that stores observations for the given concepts into the form's map creating new Obs instances if they don't exist in the form's
	 * PatientQueue encounter.
	 * 
	 * @param form
	 *            The form to store the observations into
	 * @param encounterOrObs
	 *            An {@link Encounter} or {@link Obs} containing existing {@link Obs} beans instances as a subset or superset of the conceptsToStore concepts.
	 * @param conceptsToStore
	 *            The concept names to store into the form.
	 * @return A List of Obs instances that were added to the form
	 */
	protected List<Obs> setupFormObservations(T form, Object encounterOrObs, CachedConceptId... conceptsToStore) {
		return setupFormObservations(form, encounterOrObs, Arrays.asList(conceptsToStore));
	}

	/**
	 * Helper method that stores observations for the given concepts into the form's map creating new Obs instances if they don't exist in the form's
	 * PatientQueue encounter.
	 * 
	 * @param form
	 *            The form to store the observations into
	 * @param encounterOrObs
	 *            An {@link Encounter} or {@link Obs} containing existing {@link Obs} beans instances as a subset or superset of the conceptsToStore concepts.
	 * @param conceptsToStore
	 *            The concept names to store into the form.
	 * @return A List of Obs instances that were added to the form
	 */
	protected List<Obs> setupFormObservations(T form, Object encounterOrObs, Collection<CachedConceptId> conceptsToStore) {
		final List<Obs> formObs = new ArrayList<Obs>();

		// preparation: prepare form for data entry
		for (CachedConceptId concept : conceptsToStore) {
			// check if the observation already exists in the encounter (or obs group)
			Obs obs = Functions.observation(encounterOrObs, concept);
			final Concept obsConcept = conceptService.getConcept(concept.getConceptId());
			if (obs != null) {
				// prepare the text field to contain the numeric value
				final ConceptDatatype datatype = obsConcept.getDatatype();

				if (datatype.isNumeric()) {
					if (obs.getValueNumeric() != null) {
						// ensure the 'valueText' field matches the numeric value
						obs.setValueText(FMT.format(obs.getValueNumeric()));
					}
				} else if (datatype.isCoded()) {
					// no initialization necessary: the ConceptEditor can deal with the bean directly!
				} else if (datatype.isDate()) {
					if (obs.getValueDatetime() != null) {
						// ensure the 'valueText' field matches the datetime value
						obs.setValueText(Context.getDateFormat().format(obs.getValueDatetime()));
					}
				}
			} else {
				// create a new observation entry for this
				obs = PatientConsultEntryForm.newObs(obsConcept, form.getPatient());
			}

			// store the observation into the map
			form.getObservationMap().put(concept.getConceptId(), obs);
			formObs.add(obs);
		}

		// let the caller know what observations were put into the form
		return formObs;
	}

	/**
	 * Sets the date changed, changed by, and voided fields of the given object.
	 * 
	 * @param data
	 *            The openmrs data objecct
	 */
	protected void setUpdated(OpenmrsData data) {
		final Date now = new Date();

		// set modified time information
		data.setDateChanged(now);
		data.setChangedBy(Context.getAuthenticatedUser());
		data.setVoided(Boolean.FALSE);

		if (data instanceof Obs) {
			// NOTE: 'obs' values don't store the 'dateChanged' nor 'chanegdBy' attributes, so just update the 'created' and 'creator' fields
			data.setDateCreated(now);
			data.setCreator(Context.getAuthenticatedUser());
		}
	}

	/**
	 * Utility method that loops through the given cached concept IDs and searches for the corresponding observations:
	 * <ul>
	 * <li>If the obs doesn't have a coded value (i.e., is currently empty / unanswered), then an observation with the same concept is searched for on the
	 * entire person's record in case this question has been answered previously (perhaps in another module) and uses that as the 'default' answer.
	 * </ul>
	 */
	public static void fillInWithPreviousAnswers(Person person, Object encounterOrObs, CachedConceptId... codedConcepts) {
		for (CachedConceptId codedConcept : codedConcepts) {
			// load the current Obs
			final Obs currentObs = Functions.observation(encounterOrObs, codedConcept);
			if (currentObs != null && currentObs.getValueCoded() == null && currentObs.getValueNumeric() == null
					&& StringUtils.isEmpty(currentObs.getValueText()) && currentObs.getValueDatetime() == null) {
				// no current value for this concept observation: if there is a value on the person for the same concept, then use that as the default answer
				final List<Obs> previousAnswers = Functions.observations(person, codedConcept);
				Collections.reverse(previousAnswers);
				for (final Obs previousAnswer : previousAnswers) {
					// use the first non-null / non-empty previous answer (NOTE: There are still some cases where an observation will be saved even if the
					// answer was blank / empty).
					if (previousAnswer.getValueCoded() != null || previousAnswer.getValueNumeric() != null
							|| !StringUtils.isEmpty(previousAnswer.getValueText()) || previousAnswer.getValueDatetime() != null) {
						// use the previous answer's coded value as the default value for this observation
						currentObs.setValueCoded(previousAnswer.getValueCoded());
						currentObs.setValueNumeric(previousAnswer.getValueNumeric());
						currentObs.setValueText(previousAnswer.getValueText());
						currentObs.setValueDatetime(previousAnswer.getValueDatetime());

						// stop searching for more previous answers
						break;
					}
				}
			}
		}
	}

	/**
	 * Utility method that uses the previous value of the 'source' observation to fill in details of the 'target' observation.
	 * <ul>
	 * <li>If the obs doesn't have a coded value (i.e., is currently empty / unanswered), then an observation with the same concept is searched for on the
	 * entire person's record in case this question has been answered previously (perhaps in another module) and uses that as the 'default' answer.
	 * </ul>
	 */
	public static void fillInWithPreviousAnswerUsing(Person person, Object encounterOrObs, CachedConceptId target, CachedConceptId source) {
		// load the current Obs
		final Obs currentObs = Functions.observation(encounterOrObs, target);
		if (currentObs != null && currentObs.getValueCoded() == null && currentObs.getValueNumeric() == null && StringUtils.isEmpty(currentObs.getValueText())
				&& currentObs.getValueDatetime() == null) {
			// no current value for this concept observation: if there is a value on the person for the same concept, then use that as the default answer
			final Obs previousAnswer = Functions.observation(person, source);
			if (previousAnswer != null) {
				// use the previous answer's coded value as the default value for this observation
				currentObs.setValueCoded(previousAnswer.getValueCoded());
				currentObs.setValueNumeric(previousAnswer.getValueNumeric());
				currentObs.setValueText(previousAnswer.getValueText());
				currentObs.setValueDatetime(previousAnswer.getValueDatetime());
			}
		}
	}

	/**
	 * Sets observations with a null coded value to the false concept.
	 * 
	 * @param encounterOrObsOrPerson
	 * @param concepts
	 */
	public static void setNonTrueObsToFalse(Object encounterOrObsOrPerson, CachedConceptId... concepts) {
		final Concept trueConcept = Functions.trueConcept();
		final Concept falseConcept = Functions.falseConcept();
		for (CachedConceptId concept : concepts) {
			final Obs obs = Functions.observation(encounterOrObsOrPerson, concept);
			if (obs != null && !trueConcept.equals(obs.getValueCoded())) {
				obs.setValueCoded(falseConcept);
			}
		}
	}

	/**
	 * Prepare a new pre-populated Obs instance with the given concept.
	 * <p>
	 * NOTE: The person and obsDateTime attributes are left unpopulated -- it is the caller's responsibility to populate these as they are required (non-null)
	 * fields.
	 * 
	 * @param cachedConcept
	 *            The concept to set into the Obs instance
	 * @return A pre-populated Obs instance with the given concept
	 */
	public static Obs newObs(CachedConceptId cachedConcept, Patient patient) {
		return PatientConsultEntryForm.newObs(cachedConcept, patient);
	}

	/**
	 * This method will display the patient form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") T form) {
		final Patient patient = form.getPatient();
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");
		} else if (form.getPatientQueue() == null || form.getPatientQueue().getEncounter() == null) {
			// consult not started for this patient!
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.consult.not.started");
		}

		// store the current version of the version object at the time the form was loaded (for use in optimistic locking mechanism)
		form.setVersion(getCurrentVersion(getVersionObject(form)));

		// send the to the input page
		return getInputPath(request);
	}

	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@ModelAttribute("form") T form, //
			BindingResult errors) {

		final Patient patient = form.getPatient();
		if (patient == null || patient.getPersonName() == null) {
			// patient not found; treat this as an error (NOTE: If personName is null, then the patient has probably been voided)
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.not.found");
		} else if (form.getPatientQueue() == null || form.getPatientQueue().getEncounter() == null) {
			// consult not started for this patient!
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.Patient.consult.not.started");
		} else if (getCurrentVersion(getVersionObject(form)) != form.getVersion()) {
			// optimistic locking: version mismatch
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.error.data.concurrent.update");
		} else {
			// validate the submission
			final Encounter enc = form.getPatientQueue().getEncounter();

			final PatientConsultEntryFormValidator validator = new PatientConsultEntryFormValidator();
			validator.validate(form, errors);

			// reset the form's timestamp
			form.setTimestamp(null);
			try {
				// parse out the date / time
				final Date dateComponent = Context.getDateFormat().parse(form.getTimestampDate());
				final Calendar dateCal = Calendar.getInstance();
				dateCal.setTime(dateComponent);

				final Date timeComponent = TIME_FMT.parse(form.getTimestampTime());
				final Calendar timeCal = Calendar.getInstance();
				timeCal.setTime(timeComponent);

				final Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, dateCal.get(Calendar.YEAR));
				cal.set(Calendar.MONTH, dateCal.get(Calendar.MONTH));
				cal.set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH));

				cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
				cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
				cal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));

				// store the timestamp into the form
				form.setTimestamp(cal.getTime());
			} catch (Exception ex) {
				// indicate an error in the timestamp
				errors.rejectValue("timestampDate", "chits.Patient.consult.invalid.timestamp");
			}

			// post-process after initial validation; this method can provide additional validation
			postProcess(request, form, model, enc, errors);

			try {
				// track the Obs entities that we no longer need
				final List<Obs> toPurge = new ArrayList<Obs>();

				// The observations that will be saved into the encounter
				final List<Obs> toSave = new ArrayList<Obs>();

				// cleanup: remove Obs values that contain no data
				for (Integer conceptId : form.getObservationMap().keySet()) {
					// purge observations which have no data
					final Obs obs = form.getObservationMap().get(conceptId);
					if (false //
							|| (obs.getConcept().getDatatype().isNumeric() && StringUtils.isEmpty(obs.getValueText())) //
							|| (obs.getConcept().getDatatype().isCoded() && obs.getValueCoded() == null) //
							|| (obs.getConcept().getDatatype().isDate() && StringUtils.isEmpty(obs.getValueText()) //
							|| (StringUtils.isEmpty(obs.getValueText()) && obs.getValueCoded() == null && obs.getValueDatetime() == null
									&& obs.getValueCodedName() == null && obs.getValueDrug() == null && obs.getValueNumeric() == null && StringUtils
										.isEmpty(obs.getValueComplex()))) //
					) {
						// add obs to set of items to purge since it doesn't contain a value
						toPurge.add(obs);
					} else {
						if (obs.getObsId() != null) {
							// existing observations should have their 'last modified' timestamp updated
							setUpdated(obs);
						}

						// track which observations will be saved
						toSave.add(obs);

						// this observation will be saved into the encounter
						enc.addObs(obs);
					}
				}

				// call the optional interceptor before saving the observations; NOTE: If this method adds anything to the 'toSave' collection, it is
				// responsible for adding those Obs instances to the encounter or a group parent to the encounter to be cascade-saved.
				preProcessEncounterObservations(request, form, enc, toSave, toPurge);

				for (Obs obs : toSave) {
					// update observation to be saved timestamps with the form's timestamp
					obs.setObsDatetime(form.getTimestamp());
					obs.setPerson(form.getPatient());
				}

				// detach observations that are to be purged from the encounter and from parent groups (if any) to allow for these orphans to be deleted
				for (Obs obs : toPurge) {
					if (obs.getEncounter() != null) {
						// detach from encounter (if any)
						obs.getEncounter().removeObs(obs);
						obs.setEncounter(null);
					}

					// detach from parent group (if any)
					if (obs.getObsGroup() != null) {
						obs.getObsGroup().removeGroupMember(obs);
						obs.setObsGroup(null);
					}
				}

				// save the changes!
				if (!errors.hasErrors()) {
					// invoke optional interceptor
					beforeSave(request, form, enc, toSave, toPurge);

					// save the encounter
					enc.setDateChanged(new Date());
					enc.setChangedBy(Context.getAuthenticatedUser());
					encounterService.saveEncounter(enc);

					// purge the observations that we no longer need
					for (Obs obs : toPurge) {
						try {
							// now that the obs to purge are detached from the encounter, we can delete them permanently
							chitsService.purge(obs);
						} catch (APIAuthenticationException ex) {
							// propagate authorization errors
							throw ex;
						} catch (Exception ex) {
							log.warn("Error purging observation with id: " + obs.getId(), ex);
						}
					}

					if (httpSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR) == null) {
						// add a 'success' message if there were no binding errors and no other message has already been put in place
						httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.consult.submission.data.updated");
					}

					// redirect to reload all data properly
					final String reloadPath = getReloadPath(request, patient.getPatientId());
					if (reloadPath.startsWith("redirect:")) {
						// sending a redirect: add a no cache header to ensure proper reloading
						if (reloadPath.contains("?")) {
							// append a 'nocache' parameter to force browser to reload (IE seems to be caching results)
							return reloadPath + "&nocache=" + UUID.randomUUID().toString();
						} else {
							// append a 'nocache' parameter to force browser to reload (IE seems to be caching results)
							return reloadPath + "?nocache=" + UUID.randomUUID().toString();
						}
					} else {
						// redirecting to a view, do not append any parameters
						return reloadPath;
					}
				} else {
					// submission error!
					if (httpSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR) == null) {
						// no error-specific message added, so use a general error message
						httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.consult.submission.data.errors");
					}
				}
			} catch (APIAuthenticationException ex) {
				// authorization error: Just use the exception message since this is what core modules do
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, ex.getMessage());
			} catch (Exception ex) {
				log.warn("Error processing request", ex);

				// general unknown error
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.error.saving");
			}
		}

		// send back the input page
		return getInputPath(request);
	}

	/**
	 * Returns the {@link Auditable} instance to use as a reference to obtain the 'version' value form the 'dateChanged' attribute.
	 * <p>
	 * Subclasses that only modify a small set of data in the encounter should override this to return a more relevant object to use as the version object (for
	 * example, for the UpdateVisitNotesController, the {@link Obs} representing the Notes being edited should be used as the version object).
	 * 
	 * @return
	 */
	protected Auditable getVersionObject(T form) {
		// by default, use the encounter instance
		return form.getEncounter();
	}

	/**
	 * Returns the current value of the version for the given {@link Auditable} instance.
	 * 
	 * @param versionObject
	 *            The {@link Auditable} instantce to extract the version of.
	 * @return The version of the version object.
	 */
	protected long getCurrentVersion(Auditable versionObject) {
		if (versionObject instanceof Obs) {
			final Obs versionObs = (Obs) versionObject;

			if (versionObs.getObsId() == null) {
				// new observation instances don't have version info since they are to be created
				return 0;
			} else {
				// 'obs' instances don't persist the 'dateChanged' value, so use the 'dateCreated' instead (these are automatically updated by the
				// setUpdated(..) method); NOTE: We don't use the 'obsDateTime' value since that may be manually entered (hence, unchanged during updates)
				return versionObject.getDateCreated() != null ? versionObject.getDateCreated().getTime() : 0;
			}
		} else if (versionObject != null) {
			if (versionObject.getDateChanged() != null) {
				// use the 'date changed' value
				return versionObject.getDateChanged().getTime();
			} else if (versionObject.getDateCreated() != null) {
				// use the 'date created' value
				return versionObject.getDateCreated().getTime();
			} else {
				// no available version information
				return 0;
			}
		}

		// no available object, version is '0'
		return 0;
	}

	/**
	 * Gives subclasses a chance to process any additional request parameters.
	 * <p>
	 * This method can provide additional validation.
	 * 
	 * @param request
	 * @param errors
	 */
	protected void postProcess(HttpServletRequest request, T form, ModelMap map, Encounter enc, BindingResult errors) {
		// no operation by default
	}

	/**
	 * Optional implementation to pre-process the observations before processing the obsToSave and obsToPurge
	 * <p>
	 * NOTE: If this method adds anything to the 'toSave' collection, it is responsible for adding those Obs instances to the encounter or a group parent to the
	 * encounter to be cascade-saved.
	 * <p>
	 * NOTE: this method is called even if errors are present.
	 * 
	 * @param obsToSave
	 *            The Observations that will be saved into the encounter.
	 */
	protected void preProcessEncounterObservations(HttpServletRequest request, T form, Encounter enc, Collection<Obs> obsToSave, Collection<Obs> obsToPurge) {
		// default implementation does nothing
	}

	/**
	 * Optional method invoked immediately before the encounter is saved.
	 * <p>
	 * NOTE: this method is called only if no errors are present.
	 * 
	 * @param request
	 * @param form
	 * @param enc
	 * @param toSave
	 * @param toPurge
	 */
	protected void beforeSave(HttpServletRequest request, T form, Encounter enc, Collection<Obs> obsToSave, Collection<Obs> obsToPurge) {
		// default implementation does nothing
	}

	@Autowired
	public void setViewPatientConsultsController(ViewPatientConsultsController viewPatientConsultsController) {
		this.viewPatientConsultsController = viewPatientConsultsController;
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}

	@Autowired
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Autowired
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}
}
