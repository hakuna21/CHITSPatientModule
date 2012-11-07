package org.openmrs.module.chits.web.controller.mcprogram;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.IronSupplementationServiceRecord;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCServiceRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCServiceTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Views an existing iron supplementation service record of the given patient's current or previous maternal care program.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/viewIronSupplementationServiceRecord.form")
public class ViewIronSupplementationServiceRecordController {
	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** Auto-wire the Obs service */
	protected ObsService obsService;

	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "ironSupplementationServiceRecordObsId") Integer ironSupplementationServiceRecordObsId, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// initialization the form
		final MaternalCareConsultEntryForm form = new MaternalCareConsultEntryForm();

		// load the patient record (required for rendering the form)
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;
		form.setPatient(patient);

		// load the iron supplementation service record observation for display
		final Obs obs = obsService.getObs(ironSupplementationServiceRecordObsId);

		// perform some sanity and security checks before allowing the user to view this observation
		if (patient != null && obs != null && //
				obs.getConcept().getConceptId().equals(MCServiceRecordConcepts.SERVICE_TYPE.getConceptId()) //
				&& obs.getValueCoded().getConceptId().equals(MCServiceTypes.FERROUS_SULFATE.getConceptId()) //
				&& obs.getPerson().getPersonId().equals(form.getPatient().getPersonId())) {
			// initialize the irons upplementation service record to use for viewing
			form.setIronSupplementationServiceRecord(new IronSupplementationServiceRecord(obs));
		} else {
			// invalid view request
			throw new APIAuthenticationException("chits.program.MATERNALCARE.not.enrolled");
		}

		return form;
	}

	/**
	 * This method will display the ajax fragment
	 * 
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm() {
		return "/module/chits/consults/maternalcare/ajaxViewIronSupplementationServiceRecordDetails";
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Autowired
	public void setObsService(ObsService obsService) {
		this.obsService = obsService;
	}
}
