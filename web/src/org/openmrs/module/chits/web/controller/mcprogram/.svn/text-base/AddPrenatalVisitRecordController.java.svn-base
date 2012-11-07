package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.RelationshipUtil;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDangerSignsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDeliveryReportConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMedicalHistoryConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricExamination;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPrenatalVisitRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.mcprogram.PrenatalVisitRecord;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Adds a new prenatal visit record to the patient's current active maternal care program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/addPrenatalVisitRecord.form")
public class AddPrenatalVisitRecordController extends BaseUpdateMaternalCarePatientConsultDataController {
	/** Auto-wired person service */
	private PersonService personService;

	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public MaternalCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// do pre-initialization via superclass
		final MaternalCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		if (form.getPatient() != null) {
			// initialize the main Maternal Care Program Observation
			final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(form.getPatient());
			form.setMcProgramObs(mcProgramObs);

			// initialize a blank PrenatalVisitRecord instance for entering the data
			form.setPrenatalVisitRecord(new PrenatalVisitRecord());

			// set the 'must see physician' value
			form.getMcProgramObs().setNeedsToSeePhysician(RelationshipUtil.isMustSeePhysician(form.getPatient()));

			// this is the maternal care program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		}

		// return the patient
		return form;
	}

	/**
	 * Overridden to set default options.
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form) {
		// set default values only for new records
		if (form.getPrenatalVisitRecord().getObs().getObsId() == null || form.getPrenatalVisitRecord().getObs().getObsId() == 0) {
			// set default visit type to 'health facility'
			form.getPrenatalVisitRecord().getMember(MCPrenatalVisitRecordConcepts.VISIT_TYPE)
					.setValueCoded(Functions.conceptByIdOrName("Private Health Facility"));

			// default 'nutritionally at risk' should be 'No'
			form.getPrenatalVisitRecord().getMember(MCPrenatalVisitRecordConcepts.NUTRITIONALLY_AT_RISK).setValueCoded(Functions.falseConcept());

			// default visit date should be today
			form.getPrenatalVisitRecord().getMember(MCPrenatalVisitRecordConcepts.VISIT_DATE).setValueText(Context.getDateFormat().format(new Date()));
		}

		// dispatch to regular superclass
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * Perform other non-standard validation on form fields.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, MaternalCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// set boolean values to 'false' concept if null (i.e., unticked)
		setNonTrueObsToFalse(form.getPrenatalVisitRecord().getObs(), //
				MCPrenatalVisitRecordConcepts.NUTRITIONALLY_AT_RISK);

		// set boolean values to 'false' concept if null (i.e., unticked)
		setNonTrueObsToFalse(form.getPrenatalVisitRecord().getDangerSigns().getObs(), //
				MCDangerSignsConcepts.SEVERE_HEADACHE, //
				MCDangerSignsConcepts.DIZZINESS, //
				MCDangerSignsConcepts.BLURRING_OF_VISION, //
				MCDangerSignsConcepts.VAGINAL_BLEEDING, //
				MCDangerSignsConcepts.FEVER, //
				MCDangerSignsConcepts.EDEMA);

		// set boolean values to 'false' concept if null (i.e., unticked)
		setNonTrueObsToFalse(form.getPrenatalVisitRecord().getNewMedicalConditions().getObs(), //
				MCMedicalHistoryConcepts.HYPERTENSION, //
				MCMedicalHistoryConcepts.ASTHMA, //
				MCMedicalHistoryConcepts.DIABETES, //
				MCMedicalHistoryConcepts.TUBERCULOSIS, //
				MCMedicalHistoryConcepts.HEART_DISEASE, //
				MCMedicalHistoryConcepts.ALLERGY, //
				MCMedicalHistoryConcepts.STI, //
				MCMedicalHistoryConcepts.BLEEDING_DISORDERS, //
				MCMedicalHistoryConcepts.THYROID, //
				MCMedicalHistoryConcepts.OTHERS); //

		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "prenatalVisitRecord.observationMap", errors);
		PatientConsultEntryFormValidator.validateObservationMap(form, "prenatalVisitRecord.obstetricExamination.observationMap", errors);
		PatientConsultEntryFormValidator.validateObservationMap(form, "prenatalVisitRecord.dangerSigns.observationMap", errors);
		PatientConsultEntryFormValidator.validateObservationMap(form, "prenatalVisitRecord.newMedicalConditions.observationMap", errors);

		// check that all required fields have been specified
		PatientConsultEntryFormValidator.validateRequiredFields(form, "prenatalVisitRecord.observationMap", errors, //
				MCPrenatalVisitRecordConcepts.VISIT_DATE, //
				MCPrenatalVisitRecordConcepts.VISIT_TYPE);

		// validate date value must be after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateValidPastDates(form, "prenatalVisitRecord.observationMap", errors, //
				MCPrenatalVisitRecordConcepts.VISIT_DATE);

		// validate transaction date in case of in case of 'referred' patient consult status
		validateTransactionDate(form, "prenatalVisitRecord", MCPrenatalVisitRecordConcepts.VISIT_DATE, errors);

		// further validation on visit date: if the delivery record has been accomplished, the date of the prenatal record should be before the Delivery Date
		final String visitDatePath = "prenatalVisitRecord.observationMap[" + MCPrenatalVisitRecordConcepts.VISIT_DATE.getConceptId() + "].valueText";
		if (!errors.hasFieldErrors(visitDatePath)) {
			if (!isDateBeforeDeliveryDate(form, form.getPrenatalVisitRecord(), MCPrenatalVisitRecordConcepts.VISIT_DATE)) {
				errors.rejectValue(visitDatePath, "chits.program.MATERNALCARE.prenatal.visit.date.must.be.before.delivery.date");
			}
		}

		// If values for fundic height and FHR are entered, the FHR location and Fetal presentation are required to be filled.
		final boolean fundicHeightSpecified = !PatientConsultEntryFormValidator.fieldEmpty(form, "prenatalVisitRecord.obstetricExamination.observationMap",
				errors, MCObstetricExamination.FUNDIC_HEIGHT);
		final boolean fhrSpecified = !PatientConsultEntryFormValidator.fieldEmpty(form, "prenatalVisitRecord.obstetricExamination.observationMap", errors,
				MCObstetricExamination.FHR);
		if (fundicHeightSpecified && fhrSpecified) {
			PatientConsultEntryFormValidator.validateRequiredFields(form, "prenatalVisitRecord.obstetricExamination.observationMap", errors, //
					MCObstetricExamination.FHR_LOCATION, //
					MCObstetricExamination.FETAL_PRESENTATION);
		}
	}

	/**
	 * Validates that the visit date (of the corresponding visitDateConcept) is before the delivery date.
	 * 
	 * @return false if the date is not before the delivery date.
	 */
	protected static boolean isDateBeforeDeliveryDate(MaternalCareConsultEntryForm form, GroupObs groupObs, CachedConceptId visitDateConcept) {
		// further validation on visit date: if the delivery record has been accomplished, the visit date should be before the Delivery Date
		final Obs visitDateObs = groupObs.getMember(visitDateConcept);
		final Date visitDate = visitDateObs != null ? visitDateObs.getValueDatetime() : null;

		final boolean hasDeliveryReport = Functions.observation(form.getMcProgramObs().getObs(), MCDeliveryReportConcepts.DELIVERY_REPORT) != null;
		if (hasDeliveryReport) {
			final Obs deliveryDateObs = form.getMcProgramObs().getDeliveryReport().getMember(MCDeliveryReportConcepts.DELIVERY_DATE);
			final Date deliveryDate = deliveryDateObs != null ? deliveryDateObs.getValueDatetime() : null;

			if (visitDate != null && deliveryDate != null) {
				if (!DateUtil.stripTime(visitDate).before(DateUtil.stripTime(deliveryDate))) {
					// date of visit date concept should be before the delivery date
					return false;
				}
			}
		}

		// either validation successful or unable to validate
		return true;
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// add the prenatal visit record to the encounter for processing
		setUpdatedAndAddToEncounter(form, form.getPrenatalVisitRecord().getObs());

		// NOTE: Just add directly to the mc program observation as a group member for directly storing
		// all prenatal visit records (i.e., no need for an "overall" prenatal visit record like the
		// overall "obstetric history" record which contains "obstetric history detail" members"
		form.getMcProgramObs().getObs().addGroupMember(form.getPrenatalVisitRecord().getObs());

		// ensure all observations refer to the correct patient
		form.getPrenatalVisitRecord().storePersonAndAudit(form.getPatient());
	}

	@Override
	protected void beforeSave(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// update the 'Must See Physician' flag
		RelationshipUtil.setMustSeePhysicianFlag(form.getPatient(), form.getMcProgramObs().isNeedsToSeePhysician());
		personService.savePerson(form.getPatient());
	}

	/**
	 * The version object is the current prenatal visit record entry being edited / created.
	 */
	@Override
	protected Auditable getVersionObject(MaternalCareConsultEntryForm form) {
		// any changes to the "prenatal visit record" entry being edited indicates a concurrent update
		return form.getPrenatalVisitRecord().getObs();
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/maternalcare/ajaxAddEditPrenatalVisitRecord";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// no need to redirect, the rendering page should close the dialog
		return "/module/chits/consults/maternalcare/ajaxAddEditPrenatalVisitRecord";
	}

	@Autowired
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
}
