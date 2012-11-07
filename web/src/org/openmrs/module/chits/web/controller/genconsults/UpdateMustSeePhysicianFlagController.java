package org.openmrs.module.chits.web.controller.genconsults;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.PersonService;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Updates the 'Must See Physician' attribute for the given person.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateMustSeePhysicianFlag.form")
public class UpdateMustSeePhysicianFlagController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the Person service */
	protected PersonService personService;

	/**
	 * This method will update the 'must see physician' attribute for the given patient.
	 * 
	 * @param httpSession
	 *            current browser session
	 * @param patientId
	 *            The ID of the patient (person)
	 * @param willSeePhysician
	 *            If the patient should be marked with 'must see physician'
	 * @return a redirect to the view patient consult page
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String updateMustSeePhysicianFlag(HttpSession httpSession, //
			@RequestParam(required = true, value = "patientId") Integer patientId, //
			@RequestParam(required = true, value = "willSeePhysician") Boolean willSeePhysician) throws ServletException {
		final Person person = patientId != null ? personService.getPerson(patientId) : null;
		if (person != null && willSeePhysician != null) {
			// update the flag
			RelationshipUtil.setMustSeePhysicianFlag(person, willSeePhysician);
			personService.savePerson(person);

			// add a success message
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.Patient.status.saved");
		}

		// send back to the view consults page
		return "redirect:/module/chits/consults/viewPatient.form?patientId=" + patientId;
	}

	@Autowired
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
}
