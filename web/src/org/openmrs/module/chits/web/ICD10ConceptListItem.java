package org.openmrs.module.chits.web;

import org.openmrs.Concept;
import org.openmrs.module.chits.Constants.ICD10;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebUtil;

/**
 * Custom ConceptListItem that only supports the id, name, and ICD10 code (if any) for more efficient transmission over JSON.
 * 
 * @author Bren
 */
public class ICD10ConceptListItem {
	private Integer conceptId;
	private String name;
	private String icd10;

	public ICD10ConceptListItem() {
		// default constructor doesn't initialize attributes
	}

	public ICD10ConceptListItem(Concept concept) {
		this.conceptId = concept.getConceptId();
		this.name = WebUtil.escapeHTML(concept.getName().getName());
		this.icd10 = Functions.mapping(concept, ICD10.CONCEPT_SOURCE_NAME);
	}

	public Integer getConceptId() {
		return conceptId;
	}

	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcd10() {
		return icd10;
	}

	public void setIcd10(String icd10) {
		this.icd10 = icd10;
	}
}
