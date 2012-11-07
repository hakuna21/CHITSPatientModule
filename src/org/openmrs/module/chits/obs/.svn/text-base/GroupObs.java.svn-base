package org.openmrs.module.chits.obs;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.Constants.AuditConcepts;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * A group observation encapsulating an observation map and has no homogenous children.
 * 
 * @author Bren
 */
public abstract class GroupObs {
	/** The main observation */
	private final Obs obs;

	/** Observation mapping by concept id containing the heterogenous observation members */
	private Map<Integer, Obs> observationMap = new LinkedHashMap<Integer, Obs>();

	public GroupObs(Obs obs) {
		// store reference to own observation
		this.obs = obs;

		// setup default value coded value to this (i.e., the parent) observation's concept
		// NOTE: Some observation groups double as an observation specifying a type (e.g., MCServiceRecordConcepts.SERVICE_TYPE); so if the coded value is
		// non-null, it should NOT be overridden!
		if (obs.getValueCoded() == null) {
			// observations groups should always have the coded value stored into the valueText attribute
			this.obs.setValueCoded(obs.getConcept());
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(obs);
		}

		// store obs into own observation map for referencing (also, putting the internal obs
		// instance into the map allows processing of it together with the member observations
		// by iterating over the observation map, for example when setting the encounter and
		// person of the obs)
		observationMap.put(obs.getConcept().getConceptId(), obs);
	}

	/**
	 * Sets the concepts that corresopnd to the observation members this group observation contains.
	 * <p>
	 * This is an optional method since not all group observations have heterogenous observation members.
	 * 
	 * @param concepts
	 *            The heterogenous concepts that this obersvation contains
	 */
	protected void setConcepts(CachedConceptId... concepts) {
		for (CachedConceptId concept : concepts) {
			// load current observation
			Obs memberObs = Functions.observation(this.obs, concept);
			if (memberObs == null) {
				// prepare a new blank observation
				memberObs = PatientConsultEntryForm.newObs(concept, ObsUtil.PATIENT_CONTEXT.get());

				// add the new observation as a group member
				this.obs.addGroupMember(memberObs);
			}

			// add to the observation map
			this.observationMap.put(concept.getConceptId(), memberObs);
		}
	}

	/**
	 * Convenience method for passing all values of an enum class implementing the {@link CachedConceptId} interface including the parent concept entry (which
	 * must be the first). To prevent adding the parent concept as a member (thus, creating a cyclic hierarchy), the first item is skipped.
	 * 
	 * @param concepts
	 *            The concepts to set (the first item will be skipped since it is assumed to be the concept for the parent observation)
	 */
	protected void setConceptsExceptFirst(CachedConceptId... concepts) {
		// copy all concepts except the first one to a new array
		final CachedConceptId[] excludingFirst = new CachedConceptId[concepts.length - 1];
		System.arraycopy(concepts, 1, excludingFirst, 0, excludingFirst.length);

		// dispatch to the setConcepts method
		setConcepts(excludingFirst);
	}

	/**
	 * Sets the given person into all the obs in this bean and marks audit information (creator and valueDatetime of the CREATED_BY and UPDATED_BY observations
	 * on this obs group).
	 * <p>
	 * Additionally, this method removes any new 'leaf' (i.e., with no members) obs values that have no values in them.
	 * 
	 * @param person
	 *            The person to store into the observations.
	 */
	public void storePersonAndAudit(Person person) {
		if (!person.equals(this.obs.getPerson())) {
			this.obs.setPerson(person);
		}

		for (Obs obs : observationMap.values()) {
			// make sure obs are attached to a person
			if (obs.getPerson() == null) {
				// NOTE: in some rare cases, an observation can belong to a different person
				// (such as the 'birth weight' observation in a pregnancy outcome belonging
				// to the baby and not the patient)
				obs.setPerson(person);
			}

			// remove unused new leaf nodes
			removeNewLeafIfUnused(obs);
		}

		// set the audit concepts into this observation
		final Date now = new Date();
		Obs createdBy = Functions.observation(this.obs, AuditConcepts.CREATED_BY);
		if (createdBy == null) {
			// create the audit record only if it doesn't already exist
			createdBy = PatientConsultEntryForm.newObs(AuditConcepts.CREATED_BY, person);
			createdBy.setValueDatetime(now);
			createdBy.setValueCoded(Functions.concept(AuditConcepts.CREATED_BY));
			createdBy.setCreator(Context.getAuthenticatedUser());
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(createdBy);
			this.obs.addGroupMember(createdBy);
		}

		// only create a 'modified by' audit record if the observation is not new
		if (this.obs.getObsId() != null && this.obs.getObsId() > 0) {
			Obs modifiedBy = Functions.observation(this.obs, AuditConcepts.MODIFIED_BY);
			if (modifiedBy == null) {
				// create the audit record to track the 'modified by' audit information
				modifiedBy = PatientConsultEntryForm.newObs(AuditConcepts.MODIFIED_BY, person);
				modifiedBy.setValueDatetime(now);
				modifiedBy.setValueCoded(Functions.concept(AuditConcepts.MODIFIED_BY));
				PatientConsultEntryFormValidator.setValueCodedIntoValueText(modifiedBy);
				this.obs.addGroupMember(modifiedBy);
			}

			// update timestamp and updator audit information
			modifiedBy.setPerson(person);
			modifiedBy.setValueDatetime(now);
			modifiedBy.setCreator(Context.getAuthenticatedUser());
		}
	}

