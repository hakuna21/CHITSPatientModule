package org.openmrs.module.chits.fpprogram;

import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyInformationConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Family information first entered during registration.
 * 
 * @author Bren
 */
public class FamilyInformation extends GroupObs {
	/** Used merely for storing the average monthly family income */
	private FamilyFolder familyFolder;

	public FamilyInformation() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(FPFamilyInformationConcepts.FAMILY_INFORMATION, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public FamilyInformation(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(FPFamilyInformationConcepts.values());

		// initialize the 'family folder'
		final Person patient = obs.getPerson();
		if (patient != null) {
			// setup the family folder that this patient belongs to
			final List<FamilyFolder> folders = Context.getService(CHITSService.class).getFamilyFoldersOf(patient.getPersonId());
			if (!folders.isEmpty()) {
				// patients can only belong to one folder: get the first
				this.familyFolder = folders.get(0);
			}
		}

		if (this.familyFolder == null) {
			// patient not linked to a family, or patient not found
			this.familyFolder = new FamilyFolder();
		}
	}

	public FamilyFolder getFamilyFolder() {
		return familyFolder;
	}

	public void setFamilyFolder(FamilyFolder familyFolder) {
		this.familyFolder = familyFolder;
	}
}
