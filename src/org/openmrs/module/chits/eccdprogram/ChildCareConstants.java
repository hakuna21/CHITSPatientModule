package org.openmrs.module.chits.eccdprogram;

import org.openmrs.module.chits.CachedConceptId;

/**
 * Constants used by childcare.
 */
public interface ChildCareConstants {
	/** Child care program age = 6 yrs old */
	int CHILDCARE_MAX_AGE = 6;

	/** Global property key for obtaining the low birthweight warning for the vaccination screen */
	String GP_LOW_BIRTHWEIGHT_VACCINATION_WARNING = "chits.low.birthwate.vaccination.warning";
	
	/** Global property key for obtaining the high temperature warning */
	String GP_HIGH_TEMPERATURE_WARNING = "chits.eccd.high.temperature.warning";

	/**
	 * Child care program states.
	 * 
	 * @author Bren
	 */
	public enum ChildCareProgramStates implements CachedConceptId {
		/**
		 * Indicates patient is in the child care program and has already been registered. This doubles as the concept for the observation group containing the
		 * childcare observations.
		 */
		REGISTERED("CHITS Child Care - Registered"), //
		/** Indicates patient has been removed from the child care program */
		CLOSED("CHITS Child Care - Record Closed"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private ChildCareProgramStates(String conceptName) {
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
	 * Following are child care concepts entries
	 */
	public enum ChildCareConcepts implements CachedConceptId {
		DELIVERY_INFORMATION("Child Care Delivery Information"), //
		BIRTH_LENGTH("BIRTH LENGTH (CM)"), //
		BIRTH_WEIGHT("BIRTH WEIGHT (KG)"), //
		DELIVERY_LOCATION("Planned location of delivery"), //
		METHOD_OF_DELIVERY("Method of delivery"), //
		GESTATIONAL_AGE("GESTATIONAL AGE AT BIRTH (WEEKS)"), //
		BIRTH_ORDER("Birth Order"), //
		CHILDCARE_REMARKS("Remarks"), //
		DOB_REGISTRATION("Date of Birth Registration");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private ChildCareConcepts(String conceptName) {
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
	 * Following are method of delivery concepts
	 */
	public enum MethodOfDeliveryConcepts implements CachedConceptId {
		NSD("normal spontaneous delivery"), //
		AVD("assisted vaginal delivery"), //
		BREECH("breech delivery"), //
		CS("cesarean section"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private MethodOfDeliveryConcepts(String conceptName) {
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

	public enum NewbornScreeningConcepts implements CachedConceptId {
		/** Group observation containing the newborn screening findings */
		RESULTS("Newborn Screening Results"), //
		/** Group observation containing newborn screening information */
		SCREENING_INFORMATION("Newborn Screening Information"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private NewbornScreeningConcepts(String conceptName) {
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
	 * Observations under the NewbornScreeningConcepts.SCREENING_INFORMATION observation group.
	 * 
	 * @author Bren
	 */
	public enum NewbornScreeningInformation implements CachedConceptId {
		/** The action specified for the encounter */
		ACTION("Newborn Screening Action"), //
		REPORT_DATE("Newborn Screening Report Date"), //
		SCREENING_DATE("Newborn Screening Date"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private NewbornScreeningInformation(String conceptName) {
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

	public enum NewbornScreeningResults implements CachedConceptId {
		G6PD_DEFFICIENCY("G6PD deficiency"), //
		CONG_HYPOTHYROIDISM("Congenital Hypothyroidism"), //
		CONG_GALACTOSEMIA("Congenital Galactosemia"), //
		PHENYLKETONURIA("Phenylketonuria"), //
		CONG_ADRENAL_HYPERPLASIA("Congenital Adrenal Hyperplasia"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private NewbornScreeningResults(String conceptName) {
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
	 * Represents the information within a child care vaccination record.
	 * <p>
	 * Currently, this only represents child care vaccinations, but has been designed to be able to be expanded to include adult vaccinations without
	 * interfering with the ChildCareVaccinesConcepts.
	 * 
	 * @author Bren
	 */
	public enum VaccinationConcepts implements CachedConceptId {
		/**
		 * The parent observation group containing a child care vaccination record; <br/>
		 * IMPORTANT: The 'creator' of the Obs of this record indicates who administered the vaccine.
		 */
		CHILDCARE_VACCINATION("Child Care Vaccination"), //
		/** One of the MandatoryVaccinesConcepts */
		ANTIGEN("Antigen"), //
		/** The date the vaccine was administered */
		DATE_ADMINISTERED("Date Administered"), //
		/** The health facility that administered the vaccine */
		HEALTH_FACILITY("Health Facility");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private VaccinationConcepts(String conceptName) {
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
	 * Mandatory Vaccines. These findings will be placed under the CHILDCARE_VACCINATION observation group.
	 * 
	 * @author Bren
	 */
	public enum ChildCareVaccinesConcepts implements CachedConceptId {
		BCG_24HRS("BCG (at birth) [EPI]", "BCG (at birth) [EPI]"), //
		HEPATITIS_B_24HRS("Hepatitis B 1 (at birth) [EPI]", "HEPB1 [EPI]"), //
		HEPATITIS_B_06WKS("Hepatitis B 2 (6 wks) [EPI]", "HEPB2 [EPI]"), //
		HEPATITIS_B_14WKS("Hepatitis B 3 (14 wks) [EPI]", "HEPB3 [EPI]"), //
		DPT_1_24HRS("DPT 1 (6 wks) [EPI]", "DPT1 [EPI]"), //
		DPT_2_06WKS("DPT 2 (10 wks) [EPI]", "DPT2 [EPI]"), //
		DPT_3_14WKS("DPT 3 (14 wks) [EPI]", "DPT3 [EPI]"), //
		OPV1_06WKS("OPV 1 (6 wks) [EPI]", "OPV1 [EPI]"), //
		OPV2_10WKS("OPV 2 (10 wks) [EPI]", "OPV2 [EPI]"), //
		POV3_14WKS("OPV 3 (14 wks) [EPI]", "OPV3 [EPI]"), //
		MEASLES_9MOS("Measles (9 mos) [EPI]", "MEASLES [EPI]"), //
		OTHERS("Other Antigens Given", "Other Antigens Given"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		/** The short name */
		private final String shortName;

		private ChildCareVaccinesConcepts(String conceptName, String shortName) {
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

	/**
	 * Following are child care services concepts
	 */
	public enum ChildCareServicesConcepts implements CachedConceptId {
		/** This would be the group parent and the coded value would indicate the type of service */
		CHILDCARE_SERVICE_TYPE("Child Care Service Type"), //
		/** The date the service was administered / given */
		DATE_ADMINISTERED("Date Administered"), //
		/** A free-text (value supported by drop-down lists in JSP) */
		DOSAGE("Dosage"),
		/** Child Care Service Source */
		SERVICE_SOURCE("Child Care Service Source"), //
		/** Free text remarks */
		REMARKS("Remarks"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private ChildCareServicesConcepts(String conceptName) {
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
	 * Default child care service source options.
	 * 
	 * @author Bren
	 */
	public enum ChildCareServiceSourceConcepts implements CachedConceptId {
		HEALTH_CENTER("Health Center"), //
		SPECIAL_ACTIVITY("Special Activity"), //
		OTHER_GOVT_FACILITY("Other Gov't Facility"), //
		PRIVATE("Private"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private ChildCareServiceSourceConcepts(String conceptName) {
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
	 * General 'Remarks' (currently used just for the child care services)
	 * 
	 * @author Bren
	 */
	public enum ChildCareServiceTypes implements CachedConceptId {
		VITAMIN_A_SUPPLEMENTATION("Vitamin A Supplementation"), //
		DEWORMING("Deworming"), //
		FERROUS_SULFATE("Ferrous Sulfate"), //
		DENTAL_RECORD("Dental Service");

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private ChildCareServiceTypes(String conceptName) {
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

	public enum BreastFeedingConcepts implements CachedConceptId {
		BREASTFEEDING_INFO("Breastfeeding"), //
		M1("month 1, exclusive breastfeeding"), //
		M2("month 2, exclusive breastfeeding"), //
		M3("month 3, exclusive breastfeeding"), //
		M4("month 4, exclusive breastfeeding"), //
		M5("month 5, exclusive breastfeeding"), //
		M6("month 6, exclusive breastfeeding"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private BreastFeedingConcepts(String conceptName) {
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

	public enum VitaminAServiceConcepts implements CachedConceptId {
		DOSAGE("dose, vitamin A"), //
		REMARKS("remarks, vitamin A"), //
		SERVICE_SOURCE("service source, vitamin A"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private VitaminAServiceConcepts(String conceptName) {
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

	public enum DewormingServiceConcepts implements CachedConceptId {
		MEDICATION("medication, deworming"), //
		REMARKS("remarks, deworming"), //
		SERVICE_SOURCE("service source, deworming"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private DewormingServiceConcepts(String conceptName) {
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

	public enum FerrousSulfateServiceConcepts implements CachedConceptId {
		MEDICATION("medication, ferrous sulfate"), //
		REMARKS("remarks, ferrous sulfate"), //
		SERVICE_SOURCE("service source, ferrous sulfate"); //

		/** The cached concept name and id */
		private final CachedConceptNameId conceptNameId;

		private FerrousSulfateServiceConcepts(String conceptName) {
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
