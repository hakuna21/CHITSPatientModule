package org.openmrs.module.chits;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Utility class for adding / removing state information from a patient's the {@link PatientProgram} record.
 * 
 * @author Bren
 */
public class StateUtil {
	/** Logger for this class and subclasses */
	protected final static Log log = LogFactory.getLog(StateUtil.class);

	/**
	 * Adds the given state information to the patient's {@link PatientProgram} records. The caller is responsible for saving the returned
	 * {@link PatientProgram} record to cascade save / update the state information.
	 * 
	 * @param patient
	 *            The patient to update the state information of
	 * @param cachedProgramId
	 *            The program concept
	 * @param cachedStateConceptId
	 *            The state concept to add
	 * @param startDate
	 *            The start date to put into the program
	 * @return The {@link PatientProgram} record that the caller should save to persist the state information
	 */
	public static PatientProgram addState(Patient patient, CachedProgramConceptId cachedProgramId, CachedConceptId cachedStateConceptId, Date startDate,
			Date endDate) {
		final ProgramWorkflowService programWorkflowService = Context.getProgramWorkflowService();
		final ConceptService conceptService = Context.getConceptService();

		final Date now = new Date();
		final Program program = programWorkflowService.getProgram(cachedProgramId.getProgramId());
		final ProgramWorkflow workflow = program.getWorkflowByName(ProgramConcepts.PROGRAM_WORKFLOW.getConceptName());
		final Concept stateConcept = conceptService.getConcept(cachedStateConceptId.getConceptId());
		final ProgramWorkflowState workflowState = workflow.getState(stateConcept);

		// sanity check: is workflow properly configured?
		if (program == null || workflow == null || stateConcept == null || workflowState == null) {
			throw new IllegalStateException("Program (" + cachedProgramId + ") not properly configured");
		}

		// get the current active patient program record for this patient
		final PatientProgram patientProgram = Functions.getActivePatientProgram(patient, cachedProgramId);
		if (patientProgram != null) {
			PatientState state = null;
			for (PatientState testState : patientProgram.getStates()) {
				if (cachedStateConceptId.getConceptId().equals(testState.getState().getConcept().getId())) {
					// this is the state we were looking for
					state = testState;
					break;
				}
			}

			if (state == null) {
				// create a new state record
				state = new PatientState();
				state.setDateCreated(now);
				state.setCreator(Context.getAuthenticatedUser());
				state.setUuid(UUID.randomUUID().toString());
			} else {
				state.setDateChanged(now);
				state.setChangedBy(Context.getAuthenticatedUser());
			}

			// set state information
			state.setPatientProgram(patientProgram);
			state.setStartDate(startDate);
			state.setEndDate(endDate);
			state.setState(workflowState);
			state.setVoided(Boolean.FALSE);

			// update modification information for the patient program
			patientProgram.setDateChanged(now);
			patientProgram.setChangedBy(Context.getAuthenticatedUser());

			// add the state to the program (if state already existed, this will not modify the set)
			patientProgram.getStates().add(state);
		}

		// return the patient program: NOTE: The caller must cascade save or update the state record by saving the patient program
		return patientProgram;
	}

	/**
	 * Removes the given state information from the patient's {@link PatientProgram} records. The caller is responsible for saving the returned
	 * {@link PatientProgram} record to cascade save / update the state information.
	 * 
	 * @param patient
	 *            The patient to update the state information of
	 * @param cachedProgramId
	 *            The program concept
	 * @param cachedStateConceptId
	 *            The state concept to remove
	 * @return The {@link PatientProgram} record that the caller should save to persist the state information
	 */
	public static PatientProgram removeState(Patient patient, CachedProgramConceptId cachedProgramId, CachedConceptId cachedStateConceptId) {
		final ProgramWorkflowService programWorkflowService = Context.getProgramWorkflowService();
		final ConceptService conceptService = Context.getConceptService();

		final Date now = new Date();
		final Program program = programWorkflowService.getProgram(cachedProgramId.getProgramId());
		final ProgramWorkflow workflow = program.getWorkflowByName(ProgramConcepts.PROGRAM_WORKFLOW.getConceptName());
		final Concept stateConcept = conceptService.getConcept(cachedStateConceptId.getConceptId());
		final ProgramWorkflowState workflowState = workflow.getState(stateConcept);

		// sanity check: is workflow properly configured?
		if (program == null || workflow == null || stateConcept == null || workflowState == null) {
			throw new IllegalStateException("Program (" + cachedProgramId + ") not properly configured");
		}

		// get the current active patient program record for this patient
		PatientProgram patientProgram = Functions.getActivePatientProgram(patient, cachedProgramId);
		if (patientProgram != null) {
			// does patient already have the state?
			PatientState state = null;
			for (PatientState testState : patientProgram.getStates()) {
				if (cachedStateConceptId.getConceptId().equals(testState.getState().getConcept().getId())) {
					// this is the state we were looking for
					state = testState;
					break;
				}
			}

			if (state != null) {
				// remove state from the program
				state.setPatientProgram(null);
				patientProgram.getStates().remove(state);

				// update modification information for the patient program
				patientProgram.setDateChanged(now);
				patientProgram.setChangedBy(Context.getAuthenticatedUser());
			}
		}

		// return the patient program: NOTE: The caller must cascade save or update the state record by saving the patient program
		return patientProgram;
	}
}
