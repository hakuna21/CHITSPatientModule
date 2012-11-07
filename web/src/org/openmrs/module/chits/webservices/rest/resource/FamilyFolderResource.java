package org.openmrs.module.chits.webservices.rest.resource;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.Util;
import org.openmrs.module.chits.webservices.rest.VersionConflictException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource("familyfolder")
@Handler(supports = { FamilyFolder.class }, order = 0)
public class FamilyFolderResource extends DataDelegatingCrudResource<FamilyFolder> {
	/** Auto-wired service */
	private CHITSService chitsService;

	@Override
	protected void delete(FamilyFolder folder, String reason, RequestContext paramRequestContext) throws ResponseException {
		// void the family folder
		getChitsService().voidFamilyFolder(folder, reason);
	}

	@Override
	public FamilyFolder getByUniqueId(String param) {
		// try loading by UUID
		FamilyFolder result = getChitsService().getFamilyFolderByUuid(param);
		if (result == null) {
			// if not found, try loading by family folder code
			result = getChitsService().getFamilyFolderByCode(param);
		}

		// return whatever we got
		return result;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		final DelegatingResourceDescription desc = new DelegatingResourceDescription();

		// these are read-only! (the property setters don't do anything)
		desc.addProperty("uuid");
		desc.addProperty("code");
		desc.addProperty("dateChanged");

		// add regular properties
		desc.addProperty("name");
		desc.addProperty("address");
		desc.addProperty("barangayCode");
		desc.addProperty("cityCode");
		desc.addProperty("notes");
		desc.addProperty("headOfTheFamily");
		desc.addProperty("patients");

		return desc;
	}

	@PropertyGetter("uuid")
	public static String getUUID(FamilyFolder delegate) {
		return delegate != null ? delegate.getUuid() : null;
	}

	@PropertySetter("uuid")
	public static void setUUID(FamilyFolder delegate, String uuid) throws ParseException {
		// this is a read-only attribute, so this setter doesn't do anything!
	}

	@PropertyGetter("code")
	public static String getCode(FamilyFolder delegate) {
		return delegate != null ? delegate.getCode() : null;
	}

	@PropertySetter("code")
	public static void setCode(FamilyFolder delegate, String code) throws ParseException {
		// this is a read-only attribute, so this setter doesn't do anything!
	}

	/**
	 * Returns the uuid of the head of the family.
	 * 
	 * @param delegate
	 *            The delegate
	 * @return The head of the family uuid.
	 */
	@PropertyGetter("headOfTheFamily")
	public static String getHeadOfTheFamilyUUID(FamilyFolder delegate) {
		return delegate != null && delegate.getHeadOfTheFamily() != null ? delegate.getHeadOfTheFamily().getUuid() : null;
	}

	/**
	 * Sets the head of the family of the patient with the given uuid into the delegate.
	 * 
	 * @param delegate
	 *            The delegate
	 * @param uuid
	 *            The UUID of the patient to set as the head of the family
	 */
	@PropertySetter("headOfTheFamily")
	public static void setHeadOfTheFamilyUUID(FamilyFolder delegate, String uuid) {
		if (uuid != null && !"".equals(uuid)) {
			delegate.setHeadOfTheFamily(Context.getPatientService().getPatientByUuid(uuid));
		} else {
			delegate.setHeadOfTheFamily(null);
		}
	}

	/**
	 * Returns the uuids of the patient members of the family.
	 * 
	 * @param delegate
	 *            The delegate
	 * @return The UUIDs of the patient members of the family.
	 */
	@PropertyGetter("patients")
	public static List<String> getPatientUUIDs(FamilyFolder delegate) {
		final List<String> patientUuids = new ArrayList<String>();
		for (Patient patient : delegate.getPatients()) {
			patientUuids.add(patient.getUuid());
		}

		return patientUuids;
	}

