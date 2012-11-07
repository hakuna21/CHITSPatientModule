package org.openmrs.module.chits.eccdprogram;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.Interval;
import org.joda.time.Period;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServiceTypes;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServicesConcepts;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Utility class for extracting service information (alerts, services due / overdue, allowed dosages, and allowed services)
 * 
 * @author Bren
 */
public class ServiceUtil {
	/**
	 * Returns the date the service was administered by looking at the obsDatetime value of the observation member with the 'DATE_ADMINISTERED' concept.
	 * 
	 * @param serviceRecord
	 *            The service record containing the date administered value.
	 * @return The value of the date administered or this service
	 */
	public static Date serviceDateAdministered(Obs serviceRecord) {
		Date date = null;
		if (serviceRecord != null) {
			final Obs dateAdministeredObs = Functions.observation(serviceRecord, ChildCareServicesConcepts.DATE_ADMINISTERED);
			if (dateAdministeredObs != null) {
				// use the user-entered 'date administered' value
				date = dateAdministeredObs.getValueDatetime();

				if (date == null) {
					// no 'value datetime' record? use the observation date time instead
					date = dateAdministeredObs.getObsDatetime();
				}
			} else {
				// no 'DATE_ADMINISTERED' Obs? Use the service record's obsDatetime
				date = serviceRecord.getObsDatetime();
			}
		}

		return date;
	}

	/**
	 * Extracts the service status for the given child patient.
	 * 
	 * @param patient
	 *            The child patient
	 * @return The {@link ServiceStatus} of the patient.
	 */
	public static ServiceStatus getServiceStatus(Patient patient) {
		// determine which services are updatable
		final ServiceStatus serviceStatus = new ServiceStatus();

		if (patient == null || patient.getBirthdate() == null) {
			// patient doesn't have a birthdate? there is no way to determine the service status, so just return an empty set
			return serviceStatus;
		}

		// set the services by service type
		serviceStatus.setServices(ChildCareUtil.getAdministeredServices(patient));

		// buttons can be enabled only if the program has not yet been concluded or the logged-in user is a superuser
		if (!ChildCareUtil.isProgramClosedFor(patient) || Context.getAuthenticatedUser().isSuperUser()) {
			// get child's current age
			final int ageInMonths = DateUtil.monthsBetween(patient.getBirthdate(), new Date());

			// enable vitamin A button only if child is >= 6 months old (or has low birth weight and >= 2 months old) and < 6 years old
			if ((ageInMonths >= 6 || (ChildCareUtil.isLowBirthweightThreshold(patient) && ageInMonths >= 2)) && ageInMonths < 72) {
				// vitamin A is enabled for this patient
				serviceStatus.setVitaminAEnabled(true);
			}

			// deworming service is allowed only for children 12 months old and above
			if (ageInMonths >= 12 && ageInMonths < 72) {
				// deworming is enabled
				serviceStatus.setDewormingEnabled(true);
			}

			final boolean lowBirthWeight = ChildCareUtil.isLowBirthweightThreshold(patient);
			if (lowBirthWeight && ageInMonths >= 2 && ageInMonths < 72) {
				// enable ferrous sulfate for children with low birthweight and at least 2 months of age
				serviceStatus.setFerrousSulfateEnabled(true);
			} else if (ageInMonths >= 6 && ageInMonths < 72) {
				serviceStatus.setFerrousSulfateEnabled(true);
			}
		}

		// store service alerts into the status
		storeVitaminAAlerts(serviceStatus, patient);
		storeDewormingAlerts(serviceStatus, patient);
		storeFerrousSulfateAlerts(serviceStatus, patient);

		// store due date information into the status
		storeVitaminADueServices(serviceStatus, patient);
		storeDewormingDueServices(serviceStatus, patient);
		storeFerrousSulfateDueServices(serviceStatus, patient);

		// send back the service status
		return serviceStatus;
	}

