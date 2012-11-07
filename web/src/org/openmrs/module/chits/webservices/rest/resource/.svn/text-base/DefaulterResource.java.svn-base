package org.openmrs.module.chits.webservices.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.Defaulter;
import org.openmrs.module.chits.eccdprogram.ChildCareUtil;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Handler for generating resource representation for the Defaulter class.
 * 
 * @author Bren
 */
@Resource("CHITSDefaulterResource")
@Handler(supports = { Defaulter.class }, order = -10)
public class DefaulterResource extends BaseDelegatingResource<Defaulter> {
	/** Auto-wired service */
	private CHITSService chitsService;

	/** The patient service */
	private PatientService patientService;

	@Override
	public Defaulter getByUniqueId(String param) {
		// try loading by UUID
		final Patient patient = getPatientService().getPatientByUuid(param);

		// generate a blank defaulters bean
		final Defaulter defaulter = new Defaulter();
		defaulter.setPatient(patient);

		// load overdue services for this patient
		loadOverdueServices(defaulter);

		// return whatever we got
		return defaulter;
	}

	private void loadOverdueServices(Defaulter defaulter) {
		final Patient patient = defaulter.getPatient();
		if (patient != null && patient.getBirthdate() != null) {
			// if patient is in the ECCD program and the program is not yet closed...
			if (Functions.isInProgram(patient, ProgramConcepts.CHILDCARE)) {
				// calculate overdue services
				ChildCareUtil.addDueServices(defaulter);
			}

			if (Functions.isInProgram(patient, ProgramConcepts.MATERNALCARE)) {
				/* Un-comment to enable downloading of tetanus records:
				// calculate overdue teatnus toxoid services
				MaternalCareUtil.addDueTetanusToxoidServices(defaulter);
				*/

				// if patient is in the MC program and the baby hast not yet been delivered, check overdue prenatal visits...
				if (MaternalCareUtil.isCurrentlyEnrolledAndBabyNotYetDelivered(patient)) {
					// calculate overdue prenatal services
					MaternalCareUtil.addDuePrenatalServices(defaulter);
				} else {
					// calculate overdue postnatal services
					MaternalCareUtil.addDuePostnatalServices(defaulter);
				}
			}
		}
	}

	@Override
	public String getUri(Object delegate) {
		if (!(delegate instanceof Defaulter)) {
			return "";
		}

		final Defaulter defaulter = (Defaulter) delegate;
		Resource res = (Resource) getClass().getAnnotation(Resource.class);
		if (res != null) {
			return RestConstants.URI_PREFIX + "v1" + "/" + res.value() + "/"
					+ (defaulter.getPatient() != null ? defaulter.getPatient().getUuid() : defaulter.toString());
		}

		throw new RuntimeException(getClass() + " needs a @Resource or @SubResource annotation");
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		final DelegatingResourceDescription desc = new DelegatingResourceDescription();

		// these are read-only! (the property setters don't do anything)
		desc.addProperty("patientId");
		desc.addProperty("uuid");
		desc.addProperty("dueServices");

		return desc;
	}

	@PropertyGetter("patientId")
	public static String getPatientId(Defaulter delegate) {
		final Patient patient = (delegate != null) ? delegate.getPatient() : null;
		final PatientIdentifier identifier = (patient != null) ? patient.getPatientIdentifier() : null;
		return (identifier != null) ? identifier.getIdentifier() : "[None]";
	}

	@PropertySetter("patientId")
	public static void setPatientId(Patient delegate, String lpin) {
		// this is a read-only attribute, so this setter doesn't do anything!
	}

	@PropertyGetter("uuid")
	public static String getUUID(Defaulter delegate) {
		final Patient patient = (delegate != null) ? delegate.getPatient() : null;
		return (patient != null) ? patient.getUuid() : null;
	}

	@PropertySetter("uuid")
	public static void setUUID(Patient delegate, String uuid) {
		// this is a read-only attribute, so this setter doesn't do anything!
	}

	@Override
	protected Defaulter newDelegate() {
		throw new UnsupportedOperationException("operation is not supported");
	}

	@Override
	protected void delete(Defaulter defaulter, String reason, RequestContext paramRequestContext) throws ResponseException {
		throw new UnsupportedOperationException("operation is not supported");
	}

	@Override
	public void purge(Defaulter patient, RequestContext paramRequestContext) throws ResponseException {
		throw new UnsupportedOperationException("operation is not supported");
	}

	@Override
	protected Defaulter save(Defaulter defaulter) {
		throw new UnsupportedOperationException("operation is not supported");
	}

	/**
	 * Search all patients that have overdue services for the given barangay code.
	 */
	public NeedsPaging<Defaulter> searchByBarangayCode(String barangayCode, RequestContext context) {
		// search defaulters by barangay code
		final List<Defaulter> barangayDefaulters = new ArrayList<Defaulter>();

		// load all patients
		final List<Patient> patients = getChitsService().getAllPatientsByBarangay(barangayCode, 0L, System.currentTimeMillis());
		for (Patient patient : patients) {
			// Determine if this patient is a defaulter and store the necessary attributes
			final Defaulter defaulter = new Defaulter();
			defaulter.setPatient(patient);

			// load overdue services for this patient
			loadOverdueServices(defaulter);
			if (!defaulter.getDueServices().isEmpty()) {
				// due services for this patient, add to list of the barangay's defaulters
				barangayDefaulters.add(defaulter);
			}
		}

		// send back defaulters for this barangay
		return new NeedsPaging<Defaulter>(barangayDefaulters, context);
	}

	private CHITSService getChitsService() {
		if (this.chitsService == null) {
			this.chitsService = Context.getService(CHITSService.class);
		}

		return this.chitsService;
	}

	private PatientService getPatientService() {
		if (patientService == null) {
			patientService = Context.getPatientService();
		}

		return patientService;
	}
}
