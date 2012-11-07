package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Relationship;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts;
import org.openmrs.module.chits.mcprogram.ObstetricHistory;
import org.openmrs.module.chits.mcprogram.ObstetricHistoryDetail;
import org.openmrs.module.chits.mcprogram.PregnancyOutcome;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Edits an existing obstetric history detail record in the patient's current active maternal care program.
 * 
 * @author Bren
 */
@Controller("MaternalCareEditObstetricHistoryDetailController")
@RequestMapping(value = "/module/chits/consults/editObstetricHistoryDetail.form")
public class EditObstetricHistoryDetailController extends AddObstetricHistoryDetailController {
	/** Auto-wired service */
	protected ObsService obsService;

	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "obstetricHistoryDetailObsId") Integer obstetricHistoryDetailObsId, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// dispatch to superclass for initialization
		final MaternalCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// initialize the obstetric history detail to use the one being edited
		if (form.getMcProgramObs() != null) {
			// load the observation being edited
			final Obs obs = obsService.getObs(obstetricHistoryDetailObsId);

			// perform some sanity and security checks before allowing the user to edit this observation
			if (obs != null //
					&& obs.getConcept().getConceptId().equals(MCObstetricHistoryDetailsConcepts.OBSTETRIC_HISTORY_DETAILS.getConceptId()) //
					&& obs.getPerson().getPersonId().equals(form.getPatient().getPersonId())) {
				// allow user to edit this observation
				form.setObstetricHistoryDetail(new ObstetricHistoryDetail(obs));
			}
		}

		return form;
	}

	/**
	 * If the 'delete' parameter is present, then the detail record will be deleted.
	 * 
	 * @param request
	 * @param model
	 * @param form
	 * @param errors
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, params = { "delete" })
	public String deleteRecord(HttpServletRequest request, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form, //
			BindingResult errors) {
		// purge the record completely
		final Obs obstetricHistoryDetailObs = form.getObstetricHistoryDetail().getObs();

		// void all details
		recursiveVoid(obstetricHistoryDetailObs, "Deleted By User", new Date());

		// update and save observations through the encounter
		final Encounter enc = form.getPatientQueue().getEncounter();
		if (enc != null) {
			// audit the obstetric history record
			final ObstetricHistory obHistory = form.getMcProgramObs().getObstetricHistory();
			obHistory.storePersonAndAudit(form.getPatient());

			// add to encounter for cascading
			enc.addObs(obHistory.getObs());

			// save the encounter
			encounterService.saveEncounter(enc);

			// detach the children from the form
			// detachChildren(form);

			// successfully deleted
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.program.MATERNALCARE.obstetric.history.detail.deleted");
		}

		// send back to input page to display success message
		return super.getInputPath(request);
	}

	/**
	 * Detaches all children from the mother patient.
	 * 
	 * @param form
	 *            The form containing the mother patient details.
	 */
	protected void detachChildren(MaternalCareConsultEntryForm form) {
		// detach all children
		for (PregnancyOutcome outcome : form.getObstetricHistoryDetail().getOutcomes()) {
			final Integer babyPatientId = outcome.getObs().getValueGroupId();
			final Patient babyPatient = babyPatientId != null ? patientService.getPatient(babyPatientId) : null;
			if (babyPatient != null) {
				// detach baby from the family folder of the patient
				final List<FamilyFolder> family = chitsService.getFamilyFoldersOf(form.getPatient().getPatientId());
				for (FamilyFolder ff : family) {
					ff.removePatient(babyPatient);
					if (babyPatient.equals(ff.getHeadOfTheFamily())) {
						// unlikely, but for good measure
						ff.setHeadOfTheFamily(null);
					}

					// update folder to not include this baby
					chitsService.saveFamilyFolder(ff);
				}

				// detach from mother / child relationship
				final Relationship motherRelationship = chitsPatientSearchService.getFemaleParent(babyPatient);
				if (motherRelationship != null) {
					personService.purgeRelationship(motherRelationship);
				}
			}
		}
	}

	/**
	 * Mark all observations as voided
	 * 
	 * @param obs
	 * @param voidReason
	 * @param dateVoided
	 */
	private void recursiveVoid(Obs obs, String voidReason, Date dateVoided) {
		// update void markers
		obs.setDateVoided(dateVoided);
		obs.setVoidReason(voidReason);
		obs.setVoided(Boolean.TRUE);
		obs.setVoidedBy(Context.getAuthenticatedUser());

		// repeat for all members of this observation
		if (obs.hasGroupMembers()) {
			for (Obs member : obs.getGroupMembers()) {
				recursiveVoid(member, voidReason, dateVoided);
			}
		}
	}

	@Autowired
	public void setObsService(ObsService obsService) {
		this.obsService = obsService;
	}
}
