package org.openmrs.module.chits.web.controller;

import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.HouseholdInformation;
import org.openmrs.module.chits.propertyeditor.FamilyFolderEditor;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Patient-specific form controller. Creates the model/view etc for updating household information.
 * 
 * @see org.openmrs.web.controller.person.PersonFormController
 */
@Controller
@RequestMapping(value = "/module/chits/familyfolders/updateHouseholdInformation.form")
public class UpdateHouseholdInformationController {
	/** Auto-wire the CHITS service */
	protected CHITSService chitsService;

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(FamilyFolder.class, new FamilyFolderEditor());
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
	}

	@ModelAttribute("familyFolder")
	public FamilyFolder formBackingObject(@RequestParam(required = false, value = "familyFolderId") Integer familyFolderId) throws ServletException {
		// dispatch initialization to superclass
		final FamilyFolder folder = familyFolderId != null ? chitsService.getFamilyFolder(familyFolderId) : null;
		if (folder != null) {
			// if no household information is available, create a blank one
			if (folder.getHouseholdInformation() == null) {
				folder.setHouseholdInformation(new HouseholdInformation());
			}
		}

		// return form backing object
		return folder;
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

		// use minimal headers
		model.addAttribute(WebConstants.OPENMRS_HEADER_USE_MINIMAL, "true");

		// set the version information into the form for optimistic locking
		folder.setVersion(getCurrentVersion(folder));

		// send to the form page
		return getInputPath();
	}

	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			ModelMap model, //
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

			// send back to input page
			return getInputPath();
		}

		if (errors.hasErrors()) {
			// send back to input page
			return getInputPath();
		}

		// creating a new household or modifying an existing one?
		final HouseholdInformation householdInfo = folder.getHouseholdInformation();
		if (householdInfo.getId() == null || householdInfo.getId() == 0) {
			householdInfo.setCreator(Context.getAuthenticatedUser());
			householdInfo.setUuid(UUID.randomUUID().toString());
			householdInfo.setDateCreated(new Date());
		} else {
			householdInfo.setChangedBy(Context.getAuthenticatedUser());
			householdInfo.setDateChanged(new Date());
		}

		// save the family folder to cascade changes into the household info bean and
		// set the 'dateChanged' field required for optimistic locking
		chitsService.saveFamilyFolder(folder);

		// save the household info
		chitsService.saveHouseholdInformation(householdInfo);

		// save the 'household information updated' message
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.HouseholdInformation.updated");

		// send to the view family folder page
		return "redirect:viewFolder.form?familyFolderId=" + folder.getId();
	}

	/**
	 * Links (or unlinks) the current family folder's household information to (or from) the family folder with the id of linkToFamilyId's household.
	 * 
	 * @param httpSession
	 * @param model
	 * @param linkToFamilyId
	 * @param folder
	 * @param errors
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, params = { "linkToFamilyId" })
	public String handleSubmission(HttpSession httpSession, //
			ModelMap model, //
			@RequestParam(required = true, value = "linkToFamilyId") Integer linkToFamilyId, //
			@ModelAttribute("familyFolder") FamilyFolder folder, //
			BindingResult errors) {
		final HouseholdInformation householdInformationBeingEdited = folder.getHouseholdInformation();
		final FamilyFolder linkToFamilyFolder = chitsService.getFamilyFolder(linkToFamilyId);

		// check version information
		if (getCurrentVersion(folder) != folder.getVersion()) {
			// optimistic locking: version mismatch
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.error.data.concurrent.update");

			// send back to input page
			return getInputPath();
		}

		if (folder.equals(linkToFamilyFolder)) {
			// not allowed to link to self
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.HouseholdInformation.cannot.link.to.self");

			// send back to input page
			return getInputPath();
		}

		if (linkToFamilyFolder == null) {
			// 'Remove Link' button clicked: unlink from the current household information data
			householdInformationBeingEdited.getFamilyFolders().remove(folder);
			folder.setHouseholdInformation(null);

			// purge the old household information if needed
			purgeIfOrphanedOrEvict(householdInformationBeingEdited);

			// set success message
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.HouseholdInformation.unlinked");
		} else if (linkToFamilyFolder.getHouseholdInformation() != null) {
			// If the family being linked already has an assigned household ID, the family being
			// edited will assume the household ID of the linked family.
			householdInformationBeingEdited.getFamilyFolders().remove(folder);
			folder.setHouseholdInformation(linkToFamilyFolder.getHouseholdInformation());
			linkToFamilyFolder.getHouseholdInformation().getFamilyFolders().add(folder);

			// purge the old household information if needed
			purgeIfOrphanedOrEvict(householdInformationBeingEdited);

			// success: family folder being edited has been linked to the selected household
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.HouseholdInformation.linked.to");
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { linkToFamilyFolder.getCode() });
		} else {
			// If the family being linked has no Household ID yet, a household ID will be assigned
			// to both families by the system. The linked family will reflect the link on its Family
			// Folder view when accessed.
			linkToFamilyFolder.setHouseholdInformation(householdInformationBeingEdited);
			householdInformationBeingEdited.getFamilyFolders().add(linkToFamilyFolder);
			chitsService.saveHouseholdInformation(householdInformationBeingEdited);
			chitsService.saveFamilyFolder(linkToFamilyFolder);

			// success: household ID will be assigned to both families in the system
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.HouseholdInformation.assigned");
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { linkToFamilyFolder.getCode() });
		}

		// perform save
		chitsService.saveFamilyFolder(folder);

		// send back to the edit page
		return "redirect:updateHouseholdInformation.form?familyFolderId=" + folder.getId();
	}

	/**
	 * Purges the given household information record with the given Id if it has become orphaned; otherwise, it just evits it from the session
	 * 
	 * @param householdInformationId
	 */
	private void purgeIfOrphanedOrEvict(HouseholdInformation householdInformation) {
		if (householdInformation != null && householdInformation.getHouseholdInformationId() != null) {
			if (householdInformation.getFamilyFolders().isEmpty()) {
				// purge this orphaned household information
				chitsService.purgeHouseholdInformation(householdInformation);
			} else {
				// evict form session to prevent updating of fields
				Context.evictFromSession(householdInformation);
			}
		}
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

	protected String getInputPath() {
		return "/module/chits/familyfolders/addEditHouseholdInformationForm";
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}
}