	/**
	 * Sets the members of the family given the uuids of the patients.
	 * 
	 * @param delegate
	 *            The delegate
	 * @param patientUuids
	 *            The UUIDs of the patients to add to the family folder
	 */
	@PropertySetter("patients")
	public static void setPatientUUIDs(FamilyFolder delegate, List<String> patientUuids) {
		// get the new set of patients to make members of the folder
		final PatientService patientService = Context.getPatientService();
		final List<Patient> members = new ArrayList<Patient>();
		for (String uuid : patientUuids) {
			members.add(patientService.getPatientByUuid(uuid));
		}

		// remove the patient's that are no longer members
		delegate.getPatients().retainAll(members);

		// add the new patients
		delegate.getPatients().addAll(members);
	}

	@PropertyGetter("dateChanged")
	public static Long getDateChanged(FamilyFolder delegate) {
		if (delegate.getDateChanged() != null) {
			return Long.valueOf(delegate.getDateChanged().getTime());
		} else if (delegate.getDateCreated() != null) {
			return Long.valueOf(delegate.getDateCreated().getTime());
		} else {
			return null;
		}
	}

	@PropertySetter("dateChanged")
	public static void setDateChanged(FamilyFolder delegate, Number dateChanged) {
		if (delegate.getId() != null && delegate.getId() != 0) {
			final Date changed = delegate.getDateChanged() != null ? delegate.getDateChanged() : delegate.getDateCreated();

			// sync operation is uploading information to this record: the 'dateChanged' value represents
			// the version on the unit since it last synced with the server; if it is less than the
			// current version of the delegate, then the unit cannot update this record because it is out of date!
			if (dateChanged == null || dateChanged.longValue() < changed.getTime()) {
				throw new VersionConflictException("Version conflict");
			}
		}
	}

	@Override
	protected FamilyFolder newDelegate() {
		return new FamilyFolder();
	}

	@Override
	public void purge(FamilyFolder folder, RequestContext paramRequestContext) throws ResponseException {
		if (folder == null) {
			// nothing to do
			return;
		}

		getChitsService().purgeFamilyFolder(folder);
	}

	@Override
	protected FamilyFolder save(FamilyFolder folder) {
		if (folder.getId() == null || folder.getId() == 0) {
			// perform initial save to store a primary key
			folder = getChitsService().saveFamilyFolder(folder);

			// update the 'code' based on the family code format
			folder.setCode(Util.formatFolderCode(Context.getAdministrationService(), folder.getId()));
		}

		// return saved instance
		return getChitsService().saveFamilyFolder(folder);
	}

	/**
	 * Overridden to make sure that the version is specified in the 'dateChanged' attribute.
	 */
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		if (!propertiesToUpdate.containsKey("dateChanged")) {
			throw new VersionConflictException("Version not specified in 'dateChanged' property.");
		}

		// dispatch to superclass
		return super.update(uuid, propertiesToUpdate, context);
	}

	public String getDisplayString(FamilyFolder folder) {
		return folder.getCode() + " - " + folder.getName();
	}

	@Override
	protected List<FamilyFolder> doGetAll(RequestContext context) throws ResponseException {
		return getChitsService().getAllFamilyFolders();
	}

	@Override
	protected NeedsPaging<FamilyFolder> doSearch(String query, RequestContext context) {
		final List<FamilyFolder> folders = getChitsService().getAllFamilyFoldersLike(query);
		return new NeedsPaging<FamilyFolder>(folders, context);
	}

	/**
	 * NOTE: This query has the potential of missing phantom records that haven't yet been committed into the database. To alleviate the possibility of missing
	 * the phantom records, subsequent synchronize operations should subtract 1 hour (for example) from the 'modifiedSince' value to pickup phantom records; the
	 * down side is that this may re-synchronize records on the mobile unit that are already up-to-date.
	 */
	public NeedsPaging<FamilyFolder> searchByBarangayCode(String barangayCode, Long modifiedSince, Long modifiedUpto, RequestContext context) {
		final List<FamilyFolder> folders = getChitsService().getAllFamilyFoldersByBarangay(barangayCode, modifiedSince, modifiedUpto);
		return new NeedsPaging<FamilyFolder>(folders, context);
	}

	private CHITSService getChitsService() {
		if (this.chitsService == null) {
			this.chitsService = Context.getService(CHITSService.class);
		}

		return this.chitsService;
	}
}
