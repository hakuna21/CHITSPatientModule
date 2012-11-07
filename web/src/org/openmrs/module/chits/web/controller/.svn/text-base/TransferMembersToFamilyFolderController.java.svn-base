package org.openmrs.module.chits.web.controller;

import java.text.NumberFormat;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.TransferMembersForm;
import org.openmrs.module.chits.Util;
import org.openmrs.module.chits.propertyeditor.FamilyFolderEditor;
import org.openmrs.module.chits.validator.FamilyFolderValidator;
import org.openmrs.propertyeditor.PatientEditor;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Edits the patient's visit notes and (optionally) complaints / diagnoses.
 */
@Controller
@RequestMapping(value = "/module/chits/familyfolders/transferMembersToFamilyFolder.form")
public class TransferMembersToFamilyFolderController {
	/** Auto-wire the Patient service */
	protected PatientService patientService;

	/** CHITS Service */
	protected CHITSService chitsService;

	/** Admin service */
	protected AdministrationService adminService;

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		final NumberFormat nf = NumberFormat.getInstance(Context.getLocale());
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
		binder.registerCustomEditor(org.openmrs.Patient.class, new PatientEditor());
		binder.registerCustomEditor(FamilyFolder.class, new FamilyFolderEditor());
	}

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("form")
	public Object formBackingObject(ModelMap model) throws ServletException {
		final TransferMembersForm form = new TransferMembersForm();
		form.setNewFolder(new FamilyFolder());
		form.setPatients(new ArrayList<Patient>());
		form.setExistingFolder(true);
		form.setTransferTo(null);

		// return the form
		return form;
	}

	/**
	 * This method will display the transfer members form.
	 * 
	 * @param form
	 *            The {@link TransferMembersForm}
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(@ModelAttribute("form") TransferMembersForm form) {
		// send the to the input page
		return "/module/chits/familyfolders/ajaxTransferPatient";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(//
			HttpSession httpSession, //
			@ModelAttribute("form") TransferMembersForm form, //
			BindingResult errors) {
		if (form.isExistingFolder()) {
			if (form.getTransferTo() == null) {
				// must specify folder!
				errors.rejectValue("existingFolder", "chits.folders.transfer.existing.folder.required");
				return showForm(form);
			}

			for (Patient p : form.getPatients()) {
				if (form.getTransferTo().getPatients().contains(p)) {
					errors.rejectValue("existingFolder", "chits.folders.transfer.already.members");
					return showForm(form);
				}
			}

			// transfer the patients to this folder
			for (Patient p : form.getPatients()) {
				form.getTransferTo().getPatients().add(p);
			}

			// save family folder and setup success message
			chitsService.saveFamilyFolder(form.getTransferTo());
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { form.getTransferTo().getCode() });
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.folders.transfer.transferred");
		} else {
			// validate the form
			new FamilyFolderValidator("newFolder.").validate(form.getNewFolder(), errors);
			if (!errors.hasErrors()) {
				// everything passes, prepare the family folder
				for (Patient p : form.getPatients()) {
					form.getNewFolder().getPatients().add(p);
				}

				// save to the database
				form.setTransferTo(chitsService.saveFamilyFolder(form.getNewFolder()));

				// update the 'code' based on the family code format
				form.getTransferTo().setCode(Util.formatFolderCode(adminService, form.getTransferTo().getId()));

				// save again to update the code
				form.setTransferTo(chitsService.saveFamilyFolder(form.getNewFolder()));

				// switch the form to the 'existing' mode
				form.setExistingFolder(true);

				// successfully transferred to a new folder
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { form.getTransferTo().getCode() });
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.folders.transfer.transferred");
			}
		}

		// send back to the 'show' form
		return showForm(form);
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}

	@Autowired
	public void setAdminService(AdministrationService adminService) {
		this.adminService = adminService;
	}
}
