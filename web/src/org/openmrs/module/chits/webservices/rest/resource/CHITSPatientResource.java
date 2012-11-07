package org.openmrs.module.chits.webservices.rest.resource;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CHITSPatientSearchService;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.validator.PatientValidator;
import org.openmrs.module.chits.web.controller.AddPatientController;
import org.openmrs.module.chits.webservices.rest.InvalidDateException;
import org.openmrs.module.chits.webservices.rest.MotherCannotBeDescendantException;
import org.openmrs.module.chits.webservices.rest.MotherCannotBeSelfException;
import org.openmrs.module.chits.webservices.rest.MotherNotFemaleException;
import org.openmrs.module.chits.webservices.rest.MotherNotFoundException;
import org.openmrs.module.chits.webservices.rest.NoSuchConceptAnswerException;
import org.openmrs.module.chits.webservices.rest.NoSuchConceptAttributeException;
import org.openmrs.module.chits.webservices.rest.NoSuchConceptNameException;
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

@Resource("chitspatient")
@Handler(supports = { Patient.class }, order = -10)
public class CHITSPatientResource extends DataDelegatingCrudResource<Patient> implements Constants {
	/** format for the LPIN */
	private static final DecimalFormat LPIN_FMT = new DecimalFormat("000000");

	/** Cached service */
	private CHITSService chitsService;

	/** Cached service */
	private CHITSPatientSearchService chitsPatientSearchService;

	/** The patient service */
	private PatientService patientService;

	/** The person service */
	private PersonService personService;

	/** Stores properties set by the current thread for later processing */
	private static ThreadLocal<PropertyData> properties = new ThreadLocal<PropertyData>();

	@Override
	protected void delete(Patient patient, String reason, RequestContext paramRequestContext) throws ResponseException {
		// void the family patient
		getPatientService().voidPatient(patient, reason);
	}

	@Override
	public Patient getByUniqueId(String param) {
		// try loading by UUID
		final Patient result = getPatientService().getPatientByUuid(param);

		// reset the property data in preparation for the property setters
		properties.set(new PropertyData());

		// return whatever we got
		return result;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		final DelegatingResourceDescription desc = new DelegatingResourceDescription();

		// these are read-only! (the property setters don't do anything)
		desc.addProperty("patientId");
		desc.addProperty("lpin");
		desc.addProperty("uuid");
		desc.addProperty("dateChanged");

		// add regular properties
		desc.addProperty("givenName");
		desc.addProperty("middleName");
		desc.addProperty("familyName");
		desc.addProperty("familyNameSuffix");
		desc.addProperty("gender");
		desc.addProperty("birthdate");
		desc.addProperty("civilStatus");
		desc.addProperty("motherUUID");
		desc.addProperty("crn");
		desc.addProperty("tin");
		desc.addProperty("sss");
		desc.addProperty("gsis");
		desc.addProperty("philhealth");
		desc.addProperty("philhealthExp");
		desc.addProperty("philhealthSponsor");
		desc.addProperty("createdOn");
		desc.addProperty("lastModifiedFrom");

		// family information
		desc.addProperty("familyUUID");
		desc.addProperty("headOfTheFamily");

		return desc;
	}

	/**
	 * Returns the uuid of the mother
	 * 
	 * @param delegate
	 *            The delegate
	 * @return The uuid of the patient's mother
	 */
	@PropertyGetter("motherUUID")
	public static String getPatientMotherUUID(Patient delegate) {
		final CHITSPatientSearchService chitsPatientSearchService = Context.getService(CHITSPatientSearchService.class);
		final Relationship femaleParent = chitsPatientSearchService.getFemaleParent(delegate);
		return femaleParent != null ? femaleParent.getPersonA().getUuid() : null;
	}

	/**
	 * Sets the mother of the patient (female parent).
	 * 
	 * @param delegate
	 *            The delegate
	 * @param uuid
	 *            The UUID of the patient to set as the head of the family
	 */
	@PropertySetter("motherUUID")
	public static void setPatientMotherUUID(Patient delegate, String uuid) {
		// store this in thread local storage for processing during save()
		properties.get().motherUUID = uuid;
	}

