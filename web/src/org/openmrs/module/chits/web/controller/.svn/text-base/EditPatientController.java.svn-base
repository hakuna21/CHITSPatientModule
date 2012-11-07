package org.openmrs.module.chits.web.controller;

import javax.servlet.ServletException;

import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.PatientForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Patient-specific form controller. Creates the model/view etc for editing patients.
 * 
 * @see org.openmrs.web.controller.person.PersonFormController
 */
@Controller
@RequestMapping(value = "/module/chits/patients/editPatient.form")
public class EditPatientController extends AddPatientController {
	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public PatientForm formBackingObject(ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// use superclass to initialize the form
		final PatientForm form = super.formBackingObject(model, patientId);

		// store a new blank family folder for default
		form.setFamilyFolder(new FamilyFolder());

		// for new patients, by default assume the mother's record will be looked up instead of creating a new record
		form.setExistingMother(true);

		// for new patients, by default assume the folder record will be looked up instead of creating a new record
		form.setExistingFolder(true);

		// initialize patient attributes for editing
		initAttributesForEdit(form);

		return form;
	}

	@Override
	protected String getSuccessMessage() {
		return "chits.Patient.updated";
	}
}
