package org.openmrs.module.chits.web.controller.fpprogram;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.chits.CHITSPatientModuleActivator;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.OpenSessionIfNeeded;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyInformationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyPlanningMethodConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMedicalHistoryConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPObstetricHistoryConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPObstetricOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPelvicExaminationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPPhysicalExaminationConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPRiskFactorsConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPServiceDeliveryRecordConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FamilyPlanningProgramStates;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Sets up the following startup tasks:
 * <ul>
 * <li>Registers the {@link InitFamilyPlanningVarsTask}
 * </ul>
 * 
 * @author Bren
 */
@Configuration
public class CHITSFamilyPlanningStartupConfiguration {
	/** Logger instance */
	protected final Log log = LogFactory.getLog(getClass());

	public CHITSFamilyPlanningStartupConfiguration() {
		// default constructor
	}

	@Autowired
	public void initFamilyPlanningApplicationVariablesTask(final ServletContext servletContext, final AdministrationService adminService) {
		// add a task to initialize the servlet context attributes when ready
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new InitFamilyPlanningVarsTask(servletContext, adminService));
	}

	/**
	 * Initializes family planning application context variables used throughout the CHITS Family Planning modules.
	 * 
	 * @author Bren
	 */
	class InitFamilyPlanningVarsTask extends OpenSessionIfNeeded {
		/** The servlet context */
		private final ServletContext servletContext;

		InitFamilyPlanningVarsTask(ServletContext servletContext, AdministrationService adminService) {
			super(PrivilegeConstants.VIEW_CONCEPTS);
			this.servletContext = servletContext;
		}

		@Override
		protected void execute() {
			// prepare to store concept enums into servlet context
			final List<Class<?>> enumConceptClasses = new ArrayList<Class<?>>();

			// family program states
			enumConceptClasses.add(FamilyPlanningProgramStates.class);

			// family program concepts
			enumConceptClasses.add(FPFamilyInformationConcepts.class);
			enumConceptClasses.add(FPMedicalHistoryConcepts.class);
			enumConceptClasses.add(FPRiskFactorsConcepts.class);
			enumConceptClasses.add(FPObstetricHistoryConcepts.class);
			enumConceptClasses.add(FPPhysicalExaminationConcepts.class);
			enumConceptClasses.add(FPPelvicExaminationConcepts.class);
			enumConceptClasses.add(FPFamilyPlanningMethodConcepts.class);
			enumConceptClasses.add(FPMethodOptions.class);
			enumConceptClasses.add(FPObstetricOptions.class);
			enumConceptClasses.add(FPServiceDeliveryRecordConcepts.class);

			for (Class<?> conceptEnumClass : enumConceptClasses) {
				// store enum values as attributes in the servlet context
				servletContext.setAttribute(conceptEnumClass.getSimpleName(), ConceptUtil.asMap(conceptEnumClass));
			}

			// store the constants
			servletContext.setAttribute("FamilyPlanningConstants", ConceptUtil.constantsAsMap(FamilyPlanningConstants.class));
		}
	}
}
