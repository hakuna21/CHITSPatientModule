package org.openmrs.module.chits.db.hibernate;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.CachedProgramConceptId;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.UserBarangay;
import org.openmrs.module.chits.Util;
import org.openmrs.module.chits.db.FamilyFolderDAO;

/**
 * Database implementation methods for the {@link CHITSService}.
 */
public class HibernateFamilyFolderDAO implements FamilyFolderDAO {
	/** Logger instance */
	private final Log log = LogFactory.getLog(getClass());

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	public HibernateFamilyFolderDAO() {
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
	 * @see org.openmrs.notification.db.FamilyFolderDAO#saveFamilyFolder(org.openmrs.notification.FamilyFolder)
	 */
	public FamilyFolder saveFamilyFolder(FamilyFolder familyFolder) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(familyFolder);
		return familyFolder;
	}

	/**
	 * @see org.openmrs.notification.db.FamilyFolderDAO#getFamilyFolder(java.lang.Integer)
	 */
	public FamilyFolder getFamilyFolder(Integer familyFolderId) throws DAOException {
		return (FamilyFolder) sessionFactory.getCurrentSession().get(FamilyFolder.class, familyFolderId);
	}

	/**
	 * Get {@link FamilyFolder} by UUID
	 * 
	 * @param uuid
	 *            Unique identifier
	 * @return FamilyFolder with given uuid
	 * @throws APIException
	 */
	public FamilyFolder getFamilyFolderByUuid(String uuid) throws APIException {
		return (FamilyFolder) sessionFactory.getCurrentSession().createCriteria(FamilyFolder.class) //
				.add(Restrictions.eq("uuid", uuid)).setMaxResults(1).uniqueResult();
	}

	/**
	 * Get {@link FamilyFolder} by code.
	 * 
	 * @param code
	 *            the family folder code
	 * @return FamilyFolder with given code
	 * @throws APIException
	 */
	public FamilyFolder getFamilyFolderByCode(String code) throws APIException {
		return (FamilyFolder) sessionFactory.getCurrentSession().createCriteria(FamilyFolder.class) //
				.add(Expression.eq("voided", Boolean.FALSE)).add(Restrictions.eq("code", code)) //
				.setMaxResults(1).uniqueResult();
	}

	/**
	 * @see org.openmrs.notification.db.FamilyFolderDAO#deleteFamilyFolder(org.openmrs.notification.FamilyFolder)
	 */
	public void deleteFamilyFolder(FamilyFolder familyFolder) throws DAOException {
		// load the family folder from the DB
		familyFolder = getFamilyFolder(familyFolder.getFamilyFolderId());
		if (familyFolder != null) {
			// be sure to detach all members
			familyFolder.getPatients().clear();

			// then we can delete the folder itself
			sessionFactory.getCurrentSession().delete(familyFolder);
		}
	}

