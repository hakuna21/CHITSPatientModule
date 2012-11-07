package org.openmrs.module.chits;

import java.util.Locale;

import org.openmrs.module.chits.eccdprogram.ChildCareConstants;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants;
import org.openmrs.util.LocaleUtility;

/**
 * Constants used throughout this module.
 * 
 * @author Bren
 */
public interface Constants {
	/** Global property key containing the family folder code format */
	String GP_FOLDER_FORMAT = "chits.family.folder.code.format";

	/** Global property key containing the patient id format */
	String GP_PATIENT_ID_FORMAT = "chits.patient.identifier.format";

	/** Global property key containing indicating the age (in days) to retain user session information */
	String GP_USER_SESSION_INFO_RETENTION_DAYS = "chits.user.session.retention.days";

	/** The relationship type name for parent/child relationships */
	String PARENT_RELATIONSHIP_NAME = "Parent/Child";

	/**
	 * The relationship type name for the general 'partner' definition (used in primarily by the family planning module, but logically could also indicate a
	 * patient's spouse)
	 */
	String PARTNER_RELATIONSHIP_NAME = "Partner/Partner";

	/** Global property setting which indicates if the queue time should be enabled or if consults should be started automatically */
	String GP_ENABLE_QUEUE_TIME = "enable.queue.time.tracking";

	/** Global property setting which indicates if the queue time should be enabled or if consults should be started automatically */
	String GP_CLEAR_QUEUE_ON_RESTART = "chits.clear.queue.on.restart";

	/** Session attribute key containing the session data map */
	String SESSION_DATA_KEY = "chits_session_data";

	/** Constant representing an attribute value for flagged (ticked) attributes */
	String FLAG_YES = "Y";

	/**
	 * A timestamp of when the app was deployed. This is useful for tagging resources with a "?v=${deploymentTimestamp}" to force reload of cached resources
	 * when a new deployment is performed.
	 */
	String DEPLOYMENT_TIMESTAMP_ATTR = "deploymentTimestamp";

	/** The health worker role */
	String HEALTHWORKER_ROLE = "Health Worker";

	/** American English locale */
	Locale ENGLISH = LocaleUtility.fromSpecification("en_US");

	/**
	 * Following are philhealth concept entries
	 */
	public interface PhilhealthConcepts {
		/** Person attribute type constant */
		String CHITS_PHILHEALTH = "CHITS_PHILHEALTH";

		/** Person attribute type constant */
		String CHITS_PHILHEALTH_EXPIRATION = "CHITS_PHILHEALTH_EXPIRATION";
	}

