package org.openmrs.module.chits.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSPatientSearchService;
import org.openmrs.module.chits.Util;
import org.openmrs.patient.IdentifierValidator;
import org.openmrs.patient.UnallowedIdentifierException;
import org.openmrs.web.dwr.PatientListItem;

/**
 * Specialized DWR service for searching patients (e.g., searching only 'Male' patients).
 */
public class DWRMalePatientSearchService {
	/** Logger instance */
	private final Log log = LogFactory.getLog(getClass());

	/** Maximum results */
	private Integer maximumResults;

	/**
	 * Returns a map of results with the values as count of matches and a partial list of the matching patients (depending on values of start and length
	 * parameters) while the keys are are 'count' and 'objectList' respectively, if the length parameter is not specified, then all matches will be returned
	 * from the start index if specified.
	 * 
	 * @param searchValue
	 *            patient name or identifier
	 * @param start
	 *            the beginning index
	 * @param length
	 *            the number of matching patients to return
	 * @param getMatchCount
	 *            Specifies if the count of matches should be included in the returned map
	 * @return a map of results
	 * @throws APIException
	 * @since 1.8
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findCountAndMalePatients(String searchValue, Integer start, Integer length, boolean getMatchCount) throws APIException {
		// Map to return
		Map<String, Object> resultsMap = new HashMap<String, Object>();
		Collection<Object> objectList = new Vector<Object>();
		try {
			CHITSPatientSearchService patientSearchSvc = Context.getService(CHITSPatientSearchService.class);
			int patientCount = 0;
			// if this is the first call
			if (getMatchCount) {
				patientCount += patientSearchSvc.getCountOfMalePatients(searchValue);

				// if only 2 results found and a number was not in the
				// search, then do a decapitated search: trim each word
				// down to the first three characters and search again
				if ((length == null || length > 2) && patientCount < 3 && !searchValue.matches(".*\\d+.*")) {
					String[] names = searchValue.split(" ");
					String newSearch = "";
					for (String name : names) {
						if (name.length() > 3)
							name = name.substring(0, 4);
						newSearch += " " + name;
					}

					newSearch = newSearch.trim();
					if (!newSearch.equals(searchValue)) {
						// since we already know that the list is small, it doesn't hurt to load the hits
						// so that we can remove them from the list of the new search results and get the
						// accurate count of matches
						Collection<Patient> patients = patientSearchSvc.getMalePatients(searchValue);
						newSearch = newSearch.trim();
						Collection<Patient> newPatients = patientSearchSvc.getMalePatients(newSearch);
						newPatients = CollectionUtils.union(newPatients, patients);
						// Re-compute the count of all the unique patient hits
						patientCount = newPatients.size();
						if (newPatients.size() > 0) {
							resultsMap.put(
									"notification",
									Context.getMessageSourceService().getMessage("Patient.warning.minimalSearchResults", new Object[] { newSearch },
											Context.getLocale()));
						}
					}
				}

				// no results found and a number was in the search --
				// should check whether the check digit is correct.
				else if (patientCount == 0 && searchValue.matches(".*\\d+.*")) {

					// Looks through all the patient identifier validators to see if this type of identifier
					// is supported for any of them. If it isn't, then no need to warn about a bad check
					// digit. If it does match, then if any of the validators validates the check digit
					// successfully, then the user is notified that the identifier has been entered correctly.
					// Otherwise, the user is notified that the identifier was entered incorrectly.

					final PatientService ps = Context.getPatientService();
					Collection<IdentifierValidator> pivs = ps.getAllIdentifierValidators();
					boolean shouldWarnUser = true;
					boolean validCheckDigit = false;
					boolean identifierMatchesValidationScheme = false;

					for (IdentifierValidator piv : pivs) {
						try {
							if (piv.isValid(searchValue)) {
								shouldWarnUser = false;
								validCheckDigit = true;
							}
							identifierMatchesValidationScheme = true;
						} catch (UnallowedIdentifierException e) {
						}
					}

					if (identifierMatchesValidationScheme) {
						if (shouldWarnUser)
							resultsMap.put("notification", "<b>" + Context.getMessageSourceService().getMessage("Patient.warning.inValidIdentifier") + "<b/>");
						else if (validCheckDigit)
							resultsMap.put("notification",
									"<b style=\"color:green;\">" + Context.getMessageSourceService().getMessage("Patient.message.validIdentifier") + "<b/>");
					}
				} else {
					// ensure that count never exceeds this value because the API's service layer would never
					// return more than it since it is limited in the DAO layer
					if (maximumResults == null)
						maximumResults = Util.getMaximumSearchResults();
					if (length != null && length > maximumResults)
						length = maximumResults;

					if (patientCount > maximumResults) {
						patientCount = maximumResults;
						if (log.isDebugEnabled())
							log.debug("Limitng the size of matching patients to " + maximumResults);
					}
				}

			}

			// if we have any matches or this isn't the first ajax call when the caller
			// requests for the count
			if (patientCount > 0 || !getMatchCount)
				objectList = findBatchOfMalePatients(searchValue, false, start, length);

			resultsMap.put("count", patientCount);
			resultsMap.put("objectList", objectList);
		} catch (Exception e) {
			log.error("Error while searching for patients", e);
			objectList.clear();
			objectList.add(Context.getMessageSourceService().getMessage("Patient.search.error") + " - " + e.getMessage());
			resultsMap.put("count", 0);
			resultsMap.put("objectList", objectList);
		}
		return resultsMap;
	}

	/**
	 * Search on the <code>searchValue</code>. If a number is in the search string, do an identifier search. Else, do a name search
	 * 
	 * @see PatientService#getPatients(String, String, List, boolean, int, Integer)
	 * @param searchValue
	 *            string to be looked for
	 * @param includeVoided
	 *            true/false whether or not to included voided patients
	 * @param start
	 *            The starting index for the results to return
	 * @param length
	 *            The number of results of return
	 * @return Collection<Object> of PatientListItem or String
	 * @since 1.8
	 */
	@SuppressWarnings("unchecked")
	private Collection<Object> findBatchOfMalePatients(String searchValue, boolean includeVoided, Integer start, Integer length) {
		if (maximumResults == null)
			maximumResults = Util.getMaximumSearchResults();
		if (length != null && length > maximumResults)
			length = maximumResults;

		// the list to return
		List<Object> patientList = new Vector<Object>();

		CHITSPatientSearchService patientSearchSvc = Context.getService(CHITSPatientSearchService.class);
		Collection<Patient> patients;

		try {
			patients = patientSearchSvc.getMalePatients(searchValue, start, length);
		} catch (APIAuthenticationException e) {
			patientList.add(Context.getMessageSourceService().getMessage("Patient.search.error") + " - " + e.getMessage());
			return patientList;
		}

		patientList = new Vector<Object>(patients.size());
		for (Patient p : patients)
			patientList.add(new PatientListItem(p));
		// if the length wasn't limited to less than 3 or this is the second ajax call
		// and only 2 results found and a number was not in the
		// search, then do a decapitated search: trim each word
		// down to the first three characters and search again
		if ((length == null || length > 2) && patients.size() < 3 && !searchValue.matches(".*\\d+.*")) {
			String[] names = searchValue.split(" ");
			String newSearch = "";
			for (String name : names) {
				if (name.length() > 3)
					name = name.substring(0, 4);
				newSearch += " " + name;
			}
			newSearch = newSearch.trim();

			if (!newSearch.equals(searchValue)) {
				Collection<Patient> newPatients = patientSearchSvc.getMalePatients(newSearch, start, length);
				patients = CollectionUtils.union(newPatients, patients); // get unique hits
				// reconstruct the results list
				if (newPatients.size() > 0) {
					patientList = new Vector<Object>(patients.size());
					// patientList.add("Minimal patients returned. Results for <b>" + newSearch + "</b>");
					for (Patient p : newPatients) {
						PatientListItem pi = new PatientListItem(p);
						patientList.add(pi);
					}
				}
			}
		}
		// no results found and a number was in the search --
		// should check whether the check digit is correct.
		else if (patients.size() == 0 && searchValue.matches(".*\\d+.*")) {

			// Looks through all the patient identifier validators to see if this type of identifier
			// is supported for any of them. If it isn't, then no need to warn about a bad check
			// digit. If it does match, then if any of the validators validates the check digit
			// successfully, then the user is notified that the identifier has been entered correctly.
			// Otherwise, the user is notified that the identifier was entered incorrectly.

			final PatientService ps = Context.getPatientService();
			Collection<IdentifierValidator> pivs = ps.getAllIdentifierValidators();
			boolean shouldWarnUser = true;
			boolean validCheckDigit = false;
			boolean identifierMatchesValidationScheme = false;

			for (IdentifierValidator piv : pivs) {
				try {
					if (piv.isValid(searchValue)) {
						shouldWarnUser = false;
						validCheckDigit = true;
					}
					identifierMatchesValidationScheme = true;
				} catch (UnallowedIdentifierException e) {
				}
			}

			if (identifierMatchesValidationScheme) {
				if (shouldWarnUser)
					patientList
							.add("<p style=\"color:red; font-size:big;\"><b>WARNING: Identifier has been typed incorrectly!  Please double check the identifier.</b></p>");
				else if (validCheckDigit)
					patientList
							.add("<p style=\"color:green; font-size:big;\"><b>This identifier has been entered correctly, but still no patients have been found.</b></p>");
			}
		}

		return patientList;
	}
}
