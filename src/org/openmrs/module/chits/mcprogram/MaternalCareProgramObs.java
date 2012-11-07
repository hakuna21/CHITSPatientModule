package org.openmrs.module.chits.mcprogram;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCBirthPlanConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDeliveryReportConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCFamilyMedicalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCIERecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMaternityStage;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMedicalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMenstrualHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPatientConsultStatus;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPersonalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumVisitRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPrenatalVisitRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MaternalCareProgramStates;
import org.openmrs.module.chits.obs.DatetimeGroupObsComparator;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * The maternal care program all-encapsulating parent observation.
 * 
 * @author Bren
 */
public class MaternalCareProgramObs extends GroupObs {
	/** The lazy-loaded Obstetric History record */
	private ObstetricHistory obstetricHistory;

	/** The lazy-loaded Menstrual History record */
	private MenstrualHistory menstrualHistory;

	/** The lazy-loaded patient medical history record */
	private PatientMedicalHistory patientMedicalHistory;

	/** The lazy-loaded patient's family medical history record */
	private FamilyMedicalHistory familyMedicalHistory;

	/** The lazy-loaded person history record */
	private PersonalHistory personalHistory;

	/** The lazy-loaded birth plan chart */
	private BirthPlanChart birthPlanChart;

	/** The lazy-loaded delivery report */
	private DeliveryReport deliveryReport;

	/** Stores the patient program associated with the data */
	private final PatientProgram patientProgram;

	/** Flag for setting the 'will see physician' attribute on the patient */
	private boolean needsToSeePhysician;

	/** Indicates if this form is read-only */
	private final boolean readOnly;

	/**
	 * Prepares this wrapper around the observation instance representing the patient's currently active maternal care program record.
	 * 
	 * @param patient
	 *            The patient to initialize the maternal care program observation group over.
	 * @throws APIAuthenticationException
	 *             If the given patient is not currently active in a maternal care program.
	 */
	public MaternalCareProgramObs(Patient patient) throws APIAuthenticationException {
		super(MaternalCareUtil.getObsForActiveMaternalCareProgramOrFail(patient));
		this.patientProgram = Functions.getActivePatientProgram(patient, ProgramConcepts.MATERNALCARE);

		// when attached to the currently active maternal care chart program, the chart is updatable
		this.readOnly = false;
	}

	/**
	 * Prepares this wrapper around the observation instance representing an existing maternal care patient program observation record.
	 * 
	 * @param obs
	 *            The observation corresponding to the maternal care patient program.
	 * @throws APIAuthenticationException
	 *             If the given observation does not have a corresponding maternal care patient program entry
	 */
	public MaternalCareProgramObs(Obs maternalCareProgramObs) throws APIAuthenticationException {
		super(maternalCareProgramObs);

		// lookup the patient program by the UUID of the observation
		this.patientProgram = Context.getProgramWorkflowService().getPatientProgramByUuid(maternalCareProgramObs.getUuid());

		// when attached to a historic maternal care program observation, the chart is automatically read-only
		this.readOnly = true;

		// if patient program not found, then throw an error
		if (this.patientProgram == null) {
			throw new APIAuthenticationException("chits.program.MATERNALCARE.not.enrolled");
		}
	}

	/**
	 * Lazy-loaded getter for the {@link ObstetricHistory} bean.
	 * 
	 * @return the {@link ObstetricHistory} bean;
	 */
	public ObstetricHistory getObstetricHistory() {
		if (obstetricHistory == null) {
			final Obs mcProgramObs = super.getObs();
			Obs obstetricHistoryObs = Functions.observation(mcProgramObs, MCObstetricHistoryConcepts.OBSTETRIC_HISTORY);

			if (obstetricHistoryObs == null) {
				// create a new observation and add to the maternal care program
				obstetricHistoryObs = PatientConsultEntryForm.newObs(MCObstetricHistoryConcepts.OBSTETRIC_HISTORY, ObsUtil.PATIENT_CONTEXT.get());
				mcProgramObs.addGroupMember(obstetricHistoryObs);
			}

			// encapsulate and store the obstetric history observation
			this.obstetricHistory = new ObstetricHistory(obstetricHistoryObs);
		}

		return obstetricHistory;
	}

