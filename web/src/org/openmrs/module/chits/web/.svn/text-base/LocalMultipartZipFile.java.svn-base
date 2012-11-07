package org.openmrs.module.chits.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of {@link MultipartFile} file that references a local file with a content type of "application/zip".
 * 
 * @author Bren
 */
public class LocalMultipartZipFile implements MultipartFile {
	private final File file;

	public LocalMultipartZipFile(File file) {
		this.file = file;
	}

	@Override
	public void transferTo(File destFile) throws IOException, IllegalStateException {
		// copy contents to the requested destination
		FileCopyUtils.copy(file, destFile);
	}

	@Override
	public boolean isEmpty() {
		return file.length() == 0;
	}

	@Override
	public long getSize() {
		return file.length();
	}

	@Override
	public String getOriginalFilename() {
		return file.getName();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public String getContentType() {
		return "application/zip";
	}

	@Override
	public byte[] getBytes() throws IOException {
		throw new UnsupportedOperationException();
	}
}
