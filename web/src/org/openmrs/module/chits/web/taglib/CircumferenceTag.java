package org.openmrs.module.chits.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.openmrs.module.chits.Util;

/**
 * Utility tag for writing out an observation representing a circumference in centimeters.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class CircumferenceTag extends BaseNumericObservationTag {
	/**
	 * Writes out the value which represents the circumference in centimeters.
	 */
	@Override
	protected void writeNumericValue(JspWriter out, double value) throws IOException {
		double inches = Util.cmToInches(value);

		// write out value in inches
		out.write(INCHES_FMT.format(inches));
		out.write("in (");
		out.write(CM_FMT.format(value));
		out.write("cm)");
	}
}
