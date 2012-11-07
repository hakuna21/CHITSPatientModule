package org.openmrs.module.chits.fpprogram;

import org.openmrs.module.chits.CachedConceptId;

/**
 * Constants used by the family planning program.
 */
public interface FamilyPlanningConstants {
	/** Minimum age for males open for enrollment in family planning */
	int MIN_AGE_MALE = 15;

	/** Minimum age for females open for enrollment in family planning */
	int MIN_AGE_FEMALE = 9;

	public static enum FPRegistrationPage {
		/** family information page */
		PAGE1_FAMILY_INFO("fragmentFamilyInfo.jsp"), //

		/** medical history page */
		PAGE2_MEDICAL_HISTORY("fragmentMedicalHistory.jsp"), //

		/** risk factors page */
		PAGE3_RISK_FACTORS("fragmentRiskFactors.jsp"), //

		/** obstetrical history page */
		PAGE4_OBSTETRIC_HISTORY("fragmentObstetricHistory.jsp"), //

		/** physical exam page */
		PAGE5_PHYSICAL_EXAM("fragmentPhysicalExam.jsp"), //

		/** pelvic exam page */
		PAGE6_PELVIC_EXAM("fragmentPelvicExam.jsp"), //

		/** planning method page */
		PAGE7_PLANNING_METHOD("fragmentPlanningMethod.jsp"); //

		private final String jspPath;

		FPRegistrationPage(String jspPath) {
			this.jspPath = jspPath;
		}

		public String getJspPath() {
			return jspPath;
		}
	}

	/**
	 * Family Planning program states.
	 * 
	 * @author Bren
	 */
	public enum FamilyPlanningProgramStates implements CachedConceptId {
		/**
		 * A newly enrolled patient whose registration form has not yet been completed
		 */
		NEW("CHITS Family Planning - New"), //

		/**
		 * A patient whose registration form has been completed
		 */
		REGISTERED("CHITS Family Planning - Registered"), //

		/**
		 * Currently practicing a method; the link to the program on the Visit Details screen shall indicate the method currently used.
		 */
		CURRENT("CHITS Family Planning - Current"), //

		/**
		 * The patient is actually, or perceived to be, not practicing a method. The link to the program on the Visit Details screen shall indicate the method
		 * last used.
		 */
		DROPOUT("CHITS Family Planning - Dropout"), //