	/**
	 * Following are identification concept entries
	 */
	public enum PhilhealthSponsorConcepts implements CachedConceptId {
		CHITS_PHILHEALTH_SPONSOR("CHITS_PHILHEALTH_SPONSOR"), //
		NATIONAL("NATIONAL"), //
		LGU("LGU"), //
		IPP("IPP"), //
		EMPLOYER("EMPLOYER"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private PhilhealthSponsorConcepts(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}

	public enum CivilStatusConcepts implements CachedConceptId {
		CIVIL_STATUS("Civil Status"), //
		SINGLE("SINGLE"), //
		MARRIED("MARRIED"), //
		SEPARATED("SEPARATED"), //
		LIVE_IN("LIVE_IN"), //
		WIDOWED("WIDOWED"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private CivilStatusConcepts(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}

	/**
	 * Following are identification concept entries
	 */
	public static interface IdAttributes {
		/** Person attribute type constant */
		String CHITS_LPIN = "CHITS_LPIN";

		/** Person attribute type constant */
		String CHITS_CRN = "CHITS_CRN";

		/** Person attribute type constant */
		String CHITS_TIN = "CHITS_TIN";

		/** Person attribute type constant */
		String CHITS_SSS = "CHITS_SSS";

		/** Person attribute type constant */
		String CHITS_GSIS = "CHITS_GSIS";

		/** Local Identifier */
		String LOCAL_ID = "Local ID";
	}

	/**
	 * Following are address concept entries
	 */
	public interface AddressAttributes {
		/** Person attribute type constant */
		String CHITS_CITY = "CHITS_CITY";

		/** Person attribute type constant */
		String CHITS_BARANGAY = "CHITS_BARANGAY";

		/** Person attribute type constant */
		String CHITS_ADDRESS = "CHITS_ADDRESS";

		/** Concept for 'Family Folders' */
		String FAMILY_FOLDER_CONCEPT = "CHITS_FAMILY_FOLDER";
	}

	/**
	 * Following are address concept entries
	 */
	public interface MiscAttributes {
		/** Number of pregnancies (for females only) */
		String NUMBER_OF_PREGNANCIES = "Number of preganancies";

		/** A person's occupation */
		String OCCUPATION = "Occupation";

		/** A person's educational attainment */
		String EDUCATION = "Educational Attainment";

		/** Flag indicating a non-patient record */
		String NON_PATIENT = "Non Patient";

		/** Boolean attribute indicating if patient needs to see the physician */
		String SEE_PHYSICIAN = "Must See Physician";

		/** Boolean attribute indicating if patient is flagged with '4Ps' */
		String FOUR_PS = "4Ps";

		/** Attribute for indicating where a patient record was created on (e.g., BB device ID) */
		String CREATED_ON = "Created On";

		/** Attribute for indicating where a patient record was last modified on (e.g., BB device ID) */
		String LAST_MODIFIED_ON = "Last Modified From";
	}

	/**
	 * Phone number attributes.
	 * 
	 * @author Bren
	 */
	public interface PhoneAttributes {
		/** Mobile number */
		String MOBILE_NUMBER = "Mobile";

		/** Landline / telephone number */
		String LANDLINE_NUMBER = "Telephone";
	}

	/**
	 * Following are patient visit concept entries
	 */
	public enum VisitConcepts implements CachedConceptId {
		VITAL_SIGNS("VITAL SIGNS"), //
		WEIGHT_KG("WEIGHT (KG)"), //
		HEIGHT_CM("HEIGHT (CM)"), //
		HEAD_CIRC_CM("HEAD CIRCUMFERENCE (CM)"), //
		CHEST_CIRC_CM("CHEST CIRCUMFERENCE (CM)"), //
		WAIST_CIRC_CM("WAIST CIRCUMFERENCE (CM)"), //
		HIP_CIRC_CM("HIP CIRCUMFERENCE (CM)"), //
		DBP("DIASTOLIC BLOOD PRESSURE"), //
		SBP("SYSTOLIC BLOOD PRESSURE"), //
		COMPLAINT("Complaint"), //
		DIAGNOSIS("Diagnosis"), //
		PULSE("PULSE"), //
		RESPIRATORY_RATE("RESPIRATORY RATE"), //
		TEMPERATURE_C("TEMPERATURE (C)"), //
		NOTES_NUMBER("Notes Number"), //
		CONSULT_START("Consult Start"), //
		CONSULT_END("Consult End"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private VisitConcepts(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}

	/**
	 * Drug concepts.
	 */
	public enum DrugOrderConcepts implements CachedConceptId {
		ORDER_TYPE_DRUG_ORDER("Drug Order"), //
		DRUGS_SET("Drugs");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private DrugOrderConcepts(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}

	/**
	 * Concept Sets that contain the concept template notes.
	 * 
	 * @author Bren
	 */
	public enum VisitNotesConceptSets implements CachedConceptId {
		COMPLAINT_NOTES("Complaint Notes"), //
		HISTORY_NOTES("History Notes"), //
		PHYSICAL_EXAM_NOTES("Physical Exam Notes"), //
		DIAGNOSIS_NOTES("Diagnosis Notes"), //
		TREATMENT_NOTES("Treatment Plan Notes"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private VisitNotesConceptSets(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}

	/**
	 * ICD10 constants.
	 * 
	 * @author Bren
	 */
	public interface ICD10 {
		/** The concept source name for ICD10 code mappings */
		String CONCEPT_SOURCE_NAME = "ICD10";

		/** The concept class to use for ICD10 symptom entries */
		String SYMPTOM_CONCEPT_CLASS = "Symptom/Finding";

		/** The concept class to use for ICD10 diagnosis entries */
		String DIAGNOSIS_CONCEPT_CLASS = "Diagnosis";

		/** The datatype of the concepts mapped to ICD10 codes */
		String CONCEPT_DATATYPE = "N/A";
	}

	/**
	 * Program concepts that link a program to a concept.
	 */
	public enum ProgramConcepts implements CachedProgramConceptId {
		/** Not an actual program, but represents the default "Program Workflow" concept of CHITS programs */
		PROGRAM_WORKFLOW("CHITS Program Workflow", null), //

		/** The Child care program */
		CHILDCARE("CHITS Child Care", ChildCareConstants.ChildCareProgramStates.values()), //

		/** The Maternal Care program */
		MATERNALCARE("CHITS Maternal Care", MaternalCareConstants.MaternalCareProgramStates.values()), //

		/** The Family Planning program */
		FAMILYPLANNING("CHITS Family Planning", FamilyPlanningConstants.FamilyPlanningProgramStates.values()); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		/** States within the program */
		private final CachedConceptId[] states;

		/** The cached program Id */
		private CachedProgramNameId programNameId;

		private ProgramConcepts(String programName, CachedConceptId[] states) {
			this.conceptNameId = new CachedConceptNameId(programName);
			this.programNameId = new CachedProgramNameId(programName);
			this.states = states;
		}

		public CachedConceptId[] getStates() {
			return states;
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}

		@Override
		public Integer getProgramId() {
			return programNameId.getCachedProgramId();
		}
	}

	public enum OccupationConcepts implements CachedConceptId {
		MOTHERS_OCCUPATION("mother's occupation"), //
		FATHERS_OCCUPATION("father's occupation"), //
		OCCUPATION("patient's occupation"), //
		UNEMPLOYED("Unemployed"), //
		SELF_EMPLOYED("Self Employed"), //
		EMPLOYEE("Employee"), //
		POLITICIAN("Politician"), //
		OCCUPATION_MEMBERS("patient's occupation, list"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private OccupationConcepts(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}

	public enum EducationConcepts implements CachedConceptId {
		MOTHERS_EDUCATION("mother's education"), //
		FATHERS_EDUCATION("father's education"), //
		EDUCATION("patient's education"), //
		GRADESCHOOl("Grade School Graduate"), //
		HIGHSCHOOL("High School Graduate"), //
		COLLEGE("College Graduate"), //
		MASTERS("Masters Degree"), //
		DOCTORAGE("Doctorate Degree"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private EducationConcepts(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}

	/**
	 * General purpose 'status' concepts.
	 * 
	 * @author Bren
	 */
	public enum StatusConcepts implements CachedConceptId {
		PENDING("PENDING"), //
		REFERRED("REFERRED"), //
		CLOSED("CLOSED"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private StatusConcepts(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}

	/**
	 * General purpose concepts for "Boolean" datatype concepts.
	 * 
	 * @author Bren
	 */
	public enum BooleanConcepts implements CachedConceptId {
		YES("yes"), //
		NO("no"), //
		UNKNOWN("unknown"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private BooleanConcepts(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}

	/**
	 * General purpose concepts for auditing.
	 * 
	 * @author Bren
	 */
	public enum AuditConcepts implements CachedConceptId {
		CREATED_BY("audit record, created by"), //
		MODIFIED_BY("audit record, modified by"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private AuditConcepts(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}

	/**
	 * Default health facility options.
	 * 
	 * @author Bren
	 */
	public enum HealthFacilityConcepts implements CachedConceptId {
		KAPANSANAN("Kapansanan"), //
		OTHER_GOVT("Other Government"), //
		PRIVATE("Private"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private HealthFacilityConcepts(String conceptName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
		}

		@Override
		public String getConceptName() {
			return conceptNameId.getName();
		}

		@Override
		public Integer getConceptId() {
			return conceptNameId.getCachedConceptId();
		}
	}
}
