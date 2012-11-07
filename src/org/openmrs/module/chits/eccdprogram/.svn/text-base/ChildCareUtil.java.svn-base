package org.openmrs.module.chits.eccdprogram;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.Constants.ProgramConcepts;
import org.openmrs.module.chits.Constants.VisitConcepts;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.Defaulter;
import org.openmrs.module.chits.Defaulter.DueServiceInfo.ServiceType;
import org.openmrs.module.chits.PatientConsultForm;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServiceTypes;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServicesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareVaccinesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.VaccinationConcepts;
import org.openmrs.module.chits.eccdprogram.ServiceDueInfo.ServiceDueInfoType;
import org.openmrs.module.chits.eccdprogram.ServiceUtil.ServiceStatus;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.TetanusToxoidDateAdministeredConcepts;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Contains utility methods for managing and / or querying the childcare program for a patient.
 * 
 * @author Bren
 */
public class ChildCareUtil {
	public enum ImmunizationStatus {
		FIC, // all vaccines have been administered before the child's first birthday
		CIC, // all vaccines have been administered before the child's fifth birthday
		NOT_COMPLETED, // not all vaccines administered after the child's fifth birthday
		INCOMPLETE; // not all vaccines have been administered
	}

	/**
	 * Returns whether the patient's state has been marked as closed in the childcare program.
	 * 
	 * @param patient
	 *            The patient of which to check if the program state is closed
	 * @return If the program state of the given patient is closed
	 */
	public static boolean isProgramClosedFor(Patient patient) {
		// program is closed if the patient possesses the 'closed' state
		return patient != null && Functions.getPatientState(patient, ProgramConcepts.CHILDCARE, ChildCareConstants.ChildCareProgramStates.CLOSED) != null;
	}

	/**
	 * Checks the child care pre-requisites and returns true if they are met before allowing access to the childcare module, false otherwise.
	 * <p>
	 * The child care pre-requisites require the following observations to have been taken for the current visit:
	 * <ul>
	 * <li>Patient's temperature
	 * <li>Patient's body weight
	 * </ul>
	 * 
	 * @param form
	 *            The form containing the patient queue
	 * @return true if the child care prerequisites are met for the current visit to allow access to the childcare module, false otherwise
	 */
	public static boolean childCarePrerequisitesMet(PatientConsultForm form) {
		// get current encounter
		final Encounter currentEncounter = form.getPatientQueue() != null ? form.getPatientQueue().getEncounter() : null;

		// load the patient's last recorded temperature for the current visit
		Obs temperature = null;
		for (Obs vitalSigns : Functions.observations(currentEncounter, VisitConcepts.VITAL_SIGNS)) {
			temperature = Functions.observation(vitalSigns, VisitConcepts.TEMPERATURE_C);
			if (temperature != null) {
				// latest temperature found
				break;
			}
		}

		// load the patient's last recorded weight for the current visit
		final Obs weight = Functions.observation(currentEncounter, VisitConcepts.WEIGHT_KG);

		// the child care prerequisites are met only if both the temperature and weight observations are found and specified
		return (temperature != null && temperature.getValueNumeric() != null) //
				&& (weight != null && weight.getValueNumeric() != null);
	}

