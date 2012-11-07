package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.chits.CHITSPatientModuleActivator;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.OpenSessionIfNeeded;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.DewormingConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.IronSupplementationConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCBirthPlanConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCBloodTypeOptions;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCChildsNeedsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDangerSignsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDeliveryReportConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCFamilyMedicalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCIEOptions;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCIERecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMaternityStage;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMedicalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMenstrualHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMothersNeedsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricExamination;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPatientConsultStatus;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPersonalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumEventsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumVisitRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostpartumIERecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPregnancyOutcomeConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPregnancyTestResultsOptions;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPrenatalVisitRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCReasonForEndingMCProgram;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCRegistrationPage;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCRhesusFactorOptions;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCServiceRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCServiceTypes;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MaternalCareProgramStates;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidDateAdministeredConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidDoseType;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.VitaminAConcepts;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Sets up the following startup tasks:
 * <ul>
 * <li>Registers the {@link InitMaternalCareVarsTask}
 * </ul>
 * 
 * @author Bren
 */
@Configuration
public class CHITSMaternalCareStartupConfiguration {
	/** Logger instance */
	protected final Log log = LogFactory.getLog(getClass());

	public CHITSMaternalCareStartupConfiguration() {
		// default constructor
	}

	@Autowired
	public void initMaternalCareApplicationVariablesTask(final ServletContext servletContext, final AdministrationService adminService) {
		// add a task to initialize the servlet context attributes when ready
		CHITSPatientModuleActivator.getInstance().addContextRefreshedTask(new InitMaternalCareVarsTask(servletContext, adminService));
	}

	/**
	 * Initializes maternal care application context variables used throughout the CHITS Maternal Care modules.
	 * 
	 * @author Bren
	 */
	class InitMaternalCareVarsTask extends OpenSessionIfNeeded {
		/** The servlet context */
		private final ServletContext servletContext;

		InitMaternalCareVarsTask(ServletContext servletContext, AdministrationService adminService) {
			super(PrivilegeConstants.VIEW_CONCEPTS);
			this.servletContext = servletContext;
		}

		@Override
		protected void execute() {
			// prepare to store concept enums into servlet context
			final List<Class<?>> enumConceptClasses = new ArrayList<Class<?>>();

			// maternal care states
			enumConceptClasses.add(MaternalCareProgramStates.class);

			// maternal care concepts
			enumConceptClasses.add(TetanusToxoidDateAdministeredConcepts.class);
			enumConceptClasses.add(TetanusToxoidRecordConcepts.class);
			enumConceptClasses.add(TetanusToxoidDoseType.class);
			enumConceptClasses.add(MCServiceRecordConcepts.class);
			enumConceptClasses.add(MCServiceTypes.class);
			enumConceptClasses.add(VitaminAConcepts.class);
			enumConceptClasses.add(IronSupplementationConcepts.class);
			enumConceptClasses.add(DewormingConcepts.class);
			enumConceptClasses.add(MCObstetricHistoryConcepts.class);
			enumConceptClasses.add(MCObstetricHistoryDetailsConcepts.class);
			enumConceptClasses.add(MCPregnancyOutcomeConcepts.class);
			enumConceptClasses.add(MCMenstrualHistoryConcepts.class);
			enumConceptClasses.add(MCMedicalHistoryConcepts.class);
			enumConceptClasses.add(MCFamilyMedicalHistoryConcepts.class);
			enumConceptClasses.add(MCPersonalHistoryConcepts.class);
			enumConceptClasses.add(MCDangerSignsConcepts.class);
			enumConceptClasses.add(MCPregnancyTestResultsOptions.class);
			enumConceptClasses.add(MCBloodTypeOptions.class);
			enumConceptClasses.add(MCRhesusFactorOptions.class);
			enumConceptClasses.add(MCObstetricExamination.class);
			enumConceptClasses.add(MCPrenatalVisitRecordConcepts.class);
			enumConceptClasses.add(MCIERecordConcepts.class);
			enumConceptClasses.add(MCIEOptions.class);
			enumConceptClasses.add(MCBirthPlanConcepts.class);
			enumConceptClasses.add(MCMothersNeedsConcepts.class);
			enumConceptClasses.add(MCChildsNeedsConcepts.class);
			enumConceptClasses.add(MCDeliveryReportConcepts.class);
			enumConceptClasses.add(MCMaternityStage.class);
			enumConceptClasses.add(MCPostPartumVisitRecordConcepts.class);
			enumConceptClasses.add(MCPostPartumVisitRecordConcepts.class);
			enumConceptClasses.add(MCPostPartumEventsConcepts.class);
			enumConceptClasses.add(MCPatientConsultStatus.class);
			enumConceptClasses.add(MCReasonForEndingMCProgram.class);
			enumConceptClasses.add(MCPostpartumIERecordConcepts.class);

			// other enums
			enumConceptClasses.add(MCRegistrationPage.class);

			for (Class<?> conceptEnumClass : enumConceptClasses) {
				// store enum values as attributes in the servlet context
				servletContext.setAttribute(conceptEnumClass.getSimpleName(), ConceptUtil.asMap(conceptEnumClass));
			}

			// store the constants
			servletContext.setAttribute("MaternalCareConstants", ConceptUtil.constantsAsMap(MaternalCareConstants.class));
		}
	}
}