	/**
	 * Stores the vitamin A alerts and due services into the ServiceStatus bean for the given patient.
	 * 
	 * @param serviceStatus
	 *            The {@link ServiceStatus} bean to store the alerts and due services of the patient
	 * @param patient
	 *            The patient to get the Vitamin A alerts and due services of
	 */
	private static void storeVitaminAAlerts(ServiceStatus serviceStatus, Patient patient) {
		final List<String> alerts = new ArrayList<String>();
		final Date now = new Date();
		if (patient != null) {
			// get all services rendered for this patient by service type concept id
			final Map<Concept, List<Obs>> servicesByServiceType = serviceStatus.getServices();

			// find the latest vitamin a service record
			final List<Obs> vitaminAService = servicesByServiceType.get(Functions.concept(ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION));
			final Obs lastVitaminAService = !vitaminAService.isEmpty() ? vitaminAService.get(0) : null;

			if (lastVitaminAService != null) {
				int elapsedMonths = 0;
				final Date dateAdministered = serviceDateAdministered(lastVitaminAService);
				if (now.after(dateAdministered)) {
					final Period period = new Interval(dateAdministered.getTime(), now.getTime()).toPeriod();
					elapsedMonths = period.getMonths() + period.getYears() * 12;
				}

				if (elapsedMonths < 1) {
					alerts.add("chits.childcare.warning.vitamin_a.fewer.than.1.month");
				} else if (elapsedMonths < 6) {
					alerts.add("chits.childcare.warning.vitamin_a.fewer.than.6.months");
				} else {
					// add note about prescribed vitamin A dosage
					addVitaminAPrescribedDosage(alerts, patient);
				}
			} else {
				// vitamin A has not yet been administered; add note about prescribed vitamin A dosage
				addVitaminAPrescribedDosage(alerts, patient);
			}

			if (patient.getBirthdate() != null && patient.getBirthdate().getTime() < now.getTime()) {
				// get child's current age
				final int ageInMonths = DateUtil.monthsBetween(patient.getBirthdate(), new Date());
				if (ageInMonths < 6) {
					alerts.add("chits.childcare.warning.vitamin_a.minimum.age");
				}
			}
		}

		// store list of alerts into the service status
		serviceStatus.setVitaminAServiceAlerts(alerts);
	}

	private static void addVitaminAPrescribedDosage(List<String> alerts, Patient patient) {
		final Date now = new Date();
		if (patient.getBirthdate() != null) {
			// calculate how old this person was, in months, when the observations were taken
			final int ageInMonths = DateUtil.monthsBetween(patient.getBirthdate(), now);
			if (ageInMonths >= 6 && ageInMonths < 12) {
				alerts.add("chits.childcare.vitamin_a.prescribed.100K");
			} else if (ageInMonths >= 12 && ageInMonths <= 59) {
				alerts.add("chits.childcare.vitamin_a.prescribed.200K");
			}
		}
	}

	/**
	 * Stores due date information for the vitamin A service.
	 * 
	 * @param serviceStatus
	 *            The {@link ServiceStatus} record to store the service due date info into.
	 * @param patient
	 *            The patient to check for the due service of.
	 */
	static void storeVitaminADueServices(ServiceStatus serviceStatus, Patient patient) {
		// prep the due date information
		final ServiceDueInfo serviceDueInfo = new ServiceDueInfo();

		// Update of the ECCD Service Schedule on the Program Chart is done:
		final Obs lastRoutineIssue = ChildCareUtil.findLastRoutineService(patient, ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION);
		final Obs lastTherapeuticIssue = ChildCareUtil.findLastTherapeuticService(patient, ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION);

		// set due date
		final Calendar dueDate = Calendar.getInstance();

		// Rule 1: the due date of the next service is set at for the 6th month after the last routine service date:
		if (lastRoutineIssue != null) {
			// set to the date the routine service was administered
			dueDate.setTime(DateUtil.stripTime(serviceDateAdministered(lastRoutineIssue)));
		} else if (patient.getBirthdate() != null) {
			// use child's birthdate as the basis
			dueDate.setTime(DateUtil.stripTime(patient.getBirthdate()));
		}

		// store the date that the last routine service was issued
		final Date lastRoutineIssueDate = dueDate.getTime();

		// due date is set at for the 6th month
		dueDate.add(Calendar.MONTH, 6);

		// Rule 2: If there is a therapeutic dosage given more than 5 months after the last routine issuance,
		// the due date is adjusted to one month after the date of issuance of the therapeutic dosage.
		if (lastTherapeuticIssue != null) {
			final Date lastTherapeuticIssueDate = DateUtil.stripTime(serviceDateAdministered(lastTherapeuticIssue));
			if (DateUtil.monthsBetween(lastRoutineIssueDate, lastTherapeuticIssueDate) >= 5) {
				dueDate.setTime(lastTherapeuticIssueDate);
				dueDate.add(Calendar.MONTH, 1);
			}
		}

		// store the due date
		serviceDueInfo.setDueDate(dueDate.getTime());

		// determine if the service is due or overdue
		if (serviceDueInfo.getDueDate().after(new Date())) {
			// due date in future
			serviceDueInfo.setType(ServiceDueInfo.ServiceDueInfoType.DUE);
		} else {
			// due date in past
			serviceDueInfo.setType(ServiceDueInfo.ServiceDueInfoType.OVERDUE);
		}

		// Rule 3: If the due date falls after the 6th birthday of the child, the due date entry will be "not eligible for service."
		if (patient.getBirthdate() != null) {
			final int ageOnDueDate = DateUtil.yearsBetween(patient.getBirthdate(), serviceDueInfo.getDueDate());
			if (ageOnDueDate >= 6) {
				// no longer eligible
				serviceDueInfo.setType(ServiceDueInfo.ServiceDueInfoType.NOT_ELIGIBLE);
			}
		}

		// store the Vitamin A due date information into the service status
		serviceStatus.setVitaminAServiceDueInfo(serviceDueInfo);
	}