	/**
	 * Returns the immunizations status of the given patient.
	 * 
	 * @param patient
	 *            The patient (child) to check the immunization status of
	 * @return The immunization status of the patient.
	 */
	public static ImmunizationStatus getImmunizationStatus(Patient patient) {
		Date latestDateAdministered = null;
		final List<Concept> antigensNotAdministered = Functions.members(VaccinationConcepts.CHILDCARE_VACCINATION);
		for (Obs vaccination : Functions.observations(patient, VaccinationConcepts.CHILDCARE_VACCINATION)) {
			final Obs antigen = Functions.observation(vaccination, VaccinationConcepts.ANTIGEN);

			// remove from the set of EPI antigens not administered
			if (antigensNotAdministered.remove(antigen.getValueCoded())) {
				// this was an EPI antigen: track the age at which the last EPI antigen was administered
				Date dateAdministered = null;
				Obs dateAdministeredObs = Functions.observation(vaccination, VaccinationConcepts.DATE_ADMINISTERED);
				if (dateAdministeredObs != null) {
					// use date Administered specified by user
					dateAdministered = dateAdministeredObs.getValueDatetime();
				}

				if (dateAdministered == null) {
					// if not found (unusual case), use the observation date time (guaranteed to be non-null)
					dateAdministered = vaccination.getObsDatetime();
				}

				if (latestDateAdministered == null || latestDateAdministered.before(dateAdministered)) {
					// find the date of the last administered antigen
					latestDateAdministered = dateAdministered;
				}
			}
		}

		// remove the 'others' since these 'other' antigens don't go towards the (EPI) immunization status
		antigensNotAdministered.remove(Functions.concept(ChildCareVaccinesConcepts.OTHERS));

		if (!antigensNotAdministered.isEmpty()) {
			if (patient.getAge() == null || patient.getAge() >= 5) {
				// immunization was not completed before the child reached 5 years of age
				return ImmunizationStatus.NOT_COMPLETED;
			} else {
				// child is less than 5 years old, but not all antigens have been administered yet
				return ImmunizationStatus.INCOMPLETE;
			}
		} else {
			// calculate the age at which the last antigen was given
			final int ageAtFinalAntigen = (patient.getBirthdate() != null && latestDateAdministered != null) ? DateUtil.yearsBetween(patient.getBirthdate(),
					latestDateAdministered) : Integer.MAX_VALUE;
			if (ageAtFinalAntigen < 1) {
				// all antigens administered before 1 year from date of birth
				return ImmunizationStatus.FIC;
			} else if (ageAtFinalAntigen < 5) {
				// all antigens administered, before 5 but after 1 year of age
				return ImmunizationStatus.CIC;
			} else {
				// all antigens administered, but not before child reached 5 years of age
				return ImmunizationStatus.NOT_COMPLETED;
			}
		}
	}