	/**
	 * Lazy-loaded getter for the {@link MenstrualHistory} bean.
	 * 
	 * @return the {@link MenstrualHistory} bean;
	 */
	public MenstrualHistory getMenstrualHistory() {
		if (menstrualHistory == null) {
			final Obs mcProgramObs = super.getObs();
			Obs menstrualHistoryObs = Functions.observation(mcProgramObs, MCMenstrualHistoryConcepts.MENSTRUAL_HISTORY);

			if (menstrualHistoryObs == null) {
				// create a new observation and add to the maternal care program
				menstrualHistoryObs = PatientConsultEntryForm.newObs(MCMenstrualHistoryConcepts.MENSTRUAL_HISTORY, ObsUtil.PATIENT_CONTEXT.get());
				mcProgramObs.addGroupMember(menstrualHistoryObs);
			}

			// encapsulate and store the menstrual history observation
			this.menstrualHistory = new MenstrualHistory(menstrualHistoryObs);
		}

		return menstrualHistory;
	}

	/**
	 * Lazy-loaded getter for the {@link PatientMedicalHistory} bean.
	 * 
	 * @return the {@link PatientMedicalHistory} bean;
	 */
	public PatientMedicalHistory getPatientMedicalHistory() {
		if (patientMedicalHistory == null) {
			final Obs mcProgramObs = super.getObs();
			Obs patientMedicalHistoryObs = Functions.observation(mcProgramObs, MCMedicalHistoryConcepts.MEDICAL_HISTORY);

			if (patientMedicalHistoryObs == null) {
				// create a new observation and add to the maternal care program
				patientMedicalHistoryObs = PatientConsultEntryForm.newObs(MCMedicalHistoryConcepts.MEDICAL_HISTORY, ObsUtil.PATIENT_CONTEXT.get());
				mcProgramObs.addGroupMember(patientMedicalHistoryObs);
			}

			// encapsulate and store the patient medical history observation
			this.patientMedicalHistory = new PatientMedicalHistory(patientMedicalHistoryObs);
		}

		return patientMedicalHistory;
	}

	/**
	 * Lazy-loaded getter for the {@link FamilyMedicalHistory} bean.
	 * 
	 * @return the {@link FamilyMedicalHistory} bean;
	 */
	public FamilyMedicalHistory getFamilyMedicalHistory() {
		if (familyMedicalHistory == null) {
			final Obs mcProgramObs = super.getObs();
			Obs familyMedicalHistoryObs = Functions.observation(mcProgramObs, MCFamilyMedicalHistoryConcepts.FAMILY_MEDICAL_HISTORY);

			if (familyMedicalHistoryObs == null) {
				// create a new observation and add to the maternal care program
				familyMedicalHistoryObs = PatientConsultEntryForm.newObs(MCFamilyMedicalHistoryConcepts.FAMILY_MEDICAL_HISTORY, ObsUtil.PATIENT_CONTEXT.get());
				mcProgramObs.addGroupMember(familyMedicalHistoryObs);
			}

			// encapsulate and store the family medical history
			this.familyMedicalHistory = new FamilyMedicalHistory(familyMedicalHistoryObs);
		}

		return familyMedicalHistory;
	}

	/**
	 * Lazy-loaded getter for the {@link PersonalHistory} bean.
	 * 
	 * @return the {@link PersonalHistory} bean;
	 */
	public PersonalHistory getPersonalHistory() {
		if (personalHistory == null) {
			final Obs mcProgramObs = super.getObs();
			Obs personalHistoryObs = Functions.observation(mcProgramObs, MCPersonalHistoryConcepts.PERSONAL_HISTORY);

			if (personalHistoryObs == null) {
				// create a new observation and add to the maternal care program
				personalHistoryObs = PatientConsultEntryForm.newObs(MCPersonalHistoryConcepts.PERSONAL_HISTORY, ObsUtil.PATIENT_CONTEXT.get());
				mcProgramObs.addGroupMember(personalHistoryObs);
			}

			// encapsulate and store the personal history
			this.personalHistory = new PersonalHistory(personalHistoryObs);
		}

		return personalHistory;
	}