	/**
	 * Returns the uuid of the family folder that this patient belongs to or null if the patient doesn't belong to any family.
	 * 
	 * @param delegate
	 *            The delegate
	 * @return The uuid of the family folder that this patient belongs to
	 */
	@PropertyGetter("familyUUID")
	public static String getPatientFamilyUUID(Patient delegate) {
		final CHITSService chitsService = Context.getService(CHITSService.class);
		final List<FamilyFolder> folders = chitsService.getFamilyFoldersOf(delegate.getPatientId());
		if (folders.isEmpty()) {
			return null;
		} else {
			return folders.get(0).getUuid();
		}
	}

	/**
	 * Stores the UUID of the family folder that this patient should be tagged to.
	 * 
	 * @param delegate
	 *            The delegate
	 * @param uuid
	 *            The UUID of the family folder to tag this patient to.
	 */
	@PropertySetter("familyUUID")
	public static void setPatientFamilyUUID(Patient delegate, String uuid) {
		// store this in thread local storage for processing during save()
		properties.get().familyUUID = uuid;
	}

	/**
	 * Returns whether the patient is the head of the family in the folder it belongs to.
	 * 
	 * @param delegate
	 *            The delegate
	 * @return Wether the patient is the head of the family in the folderi t belongs to
	 */
	@PropertyGetter("headOfTheFamily")
	public static Boolean isPatientHeadOfTheFamily(Patient delegate) {
		final CHITSService chitsService = Context.getService(CHITSService.class);
		final List<FamilyFolder> folders = chitsService.getFamilyFoldersOf(delegate.getPatientId());
		if (folders.isEmpty()) {
			// can't be head of the family if you don't belong to a family!
			return false;
		} else {
			// head if set in the folder as the head
			return delegate.equals(folders.get(0).getHeadOfTheFamily());
		}
	}

	/**
	 * Stores the head of the family flag which indicates if this patient is the head of the family of the family to which it belongs.
	 * 
	 * @param delegate
	 *            The delegate
	 * @param headOfTheFamily
	 *            Flag indicating if this patient is the head of the family of the folder to which it belongs
	 */
	@PropertySetter("headOfTheFamily")
	public static void setPatientHeadOfTheFamily(Patient delegate, Boolean headOfTheFamily) {
		// store this in thread local storage for processing during save()
		properties.get().headOfTheFamily = headOfTheFamily != null ? headOfTheFamily.booleanValue() : false;
	}

	@PropertyGetter("dateChanged")
	public static Long getDateChanged(Patient delegate) {
		if (delegate.getDateChanged() != null) {
			return Long.valueOf(delegate.getDateChanged().getTime());
		} else if (delegate.getDateCreated() != null) {
			return Long.valueOf(delegate.getDateCreated().getTime());
		} else {
			return null;
		}
	}

