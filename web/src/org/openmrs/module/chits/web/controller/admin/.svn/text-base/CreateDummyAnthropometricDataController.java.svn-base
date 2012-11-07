package org.openmrs.module.chits.web.controller.admin;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Create dummy anthropometric data controller.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/createDummyAnthropometricData")
public class CreateDummyAnthropometricDataController implements Constants {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the patient service */
	private PatientService patientService;

	/** Auto-wire the encounter service */
	private EncounterService encounterService;

	/** Auto-wire the concept service */
	private ConceptService conceptService;

	/** Height of males form birth to 20 years old in centimeters */
	private final static Map<Integer, Integer> maleAgeHeight = new HashMap<Integer, Integer>();

	/** Weight of males form birth to 20 years old in kilograms */
	private final static Map<Integer, Double> maleAgeWeight = new HashMap<Integer, Double>();

	/** Head circumference of males form birth to 3 years old in centimeters */
	private final static Map<Integer, Double> maleAgeHeadCirc = new HashMap<Integer, Double>();

	/** Height of males form birth to 20 years old in centimeters */
	private final static Map<Integer, Integer> femaleAgeHeight = new HashMap<Integer, Integer>();

	/** Weight of females form birth to 20 years old in kilograms */
	private final static Map<Integer, Double> femaleAgeWeight = new HashMap<Integer, Double>();

	/** Head circumference of males form birth to 3 years old in centimeters */
	private final static Map<Integer, Double> femaleAgeHeadCirc = new HashMap<Integer, Double>();