	/**
	 * Lazy-loaded getter for the {@link BirthPlanChart} bean.
	 * 
	 * @return the {@link BirthPlanChart} bean;
	 */
	public BirthPlanChart getBirthPlanChart() {
		if (birthPlanChart == null) {
			final Obs mcProgramObs = super.getObs();
			Obs birthPlanChartObs = Functions.observation(mcProgramObs, MCBirthPlanConcepts.BIRTH_PLAN);

			if (birthPlanChartObs == null) {
				// create a new observation and add to the maternal care program
				birthPlanChartObs = PatientConsultEntryForm.newObs(MCBirthPlanConcepts.BIRTH_PLAN, ObsUtil.PATIENT_CONTEXT.get());
				mcProgramObs.addGroupMember(birthPlanChartObs);
			}

			// encapsulate and store the birth plan chart
			this.birthPlanChart = new BirthPlanChart(birthPlanChartObs);
		}

		return birthPlanChart;
	}

	/**
	 * Lazy-loaded getter for the {@link DeliveryReport} bean.
	 * 
	 * @return the {@link DeliveryReport} bean;
	 */
	public DeliveryReport getDeliveryReport() {
		if (deliveryReport == null) {
			final Obs mcProgramObs = super.getObs();
			Obs deliveryReportObs = Functions.observation(mcProgramObs, MCDeliveryReportConcepts.DELIVERY_REPORT);

			if (deliveryReportObs == null) {
				// create a new observation and add to the maternal care program
				deliveryReportObs = PatientConsultEntryForm.newObs(MCDeliveryReportConcepts.DELIVERY_REPORT, ObsUtil.PATIENT_CONTEXT.get());
				mcProgramObs.addGroupMember(deliveryReportObs);
			}

			// encapsulate and store the delivery report
			this.deliveryReport = new DeliveryReport(deliveryReportObs);
		}

		return deliveryReport;
	}

	/**
	 * Override to propagate into lazy-loaded attributes.
	 */
	@Override
	public void storePersonAndAudit(Person person) {
		// store into internal observations
		super.storePersonAndAudit(person);

		// propagate to lazy-loaded attributes
		if (obstetricHistory != null) {
			obstetricHistory.storePersonAndAudit(person);
		}

		if (menstrualHistory != null) {
			menstrualHistory.storePersonAndAudit(person);
		}

		if (patientMedicalHistory != null) {
			patientMedicalHistory.storePersonAndAudit(person);
		}

		if (familyMedicalHistory != null) {
			familyMedicalHistory.storePersonAndAudit(person);
		}

		if (personalHistory != null) {
			personalHistory.storePersonAndAudit(person);
		}

		if (birthPlanChart != null) {
			birthPlanChart.storePersonAndAudit(person);
		}

		if (deliveryReport != null) {
			deliveryReport.storePersonAndAudit(person);
		}
	}

	public boolean isNeedsToSeePhysician() {
		return needsToSeePhysician;
	}

	public void setNeedsToSeePhysician(boolean needsToSeePhysician) {
		this.needsToSeePhysician = needsToSeePhysician;
	}

	/**
	 * Calculates Estimated Date of Confinement (EDC) based on the last menstrual period entry
	 * <p>
	 * Computed as: LMP + 9 months & 7 days
	 * 
	 * @return The EDC value
	 */
	public Date getEstimatedDateOfConfinement() {
		final Obs lmpObs = getObstetricHistory().getMember(MCObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD);
		if (lmpObs == null || lmpObs.getValueDatetime() == null) {
			return null;
		}

		// discard time component when calculating EDC
		final Calendar c = Calendar.getInstance();
		c.setTime(DateUtil.stripTime(lmpObs.getValueDatetime()));
		c.add(Calendar.MONTH, 9);
		c.add(Calendar.DATE, 7);

		// return estimated date of confinement
		return c.getTime();
	}

