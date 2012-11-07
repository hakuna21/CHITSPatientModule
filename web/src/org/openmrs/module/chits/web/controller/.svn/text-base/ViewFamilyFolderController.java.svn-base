package org.openmrs.module.chits.web.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Patient-specific form controller. Creates the model/view etc for editing patients.
 * 
 * @see org.openmrs.web.controller.person.PersonFormController
 */
@Controller
@RequestMapping(value = "/module/chits/familyfolders/viewFolder.form")
public class ViewFamilyFolderController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the CHITS service */
	protected CHITSService chitsService;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@ModelAttribute("familyFolder")
	public FamilyFolder formBackingObject(@RequestParam(required = false, value = "familyFolderId") Integer familyFolderId) throws ServletException {
		final FamilyFolder familyFolder = familyFolderId != null ? chitsService.getFamilyFolder(familyFolderId) : null;

		return familyFolder;
	}

	/**
	 * This method will display the family folder form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("familyFolder") FamilyFolder folder) {
		if (folder == null) {
			// family folder not found; treat this as an error
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.FamilyFolder.not.found");

			// send back to folder listing page since there is no folder to view!
			return "redirect:foldersList.htm";
		}

		// set the transient version information into the folder for optimistic locking
		folder.setVersion(getCurrentVersion(folder));

		// send to the form page
		return getFormPath();
	}

	/**
	 * Returns the current version of the folder being edited.
	 * 
	 * @param folder
	 *            The {@link FamilyFolder} instantce to extract the version of.
	 * @return The version of the version object.
	 */
	protected long getCurrentVersion(FamilyFolder versionObject) {
		if (versionObject != null) {
			// use the 'dateChanged' value as the version
			return versionObject.getDateChanged() != null ? versionObject.getDateChanged().getTime() : 0;
		}

		// no available object, version is '0'
		return 0;
	}

	protected String getFormPath() {
		return "/module/chits/familyfolders/viewFolderForm";
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}
}