	public static ChildProtectedAtBirthStatus getCPABStatus(Patient patient) {
		final Person mother = RelationshipUtil.getMother(patient);
		if (mother == null || RelationshipUtil.isNonPatient(mother)) {
			return ChildProtectedAtBirthStatus.MOTHER_NOT_LINKED;
		}

		if (patient.getBirthdate() == null) {
			// patient's birthdate not set, no way to determine CPAB status
			return ChildProtectedAtBirthStatus.NO;
		}

		// get tetanus toxoid vaccination administration dates
		Obs tt1 = Functions.observation(mother, TetanusToxoidDateAdministeredConcepts.TT1);
		Obs tt2 = Functions.observation(mother, TetanusToxoidDateAdministeredConcepts.TT2);
		Obs tt3 = Functions.observation(mother, TetanusToxoidDateAdministeredConcepts.TT3);
		Obs tt4 = Functions.observation(mother, TetanusToxoidDateAdministeredConcepts.TT4);
		Obs tt5 = Functions.observation(mother, TetanusToxoidDateAdministeredConcepts.TT5);

		// consider only tetanus vaccines administered on or before the child's birthdate
		if (tt1 == null || tt1.getValueDatetime() == null || tt1.getValueDatetime().after(patient.getBirthdate())) {
			// tt1 was administered after child's birth!
			tt1 = null;
		}

		if (tt2 == null || tt2.getValueDatetime() == null || tt2.getValueDatetime().after(patient.getBirthdate())) {
			// tt2 was administered after child's birth!
			tt2 = null;
		}

		if (tt3 == null || tt3.getValueDatetime() == null || tt3.getValueDatetime().after(patient.getBirthdate())) {
			// tt3 was administered after child's birth!
			tt3 = null;
		}

		if (tt4 == null || tt4.getValueDatetime() == null || tt4.getValueDatetime().after(patient.getBirthdate())) {
			// tt4 was administered after child's birth!
			tt4 = null;
		}

		if (tt5 == null || tt5.getValueDatetime() == null || tt5.getValueDatetime().after(patient.getBirthdate())) {
			// tt5 was administered after child's birth!
			tt5 = null;
		}

		if (tt1 == null) {
			// No tetanus toxoid shot recorded to be received by mother
			return ChildProtectedAtBirthStatus.NO;
		} else if (tt2 == null) {
			// TT2 has not yet been given
			return ChildProtectedAtBirthStatus.NO_TT2;
		} else if (tt3 == null) {
			// TT1 and TT2 given: check how long before child's birth TT2 was given
			final int daysBetweenTT2andChildsBirth = DateUtil.daysBetween(tt2.getValueDatetime(), patient.getBirthdate());
			if (daysBetweenTT2andChildsBirth < 28) {
				return ChildProtectedAtBirthStatus.TT2_UNDER_28_DAYS_BEFORE_BIRTH;
			} else if (daysBetweenTT2andChildsBirth > 180) {
				return ChildProtectedAtBirthStatus.TT2_OVER_180_DAYS_BEFORE_BIRTH;
			} else {
				return ChildProtectedAtBirthStatus.TT2_WITHIN_180_TO_28;
			}
		} else if (tt4 == null) {
			// TT1, TT2, and TT3 given: check how long TT3 was given after TT2
			final int monthsBetweenTT3andTT2 = DateUtil.daysBetween(tt2.getValueDatetime(), tt3.getValueDatetime());
			if (monthsBetweenTT3andTT2 >= 6) {
				// TT3 given more than 6 months after TT2 (NOTE: Equal to 6 means over a day after the sixth month!)
				return ChildProtectedAtBirthStatus.TT3_OVER_6MOS_AFTER_TT2;
			} else {
				// TT3 given within 6 months
				return ChildProtectedAtBirthStatus.TT3_WITHIN_6_MONTHS;
			}
		} else if (tt5 == null) {
			// TT1 to TT4 given: check how long TT4 was given after TT3
			final int yearsBetweenTT4andTT3 = DateUtil.yearsBetween(tt3.getValueDatetime(), tt4.getValueDatetime());
			if (yearsBetweenTT4andTT3 >= 1) {
				// TT4 given more than 1 year after TT3
				return ChildProtectedAtBirthStatus.TT4_OVER_1YR_AFTER_TT3;
			} else {
				// TT4 given within a year of TT3
				return ChildProtectedAtBirthStatus.TT4_WITHIN_1_YEAR;
			}
		} else {
			// TT1 to TT5 given: check how long TT5 was given after TT4
			final int yearsBetweenTT5andTT4 = DateUtil.yearsBetween(tt4.getValueDatetime(), tt5.getValueDatetime());
			if (yearsBetweenTT5andTT4 >= 1) {
				// TT5 given more than 1 year after TT4
				return ChildProtectedAtBirthStatus.TT5_OVER_1YR_AFTER_TT4;
			} else {
				// TT5 given within a year of TT4
				return ChildProtectedAtBirthStatus.TT5_WITHIN_1_YEAR;
			}
		}
	}

	/**
	 * Gets All services sorted by descending 'DATE_ADMINISTERED' value.
	 */
	public static Map<Concept, List<Obs>> getAdministeredServices(Patient patient) {
		// setup services map
		final Map<Concept, List<Obs>> services = new HashMap<Concept, List<Obs>>();

		if (patient != null) {
			for (CachedConceptId serviceType : ChildCareServiceTypes.values()) {
				services.put(Functions.concept(serviceType), new ArrayList<Obs>());
			}

			// store each service into the corresponding map entry
			final List<Obs> parentServiceObservations = Functions.observations(patient, ChildCareServicesConcepts.CHILDCARE_SERVICE_TYPE);
			for (Obs parentServiceObs : parentServiceObservations) {
				if (services.containsKey(parentServiceObs.getValueCoded())) {
					services.get(parentServiceObs.getValueCoded()).add(parentServiceObs);
				}
			}
		}

		// sort all services by descending 'date administered' value
		for (List<Obs> servicesRendered : services.values()) {
			Collections.sort(servicesRendered, new DescendingDateAdministeredComparator());
		}

		// return services keyed by child care service type
		return services;
	}

	/**
	 * Returns whether the given service observation remarks indicate that the service rendered was 'routine' according to the remarks.
	 * 
	 * @param serviceObsParent
	 *            The parent observation containing the service observation details.
	 * @return true if the rendered service was 'routine' according to the remarks.
	 */
	public static boolean isRoutineService(Obs serviceObsParent) {
		final Obs remarks = Functions.observation(serviceObsParent, ChildCareServicesConcepts.REMARKS);
		return isRoutineRemarks(remarks);
	}

