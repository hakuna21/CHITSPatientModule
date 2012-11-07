package org.openmrs.module.chits;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants.VisitConcepts;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.web.taglib.Functions;

public class ObsUtil {
	/** The patient context */
	public static final ThreadLocal<Patient> PATIENT_CONTEXT = new ThreadLocal<Patient>();

	/**
	 * Prepares an observation for update. If the observation already exists at the top level of the encounter's observations, then it is returned with the
	 * 'dateChanged' and 'changedBy' fields updated, otherwise a new {@link Obs} instance will be initialized, attached to the encounter, and returned
	 * 
	 * @param enc
	 *            The encounter to search the observation from
	 * @param cachedConcept
	 *            The concept of the observation
	 * @param userService
	 *            The {@link UserService} used if a daemon user needs to be looked up in case there is currently no authenticated user
	 * @return A prepped Obs instance.
	 */
	public static Obs observationForUpdate(Encounter enc, CachedConceptId cachedConcept, UserService userService) {
		Obs obs = Functions.observation(enc, VisitConcepts.CONSULT_END);
		if (obs == null) {
			// create a new observation entry for this
			obs = new Obs();
			obs.setConcept(Functions.concept(cachedConcept));
			obs.setUuid(UUID.randomUUID().toString());
			obs.setValueText(null);
			obs.setValueNumeric(null);
			obs.setValueCoded(null);
			obs.setDateCreated(new Date());
			obs.setCreator(getUserForAudit(userService));
			obs.setVoided(Boolean.FALSE);
			obs.setPerson(enc.getPatient());

			// add to this encounter
			enc.addObs(obs);
		} else {
			// notes obs already exists, just update the 'changed' flags
			obs.setDateChanged(new Date());
			obs.setChangedBy(getUserForAudit(userService));
		}

		// return the observation that is ready for update
		return obs;
	}

	/**
	 * Gets the currently authenticated user or the daemon user if invoked during the startup process.
	 * 
	 * @param userService
	 *            Used for retrieving the daemon user if no currently authenticatd user.
	 * @return The currently authenticated user (or the daemon user if called during startup process)
	 */
	public static User getUserForAudit(UserService userService) {
		User user = Context.getAuthenticatedUser();
		if (user == null) {
			user = userService.getUserByUuid("A4F30A1B-5EB9-11DF-A648-37A07F9C90FB");
		}

		return user;
	}

	/**
	 * Returns true if the obs has a null or 0 id indicating that it is a new record not yet saved to the DB.
	 * 
	 * @param obs
	 *            the obs to check
	 * @return true if the obs has a null or 0 id
	 */
	public static boolean isNewObs(Obs obs) {
		return obs.getObsId() == null || obs.getObsId() == 0;
	}

	/**
	 * Returns true if the record has a null or 0 id indicating that it is a new record not yet saved to the DB.
	 * 
	 * @param entity
	 *            the entity to check
	 * @return true if the entity has a null or 0 id
	 */
	public static boolean isNewEntity(OpenmrsObject entity) {
		return entity.getId() == null || entity.getId() == 0;
	}

	/**
	 * Performs a shallow copy of the data in 'source' to 'target' (i.e., only the GroupObs's obs itself and immediate members)
	 * 
	 * @param source
	 *            Values to copy from
	 * @param target
	 *            Values to copy to
	 */
	public static void shallowCopy(GroupObs source, GroupObs target) {
		// copy the obs itself
		copyValues(source.getObs(), target.getObs());

		// copy values from the source group obs to the target
		for (Map.Entry<Integer, Obs> obsEntry : source.getObservationMap().entrySet()) {
			final Obs targetObs = target.getObservationMap().get(obsEntry.getKey());
			if (targetObs != null) {
				copyValues(obsEntry.getValue(), targetObs);
			}
		}
	}

	/**
	 * Copies values (text, numeric, date, and coded) from 'source' to 'target'
	 * 
	 * @param source
	 *            The source observation to copy from
	 * @param target
	 *            The source observation to copy to
	 */
	public static void copyValues(Obs source, Obs target) {
		target.setValueNumeric(source.getValueNumeric());
		target.setValueCoded(source.getValueCoded());
		target.setValueDatetime(source.getValueDatetime());
		target.setValueText(source.getValueText());
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
	public static Obs newObs(Concept concept, Person person) {
		final Date now = new Date();
		final Obs obs = new Obs();
		obs.setConcept(concept);
		obs.setUuid(UUID.randomUUID().toString());
		obs.setValueText(null);
		obs.setValueNumeric(null);
		obs.setValueCoded(null);
		obs.setDateCreated(now);
		obs.setObsDatetime(now);
		obs.setPerson(person);
		obs.setCreator(ObsUtil.getUserForAudit(Context.getUserService()));
		obs.setVoided(Boolean.FALSE);
		return obs;
	}
}
