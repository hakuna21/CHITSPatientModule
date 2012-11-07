package org.openmrs.module.chits.web.controller.fpprogram;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.FamilyPlanningConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyPlanningMethodConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFemaleMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningMethod;
import org.openmrs.module.chits.fpprogram.ServiceDeliveryRecord;
import org.openmrs.module.chits.obs.GroupObs.FieldPath;
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
 * Displays the form allowing the user to set the next service date of the latest service record.
 * 
 * @author Bren
 */
@Controller
@RequestMapping(value = "/module/chits/consults/setFamilyPlanningDateOfNextService.form")
public class SetDateOfNextServiceController extends AddServiceDeliveryRecordController {
	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public FamilyPlanningConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		// do pre-initialization via superclass to get the latest family planning method into 'familyPlanningMethod'
		final FamilyPlanningConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// store the latest service delivery record in the form for display to the user for contextual information
		// while setting the date of next service
		if (form.getPatient() != null && form.getFamilyPlanningMethod() != null) {
			// populate latest service delivery record
			final List<ServiceDeliveryRecord> serviceDeliveryRecords = form.getFamilyPlanningMethod().getServiceRecords();
			if (!serviceDeliveryRecords.isEmpty()) {
				// store the latest service delivery record into the form for display to the user
				form.setServiceDeliveryRecord(serviceDeliveryRecords.get(0));
			}
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
			@ModelAttribute("form") FamilyPlanningConsultEntryForm form) {
		final FamilyPlanningMethod fpm = form.getFamilyPlanningMethod();
		if (fpm != null) {
			// set default value of next service
			final Obs nextSvcObs = fpm.getMember(FPFamilyPlanningMethodConcepts.DATE_OF_NEXT_SERVICE);
			nextSvcObs.setValueText(Context.getDateFormat().format(calculateNextDateOfService(form)));

			// NOTE: Permanent methods do not require setting of a 'next service date'
			if (!fpm.isPermanentMethod()) {
				// remove any previous session message (which should have
				// come from the successful posting to the AddServiceDeliveryRecordController form)
				httpSession.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
			}
		}

		// dispatch to superclass
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * NOTe: see table of computations for calculating default next date of service.
	 * 
	 * @return The calculated next date of service for the given family planning method
	 */
	private Date calculateNextDateOfService(FamilyPlanningConsultEntryForm form) {
		// get current service date
		final Date today = new Date();
		final Calendar c = Calendar.getInstance();

		// get milestone dates
		Date serviceDate = form.getServiceDeliveryRecord().getDateAdministered();
		Date enrollmentDate = form.getFamilyPlanningMethod().getEnrollmentDate();
		serviceDate = DateUtil.stripTime(serviceDate != null ? serviceDate : today);
		enrollmentDate = DateUtil.stripTime(enrollmentDate != null ? enrollmentDate : today);

		final Concept fpMethod = form.getFamilyPlanningMethod().getObs().getValueCoded();
		if (Functions.concept(FPFemaleMethodOptions.PILLS).equals(fpMethod)) {
			// PILLS: Service_date + 20 days
			c.setTime(serviceDate);
			c.add(Calendar.DATE, 20);
		} else if (Functions.concept(FPFemaleMethodOptions.INJ).equals(fpMethod)) {
			// Service_date + 83 days
			c.setTime(serviceDate);
			c.add(Calendar.DATE, 83);
		} else if (Functions.concept(FPFemaleMethodOptions.IUD).equals(fpMethod)) {
			if (form.getFamilyPlanningMethod().getChildren().size() == 1) {
				// Follow up visit 1: Enrolment_date + 20 days
				c.setTime(enrollmentDate);
				c.add(Calendar.DATE, 20);
			} else {
				// Follow-up visits after visit 1: Service_date + 729 days
				c.setTime(serviceDate);
				c.add(Calendar.DATE, 729);
			}
		} else if (Functions.concept(FPFemaleMethodOptions.CONDOM).equals(fpMethod)) {
			// Service_date + 20 days
			c.setTime(serviceDate);
			c.add(Calendar.DATE, 20);
		} else if (Functions.concept(FPFemaleMethodOptions.NFP_LAM).equals(fpMethod)) {
			// Enrollment date + 183 days
			c.setTime(enrollmentDate);
			c.add(Calendar.DATE, 183);
		} else if (Functions.concept(FPFemaleMethodOptions.NFP_BB).equals(fpMethod)) {
			// Service date + 31
			c.setTime(serviceDate);
			c.add(Calendar.DATE, 31);
		} else if (Functions.concept(FPFemaleMethodOptions.NFP_CM).equals(fpMethod)) {
			// Service date + 31
			c.setTime(serviceDate);
			c.add(Calendar.DATE, 31);
		} else if (Functions.concept(FPFemaleMethodOptions.NFP_SDM).equals(fpMethod)) {
			// Service date + 31
			c.setTime(serviceDate);
			c.add(Calendar.DATE, 31);
		} else if (Functions.concept(FPFemaleMethodOptions.NFP_STM).equals(fpMethod)) {
			// Service date + 31
			c.setTime(serviceDate);
			c.add(Calendar.DATE, 31);
		}

		// return calculated date of next service
		return c.getTime();
	}

	/**
	 * Perform other non-standard validation on form fields.
	 */
	@Override
	protected void postProcess(HttpServletRequest request, FamilyPlanningConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// build field path
		final FamilyPlanningMethod fpMethodObs = form.getFamilyPlanningMethod();

		if (fpMethodObs == null) {
			// if no family planning method was loaded from the form backing object, this indicates that the patient isn't enrolled yet
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.FAMILYPLANNING.not.enrolled");
			errors.reject("chits.program.FAMILYPLANNING.not.enrolled");
		} else if (fpMethodObs.isDroppedOut()) {
			// if the patient has already dropped out from this method, then we shouldn't be adding service records to it
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.FAMILYPLANNING.method.already.dropped.out");
			errors.reject("chits.program.FAMILYPLANNING.method.already.dropped.out");
		} else {
			// setup the path for validation
			final FieldPath path = fpMethodObs.path("familyPlanningMethod");

			// remove all concepts from observation map for validation except the date of next service
			final Set<Integer> obsToRetain = new HashSet<Integer>();
			obsToRetain.add(FPFamilyPlanningMethodConcepts.DATE_OF_NEXT_SERVICE.getConceptId());
			fpMethodObs.getObservationMap().keySet().retainAll(obsToRetain);

			// perform standard validation
			PatientConsultEntryFormValidator.validateObservationMap(form, path, errors);

			// only the 'next service date' requires validation
			PatientConsultEntryFormValidator.validateRequiredFields(form, path, errors, //
					FPFamilyPlanningMethodConcepts.DATE_OF_NEXT_SERVICE);
		}
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		return "/module/chits/consults/familyplanning/ajaxSetDateOfNextService";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		return "/module/chits/consults/familyplanning/ajaxSetDateOfNextService";
	}
}
