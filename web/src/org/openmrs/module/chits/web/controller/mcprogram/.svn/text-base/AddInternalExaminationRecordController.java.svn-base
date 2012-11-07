package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.InternalExaminationRecord;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCIEOptions;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCIERecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Adds a new internal examination record to the patient's current active maternal care program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/addInternalExaminationRecord.form")
public class AddInternalExaminationRecordController extends BaseUpdateMaternalCarePatientConsultDataController {
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

			// initialize a blank InternalExaminationRecord instance for entering the data
			form.setInternalExaminationRecord(new InternalExaminationRecord());

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
		if (form.getInternalExaminationRecord().getObs().getObsId() == null || form.getInternalExaminationRecord().getObs().getObsId() == 0) {
			// default visit date should be today
			form.getInternalExaminationRecord().getMember(MCIERecordConcepts.VISIT_DATE).setValueText(Context.getDateFormat().format(new Date()));

			// set default "external genitalia" to "normal"
			form.getInternalExaminationRecord().getMember(MCIERecordConcepts.EXTERNAL_GENITALIA).setValueCoded(Functions.concept(MCIEOptions.NORMAL));

			// set default "vagina" to "parous"
			form.getInternalExaminationRecord().getMember(MCIERecordConcepts.VAGINA).setValueCoded(Functions.concept(MCIEOptions.PAROUS));

			// set default "cervix" to "closed"
			form.getInternalExaminationRecord().getMember(MCIERecordConcepts.CERVIX_STATE).setValueCoded(Functions.concept(MCIEOptions.CLOSED));

			// set default "adnexal mass / tenderness" to "negative"
			form.getInternalExaminationRecord().getMember(MCIERecordConcepts.TENDERNESS).setValueCoded(Functions.concept(MCIEOptions.NEGATIVE));

			// set default "pelvimetry" to "adequate"
			form.getInternalExaminationRecord().getMember(MCIERecordConcepts.PELVIMETRY).setValueCoded(Functions.concept(MCIEOptions.ADEQUATE));

			// set default "membranes" to "intact"
			form.getInternalExaminationRecord().getMember(MCIERecordConcepts.MEMBRANES).setValueCoded(Functions.concept(MCIEOptions.INTACT));

			// set default "bloody show" to "negative"
			form.getInternalExaminationRecord().getMember(MCIERecordConcepts.BLOODY_SHOW).setValueCoded(Functions.concept(MCIEOptions.NEGATIVE));
		}

		// dispatch to regular superclass
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * Perform other non-standard validation on form fields.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, MaternalCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "internalExaminationRecord.observationMap", errors);

		// check that all required fields have been specified
		PatientConsultEntryFormValidator.validateRequiredFields(form, "internalExaminationRecord.observationMap", errors, //
				MCIERecordConcepts.VISIT_DATE);

		// validate date value must be after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateValidPastDates(form, "internalExaminationRecord.observationMap", errors, //
				MCIERecordConcepts.VISIT_DATE);

		// validate transaction date in case of in case of 'referred' patient consult status
		validateTransactionDate(form, "internalExaminationRecord", MCIERecordConcepts.VISIT_DATE, errors);

		// if external genitalia is 'others', then the text field is required
		if (form.getInternalExaminationRecord().getMember(MCIERecordConcepts.EXTERNAL_GENITALIA).getValueCoded().equals(Functions.concept(MCIEOptions.OTHERS))) {
			PatientConsultEntryFormValidator.validateRequiredFields(form, "internalExaminationRecord.observationMap", errors, //
					MCIERecordConcepts.EXTERNAL_GENITALIA_TEXT);
		}

		// further validation on visit date: if the delivery record has been accomplished, the date of the IE record should be before the Delivery Date
		final String visitDatePath = "internalExaminationRecord.observationMap[" + MCIERecordConcepts.VISIT_DATE.getConceptId() + "].valueText";
		if (!errors.hasFieldErrors(visitDatePath)) {
			if (!AddPrenatalVisitRecordController.isDateBeforeDeliveryDate(form, form.getInternalExaminationRecord(), MCIERecordConcepts.VISIT_DATE)) {
				errors.rejectValue(visitDatePath, "chits.program.MATERNALCARE.ie.visit.date.must.be.before.delivery.date");
			}
		}
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void beforeSave(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, java.util.Collection<Obs> obsToSave,
			java.util.Collection<Obs> obsToPurge) {
		// add the internal examination record to the encounter for processing
		setUpdatedAndAddToEncounter(form, form.getInternalExaminationRecord().getObs());

		// NOTE: Just add directly to the mc program observation as a group member for directly storing
		// all internal examination records (i.e., no need for an "overall" internal examination record like the
		// overall "obstetric history" record which contains "obstetric history detail" members"
		form.getMcProgramObs().getObs().addGroupMember(form.getInternalExaminationRecord().getObs());

		// ensure all observations refer to the correct patient
		form.getInternalExaminationRecord().storePersonAndAudit(form.getPatient());
	}

	/**
	 * The version object is the current internal examination record entry being edited / created.
	 */
	@Override
	protected Auditable getVersionObject(MaternalCareConsultEntryForm form) {
		// any changes to the "internal examination record" entry being edited indicates a concurrent update
		return form.getInternalExaminationRecord().getObs();
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/maternalcare/ajaxAddEditInternalExaminationRecord";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// no need to redirect, the rendering page should close the dialog
		return "/module/chits/consults/maternalcare/ajaxAddEditInternalExaminationRecord";
	}
}
