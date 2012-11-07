package org.openmrs.module.chits.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.module.chits.Barangay;
import org.openmrs.module.chits.Municipality;
import org.openmrs.module.chits.Province;
import org.openmrs.module.chits.Region;
import org.openmrs.module.chits.impl.StaticBarangayCodesHolder;

/**
 * DWR service for optimal retrieval of barangay information.
 */
public class DWRBarangayService {
	/** Never return more than 100 matching results for performance reasons */
	private static final int MAX_RESULTS = 100;

	/** barangay codes holder */
	private final StaticBarangayCodesHolder holder = StaticBarangayCodesHolder.getInstance();

	/**
	 * Returns a stripped-down list of {@link Region} without the list of connected provinces.
	 * 
	 * @return List of all regions
	 */
	public List<Region> getAllRegions() {
		return holder.regions;
	}

	/**
	 * Returns a stripped-down list of all {@link Province}s without the list of connected municipalities.
	 * 
	 * @return List of all provinces
	 */
	public List<Province> getAllProvinces() {
		final List<Province> allProvinces = new ArrayList<Province>();
		for (List<Province> provinces : holder.regionProvinces.values()) {
			allProvinces.addAll(provinces);
		}

		return allProvinces;
	}

	/**
	 * Returns a stripped-down list of all provinces of the specified region without the list of connected municipalities.
	 * 
	 * @param regionCode
	 *            The region code
	 * @return All provinces of the specified region
	 */
	public List<Province> getProvinces(Integer regionCode) {
		return holder.regionProvinces.get(regionCode);
	}

	/**
	 * Returns a stripped-down list of all municipalities of the specified province without the list of connected barangays.
	 * 
	 * @param provinceCode
	 *            The province code
	 * @return All municipalities of the specified province.
	 */
	public List<Municipality> getMunicipalities(Integer provinceCode) {
		return holder.provinceMunicipalities.get(provinceCode);
	}

	/**
	 * Returns a list of all barangays for the specified municipality.
	 * 
	 * @param municipalityCode
	 *            The municipality code
	 * @return All barangays of the specified municipality
	 */
	public List<Barangay> getBarangays(Integer municipalityCode) {
		return holder.municipalityBarangays.get(municipalityCode);
	}

	/**
	 * Searches for all {@link Municipality} instances that contain the given text in the name.
	 * 
	 * @param regionCode
	 *            If non-zero, only municipalities in the specified region will be searched
	 * @param provinceCode
	 *            If non-zero, only municipalities in the specified province will be searched
	 * @param like
	 *            Text to search for within municipality names
	 * @return All matching municipalities
	 */
	public List<Municipality> findMunicipalities(Integer regionCode, Integer provinceCode, String like) {
		// use case insensitive search
		like = like.toLowerCase();

		final List<Municipality> matchingMunicipalities = new ArrayList<Municipality>();
		for (Map.Entry<String, Municipality> municipalityEntry : holder.municipalitiesByName.entrySet()) {
			final String lowerCaseName = municipalityEntry.getKey();
			final Municipality municipality = municipalityEntry.getValue();

			// check for province filter
			final Province province = municipality.getProvince();
			if (provinceCode != null && provinceCode != 0 && provinceCode != province.getProvinceCode()) {
				// filter does not pass
				continue;
			}

			// check for region filter
			final Region region = province.getRegion();
			if (regionCode != null && regionCode != 0 && region.getRegionCode() != regionCode) {
				// filter does not pass
				continue;
			}

			if (lowerCaseName.contains(like)) {
				matchingMunicipalities.add(municipality);

				if (matchingMunicipalities.size() > MAX_RESULTS) {
					break;
				}
			}
		}

		// return all matches
		return matchingMunicipalities;
	}

	/**
	 * Searches for all {@link Barangay} instances that contain the given text in the name.
	 * 
	 * @param regionCode
	 *            If non-zero, only barangays in the specified region will be searched
	 * @param provinceCode
	 *            If non-zero, only barangays in the specified province will be searched
	 * @param municipalityCode
	 *            If non-zero, only barangays in the specified municipality will be searched
	 * @param like
	 *            Text to search for within barangay names
	 * @return All matching barangays
	 */
	public List<Barangay> findBarangays(Integer regionCode, Integer provinceCode, Integer municipalityCode, String like) {
		// use case insensitive search
		like = like.toLowerCase();

		final List<Barangay> matchingBarangays = new ArrayList<Barangay>();
		for (Map.Entry<String, Barangay> barangayEntry : holder.barangaysByName.entrySet()) {
			final String lowerCaseName = barangayEntry.getKey();
			final Barangay barangay = barangayEntry.getValue();

			// check for municipality filter
			final Municipality municipality = barangay.getMunicipality();
			if (municipalityCode != null && municipalityCode != 0 && municipalityCode != municipality.getMunicipalityCode()) {
				// filter does not pass
				continue;
			}

			// check for province filter
			final Province province = municipality.getProvince();
			if (provinceCode != null && provinceCode != 0 && provinceCode != province.getProvinceCode()) {
				// filter does not pass
				continue;
			}

			// check for region filter
			final Region region = province.getRegion();
			if (regionCode != null && regionCode != 0 && region.getRegionCode() != regionCode) {
				// filter does not pass
				continue;
			}

			if (lowerCaseName.contains(like)) {
				matchingBarangays.add(barangay);

				if (matchingBarangays.size() > MAX_RESULTS) {
					break;
				}
			}
		}

		// return all matches
		return matchingBarangays;
	}
}
