package org.openmrs.module.chits.web.controller.eccdprogram;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareConcepts;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator.DateValidationType;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Submits the childcare registration form.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateChildCareDeliveryInformation.form")
public class UpdateDeliveryInformationController extends BaseUpdateChildCarePatientConsultDataController implements Constants {
	/** Delivery information concepts that can be edited */
	private static Collection<CachedConceptId> DELIVERY_CONCEPTS = Arrays.asList( //
			new CachedConceptId[] { ChildCareConcepts.BIRTH_LENGTH, //
					ChildCareConcepts.BIRTH_WEIGHT, //
					ChildCareConcepts.DELIVERY_LOCATION, //
					ChildCareConcepts.METHOD_OF_DELIVERY, //
					ChildCareConcepts.GESTATIONAL_AGE, //
					ChildCareConcepts.BIRTH_ORDER, //
					ChildCareConcepts.DOB_REGISTRATION //
			});

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public ChildCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		final ChildCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// get the parent delivery information observation (if any) form
		final Obs deliveryInformation = Functions.observation(form.getPatient(), ChildCareConcepts.DELIVERY_INFORMATION);

		// load the encounter observations that we need to edit into the form
		setupFormObservations(form, deliveryInformation, getConcepts());

		// this is the child care program
		form.setProgram(ProgramConcepts.CHILDCARE);

		// return the patient
		return form;
	}

	/**
	 * This method will display the patient form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") ChildCareConsultEntryForm form) {
		final Patient patient = form.getPatient();
		if (patient != null) {
			// clear program if the patient is not enrolled in the program
			if (form.getProgram() != null && !Functions.isInProgram(patient, form.getProgram())) {
				// patient is not in this program!
				form.setProgram(null);
			} else {
				// this is the child care program
				form.setProgram(ProgramConcepts.CHILDCARE);
			}
		}

		// dispatch to superclass for additional validation
		return super.showForm(request, httpSession, model, form);
	}

	protected Collection<CachedConceptId> getConcepts() {
		// The concepts that this controller creates / updates
		return DELIVERY_CONCEPTS;
	}

	@Override
	protected void postProcess(HttpServletRequest request, ChildCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// perform extra validation: DOB registration must be on or after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateDateValue(form, ChildCareConcepts.DOB_REGISTRATION, errors, //
				DateValidationType.MUST_NOT_BE_IN_FUTURE, DateValidationType.ON_OR_AFTER_BIRTHDATE);
	}

	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, ChildCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		if (!obsToSave.isEmpty()) {
			// get the parent registration observation (if any) form
			Obs deliveryInformation = Functions.observation(form.getPatient(), ChildCareConcepts.DELIVERY_INFORMATION);
			if (deliveryInformation == null) {
				// create the parent 'delivery information' observation group
				deliveryInformation = newObs(ChildCareConcepts.DELIVERY_INFORMATION, form.getPatient());
			} else {
				// set modified time information
				setUpdated(deliveryInformation);
			}

			// for convenience when viewing the database manually, we set the coded value and value text into the observation group parent
			deliveryInformation.setValueCoded(conceptService.getConcept(ChildCareConcepts.DELIVERY_INFORMATION.getConceptId()));
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(deliveryInformation);

			// set all other observations to be members of the registration observation group form
			for (Obs obs : obsToSave) {
				deliveryInformation.addGroupMember(obs);
			}

			// set the registration data to be a member of the encounter
			enc.addObs(deliveryInformation);
		}
	}

	/**
	 * The version object is the delivery information parent group observation.
	 */
	@Override
	protected Auditable getVersionObject(ChildCareConsultEntryForm form) {
		// extract the current encounter instance
		final Encounter enc = form.getEncounter();

		if (enc != null) {
			// the version object is the latest administered service in this encounter
			return Functions.observation(enc, ChildCareConcepts.DELIVERY_INFORMATION);
		}

		// no available version object
		return null;
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// return full page
		return "/module/chits/consults/childcare/ajaxUpdateChildCareDeliveryInformation";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/updateChildCareDeliveryInformation.form?patientId=" + patientId;
	}
}
