package org.openmrs.module.chits.web.controller.mcprogram;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.DeliveryReport;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCDeliveryReportConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPregnancyOutcomeConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.mcprogram.ObstetricHistoryDetail;
import org.openmrs.module.chits.mcprogram.PregnancyOutcome;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Edits the delivery report record of the patient's current active maternal care program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/editDeliveryReport.form")
public class EditDeliveryReportController extends BaseUpdateMaternalCarePatientConsultDataController {
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

			// this is the maternal care program
			form.setProgram(ProgramConcepts.MATERNALCARE);
		}

		// return the patient
		return form;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form) {
		// load relevant beans
		final MaternalCareProgramObs mcProgramObs = form.getMcProgramObs();
		final DeliveryReport deliveryReport = mcProgramObs != null ? mcProgramObs.getDeliveryReport() : null;
		final ObstetricHistoryDetail obstetricHistoryDetail = deliveryReport != null ? deliveryReport.getObstetricHistoryDetail() : null;

		// adds one blank pregnancy outcome for the user to fill-in if there are currently none
		if (obstetricHistoryDetail != null && obstetricHistoryDetail.getOutcomes().isEmpty()) {
			// add a blank pregnancy outcome
			obstetricHistoryDetail.addChild(new PregnancyOutcome());
		}

		// set default values only for new records
		if (deliveryReport != null && (deliveryReport.getObs().getObsId() == null || deliveryReport.getObs().getObsId() == 0)) {
			final String today = Context.getDateFormat().format(new Date());

			// default delivery date should be today
			deliveryReport.getMember(MCDeliveryReportConcepts.DELIVERY_DATE).setValueText(today);

			if (obstetricHistoryDetail != null) {
				// default location of delivery is 'home'
				obstetricHistoryDetail.getMember(MCObstetricHistoryDetailsConcepts.PLACE_OF_DELIVERY).setValueCoded(Functions.conceptByIdOrName("home"));
			}

			// birth attendant should default to 'midwife'
			deliveryReport.getMember(MCDeliveryReportConcepts.BIRTH_ATTENDANT).setValueCoded(Functions.conceptByIdOrName("midwife"));

			// Date and Time Initiated Breastfeeding consists of a Date input set by
			// default to the current date and a time input that is blank by default.
			deliveryReport.getMember(MCDeliveryReportConcepts.DATE_INITIATED_BREASTFEEDING).setValueText(today);
		}

		// dispatch to superclass
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * Perform other non-standard validation on form fields.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, MaternalCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// load relevant beans
		final MaternalCareProgramObs mcProgramObs = form.getMcProgramObs();
		final DeliveryReport deliveryReport = mcProgramObs != null ? mcProgramObs.getDeliveryReport() : null;
		final ObstetricHistoryDetail obstetricHistoryDetail = deliveryReport != null ? deliveryReport.getObstetricHistoryDetail() : null;

		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "mcProgramObs.deliveryReport.observationMap", errors);
		PatientConsultEntryFormValidator.validateObservationMap(form, "mcProgramObs.deliveryReport.obstetricHistoryDetail.observationMap", errors);

		// check that all required fields have been specified
		PatientConsultEntryFormValidator.validateRequiredFields(form, "mcProgramObs.deliveryReport.observationMap", errors, //
				MCDeliveryReportConcepts.DELIVERY_DATE, //
				MCDeliveryReportConcepts.OBSTETRIC_SCORE_PARA, //
				MCDeliveryReportConcepts.OBSTETRIC_SCORE_FT, //
				MCDeliveryReportConcepts.OBSTETRIC_SCORE_PT, //
				MCDeliveryReportConcepts.OBSTETRIC_SCORE_AM, //
				MCDeliveryReportConcepts.OBSTETRIC_SCORE_LC);

		// input for each of the fields should not be less than the previous value.
		for (CachedConceptId score : new CachedConceptId[] { MCObstetricHistoryDetailsConcepts.GRAVIDA, //
				MCDeliveryReportConcepts.OBSTETRIC_SCORE_PARA, //
				MCDeliveryReportConcepts.OBSTETRIC_SCORE_FT, //
				MCDeliveryReportConcepts.OBSTETRIC_SCORE_PT, //
				MCDeliveryReportConcepts.OBSTETRIC_SCORE_AM, //
				MCDeliveryReportConcepts.OBSTETRIC_SCORE_LC }) {
			final Obs oldScoreObs = mcProgramObs.getObstetricHistory().getMember(score);
			final Obs newScoreObs;
			if (MCObstetricHistoryDetailsConcepts.GRAVIDA == score) {
				newScoreObs = mcProgramObs.getDeliveryReport().getObstetricHistoryDetail().getMember(score);
			} else {
				newScoreObs = mcProgramObs.getDeliveryReport().getMember(score);
			}

			// get the numeric values
			final Double oldScoreValue = oldScoreObs != null ? oldScoreObs.getValueNumeric() : null;
			final Double newScoreValue = newScoreObs != null ? newScoreObs.getValueNumeric() : null;

			if (oldScoreValue != null && newScoreValue != null && newScoreValue < oldScoreValue) {
				// field should not be less than the previous value!
				if (MCObstetricHistoryDetailsConcepts.GRAVIDA == score) {
					errors.rejectValue("mcProgramObs.deliveryReport.obstetricHistoryDetail.observationMap[" //
							+ score.getConceptId() + "].valueText", "chits.program.MATERNALCARE.cannot.be.less.than.previous.value");
				} else {
					errors.rejectValue("mcProgramObs.deliveryReport.observationMap[" //
							+ score.getConceptId() + "].valueText", "chits.program.MATERNALCARE.cannot.be.less.than.previous.value");
				}
			}
		}

		PatientConsultEntryFormValidator.validateRequiredFields(form, "mcProgramObs.deliveryReport.obstetricHistoryDetail.observationMap", errors, //
				MCObstetricHistoryDetailsConcepts.GRAVIDA);

		// store month component into the 'year of pregnancy' datetime value from the delivery date
		final Obs deliveryDateObs = deliveryReport.getObservationMap().get(MCDeliveryReportConcepts.DELIVERY_DATE.getConceptId());
		final Obs yearOfPregnancyObs = obstetricHistoryDetail.getObservationMap().get(MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY.getConceptId());
		if (deliveryDateObs != null && deliveryDateObs.getValueDatetime() != null && yearOfPregnancyObs != null) {
			final Calendar c = Calendar.getInstance();
			c.setTime(deliveryDateObs.getValueDatetime());

			// store delivery date as year of pregnancy
			yearOfPregnancyObs.setValueDatetime(c.getTime());
			yearOfPregnancyObs.setValueText(new SimpleDateFormat("yyyy").format(c.getTime()));

			// NOTE: Month is zero-based, so subtract one from the month value which is stored in the valueNumeric field
			yearOfPregnancyObs.setValueNumeric(new Double(c.get(Calendar.MONTH) + 1));
		}

		// validate date value must be after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateValidPastDates(form, "mcProgramObs.deliveryReport.observationMap", errors, //
				MCDeliveryReportConcepts.DELIVERY_DATE, //
				MCDeliveryReportConcepts.DATE_INITIATED_BREASTFEEDING);

		// require at least one pregnancy outcome
		boolean hasOutcome = false;
		final List<PregnancyOutcome> children = obstetricHistoryDetail.getChildren();
		for (int i = 0; i < children.size(); i++) {
			final PregnancyOutcome outcome = children.get(i);
			if (outcome != null) {
				// a pregnancy outcome was specified: validate each pregnancy outcome
				hasOutcome = true;

				// perform standard validation
				PatientConsultEntryFormValidator.validateObservationMap(form, //
						"mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[" + i + "].observationMap", errors);

				// check that all required fields have been specified
				PatientConsultEntryFormValidator.validateRequiredFields(form, //
						"mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[" + i + "].observationMap", errors, //
						MCPregnancyOutcomeConcepts.OUTCOME, //
						MCPregnancyOutcomeConcepts.METHOD, //
						MCPregnancyOutcomeConcepts.TERM, //
						MCPregnancyOutcomeConcepts.SEX, //
						MCPregnancyOutcomeConcepts.BIRTH_WEIGHT_OF_BABY_KG);
						// MCPregnancyOutcomeConcepts.BIRTH_WEIGHT_KG);
			}
		}

		if (!hasOutcome) {
			errors.reject("chits.program.MATERNALCARE.pregnancy.outcome.required");
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.MATERNALCARE.pregnancy.outcome.required");
			return;
		}
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// mark the delivery report record's timestamp
		setUpdated(form.getMcProgramObs().getDeliveryReport().getObs());

		// ensure all observations refer to the correct patient
		form.getMcProgramObs().getDeliveryReport().storePersonAndAudit(form.getPatient());
	}

	/**
	 * The version object is the current active patient program when submitting registration information.
	 */
	@Override
	protected Auditable getVersionObject(MaternalCareConsultEntryForm form) {
		// any changes to the "delivery report" entry being edited indicates a concurrent update
		return form.getMcProgramObs().getDeliveryReport().getObs();
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/maternalcare/ajaxEditDeliveryReport";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// no need to redirect, the rendering page should refresh the MC tab and close the dialog
		return "/module/chits/consults/maternalcare/ajaxEditDeliveryReport";
	}
}