	/**
	 * Gets the current (latest) state of the patient in this maternal care program.
	 * 
	 * @return The current (latest) state of the patient in the maternal care program.
	 */
	public MaternalCareProgramStates getCurrentState() {
		final PatientConsultStatus status = getPatientConsultStatus();
		if (status != null && status.getStatus() != null) {
			return status.getStatus();
		} else {
			// assume 'NEW'
			return MaternalCareProgramStates.NEW;
		}
	}

	/**
	 * Utility method to return the last menstrual period (stripped of the time component).
	 * 
	 * @return The last menstural period date, or null if not found.
	 */
	Calendar getLastMenstrualPeriod() {
		final Obs lmpObs = getObstetricHistory().getMember(MCObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD);
		if (lmpObs == null || lmpObs.getValueDatetime() == null) {
			// unable to determine maternity stage
			return null;
		}

		// use stripped time values when comparing
		final Calendar c = Calendar.getInstance();
		c.setTime(DateUtil.stripTime(lmpObs.getValueDatetime()));

		// return a calendar instance representing the last menstrual period
		return c;
	}

	/**
	 * Calculates the end date of the first trimester
	 * 
	 * @return The end date of the first trimester
	 */
	public Date getEndOfFirstTrimester() {
		final Calendar c = getLastMenstrualPeriod();
		if (c == null) {
			// unable to determine maternity stage
			return null;
		}

		// 1st Trimester - add 12 weeks (84 days) to LMP
		c.add(Calendar.DATE, 84);
		return c.getTime();
	}

	/**
	 * Calculates the end date of the second trimester
	 * 
	 * @return The end date of the second trimester
	 */
	public Date getEndOfSecondTrimester() {
		final Calendar c = getLastMenstrualPeriod();
		if (c == null) {
			// unable to determine maternity stage
			return null;
		}

		// 2nd Trimester - add 27 weeks (189 days) to LMP
		c.add(Calendar.DATE, 189);
		return c.getTime();
	}

	/**
	 * This is the same as the estimated date of confinement.
	 * 
	 * @return The end date of the third trimester
	 */
	public Date getEndOfThirdTrimester() {
		return getEstimatedDateOfConfinement();
	}

	/**
	 * Calculates the end date of postpartum.
	 * 
	 * @return The end date of postpartum
	 */
	public Date getEndOfPostpartum() {
		final Date edc = getEstimatedDateOfConfinement();
		if (edc == null) {
			return null;
		}

		// Postpartum - add 6 weeks (42 days) to EDC
		final Calendar c = Calendar.getInstance();
		c.setTime(edc);
		c.add(Calendar.DATE, 42);
		return c.getTime();
	}

	/**
	 * Calculate the maternity stage for the given date:
	 * <ul>
	 * <li>1st Trimester - add 12 weeks (84 days) to LMP</li>
	 * <li>2nd Trimester - add 27 weeks (189 days) to LMP</li>
	 * <li>3rd Trimester - EDC</li>
	 * <li>Postpartum - add 6 weeks (42 days) to EDC</li>
	 * </ul>
	 * 
	 * @param date
	 *            The date to check the maternity stage of
	 * @return The current maternity stage, or null if not known or not applicable.
	 */
	public MCMaternityStage getMaternityStageOn(Date date) {
		final Calendar c = getLastMenstrualPeriod();
		if (c == null) {
			// unable to determine maternity stage
			return null;
		}

		// use stripped time values when comparing
		final Date testDate = DateUtil.stripTime(date);

		// is date before LMP?
		if (testDate.before(c.getTime())) {
			// given date is before the last menstrual period; this date is not applicable
			return null;
		}

		// 1st Trimester - add 12 weeks (84 days) to LMP
		c.add(Calendar.DATE, 84);
		if (testDate.before(c.getTime())) {
			// this is within the first trimester
			return MCMaternityStage.FIRST_TRIMESTER;
		}

		// 2nd Trimester - add 27 weeks (189 days) to LMP/li>
		c.add(Calendar.DATE, 189 - 84);
		if (testDate.before(c.getTime())) {
			// this is within the second trimester
			return MCMaternityStage.SECOND_TRIMESTER;
		}

		// 3rd Trimester - EDC
		c.setTime(getEstimatedDateOfConfinement());
		if (testDate.before(c.getTime())) {
			// this is within the third trimester
			return MCMaternityStage.THIRD_TRIMESTER;
		}

		// Postpartum - add 6 weeks (42 days) to EDC
		c.add(Calendar.DATE, 42);
		if (testDate.before(c.getTime())) {
			// this is within the postpartum period
			return MCMaternityStage.POSTPARTUM;
		}

		// after postpartum; date is out of range
		return null;
	}