	/**
	 * Stores the deworming alerts and due services into the ServiceStatus bean for the given patient.
	 * 
	 * @param serviceStatus
	 *            The {@link ServiceStatus} bean to store the alerts and due services of the patient
	 * @param patient
	 *            The patient to get the deworming alerts and due services of
	 */
	private static void storeDewormingAlerts(ServiceStatus serviceStatus, Patient patient) {
		final List<String> alerts = new ArrayList<String>();
		if (patient != null) {
			final Date now = new Date();
			if (patient.getBirthdate() != null) {
				// calculate how old this person was, in months, when the observations were taken
				final int ageInMonths = DateUtil.monthsBetween(patient.getBirthdate(), now);

				if (ageInMonths < 12) {
					alerts.add("chits.childcare.warning.deworming.minimum.age");
				} else if (ageInMonths >= 12 && ageInMonths < 24) {
					alerts.add("chits.childcare.deworming.12.to.23");
				} else if (ageInMonths >= 24 && ageInMonths <= 71) {
					alerts.add("chits.childcare.deworming.24.to.71");
				} else {
					alerts.add("chits.childcare.deworming.age.warning");
				}
			}
		}

		// store list of alerts into the service status
		serviceStatus.setDewormingAlerts(alerts);
	}

	/**
	 * Stores due date information for the deworming service.
	 * 
	 * @param serviceStatus
	 *            The {@link ServiceStatus} record to store the service due date info into.
	 * @param patient
	 *            The patient to check for the due service of.
	 */
	static void storeDewormingDueServices(ServiceStatus serviceStatus, Patient patient) {
		// prep the due date information
		final ServiceDueInfo serviceDueInfo = new ServiceDueInfo();

		// Update of the ECCD Service Schedule on the Program Chart is done:
		// The due date of the next service is set at for the 6th month after the last deworming service date.
		final Obs lastDewormingIssue = ChildCareUtil.findLastService(patient, ChildCareServiceTypes.DEWORMING);

		// set due date
		final Calendar dueDate = Calendar.getInstance();

		// Rule 1: the due date of the next service is set at for the 6th month after the last routine service date:
		if (lastDewormingIssue != null) {
			// set to the date the routine service was administered
			dueDate.setTime(DateUtil.stripTime(serviceDateAdministered(lastDewormingIssue)));

			// due date is set at for the 6th month
			dueDate.add(Calendar.MONTH, 6);
		} else if (patient.getBirthdate() != null) {
			// use child's first birthdate as the due date
			dueDate.setTime(DateUtil.stripTime(patient.getBirthdate()));

			// add 12 months to get the first birth date
			dueDate.add(Calendar.MONTH, 12);
		}

		// store the due date
		serviceDueInfo.setDueDate(dueDate.getTime());

		// determine if the service is due or overdue
		if (serviceDueInfo.getDueDate().after(new Date())) {
			// due date in future
			serviceDueInfo.setType(ServiceDueInfo.ServiceDueInfoType.DUE);
		} else {
			// due date in past
			serviceDueInfo.setType(ServiceDueInfo.ServiceDueInfoType.OVERDUE);
		}

		// Rule 2: If the due date falls after the 6th birthday of the child, the due date entry will be "not eligible for service."
		if (patient.getBirthdate() != null) {
			final int ageOnDueDate = DateUtil.yearsBetween(patient.getBirthdate(), serviceDueInfo.getDueDate());
			if (ageOnDueDate >= 6) {
				// no longer eligible
				serviceDueInfo.setType(ServiceDueInfo.ServiceDueInfoType.NOT_ELIGIBLE);
			}
		}

		// store the Deworming due date information into the service status
		serviceStatus.setDewormingServiceDueInfo(serviceDueInfo);
	}