	public static boolean isRoutineRemarks(Obs remarks) {
		final Concept valueCoded;
		final ConceptName conceptName;

		return remarks != null //
				&& (valueCoded = remarks.getValueCoded()) != null //
				&& (conceptName = valueCoded.getName()) != null //
				&& conceptName.getName().toLowerCase().contains("routine");
	}

	/**
	 * Returns whether the given service observation remarks indicate that the service rendered was 'therapeutic' according to the remarks.
	 * 
	 * @param serviceObsParent
	 *            The parent observation containing the service observation details.
	 * @return true if the rendered service was 'therapeutic' according to the remarks.
	 */
	public static boolean isTherapeuticService(Obs serviceObsParent) {
		final Obs remarks = Functions.observation(serviceObsParent, ChildCareServicesConcepts.REMARKS);
		return isTherapeuticRemarks(remarks);
	}

	public static boolean isTherapeuticRemarks(Obs remarks) {
		final Concept valueCoded;
		final ConceptName conceptName;
		final String name;

		return remarks != null //
				&& (valueCoded = remarks.getValueCoded()) != null //
				&& (conceptName = valueCoded.getName()) != null //
				&& ((name = conceptName.getName().toLowerCase()).contains("therapeutic") || name.contains("supplemental"));
	}

	/**
	 * Returns the parent service observation of the last administered (based on 'DATE_ADMINSITERED' value) routine service of the given service type.
	 * 
	 * @param patient
	 *            The patient to search for the rendered service
	 * @param type
	 *            The type of service to search for
	 * @return The most recent service administered parent observation with remarks indicating a 'routine' service
	 */
	public static Obs findLastRoutineService(Patient patient, ChildCareServiceTypes type) {
		// get all services rendered of this type
		final List<Obs> services = new ArrayList<Obs>();
		for (Obs service : Functions.observations(patient, ChildCareServicesConcepts.CHILDCARE_SERVICE_TYPE)) {
			if (service.getValueCoded() != null && service.getValueCoded().getConceptId().equals(type.getConceptId())) {
				services.add(service);
			}
		}

		// sort by descending 'date administered'
		Collections.sort(services, new DescendingDateAdministeredComparator());

		// find the first (most recent) 'routine' service
		for (Obs serviceParentObs : services) {
			if (isRoutineService(serviceParentObs)) {
				return serviceParentObs;
			}
		}

		// no matching rendered service
		return null;
	}

	/**
	 * Returns the parent service observation of the last administered (based on 'DATE_ADMINSITERED' value) therapeutic service of the given service type.
	 * 
	 * @param patient
	 *            The patient to search for the rendered service
	 * @param type
	 *            The type of service to search for
	 * @return The most recent service administered parent observation with remarks indicating a 'therapeutic' service
	 */
	public static Obs findLastTherapeuticService(Patient patient, ChildCareServiceTypes type) {
		// get all services rendered of this type
		final List<Obs> services = new ArrayList<Obs>();
		for (Obs service : Functions.observations(patient, ChildCareServicesConcepts.CHILDCARE_SERVICE_TYPE)) {
			if (service.getValueCoded() != null && service.getValueCoded().getConceptId().equals(type.getConceptId())) {
				services.add(service);
			}
		}

		// sort by descending 'date administered'
		Collections.sort(services, new DescendingDateAdministeredComparator());

		// find the first (most recent) 'therapeutic' service
		for (Obs serviceParentObs : services) {
			if (isTherapeuticService(serviceParentObs)) {
				return serviceParentObs;
			}
		}

		// no matching rendered service
		return null;
	}

	/**
	 * Returns the parent service observation of the last administered (based on 'DATE_ADMINSITERED' value) service of the given service type.
	 * 
	 * @param patient
	 *            The patient to search for the rendered service
	 * @param type
	 *            The type of service to search for
	 * @return The most recent service administered parent observation with remarks indicating a 'routine' service
	 */
	public static Obs findLastService(Patient patient, ChildCareServiceTypes type) {
		// get all services rendered of this type
		final List<Obs> services = new ArrayList<Obs>();
		for (Obs service : Functions.observations(patient, ChildCareServicesConcepts.CHILDCARE_SERVICE_TYPE)) {
			if (service.getValueCoded() != null && service.getValueCoded().getConceptId().equals(type.getConceptId())) {
				services.add(service);
			}
		}

		// sort by descending 'date administered'
		Collections.sort(services, new DescendingDateAdministeredComparator());

		// find the first (most recent) 'routine' service
		for (Obs serviceParentObs : services) {
			return serviceParentObs;
		}

		// no matching rendered service
		return null;
	}

