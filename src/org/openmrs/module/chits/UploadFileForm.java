package org.openmrs.module.chits;

import org.springframework.web.multipart.MultipartFile;

/**
 * A generalized form for uploading files.
 * 
 * @author Bren
 */
public class UploadFileForm {
	/** The file to upload */
	private MultipartFile file;

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public MultipartFile getFile() {
		return file;
	}
}
