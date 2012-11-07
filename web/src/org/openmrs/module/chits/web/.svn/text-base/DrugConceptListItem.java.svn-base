package org.openmrs.module.chits.web;

import org.openmrs.Drug;
import org.openmrs.web.WebUtil;

/**
 * Custom DrugConceptListItem that only supports the name and ID of the drug for efficient transmission over JSON.
 * 
 * @author Bren
 */
public class DrugConceptListItem {
	private Integer drugId;
	private String name;

	public DrugConceptListItem() {
		// default constructor doesn't initialize attributes
	}

	public DrugConceptListItem(Drug drug) {
		this.drugId = drug.getDrugId();
		this.name = WebUtil.escapeHTML(drug.getConcept().getName().getName());
	}

	public Integer getDrugId() {
		return drugId;
	}

	public void setDrugId(Integer drugId) {
		this.drugId = drugId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
