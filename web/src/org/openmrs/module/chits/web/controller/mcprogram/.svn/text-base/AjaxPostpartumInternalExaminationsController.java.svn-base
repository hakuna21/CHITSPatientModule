package org.openmrs.module.chits.web.controller.mcprogram;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Renders the ajaxPostpartumInternalExaminations.jsp page fragment for the specified patient's active maternal care program.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/ajaxPostpartumInternalExaminations.form")
public class AjaxPostpartumInternalExaminationsController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** Auto-wire the Obs service */
	protected ObsService obsService;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(ModelMap model, //
			@RequestParam(required = true, value = "mcProgramObsId") Integer mcProgramObsId, //
			@RequestParam(required = true, value = "patientId") Integer patientId) {
		// initialization the form
		final MaternalCareConsultEntryForm form = new MaternalCareConsultEntryForm();

		// load the patient record (required for rendering the form)
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;
		form.setPatient(patient);

		// load the internal examination record observation for display
		final Obs obs = obsService.getObs(mcProgramObsId);

		// perform some sanity and security checks before allowing the user to view this observation
		if (patient != null && obs != null //
				&& obs.getPerson().getPersonId().equals(form.getPatient().getPersonId()) //
				&& obs.getConcept().getId().equals(ProgramConcepts.MATERNALCARE.getConceptId())) {
			// initialize the internal maternal care bean record to use for viewing
			form.setMcProgramObs(new MaternalCareProgramObs(obs));

			// this is the maternal care program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		} else {
			// invalid view request
			throw new APIAuthenticationException("chits.program.MATERNALCARE.not.enrolled");
		}

		// return the initialized form
		return form;
	}

	/**
	 * This method will display the ajax fragment
	 * 
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm() {
		return "/module/chits/consults/maternalcare/chartfragments/ajaxPostpartumInternalExaminations";
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
