package org.openmrs.module.chits.web.controller;

import javax.servlet.ServletException;

import org.openmrs.module.chits.FamilyFolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Patient-specific form controller. Creates the model/view etc for editing patients.
 * 
 * @see org.openmrs.web.controller.person.PersonFormController
 */
@Controller
@RequestMapping(value = "/module/chits/familyfolders/editFolder.form")
public class EditFamilyFolderController extends AddFamilyFolderController {
	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("familyFolder")
	public FamilyFolder formBackingObject(@RequestParam(required = false, value = "familyFolderId") Integer familyFolderId) throws ServletException {
		final FamilyFolder familyFolder = familyFolderId != null ? chitsService.getFamilyFolder(familyFolderId) : null;

		return familyFolder;
	}

	@Override
	protected String getSuccessMessage() {
		return "chits.FamilyFolder.updated";
	}
}
