package org.openmrs.module.chits.installer;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Encoder;

/**
 * Extracts the theme files to the openmrs context directory.
 * <p>
 * This class calculates the SHA1 checksums of the files to determine which files need to be overwritten / created. Files that are already up-to-date are
 * skipped.
 * 
 * @author Bren
 */
public class ThemeInstaller {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Performs the install.
	 * 
	 * @param contextRoot
	 *            The root path to the the webapp context.
	 * @return The number of files that were created / updated.
	 * @throws IOException
	 *             If any error occurs while processing the files
	 * @throws NoSuchAlgorithmException
	 *             If there were errors attempting to calculate the checksum of the files.
	 */
	public int doInstall(File contextRoot) throws IOException, NoSuchAlgorithmException {
		int totalFilesUpdated = 0;
		log.info("Installing theme to: " + contextRoot.getCanonicalFile());
		final ZipFile zipFile = new ZipFile(new File(contextRoot, "WEB-INF/view/module/chits/openmrs-chits-theme.zip"));
		try {
			final Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				final ZipEntry zipEntry = entries.nextElement();
				if (zipEntry.isDirectory()) {
					// ignore directories
					continue;
				}

				// get zip entry checksum
				final InputStream zipEntryIS = zipFile.getInputStream(zipEntry);
				final String zipEntryChecksum;
				try {
					zipEntryChecksum = calculateChecksum(zipEntryIS);
				} finally {
					zipEntryIS.close();
				}

				// get corresponding file checksum
				final String currentFileChecksum;
				final File destination = new File(contextRoot, zipEntry.getName());
				if (destination.exists()) {
					final InputStream currentFileIS = new FileInputStream(destination);
					try {
						currentFileChecksum = calculateChecksum(currentFileIS);
					} finally {
						currentFileIS.close();
					}
				} else {
					currentFileChecksum = "";
				}

				if (!zipEntryChecksum.equals(currentFileChecksum)) {
					log.info("Copying: " + zipEntry.getName() + " to: " + destination.getAbsolutePath());

					// copy zip data to the file
					final InputStream source = zipFile.getInputStream(zipEntry);
					try {
						copy(source, destination);
					} finally {
						source.close();
					}

					// increment the number of files changed
					totalFilesUpdated++;
				} else {
					log.info("Skipping: " + zipEntry.getName());
				}
			}
		} finally {
			zipFile.close();
		}

		// send back the total number of files we've updated
		return totalFilesUpdated;
	}

	private void copy(InputStream source, File currentFile) throws IOException {
		final File dir = currentFile.getParentFile();
		if (dir != null && !dir.exists() || !dir.isDirectory()) {
			if (!dir.mkdirs()) {
				throw new IOException(String.format("Unable to create directory: %s", dir.toString()));
			}
		}

		// copy stream content into the destination file
		final byte[] buffer = new byte[8196];
		final OutputStream sink = new FileOutputStream(currentFile);
		try {
			while (true) {
				final int read = source.read(buffer);
				if (read > 0) {
					sink.write(buffer, 0, read);
				} else if (read == -1) {
					break;
				}
			}
		} catch (EOFException eofe) {
			// strangely, this really does occur instead of read() returning -1
		} finally {
			// close the output file;
			sink.close();
		}
	}

	private String calculateChecksum(InputStream in) throws IOException, NoSuchAlgorithmException {
		final MessageDigest digest = MessageDigest.getInstance("SHA1");
		final byte[] buffer = new byte[8196];
		try {
			while (true) {
				final int read = in.read(buffer);
				if (read > 0) {
					digest.update(buffer, 0, read);
				} else if (read == -1) {
					break;
				}
			}
		} catch (EOFException eofe) {
			// strangely, this really may occur instead of read() returning -1
		}

		return new BASE64Encoder().encode(digest.digest());
	}
}
