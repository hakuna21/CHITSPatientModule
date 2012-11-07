package org.openmrs.module.chits.web.controller.mcprogram;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.InternalExaminationRecord;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCIERecordConcepts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Edits an existing internal examination record in the patient's current active maternal care program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/editInternalExaminationRecord.form")
public class EditInternalExaminationRecordController extends AddInternalExaminationRecordController {
	/** Auto-wired service */
	protected ObsService obsService;

	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "internalExaminationRecordObsId") Integer internalExaminationRecordObsId, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// dispatch to superclass for initialization
		final MaternalCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// initialize the internal examination record to use the one being edited
		if (form.getMcProgramObs() != null) {
			// load the observation being edited
			final Obs obs = obsService.getObs(internalExaminationRecordObsId);

			// perform some sanity and security checks before allowing the user to edit this observation
			if (obs != null //
					&& obs.getConcept().getConceptId().equals(MCIERecordConcepts.INTERNAL_EXAMINATION.getConceptId()) //
					&& obs.getPerson().getPersonId().equals(form.getPatient().getPersonId())) {
				// allow user to edit this observation
				form.setInternalExaminationRecord(new InternalExaminationRecord(obs));
			}
		}

		return form;
	}

	@Autowired
	public void setObsService(ObsService obsService) {
		this.obsService = obsService;
	}
}
