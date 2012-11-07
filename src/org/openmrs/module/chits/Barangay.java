package org.openmrs.module.chits;

import java.io.Serializable;

/**
 * Barangay code
 */
@SuppressWarnings("serial")
public class Barangay implements Serializable {
	
	private Municipality municipality;
	
	private int barangayCode;
	
	private String name;
	
	private boolean urban;
	
	private boolean partiallyUrban;
	
	private boolean rural;
	
	private int population;
	
	/**
	 * @return the municipality
	 */
	public Municipality getMunicipality() {
		return municipality;
	}
	
	/**
	 * @param municipality the municipality to set
	 */
	public void setMunicipality(Municipality municipality) {
		this.municipality = municipality;
	}
	
	/**
	 * @return the barangayCode
	 */
	public int getBarangayCode() {
		return barangayCode;
	}
	
	/**
	 * @param barangayCode the barangayCode to set
	 */
	public void setBarangayCode(int barangayCode) {
		this.barangayCode = barangayCode;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the urban
	 */
	public boolean isUrban() {
		return urban;
	}
	
	/**
	 * @param urban the urban to set
	 */
	public void setUrban(boolean urban) {
		this.urban = urban;
	}
	
	/**
	 * @return the partiallyUrban
	 */
	public boolean isPartiallyUrban() {
		return partiallyUrban;
	}
	
	/**
	 * @param partiallyUrban the partiallyUrban to set
	 */
	public void setPartiallyUrban(boolean partiallyUrban) {
		this.partiallyUrban = partiallyUrban;
	}
	
	/**
	 * @return the rural
	 */
	public boolean isRural() {
		return rural;
	}
	
	/**
	 * @param rural the rural to set
	 */
	public void setRural(boolean rural) {
		this.rural = rural;
	}
	
	/**
	 * @return the population
	 */
	public int getPopulation() {
		return population;
	}
	
	/**
	 * @param population the population to set
	 */
	public void setPopulation(int population) {
		this.population = population;
	}
}
