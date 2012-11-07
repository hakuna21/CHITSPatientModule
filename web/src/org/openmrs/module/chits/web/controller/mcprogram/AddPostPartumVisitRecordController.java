package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Auditable;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumEventsConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostPartumVisitRecordConcepts;
import org.openmrs.module.chits.mcprogram.MaternalCareProgramObs;
import org.openmrs.module.chits.mcprogram.PostPartumVisitRecord;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.controller.genconsults.EnrollInProgramController;
import org.openmrs.module.chits.web.taglib.Functions;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Adds a new post-partum visit record to the patient's current active maternal care program.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/addPostPartumVisitRecord.form")
public class AddPostPartumVisitRecordController extends BaseUpdateMaternalCarePatientConsultDataController {
	/** Request attribute flagged if form should redirect page to FP registration */
	private static final String AJAX_REDIRECT_TO_FP = "pp.visit.redirect.to.fp.after.save";
	
	/** Auto-wired program workflow service */
	private ProgramWorkflowService programWorkflowService;

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

			// initialize a blank PostPartumVisitRecord instance for entering the data
			form.setPostPartumVisitRecord(new PostPartumVisitRecord());

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
		if (form.getPostPartumVisitRecord().getObs().getObsId() == null || form.getPostPartumVisitRecord().getObs().getObsId() == 0) {
			// default visit date should be today
			form.getPostPartumVisitRecord().getMember(MCPostPartumVisitRecordConcepts.VISIT_DATE).setValueText(Context.getDateFormat().format(new Date()));
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
		setNonTrueObsToFalse(form.getPostPartumVisitRecord().getObs(), //
				MCPostPartumVisitRecordConcepts.BREASTFED_WITHIN_HOUR, //
				MCPostPartumVisitRecordConcepts.BREASTFED_EXCLUSIVELY);

		// set boolean values to 'false' concept if null (i.e., unticked)
		setNonTrueObsToFalse(form.getPostPartumVisitRecord().getPostPartumEvents().getObs(), //
				MCPostPartumEventsConcepts.VAGINAL_INFECTION, //
				MCPostPartumEventsConcepts.VAGINAL_BLEEDING, //
				MCPostPartumEventsConcepts.FEVER_OVER_38, //
				MCPostPartumEventsConcepts.PALLOR, //
				MCPostPartumEventsConcepts.CORD_NORMAL);

		// perform standard validation
		PatientConsultEntryFormValidator.validateObservationMap(form, "postPartumVisitRecord.observationMap", errors);
		PatientConsultEntryFormValidator.validateObservationMap(form, "postPartumVisitRecord.postPartumEvents.observationMap", errors);

		// check that all required fields have been specified
		PatientConsultEntryFormValidator.validateRequiredFields(form, "postPartumVisitRecord.observationMap", errors, //
				MCPostPartumVisitRecordConcepts.VISIT_DATE, //
				MCPostPartumVisitRecordConcepts.VISIT_TYPE);

		// validate date value must be after the patient's birth date and not in future
		PatientConsultEntryFormValidator.validateValidPastDates(form, "postPartumVisitRecord.observationMap", errors, //
				MCPostPartumVisitRecordConcepts.VISIT_DATE);

		// validate transaction date in case of in case of 'referred' patient consult status
		validateTransactionDate(form, "postPartumVisitRecord", MCPostPartumVisitRecordConcepts.VISIT_DATE, errors);

		// further validation on visit date: if the delivery record has been accomplished, the date of the postpartum record should be on or after the Delivery
		// Date
		final String visitDatePath = "postPartumVisitRecord.observationMap[" + MCPostPartumVisitRecordConcepts.VISIT_DATE.getConceptId() + "].valueText";
		if (!errors.hasFieldErrors(visitDatePath)) {
			if (AddPrenatalVisitRecordController.isDateBeforeDeliveryDate(form, form.getPostPartumVisitRecord(), MCPostPartumVisitRecordConcepts.VISIT_DATE)) {
				errors.rejectValue(visitDatePath, "chits.program.MATERNALCARE.postpartum.visit.date.must.not.be.before.delivery.date");
			}
		}
		
		// no errors, should we send to FP after this step?
		if (!errors.hasErrors() && Boolean.valueOf(request.getParameter("fpAfterSave"))) {
			if (!Functions.isInProgram(form.getPatient(), ProgramConcepts.FAMILYPLANNING)) {
				// never been enrolled in FP before: enroll the patient in the FP program
				final Program program = programWorkflowService.getProgram(ProgramConcepts.FAMILYPLANNING.getProgramId());
				EnrollInProgramController.enrollPatientInProgram(programWorkflowService, form.getPatient(), program);
				request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "chits.program.MATERNALCARE.enroll.in.family.planning.success");
			}

			// take the user to the family planning registration pages after saving
			// NOTE: If user is already registered to FP, this will send them to the FP regular access screen, otherwise, they will be sent to the FP
			// registration pages
			request.setAttribute(AJAX_REDIRECT_TO_FP, Boolean.TRUE);
		}
	}

	/**
	 * Setup the main observations.
	 */
	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, MaternalCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// add the post-partum visit record to the encounter for processing
		setUpdatedAndAddToEncounter(form, form.getPostPartumVisitRecord().getObs());

		// NOTE: Just add directly to the mc program observation as a group member for directly storing
		// all post-partum visit records (i.e., no need for an "overall" post-partum visit record like the
		// overall "obstetric history" record which contains "obstetric history detail" members"
		form.getMcProgramObs().getObs().addGroupMember(form.getPostPartumVisitRecord().getObs());

		// ensure all observations refer to the correct patient
		form.getPostPartumVisitRecord().storePersonAndAudit(form.getPatient());
	}

	/**
	 * The version object is the current post-partum visit record entry being edited / created.
	 */
	@Override
	protected Auditable getVersionObject(MaternalCareConsultEntryForm form) {
		// any changes to the "post-partum visit record" entry being edited indicates a concurrent update
		return form.getPostPartumVisitRecord().getObs();
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/maternalcare/ajaxAddEditPostPartumVisitRecord";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		if (Boolean.TRUE.equals(request.getAttribute(AJAX_REDIRECT_TO_FP))) {
			// NOTE: If user is already registered to FP, this will send them to the FP regular access screen, otherwise, they will be sent to the FP
			// registration pages
			return "/module/chits/consults/maternalcare/ajaxRedirectToFamilyPlanningProgram";
		} else {
			// no need to redirect, the rendering page should close the dialog
			return "/module/chits/consults/maternalcare/ajaxAddEditPostPartumVisitRecord";
		}
	}
	
	@Autowired
	public void setProgramWorkflowService(ProgramWorkflowService programWorkflowService) {
		this.programWorkflowService = programWorkflowService;
	}
}
