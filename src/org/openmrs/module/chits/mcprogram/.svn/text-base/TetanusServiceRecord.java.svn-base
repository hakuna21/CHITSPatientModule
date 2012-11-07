package org.openmrs.module.chits.mcprogram;

import java.util.Date;

import org.openmrs.Obs;
import org.openmrs.User;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidDateAdministeredConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidRecordConcepts;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Encapsulates details of a a Tetanus Toxoid service record.
 * <p>
 * NOTE: TetanusDetailsConcepts by default uses the date administered value for TT1 ("tetanus toxoid first dose, date administered"), however, the concept
 * changes based on the vaccine type. i.e., one of the following will be used based on the vaccine type:
 * <ul>
 * <li>TetanusToxoidDateAdministeredConcepts.TT1 ("tetanus toxoid first dose, date administered")
 * <li>TetanusToxoidDateAdministeredConcepts.TT2 ("tetanus toxoid second dose, date administered")
 * <li>TetanusToxoidDateAdministeredConcepts.TT3 ("tetanus toxoid third dose, date administered")
 * <li>TetanusToxoidDateAdministeredConcepts.TT4 ("tetanus toxoid fourth dose, date administered")
 * <li>TetanusToxoidDateAdministeredConcepts.TT5 ("tetanus toxoid fifth dose, date administered")
 * </ul>
 * 
 * @author Bren
 */
public class TetanusServiceRecord extends GroupObs {
	/** Stores the 'date administered' value */
	private Date dateAdministered;

	/** Stores the 'administered by' value */
	private User administeredBy;

	public TetanusServiceRecord() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(TetanusToxoidRecordConcepts.VACCINE_TYPE, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public TetanusServiceRecord(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set 'administeredBy' and 'dateAdministered' for existing records only
		if (obs.getId() != null && obs.getId() != 0) {
			administeredBy = obs.getCreator();
			dateAdministered = findDateAdministered();
		}

		// set heterogenous member concepts
		super.setConceptsExceptFirst(TetanusToxoidRecordConcepts.values());
	}

	/**
	 * Finds the 'date administered' value based on the service type.
	 * <p>
	 * NOTE: Current implementation simply searches for the available TetanusToxoidDateAdministeredConcepts since only one should exist anyway per observation
	 * group.
	 * 
	 * @return The visit date
	 */
	private Date findDateAdministered() {
		Obs dateAdministered = null;

		// get first available 'date administered' value
		for (CachedConceptId dateAdministeredConcept : TetanusToxoidDateAdministeredConcepts.values()) {
			dateAdministered = Functions.observation(getObs(), dateAdministeredConcept);
			if (dateAdministered != null) {
				// found!
				break;
			}
		}

		return dateAdministered != null ? dateAdministered.getValueDatetime() : null;
	}

	public Date getDateAdministered() {
		return dateAdministered;
	}

	public void setDateAdministered(Date dateAdministered) {
		this.dateAdministered = dateAdministered;
	}

	public User getAdministeredBy() {
		return administeredBy;
	}

	public void setAdministeredBy(User administeredBy) {
		this.administeredBy = administeredBy;
	}
}
