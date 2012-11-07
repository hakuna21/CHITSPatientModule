package org.openmrs.module.chits.web.startup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.openmrs.api.AdministrationService;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.chits.ConceptUtil;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.Constants.AuditConcepts;
import org.openmrs.module.chits.Constants.BooleanConcepts;
import org.openmrs.module.chits.Constants.CivilStatusConcepts;
import org.openmrs.module.chits.Constants.EducationConcepts;
import org.openmrs.module.chits.Constants.HealthFacilityConcepts;
import org.openmrs.module.chits.Constants.OccupationConcepts;
import org.openmrs.module.chits.Constants.PhilhealthSponsorConcepts;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.Constants.StatusConcepts;
import org.openmrs.module.chits.Constants.VisitConcepts;
import org.openmrs.module.chits.Constants.VisitNotesConceptSets;
import org.openmrs.module.chits.OpenSessionIfNeeded;
import org.openmrs.module.chits.impl.StaticBarangayCodesHolder;
import org.openmrs.module.chits.web.GlobalPropertyMap;
import org.openmrs.util.PrivilegeConstants;

/**
 * Initializes application context variables used throughout the CHITS modules.
 * 
 * @author Bren
 */
class InitApplicationVariables extends OpenSessionIfNeeded {
	/** Barangay codes holder */
	protected final StaticBarangayCodesHolder barangayCodesHolder = StaticBarangayCodesHolder.getInstance();

	/** The servlet context */
	private final ServletContext servletContext;

	/** The administration service for reference */
	private final AdministrationService adminService;

	InitApplicationVariables(ServletContext servletContext, AdministrationService adminService) {
		super(PrivilegeConstants.VIEW_CONCEPTS);
		this.servletContext = servletContext;
		this.adminService = adminService;
	}

	@Override
	protected void execute() {
		// prepare to store concept enums into servlet context
		final List<Class<?>> enumConceptClasses = new ArrayList<Class<?>>();
		enumConceptClasses.add(PhilhealthSponsorConcepts.class);
		enumConceptClasses.add(CivilStatusConcepts.class);
		enumConceptClasses.add(VisitConcepts.class);
		enumConceptClasses.add(VisitNotesConceptSets.class);

		// program concepts
		enumConceptClasses.add(ProgramConcepts.class);

		// miscellaneous concepts
		enumConceptClasses.add(AuditConcepts.class);
		enumConceptClasses.add(BooleanConcepts.class);
		enumConceptClasses.add(HealthFacilityConcepts.class);
		enumConceptClasses.add(OccupationConcepts.class);
		enumConceptClasses.add(EducationConcepts.class);
		enumConceptClasses.add(StatusConcepts.class);

		for (Class<?> conceptEnumClass : enumConceptClasses) {
			// store enum values as attributes in the servlet context
			servletContext.setAttribute(conceptEnumClass.getSimpleName(), ConceptUtil.asMap(conceptEnumClass));
		}

		// add the 'Constants' to the application scope for convenience
		servletContext.setAttribute("Constants", ConceptUtil.constantsAsMap(Constants.class));
		servletContext.setAttribute("PhilhealthConcepts", ConceptUtil.constantsAsMap(Constants.PhilhealthConcepts.class));
		servletContext.setAttribute("IdAttributes", ConceptUtil.constantsAsMap(Constants.IdAttributes.class));
		servletContext.setAttribute("AddressAttributes", ConceptUtil.constantsAsMap(Constants.AddressAttributes.class));
		servletContext.setAttribute("PhoneAttributes", ConceptUtil.constantsAsMap(Constants.PhoneAttributes.class));
		servletContext.setAttribute("MiscAttributes", ConceptUtil.constantsAsMap(Constants.MiscAttributes.class));
		servletContext.setAttribute("ICD10", ConceptUtil.constantsAsMap(Constants.ICD10.class));

		// store barangays
		servletContext.setAttribute("barangays", barangayCodesHolder.barangays);

		// store municipalities
		servletContext.setAttribute("municipalities", barangayCodesHolder.municipalities);

		// store the global property map
		servletContext.setAttribute("GlobalProperty", new GlobalPropertyMap(adminService));

		// extract chits version from the module
		final Module chitsModule = ModuleFactory.getModuleById("chits");
		final String chitsVersion = chitsModule != null ? chitsModule.getVersion() : "x.x.x";

		// store the deployment timestamp (useful for tagging resources with a "?v=${deploymentTimestamp}" to force reload of cached resources when
		// a new deployment is performed)
		final File deploymentPath = new File(servletContext.getRealPath("/WEB-INF/view/module/chits"));
		servletContext.setAttribute(Constants.DEPLOYMENT_TIMESTAMP_ATTR, chitsVersion + "_" + Long.toString(deploymentPath.lastModified()));
	}
}