package org.openmrs.module.chits.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.Util;

/**
 * DWR service for searching family folders.
 */
public class DWRFamilyFolderService {
	/** Logger instance */
	private final Log log = LogFactory.getLog(getClass());

	/** Maximum results */
	private Integer maximumResults;

	/**
	 * Search on the <code>searchValue</code>.
	 * 
	 * @param searchValue
	 *            string to be looked for
	 * @param start
	 *            The starting index for the results to return
	 * @param length
	 *            The number of results of return
	 * @return Collection<Object> of {@link FamilyFolder} matching results or a String error message
	 */
	private Collection<Object> findBatchOfFolders(String barangayCode, String searchValue, Integer start, Integer length) {
		if (maximumResults == null) {
			maximumResults = Util.getMaximumSearchResults();
		}

		if (length != null && length > maximumResults) {
			length = maximumResults;
		}

		// the list to return
		List<Object> folderList = new ArrayList<Object>();
		try {
			final CHITSService chitsService = Context.getService(CHITSService.class);
			for (FamilyFolder folder : chitsService.getFamilyFoldersLike(barangayCode, searchValue, start, length)) {
				folderList.add(new FamilyFolderListItem(folder));
			}
		} catch (APIAuthenticationException e) {
			log.error("Error while searching for folders", e);
			folderList.add(Context.getMessageSourceService().getMessage("chits.FamilyFolder.search.error") + " - " + e.getMessage());
			return folderList;
		}

		return folderList;
	}

	/**
	 * Returns a map of results with the values as count of matches and a partial list of the matching family folders (depending on values of start and length
	 * parameters) while the keys are are 'count' and 'objectList' respectively, if the length parameter is not specified, then all matches will be returned
	 * from the start index if specified.
	 * <p>
	 * NOTE: This implementation was patterned after DWRPatientService#findCountAndPatients(String, Integer, Integer, boolean)
	 * 
	 * @param searchValue
	 *            family folder code or name
	 * @param start
	 *            the beginning index
	 * @param length
	 *            the number of matching folders to return
	 * @param getMatchCount
	 *            Specifies if the count of matches should be included in the returned map
	 * @return a map of results
	 * @throws APIException
	 * @since 1.8
	 */
	public Map<String, Object> findCountAndFamilyFolders(String barangayCode, String searchValue, Integer start, Integer length, boolean getMatchCount)
			throws APIException {
		final CHITSService chitsService = Context.getService(CHITSService.class);

		// Map to return
		Map<String, Object> resultsMap = new HashMap<String, Object>();
		Collection<Object> objectList = new ArrayList<Object>();
		try {
			int foldersCount = 0;

			// if this is the first call
			if (getMatchCount) {
				foldersCount += chitsService.getFamilyFoldersCountLike(barangayCode, searchValue);

				// ensure that count never exceeds this value because the API's service layer would never
				// return more than it since it is limited in the DAO layer
				if (maximumResults == null) {
					maximumResults = Util.getMaximumSearchResults();
				}

				if (length != null && length > maximumResults) {
					length = maximumResults;
				}

				if (foldersCount > maximumResults) {
					foldersCount = maximumResults;
					if (log.isDebugEnabled()) {
						log.debug("Limitng the size of matching folders to " + maximumResults);
					}

				}
			}

			// if we have any matches or this isn't the first ajax call when the caller
			// requests for the count
			if (foldersCount > 0 || !getMatchCount) {
				objectList = findBatchOfFolders(barangayCode, searchValue, start, length);
			}

			resultsMap.put("count", foldersCount);
			resultsMap.put("objectList", objectList);
		} catch (Exception e) {
			log.error("Error while searching for folders", e);
			objectList.clear();
			objectList.add(Context.getMessageSourceService().getMessage("chits.FamilyFolder.search.error") + " - " + e.getMessage());
			resultsMap.put("count", 0);
			resultsMap.put("objectList", objectList);
		}

		return resultsMap;
	}
}
