package org.openmrs.module.chits.web;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;

/**
 * Implements a map returning global property values by property keys.
 * 
 * @author Bren
 */
public class GlobalPropertyMap extends AbstractMap<String, String> {
	/** The admin service for obtaining global property values */
	private final AdministrationService adminService;

	/**
	 * Initializes with the admin service.
	 * 
	 * @param adminService
	 *            The admin service for getting global property values.
	 */
	public GlobalPropertyMap(AdministrationService adminService) {
		this.adminService = adminService;
	}

	/**
	 * Obtains the global property value from the admin service
	 * 
	 * @param key
	 *            The global property to obtain
	 * @return The global property value
	 */
	@Override
	public String get(Object key) {
		return adminService.getGlobalProperty((String) key);
	}

	/**
	 * Overridden to support {@link AbstractMap}
	 */
	@Override
	public Set<Map.Entry<String, String>> entrySet() {
		final Set<Map.Entry<String, String>> entries = new HashSet<Map.Entry<String, String>>();
		for (final GlobalProperty gp : adminService.getAllGlobalProperties()) {
			entries.add(new Map.Entry<String, String>() {
				@Override
				public String getKey() {
					return gp.getProperty();
				}

				@Override
				public String getValue() {
					return gp.getPropertyValue();
				}

				@Override
				public String setValue(String value) {
					throw new UnsupportedOperationException();
				}
			});
		}

		// return the entries
		return entries;
	}
}
