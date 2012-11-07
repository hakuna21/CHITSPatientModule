package org.openmrs.module.chits.web.controller.admin;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Required concepts validator.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/validateRequiredConceptsController")
public class ValidateRequiredConceptsController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * This method will iterate over all servlet context attributes searching for maps with values of type CachedConceptId and validate that each is defined in
	 * the concept dictionary.
	 * 
	 * @param httpSession
	 *            The request session
	 * @param context
	 *            The servlet context
	 * @return the view to be rendered
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(method = RequestMethod.GET)
	public String validateRequiredConcepts(HttpSession httpSession) {
		// user must be an administrator
		if (!Context.hasPrivilege(PrivilegeConstants.VIEW_ADMIN_FUNCTIONS)) {
			throw new APIAuthenticationException("Privilege required: " + PrivilegeConstants.VIEW_ADMIN_FUNCTIONS);
		}

		try {
			final ServletContext context = httpSession.getServletContext();
			final List<String> conceptsFailingValidation = new ArrayList<String>();
			final Enumeration attribs = context.getAttributeNames();
			while (attribs.hasMoreElements()) {
				final String attrib = (String) attribs.nextElement();
				final Object testMap = context.getAttribute(attrib);
				if (testMap instanceof Map) {
					final Map<Object, Object> map = (Map<Object, Object>) testMap;
					for (Map.Entry entry : map.entrySet()) {
						final Object value = entry.getValue();
						if (value instanceof CachedConceptId) {
							final CachedConceptId concept = (CachedConceptId) value;
							final String key = entry.getKey() + "." + concept.toString();
							log.info("Validating " + key);

							if (!validate(concept)) {
								conceptsFailingValidation.add(key);
							}
						}
					}
				}
			}

			if (conceptsFailingValidation.isEmpty()) {
				log.info("All concepts validated successfully");

				// nothing changes performed
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.concepts.validation.successful");
			} else {
				log.error("Concepts that failed validation: " + conceptsFailingValidation);

				// files were changed
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.admin.concepts.validation.failed");
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { conceptsFailingValidation.toString() });
			}
		} catch (Exception ex) {
			log.error("Concept validation fialed", ex);

			// indicate the error
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ARGS, new Object[] { ex.getMessage() });
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.admin.concepts.validation.failed");
		}

		// send back to the admin page
		return "redirect:/admin/index.htm";
	}

	/**
	 * Validates the given concept returning true if it is properly defined in the concept dictionary
	 * 
	 * @param concept
	 *            The concept to validate
	 * @return true if the concept is properly defined in the concept dictionary
	 */
	private boolean validate(CachedConceptId concept) {
		try {
			if (concept.getConceptId() == null || concept.getConceptId() == 0) {
				throw new IllegalArgumentException("Concept undefined: " + concept);
			}

			// concept is properly defined
			return true;
		} catch (Exception ex) {
			// concept is not defined
			return false;
		}
	}
}