	@PropertySetter("dateChanged")
	public static void setDateChanged(Patient delegate, Number dateChanged) {
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

	@PropertyGetter("birthdate")
	public static String getBirthdate(Patient delegate) {
		return delegate.getBirthdate() != null ? Context.getDateFormat().format(delegate.getBirthdate()) : null;
	}

	@PropertySetter("birthdate")
	public static void setBirthdate(Patient delegate, String birthdate) {
		try {
			delegate.setBirthdate(birthdate != null && !"".equals(birthdate.trim()) ? Context.getDateFormat().parse(birthdate.trim()) : null);
		} catch (ParseException pe) {
			throw new InvalidDateException(birthdate);
		}
	}

	@PropertyGetter("civilStatus")
	public static String getCivilStatus(Patient delegate) {
		return getPersonAttributeConceptAnswer(delegate, CivilStatusConcepts.CIVIL_STATUS.getConceptName());
	}

	@PropertySetter("civilStatus")
	public static void setCivilStatus(Patient delegate, String civilStatus) {
		setPersonAttributeConceptAnswer(delegate, CivilStatusConcepts.CIVIL_STATUS.getConceptName(), civilStatus);
	}

	@PropertyGetter("crn")
	public static String getCrn(Patient delegate) {
		return getPersonAttribute(delegate, IdAttributes.CHITS_CRN);
	}

	@PropertySetter("crn")
	public static void setCrn(Patient delegate, String crn) {
		setPersonAttribute(delegate, IdAttributes.CHITS_CRN, crn);
	}

	@PropertyGetter("tin")
	public static String getTin(Patient delegate) {
		return getPersonAttribute(delegate, IdAttributes.CHITS_TIN);
	}

	@PropertySetter("tin")
	public static void setTin(Patient delegate, String tin) {
		setPersonAttribute(delegate, IdAttributes.CHITS_TIN, tin);
	}

	@PropertyGetter("sss")
	public static String getSss(Patient delegate) {
		return getPersonAttribute(delegate, IdAttributes.CHITS_SSS);
	}

	@PropertySetter("sss")
	public static void setSss(Patient delegate, String sss) {
		setPersonAttribute(delegate, IdAttributes.CHITS_SSS, sss);
	}

	@PropertyGetter("gsis")
	public static String getGsis(Patient delegate) {
		return getPersonAttribute(delegate, IdAttributes.CHITS_GSIS);
	}

	@PropertySetter("gsis")
	public static void setGsis(Patient delegate, String gsis) {
		setPersonAttribute(delegate, IdAttributes.CHITS_GSIS, gsis);
	}

	@PropertyGetter("philhealth")
	public static String getPhilhealth(Patient delegate) {
		return getPersonAttribute(delegate, PhilhealthConcepts.CHITS_PHILHEALTH);
	}

	@PropertySetter("philhealth")
	public static void setPhilhealth(Patient delegate, String philhealth) {
		setPersonAttribute(delegate, PhilhealthConcepts.CHITS_PHILHEALTH, philhealth);
	}

	@PropertyGetter("philhealthExp")
	public static String getPhilhealthExp(Patient delegate) {
		return getPersonAttribute(delegate, PhilhealthConcepts.CHITS_PHILHEALTH_EXPIRATION);
	}

	@PropertySetter("philhealthExp")
	public static void setPhilhealthExp(Patient delegate, String philhealthExp) {
		setPersonAttribute(delegate, PhilhealthConcepts.CHITS_PHILHEALTH_EXPIRATION, philhealthExp);
	}

	@PropertyGetter("philhealthSponsor")
	public static String getPhilhealthSponsor(Patient delegate) {
		return getPersonAttributeConceptAnswer(delegate, PhilhealthSponsorConcepts.CHITS_PHILHEALTH_SPONSOR.getConceptName());
	}

	@PropertySetter("philhealthSponsor")
	public static void setPhilhealthSponsor(Patient delegate, String philhealthSponsor) {
		setPersonAttributeConceptAnswer(delegate, PhilhealthSponsorConcepts.CHITS_PHILHEALTH_SPONSOR.getConceptName(), philhealthSponsor);
	}

	@PropertyGetter("createdOn")
	public static String getCreatedOn(Patient delegate) {
		return getPersonAttribute(delegate, MiscAttributes.CREATED_ON);
	}

	@PropertySetter("createdOn")
	public static void setCreatedOn(Patient delegate, String createdOn) {
		setPersonAttribute(delegate, MiscAttributes.CREATED_ON, createdOn);
	}

	@PropertyGetter("lastModifiedFrom")
	public static String getLastModifiedFrom(Patient delegate) {
		return getPersonAttribute(delegate, MiscAttributes.LAST_MODIFIED_ON);
	}

	@PropertySetter("lastModifiedFrom")
	public static void setLastModifiedFrom(Patient delegate, String lastModifiedFrom) {
		setPersonAttribute(delegate, MiscAttributes.LAST_MODIFIED_ON, lastModifiedFrom);
	}

	@PropertyGetter("uuid")
	public static String getUUID(Patient delegate) {
		return delegate != null ? delegate.getUuid() : null;
	}

	@PropertySetter("uuid")
	public static void setUUID(Patient delegate, String uuid) {
		// this is a read-only attribute, so this setter doesn't do anything!
	}

	@PropertyGetter("lpin")
	public static String getLpin(Patient delegate) {
		return LPIN_FMT.format(delegate.getId() != null ? delegate.getId() : 0);
	}

	@PropertySetter("lpin")
	public static void setLpin(Patient delegate, String lpin) {
		// this is a read-only attribute, so this setter doesn't do anything!
	}

	@PropertyGetter("gender")
	public static String getGender(Patient delegate) {
		return delegate != null ? delegate.getGender() : null;
	}

	@PropertySetter("gender")
	public static void setGender(Patient delegate, String gender) {
		delegate.setGender(gender);
	}

	@PropertyGetter("patientId")
	public static String getPatientId(Patient delegate) {
		return delegate.getPatientIdentifier() != null ? delegate.getPatientIdentifier().getIdentifier() : "[None]";
	}

	@PropertySetter("patientId")
	public static void setPatientId(Patient delegate, String lpin) {
		// this is a read-only attribute, so this setter doesn't do anything!
	}

	@PropertyGetter("givenName")
	public static String getGivenName(Patient delegate) {
		return delegate.getPersonName() != null ? delegate.getPersonName().getGivenName() : null;
	}

	@PropertyGetter("middleName")
	public static String getMiddleName(Patient delegate) {
		return delegate.getPersonName() != null ? delegate.getPersonName().getMiddleName() : null;
	}

	@PropertyGetter("familyName")
	public static String getFamilyName(Patient delegate) {
		return delegate.getPersonName() != null ? delegate.getPersonName().getFamilyName() : null;
	}

	@PropertyGetter("familyNameSuffix")
	public static String getFamilynameSuffix(Patient delegate) {
		return delegate.getPersonName() != null ? delegate.getPersonName().getFamilyNameSuffix() : null;
	}

	@PropertySetter("givenName")
	public static void setGivenName(Patient delegate, String value) {
		getOrCreatePersonName(delegate).setGivenName(value);
	}

	@PropertySetter("middleName")
	public static void setMiddleName(Patient delegate, String value) {
		getOrCreatePersonName(delegate).setMiddleName(value);
	}

	@PropertySetter("familyName")
	public static void setFamilyName(Patient delegate, String value) {
		getOrCreatePersonName(delegate).setFamilyName(value);
	}

	@PropertySetter("familyNameSuffix")
	public static void setFamilynameSuffix(Patient delegate, String value) {
		getOrCreatePersonName(delegate).setFamilyNameSuffix(value);
	}

	private static PersonName getOrCreatePersonName(Patient delegate) {
		final PersonName personName;
		if (delegate.getPersonName() == null) {
			personName = RelationshipUtil.newBlankPersonName();
			delegate.addName(personName);
		} else {
			personName = delegate.getPersonName();
		}

		return personName;
	}

	/**
	 * Convenience method to get a person attribute concept answer and lookup the corresponding 'name'.
	 * 
	 * @param delegate
	 * @param attribute
	 * @return
	 */
	private static String getPersonAttributeConceptAnswer(Person delegate, String attributeName) {
		final ConceptService conceptService = Context.getConceptService();
		final PersonAttribute attribute = delegate.getAttributeMap().get(attributeName);
		final Concept answer = attribute != null ? conceptService.getConcept(attribute.getValue()) : null;
		return answer != null ? answer.getName().toString() : null;
	}

	/**
	 * Convenience method to get a person attribute concept answer and lookup the corresponding 'name'.
	 * 
	 * @param delegate
	 * @param attribute
	 * @return
	 */
	private static void setPersonAttributeConceptAnswer(Person delegate, String attributeName, String conceptAnswer) {
		// lookup the attribute type
		final PersonAttributeType attribType = Context.getPersonService().getPersonAttributeTypeByName(attributeName);
		if (attribType == null) {
			throw new NoSuchConceptNameException("No such concept attribute: " + attributeName);
		}

		if (conceptAnswer == null || "".equals(conceptAnswer.trim())) {
			// remove the attribute from the person
			final PersonAttribute currentAttrib = delegate.getAttribute(attribType);
			if (currentAttrib != null) {
				delegate.removeAttribute(currentAttrib);
			}
		} else {
			// lookup the concept answer
			final Concept answer = Context.getConceptService().getConcept(conceptAnswer);
			if (answer == null) {
				throw new NoSuchConceptAnswerException("Invalid attribute concept answer: " + conceptAnswer);
			}

			// store the answer
			delegate.addAttribute(new PersonAttribute(attribType, answer.getId().toString()));
		}
	}

	/**
	 * Convenience method to get a person attribute concept answer and lookup the corresponding 'name'.
	 * 
	 * @param delegate
	 * @param attribute
	 * @return
	 */
	private static String getPersonAttribute(Person delegate, String attributeName) {
		final PersonAttribute attribute = delegate.getAttributeMap().get(attributeName);
		return attribute != null ? attribute.getValue() : null;
	}

	/**
	 * Convenience method to get a person attribute concept answer and lookup the corresponding 'name'.
	 * 
	 * @param delegate
	 * @param attribute
	 * @return
	 */
	private static void setPersonAttribute(Person delegate, String attributeName, String value) {
		// lookup the attribute type
		final PersonAttributeType attribType = Context.getPersonService().getPersonAttributeTypeByName(attributeName);
		if (attribType == null) {
			throw new NoSuchConceptAttributeException("No such concept attribute: " + attributeName);
		}

		PersonAttribute currentAttrib = delegate.getAttribute(attribType);
		if (value == null || "".equals(value.trim())) {
			// remove the attribute from the person
			if (currentAttrib != null) {
				delegate.removeAttribute(currentAttrib);
			}
		} else {
			if (currentAttrib != null) {
				// update the person's attribute value
				currentAttrib.setValue(value);
			} else {
				// add the new attribute for this patient
				delegate.addAttribute(new PersonAttribute(attribType, value));
			}
		}
	}

	@Override
	protected Patient newDelegate() {
		// create a new blank patient
		final Patient newPatient = RelationshipUtil.newBlankPatient();

		// ensure the identifier is unique for new patients (though this will need to be changed when the patient gets created)
		newPatient.getPatientIdentifier().setIdentifier(UUID.randomUUID().toString());

		// reset the property data in preparation for the property setters
		properties.set(new PropertyData());

		// ready for creation
		return newPatient;
	}

	@Override
	public void purge(Patient patient, RequestContext paramRequestContext) throws ResponseException {
		if (patient == null) {
			// nothing to do
			return;
		}

		// purge any relationships involving this person to be deleted
		final List<Relationship> relations = getPersonService().getRelationshipsByPerson(patient);
		for (Relationship relation : relations) {
			getPersonService().purgeRelationship(relation);
		}

		// now we can purge the patient!
		getPatientService().purgePatient(patient);
	}

	@Override
	protected Patient save(Patient patient) {
		boolean newPatient = patient.getId() == null || patient.getId() == 0;

		// return saved instance
		patient = getPatientService().savePatient(patient);
		if (newPatient) {
			AddPatientController.formatAndSavePatientIdentifier(patient);
		}

		// set the 'mother' relationship: find current patient's mother
		savePatientMother(patient);

		// save patient's family folder tags
		savePatientFolderTags(patient);

		return patient;
	}

	/**
	 * Sets the patient's mother from the saved properties.
	 * <p>
	 * This method must always be called on a saved patient (i.e., this cannot be called on a patient that hasn't been saved in hibernate yet)
	 * 
	 * @param patient
	 *            The patient to set the mother of.
	 */
	private void savePatientMother(Patient patient) {
		final CHITSPatientSearchService chitsPatientSearchService = getChitsPatientSearchService();
		final PersonService personService = getPersonService();
		Relationship femaleParent = chitsPatientSearchService.getFemaleParent(patient);

		final String uuid = properties.get().motherUUID;
		if (uuid != null && !"".equals(uuid)) {
			final Person mother = personService.getPersonByUuid(uuid);
			if (mother == null) {
				throw new MotherNotFoundException("Person (mother) with uuid not found: " + uuid);
			} else if (!"F".equals(mother.getGender())) {
				throw new MotherNotFemaleException("Patient's mother must be female: " + uuid);
			} else if (mother.equals(patient)) {
				// cannot specify a descendant as one's mother
				throw new MotherCannotBeSelfException("Patient's mother cannot be herself: " + uuid);
			} else if (PatientValidator.isDescendantOf(mother, patient)) {
				// cannot specify a descendant as one's mother
				throw new MotherCannotBeDescendantException("Patient's mother cannot be a descendant of the patient: " + uuid);
			}

			if (femaleParent != null) {
				// change the patient's mother
				femaleParent.setPersonA(mother);
			} else {
				// create a relationship entry for this patient's mother
				// create a new parent for this patient
				final RelationshipType parentRelType = personService.getRelationshipTypeByName(Constants.PARENT_RELATIONSHIP_NAME);
				femaleParent = new Relationship();
				femaleParent.setPersonA(mother);
				femaleParent.setPersonB(patient);
				femaleParent.setRelationshipType(parentRelType);
				femaleParent.setUuid(UUID.randomUUID().toString());
				femaleParent.setCreator(Context.getAuthenticatedUser());
				femaleParent.setDateCreated(new Date());
			}

			// update the patient / mother relationship
			personService.saveRelationship(femaleParent);
		} else {
			// un-relate the patient's mother
			if (femaleParent != null) {
				personService.purgeRelationship(femaleParent);
			}
		}
	}

	/**
	 * Sets the patient's tagging to a folder.
	 * <p>
	 * This method must always be called on a saved patient (i.e., this cannot be called on a patient that hasn't been saved in hibernate yet)
	 * 
	 * @param patient
	 *            The patient to set the folder of.
	 */
	private void savePatientFolderTags(Patient patient) {
		final CHITSService chitsService = getChitsService();

		final PropertyData data = properties.get();
		final String familyUUID = data.familyUUID;
		if (familyUUID == null || "".equals(familyUUID.trim())) {
			// remove this patient from all folders
			final List<FamilyFolder> folders = chitsService.getFamilyFoldersOf(patient.getPatientId());

			// this patient should not belong to any folders
			for (FamilyFolder folder : folders) {
				// remove patient from folder!
				folder.getPatients().remove(patient);
				if (patient.equals(folder.getHeadOfTheFamily())) {
					// if patient is not a member of the family, then he / she cannot be the head of the family either
					folder.setHeadOfTheFamily(null);
				}

				// save the folder
				chitsService.saveFamilyFolder(folder);
			}
		} else {
			// find corresponding folder
			final FamilyFolder folder = chitsService.getFamilyFolderByUuid(familyUUID);
			if (folder != null) {
				// add this patient to the folder
				folder.getPatients().add(patient);

				if (data.headOfTheFamily) {
					// set patient as the head of the family
					folder.setHeadOfTheFamily(patient);
				} else if (patient.equals(folder.getHeadOfTheFamily())) {
					// patient should not be the head of the family
					folder.setHeadOfTheFamily(null);
				}

				// save the folder
				chitsService.saveFamilyFolder(folder);
			}
		}
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

	public String getDisplayString(Patient patient) {
		return patient != null ? (patient.getPersonName() != null ? patient.getPersonName().toString() : patient.toString()) : "null";
	}

	@Override
	protected List<Patient> doGetAll(RequestContext context) throws ResponseException {
		return getPatientService().getAllPatients();
	}

	@Override
	protected NeedsPaging<Patient> doSearch(String query, RequestContext context) {
		final List<Patient> folders = getPatientService().getPatients(query);
		return new NeedsPaging<Patient>(folders, context);
	}

	/**
	 * NOTE: This query has the potential of missing phantom records that haven't yet been committed into the database. To alleviate the possibility of missing
	 * the phantom records, subsequent synchronize operations should subtract 1 hour (for example) from the 'modifiedSince' value to pickup phantom records; the
	 * down side is that this may re-synchronize records on the mobile unit that are already up-to-date.
	 */
	public NeedsPaging<Patient> searchByBarangayCode(String barangayCode, Long modifiedSince, Long modifiedUpto, RequestContext context) {
		final List<Patient> folders = getChitsService().getAllPatientsByBarangay(barangayCode, modifiedSince, modifiedUpto);
		return new NeedsPaging<Patient>(folders, context);
	}

	private CHITSService getChitsService() {
		if (this.chitsService == null) {
			this.chitsService = Context.getService(CHITSService.class);
		}

		return this.chitsService;
	}

	private CHITSPatientSearchService getChitsPatientSearchService() {
		if (this.chitsPatientSearchService == null) {
			this.chitsPatientSearchService = Context.getService(CHITSPatientSearchService.class);
		}

		return this.chitsPatientSearchService;
	}

	private PatientService getPatientService() {
		if (patientService == null) {
			patientService = Context.getPatientService();
		}

		return patientService;
	}

	private PersonService getPersonService() {
		if (personService == null) {
			personService = Context.getPersonService();
		}

		return personService;
	}

	static class PropertyData {
		String motherUUID;
		String familyUUID;
		boolean headOfTheFamily;

		public PropertyData() {
			reset();
		}

		public void reset() {
			motherUUID = null;
			familyUUID = null;
			headOfTheFamily = false;
		}
	}
}
