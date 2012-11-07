package org.openmrs.module.chits.extension.html;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;
import org.openmrs.util.PrivilegeConstants;

/**
 * This class defines the links that will appear on the administration page under the "chits.Theme.admin.title" heading.
 */
public class AdminExt extends AdministrationSectionExt {

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
	 * @return The media type.
	 */
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getRequiredPrivilege()
	 * @return The extension title.
	 */
	public String getTitle() {
		return "chits.Theme.admin.title";
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getRequiredPrivilege()
	 * @return The required privileges names (comma-separated).
	 */
	public String getRequiredPrivilege() {
		return PrivilegeConstants.VIEW_ADMIN_FUNCTIONS;
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
	 * @return Map<String, String> of <link, title>.
	 */
	public Map<String, String> getLinks() {
		final Map<String, String> map = new LinkedHashMap<String, String>();
		// map.put("module/chits/admin/audit/loggedInUsers.list?showOnlyLoggedInUsers=true", "chits.admin.audit.logged.in.users.title");
		map.put("module/chits/admin/installTheme.htm", "chits.Theme.install.title");
		map.put("module/chits/admin/validateRequiredConceptsController.htm", "chits.admin.validate.required.concepts.title");
		map.put("module/chits/admin/upgradeChitsConcepts.htm", "chits.concepts.upgrade.title");
		map.put("module/chits/admin/templates/installPackagedTemplates.form", "chits.admin.templates.install.packaged.title");
		map.put("module/chits/admin/templates/uploadTemplates.form", "chits.admin.templates.upload.title");
		map.put("module/chits/admin/icd10/uploadICD10Codes.form", "chits.admin.icd10.upload.title");
		map.put("module/chits/admin/drugs/uploadDrugCodes.form", "chits.admin.drugs.upload.title");
		map.put("module/chits/admin/concepts/validateConcepts.form", "chits.admin.concepts.validate.title");
		map.put("module/chits/admin/concepts/uploadConcepts.form", "chits.admin.concepts.upload.title");
		map.put("module/chits/admin/concepts/validateAnswersAndSets.form", "chits.admin.concepts.validate.answers.and.sets.title");

		return map;
	}
}