	/**
	 * Gets the visits keyed by the maternity stage.
	 * <p>
	 * NOTE: This only includes records entered within the first, second, and third trimesters. Any records outside these ranges will not be included even if
	 * encoded into the program.
	 * 
	 * @return The prenatal visit records keyed by maternity stage.
	 */
	public Map<MCMaternityStage, List<PrenatalVisitRecord>> getLatestPrenatalVisits() {
		// get end dates for each trimester
		final Date firstTrimesterEndDate = getEndOfFirstTrimester();
		final Date secondTrimesterEndDate = getEndOfSecondTrimester();
		final Date thirdTrimesterEndDate = getEndOfThirdTrimester();

		// organize the prenatal visits into a map keyed by the corresponding trimester maternity stage
		final Map<MCMaternityStage, List<PrenatalVisitRecord>> latestPrenatalVisits = new HashMap<MCMaternityStage, List<PrenatalVisitRecord>>();
		latestPrenatalVisits.put(MCMaternityStage.FIRST_TRIMESTER, new ArrayList<PrenatalVisitRecord>());
		latestPrenatalVisits.put(MCMaternityStage.SECOND_TRIMESTER, new ArrayList<PrenatalVisitRecord>());
		latestPrenatalVisits.put(MCMaternityStage.THIRD_TRIMESTER, new ArrayList<PrenatalVisitRecord>());

		for (Obs prenatalObs : Functions.observations(getObs(), MCPrenatalVisitRecordConcepts.PRENATAL_VISIT_RECORD)) {
			final Obs visitDateObs = Functions.observation(prenatalObs, MCPrenatalVisitRecordConcepts.VISIT_DATE);
			if (visitDateObs != null && visitDateObs.getValueDatetime() != null) {
				final Date visitDate = visitDateObs.getValueDatetime();
				if (visitDate.before(firstTrimesterEndDate)) {
					// store in first trimester visits
					latestPrenatalVisits.get(MCMaternityStage.FIRST_TRIMESTER).add(new PrenatalVisitRecord(prenatalObs));
				} else if (visitDate.before(secondTrimesterEndDate)) {
					// store in second trimester visits
					latestPrenatalVisits.get(MCMaternityStage.SECOND_TRIMESTER).add(new PrenatalVisitRecord(prenatalObs));
				} else if (visitDate.before(thirdTrimesterEndDate)) {
					// store in third trimester visits
					latestPrenatalVisits.get(MCMaternityStage.THIRD_TRIMESTER).add(new PrenatalVisitRecord(prenatalObs));
				}
			}
		}

		// sort each by descending visit date so that the first entry in each list will be the 'latest' visit
		final Comparator<PrenatalVisitRecord> visitDateComparator = new DatetimeGroupObsComparator<PrenatalVisitRecord>(
				MCPrenatalVisitRecordConcepts.VISIT_DATE);
		for (List<PrenatalVisitRecord> visits : latestPrenatalVisits.values()) {
			Collections.sort(visits, visitDateComparator);
			Collections.reverse(visits);
		}

		// send back the latest prenatal visits grouped by trimester
		return latestPrenatalVisits;
	}

