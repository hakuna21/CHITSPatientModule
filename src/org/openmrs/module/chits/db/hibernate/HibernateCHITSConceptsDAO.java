package org.openmrs.module.chits.db.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.ConceptWord;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants.ICD10;
import org.openmrs.module.chits.db.CHITSConceptsDAO;

/**
 * Database methods for the {@link CHITSService} relating to concept records.
 */
public class HibernateCHITSConceptsDAO implements CHITSConceptsDAO {
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	public HibernateCHITSConceptsDAO() {
	}

	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Search concept answers for matching query.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Concept> findICD10SymptomConcepts(String query, int maxResults) throws DAOException {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptSource.class, "source");
		criteria.add(Expression.eq("source.name", ICD10.CONCEPT_SOURCE_NAME));
		final ConceptSource icd10ConceptSource = (ConceptSource) criteria.uniqueResult();

		final List<Concept> results = new ArrayList<Concept>();
		if (icd10ConceptSource != null) {
			final List<ConceptMap> cms = sessionFactory.getCurrentSession().createQuery(//
					"SELECT cm " //
							+ " FROM ConceptMap cm " //
							+ " JOIN cm.concept.names cn " //
							+ "WHERE cm.source = :conceptSource " //
							+ "  AND cn.name LIKE :query " //
							+ "  AND cm.sourceCode LIKE 'R%' ") //
					.setParameter("conceptSource", icd10ConceptSource) //
					.setParameter("query", "%" + query + "%") //
					.setMaxResults(maxResults) //
					.list();

			for (ConceptMap cm : cms) {
				results.add(cm.getConcept());
			}
		}

		return results;

	}

	/**
	 * Search concept set members for matching query.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Concept> findICD10DiagnosisConcepts(String query, int maxResults) throws DAOException {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptSource.class, "source");
		criteria.add(Expression.eq("source.name", ICD10.CONCEPT_SOURCE_NAME));
		final ConceptSource icd10ConceptSource = (ConceptSource) criteria.uniqueResult();

		final List<Concept> results = new ArrayList<Concept>();
		if (icd10ConceptSource != null) {
			final List<ConceptMap> cms = sessionFactory.getCurrentSession().createQuery(//
					"SELECT cm " //
							+ " FROM ConceptMap cm " //
							+ " JOIN cm.concept.names cn " //
							+ "WHERE cm.source = :conceptSource " //
							+ "  AND cn.name LIKE :query " //
							+ "  AND cm.sourceCode NOT LIKE 'R%' ") //
					.setParameter("conceptSource", icd10ConceptSource) //
					.setParameter("query", "%" + query + "%") //
					.setMaxResults(maxResults) //
					.list();

			for (ConceptMap cm : cms) {
				results.add(cm.getConcept());
			}
		}

		return results;
	}

	/**
	 * Finds all concept names matching the given name.
	 * 
	 * @param name
	 *            The name to search for
	 * @return All ConceptName records matching the given name.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ConceptName> findMatchingConceptNames(String name) {
		return (List<ConceptName>) sessionFactory.getCurrentSession().createQuery(//
				"SELECT cn " //
						+ " FROM ConceptName cn " //
						+ "WHERE cn.name = :name").setParameter("name", name) //
				.list();
	}

	/**
	 * Purges the given concept name and all related concept name tags and words.
	 */
	@Override
	public void purgeConceptName(ConceptName conceptName) {
		final Session session = sessionFactory.getCurrentSession();

		// void the concept name in case deletion doesn't work for any reason
		conceptName.setVoided(Boolean.TRUE);
		conceptName.setVoidedBy(Context.getAuthenticatedUser());
		session.save(conceptName);

		// delete all associated words
		@SuppressWarnings("unchecked")
		final List<ConceptWord> words = session.createCriteria(ConceptWord.class).add(Expression.eq("conceptName", conceptName)).list();
		for (ConceptWord word : words) {
			session.delete(word);
		}

		// flush the session to ensure all the words are deleted
		session.flush();

		// detach from owner and save to cascade-delete the concept name
		final Concept owner = conceptName.getConcept();
		owner.removeName(conceptName);
		conceptName.setConcept(null);
		session.save(owner);

		// make sure the name has been deleted!
		session.delete(conceptName);
	}

	public Concept findConceptByFullySpecifiedName(String name) {
		final ConceptName cn = (ConceptName) sessionFactory.getCurrentSession().createQuery("" //
				+ "SELECT cn " //
				+ "  FROM ConceptName cn " //
				+ " WHERE cn.name = :name " //
				+ "   AND cn.conceptNameType = :type ") //
				.setParameter("name", name) //
				.setParameter("type", ConceptNameType.FULLY_SPECIFIED) //
				.setMaxResults(1) //
				.uniqueResult();

		// return the concept owning the fully specified name
		return cn != null ? cn.getConcept() : null;
	}
}