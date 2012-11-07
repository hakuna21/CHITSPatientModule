package org.openmrs.module.chits.web.controller.eccdprogram;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.chits.CHITSPatientModuleActivator;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.OpenSessionIfNeeded;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.BreastFeedingConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareProgramStates;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServiceSourceConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServiceTypes;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServicesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareVaccinesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.DewormingServiceConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.FerrousSulfateServiceConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.MethodOfDeliveryConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.NewbornScreeningConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.NewbornScreeningInformation;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.NewbornScreeningResults;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.VaccinationConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.VitaminAServiceConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareUtil.ChildProtectedAtBirthStatus;
import org.openmrs.module.chits.eccdprogram.ChildCareUtil.ImmunizationStatus;
import org.openmrs.module.chits.eccdprogram.ServiceDueInfo.ServiceDueInfoType;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Sets up the following startup tasks:
 * <ul>
 * <li>Registers the {@link InitChildCareVarsTask}
 * </ul>
 * 
 * @author Bren
 */
@Configuration
public class CHITSChildCareStartupConfiguration {
	/** Logger instance */
	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	public void initChildCareApplicationVariablesTask(final ServletContext servletContext, final AdministrationService adminService) {
		// add a task to initialize the servlet context attributes when ready
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new InitChildCareVarsTask(servletContext, adminService));
	}

	/**
	 * Initializes child care application context variables used throughout the CHITS Child Care module.
	 * 
	 * @author Bren
	 */
	class InitChildCareVarsTask extends OpenSessionIfNeeded {
		/** The servlet context */
		private final ServletContext servletContext;

		InitChildCareVarsTask(ServletContext servletContext, AdministrationService adminService) {
			super(PrivilegeConstants.VIEW_CONCEPTS);
			this.servletContext = servletContext;
		}

		@Override
		protected void execute() {
			// prepare to store concept enums into servlet context
			final List<Class<?>> enumConceptClasses = new ArrayList<Class<?>>();

			// child care program states
			enumConceptClasses.add(ChildCareProgramStates.class);

			// child care concepts
			enumConceptClasses.add(ChildCareConcepts.class);
			enumConceptClasses.add(MethodOfDeliveryConcepts.class);
			enumConceptClasses.add(NewbornScreeningConcepts.class);
			enumConceptClasses.add(NewbornScreeningResults.class);
			enumConceptClasses.add(NewbornScreeningInformation.class);
			enumConceptClasses.add(ChildProtectedAtBirthStatus.class);

			// vaccination concepts
			enumConceptClasses.add(VaccinationConcepts.class);
			enumConceptClasses.add(ChildCareVaccinesConcepts.class);
			enumConceptClasses.add(ImmunizationStatus.class);

			// child care service concepts
			enumConceptClasses.add(ServiceDueInfoType.class);
			enumConceptClasses.add(ChildCareServicesConcepts.class);
			enumConceptClasses.add(ChildCareServiceTypes.class);
			enumConceptClasses.add(ChildCareServiceSourceConcepts.class);
			enumConceptClasses.add(VitaminAServiceConcepts.class);
			enumConceptClasses.add(DewormingServiceConcepts.class);
			enumConceptClasses.add(FerrousSulfateServiceConcepts.class);

			// breastfeeding concepts
			enumConceptClasses.add(BreastFeedingConcepts.class);

			for (Class<?> conceptEnumClass : enumConceptClasses) {
				// store enum values as attributes in the servlet context
				servletContext.setAttribute(conceptEnumClass.getSimpleName(), ConceptUtil.asMap(conceptEnumClass));
			}

			// store the constants
			servletContext.setAttribute("ChildCareConstants", ConceptUtil.constantsAsMap(ChildCareConstants.class));
		}
	}
}