	/**
	 * Returns lists of danger signs and medical conditions keyed by visit date.
	 * 
	 * @return List of danger signs and medical conditions on record keyed by visit date.
	 */
	public Map<Date, List<Obs>> getDangerSignsAndMedicalConditionsByVisitDate() {
		final Concept trueConcept = Functions.trueConcept();
		final Map<Date, List<Obs>> dangerSigns = new TreeMap<Date, List<Obs>>();
		for (Obs prenatalObs : Functions.observations(getObs(), MCPrenatalVisitRecordConcepts.PRENATAL_VISIT_RECORD)) {
			final PrenatalVisitRecord visit = new PrenatalVisitRecord(prenatalObs);
			final Date visitDate = visit.getVisitDate();
			List<Obs> signs = dangerSigns.get(visitDate);
			if (signs == null) {
				signs = new ArrayList<Obs>();
			}

			for (Obs obs : visit.getDangerSigns().getObs().getGroupMembers()) {
				if (trueConcept.equals(obs.getValueCoded())) {
					// this danger sign is ticked, add to the list
					signs.add(obs);
				}
			}

			for (Obs obs : visit.getNewMedicalConditions().getObs().getGroupMembers()) {
				if (trueConcept.equals(obs.getValueCoded())) {
					// this new medical condition is ticked, add to the list
					signs.add(obs);
				}
			}

			if (!signs.isEmpty()) {
				// ensure these list of items are in the danger signs map
				dangerSigns.put(visitDate, signs);
			}
		}

		// send back summarized results
		return dangerSigns;
	}

	/**
	 * Convenience method for getting the list of all prenatal visits in this program record sorted by visit date.
	 * 
	 * @return prenatal visits on record for this program sorted by visit date.
	 */
	public List<PrenatalVisitRecord> getPrenatalVisits() {
		// get the latest visit by visit date
		final List<PrenatalVisitRecord> prenatalVisitsRecords = new ArrayList<PrenatalVisitRecord>();

		for (Obs prenatalVisitObs : Functions.observations(getObs(), MCPrenatalVisitRecordConcepts.PRENATAL_VISIT_RECORD)) {
			prenatalVisitsRecords.add(new PrenatalVisitRecord(prenatalVisitObs));
		}

		if (!prenatalVisitsRecords.isEmpty()) {
			// sort by visit date to get the latest
			Collections.sort(prenatalVisitsRecords, new DatetimeGroupObsComparator<PrenatalVisitRecord>(MCPrenatalVisitRecordConcepts.VISIT_DATE));
		}

		// send back the sorted prenatal visit records
		return prenatalVisitsRecords;
	}

	/**
	 * Convenience method for getting the list of all post-partum visits in this program record sorted by visit date.
	 * 
	 * @return post-partum visits on record for this program sorted by visit date.
	 */
	public List<PostPartumVisitRecord> getPostPartumVisits() {
		// get the latest visit by visit date
		final List<PostPartumVisitRecord> postPartumVisitsRecords = new ArrayList<PostPartumVisitRecord>();

		for (Obs postPartumVisitObs : Functions.observations(getObs(), MCPostPartumVisitRecordConcepts.POSTPARTUM_VISIT_RECORD)) {
			postPartumVisitsRecords.add(new PostPartumVisitRecord(postPartumVisitObs));
		}

		if (!postPartumVisitsRecords.isEmpty()) {
			// sort by visit date to get the latest
			Collections.sort(postPartumVisitsRecords, new DatetimeGroupObsComparator<PostPartumVisitRecord>(MCPostPartumVisitRecordConcepts.VISIT_DATE));
		}

		// send back the sorted post-partum visit records
		return postPartumVisitsRecords;
	}

