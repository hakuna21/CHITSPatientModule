package org.openmrs.module.chits.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;

/**
 * Specialized DWR service for searching for lists of concepts including:
 * <ul>
 * <li>Concepts that are members of a convenience set
 * <li>Concepts that are answers to a concept question
 * </ul>
 */
public class DWRCHITSConceptService {
	private static final int MAX_RESULTS = 50;

	/** Logger instance */
	private final Log log = LogFactory.getLog(getClass());

	/**
	 * Results that will let the user know that there are no entries in the database regardless of search query.
	 */
	private final List<ICD10ConceptListItem> noEntries = Arrays.asList(new ICD10ConceptListItem[] { new ICD10ConceptListItem() {
		{
			setConceptId(0);
			setName("WARNING: There are no entries in the database");
		}
	} });

	/**
	 * Returns concept set members matching the search value string of the concept with the parentConcept name.
	 * 
	 * @param parentConceptId
	 *            The id of the parent concept set to search members of
	 * @param searchValue
	 *            concept name (may be partial)
	 * @return List of results
	 * @throws APIException
	 */
	public List<ICD10ConceptListItem> findICD10ConceptSetMembers(Integer parentConceptId, String searchValue) throws APIException {
		// results to return
		List<ICD10ConceptListItem> matches = null;
		try {
			ConceptService conceptService = Context.getConceptService();
			final Concept parent = conceptService.getConcept(parentConceptId);

			// filter out and sort matching concepts (NOTE: this ignores sort weight even if set)
			if (parent != null && (parent.isSet() == null || parent.isSet())) {
				if (parent.getSetMembers().isEmpty()) {
					matches = noEntries;
				} else {
					// concept found and is a set concept
					matches = filterAndSort(parent.getSetMembers(), searchValue);
				}
			}
		} catch (Exception e) {
			log.error("Error while searching for concepts", e);
		}

		// return the results
		return matches != null ? matches : new ArrayList<ICD10ConceptListItem>();
	}

	/**
	 * Returns concept answers matching the search value string of the concept with the parentConcept name.
	 * 
	 * @param parentConceptId
	 *            The id of the parent concept question to search answers to
	 * @param searchValue
	 *            concept name (may be partial)
	 * @return a map of results
	 * @throws APIException
	 */
	public List<ICD10ConceptListItem> findICD10Concepts(String type, String searchValue) throws APIException {
		// results to return
		List<ICD10ConceptListItem> matches = null;
		try {
			if (type == null) {
				matches = noEntries;
			} else {
				matches = new ArrayList<ICD10ConceptListItem>();

				// search only those matching records
				final List<Concept> matchingConcepts;
				if ("Symptoms".equalsIgnoreCase(type)) {
					matchingConcepts = Context.getService(CHITSService.class).findICD10SymptomConcept(searchValue, MAX_RESULTS);
				} else {
					matchingConcepts = Context.getService(CHITSService.class).findICD10DiagnosisConcept(searchValue, MAX_RESULTS);
				}

				for (Concept c : matchingConcepts) {
					matches.add(new ICD10ConceptListItem(c));
				}

				if (matches.size() == MAX_RESULTS) {
					// assume there were too many results...
					addTooManyRecordsWarning(matches);
				}
			}
		} catch (Exception e) {
			log.error("Error while searching for concepts", e);
		}

		// return the results
		return matches != null ? matches : new ArrayList<ICD10ConceptListItem>();
	}

	private void addTooManyRecordsWarning(Collection<ICD10ConceptListItem> collection) {
		final ICD10ConceptListItem tooManyResults = new ICD10ConceptListItem();
		tooManyResults.setConceptId(0);
		tooManyResults.setName("[Too many results, list is truncated...]");
		collection.add(tooManyResults);
	}

	/**
	 * Returns Drug records matching the search value string.
	 * 
	 * @param searchValue
	 *            concept name (may be partial)
	 * @return all matching drugs
	 * @throws APIException
	 */
	public List<DrugConceptListItem> findDrugs(String searchValue) throws APIException {
		// results to return
		List<DrugConceptListItem> matches = new ArrayList<DrugConceptListItem>();
		try {
			ConceptService conceptService = Context.getConceptService();
			for (Drug drug : conceptService.getDrugs(searchValue)) {
				matches.add(new DrugConceptListItem(drug));
			}
		} catch (Exception e) {
			log.error("Error while searching for drugs", e);
		}

		// return the results
		return matches;
	}

	/**
	 * Returns a filtered list of concepts from the full list of concepts given and a query string. The search is case insensitive.
	 * <p>
	 * The returned list of {@link ICD10ConceptListItem} is are AJAX friendly return values.
	 * 
	 * @param concepts
	 *            The complete collection of concepts to filter using the {@link java.lang.String#contains(CharSequence)} method to determine if the record
	 *            should match.
	 * @param query
	 *            The search filter. Partial matches will be included.
	 * @return the filtered and sorted set of concepts based on the query string.
	 */
	private List<ICD10ConceptListItem> filterAndSort(Collection<Concept> concepts, String query) {
		final String queryLowerCased = query.toLowerCase();
		final List<ICD10ConceptListItem> filtered = new ArrayList<ICD10ConceptListItem>();
		for (Concept c : concepts) {
			// partial match may be on name or on description
			boolean partialMatch = false;

			// don't include retired concepts!
			if (c.isRetired() == null || c.isRetired() == false) {
				// check all names (including synonyms) for a match
				for (ConceptName synonym : c.getNames()) {
					partialMatch = partialMatch || (synonym != null && synonym.getName() != null && synonym.getName().toLowerCase().contains(queryLowerCased));

					if (partialMatch) {
						// if matched, no need to check other names
						break;
					}
				}

				// try description (shortcut-operator skips comparison if 'partialMatch' is already true!
				partialMatch = partialMatch
						|| (c.getDescription() != null && c.getDescription().getDescription() != null && c.getDescription().getDescription().toLowerCase()
								.contains(queryLowerCased));

				if (partialMatch) {
					// a partial match; include in results
					filtered.add(new ICD10ConceptListItem(c));
				}
			}
		}

		// sort the results by name
		Collections.sort(filtered, new Comparator<ICD10ConceptListItem>() {
			@Override
			public int compare(ICD10ConceptListItem o1, ICD10ConceptListItem o2) {
				final String name1 = o1 != null ? o1.getName() : null;
				final String name2 = o2 != null ? o2.getName() : null;

				if (name1 != null && name2 != null) {
					return name1.compareToIgnoreCase(name2);
				} else if (name1 != null && name2 == null) {
					// non-null trumps null
					return +1;
				} else if (name1 == null && name2 != null) {
					// null loses to non-null
					return -1;
				} else {
					// null ties with null
					return 0;
				}
			}
		});

		// return the filtered and sorted list of concepts
		return filtered;
	}
}
