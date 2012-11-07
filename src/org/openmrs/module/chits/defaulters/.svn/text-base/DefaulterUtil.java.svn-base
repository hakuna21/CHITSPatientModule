package org.openmrs.module.chits.defaulters;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.Defaulter;
import org.openmrs.module.chits.Defaulter.DueServiceInfo;
import org.openmrs.module.chits.Defaulter.DueServiceInfo.ServiceType;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareUtil;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Creates due service records for efficient generation of the defaulters list.
 * <p>
 * Plan for synchronization:
 * <ul>
 * <li>Implement updating of patient's defaulter observations when an encounter is saved
 * <li>Add a family service advice to reset all family member defaulter observations (so that sync of someone authorized in that barangay will pick them up
 * despite timestamps being from an earlier sync) <br/>
 * <br/>
 * Advice applied when:
 * <ul>
 * <li>family barangay changed
 * <li>patient member added (not removed)
 * <li>head of the family set
 * </ul>
 * <li>Implement mechanism to prepare defaulter observations for all patients upon first synchronizing a barangay
 * <li>Implement syncing of defaulters based on last sync timestamp: - include items where valueDateTime &gt; last sync timestamp or valueVoided &gt; last sync
 * timestamp
 * </ul>
 * 
 * @author Bren
 */
public class DefaulterUtil {
	private final ObsService obsService;

	private final CHITSService chitsService;

	/**
	 * Constructs the utility class which is dependent on the given services.
	 * 
	 * @param programWorkflowService
	 * @param obsService
	 * @param conceptService
	 */
	public DefaulterUtil(CHITSService chitsService, ProgramWorkflowService programWorkflowService, ObsService obsService, ConceptService conceptService) {
		this.chitsService = chitsService;
		this.obsService = obsService;
	}

	/**
	 * Updates the patient's due service observations:
	 * <ul>
	 * <li>voids old entries that are no longer due (or invalid)
	 * <li>adds new obs to represent due services
	 * </ul>
	 * 
	 * @param patient
	 *            The patient to update the due services of.
	 */
	public void updateDueServices(Patient patient) {
		// get all the due services for this patient currently in the database mapped by description
		final Map<String, Obs> oldDueServices = new HashMap<String, Obs>();

		// map which services are to be added or updated
		final List<Obs> overdueServicesToSave = new ArrayList<Obs>();

		// search existing obs
		for (Obs dueServiceObs : Functions.observations(patient, ServiceType.SERVICE_TYPE)) {
			final String description = dueServiceObs.getValueText();
			if (description != null && !oldDueServices.containsKey(description)) {
				// map by description
				oldDueServices.put(description, dueServiceObs);
			} else {
				// invalid due service observation (either null or duplicate description): delete it
				dueServiceObs.setVoided(Boolean.TRUE);
				dueServiceObs.setVoidReason("Invalid or duplicate");
				dueServiceObs.setDateVoided(new Date());
				chitsService.save(dueServiceObs);
			}
		}

		// calculate all due services for the given patient
		final Defaulter defaulter = new Defaulter();
		defaulter.setPatient(patient);
		loadOverdueServices(defaulter);

		// determine what services need to be updated, deleted, created, or left alone
		for (DueServiceInfo dsi : defaulter.getDueServices()) {
			// find out if there is an existing due service (remove it from the 'oldDueServices' as well since it is still due
			Obs dueServiceObs = oldDueServices.remove(dsi.getDescription());
			final Concept serviceConcept = dueServiceObs != null ? dueServiceObs.getValueCoded() : null;
			if (dueServiceObs != null) {
				// check if the due service info has changed
				if ((dueServiceObs.getValueDatetime() == null || !dueServiceObs.getValueDatetime().equals(dsi.getDateDue())) //
						|| (serviceConcept == null || !serviceConcept.getConceptId().equals(dsi.getServiceType().getConceptId()))) {
					// this due service has changed, void the old one to make a new one
					dueServiceObs.setVoided(Boolean.TRUE);
					dueServiceObs.setVoidReason("Changed");
					dueServiceObs.setDateVoided(new Date());
					chitsService.save(dueServiceObs);

					// mark as null to indicate that a new obs needs to be created
					dueServiceObs = null;
				}
			}

			if (dueServiceObs == null) {
				// create a new observation for this due service
				dueServiceObs = ObsUtil.newObs(Functions.concept(ServiceType.SERVICE_TYPE), patient);
				dsi.storeInto(dueServiceObs);

				// mark this for update (ensure an up-to-date obsDatetime value)
				dueServiceObs.setObsDatetime(new Date());
				overdueServicesToSave.add(dueServiceObs);
			}
		}

		// save all the 'new' due services
		for (Obs obs : overdueServicesToSave) {
			obsService.saveObs(obs, "New due service");
		}

		// delete all out-of-date observations
		for (Obs obs : oldDueServices.values()) {
			// this service is no longer due
			obs.setVoided(Boolean.TRUE);
			obs.setVoidReason("No longer due");
			obs.setDateVoided(new Date());
			chitsService.save(obs);
		}
	}

	/**
	 * Loads all relevant due services for the patient in the given Defaulter record.
	 * 
	 * @param defaulter
	 *            The record to store the due services into (should already contain the patient)
	 */
	private void loadOverdueServices(Defaulter defaulter) {
		final Patient patient = defaulter.getPatient();
		if (patient != null && patient.getBirthdate() != null) {
			// if patient is in the ECCD program and the program is not yet closed...
			if (Functions.isInProgram(patient, ProgramConcepts.CHILDCARE)) {
				// calculate overdue services
				ChildCareUtil.addDueServices(defaulter);
			}

			if (Functions.isInProgram(patient, ProgramConcepts.MATERNALCARE)) {
				/*
				 * Un-comment to enable downloading of tetanus records: // calculate overdue teatnus toxoid services
				 * MaternalCareUtil.addDueTetanusToxoidServices(defaulter);
				 */

				// if patient is in the MC program and the baby hast not yet been delivered, check overdue prenatal visits...
				if (MaternalCareUtil.isCurrentlyEnrolledAndBabyNotYetDelivered(patient)) {
					// calculate overdue prenatal services
					MaternalCareUtil.addDuePrenatalServices(defaulter);
				} else {
					// calculate overdue postnatal services
					MaternalCareUtil.addDuePostnatalServices(defaulter);
				}
			}
		}
	}
}