	public enum ChildProtectedAtBirthStatus {
		MOTHER_NOT_LINKED(false, true), // if mother is not linked, hence, information not available
		NO(false, true), // mother linked and no record of tetanus toxoid vaccine given
		NO_TT2(false, true), //
		TT2_UNDER_28_DAYS_BEFORE_BIRTH(true, true), //
		TT2_OVER_180_DAYS_BEFORE_BIRTH(true, true), //
		TT2_WITHIN_180_TO_28(true, false), //
		TT3_OVER_6MOS_AFTER_TT2(true, true), //
		TT3_WITHIN_6_MONTHS(true, false), //
		TT4_OVER_1YR_AFTER_TT3(true, true), //
		TT4_WITHIN_1_YEAR(true, false), //
		TT5_OVER_1YR_AFTER_TT4(true, true), // , //
		TT5_WITHIN_1_YEAR(true, false);

		private boolean protectedAtBirth;
		private boolean warning;

		private ChildProtectedAtBirthStatus(boolean protectedAtBirth, boolean warning) {
			this.protectedAtBirth = protectedAtBirth;
			this.warning = warning;
		}

		public boolean isProtectedAtBirth() {
			return protectedAtBirth;
		}

		public boolean isWarning() {
			return warning;
		}
	}

	/**
	 * Compares the 'DATE_ADMINISTERED' obs in two parent obs service observations.
	 */
	public static class DescendingDateAdministeredComparator implements Comparator<Obs> {
		@Override
		public int compare(Obs serviceObsParent1, Obs serviceObsParent2) {
			final Date obs1Date = ServiceUtil.serviceDateAdministered(serviceObsParent1);
			final Date obs2Date = ServiceUtil.serviceDateAdministered(serviceObsParent2);

			if (obs1Date == null && obs2Date == null) {
				return 0;
			} else if (obs1Date == null && obs2Date != null) {
				return +1;
			} else if (obs1Date != null && obs2Date == null) {
				return -1;
			} else {
				return obs2Date.compareTo(obs1Date);
			}
		}
	}

	/**
	 * Returns the low birth weight threshold value (default is 2.5kg) from the "chits.low.birthwate.vaccination.warning" global property value.
	 * 
	 * @return The low birth weight threshold value
	 */
	public static boolean isLowBirthweightThreshold(Patient patient) {
		if (patient != null) {
			double lowBirthweightThreshold;
			try {
				lowBirthweightThreshold = Double.parseDouble(Context.getAdministrationService().getGlobalProperty(
						ChildCareConstants.GP_LOW_BIRTHWEIGHT_VACCINATION_WARNING, "2.5"));
			} catch (Exception ex) {
				lowBirthweightThreshold = 2.5;
			}

			final Obs birthWeight = Functions.observation(patient, ChildCareConcepts.BIRTH_WEIGHT);
			if (birthWeight != null && birthWeight.getValueNumeric() != null && birthWeight.getValueNumeric() < lowBirthweightThreshold) {
				// patient has low birth weight
				return true;
			}
		}

		// patient does not have a low birth weight
		return false;
	}

