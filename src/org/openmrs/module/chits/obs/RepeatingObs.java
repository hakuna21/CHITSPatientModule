package org.openmrs.module.chits.obs;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * A group observation encapsulating a homogenous collection of repeating observation members.
 * 
 * @author Bren
 */
public abstract class RepeatingObs<T extends GroupObs> extends GroupObs {
	/** The homogenous members of the parent observation of the cached concept type */
	@SuppressWarnings("serial")
	private final List<T> children = new ArrayList<T>() {
		/**
		 * Overridden to add the element as a group member of the inner observation.
		 */
		public void add(int index, T element) {
			// link as group member to the internal observation
			getObs().addGroupMember(element.getObs());

			// dispatch to superclass
			super.add(index, element);
		};

		/**
		 * Overridden to add the element as a group member of the inner observation.
		 */
		public boolean add(T e) {
			// link as group member to the internal observation
			getObs().addGroupMember(e.getObs());

			// dispatch to superclass
			return super.add(e);
		};
	};

	/**
	 * Constructs a parent containing the homogenous children.
	 * 
	 * @param obs
	 *            The observation represented by the parent
	 * @param concept
	 *            The concept
	 */
	public RepeatingObs(Obs obs) {
		// store the internal observation
		super(obs);

		// get generified type for initializing the children
		@SuppressWarnings("unchecked")
		final Class<T> type = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

		// add all homogenous children using the generified type
		for (Obs member : Functions.observations(obs, getChildrenConcept())) {
			try {
				// add a new encapsulation of the child
				final T child = type.getConstructor(Obs.class).newInstance(member);
				children.add(child);
			} catch (Exception e) {
				// constructor not found
				throw new IllegalArgumentException("Generic declaration type (" //
						+ type + ") does not have constructor accepting an Observation for: " //
						+ this.getClass());
			}
		}
	}

	/** Subclasses must return the concept type of the homogenous children member observations */
	public abstract CachedConceptId getChildrenConcept();

	/**
	 * Sets the given person into all the obs in this bean.
	 * 
	 * @param person
	 *            The person to store into the observations.
	 */
	@Override
	public void storePersonAndAudit(Person person) {
		// store in internal obs
		super.storePersonAndAudit(person);

		// store in all children
		for (T member : getChildren()) {
			member.storePersonAndAudit(person);
		}
	}

	public List<T> getChildren() {
		return children;
	}

	public void addChild(T child) {
		this.children.add(child);
	}
}
