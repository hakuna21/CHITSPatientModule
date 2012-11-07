package org.openmrs.module.chits.impl;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openmrs.module.chits.Barangay;
import org.openmrs.module.chits.Municipality;
import org.openmrs.module.chits.Province;
import org.openmrs.module.chits.Region;

/**
 * Initializes the service by loading the 'barangaycodes.ser' file and stripping it down.
 */
public class StaticBarangayCodesHolder {
	static final StaticBarangayCodesHolder INSTANCE = new StaticBarangayCodesHolder();

	/** Regions without their linked provinces */
	public final List<Region> regions;

	/** Region provinces by region code without the provinces' linked municipalities */
	public final Map<Integer, List<Province>> regionProvinces = new HashMap<Integer, List<Province>>();

	/** province municipalities by province code without the municipalitie's linked barangays */
	public final Map<Integer, List<Municipality>> provinceMunicipalities = new HashMap<Integer, List<Municipality>>();

	/** municipality barangays by municipality code */
	public final Map<Integer, List<Barangay>> municipalityBarangays = new HashMap<Integer, List<Barangay>>();

	/** barangays keyed by barangay code */
	public final Map<String, Barangay> barangays = new HashMap<String, Barangay>();

	/** Municipalities keyed by municipality code */
	public final Map<String, Municipality> municipalities = new HashMap<String, Municipality>();

	/** Sorted list of municipalities keyed by lower-case name and code (&lt;name&gt; &ltcode&gt; ) */
	public final SortedMap<String, Municipality> municipalitiesByName = new TreeMap<String, Municipality>();

	/** Sorted list of barangays keyed by lower-case name and code (&lt;name&gt; &ltcode&gt; ) */
	public final SortedMap<String, Barangay> barangaysByName = new TreeMap<String, Barangay>();

	public static StaticBarangayCodesHolder getInstance() {
		return INSTANCE;
	}

	/**
	 * Initializes the barangay codes.
	 */
	@SuppressWarnings("unchecked")
	private StaticBarangayCodesHolder() {
		try {
			final InputStream in = getClass().getResourceAsStream("/barangaycodes.ser");
			try {
				final ObjectInputStream ois = new ObjectInputStream(in);
				this.regions = (List<Region>) ois.readObject();

				// strip down the objects so that they don't contain references to their children (needed to optimize DWR return values)
				for (Region region : regions) {
					regionProvinces.put(region.getRegionCode(), region.getProvinces());
					for (Province province : region.getProvinces()) {
						provinceMunicipalities.put(province.getProvinceCode(), province.getMunicipalities());
						for (Municipality municipality : province.getMunicipalities()) {
							municipalityBarangays.put(municipality.getMunicipalityCode(), municipality.getBarangays());
							municipalities.put(Integer.toString(municipality.getMunicipalityCode()), municipality);

							for (Barangay barangay : municipality.getBarangays()) {
								// add to full list of barangays
								barangaysByName.put(barangay.getName().toLowerCase() + " " + barangay.getBarangayCode(), barangay);
								barangays.put(Integer.toString(barangay.getBarangayCode()), barangay);
							}

							// add to full list of municipalities
							municipalitiesByName.put(municipality.getName().toLowerCase() + " " + municipality.getMunicipalityCode(), municipality);

							// clear municipality's barangays for optimal JSON serialization
							municipality.setBarangays(null);
						}

						// clear province's municipalities for optimal JSON serialization
						province.setMunicipalities(null);
					}

					// clear region's provinces for optimal JSON serialization
					region.setProvinces(null);
				}
			} finally {
				in.close();
			}
		} catch (Exception ex) {
			throw new IllegalStateException("Unable to load barangay codes", ex);
		}
	}
}