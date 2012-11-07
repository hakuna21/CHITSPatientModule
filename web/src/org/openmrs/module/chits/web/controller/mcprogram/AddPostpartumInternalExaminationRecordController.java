package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostpartumIERecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.mcprogram.PostpartumInternalExaminationRecord;
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
 * Adds a new postpartum internal examination record to the patient's current active maternal care program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/addPostpartumInternalExaminationRecord.form")
public class AddPostpartumInternalExaminationRecordController extends BaseUpdateMaternalCarePatientConsultDataController {
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
			form.setPostpartumInternalExaminationRecord(new PostpartumInternalExaminationRecord());

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
		if (form.getPostpartumInternalExaminationRecord().getObs().getObsId() == null || form.getPostpartumInternalExaminationRecord().getObs().getObsId() == 0) {
			// default visit date should be today
			form.getPostpartumInternalExaminationRecord().getMember(MCPostpartumIERecordConcepts.VISIT_DATE)
					.setValueText(Context.getDateFormat().format(DateUtil.stripTime(new Date())));

			// by default, assume this is a routine visit
			form.getPostpartumInternalExaminationRecord().getMember(MCPostpartumIERecordConcepts.POSTDELIVERY_IE).setValueCoded(Functions.falseConcept());
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
		PatientConsultEntryFormValidator.validateObservationMap(form, "postpartumInternalExaminationRecord.observationMap", errors);

		// check that all required fields have been specified
		PatientConsultEntryFormValidator.validateRequiredFields(form, "postpartumInternalExaminationRecord.observationMap", errors, //
				MCPostpartumIERecordConcepts.VISIT_DATE);

		// validate date value must be after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateValidPastDates(form, "postpartumInternalExaminationRecord.observationMap", errors, //
				MCPostpartumIERecordConcepts.VISIT_DATE);

		// validate transaction date in case of in case of 'referred' patient consult status
		validateTransactionDate(form, "postpartumInternalExaminationRecord", MCPostpartumIERecordConcepts.VISIT_DATE, errors);

		// further validation on visit date: if the delivery record has been accomplished, the date of the postpartum IE record should be on or after the
		// Delivery Date
		final String visitDatePath = "postpartumInternalExaminationRecord.observationMap[" + MCPostpartumIERecordConcepts.VISIT_DATE.getConceptId()
				+ "].valueText";
		if (!errors.hasFieldErrors(visitDatePath)) {
			if (AddPrenatalVisitRecordController.isDateBeforeDeliveryDate(form, form.getPostpartumInternalExaminationRecord(),
					MCPostpartumIERecordConcepts.VISIT_DATE)) {
				errors.rejectValue(visitDatePath, "chits.program.MATERNALCARE.postpartum.ie.visit.date.must.not.be.before.delivery.date");
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
		setUpdatedAndAddToEncounter(form, form.getPostpartumInternalExaminationRecord().getObs());

		// store the 'visitTime' component from the record into itself to trigger storage into the observation
		final PostpartumInternalExaminationRecord record = form.getPostpartumInternalExaminationRecord();
		record.storeVisitTimeIntoVisitDate();

		// NOTE: Just add directly to the mc program observation as a group member for directly storing
		// all internal examination records (i.e., no need for an "overall" internal examination record like the
		// overall "obstetric history" record which contains "obstetric history detail" members"
		form.getMcProgramObs().getObs().addGroupMember(form.getPostpartumInternalExaminationRecord().getObs());

		// ensure all observations refer to the correct patient
		form.getPostpartumInternalExaminationRecord().storePersonAndAudit(form.getPatient());
	}

	/**
	 * The version object is the current record being edited.
	 */
	@Override
	protected Auditable getVersionObject(MaternalCareConsultEntryForm form) {
		// any changes to the "postpartum internal examination record" entry being edited indicates a concurrent update
		return form.getPostpartumInternalExaminationRecord().getObs();
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/maternalcare/ajaxAddEditPostpartumInternalExaminationRecord";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// no need to redirect, the rendering page should close the dialog
		return "/module/chits/consults/maternalcare/ajaxAddEditPostpartumInternalExaminationRecord";
	}
}
