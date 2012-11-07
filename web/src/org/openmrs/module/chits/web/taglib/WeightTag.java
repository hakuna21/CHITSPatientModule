package org.openmrs.module.chits.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.openmrs.module.chits.Util;

/**
 * Utility tag for writing out an observation representing weight in kilograms
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class WeightTag extends BaseNumericObservationTag {
	/**
	 * Writes out the value which represents the weight in kilograms.
	 */
	@Override
	protected void writeNumericValue(JspWriter out, double value) throws IOException {
		final double pounds = Util.kgToLbs(value);

		// write out in the format f'i
		out.write(LBS_FMT.format(pounds));
		out.write("lb (");
		out.write(KGS_FMT.format(value));
		out.write("Kg)");
	}
}
