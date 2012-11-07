package org.openmrs.module.chits.web.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chits.Util;

/**
 * Utility taglib for writing out the elapsed amount of time.
 * 
 * @author Bren
 */
@SuppressWarnings("serial")
public class ElapsedTag extends BodyTagSupport {
	/**
	 * Log
	 */
	private final Log log = LogFactory.getLog(getClass());

	/**
	 * If specified, then the elapsed time is calculated since this time, otherwise elapsed time is calculated up to now.
	 */
	private Date upto;

	/**
	 * Calculate elapsed time since this timestamp.
	 */
	private Date since;

	/**
	 * Render the value.
	 * 
	 * @return return result code
	 */
	public int doStartTag() throws JspException {
		try {
			final JspWriter out = pageContext.getOut();

			if (since != null) {
				if (upto != null) {
					out.write(Util.describeAge(since, upto));
				} else {
					out.write(Util.describeAge(since));
				}
			}
		} catch (IOException e) {
			log.error("Unable to generate vital signs data", e);
		}

		return EVAL_BODY_BUFFERED;
	}

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		try {
			if (bodyContent != null) {
				bodyContent.writeOut(bodyContent.getEnclosingWriter());
			}
		} catch (java.io.IOException e) {
			throw new JspTagException("IO Error: " + e.getMessage());
		}

		return EVAL_PAGE;
	}

	/**
	 * @return the upto
	 */
	public Date getUpto() {
		return upto;
	}

	/**
	 * @param upto
	 *            the upto to set
	 */
	public void setUpto(Date upto) {
		this.upto = upto;
	}

	/**
	 * @return the since
	 */
	public Date getSince() {
		return since;
	}

	/**
	 * @param since
	 *            the since to set
	 */
	public void setSince(Date since) {
		this.since = since;
	}
}
