package org.openmrs.module.chits.web.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.chits.Barangay;
import org.openmrs.module.chits.CHITSService;
import org.openmrs.module.chits.FamilyFolder;
import org.openmrs.module.chits.Util;
import org.openmrs.module.chits.impl.StaticBarangayCodesHolder;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Create dummy data controller.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/createDummyData")
public class CreateDummyDataController {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Auto-wire the CHITS service */
	private CHITSService chitsService;

	/** Auto-wire the administration service */
	private AdministrationService adminService;

	/** Auto-wire the patient service */
	private PatientService patientService;

	/** Auto-wire the location service */
	private LocationService locationService;

	/** Barangay codes holder */
	private final StaticBarangayCodesHolder barangayCodesHolder = StaticBarangayCodesHolder.getInstance();

	/**
	 * This method will display the add family folder form
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String createDummyData(HttpSession httpSession) {
		final String[] firstNames = new String[] { "Joshua ", "Christian ", "John Paul ", "Justin ", "John Mark ", "Adrian ", "Angelo ", "John Michael ",
				"James ", "John Lloyd", "Angel ", "Nicole ", "Angelica ", "Angela ", "Jasmine ", "Mary Joy ", "Kimberly ", "Mariel ", "Mary Grace ", "Princess" };
		int firstNameIndex = 0;

		for (String lastName : new String[] { "Cruz", "Santos", "Reyes", "del Rosario", "Gonzales", "Bautista", "García", "Mendoza", "Pascual", "Castillo",
				"Villanueva", "Ramos", "Diaz", "Rivera", "Aquino", "Navarro", "Mercado", "Guevarra", "Morales", "Fernández", "de Leon" }) {
			final Barangay barangay = randomBarangay();
			FamilyFolder folder = new FamilyFolder();
			folder.setName(lastName);
			folder.setBarangayCode(Integer.toString(barangay.getBarangayCode()));
			folder.setCityCode(Integer.toString(barangay.getMunicipality().getMunicipalityCode()));
			folder.setNotes("Lives in " + barangay.getName());

			// save to the database (#TODO: how to update the model attribute?)
			folder = chitsService.saveFamilyFolder(folder);

			// set the 'code' based on the family code format
			folder.setCode(Util.formatFolderCode(adminService, folder.getId()));

			// create zero to five patients for this family
			final int members = (int) (6 * Math.random());
			for (int i = 0; i < members; i++) {
				final Patient patient = createPatient(firstNames[firstNameIndex], lastName, firstNameIndex < 10 ? "M" : "F");
				firstNameIndex = (firstNameIndex + 1) % firstNames.length;
				folder.getPatients().add(patient);
			}

			// save again to update the code and family members
			folder = chitsService.saveFamilyFolder(folder);
		}

		// save the 'folder created' message
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.DummyData.dummy.data.created");

		// send to the admin page
		return "redirect:/admin/index.htm";
	}

	private Patient createPatient(String firstName, String lastName, String gender) {
		Patient patient = new Patient();
		patient.setGender(gender);
		patient.setBirthdateFromAge(12 + (int) (30 * Math.random()), new Date());

		// add an identifier for this patient initially using a uuid
		final PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientService.getPatientIdentifierTypeByName("Old Identification Number"));
		patientIdentifier.setPatient(patient);
		patientIdentifier.setIdentifier(UUID.randomUUID().toString());
		patientIdentifier.setLocation(locationService.getDefaultLocation());

		// attach the identifier to the patient and save it
		patient.addIdentifier(patientIdentifier);

		// give the patient a name
		final PersonName personName = new PersonName();
		personName.setFamilyName(lastName);
		personName.setGivenName(firstName);
		personName.setMiddleName("");
		personName.setPreferred(true);
		patient.addName(personName);

		// create the patient without an identifier first
		patient = patientService.savePatient(patient);

		// update the identifier to use the formatted primary key ID
		patientIdentifier.setIdentifier(Util.formatPatientId(adminService, patient.getId()));
		patientService.savePatientIdentifier(patientIdentifier);

		// send back the updated patient
		return patient;
	}

	private Barangay randomBarangay() {
		final List<Barangay> barangays = new ArrayList<Barangay>(barangayCodesHolder.barangays.values());
		return barangays.get((int) (Math.random() * barangays.size()));
	}

	@Autowired
	public void setAdminService(AdministrationService adminService) {
		this.adminService = adminService;
	}

	@Autowired
	public void setChitsService(CHITSService chitsService) {
		this.chitsService = chitsService;
	}

	@Autowired
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Autowired
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
}
