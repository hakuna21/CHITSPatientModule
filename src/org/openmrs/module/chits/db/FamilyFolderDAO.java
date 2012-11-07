package org.openmrs.module.chits.db;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.CachedProgramConceptId;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.UserBarangay;

/**
 * Database methods for the {@link CHITSService}.
 */
public interface FamilyFolderDAO {
	public FamilyFolder saveFamilyFolder(FamilyFolder familyFolder) throws DAOException;

	public FamilyFolder getFamilyFolder(Integer familyFolderId) throws DAOException;

	public FamilyFolder getFamilyFolderByUuid(String uuid) throws APIException;

	public FamilyFolder getFamilyFolderByCode(String code) throws APIException;

	public void deleteFamilyFolder(FamilyFolder familyFolder) throws DAOException;

	public List<FamilyFolder> getAllFamilyFolders() throws DAOException;

	public List<FamilyFolder> getAllFamilyFoldersByBarangay(String barangayCode, Long modifiedSince, Long modifiedUpto);

	public List<Patient> getAllPatientsByBarangay(String barangayCode, Long modifiedSince, Long modifiedUpto);

	public List<Patient> getAllPatientVisitsByBarangay(String barangayCode, Long visitedFrom, Long visitedTo, CachedProgramConceptId program);

	public List<FamilyFolder> getFamilyFoldersLike(String barangayCode, String like, Integer start, Integer length) throws DAOException;

	public List<FamilyFolder> getAllFamilyFoldersLike(String like) throws DAOException;

	public List<FamilyFolder> getFamilyFoldersOf(Integer patientId) throws DAOException;

	public Integer getFamilyFoldersCountLike(String barangayCode, String like) throws DAOException;

	public List<UserBarangay> getUserBarangays(User user) throws DAOException;

	public void saveUserBarangay(UserBarangay userBarangay) throws DAOException;

	public void deleteUserBarangay(UserBarangay userBarangay) throws DAOException;

	public void mergePatients(Patient preferred, Patient notPreferred);
}
