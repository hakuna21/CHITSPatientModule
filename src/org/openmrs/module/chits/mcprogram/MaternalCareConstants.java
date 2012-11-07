package org.openmrs.module.chits.mcprogram;

import org.openmrs.PatientProgram;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.MethodOfDeliveryConcepts;

/**
 * Constants used by the maternal care program.
 * <p>
 * Observation hierarchy:
 * <ul>
 * <li>{@link ProgramConcepts#MATERNALCARE} ("CHITS Maternal Care") with UUID matching the {@link PatientProgram} record. Since females may be enrolled multiple
 * times in the program, the records will be grouped by the UUID of the corresponding patient program record.
 * <ul>
 * <li>1 - {@link MCObstetricHistoryConcepts#OBSTETRIC_HISTORY} - contains obstetric history registration data (page 1)</li>
 * <li>* - {@link MCObstetricHistoryDetailsConcepts#OBSTETRIL_HISTORY_DETAILS} - contains obstetric history details registration data (page 1)
 * <ul>
 * <li>* - {@link MCPregnancyOutcomeConcepts#PREGNANCY_OUTCOME} - contains the pregnancy outcome for one fetus</li>
 * </ul>
 * </li>
 * <li>1 - {@link MCMenstrualHistoryConcepts#MENSTRUAL_HISTORY} - contains menstrual history registration data (page 2)</li>
 * <li>1 - {@link MCMedicalHistoryConcepts#MEDICAL_HISTORY} - contains medical history registration data (page 2)</li>
 * <li>1 - {@link MCFamilyMedicalHistoryConcepts#FAMILY_MEDICAL_HISTORY} - contains family medical history registration data (page 2)</li>
 * <li>1 - {@link MCPersonalHistoryConcepts#PERSONAL_HISTORY} - contains personal / social history registration data (page 2)</li>
 * <li>1 - {@link MCDangerSignsConcepts#DANGER_SIGNS} - contains danger signs registration data (page 3)</li>
 * </ul>
 * </li>
 * <li>{@link TetanusToxoidDateAdministeredConcepts} - individual administration dates of the tetanus vaccines; this applies to the patient and is not a member
 * of a parent observation.</li>
 * </ul>
 */
public interface MaternalCareConstants {
	public static enum MCRegistrationPage {
		/** obstetric history registration page */
		PAGE1_OBSTETRIC_HISTORY("fragmentObstetricHistoryRegistrationForm.jsp"),

		/** menstrual, past / present, family, and social history registration page */
		PAGE2_OTHER_HISTORY("fragmentOtherHistoryRegistrationForm.jsp");

		private final String jspPath;

		MCRegistrationPage(String jspPath) {
			this.jspPath = jspPath;
		}

		public String getJspPath() {
			return jspPath;
		}
	}

	/**
	 * Maternal Care program states.
	 * 
	 * @author Bren
	 */
	public static enum MaternalCareProgramStates implements CachedConceptId {
		/**
		 * Initial state when a patient is enrolled but not yet registered to the maternal care program.
		 */
		NEW("CHITS Maternal Care - New"), //

		/**
		 * Stores the state of registration -- this should not be modifiable by the "set patient consult status" function.
		 */
		REGISTERED("CHITS Maternal Care - Registered"), //

		/**
		 * Indicates patient is in the maternal care program and has already been registered.
		 */
		ACTIVE("CHITS Maternal Care - Active"), //

		/**
		 * The patient has been referred to another facility for continued or further management.
		 */
		REFERRED("CHITS Maternal Care - Referred"), //

		/**
		 * The patient is already at term and is ready for admission to the lying-in clinic.
		 */
		ADMITTED("CHITS Maternal Care - Admitted"), //