		/**
		 * Indicates patient has been removed from the family planning program
		 */
		CLOSED("CHITS Family Planning - Record Closed"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FamilyPlanningProgramStates(String conceptName) {
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
	 * Family information concepts for storing family planning registration information (page 1).
	 */
	public enum FPFamilyInformationConcepts implements CachedConceptId {
		/** Parent observation group containing the medical history information */
		FAMILY_INFORMATION("Family Information, Family Planning"), //

		/** Number of children (numeric) */
		NUMBER_OF_CHILDREN("total number of living children"), //

		/** Number of children (numeric) */
		NMBR_OF_CHILDREN_DESIRED("number of additional children desired"), //

		/** Planned interval, in years (numeric) */
		PLANNED_INTERVAL("planned birth interval (in years)"), //

		/** Reason for practicing family planning (text) */
		REASON_FOR_PRACTICING("reason for practicing family planning"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPFamilyInformationConcepts(String conceptName) {
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
	 * Medical History concepts for storing family planning registration information (page 2).
	 * 
	 * @author Bren
	 */
	public enum FPMedicalHistoryConcepts implements CachedConceptId {
		/** Parent observation group containing the medical history information */
		MEDICAL_HISTORY("family planning medical history"), //

		/*
		 * HEENT conditions
		 */
		SEIZURE("seizure episodes"), //
		HEADACHE("Headache, symptom"), //
		BLURRING("Blurring of Vision"), //
		YELLOWISH_CONJUNCTIVE("icteric sclera"), //
		ENLARGED_THYROID("enlarged thyroid"), //

		/*
		 * Chest/Heart conditions
		 */
		CHEST_PAIN("chest pain"), //
		FATIGABILITY("easy fatigability"), //
		BREAST_MASS("breast mass"), //
		NIPPLE_BLOOD_DISCHARGE("nipple discharge (blood)"), //
		NIPPLE_PUS_DISCHARGE("nipple discharge (pus)"), //
		SBP_OVER140("systolic blood pressure greater than or equal to 140"), //
		DBP_OVER90("diastolic blood pressure greater than or equal to 90"), //
		FAMILY_HISTORY_OF_STROKES_ETC("family history of stroke, hypertension, asthma, or rheumatic heart disease"), //

		/*
		 * Abdomen conditions
		 */
		ABDOMINAL_MASS("abdominal mass"), //
		HISTORY_OF_GALLBLADDER("history of gallbladder disease"), //
		HISTORY_OF_LIVER_DISEASE("history of liver disease"), //

		/*
		 * Genital conditions
		 */
		MASS_IN_UTERUS("uterine mass"), //
		VAGINAL_DISCHARGE("Vaginal Discharge"), //
		INTERMENSTRUAL_BLEEDING("intermenstrual bleeding"), //
		POSTCOITAL_BLEEDING("postcoital bleeding"), //

		/*
		 * Extremities conditions
		 */
		SEVERE_VARICOSITIES("severe varicosities"), //
		EDEMA("edema"), //

		/*
		 * Skin conditions
		 */
		YELLOWISH_SKIN("yellowish skin"), //

		/** Has smoking history (Boolean) */
		SMOKING_HISTORY("smoking history"), //

		/** Number of sticks per day smoked (numeric) */
		SMOKING_STICKS_PER_DAY("cigarettes per day, smoking history"), //

		/** Number of years smoking (numeric) */
		SMOKING_YEARS("duration in years, smoking history"), //

		/** Allergies (text) */
		ALLERGIES("allergy, other"), //

		/** Text for specifying medical drug intakes (text) */
		DRUG_INTAKE("drug intake (medical), other"), //

		/*
		 * Other conditions
		 */
		BLEEDING_TENDENCIES("bleeding disorders"), //
		ANEMIA("anemia"), //
		DIABETES("diabetes mellitus"), //
		HYDATIDIFORM_MOLE("hydatidiform mole within the last twelve months"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPMedicalHistoryConcepts(String conceptName) {
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
	 * Risk Factors concepts for storing family planning registration information (page 3).
	 * 
	 * @author Bren
	 */
	public enum FPRiskFactorsConcepts implements CachedConceptId {
		/** Parent observation group containing the medical history information */
		RISK_FACTORS("risk factor list for family planning"), //

		/*
		 * STI RISKS
		 */
		MULTIPLE_PARTNERS("multiple partners"), //
		VAGINAL_DISCHARGE("vaginal discharge"), //
		VAGINAL_ITCHING("vaginal itching"), //
		BURNING_SENSATION("pain or burning sensation in the genitals"), //
		HISTORY_OF_STI_TREATMENT("history of sexually transmitted infection treatment"), //
		GENITAL_SORES("genital sores"), //
		PENILE_DISCHARGE("penile discharge"), //
		GENITAL_SWELLING("genital swelling"), //

		/*
		 * RISKS FOR VIOLENCE
		 */
		DOMESTIC_VIOLENCE("history of domestic violence"), //
		UNPLEASANT_RELATINOSHIP("unpleasant relationship with partner"), //
		PARTNER_DISAPPROVAL_VISIT("partner disapproval of family planning clinic visit"), //
		PARTNER_DISAPPROVAL_FP("partner disapproval of family planning method use"), //

		/*
		 * Referred to (check all that apply)
		 */
		DSWD("referred to Department of Social Welfare and Development"), //
		WCPU("referred to Women and Children Protection Unit"), //
		NGO("referred to Non-Government Organization"), //
		SOCIAL_HYGIENE_CLINIC("referred to Social Hygiene Clinic"), //
		OTHERS("others, referred to, family planning"), //
		DATE_REFERRED("date referred"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPRiskFactorsConcepts(String conceptName) {
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
	 * Obstetric History concepts for storing family planning registration information (page 4).
	 * 
	 * @author Bren
	 */
	public enum FPObstetricHistoryConcepts implements CachedConceptId {
		/** Parent observation group containing the medical history information */
		OBSTETRIC_HISTORY("Obstetrical History, Family Planning"), //

		/** G* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_GRAVIDA("gravida"), //

		/** P* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_PARA("para"), //

		/** F* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_FT("number of full term pregnancies"), //

		/** P* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_PT("number of preterm births"), //

		/** A* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_AM("number of abortions/miscarriages"), //

		/** L* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_LC("total number of living children"), //

		DATE_OF_LAST_DELIVERY("date of last delivery"), //
		TYPE_OF_LAST_DELIVERY("method of last delivery"), //
		PREVIOUS_MENSTRUAL_PERIOD("previous menstrual period"), //
		LAST_MENSTRUAL_PERIOD("Last menstrual period"), //
		DURATION_OF_BLEEDING("duration in days, menstrual bleeding"), //
		DYSMENORRHEA("presence of dysmenorrhea"), // -> yes | no (one of FPObstetricOptions)
		AMOUNT_OF_BLEEDING("qualitative amount of bleeding"), // -> light | moderate | heavy (one of FPObstetricOptions)
		REGULARITY("regularity, menstrual cycle"); // -> regular, menstrual cycle | irregular, menstrual cycle (one of FPObstetricOptions)

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPObstetricHistoryConcepts(String conceptName) {
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
	 * Physical Examination concepts for storing family planning registration information (page 5).
	 * 
	 * @author Bren
	 */
	public enum FPPhysicalExaminationConcepts implements CachedConceptId {
		/** Parent observation group containing the medical history information */
		PHYSICAL_EXAMINATION("family planning physical examination"), //

		PALE_CONJUNCTIVA("pale conjunctiva"), // true/false
		YELOWISH_CONJUNCTIVA("yellowish skin"), // true/false
		ENLARGED_THYROID("enlarged thyroid"), // true/false
		ENLARGED_LYMPH_NODES("enlarged lymph nodes, physical exam findings"), // true/false
		MASS_UL_ORB("breast mass, upper outer, right"), // true/false
		MASS_UL_IRB("breast mass, upper inner, right"), // true/false
		MASS_LL_ORB("breast mass, lower outer, right"), // true/false
		MASS_LL_IRB("breast mass, lower inner, right"), // true/false
		MASS_UL_OLB("breast mass, upper outer, left"), // true/false
		MASS_UL_ILB("breast mass, upper inner, left"), // true/false
		MASS_LL_OLB("breast mass, lower outer, left"), // true/false
		MASS_LL_ILB("breast mass, lower inner, left"), // true/false
		MASS_ON_LEFT_DESCR("description of mass(es), left breast"), // text
		MASS_ON_RIGHT_DESCR("description of mass(es), right breast"), // text
		NIPPLE_DISCHARGE_LB("nipple discharge, left"), // true/false
		NIPPLE_DISCHARGE_RB("nipple discharge, right"), // true/false
		DIMPLING_LEFT("peau d'orange, left"), // true/false
		DIMPLING_RIGHT("peau d'orange, right"), // true/false
		ENLARGED_AXILLARY_LEFT_LYMPH_NODES("enlarged axillary lymph nodes, right"), // true/false
		ENLARGED_AXILLARY_RIGHT_LYMPH_NODES("enlarged axillary lymph nodes, left"), // true/false
		ABNORMAL_HEART_SOUNDS("abnormal heart sounds"), // true/false
		ABNORMAL_BREATH_SOUNDS("breathing sounds, abnormal"), // true/false
		ENLARGED_LIVER("liver enlargement"), // true/false
		ABDOMINAL_MASS("abdominal mass"), // true/false
		ABDOMINAL_TENDERNESS("unspecified abdominal tenderness"), // true/false
		EDEMA("Edema"), // true/false
		VARICOSITIES("severe varicosities"), // true/false

		/** Text entry for description for other remarks (text) */
		OTHERS("others, family planning physical examination"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPPhysicalExaminationConcepts(String conceptName) {
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
	 * Pelvic Examination concepts for storing family planning registration information (page 6).
	 * 
	 * @author Bren
	 */
	public enum FPPelvicExaminationConcepts implements CachedConceptId {
		/** Parent observation group containing the medical history information */
		PELVIC_EXAMINATION("family planning pelvic examination"), //

		/*
		 * PERINEUM
		 */
		SCARS("perineal scar(s) present"), // true/false
		PERINEUM_WARTS("perineal wart(s) present"), // true/false
		REDDISH("perineum reddish"), // true/false
		PERINEUM_LACERATION("perineal laceration(s) present"), // true/false

		/*
		 * VAGINA
		 */
		VAGINA_CONGESTED("vagina congested"), // true/false
		BARTHOLINS_CYST("Bartholin's cyst present"), // true/false
		VAGINA_WARTS("vaginal wart(s) present"), // true/false
		SKENES_GLAND("Skene's gland swollen"), // true/false
		VAGINAL_DISCHARGE("description of vaginal discharge"), // Boolean and Text
		VAGINAL_RECTOCOELE("rectocoele present"), // true/false
		CYTOCOELE("cytocoele present"), // true/false

		/*
		 * CERVIX
		 */
		CERVIX_CONGESTED("cervix congested"), // true/false
		ERODED("cervix eroded"), // true/false
		CERVICAL_DISCHARGE("description of cervical discharge"), // Boolean and text
		POLYPS("cervical polyps or cysts present"), // true/false
		CERVICAL_LACERATION("cervical lacerations present"), // true/false

		/*
		 * CERVIX COLOR
		 */
		CERVIX_PINKISH("cervix is pinkish"), // true/false
		CERVIX_BLUISH("cervix is bluish"), // true/false

		/*
		 * CERVIX CONSISTENCY
		 */
		CERVIX_FIRM("firm cervix"), // true/false
		CERVIX_SOFT("soft cervix"), // true/false

		/*
		 * UTERUS POSITION
		 */
		UTERUS_MID("uterus mid"), // true/false
		UTERUS_ANTEFLEXED("uterus anteflexed"), // true/false
		UTERUS_RETROFLEXED("uterus retroflexed"), // true/false

		/*
		 * UTERUS SIZE
		 */
		NORMAL_UTERUS("normal corpus size"), // true/false
		SMALL_UTERUS("small corpus"), // true/false
		LARGE_UTERUS("enlarged corpus"), // true/false

		/*
		 * UTERUS MASS
		 */
		UTERINE_DEPTH("uterine depth (cm)"), // (numeric)

		/*
		 * ADNEXA
		 */
		NORMAL_ADNEXA("normal adnexa"), // true/false
		ADNEXA_WITH_MASSES("adnexa with mass(es)"), // true/false
		ADNEXA_WITH_TENDERNESS("adnexa with tenderness"), // true/false

		/** Text entry for description for other remarks (text) */
		OTHERS("other findings for family planning pelvic examination"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPPelvicExaminationConcepts(String conceptName) {
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
	 * Family planning method concepts for storing family planning registration information (page 7).
	 * <p>
	 * NOTE: This observations for this enum can contain member observations of the FPServiceDeliveryConcepts type.
	 */
	public enum FPFamilyPlanningMethodConcepts implements CachedConceptId {
		/**
		 * Parent observation group containing the family planning method information;<br/>
		 * Coded value indicates the actual method: answers for female / male should be under the FPMethodOptions.FEMALE_METHODS / FPMethodOptions.MALE_METHODS
		 * concepts (coded)
		 */
		FAMILY_PLANNING_METHOD("family planning method"), //

		/** Date of enrollment (datetime) */
		DATE_OF_ENROLLMENT("Date of Enrollment, Family Planning"), //

		/** Date of dropout (datetime) */
		DATE_OF_DROPOUT("Date of Dropout, Family Planning"), //

		/** Date of next service (datetime) */
		DATE_OF_NEXT_SERVICE("Date of Next Service, Family Planning"), //

		/** Date of dropout (datetime) */
		DROPOUT_REASON("reason for method dropout"), //

		/** Client type (one of the ClientTypeConcepts); concept answers should include all FPClientTypeConcepts (coded) */
		CLIENT_TYPE("family planning client type"), //

		/** Remarks */
		REMARKS("family planning method remarks");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPFamilyPlanningMethodConcepts(String conceptName) {
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
	 * Represents observation members of a family planning method (FPFamilyPlanningMethodConcepts.FAMILY_PLANNING_METHOD).
	 * <p>
	 * 
	 * @author Bren
	 */
	public enum FPServiceDeliveryRecordConcepts implements CachedConceptId {
		/** Parent observation group containing a service delivery record. */
		SERVICE_DELIVERY_RECORD("Family Planning Service Delivery Record"), //

		/** Date the service was administered */
		DATE_ADMINISTERED("Date Administered"), //

		/** Supply quantity (not applicable for PERMANENT types */
		SUPPLY_QUANTITY("method supply quantity"), //

		/** Supply source (not applicable for PERMANENT types */
		SUPPLY_SOURCE("method supply sources"), //

		/** Free-text remarks */
		REMARKS("remarks on family planning method service delivery");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPServiceDeliveryRecordConcepts(String conceptName) {
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
	 * Concept answers for the value coded concept value of the CLIENT_TYPE observation indicating the client type.
	 * 
	 * @author Bren
	 */
	public enum FPClientTypeConcepts implements CachedConceptId {
		NA("new acceptor (NA)"), //
		LU("learning user (LU)"), //
		CU("current/continuing user (CU)"), //
		CC("other acceptor - changing clinic (CC)"), //
		CM("other acceptor - changing method (CM)"), //
		RS("other acceptor - restart (RS)"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPClientTypeConcepts(String conceptName) {
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
	 * Concept answers for the value coded concept value of the METHOD observation indicating the family planning method desired.
	 * 
	 * @author Bren
	 */
	public enum FPMethodOptions implements CachedConceptId {
		/** Concept containing the valid answers for the family planning method for male patients */
		MALES("Family Planning Method, Males"), //

		/** Concept containing the valid answers for the family planning method for female patients */
		FEMALES("Family Planning Method, Females"), //

		/** Concept convenience set containing the natural family planning methods */
		NATURAL("Family Planning Method, Natural"), //

		/** Concept convenience set containing the non-permanent artificial family planning methods */
		ARTIFICIAL_NONPERM("Family Planning Method, Artificial, Non-Permanent"), //

		/** Concept convenience set containing the permanent artificial family planning methods */
		ARTIFICIAL_PERM("Family Planning Method, Artificial, Permanent"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPMethodOptions(String conceptName) {
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
	 * Family planning method options that apply to males.
	 */
	public enum FPMaleMethodOptions implements CachedConceptId {
		/** Family planning method: condom (coded answer) */
		CONDOM("condom (CON)"), //
		VASECTOMY("vasectomy (MSTR/VASECTOMY)"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPMaleMethodOptions(String conceptName) {
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
	 * Family planning method options that apply to females.
	 */
	public enum FPFemaleMethodOptions implements CachedConceptId {
		NFP_BB("basal body temperature (NFP-BBP)"), //
		NFP_CM("cervical mucus method (NFP-CM)"), //
		NFP_LAM("lactational amenorrhea (NFP-LAM)"), //
		NFP_SDM("standard days method (NFP-SDM)"), //
		NFP_STM("sympothermal method (NFP-STM)"), //
		PILLS("contraceptive pills (PILLS)"), //
		CONDOM("condom (CON)"), //
		INJ("injectables (INJ)"), //
		IUD("intra-uterine device (IUD)"), //
		FSTRL_BTL("tubal ligation (FSTR/BTL)"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPFemaleMethodOptions(String conceptName) {
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

	/*
	 * Options and answers follow.
	 */

	public static enum FPObstetricOptions implements CachedConceptId {
		/*
		 * Answers for "presence of dysmenorrhea"
		 */
		PAINFUL("yes"), PAINLESS("no"),

		/*
		 * Answers for "qualitative amount of bleeding"
		 */
		LIGHT("light"), MODERATE("moderate"), HEAVY("heavy"),

		/*
		 * Answers for "regularity, menstrual cycle")
		 */
		REGULAR("regular, menstrual cycle"), IRREGULAR("irregular, menstrual cycle");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FPObstetricOptions(String conceptName) {
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
