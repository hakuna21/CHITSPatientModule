package org.openmrs.module.chits.web.controller.fpprogram;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.module.chits.CHITSPatientSearchService;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.fpprogram.FamilyInformation;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyInformationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningProgramObs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Views an existing family information record of the given patient's current or previous family planning program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/viewFamilyPlanningFamilyInformation.form")
public class ViewFamilyInformationController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** Auto-wire the Obs service */
	protected ObsService obsService;

	/** Indirectly auto-wired RelationshipUtil class */
	protected RelationshipUtil relationshipUtil;

	@ModelAttribute("form")
	public FamilyPlanningConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "familyInformationObsId") Integer familyInformationObsId, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// initialization the form
		final FamilyPlanningConsultEntryForm form = new FamilyPlanningConsultEntryForm();

		// load the patient record (required for rendering the form)
		final Patient patient = patientId != null ? patientService.getPatient(patientId) : null;
		form.setPatient(patient);

		// load the family information record observation for display
		final Obs obs = obsService.getObs(familyInformationObsId);

		// perform some sanity and security checks before allowing the user to view this observation
		if (patient != null && obs != null
				&& obs.getConcept().getConceptId().equals(FPFamilyInformationConcepts.FAMILY_INFORMATION.getConceptId()) //
				&& obs.getPerson().getPersonId().equals(form.getPatient().getPersonId()) && obs.getObsGroup() != null
				&& obs.getObsGroup().getConcept().getId().equals(ProgramConcepts.FAMILYPLANNING.getConceptId())) {
			// initialize the family information record to use for viewing
			form.setFpProgramObs(new FamilyPlanningProgramObs(obs.getObsGroup()));

			// store the patient's partner records (if any)
			final Patient partner = relationshipUtil.getPatientPartnerOrCreteNew(form.getPatient().getPatientId());
			form.setPartner(partner);

			// allow user to view this observation
			form.getFpProgramObs().setFamilyInformation(new FamilyInformation(obs));
		} else {
			// invalid view request
			throw new APIAuthenticationException("chits.program.FAMILYPLANNING.not.enrolled");
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
		return "/module/chits/consults/familyplanning/chartfragments/familyInformation";
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Autowired
	public void setObsService(ObsService obsService) {
		this.obsService = obsService;
	}

	@Autowired
	public void initRelationshipUtil(PersonService personService, PatientService patientService, CHITSPatientSearchService chitsPatientSearchService) {
		this.relationshipUtil = new RelationshipUtil(personService, patientService, chitsPatientSearchService);
	}
}
