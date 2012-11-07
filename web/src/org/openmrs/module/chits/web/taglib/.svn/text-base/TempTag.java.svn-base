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
public class TempTag extends BaseNumericObservationTag {
	/**
	 * Writes out the value which represents the height in centimeters.
	 */
	@Override
	protected void writeNumericValue(JspWriter out, double value) throws IOException {
		double fahrenheit = Util.centigradeToFahrenheit(value);

		// write out in the format
		out.write(TEMP_C_FMT.format(value));
		out.write("&deg;C (");
		out.write(TEMP_F_FMT.format(fahrenheit));
		out.write("&deg;F)");
	}
}