	/**
	 * Stores the ferrous sulfate alerts and due services into the ServiceStatus bean for the given patient.
	 * 
	 * @param serviceStatus
	 *            The {@link ServiceStatus} bean to store the alerts and due services of the patient
	 * @param patient
	 *            The patient to get the ferrous sulfate alerts and due services of
	 */
	private static void storeFerrousSulfateAlerts(ServiceStatus serviceStatus, Patient patient) {
		final List<String> alerts = new ArrayList<String>();
		if (patient != null) {
			final Date now = new Date();
			if (patient.getBirthdate() != null) {
				// determine if patient has low birthweight
				final boolean lowBirthWeight = ChildCareUtil.isLowBirthweightThreshold(patient);

				// calculate how old this person was, in months, when the observations were taken
				final int ageInMonths = DateUtil.monthsBetween(patient.getBirthdate(), now);

				if (lowBirthWeight && ageInMonths < 2) {
					alerts.add("chits.childcare.warning.ferroussulfate.lowbirthweight");
				} else if (!lowBirthWeight && ageInMonths < 6) {
					alerts.add("chits.childcare.warning.ferroussulfate.minimum.age");
				}
			}
		}

		// store list of alerts into the service status
		serviceStatus.setFerrousSulfateAlerts(alerts);
	}

	/**
	 * Stores due date information for the ferrous sulfate service.
	 * 
	 * @param serviceStatus
	 *            The {@link ServiceStatus} record to store the service due date info into.
	 * @param patient
	 *            The patient to check for the due service of.
	 */
	static void storeFerrousSulfateDueServices(ServiceStatus serviceStatus, Patient patient) {
		// prep the due date information
		final ServiceDueInfo serviceDueInfo = new ServiceDueInfo();

		// Update of the ECCD Service Schedule on the Program Chart is done:
		final Obs lastIronIssue = ChildCareUtil.findLastService(patient, ChildCareServiceTypes.FERROUS_SULFATE);

		// set due date
		final Calendar dueDate = Calendar.getInstance();

		// Rule 1: The due date of the next service is set at to one (1) month after the last supplement issue, but may be changed using the Set Appointment
		// module (not covered in this project).
		if (lastIronIssue != null) {
			// set to 1 month after last issue
			dueDate.setTime(DateUtil.stripTime(serviceDateAdministered(lastIronIssue)));
			dueDate.add(Calendar.MONTH, 1);
		} else if (patient.getBirthdate() != null) {
			// use child's birthdate and low birth weight status to determine first issue date
			dueDate.setTime(DateUtil.stripTime(patient.getBirthdate()));
			final boolean lowBirthWeight = ChildCareUtil.isLowBirthweightThreshold(patient);
			if (lowBirthWeight) {
				// set issue date to when child turns two months old
				dueDate.add(Calendar.MONTH, 2);
			} else {
				// set issue date to when child turns 6 months old
				dueDate.add(Calendar.MONTH, 6);
			}
		}

		// store the due date
		serviceDueInfo.setDueDate(dueDate.getTime());

		// determine if the service is due or overdue
		if (serviceDueInfo.getDueDate().after(new Date())) {
			// due date in future
			serviceDueInfo.setType(ServiceDueInfo.ServiceDueInfoType.DUE);
		} else {
			// due date in past
			serviceDueInfo.setType(ServiceDueInfo.ServiceDueInfoType.OVERDUE);
		}

		// Rule 2: If the due date falls after the 6th birthday of the child, the due date entry will be "not eligible for service."
		if (patient.getBirthdate() != null) {
			final int ageOnDueDate = DateUtil.yearsBetween(patient.getBirthdate(), serviceDueInfo.getDueDate());
			if (ageOnDueDate >= 6) {
				// no longer eligible
				serviceDueInfo.setType(ServiceDueInfo.ServiceDueInfoType.NOT_ELIGIBLE);
			}
		}

		// store the ferrous sulfate due date information into the service status
		serviceStatus.setFerrousSulfateServiceDueInfo(serviceDueInfo);
	}

