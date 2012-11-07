package org.openmrs.module.chits.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.FamilyFolder;
import org.springframework.util.StringUtils;

/**
 * Allows for serializing/deserializing a FamilyFolderEditor object to a string so that Spring knows how to pass a FamilyFolder back and forth through an html
 * form or other medium
 * 
 * @see FamilyFolderEditor
 */
public class FamilyFolderEditor extends PropertyEditorSupport {
	/** logger */
	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		CHITSService cs = Context.getService(CHITSService.class);
		if (StringUtils.hasText(text)) {
			try {
				setValue(cs.getFamilyFolder(Integer.valueOf(text)));
			} catch (Exception ex) {
				log.error("Error setting text: " + text, ex);
				throw new IllegalArgumentException("Family Folder not found: " + ex.getMessage());
			}
		} else {
			setValue(null);
		}
	}

	/**
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 */
	public String getAsText() {
		FamilyFolder ff = (FamilyFolder) getValue();
		if (ff == null) {
			return "";
		} else {
			return ff.getFamilyFolderId().toString();
		}
	}
}
