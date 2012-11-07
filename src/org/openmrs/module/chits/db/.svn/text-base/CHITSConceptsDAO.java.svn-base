package org.openmrs.module.chits.db;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.chits.CHITSService;

/**
 * Database methods for the {@link CHITSService} relating to concepts.
 */
public interface CHITSConceptsDAO {
	/**
	 * Search matching symptom concepts
	 */
	public List<Concept> findICD10SymptomConcepts(String query, int maxResults) throws DAOException;

	/**
	 * Search matching diagnosis concepts
	 */
	public List<Concept> findICD10DiagnosisConcepts(String query, int maxResults) throws DAOException;

	/**
	 * Searches all ConceptName instances matching the given name, regardless of locale or concept name type.
	 * 
	 * @param name
	 *            The name of the concept
	 * @return All ConceptName instances matching the given name
	 */
	public List<ConceptName> findMatchingConceptNames(String name);

	/**
	 * Purges the given concept name and all related concept name tags and words.
	 */
	public void purgeConceptName(ConceptName conceptName);

	/**
	 * Searches a concept by its fully specified name
	 * 
	 * @param name
	 *            The fully specified name of the concept
	 * @return The concept with the given fully specified name
	 */
	public Concept findConceptByFullySpecifiedName(String name);
}