	/**
	 * Convenience method for getting the list of all internal examination visits in this program record sorted by visit date.
	 * 
	 * @return IE visits on record for this program sorted by visit date.
	 */
	public List<InternalExaminationRecord> getInternalExaminationRecords() {
		// get the latest visit by visit date
		final List<InternalExaminationRecord> ieRecords = new ArrayList<InternalExaminationRecord>();

		for (Obs ieRecordObs : Functions.observations(getObs(), MCIERecordConcepts.INTERNAL_EXAMINATION)) {
			ieRecords.add(new InternalExaminationRecord(ieRecordObs));
		}

		if (!ieRecords.isEmpty()) {
			// sort by visit date to get the latest
			Collections.sort(ieRecords, new DatetimeGroupObsComparator<InternalExaminationRecord>(MCIERecordConcepts.VISIT_DATE));
		}

		// send back the sorted internal examination records
		return ieRecords;
	}

	/**
	 * Convenience method for getting the list of all patient consult status records sorted by date of status change.
	 * 
	 * @return PatientConsultStatus records for this program sorted by visit date.
	 */
	public List<PatientConsultStatus> getAllPatientConsultStatus() {
		// get all records
		final List<PatientConsultStatus> records = new ArrayList<PatientConsultStatus>();

		for (Obs recordObs : Functions.observations(getObs(), MCPatientConsultStatus.PATIENT_CONSULT_STATUS)) {
			records.add(new PatientConsultStatus(recordObs));
		}

		if (!records.isEmpty()) {
			// sort by ascending status change date
			Collections.sort(records, new DatetimeGroupObsComparator<PatientConsultStatus>(MCPatientConsultStatus.DATE_OF_CHANGE));
		}

		// send back the sorted records
		return records;
	}

	/**
	 * Convenience method to get the current patient consult status.
	 * 
	 * @return The current patient consult status.
	 */
	public PatientConsultStatus getPatientConsultStatus() {
		final List<PatientConsultStatus> allStatuses = getAllPatientConsultStatus();
		if (!allStatuses.isEmpty()) {
			// get the latest status
			return allStatuses.get(getAllPatientConsultStatus().size() - 1);
		} else {
			// no applicable status
			return null;
		}
	}

	public PatientProgram getPatientProgram() {
		return patientProgram;
	}

	/**
	 * Records the state change by creating a PatientConsultStatus record entry. NOTE: This does not automatically save the observations so the parent obs must
	 * be saved to cascade the changes.
	 * 
	 * @param state
	 * @param date
	 * @param remarks
	 */
	public PatientConsultStatus recordStateChange(MaternalCareProgramStates state, Date date, String remarks) {
		final PatientConsultStatus status = new PatientConsultStatus();
		status.getMember(MCPatientConsultStatus.DATE_OF_CHANGE).setValueDatetime(date);
		status.getMember(MCPatientConsultStatus.DATE_OF_CHANGE).setValueText(Context.getDateFormat().format(date));
		status.getMember(MCPatientConsultStatus.REMARKS).setValueText(remarks);
		status.getMember(MCPatientConsultStatus.STATUS).setValueCoded(Functions.concept(state));
		PatientConsultEntryFormValidator.setValueCodedIntoValueText(status.getMember(MCPatientConsultStatus.STATUS));

		// add to this observation for cascade saving
		getObs().addGroupMember(status.getObs());

		// send back the created record
		return status;
	}

	/**
	 * The birth plan chart becomes View-Only when the Patient Consult Status is changed to ADMITTED, or when the Delivery Record has been filled out.
	 */
	public boolean isBirthPlanChartReadOnly() {
		final boolean hasDeliveryReport = Functions.observation(getObs(), MCDeliveryReportConcepts.DELIVERY_REPORT) != null;
		return hasDeliveryReport || MaternalCareProgramStates.ADMITTED == getCurrentState();
	}

	public boolean isReadOnly() {
		return readOnly;
	}
	
	/**
	 * Counts the number of encounters since the start of this patient program up to the given date.
	 */
	public int getVisitSequenceNumber(Date upTo) {
		// load matching encounters
		final EncounterService encounterService = Context.getEncounterService();
		final List<Encounter> encounters = encounterService.getEncounters(patientProgram.getPatient(), null,
				DateUtil.stripTime(patientProgram.getDateEnrolled()), DateUtil.midnight(upTo), null, null, null, false);
		return encounters.size();
	}
}
