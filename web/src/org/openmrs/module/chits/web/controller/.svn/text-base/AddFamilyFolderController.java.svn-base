package org.openmrs.module.chits.web.controller;

import java.text.NumberFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.Util;
import org.openmrs.module.chits.validator.FamilyFolderValidator;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Patient-specific form controller. Creates the model/view etc for adding a family folder.
 * <p>
 * NOTE: Controller and RequestMapping attributes have been removed from this controller since family folders can no longer be created directly; instead, they
 * can only be created within the context of an existing or new patient -- this prevents users from being able to directly create a family folder without any
 * members.
 * 
 * @see org.openmrs.web.controller.person.PersonFormController
 */
// @Controller
// @RequestMapping(value = "/module/chits/familyfolders/addFolder.form")
public class AddFamilyFolderController extends ViewFamilyFolderController {
	/** Auto-wire the administration service */
	protected AdministrationService adminService;

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		final NumberFormat nf = NumberFormat.getInstance(Context.getLocale());
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
	}

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("familyFolder")
	public FamilyFolder formBackingObject() throws ServletException {
		final FamilyFolder blankFamilyFolder = new FamilyFolder();
		blankFamilyFolder.setCode("[New]");
		blankFamilyFolder.setBarangayCode("");
		blankFamilyFolder.setCityCode("");
		blankFamilyFolder.setAddress("");
		blankFamilyFolder.setName("");
		blankFamilyFolder.setNotes("");

		return blankFamilyFolder;
	}

	protected String getFormPath() {
		return "/module/chits/familyfolders/addEditFolderForm";
	}

	protected String getSuccessMessage() {
		return "chits.FamilyFolder.created";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			ModelMap model, //
			@RequestParam(required = false, value = "headOfTheFamily") Integer headOfTheFamily, //
			@RequestParam(required = false, value = "patientIds[]") Integer[] patientIds, //
			@ModelAttribute("familyFolder") FamilyFolder folder, //
			BindingResult errors) {
		if (folder == null) {
			// family folder not found; treat this as an error
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.FamilyFolder.not.found");

			// send back to folder listing page since there is no folder to view!
			return "redirect:foldersList.htm";
		}

		// check version information
		if (getCurrentVersion(folder) != folder.getVersion()) {
			// optimistic locking: version mismatch
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.error.data.concurrent.update");

			// send back to main form without any further action
			return getFormPath();
		}

		// validate the form
		new FamilyFolderValidator().validate(folder, errors);

		if (errors.hasErrors()) {
			// send back to main form
			return getFormPath();
		}

		// save to the database
		folder = chitsService.saveFamilyFolder(folder);

		// set the 'code' based on the family code format
		folder.setCode(Util.formatFolderCode(adminService, folder.getId()));

		// attach the requested patients
		final PatientService patientService = Context.getPatientService();

		// clear old set of members
		folder.getPatients().clear();

		if (patientIds != null) {
			// add new members
			for (Integer patientId : patientIds) {
				try {
					final Patient familyMember = patientService.getPatient(patientId);
					if (familyMember != null) {
						folder.getPatients().add(familyMember);
					}
				} catch (APIAuthenticationException ex) {
					// propagate authorization errors
					throw ex;
				} catch (Exception ex) {
					log.warn("Was not able to add patient: " + patientId);
				}
			}
		}

		if (headOfTheFamily != null) {
			// set the head of the family
			folder.setHeadOfTheFamily(patientService.getPatient(headOfTheFamily));
		} else {
			// there is no head of the family
			folder.setHeadOfTheFamily(null);
		}

		// save again to update the code
		folder = chitsService.saveFamilyFolder(folder);

		// save the 'folder created' message
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { folder.getCode() });
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, getSuccessMessage());

		// send to the folders listing page
		return "redirect:viewFolder.form?familyFolderId=" + folder.getId();
	}

	@Autowired
	public void setAdminService(AdministrationService adminService) {
		this.adminService = adminService;
	}
}