	/**
	 * Holds the service status values.
	 * 
	 * @author Bren
	 */
	public static class ServiceStatus {
		/** Due date information for Vitamin A service */
		private ServiceDueInfo vitaminAServiceDueInfo;

		/** Due date information for Deworming service */
		private ServiceDueInfo dewormingServiceDueInfo;

		/** Due date information for Ferrous Sulfate service */
		private ServiceDueInfo ferrousSulfateServiceDueInfo;

		/** flags for the enablement of services */
		private boolean vitaminAEnabled = false;
		private boolean dewormingEnabled = false;
		private boolean ferrousSulfateEnabled = false;

		/** Services rendered by service type */
		private Map<Concept, List<Obs>> services;

		/** Vitamin A alerts */
		private List<String> vitaminAServiceAlerts;

		/** Deworming alerts */
		private List<String> dewormingAlerts;

		/** Ferrous Sulfate alerts */
		private List<String> ferrousSulfateAlerts;

		public ServiceDueInfo getVitaminAServiceDueInfo() {
			return vitaminAServiceDueInfo;
		}

		public void setVitaminAServiceDueInfo(ServiceDueInfo vitaminAServiceDueInfo) {
			this.vitaminAServiceDueInfo = vitaminAServiceDueInfo;
		}

		public ServiceDueInfo getDewormingServiceDueInfo() {
			return dewormingServiceDueInfo;
		}

		public void setDewormingServiceDueInfo(ServiceDueInfo dewormingServiceDueInfo) {
			this.dewormingServiceDueInfo = dewormingServiceDueInfo;
		}

		public ServiceDueInfo getFerrousSulfateServiceDueInfo() {
			return ferrousSulfateServiceDueInfo;
		}

		public void setFerrousSulfateServiceDueInfo(ServiceDueInfo ferrousSulfateServiceDueInfo) {
			this.ferrousSulfateServiceDueInfo = ferrousSulfateServiceDueInfo;
		}

		public boolean isVitaminAEnabled() {
			return vitaminAEnabled;
		}

		public void setVitaminAEnabled(boolean vitaminAEnabled) {
			this.vitaminAEnabled = vitaminAEnabled;
		}

		public boolean isDewormingEnabled() {
			return dewormingEnabled;
		}

		public void setDewormingEnabled(boolean dewormingEnabled) {
			this.dewormingEnabled = dewormingEnabled;
		}

		public boolean isFerrousSulfateEnabled() {
			return ferrousSulfateEnabled;
		}

		public void setFerrousSulfateEnabled(boolean ferrousSulfateEnabled) {
			this.ferrousSulfateEnabled = ferrousSulfateEnabled;
		}

		public Map<Concept, List<Obs>> getServices() {
			return services;
		}

		public void setServices(Map<Concept, List<Obs>> services) {
			this.services = services;
		}

		public List<String> getVitaminAServiceAlerts() {
			return vitaminAServiceAlerts;
		}

		public void setVitaminAServiceAlerts(List<String> vitaminAServiceAlerts) {
			this.vitaminAServiceAlerts = vitaminAServiceAlerts;
		}

		public List<String> getDewormingAlerts() {
			return dewormingAlerts;
		}

		public void setDewormingAlerts(List<String> dewormingAlerts) {
			this.dewormingAlerts = dewormingAlerts;
		}

		public List<String> getFerrousSulfateAlerts() {
			return ferrousSulfateAlerts;
		}

		public void setFerrousSulfateAlerts(List<String> ferrousSulfateAlerts) {
			this.ferrousSulfateAlerts = ferrousSulfateAlerts;
		}
	}
}
