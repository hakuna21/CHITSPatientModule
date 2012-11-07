package org.openmrs.module.chits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for processing CSV files.
 * 
 * @author Bren
 */
public class CSVUtil {
	/** Contains an ordered the list of header names */
	private final List<String> headers = new ArrayList<String>();

	/** Contains the original headers (not lower-cased) */
	private final List<String> originalHeaders = new ArrayList<String>();

	/** The current row value */
	private String[] currentRow;

	/**
	 * initializes the utility class with the CSV headers from the first row.
	 * 
	 * @param headers
	 */
	public CSVUtil(String[] headers) {
		if (headers != null) {
			for (String header : headers) {
				// add lower-cased value of header
				this.headers.add(header != null ? header.toLowerCase().trim() : "");
				this.originalHeaders.add(header != null ? header : "");
			}
		}
	}

	/**
	 * Returns whether the header is present. The headers are not case sensitive.
	 * 
	 * @param header
	 *            The header name
	 * @return true if the header is contained in the CSV headers, false otherwise.
	 */
	public boolean containsHeader(String header) {
		return this.headers.contains(header.toLowerCase().trim());
	}

	/**
	 * Returns whether all the headers are present. The headers are not case sensitive.
	 * 
	 * @param headers
	 *            The header names
	 * @return true if all the headers are contained in the CSV headers, false otherwise.
	 */
	public boolean containsAllHeaders(Collection<String> headers) {
		for (String header : headers) {
			if (!containsHeader(header)) {
				// not all headers contained
				return false;
			}
		}

		// yes, all the headers were contained
		return true;
	}

	/**
	 * Point to next row.
	 * 
	 * @param row
	 */
	public String[] nextRow(String[] row) {
		this.currentRow = row;
		return row;
	}

	/**
	 * Gets the column value at the column index of the header.
	 * 
	 * @param header
	 *            The header to get the column field value of
	 * @return The value of the field at the given header's index, or null if not available.
	 */
	public String get(String header) {
		final int columnIndex = this.headers.indexOf(header.toLowerCase().trim());
		if (currentRow == null || columnIndex == -1 || currentRow.length < columnIndex) {
			// no column available
			return null;
		} else {
			// send back the field at the header's index
			return currentRow[columnIndex] != null ? currentRow[columnIndex].trim() : null;
		}
	}

	/**
	 * Returns all the headers.
	 * 
	 * @return The original headers in the CSV file.
	 */
	public List<String> getHeaders() {
		return originalHeaders;
	}
}
