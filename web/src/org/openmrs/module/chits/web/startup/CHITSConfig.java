package org.openmrs.module.chits.web.startup;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.UserService;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.ConceptUtilFactory;
import org.openmrs.util.LocaleUtility;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defines the {@link ConceptUtilFactory} bean and configures the default locale to 'en_US'.
 * 
 * @author Bren
 */
@Configuration
public class CHITSConfig {
	/** Logger instance */
	protected final Log log = LogFactory.getLog(getClass());

	public CHITSConfig() {
		// default constructor
	}

	@Autowired
	public void updateDefaultLocaleLocale(AdministrationService adminService) throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		// update the default locale to be what's set in the global properties (openmrs hard coded this to en_GB)
		final String defaultLocale = adminService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, "en_US");
		final Field localeField = LocaleUtility.class.getDeclaredField("defaultLocaleCache");
		localeField.setAccessible(true);

		final String localeToUse = !StringUtils.isEmpty(defaultLocale) ? defaultLocale : "en_US";
		log.info("Setting locale to: " + localeToUse);
		localeField.set(null, LocaleUtility.fromSpecification(localeToUse));
	}

	/**
	 * Bean prepares the {@link ConceptUtilFactory}. All required services are passed in the parameters to guarantee that they are usable before initialization
	 * occurs.
	 * 
	 * @param servletContext
	 *            Required for storing references to concepts in the application scope
	 * @param adminService
	 *            Required service for {@link ConceptUtil}
	 * @param conceptService
	 *            Required service for {@link ConceptUtil}
	 * @param programWorkflowService
	 *            Required service for {@link ConceptUtil}
	 * @param userService
	 *            Required service for {@link ConceptUtil}
	 * @param personService
	 *            Required service for {@link ConceptUtil}
	 * @param chitsService
	 *            Required service for initializing the reusable application attributes
	 * @return The singleton {@link ConceptUtilFactory} bean.
	 */
	@Bean
	public ConceptUtilFactory newConceptUtilFactory(final AdministrationService adminService, final ConceptService conceptService,
			final ProgramWorkflowService programWorkflowService, final UserService userService, final PersonService personService,
			final CHITSService chitsService) {
		/**
		 * Setup an instance of the concept util factory using the required services.
		 */
		return new ConceptUtilFactory() {
			@Override
			public ConceptUtil newInstance() {
				return new ConceptUtil(adminService, conceptService, programWorkflowService, userService, personService, chitsService);
			}
		};
	}
}