	/**
	 * This method will display the add family folder form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String createDummyData(HttpSession httpSession) {
		// find all patients
		int created = 0;
		for (Patient patient : patientService.getAllPatients()) {
			if (patient.getBirthdate() != null) {
				if (encounterService.getCountOfEncounters(patient.getPatientIdentifier().getIdentifier(), false) == 0) {
					createPatientDummyData(patient);
					created++;
				}
			}
		}

		// save the 'folder created' message
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.DummyData.dummy.anthropometric.data.created");
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, new Object[] { Integer.toString(created) });

		// send to the admin page
		return "redirect:/admin/index.htm";
	}

	private void createPatientDummyData(Patient patient) {
		final Date today = new Date();
		final Calendar cal = Calendar.getInstance();
		cal.setTime(patient.getBirthdate());

		// create deviation for this patient from 90% to 110%;
		final double deviation = 0.90 + 0.20 * Math.random();

		// create data from birth to 20 years old
		int encounters = encounterService.getCountOfEncounters(patient.getPatientIdentifier().getIdentifier(), false);
		for (int monthsOld = 0; monthsOld <= 20 * 12; monthsOld++) {
			// set a random hour from 8AM to 5PM for this encounter
			cal.set(Calendar.HOUR_OF_DAY, (int) (9.0 * Math.random()) + 8);
			cal.set(Calendar.MINUTE, (Math.random() < 0.5) ? 0 : 30);
			cal.set(Calendar.DAY_OF_MONTH, (int) (28 * Math.random()));

			if (cal.getTime().after(today)) {
				// don't add future data!
				break;
			}

			// determine type of encounter (adult / ped, initial / return)
			final boolean adult = monthsOld >= 16 * 12;
			final String encTypeName = (adult ? "ADULT" : "PEDS") + (encounters == 0 ? "INITIAL" : "RETURN");
			final EncounterType encType = encounterService.getEncounterType(encTypeName);

			// prepare an encounter at this age
			final Encounter enc = new Encounter();
			enc.setEncounterType(encType);
			enc.setPatient(patient);

			// set provider to currently logged-in user
			enc.setProvider(Context.getAuthenticatedUser().getPerson());
			enc.setEncounterDatetime(cal.getTime());

			// obtain data for this age
			final boolean male = "M".equals(patient.getGender());
			final Double weightKG = male ? maleAgeWeight.get(monthsOld) : femaleAgeWeight.get(monthsOld);
			final Integer heightCM = male ? maleAgeHeight.get(monthsOld) : femaleAgeHeight.get(monthsOld);
			final Double headCircCM = male ? maleAgeHeadCirc.get(monthsOld) : femaleAgeHeadCirc.get(monthsOld);

			// add observations (if any)
			if (weightKG != null) {
				addObservation(VisitConcepts.WEIGHT_KG, enc, weightKG * deviation);
			}

			if (heightCM != null) {
				addObservation(VisitConcepts.HEIGHT_CM, enc, heightCM * deviation);
			}

			if (headCircCM != null) {
				addObservation(VisitConcepts.HEAD_CIRC_CM, enc, headCircCM * deviation);

				// at birth, assume H/C ratio of 120%, at 12 months and over, assume H/C ratio of 80%
				final double hcRatio;
				if (monthsOld < 12) {
					hcRatio = 1.2D - 0.40D * (monthsOld / 12.0D);
				} else {
					hcRatio = 0.80D;
				}

				// derive circumference
				addObservation(VisitConcepts.CHEST_CIRC_CM, enc, headCircCM * deviation / hcRatio);
			}

			// if there were any observations made, then save this data
			if (!enc.getObs().isEmpty()) {
				if (monthsOld > 17 * 12) {
					// add chest, waist, and hip circumference
					if (male) {
						addObservation(VisitConcepts.WAIST_CIRC_CM, enc, 33.0 * 2.54);
						addObservation(VisitConcepts.HIP_CIRC_CM, enc, 29.0 * 2.54);
					} else {
						addObservation(VisitConcepts.WAIST_CIRC_CM, enc, 27.0 * 2.54);
						addObservation(VisitConcepts.HIP_CIRC_CM, enc, 36.0 * 2.54);
					}
				}

				// add three vital signs observations, taken 10 minutes apart
				for (int i = 0; i < 3; i++) {
					// create deviations within this group for this patient from 90% to 110%;
					final double vitalSignsDeviation = deviation * (0.90 + 0.20 * Math.random());

					// set time interval for this set of vital signs (10 minutes per interval)
					final Obs vitalSigns = new Obs();
					vitalSigns.setConcept(conceptService.getConcept(VisitConcepts.VITAL_SIGNS.getConceptId()));
					vitalSigns.setObsDatetime(new Date(enc.getEncounterDatetime().getTime() + (i + 1) * 10L * 60 * 1000));

					// for convenience when viewing the database manually, we set the coded value and value text into the observation group parent
					vitalSigns.setValueCoded(conceptService.getConcept(VisitConcepts.VITAL_SIGNS.getConceptId()));
					PatientConsultEntryFormValidator.setValueCodedIntoValueText(vitalSigns);

					enc.addObs(vitalSigns);

					// add a temperature
					vitalSigns.addGroupMember(addObservation(VisitConcepts.TEMPERATURE_C, enc, 37.5 * vitalSignsDeviation));

					// add fixed blood pressure values
					vitalSigns.addGroupMember(addObservation(VisitConcepts.SBP, enc, 120.0));
					vitalSigns.addGroupMember(addObservation(VisitConcepts.DBP, enc, 80.0));

					// calculate maximum heart rate based on age: HRmax = 205.8 - (0.685 × age)
					final double hrMax = (205.8 - (0.685 * monthsOld / 12));

					// estimate heart at resting rate at 33% max
					vitalSigns.addGroupMember(addObservation(VisitConcepts.PULSE, enc, (int) (hrMax * 0.32 * vitalSignsDeviation)));

					// add fixed respiratory rate
					if (monthsOld < 12) {
						// Less Than 1 Year: 30-40 breaths per minute
						vitalSigns.addGroupMember(addObservation(VisitConcepts.RESPIRATORY_RATE, enc, (int) (35 * vitalSignsDeviation)));
					} else if (monthsOld < 3 * 12) {
						// 1-3 Years: 23-35 breaths per minute
						vitalSigns.addGroupMember(addObservation(VisitConcepts.RESPIRATORY_RATE, enc, (int) (29 * vitalSignsDeviation)));
					} else if (monthsOld < 6 * 12) {
						// 3-6 Years: 20-30 breaths per minute
						vitalSigns.addGroupMember(addObservation(VisitConcepts.RESPIRATORY_RATE, enc, (int) (25 * vitalSignsDeviation)));
					} else if (monthsOld < 12 * 12) {
						// 6-12 Years: 18-26 breaths per minute
						vitalSigns.addGroupMember(addObservation(VisitConcepts.RESPIRATORY_RATE, enc, (int) (22 * vitalSignsDeviation)));
					} else if (monthsOld < 17 * 12) {
						// 12-17 Years: 12-20 breaths per minute
						vitalSigns.addGroupMember(addObservation(VisitConcepts.RESPIRATORY_RATE, enc, (int) (16 * vitalSignsDeviation)));
					} else {
						// Adults Over 18: 12–20 breaths per minute.
						vitalSigns.addGroupMember(addObservation(VisitConcepts.RESPIRATORY_RATE, enc, (int) (16 * vitalSignsDeviation)));
					}
				}

				// save the encounter
				encounterService.saveEncounter(enc);
				encounters++;
			}

			// advance the patient's age by one month
			cal.add(Calendar.MONTH, 1);
		}
	}

	private Obs addObservation(CachedConceptId concept, Encounter enc, double value) {
		final DecimalFormat df = new DecimalFormat("0.##");
		final Concept c = conceptService.getConcept(concept.getConceptId());
		if (c == null) {
			throw new IllegalStateException("Unable to load concept: " + concept);
		}

		final Obs observation = new Obs();
		observation.setConcept(c);
		observation.setValueText(df.format(value));
		observation.setValueNumeric(value);
		enc.addObs(observation);

		return observation;
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Autowired
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}

	@Autowired
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	static {
		maleAgeHeight.put(0, 51);
		maleAgeHeight.put(1, 54);
		maleAgeHeight.put(2, 57);
		maleAgeHeight.put(3, 61);
		maleAgeHeight.put(4, 64);
		maleAgeHeight.put(5, 65);
		maleAgeHeight.put(6, 67);
		maleAgeHeight.put(7, 68);
		maleAgeHeight.put(8, 70);
		maleAgeHeight.put(9, 71);
		maleAgeHeight.put(10, 73);
		maleAgeHeight.put(11, 74);
		maleAgeHeight.put(12, 75);
		maleAgeHeight.put(13, 77);
		maleAgeHeight.put(14, 78);
		maleAgeHeight.put(15, 79);
		maleAgeHeight.put(16, 80);
		maleAgeHeight.put(17, 81);
		maleAgeHeight.put(18, 82);
		maleAgeHeight.put(19, 83);
		maleAgeHeight.put(20, 84);
		maleAgeHeight.put(21, 85);
		maleAgeHeight.put(22, 86);
		maleAgeHeight.put(23, 86);
		maleAgeHeight.put(24, 87);
		maleAgeHeight.put(2 * 12, 87);
		maleAgeHeight.put(3 * 12, 95);
		maleAgeHeight.put(4 * 12, 100);
		maleAgeHeight.put(5 * 12, 110);
		maleAgeHeight.put(6 * 12, 115);
		maleAgeHeight.put(7 * 12, 123);
		maleAgeHeight.put(8 * 12, 126);
		maleAgeHeight.put(9 * 12, 133);
		maleAgeHeight.put(10 * 12, 138);
		maleAgeHeight.put(11 * 12, 143);
		maleAgeHeight.put(12 * 12, 150);
		maleAgeHeight.put(13 * 12, 156);
		maleAgeHeight.put(14 * 12, 163);
		maleAgeHeight.put(15 * 12, 170);
		maleAgeHeight.put(16 * 12, 173);
		maleAgeHeight.put(17 * 12, 174);
		maleAgeHeight.put(18 * 12, 175);
		maleAgeHeight.put(19 * 12, 176);
		maleAgeHeight.put(20 * 12, 177);

		maleAgeWeight.put(0, 3.6);
		maleAgeWeight.put(1, 4.6);
		maleAgeWeight.put(2, 5.4);
		maleAgeWeight.put(3, 6.0);
		maleAgeWeight.put(4, 6.8);
		maleAgeWeight.put(5, 7.2);
		maleAgeWeight.put(6, 7.8);
		maleAgeWeight.put(7, 8.4);
		maleAgeWeight.put(8, 8.8);
		maleAgeWeight.put(9, 9.2);
		maleAgeWeight.put(10, 9.6);
		maleAgeWeight.put(11, 10.0);
		maleAgeWeight.put(12, 10.2);
		maleAgeWeight.put(13, 10.4);
		maleAgeWeight.put(14, 10.8);
		maleAgeWeight.put(15, 11.0);
		maleAgeWeight.put(16, 11.2);
		maleAgeWeight.put(17, 11.4);
		maleAgeWeight.put(18, 11.7);
		maleAgeWeight.put(19, 11.8);
		maleAgeWeight.put(20, 12.0);
		maleAgeWeight.put(21, 12.2);
		maleAgeWeight.put(22, 12.4);
		maleAgeWeight.put(23, 12.6);
		maleAgeWeight.put(24, 12.8);
		maleAgeWeight.put(2 * 12, 13.0);
		maleAgeWeight.put(3 * 12, 14.0);
		maleAgeWeight.put(4 * 12, 16.0);
		maleAgeWeight.put(5 * 12, 18.0);
		maleAgeWeight.put(6 * 12, 20.0);
		maleAgeWeight.put(7 * 12, 23.0);
		maleAgeWeight.put(8 * 12, 25.0);
		maleAgeWeight.put(9 * 12, 28.0);
		maleAgeWeight.put(10 * 12, 32.0);
		maleAgeWeight.put(11 * 12, 35.0);
		maleAgeWeight.put(12 * 12, 41.0);
		maleAgeWeight.put(13 * 12, 45.0);
		maleAgeWeight.put(14 * 12, 51.0);
		maleAgeWeight.put(15 * 12, 56.0);
		maleAgeWeight.put(16 * 12, 60.0);
		maleAgeWeight.put(17 * 12, 64.0);
		maleAgeWeight.put(18 * 12, 67.0);
		maleAgeWeight.put(19 * 12, 68.0);
		maleAgeWeight.put(20 * 12, 70.0);

		maleAgeHeadCirc.put(0, 36.0);
		maleAgeHeadCirc.put(1, 38.0);
		maleAgeHeadCirc.put(2, 40.0);
		maleAgeHeadCirc.put(3, 41.0);
		maleAgeHeadCirc.put(4, 42.0);
		maleAgeHeadCirc.put(5, 43.0);
		maleAgeHeadCirc.put(6, 43.4);
		maleAgeHeadCirc.put(7, 44.2);
		maleAgeHeadCirc.put(8, 44.7);
		maleAgeHeadCirc.put(9, 45.2);
		maleAgeHeadCirc.put(10, 45.6);
		maleAgeHeadCirc.put(11, 45.8);
		maleAgeHeadCirc.put(12, 46.2);
		maleAgeHeadCirc.put(13, 46.6);
		maleAgeHeadCirc.put(14, 46.9);
		maleAgeHeadCirc.put(15, 47.0);
		maleAgeHeadCirc.put(16, 47.2);
		maleAgeHeadCirc.put(17, 47.4);
		maleAgeHeadCirc.put(18, 47.6);
		maleAgeHeadCirc.put(19, 47.8);
		maleAgeHeadCirc.put(20, 48.0);
		maleAgeHeadCirc.put(21, 48.2);
		maleAgeHeadCirc.put(22, 48.4);
		maleAgeHeadCirc.put(23, 48.5);
		maleAgeHeadCirc.put(24, 48.6);
		maleAgeHeadCirc.put(2 * 12, 48.6);
		maleAgeHeadCirc.put(3 * 12, 49.2);

		femaleAgeHeight.put(0, 51);
		femaleAgeHeight.put(1, 54);
		femaleAgeHeight.put(2, 57);
		femaleAgeHeight.put(3, 61);
		femaleAgeHeight.put(4, 64);
		femaleAgeHeight.put(5, 65);
		femaleAgeHeight.put(6, 67);
		femaleAgeHeight.put(7, 68);
		femaleAgeHeight.put(8, 70);
		femaleAgeHeight.put(9, 71);
		femaleAgeHeight.put(10, 73);
		femaleAgeHeight.put(11, 74);
		femaleAgeHeight.put(12, 75);
		femaleAgeHeight.put(13, 77);
		femaleAgeHeight.put(14, 78);
		femaleAgeHeight.put(15, 79);
		femaleAgeHeight.put(16, 80);
		femaleAgeHeight.put(17, 81);
		femaleAgeHeight.put(18, 82);
		femaleAgeHeight.put(19, 83);
		femaleAgeHeight.put(20, 84);
		femaleAgeHeight.put(21, 85);
		femaleAgeHeight.put(22, 86);
		femaleAgeHeight.put(23, 86);
		femaleAgeHeight.put(24, 87);
		femaleAgeHeight.put(2 * 12, 87);
		femaleAgeHeight.put(3 * 12, 94);
		femaleAgeHeight.put(4 * 12, 102);
		femaleAgeHeight.put(5 * 12, 108);
		femaleAgeHeight.put(6 * 12, 114);
		femaleAgeHeight.put(7 * 12, 122);
		femaleAgeHeight.put(8 * 12, 126);
		femaleAgeHeight.put(9 * 12, 133);
		femaleAgeHeight.put(10 * 12, 138);
		femaleAgeHeight.put(11 * 12, 143);
		femaleAgeHeight.put(12 * 12, 150);
		femaleAgeHeight.put(13 * 12, 158);
		femaleAgeHeight.put(14 * 12, 160);
		femaleAgeHeight.put(15 * 12, 163);
		femaleAgeHeight.put(16 * 12, 164);
		femaleAgeHeight.put(17 * 12, 165);
		femaleAgeHeight.put(18 * 12, 166);
		femaleAgeHeight.put(19 * 12, 167);
		femaleAgeHeight.put(20 * 12, 168);

		femaleAgeWeight.put(0, 3.6);
		femaleAgeWeight.put(1, 4.0);
		femaleAgeWeight.put(2, 5.0);
		femaleAgeWeight.put(3, 5.4);
		femaleAgeWeight.put(4, 6.0);
		femaleAgeWeight.put(5, 6.8);
		femaleAgeWeight.put(6, 7.0);
		femaleAgeWeight.put(7, 7.8);
		femaleAgeWeight.put(8, 8.2);
		femaleAgeWeight.put(9, 8.4);
		femaleAgeWeight.put(10, 8.8);
		femaleAgeWeight.put(11, 9.2);
		femaleAgeWeight.put(12, 9.4);
		femaleAgeWeight.put(13, 9.8);
		femaleAgeWeight.put(14, 10.0);
		femaleAgeWeight.put(15, 10.3);
		femaleAgeWeight.put(16, 10.4);
		femaleAgeWeight.put(17, 10.8);
		femaleAgeWeight.put(18, 11.0);
		femaleAgeWeight.put(19, 11.2);
		femaleAgeWeight.put(20, 11.4);
		femaleAgeWeight.put(21, 11.6);
		femaleAgeWeight.put(22, 11.8);
		femaleAgeWeight.put(23, 11.9);
		femaleAgeWeight.put(24, 12.0);
		femaleAgeWeight.put(2 * 12, 12.0);
		femaleAgeWeight.put(3 * 12, 14.0);
		femaleAgeWeight.put(4 * 12, 15.0);
		femaleAgeWeight.put(5 * 12, 18.0);
		femaleAgeWeight.put(6 * 12, 20.0);
		femaleAgeWeight.put(7 * 12, 23.0);
		femaleAgeWeight.put(8 * 12, 25.0);
		femaleAgeWeight.put(9 * 12, 28.0);
		femaleAgeWeight.put(10 * 12, 33.0);
		femaleAgeWeight.put(11 * 12, 37.0);
		femaleAgeWeight.put(12 * 12, 42.0);
		femaleAgeWeight.put(13 * 12, 45.0);
		femaleAgeWeight.put(14 * 12, 50.0);
		femaleAgeWeight.put(15 * 12, 52.0);
		femaleAgeWeight.put(16 * 12, 54.0);
		femaleAgeWeight.put(17 * 12, 55.0);
		femaleAgeWeight.put(18 * 12, 56.0);
		femaleAgeWeight.put(19 * 12, 57.0);
		femaleAgeWeight.put(20 * 12, 58.0);

		femaleAgeHeadCirc.put(0, 36.0 - 1.0);
		femaleAgeHeadCirc.put(1, 38.0 - 1.0);
		femaleAgeHeadCirc.put(2, 40.0 - 1.0);
		femaleAgeHeadCirc.put(3, 41.0 - 1.0);
		femaleAgeHeadCirc.put(4, 42.0 - 1.0);
		femaleAgeHeadCirc.put(5, 43.0 - 1.0);
		femaleAgeHeadCirc.put(6, 43.4 - 1.0);
		femaleAgeHeadCirc.put(7, 44.2 - 1.0);
		femaleAgeHeadCirc.put(8, 44.7 - 1.0);
		femaleAgeHeadCirc.put(9, 45.2 - 1.0);
		femaleAgeHeadCirc.put(10, 45.6 - 1.0);
		femaleAgeHeadCirc.put(11, 45.8 - 1.0);
		femaleAgeHeadCirc.put(12, 46.2 - 1.0);
		femaleAgeHeadCirc.put(13, 46.6 - 1.0);
		femaleAgeHeadCirc.put(14, 46.9 - 1.0);
		femaleAgeHeadCirc.put(15, 47.0 - 1.0);
		femaleAgeHeadCirc.put(16, 47.2 - 1.0);
		femaleAgeHeadCirc.put(17, 47.4 - 1.0);
		femaleAgeHeadCirc.put(18, 47.6 - 1.0);
		femaleAgeHeadCirc.put(19, 47.8 - 1.0);
		femaleAgeHeadCirc.put(20, 48.0 - 1.0);
		femaleAgeHeadCirc.put(21, 48.2 - 1.0);
		femaleAgeHeadCirc.put(22, 48.4 - 1.0);
		femaleAgeHeadCirc.put(23, 48.5 - 1.0);
		femaleAgeHeadCirc.put(24, 48.6 - 1.0);
		femaleAgeHeadCirc.put(2 * 12, 48.6 - 1.0);
		femaleAgeHeadCirc.put(3 * 12, 49.2 - 1.0);
	}
}