		/** Indicates the program has been ended */
		ENDED("CHITS Maternal Care - Ended"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MaternalCareProgramStates(String conceptName) {
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
	 * Tetanus Toxoid date administered concepts: Unlike other 'date administered' concepts, there is one concept for each of the five tetanus doses. The parent
	 * observation is the TetanusToxoidRecordConcepts.VACCINE_TYPE.
	 * <p>
	 * NOTE: These are already installed by the uploaded maternity care concept dictionary.
	 * 
	 * @author Bren
	 */
	public static enum TetanusToxoidDateAdministeredConcepts implements CachedConceptId {
		TT1("tetanus toxoid first dose, date administered", "TT1"), //
		TT2("tetanus toxoid second dose, date administered", "TT2"), //
		TT3("tetanus toxoid third dose, date administered", "TT3"), //
		TT4("tetanus toxoid fourth dose, date administered", "TT4"), //
		TT5("tetanus toxoid fifth dose, date administered", "TT5"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		/** The short name */
		private final String shortName;

		private TetanusToxoidDateAdministeredConcepts(String conceptName, String shortName) {
			this.conceptNameId = new CachedConceptNameId(conceptName);
			this.shortName = shortName;
		}

		public String getShortName() {
			return shortName;
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

	public static enum TetanusToxoidRecordConcepts implements CachedConceptId {
		/** Tetanus Vaccination type: serves both as the group obs and the tetanus vaccine type (one of TetanusToxoidDoseType), (coded) */
		VACCINE_TYPE("vaccine type, tetanus vaccination, maternal care services"),

		/**
		 * Holder for the date 'administered' valueDatetime: NOTE: The concept of this observation should be changed to the appropriate concept based on the
		 * vaccine type before saving, (Date)
		 */
		DATE_ADMINISTERED("tetanus toxoid first dose, date administered"), //

		/** Coded answer, (coded) */
		VISIT_TYPE("visit type, tetanus vaccination, maternal care services"), //

		/** Coded answer, (coded) */
		SERVICE_SOURCE("service source, tetanus vaccination, maternal care services"), //

		/** Free-text, (text) */
		REMARKS("remarks, tetanus vaccination, maternal care services");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private TetanusToxoidRecordConcepts(String conceptName) {
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

	public static enum TetanusToxoidDoseType implements CachedConceptId {
		TT1("tetanus toxoid 1"), //
		TT2("tetanus toxoid 2"), //
		TT3("tetanus toxoid 3"), //
		TT4("tetanus toxoid 4"), //
		TT5("tetanus toxoid 5"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private TetanusToxoidDoseType(String conceptName) {
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
	 * Obstetric History concepts for storing maternal care registration information (page 1).
	 * <p>
	 * This concept is a child of the {@link ProgramConcepts#MATERNALCARE} observation.
	 * 
	 * @author Bren
	 */
	public static enum MCObstetricHistoryConcepts implements CachedConceptId {
		/** Parent observation group containing the obstetric history information (ConvSet) */
		OBSTETRIC_HISTORY("Obstetric History"), //

		/** Last menstrual period (Date) */
		LAST_MENSTRUAL_PERIOD("Last menstrual period"), //

		/** Last menstrual period, remarks (Text) */
		LMP_REMARKS("remarks on last menstrual period"), //

		/** Blood type: one of the {@link MCBloodTypeOptions} values, (coded) */
		BLOOD_TYPE("Blood Type (coded)"), //

		/** RH factor: one of the {@link MCRHFactorOptions} values, (coded) */
		RHESUS_FACTOR("Rhesus Factor"), //

		/** Pregnancy test date performed, (Date) */
		PREGNANCY_TEST_DATE_PERFORMED("pregnancy test, date performed"), //

		/** Pregnancy test result (valueCoded): one of the {@link MCPregnancyTestResultsOptions} values, (coded) */
		PREGNANCY_TEST_RESULT("pregnancy test"), //

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

		/** History of previous c-section, (Question/Boolean) */
		HISTORY_PREV_CSECTION("history of previous cesarean section"), //

		/** History of 3 or more consecutive miscarriages or stillborn, yes or no coded values (Question/Boolean) */
		HISTORY_3OR_MORE_MISCARRIAGES("history of 3 or more consecutive miscarriages or stillborn"), //

		/** History of postpartum hemorrhage, yes or no coded values (Question/Boolean) */
		HISTORY_OF_POSTPARTUM_HEMORRHAGE("history of postpartum hemorrhage");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCObstetricHistoryConcepts(String conceptName) {
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
	 * Encapsulates a single record of the obstetric history details form within the OBSTETRIC_HISTORY ("Obstetric History") observation.
	 * 
	 * @author Bren
	 */
	public static enum MCObstetricHistoryDetailsConcepts implements CachedConceptId {
		/**
		 * Parent observation group containing the obstetric history details.
		 * <P>
		 * IMPORTANT: The 'creator' of the Obs of this record indicates the "assisted by" value.
		 */
		OBSTETRIC_HISTORY_DETAILS("Obstetric History Details, Maternal Care"), //

		/** Gravida, (numeric) */
		GRAVIDA("gravida"), //

		/** Year of delivery, (Date) */
		YEAR_OF_PREGNANCY("year of delivery"), //

		/** A coded answer to "location of delivery" defined in the maternal concept dictionary, (coded) */
		PLACE_OF_DELIVERY("location of delivery"), //

		/** The delivery assistant, (coded) */
		DELIVERY_ASSISTANT("delivery assistant"), //
		DELIVERY_ASSISTANT_ANSWERS("delivery assistant, answers");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCObstetricHistoryDetailsConcepts(String conceptName) {
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
	 * Encapsulates a single pregnancy outcome contained within the OBSTETRIC_HISTORY_DETAILS ("Obstetric History Details, Maternal Care") observation.
	 * <p>
	 * NOTE: These concepts (except the pregnancy outcome parent concept) are already defined by the maternal care concept dictionary.
	 * 
	 * @author Bren
	 */
	public static enum MCPregnancyOutcomeConcepts implements CachedConceptId {
		/**
		 * Parent observation group containing the pregnancy outcome (ConvSet).
		 * <p>
		 * NOTE: The observation parent doubles as a link to the baby's patient record<br/>
		 * using the 'valueGroupId' to store the patientId representing the baby.
		 */
		PREGNANCY_OUTCOME("Pregnancy Outcome, Maternal Care"), //

		/** Sex of baby */
		SEX("sex of baby"), //
		SEX_ANSWERS("sex of baby, answers"), //

		/** Term (coded) */
		TERM("gestational age at birth, quality"), //
		TERM_ANSWERS("gestational age at birth, quality, answers"), //

		/** delivery weight: Special note: this observation should be owned by the baby patient record, and not the maternal care patient */
		BIRTH_WEIGHT_KG("birth weight (kg)"), //
		
		/** Re-defined to encode birth weight of baby */
		BIRTH_WEIGHT_OF_BABY_KG("birth weight of baby (kg)"), //

		/** Method of delivery Question: one of {@link MethodOfDeliveryConcepts} answers, (coded) */
		METHOD("method of delivery"), //
		METHOD_ANSWERS("method of delivery, answers"), //

		/** Outcome of pregnancy: one of the answers already defined in the concept dictionary, coded */
		OUTCOME("outcome of pregnancy"), //
		OUTCOME_ANSWERS("outcome of pregnancy, answers"), //

		/** If breastfed within 1 hour */
		BREASTFED_WITHIN_HOUR("Breastfeeding initiated within one hour");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCPregnancyOutcomeConcepts(String conceptName) {
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
	 * Menstrual History concepts for storing maternal care registration information (part of page 2).
	 * <p>
	 * This concept is a child of the {@link ProgramConcepts#MATERNALCARE} observation.
	 * 
	 * @author Bren
	 */
	public static enum MCMenstrualHistoryConcepts implements CachedConceptId {
		/** Parent observation group containing the menstrual history (ConvSet) */
		MENSTRUAL_HISTORY("Menstrual History"), //

		/** Age of Menarche (numeric) */
		AGE_OF_MENARCHE("menarche"), //

		/** Interval in days, menstrual cycle, (numeric) */
		INTERVAL("interval in days, menstrual cycle"), //

		/** Duration in days, menstrual cycle, (numeric) */
		DURATION("duration in days, menstrual cycle"), //

		/** presence of dysmenorrhea, one of the answers: yes/no (coded) */
		DYSMENORRHEA("presence of dysmenorrhea"), //

		/** free text input of number of pads the patient uses per day of menstruation, in range, (text) */
		FLOW("amount, menstrual cycle"), //

		/** Coded value of yes/no/unknown (coded) */
		GIVEN_TETANUS_DOSE("tetanus toxoid 1"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCMenstrualHistoryConcepts(String conceptName) {
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
	 * Past / Present Medical History concepts for storing maternal care registration information (part of page 2).
	 * <p>
	 * This concept is a child of the {@link ProgramConcepts#MATERNALCARE} observation.
	 * 
	 * @author Bren
	 */
	public static enum MCMedicalHistoryConcepts implements CachedConceptId {
		/** Parent observation group containing the medical history (ConvSet) */
		MEDICAL_HISTORY("Past Medical History"), //

		/** Indicates hypertension, (Boolean) */
		HYPERTENSION("hypertension"), //

		/** Medical history indication, yes or no coded values, (Boolean) */
		ASTHMA("asthma"), //

		/** Medical history indication, yes or no coded values, (Boolean) */
		DIABETES("diabetes mellitus"), //

		/** Medical history indication, yes or no coded values, (Boolean) */
		TUBERCULOSIS("tuberculosis"), //

		/** Medical history indication, yes or no coded values, (Boolean) */
		HEART_DISEASE("heart disease"), //

		/** Medical history indication, yes or no coded values, (Boolean) */
		ALLERGY("allergy"), //

		/** Medical history indication, yes or no coded values, (Boolean) */
		STI("sexually transmitted infections"), //

		/** Medical history indication, yes or no coded values, (Boolean) */
		BLEEDING_DISORDERS("bleeding disorders"), //

		/** Other medical history, (Text) */
		OTHERS("others, text, past medical history"), //

		/** Medical history indication, yes or no coded values, (Boolean) */
		THYROID("thyroid disease"), //

		/** General remarks on the patient's medical history, (Text) */
		REMARKS("remarks, medical history");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCMedicalHistoryConcepts(String conceptName) {
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
	 * Family Medical History concepts for storing maternal care registration information (part of page 2).
	 * <p>
	 * This concept is a child of the {@link ProgramConcepts#MATERNALCARE} observation.
	 * <p>
	 * NOTE: The observations under this group are the same ones under {@link MCMedicalHistoryConcepts}
	 * 
	 * @author Bren
	 */
	public static enum MCFamilyMedicalHistoryConcepts implements CachedConceptId {
		/** Parent observation group containing the medical history (ConvSet) */
		FAMILY_MEDICAL_HISTORY("Family Medical History"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCFamilyMedicalHistoryConcepts(String conceptName) {
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
	 * Personal / Social History concepts for storing maternal care registration information (part of page 2).
	 * <p>
	 * This concept is a child of the {@link ProgramConcepts#MATERNALCARE} observation.
	 * 
	 * @author Bren
	 */
	public static enum MCPersonalHistoryConcepts implements CachedConceptId {
		/** Parent observation group containing the medical history (ConvSet) */
		PERSONAL_HISTORY("Personal and Social History"), //

		/** Has smoking history (Boolean) */
		SMOKING_HISTORY("smoking history"), //

		/** Number of sticks per day smoked (numeric) */
		SMOKING_STICKS_PER_DAY("cigarettes per day, smoking history"), //

		/** Number of years smoking (numeric) */
		SMOKING_YEARS("duration in years, smoking history"), //

		/** Has illicit drug history (Boolean) */
		ILLICIT_DRUG_USE("illicit drug use"), //

		/** Text for specifying drugs that were previously taken (text) */
		ILLICIT_DRUG_USE_DETAILS("details, illicit drug use"), //

		/** history of alcholic intake (boolean) */
		ALCOHOLIC_INTAKE("alcoholic intake"), //

		/** free text input of frequency of alcoholic intake sessions, (text) */
		ALCOHOLIC_INTAKE_DETAILS("frequency, alcoholic intake"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCPersonalHistoryConcepts(String conceptName) {
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
	 * Danger Signs concepts for storing maternal care registration information (part of page 3).
	 * <p>
	 * This concept is a child of the {@link ProgramConcepts#MATERNALCARE} observation.
	 * 
	 * @author Bren
	 */
	public static enum MCDangerSignsConcepts implements CachedConceptId {
		/** Parent observation group containing the medical history */
		DANGER_SIGNS("Danger signs, pregnancy"), //

		/** Danger sign indication (Symptom / Boolean) */
		SEVERE_HEADACHE("Severe Headache"), //

		/** Danger sign indication (Symptom / Boolean) */
		FEVER("Fever"), //

		/** Danger sign indication (Symptom / Boolean) */
		BLURRING_OF_VISION("Blurring of Vision"), //

		/** Danger sign indication (Symptom / Boolean) */
		VOMITING("Vomiting"), //

		/** Danger sign indication (Symptom / Boolean) */
		ABDOMINAL_PAIN("Abdominal Pain"), //

		/** Danger sign indication (Symptom / Boolean) */
		VAGINAL_BLEEDING("Vaginal Bleeding"), //

		/** Danger sign indication (Symptom / Boolean) */
		VAGINAL_DISCHARGE("Vaginal Discharge"), //

		/** Danger sign indication (Symptom / Boolean) */
		PRETERM_LABOR("Preterm Labor"), //

		/** Danger sign indication (Symptom / Boolean) */
		DECREASED_FETAL_MOVEMENT("Decreased Fetal Movement"), //

		/** Danger sign indication (Symptom / Boolean) */
		EDEMA("Edema"), //

		/** Danger sign indication (Symptom/Boolean) */
		DIZZINESS("dizziness"), //

		/** Remarks (Misc/Text) */
		REMARKS("Remarks"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCDangerSignsConcepts(String conceptName) {
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
	 * Obstetric examination and leopold's maneuver findings
	 * 
	 * @author Bren
	 */
	public static enum MCObstetricExamination implements CachedConceptId {
		/** Parent observation group containing the obstetric examination record details */
		OBSTETRIC_EXAMINATION("Physical Examination, Specialized: Obstetric"), //

		/** Fundal height, (Numeric) */
		FUNDIC_HEIGHT("fundal height (cm)"), //

		/** Fetal heart rate, (Numeric) */
		FHR("fetal heart rate (beats/minute)"), //

		/** Fetal heart rate location, (coded) */
		FHR_LOCATION("location of fetal heart tones"), //

		/** Fetal presentation, (coded) */
		FETAL_PRESENTATION("fetal presentation"), //

		/** fundal grip, (Text) */
		FUNDAL_GRIP("fundal grip findings"), //

		/** umbilical grip, (Text) */
		UMBILICAL_GRIP("umbilical grip findings"), //

		/** pawlick's grip, (Text) */
		PAWLICKS_GRIP("pawlick's grip findings"), //

		/** pelvic grip, (Text) */
		PELVIC_GRIP("pelvic grip findings");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCObstetricExamination(String conceptName) {
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
	 * Prenatal visits record concepts for storing prenatal visit details.
	 * <p>
	 * This concept is a child of the {@link ProgramConcepts#MATERNALCARE} observation.
	 * 
	 * @author Bren
	 */
	public static enum MCPrenatalVisitRecordConcepts implements CachedConceptId {
		/** Parent observation group containing the prenatal visit record details */
		PRENATAL_VISIT_RECORD("Prenatal Visit Record, Maternal Care"), //

		/** Prenatal visit date, (Date) */
		VISIT_DATE("current date"), //

		/** prenatal visit type, (coded) */
		VISIT_TYPE("visit type, prenatal"), //

		/** Nutritionally at risk, (Boolean) */
		NUTRITIONALLY_AT_RISK("nutritionally at risk"), //

		/** Prenatal other findings and remarks, (Text) */
		REMARKS("remarks, prenatal consult");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCPrenatalVisitRecordConcepts(String conceptName) {
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
	 * Internal examination record
	 * 
	 * @author Bren
	 */
	public static enum MCIERecordConcepts implements CachedConceptId {
		/** Parent observation group containing the internal examination (IE) record details */
		INTERNAL_EXAMINATION("Physical Examination, Specialized: IE"), //

		/** Prenatal visit date, (Date) */
		VISIT_DATE("current date"), //

		/** One of the MCIEOptions concepts - Normal or Others, (coded) */
		EXTERNAL_GENITALIA("external genitalia, genitourinary findings"), //

		/** Specified if 'other' selected, (Text) */
		EXTERNAL_GENITALIA_TEXT("others, external genitalia"), //

		/** One of the MCIEOptions concepts - Nulliparous or Parous coded values, (coded) */
		VAGINA("vaginal integrity, genitourinary findings"), //

		/** One of the MCIEOptions concepts - open or closed coded values, (coded) */
		CERVIX_STATE("cervical os, genitourinary findings"), //

		/** One of the MCIEOptions concepts - soft or firm coded values, (coded) */
		CERVIX_CONSISTENCY("cervical integrity, genitourinary findings"), //

		/** Uterus: Enlarged to (in weeks AOG), (numeric) */
		ENLARGED_TO("enlarged to (in weeks)"), //

		/** Adnexal Mass / Tenderness: One of the MCIEOptions concepts - positive or negative, (coded) */
		TENDERNESS("adnexal mass or tenderness, bimanual examination, genitourinary findings"), //

		/** Masses / Locations, (Text) */
		MASSES_LOCATIONS("other findings, abdominal findings"), //

		/** Pelvimetry: One of the MCIEOptions concepts - adequate or inadequate, (coded) */
		PELVIMETRY("pelvimetry"), //

		/** Membranes: One of the MCIEOptions concepts - intact or ruptured, (coded) */
		MEMBRANES("status of membranes"), //

		/** Fetal presentation - dropdown, (coded) */
		FETAL_PRESENTATION("fetal presentation"), //

		/** Fetal station - dropdown (Coded) */
		FETAL_STATION("fetal station"), //

		/** Bloody Show: One of the MCIEOptions concepts - positive or negative, (coded) */
		BLOODY_SHOW("bloody show"), //

		/** Remarks, (Text) */
		REMARKS("remarks, obstetric examination");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCIERecordConcepts(String conceptName) {
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
	 * Internal examination record
	 * 
	 * @author Bren
	 */
	public static enum MCPostpartumIERecordConcepts implements CachedConceptId {
		/**
		 * Parent observation group containing the internal examination (IE) record details.
		 * <p>
		 * NOTE: The 'valueCoded' will contain 'true' if this is a discharge IE, otherwise, it is considered a routine IE.
		 */
		POSTDELIVERY_IE("Post-delivery"), //

		/** Prenatal visit date, (Date) */
		VISIT_DATE("current date"), //

		/** One of the MCIEOptions concepts - open or closed coded values, (coded) */
		CERVIX_STATE("cervical os, genitourinary findings"), //

		/** One of the MCIEOptions concepts - soft or firm coded values, (coded) */
		UTERUS("cervical integrity, genitourinary findings"), //

		/** One of the MCIEOptions concepts - MINIMAL, MODERATE, SEVERE, (coded) */
		BLEEDING("vaginal bleeding, post-delivery"), //

		/** One of the MCIEOptions concepts - NONE, MINIMAL, MODERATE, SEVERE, (coded) */
		VAGINAL_DISCHARGE("vaginal discharge, post-delivery"), //

		/** One of the MCIEOptions concepts - Absent or Present, (coded) */
		WOUND_DEHISCENCE("wound dehiscence, post-delivery"), //

		/** One of the MCIEOptions concepts - SUTURES_INTACT or SUTURES_NOT_INTACT, (coded) */
		SUTURES("sutures, post-delivery"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCPostpartumIERecordConcepts(String conceptName) {
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
	 * Birth plan record
	 * <p>
	 * This concept is a child of the {@link ProgramConcepts#MATERNALCARE} observation.
	 * 
	 * @author Bren
	 */
	public static enum MCBirthPlanConcepts implements CachedConceptId {
		/** Parent observation group containing the birth plan details */
		BIRTH_PLAN("birth plan"), //
		FATHERS_NAME("father's name"), //
		FATHERS_AGE("father's age"), //
		CHILDS_NAME("child's name"), //
		EMAIL_ADDRESS("email address"), //
		CONTACT_PHONE("contact phone number"), //
		CONTACT_CELL("cellphone number"), //
		BIRTH_ATTENDANT("person planned to deliver the baby"), //
		DELIVERY_LOCATION("planned location of delivery"), //
		MODE_OF_TRANSPO("transportation to be used"), //
		DELIVERY_COMPANION("person accompanying patient during delivery"), //
		STAY_AT_HOME("person to stay at home"), //
		BLOOD_DONOR("name of blood donor"), //
		EMERGENCY_PERSON("person to call in case of emergency"), //
		EMERGENCY_PHONE("contact phone number to call in case of emergency"), //
		EMERGENCY_CELL("cellphone number to call in case of emergency"), //
		/*
		 * Expenses
		 */
		NEWBORN_SCREENING("newborn screening costs (Boolean)"), //
		NEWBORN_SCREENING_COSTS("newborn screening costs"), //
		BIRTH_REGISTRATION("birth registration costs (Boolean)"), //
		BIRTH_REGISTRATION_COSTS("birth registration costs"), //
		DELIVERY("delivery costs (Boolean)"), //
		DELIVERY_COSTS("delivery costs"), //
		MEDICINE("medicine costs (Boolean)"), //
		MEDICINE_COSTS("medicine costs"), //
		TRANSPORTATION("transportation costs (Boolean)"), //
		TRANSPORTATION_COSTS("transportation costs"), //
		OTHER("other costs (Boolean)"), //
		OTHER_COSTS("other costs"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCBirthPlanConcepts(String conceptName) {
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
	 * This is a member of the {@link MCBirthPlanConcepts#BIRTH_PLAN} concept.
	 * 
	 * @author Bren
	 */
	public static enum MCMothersNeedsConcepts implements CachedConceptId {
		/** Parent observation group containing the mother's needs details */
		MOTHERS_NEEDS("mother's needs"), //
		SANITARY_NAPKIN("sanitary napkin"), //
		ALCOHOL("rubbing alcohol"), //
		CLOTHES("change of clothes"), //
		BLANKET("blanket"), //
		OTHER("others, mother's needs"), //
		CASH("extra money"), //
		TOWEL("towel"), //
		SANDALS("flip flops"), //
		TOILET_PAPER("toilet paper"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCMothersNeedsConcepts(String conceptName) {
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
	 * This is a member of the {@link MCBirthPlanConcepts#BIRTH_PLAN} concept.
	 * 
	 * @author Bren
	 */
	public static enum MCChildsNeedsConcepts implements CachedConceptId {
		/** Parent observation group containing the child's needs details */
		CHILDS_NEEDS("child's needs"), //
		CLOTHES("child's clothes"), //
		DIAPER("diaper"), //
		OTHER("others, child's needs"), //
		MITTENS("mittens"), //
		BOTTLE("bottle"), //
		BLANKET("blanket"), //
		SAFETY_PIN("safety pin"), //
		SOAP("soap"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCChildsNeedsConcepts(String conceptName) {
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
	 * Delivery report record
	 * <p>
	 * This concept is a child of the {@link ProgramConcepts#MATERNALCARE} observation.
	 * 
	 * @author Bren
	 */
	public static enum MCDeliveryReportConcepts implements CachedConceptId {
		/** Parent observation group containing the child's needs details */
		DELIVERY_REPORT("Delivery"), //
		DELIVERY_DATE("delivery of baby, date"), //
		BIRTH_ATTENDANT("delivery assistant"), //
		DATE_INITIATED_BREASTFEEDING("date initiated breastfeeding"), //
		TIME_INITIATED_BREASTFEEDING("time initiated breastfeeding"), //

		/** P* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_PARA("para"), //

		/** F* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_FT("number of full term pregnancies"), //

		/** P* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_PT("number of preterm births"), //

		/** A* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_AM("number of abortions/miscarriages"), //

		/** L* (Obstetric score), (numeric) */
		OBSTETRIC_SCORE_LC("total number of living children"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCDeliveryReportConcepts(String conceptName) {
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
	 * Post-partum visit concepts for storing post partum visit record information.
	 * <p>
	 * This concept is a child of the {@link ProgramConcepts#MATERNALCARE} observation.
	 * 
	 * @author Bren
	 */
	public static enum MCPostPartumVisitRecordConcepts implements CachedConceptId {
		/** Parent observation group containing the post-partum visit record details */
		POSTPARTUM_VISIT_RECORD("Post-Partum Record"),

		/** post-partum visit date, (Date) */
		VISIT_DATE("current date"), //

		/** post-partum visit type, (coded) */
		VISIT_TYPE("post-partum visit type"), //

		/** text-entry findings (Text) */
		BREAST_EXAM_FINDINGS("breast examination findings"), //

		/** text-entry findings (Text) */
		UTERUS_EXAM_FINDINGS("uterus examination findings"), //

		/** text-entry findings (Text) */
		VAGNIAL_EXAM_FINDINGS("vaginal discharge examination findings"), //

		/** text-entry findings (Text) */
		EPISIOTOMY_EXAM_FINDINGS("laceration/episiotomy inspection findings"), //

		/** text-entry findings (Text) */
		OTHER_FINDINGS("other findings"), //

		/** Checkbox question (coded) */
		BREASTFED_WITHIN_HOUR("Breastfeeding initiated within one hour"), //

		/** Checkbox question (coded) */
		BREASTFED_EXCLUSIVELY("Baby still exlusively breastfeeding at time of consult"), //

		/** text entry notes (Text) */
		IMMUNIZATION_NOTES("consultation notes: immunization"), //

		/** text entry notes (Text) */
		NUTRITION_NOTES("consultation notes: nutrition"), //

		/** text entry notes (Text) */
		HYGIENE_NOTES("consultation notes: personal hygiene"), //

		/** text entry notes (Text) */
		ADVICE_GIVEN("consultation notes: advice given"), //

		/** text entry notes (Text) */
		REMARKS("remarks on post-partum checkup"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCPostPartumVisitRecordConcepts(String conceptName) {
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
	 * Post-partum events checklist for storing post partum visit record information.
	 * <p>
	 * This concept is a child of the {@link MCPostPartumVisitRecordConcepts#POSTPARTUM_VISIT_RECORD} observation.
	 * 
	 * @author Bren
	 */
	public static enum MCPostPartumEventsConcepts implements CachedConceptId {
		/** Parent observation group containing the post-partum visit record details */
		POSTPARTUM_CHECKLIST("post-partum event checklist"), //

		/** Boolean checkbox item (Coded) */
		VAGINAL_INFECTION("postpartum vaginal infection"), //

		/** Boolean checkbox item (Coded) */
		VAGINAL_BLEEDING("postpartum vaginal bleeding"), //

		/** Boolean checkbox item (Coded) */
		FEVER_OVER_38("postpartum fever greater than 38 degrees celsius"), //

		/** Boolean checkbox item (Coded) */
		PALLOR("postpartum pallor"), //

		/** Boolean checkbox item (Coded) */
		CORD_NORMAL("baby's cord unremarkable postpartum"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCPostPartumEventsConcepts(String conceptName) {
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
	 * Encapsulates patient consult status information.
	 * 
	 * @author Bren
	 */
	public static enum MCPatientConsultStatus implements CachedConceptId {
		/**
		 * Patient consult status Parent observation group containing the consult status changes
		 */
		PATIENT_CONSULT_STATUS("Patient Consult Status, Maternal Care"), //

		/** Date of status change, (Date) */
		DATE_OF_CHANGE("current date"), //

		/** The consult status (one of MaternalCareProgramStates) */
		STATUS("consult status"), //

		/** Reason for ending program - filled in only for ENDED status - one of MCReasonForEndingMCProgram (coded) */
		REASON_FOR_ENDING("Reason for ending the maternal care program"), //

		/** Remarks (Misc/Text) */
		REMARKS("Remarks"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCPatientConsultStatus(String conceptName) {
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
	 * Following are generic services concepts. Depending on the service type, other concept observations may be added.
	 */
	public enum MCServiceRecordConcepts implements CachedConceptId {
		/** This would be the group parent and the coded value would indicate the type of service, one of MCServiceTypes (coded) */
		SERVICE_TYPE("Maternal Care Services"), //

		/** The date the service was administered / given */
		DATE_ADMINISTERED("Date Administered"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCServiceRecordConcepts(String conceptName) {
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
	 * Additional concepts (on top of MCServiceRecordConcepts)
	 * 
	 * @author Bren
	 */
	public enum VitaminAConcepts implements CachedConceptId {
		VISIT_TYPE("visit type, vitamin A, maternal care services"), //
		SERVICE_SOURCE("service source, vitamin A, maternal care services"), //
		REMARKS("remarks, vitamin A, maternal care services");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private VitaminAConcepts(String conceptName) {
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
	 * Additional concepts (on top of MCServiceRecordConcepts)
	 * 
	 * @author Bren
	 */
	public enum IronSupplementationConcepts implements CachedConceptId {
		VISIT_TYPE("visit type, iron supplementation, maternal care services"), //
		SERVICE_SOURCE("service source, iron supplementation, maternal care services"), //
		DRUG_FORMULARY("drug preparation, ferrous sulfate, maternal care services"), //
		QUANTITY("quantity, ferrous sulfate, maternal care services"), //
		REMARKS("remarks, ferrous sulfate, maternal care services");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private IronSupplementationConcepts(String conceptName) {
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
	 * Additional concepts (on top of MCServiceRecordConcepts)
	 * 
	 * @author Bren
	 */
	public enum DewormingConcepts implements CachedConceptId {
		VISIT_TYPE("visit type, deworming, maternal care services"), //
		SERVICE_SOURCE("service source, deworming, maternal care services"), //
		REMARKS("remarks, deworming, maternal care services");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private DewormingConcepts(String conceptName) {
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

	public static enum MCIEOptions implements CachedConceptId {
		/*
		 * Answers for "External Genitalia"
		 */
		NORMAL("normal"), OTHERS("other non-coded"),

		/*
		 * Answers for "Vagina"
		 */
		NULLIPAROUS("nulliparous"), PAROUS("parous"),

		/*
		 * Answers for "Cervix State"
		 */
		CLOSED("closed"), OPEN("open"),

		/*
		 * Answers for "Cervix Consistency"
		 */
		SOFT("soft cervix"), FIRM("firm cervix"),

		/*
		 * Answers for "Adnexal Mass / tenderness" and "Bloody Show"
		 */
		POSITIVE("positive"), NEGATIVE("negative"),

		/*
		 * Answers for "Pelvimetry"
		 */
		ADEQUATE("adequate"), INADEQUATE("inadequate"),

		/*
		 * Answers for "Membranes"
		 */
		INTACT("intact membranes"), RUPTURED("ruptured membranes"),

		/*
		 * Answers for "Wound Dehiscence"
		 */
		ABSENT("absent"), PRESENT("present"), //

		/*
		 * Answers for "sutures, post-delivery"
		 */
		SUTURES_INTACT("intact"), SUTURES_NOT_INTACT("not intact"),

		/*
		 * Answers for "vaginal discharge, post-delivery" or "vaginal bleeding, post-delivery" (except NONE)
		 */
		NONE("none"), MINIMAL("minimal"), MODERATE("moderate"), SEVERE("severe");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCIEOptions(String conceptName) {
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
	 * Maternal care pregnanacy result options: Already defined in concept dictionary.
	 */
	public static enum MCPregnancyTestResultsOptions implements CachedConceptId {
		/** Misc/Boolean answer */
		POSITIVE("pregnancy test positive"), //

		/** Misc/Boolean answer */
		NEGATIVE("pregnancy test negative"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCPregnancyTestResultsOptions(String conceptName) {
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
	 * Blood type result options.
	 */
	public static enum MCBloodTypeOptions implements CachedConceptId {
		A_POSITIVE("Blood Type A"), //
		B_POSITIVE("Blood Type B"), //
		AB_POSITIVE("Blood Type AB"), //
		O_POSITIVE("Blood Type O"), //
		UNKNOWN("unknown"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCBloodTypeOptions(String conceptName) {
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
	 * Rh factor options.
	 */
	public static enum MCRhesusFactorOptions implements CachedConceptId {
		POSITIVE("positive"), //
		NEGATIVE("negative"), //
		UNKNOWN("unknown"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCRhesusFactorOptions(String conceptName) {
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
	 * Convenience enums
	 */

	/**
	 * Enums represents the different stages of maternity.
	 * 
	 * @author Bren
	 */
	public static enum MCMaternityStage {
		FIRST_TRIMESTER("chits.program.MATERNALCARE.first.trimester"), //
		SECOND_TRIMESTER("chits.program.MATERNALCARE.second.trimester"), //
		THIRD_TRIMESTER("chits.program.MATERNALCARE.third.trimester"), //
		POSTPARTUM("chits.program.MATERNALCARE.postpartum");

		private final String key;

		private MCMaternityStage(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	/**
	 * Service types: answers the MCServiceRecordConcepts.SERVICE_TYPE question.
	 * 
	 * @author Bren
	 */
	public enum MCServiceTypes implements CachedConceptId {
		VITAMIN_A_SUPPLEMENTATION("Vitamin A Supplementation"), //
		DEWORMING("Deworming"), //
		FERROUS_SULFATE("Ferrous Sulfate"), //
		DENTAL_RECORD("Dental Service");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCServiceTypes(String conceptName) {
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
	 * Reasons for ending the maternal care program.
	 * 
	 * @author Bren
	 */
	public enum MCReasonForEndingMCProgram implements CachedConceptId {
		COMPLETED("patient completed maternal care"), //
		MOVED("patient moved to a new location"), //
		GONE("patient cannot be located"), //
		DECEASED("patient died of maternal causes"), //
		DECEASED_NONMATERNAL("patient died of non-maternal causes"), //
		HAD_ABORTION("patient had an abortion"), //
		EARLY_TERMINATION("patient had an early termination of pregnancy"), //
		DIAGNOSED_HMOLE("patient was diagnosed with an H-mole"), //
		OTHER("other non-coded");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MCReasonForEndingMCProgram(String conceptName) {
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