	/**
	 * Gets ECCD services due for the given patient.
	 * <p>
	 * Assumptions:
	 * <ul>
	 * <li>Patient has birthdate set (i.e. patient.getBirthdate() != null)
	 * <li>Patient is enrolled in the ECCD program and program has not yet been closed (i.e., Functions.isInProgram(patient, ProgramConcepts.CHILDCARE) == true)
	 * </ul>
	 * 
	 * @param patient
	 */
	public static void addDueServices(Defaulter defaulter) {
		// store due date information into the status
		final ServiceStatus serviceStatus = new ServiceStatus();
		final Patient patient = defaulter.getPatient();

		// check if overdue for vitamin A service
		ServiceUtil.storeVitaminADueServices(serviceStatus, patient);
		if (serviceStatus.getVitaminAServiceDueInfo().getType() == ServiceDueInfoType.OVERDUE) {
			// vitamin A is overdue
			defaulter.addDueService(ServiceType.ECCD_SERVICE, "Vitamin A", serviceStatus.getVitaminAServiceDueInfo().getDueDate());
		}

		// check if overdue for ferrous sulfate service
		ServiceUtil.storeFerrousSulfateDueServices(serviceStatus, patient);
		if (serviceStatus.getFerrousSulfateServiceDueInfo().getType() == ServiceDueInfoType.OVERDUE) {
			// iron supplementation is overdue
			defaulter.addDueService(ServiceType.ECCD_SERVICE, "Iron Supplementation", serviceStatus.getFerrousSulfateServiceDueInfo().getDueDate());
		}

		// check if overdue for deworming service
		ServiceUtil.storeDewormingDueServices(serviceStatus, patient);
		if (serviceStatus.getDewormingServiceDueInfo().getType() == ServiceDueInfoType.OVERDUE) {
			// deworming is overdue
			defaulter.addDueService(ServiceType.ECCD_SERVICE, "Deworming", serviceStatus.getDewormingServiceDueInfo().getDueDate());
		}

		// get list of antigens administered to this patient
		final List<Obs> antigensAdministered = Functions.observations(patient, VaccinationConcepts.ANTIGEN);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.BCG_24HRS, "BCG", Calendar.HOUR, 24);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.HEPATITIS_B_24HRS, "HEPB1 Vaccination", Calendar.HOUR, 24);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.HEPATITIS_B_06WKS, "HEPB2 Vaccination", Calendar.WEEK_OF_YEAR, 6);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.HEPATITIS_B_14WKS, "HEPB3 Vaccination", Calendar.WEEK_OF_YEAR, 14);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.DPT_1_24HRS, "DPT1 Vaccination", Calendar.HOUR, 24);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.DPT_2_06WKS, "DPT2 Vaccination", Calendar.WEEK_OF_YEAR, 6);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.DPT_3_14WKS, "DPT3 Vaccination", Calendar.WEEK_OF_YEAR, 14);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.OPV1_06WKS, "OPV1 Vaccination", Calendar.WEEK_OF_YEAR, 6);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.OPV2_10WKS, "OPV2 Vaccination", Calendar.WEEK_OF_YEAR, 10);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.POV3_14WKS, "OPV3 Vaccination", Calendar.WEEK_OF_YEAR, 14);
		checkIfServiceDueImpl(defaulter, antigensAdministered, ChildCareVaccinesConcepts.MEASLES_9MOS, "Measles Vaccination", Calendar.MONTH, 9);
	}

	/**
	 * Checks the defaulter record if the antigen has already been administered if the due date has already elapsed and adds a service due record if not.
	 * 
	 * @param defaulter
	 *            The defaulter record containing the patient to check
	 * @param antigensAdministered
	 *            The antigen records already administered to the patient
	 * @param antigen
	 *            The antigen to check
	 * @param description
	 *            A description of the service due
	 * @param calendarUnits
	 *            The units (Calendar field constant) indicating the the units of the 'birthdayOffset' parameter (e.g., Calendar.DATE, Calendar.MONTH, etc.)
	 * @param birthdayOffset
	 *            The number of units from the patient's birthday at which time the vaccination becomes overdue
	 */
	private static void checkIfServiceDueImpl(Defaulter defaulter, List<Obs> antigensAdministered, CachedConceptId antigen, String description,
			int calendarUnits, int birthdayOffset) {
		// calculate due date counting from patient's birthdate
		final Date today = DateUtil.stripTime(new Date());
		final Calendar dueDateCal = Calendar.getInstance();
		dueDateCal.setTime(DateUtil.stripTime(defaulter.getPatient().getBirthdate()));
		dueDateCal.add(calendarUnits, birthdayOffset);

		// has the due date elapsed?
		final Date dueDate = dueDateCal.getTime();
		if (today.after(dueDate)) {
			// this service should already have been administered: check if already administered
			if (Functions.filterByCodedValue(antigensAdministered, antigen).isEmpty()) {
				// antigen not yet administered, it is due!
				defaulter.addDueService(ServiceType.ECCD_VACCINE, description, dueDate);
			}
		}
	}
}
