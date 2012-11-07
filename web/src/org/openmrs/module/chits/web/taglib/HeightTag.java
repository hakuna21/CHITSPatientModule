package org.openmrs.module.chits.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.openmrs.module.chits.Util;

/**
 * Utility tag for writing out an observation representing height in centimeters.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class HeightTag extends BaseNumericObservationTag {
	/**
	 * Writes out the value which represents the height in centimeters.
	 */
	@Override
	protected void writeNumericValue(JspWriter out, double value) throws IOException {
		double inches = Util.cmToInches(value);
		final int feet = ((int) inches) / 12;
		inches -= 12 * feet;

		// write out in the format f'i
		out.write(Integer.toString(feet));
		out.write("'");
		out.write(INCHES_FMT.format(inches));
		out.write(" (");
		out.write(CM_FMT.format(value));
		out.write("cm)");
	}
}