	@SuppressWarnings("unchecked")
	public List<FamilyFolder> getAllFamilyFolders() throws DAOException {
		final Criteria crit = sessionFactory.getCurrentSession().createCriteria(FamilyFolder.class);
		crit.add(Expression.eq("voided", Boolean.FALSE));

		return crit.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FamilyFolder> getAllFamilyFoldersByBarangay(String barangayCode, Long modifiedSince, Long modifiedUpto) throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("SELECT f " //
				+ " FROM FamilyFolder f " //
				+ "WHERE f.barangayCode = :barangayCode " //
				+ "  AND f.voided = false " //
				+ "  AND (f.dateCreated BETWEEN :modifiedSince AND :modifiedUpto " //
				+ "       OR f.dateChanged BETWEEN :modifiedSince AND :modifiedUpto)") //
				.setParameter("barangayCode", barangayCode) //
				.setParameter("modifiedSince", modifiedSince != null ? new Date(modifiedSince) : new Date(0)) //
				.setParameter("modifiedUpto", modifiedUpto != null ? new Date(modifiedUpto) : new Date()) //
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Patient> getAllPatientsByBarangay(String barangayCode, Long modifiedSince, Long modifiedUpto) {
		return sessionFactory.getCurrentSession().createQuery("SELECT p " //
				+ " FROM FamilyFolder f " //
				+ " JOIN f.patients p " //
				+ "WHERE f.barangayCode = :barangayCode " //
				+ "  AND f.voided = false " //
				+ "  AND p.voided = false " //
				+ "  AND (p.dateCreated BETWEEN :modifiedSince AND :modifiedUpto " //
				+ "       OR p.dateChanged BETWEEN :modifiedSince AND :modifiedUpto)") //
				.setParameter("barangayCode", barangayCode) //
				.setParameter("modifiedSince", modifiedSince != null ? new Date(modifiedSince) : new Date(0)) //
				.setParameter("modifiedUpto", modifiedUpto != null ? new Date(modifiedUpto) : new Date()) //
				.list();
	}

	/**
	 * Get all patients belonging to the given barangay that had visits (encounters) between the specified dates.
	 * <p>
	 * Assumptions:
	 * <ul>
	 * <li>baranagyCode is required
	 * <li>visitedFrom must be less than visitedTo, both required</li>
	 * <li>The CachedProgramConceptId program parameter must have already been defined</li>
	 * </ul>
	 * 
	 * @param barangayCode
	 *            The barangay code to search patients
	 * @param visitedFrom
	 *            Include patients that had visits on or after this time
	 * @param visitedTo
	 *            Include patients that had visits on or up to this time
	 * @return All patients that had visits between the specified dates
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Patient> getAllPatientVisitsByBarangay(String barangayCode, Long visitedFrom, Long visitedTo, CachedProgramConceptId program) {
		final StringBuilder query = new StringBuilder();
		query.append("SELECT DISTINCT p " //
				+ "     FROM Encounter e " //
				+ "     JOIN e.patient p " //
				+ "    WHERE (e.dateChanged BETWEEN :visitedFrom AND :visitedTo) " //
				+ "      AND e.voided = false " //
				+ "      AND p.voided = false " //
				+ "      AND p IN (SELECT p FROM FamilyFolder f JOIN f.patients p WHERE f.barangayCode = :barangayCode AND f.voided = false) ");

		if (program != null) {
			// filter only patients enrolled in the specified program during that time frame
			query.append("" //
					+ "  AND p IN (SELECT pp.patient " //
					+ "              FROM PatientProgram pp " //
					+ "             WHERE pp.program = :program " //
					+ "               AND (pp.voided = false) " //
					+ "               AND (pp.dateEnrolled <= :visitedTo)" //
					+ "               AND (pp.dateCompleted IS NULL or pp.dateCompleted >= :visitedFrom)" //
					+ "           )");
		}

		// build up the query
		final Query q = sessionFactory.getCurrentSession().createQuery(query.toString()) //
				.setParameter("barangayCode", barangayCode) //
				.setParameter("visitedFrom", visitedFrom != null ? new Date(visitedFrom) : new Date(0)) //
				.setParameter("visitedTo", visitedTo != null ? new Date(visitedTo) : new Date());

		if (program != null) {
			// set program parameter
			final Program p = Context.getProgramWorkflowService().getProgram(program.getProgramId());
			q.setParameter("program", p);
		}

		// return matching results
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public List<FamilyFolder> getFamilyFoldersLike(String barangayCode, String like, Integer start, Integer length) throws DAOException {
		final Criteria crit = sessionFactory.getCurrentSession().createCriteria(FamilyFolder.class);
		crit.add(Expression.eq("voided", Boolean.FALSE));

		// include only the ones with the search text in the code or name
		log.debug("Criteria on 'code' and 'name' for: " + like);
		if (!StringUtils.isEmpty(barangayCode)) {
			crit.add(Expression.eq("barangayCode", barangayCode));
		}

		crit.add(Expression.or(Expression.like("code", like, MatchMode.ANYWHERE), Expression.like("name", like, MatchMode.ANYWHERE))) //
				.setFirstResult(start != null ? start : 0) //
				.addOrder(Order.asc("name")) //
				.addOrder(Order.asc("code")) //
				.setMaxResults(length != null ? length : Util.getMaximumSearchResults());

		// return the sublist
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<FamilyFolder> getAllFamilyFoldersLike(String like) throws DAOException {
		final Criteria crit = sessionFactory.getCurrentSession().createCriteria(FamilyFolder.class);
		crit.add(Expression.eq("voided", Boolean.FALSE));

		// include only the ones with the search text in the code or name
		log.debug("Criteria on 'code' and 'name' for: " + like);
		crit.add(Expression.or(Expression.like("code", like, MatchMode.ANYWHERE), Expression.like("name", like, MatchMode.ANYWHERE)));

		// return the sublist
		return crit.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FamilyFolder> getFamilyFoldersOf(Integer patientId) throws DAOException {
		return (List<FamilyFolder>) sessionFactory.getCurrentSession() //
				.createQuery("SELECT DISTINCT f " //
						+ "     FROM FamilyFolder f " //
						+ "     JOIN f.patients p " //
						+ "    WHERE p.patientId = :patientId" //
						+ "      AND f.voided = false" //
				).setParameter("patientId", patientId).list();
	}

	public Integer getFamilyFoldersCountLike(String barangayCode, String like) throws DAOException {
		final Criteria crit = sessionFactory.getCurrentSession().createCriteria(FamilyFolder.class);
		crit.add(Expression.eq("voided", Boolean.FALSE));

		// include only the ones with the search text in the code or name
		log.debug("Criteria count(*) on 'code' and 'name' for: " + like);
		if (!StringUtils.isEmpty(barangayCode)) {
			crit.add(Expression.eq("barangayCode", barangayCode));
		}

		crit.add(Expression.or(Expression.like("code", like, MatchMode.ANYWHERE), Expression.like("name", like, MatchMode.ANYWHERE))) //
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)//
				.setProjection(Projections.countDistinct("familyFolderId"));

		// return the sublist
		return (Integer) crit.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<UserBarangay> getUserBarangays(User user) throws DAOException {
		final Criteria crit = sessionFactory.getCurrentSession().createCriteria(UserBarangay.class);

		// include only the ones for the given user
		crit.add(Expression.eq("user", user));

		// return the results
		return crit.list();
	}

	/**
	 * Changes references to the notPreferred {@link Patient} to the preferred patient in the family folder table.
	 * 
	 * @param preferred
	 *            The patient to change the notPreferred patient references to
	 * @param notPreferred
	 *            The patient to change to the preferred patient reference
	 */
	@Override
	public void mergePatients(Patient preferred, Patient notPreferred) {
		// prepare 'changedBy' and 'dateChanged' parameters
		final User user = Context.getAuthenticatedUser();
		final Date now = new Date();
		final Session session = sessionFactory.getCurrentSession();

		// change references to the 'notPreferred' patient head of the family to the 'preferred' patient
		session.createQuery("" //
				+ "UPDATE FamilyFolder " //
				+ "   SET headOfTheFamily = :preferred, " //
				+ "       changedBy = :user, " //
				+ "       dateChanged = :now " //
				+ " WHERE headOfTheFamily = :notPreferred") //
				.setParameter("preferred", preferred) //
				.setParameter("user", user) //
				.setParameter("now", now) //
				.setParameter("notPreferred", notPreferred)//
				.executeUpdate();

		@SuppressWarnings("unchecked")
		final List<FamilyFolder> foldersWithNotPreferredMembers = session.createQuery("" //
				+ "SELECT f " //
				+ "  FROM FamilyFolder f " //
				+ "  JOIN f.patients p " //
				+ " WHERE p = :notPreferred") //
				.setParameter("notPreferred", notPreferred) //
				.list();

		// change references to patients in each family folder containing the 'notPreferred' patient member
		for (FamilyFolder ff : foldersWithNotPreferredMembers) {
			if (ff.getPatients().contains(notPreferred)) {
				// remove the notPreferred patient
				ff.getPatients().remove(notPreferred);

				// add the preferred patient: since this is a set duplicates will be ignored
				ff.getPatients().add(preferred);

				// mark the changed by and date attributes
				ff.setChangedBy(user);
				ff.setDateChanged(now);
			}

			// save the family folder
			session.saveOrUpdate(ff);
		}
	}

	public void saveUserBarangay(UserBarangay userBarangay) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(userBarangay);
	}

	public void deleteUserBarangay(UserBarangay userBarangay) throws DAOException {
		sessionFactory.getCurrentSession().delete(userBarangay);
	}
}