	/**
	 * Detaches the obs if it has no members and no values.
	 * 
	 * @param obs
	 */
	private boolean removeNewLeafIfUnused(Obs obs) {
		if (obs.getId() != null || obs.hasGroupMembers()) {
			// existing or non-leaf obs should not be removed
			return false;
		}

		if (!StringUtils.isEmpty(obs.getValueText()) //
				|| obs.getValueCoded() != null //
				|| obs.getValueDatetime() != null //
				|| obs.getValueNumeric() != null) {
			// has a non-empty value, this obs should not be removed
			return false;
		}

		// detach this unused new non-leaf obs and remove it so that it doesn't get saved
		if (obs.getEncounter() != null) {
			// detach from encounter
			obs.getEncounter().removeObs(obs);
			obs.setEncounter(null);
		}

		// detach from the parent obs group
		if (obs.getObsGroup() != null) {
			// detach from parent observation
			obs.getObsGroup().removeGroupMember(obs);
			obs.setObsGroup(null);
		}

		// obs has been removed
		return true;
	}

	/**
	 * Convenience method to extract the {@link Obs} instance corresponding to the given {@link CachedConceptId}.
	 * 
	 * @param concept
	 *            The concept to lookup the observation by
	 * @return The matching observation.
	 */
	public Obs getMember(CachedConceptId concept) {
		// lookup in the internal observation map by the concept's ID
		return getObservationMap().get(concept.getConceptId());
	}

	public Obs getObs() {
		return obs;
	}

	public Map<Integer, Obs> getObservationMap() {
		return observationMap;
	}

	public void setObservationMap(Map<Integer, Obs> observationMap) {
		this.observationMap = observationMap;
	}

	public FieldPath path() {
		return new FieldPath();
	}

	public FieldPath path(String prefix) {
		return new FieldPath(prefix);
	}

	/**
	 * A fluent expression utility class that enables construction of field paths to the observation map.
	 * <p>
	 * Sample usage: <code>
	 * 		GroupObs grpObs = new PrenatalVisitRecord();
	 * 		String fieldExpr = grpObs.path("prenatalVisitRecord").to(MCPrenatalVisitRecordConcepts.VISIT_DATE).valueText();
	 * 		System.out.println(fieldExpr);
	 * 
	 * 		// would print out:
	 * 		// 		"prenatalVisitRecord.observationMap[xxx].valueText"
	 * 		// where 'xxx' is the concept ID of MCPrenatalVisitRecordConcepts.VISIT_DATE
	 * 
	 * 		GroupObs grpObs = new VitaminAServiceRecord();
	 * 		String fieldExpr = grpObs.path("vitaminAServiceRecord").valueCoded();
	 * 		System.out.println(fieldExpr);
	 * 
	 * 		// would print out:
	 * 		// 		"prenatalVisitRecord.obs.valueCoded"
	 * </code>
	 * 
	 * @author Bren
	 */
	public class FieldPath {
		private final String[] fieldPaths;
		private CachedConceptId memberType;

		FieldPath(String... fieldPaths) {
			this.fieldPaths = fieldPaths;
		}

		public FieldPath to(CachedConceptId memberType) {
			this.memberType = memberType;
			return this;
		}

		/**
		 * Returns the path to the observation map in the form "[prefix].observationMap"
		 * <p>
		 * e.g.:
		 * 
		 * <pre>
		 * prenatalVisitRecord.observationMap
		 * </pre>
		 * 
		 * @return The path to the observation map
		 */
		public String toObsMap() {
			final StringBuilder path = new StringBuilder();
			if (fieldPaths != null && fieldPaths.length > 0) {
				for (int i = 0; i < fieldPaths.length; i++) {
					path.append(fieldPaths[i]);
					path.append(".");
				}
			}

			// returnp ath to the observation map
			path.append("observationMap");

			// reset member type path for re-use
			this.memberType = null;

			return path.toString();
		}

		/**
		 * Returns the path to the coded value in one of either two forms:
		 * <ul>
		 * <li>"[prefix].observationMap[" + concept ID of concept+ "].valueCoded"
		 * <li>"[prefix].obs.valueCoded"
		 * </ul>
		 * e.g.:
		 * 
		 * <pre>
		 * prenatalVisitRecord.observationMap[1234].valueCoded
		 * </pre>
		 * 
		 * @return The path to the coded value of the observation using an optional member type
		 */
		public String valueCoded() {
			final String path = pathTo("valueCoded");

			// reset member type path for re-use
			this.memberType = null;

			return path;
		}

		/**
		 * Returns the path to the coded value in one of either two forms:
		 * <ul>
		 * <li>"[prefix].observationMap[" + concept ID of concept+ "].valueText"
		 * <li>"[prefix].obs.valueText"
		 * </ul>
		 * 
		 * e.g.:
		 * 
		 * <pre>
		 * prenatalVisitRecord.observationMap[1234].valueText
		 * </pre>
		 * 
		 * @return The path to the coded value of the observation using an optional member type
		 */
		public String valueText() {
			final String path = pathTo("valueText");

			// reset member type path for re-use
			this.memberType = null;

			return path;
		}

		/**
		 * Implementation returns the path to one of the observations in this object.
		 * 
		 * @param suffix
		 *            The last part of the expression
		 * @return A spring path constructed using the parameters.
		 */
		private String pathTo(String suffix) {
			final StringBuilder path = new StringBuilder();
			if (fieldPaths != null && fieldPaths.length > 0) {
				for (int i = 0; i < fieldPaths.length; i++) {
					path.append(fieldPaths[i]);
					path.append(".");
				}
			}

			if (memberType != null) {
				// path is to one of the concepts in the observation map
				path.append("observationMap[");
				path.append(memberType.getConceptId());
				path.append("].");
			} else {
				// path is to the group's observation itself
				path.append("obs.");
			}

			if (suffix != null) {
				// append the last part of the expression
				path.append(suffix);
			}

			return path.toString();
		}
	}
}
