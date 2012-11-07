package org.openmrs.module.chits.web.controller.mcprogram;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Obs;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.MaternalCareConsultEntryForm;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPatientConsultStatus;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MaternalCareProgramStates;
import org.openmrs.module.chits.mcprogram.MaternalCareUtil;
import org.openmrs.module.chits.mcprogram.PatientConsultStatus;
import org.openmrs.module.chits.obs.GroupObs;
import org.openmrs.module.chits.web.controller.BaseUpdatePatientConsultDataController;
import org.openmrs.web.WebConstants;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Decorator around the {@link BaseUpdatePatientConsultDataController} that intercepts POST submissions and sends back to the input page with an error if the
 * patient's maternal care program has already been concluded.
 * <p>
 * Additionally, this method converts the superclass&apos; {@link PatientConsultEntryForm} form backing object to a {@link MaternalCareConsultEntryForm} type
 * after generifying "T" to {@link MaternalCareConsultEntryForm}.
 * 
 * @author Bren
 */
public abstract class BaseUpdateMaternalCarePatientConsultDataController extends BaseUpdatePatientConsultDataController<MaternalCareConsultEntryForm> {
	/**
	 * Convert the superclass&apos; {@link PatientConsultEntryForm} to a {@link MaternalCareConsultEntryForm} type.
	 */
	@Override
	public MaternalCareConsultEntryForm formBackingObject(HttpServletRequest request, ModelMap model, Integer patientId) throws ServletException {
		final PatientConsultEntryForm pForm = super.formBackingObject(request, model, patientId);

		// convert to a maternal care consult entry form
		final MaternalCareConsultEntryForm form = new MaternalCareConsultEntryForm();
		try {
			PropertyUtils.copyProperties(form, pForm);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}

		// return the encapsulated form
		return form;
	}

	/**
	 * Performs maternal care pre-requisite checks before forwarding to the update form.
	 * 
	 * @param request
	 * @param httpSession
	 * @param model
	 * @param form
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form) {
		// verify that the patient meets the maternal care prerequisites before showing the form
		if (!MaternalCareUtil.maternalCarePrerequisitesMet(form)) {
			// maternal care prerequisites have not been met: redirect using patient id
			final Integer patientId = form.getPatient() != null ? form.getPatient().getPatientId() : null;

			// send back to the view consults page
			return viewPatientConsultsController.redirect(patientId);
		} else {
			// ok to proceed: dispatch to superclass
			return super.showForm(request, httpSession, model, form);
		}
	}

	/**
	 * Intercepts the submission and sends back to input page with an error if the patient's maternal care program has already been concluded.
	 */
	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@ModelAttribute("form") MaternalCareConsultEntryForm form, //
			BindingResult errors) {
		if (MaternalCareUtil.isProgramClosedFor(form.getPatient())) {
			// maternal care program already concluded, updates are not allowed!
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.program.MATERNALCARE.program.closed");

			// send back to the input page
			return getInputPath(request);
		} else if (!MaternalCareUtil.maternalCarePrerequisitesMet(form)) {
			// maternal care prerequisites have not been met: cannot post updates!
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "chits.consult.submission.data.errors");

			// maternal care prerequisites have not been met: redirect using patient id
			final Integer patientId = form.getPatient() != null ? form.getPatient().getPatientId() : null;

			// send back to the view consults page
			return viewPatientConsultsController.redirect(patientId);
		} else {
			// ok to proceed: dispatch to superclass
			return super.handleSubmission(httpSession, request, model, form, errors);
		}
	}

	/**
	 * This method validates the transaction date based on the patient consult status (including back-dated transactions).
	 */
	protected void validateTransactionDate(MaternalCareConsultEntryForm form, String groupObsPath, CachedConceptId txnDateConcept, BindingResult errors) {
		// calculate path to the field
		final String fieldPath = groupObsPath + ".observationMap[" + txnDateConcept.getConceptId() + "].valueText";
		if (errors.hasFieldErrors(fieldPath)) {
			// only validate if the field in question doesn't already have a validation error
			return;
		}

		// extract the group obs containing the transaction date value
		final GroupObs groupObs;
		try {
			groupObs = (GroupObs) PropertyUtils.getProperty(form, groupObsPath);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid path '" + groupObsPath + "' for form: " + form);
		}

		// extract the transaction date entered by the user (if any)
		final Obs txnDateObs = groupObs != null ? groupObs.getMember(txnDateConcept) : null;
		final Date txnDate = txnDateObs != null && txnDateObs.getValueDatetime() != null ? DateUtil.stripTime(txnDateObs.getValueDatetime()) : null;
		if (txnDate == null) {
			// unable to validate transaction date
			return;
		}

		// determine the patient consult status from the given date: get all statuses
		final List<PatientConsultStatus> statuses = form.getMcProgramObs().getAllPatientConsultStatus();

		// reverse the order so that the latest status is at position 0 in the list
		Collections.reverse(statuses);

		// find the patient consult status at the time of the transaction date
		PatientConsultStatus statusAtTxnDate = null;
		while (!statuses.isEmpty()) {
			// get latest status
			statusAtTxnDate = statuses.get(0);
			final Obs effectivityDateObs = statusAtTxnDate.getMember(MCPatientConsultStatus.DATE_OF_CHANGE);
			final Date effectivityDate = effectivityDateObs != null ? effectivityDateObs.getValueDatetime() : null;
			if (effectivityDate == null || DateUtil.stripTime(effectivityDate).after(txnDate)) {
				// this status applies after the transaction date, so keep going backwards
				// to find the status at the time of the transaction
				statusAtTxnDate = null;
				statuses.remove(0);
				continue;
			} else {
				// this is the status at the time of the transaction
				break;
			}
		}

		// if status is 'REFERRED', then this transaction is not allowed
		if (statusAtTxnDate != null && statusAtTxnDate.getStatus() == MaternalCareProgramStates.REFERRED) {
			// transactions not allowed when the status is 'REFERRED'
			errors.rejectValue(fieldPath, "chits.program.MATERNALCARE.transactions.not.allowed.during.referred.status");
		}
	}

	/**
	 * Invokes setUpdated() on- and adds- the given observations to the form's encounter bean.
	 * 
	 * @param form
	 *            The form containing the encounter
	 * @param observations
	 *            The observations to invoke setUpdated() on and add to the encounter.
	 */
	protected void setUpdatedAndAddToEncounter(MaternalCareConsultEntryForm form, Obs... observations) {
		for (Obs obs : observations) {
			form.getEncounter().addObs(obs);
			setUpdated(obs);
		}
	}
}
