package org.openmrs.module.chits.web.controller.eccdprogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Auditable;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.ChildCareConsultEntryForm;
import org.openmrs.module.chits.ChildCareConsultEntryForm.ServiceRecord;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServiceTypes;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.ChildCareServicesConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.DewormingServiceConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.FerrousSulfateServiceConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareConstants.VitaminAServiceConcepts;
import org.openmrs.module.chits.eccdprogram.ChildCareUtil;
import org.openmrs.module.chits.eccdprogram.ServiceUtil;
import org.openmrs.module.chits.eccdprogram.ServiceUtil.ServiceStatus;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator.DateValidationType;
import org.openmrs.module.chits.web.taglib.Functions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Submits the childcare registration form.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/addChildCareServiceRecord.form")
public class AddServiceRecordController extends BaseUpdateChildCarePatientConsultDataController implements Constants {
	/**
	 * This is called prior to displaying a form for the first time. It tells Spring the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ModelAttribute("form")
	public ChildCareConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = false, value = "patientId") Integer patientId) throws ServletException {
		final ChildCareConsultEntryForm form = super.formBackingObject(request, model, patientId);

		// prep a service record in case a new record needs to be created
		final ServiceRecord sr = new ServiceRecord();
		final Patient patient = form.getPatient();
		sr.setServiceType(newObs(ChildCareServicesConcepts.CHILDCARE_SERVICE_TYPE, patient));
		sr.setDateGiven(newObs(ChildCareServicesConcepts.DATE_ADMINISTERED, patient));
		sr.setQuantityOrDosage(newObs(ChildCareServicesConcepts.DOSAGE, patient));
		sr.setRemarks(newObs(ChildCareServicesConcepts.REMARKS, patient));
		sr.setServiceSource(newObs(ChildCareServicesConcepts.SERVICE_SOURCE, patient));
		form.setServiceRecord(sr);

		// this is the child care program
		form.setProgram(ProgramConcepts.CHILDCARE);

		return form;
	}

	/**
	 * Populates the service status model
	 */
	@ModelAttribute("serviceStatus")
	public ServiceStatus getServiceStatus(@ModelAttribute("form") ChildCareConsultEntryForm form) {
		// Extract the service status information
		final ServiceStatus serviceStatus = ServiceUtil.getServiceStatus(form.getPatient());

		// and send back the model
		return serviceStatus;
	}

	/**
	 * Override to set the issuance date the current date for Vitamin A and Deworming service types.
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, //
			HttpSession httpSession, //
			ModelMap model, //
			@ModelAttribute("form") ChildCareConsultEntryForm form) {
		final ServiceRecord sr = form.getServiceRecord();
		if (sr.isVitaminAServiceType() || sr.isDewormingServiceType() || sr.isFerrousSulfateServiceType()) {
			// for Vitamin A, Deworming, and ferrous sulfate service types, set the default issuance date to the current date
			final Obs dateGivenObs = sr.getDateGiven();
			if (dateGivenObs != null) {
				dateGivenObs.setValueDatetime(new Date());
				dateGivenObs.setValueText(Context.getDateFormat().format(dateGivenObs.getValueDatetime()));
			}
		}

		// dispatch to superclass
		return super.showForm(request, httpSession, model, form);
	}

	/**
	 * Populates the 'remarksOpts' containing the available options for the 'Remarks' drop-down list.
	 * 
	 * @param form
	 * @return
	 */
	@ModelAttribute("remarksOpts")
	public List<Concept> getRemarksOptions(@ModelAttribute("form") ChildCareConsultEntryForm form) {
		// store options for 'remarks' and 'dosage' based on the selected service type
		final ServiceRecord sr = form.getServiceRecord();
		if (sr != null && sr.getServiceType() != null && sr.getServiceType().getValueCoded() != null) {
			final int serviceTypeConceptId = sr.getServiceType().getValueCoded().getConceptId();
			if (ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION.getConceptId().equals(serviceTypeConceptId)) {
				// Vitamin A
				return Functions.answers(VitaminAServiceConcepts.REMARKS);
			} else if (ChildCareServiceTypes.DEWORMING.getConceptId().equals(serviceTypeConceptId)) {
				// Deworming: NOTE: This is not relevant since remarks for 'deworming' are in free-text form
				return Functions.answers(DewormingServiceConcepts.REMARKS);
			} else if (ChildCareServiceTypes.FERROUS_SULFATE.getConceptId().equals(serviceTypeConceptId)) {
				// Ferrous Sulfate: NOTE: This is not relevant since remarks for 'iron supplementation' are in free-text form
				return Functions.answers(FerrousSulfateServiceConcepts.REMARKS);
			}
		}

		// remarks not available
		return null;
	}

	/**
	 * Populates the 'medicationOpts' containing the available options for the 'Dosage / Medication' drop-down list.
	 * 
	 * @param form
	 * @return
	 */
	@ModelAttribute("medicationOpts")
	public List<Concept> getMedicationOptions(@ModelAttribute("form") ChildCareConsultEntryForm form) {
		// store options for 'remarks' and 'dosage' based on the selected service type
		final ServiceRecord sr = form.getServiceRecord();
		if (sr != null && sr.getServiceType() != null && sr.getServiceType().getValueCoded() != null) {
			final int serviceTypeConceptId = sr.getServiceType().getValueCoded().getConceptId();
			if (ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION.getConceptId().equals(serviceTypeConceptId)) {
				final List<Concept> dosages = new ArrayList<Concept>();
				final int ageInMonths = DateUtil.monthsBetween(form.getPatient().getBirthdate(), new Date());
				if (ageInMonths >= 6 && ageInMonths < 12) {
					final Concept dosage100KIU = Functions.conceptByIdOrName("100, 000 IU");
					if (dosage100KIU != null) {
						dosages.add(dosage100KIU);
					}
				} else if (ageInMonths >= 12 && ageInMonths <= 71) {
					final Concept dosage200KIU = Functions.conceptByIdOrName("200, 000 IU");
					if (dosage200KIU != null) {
						dosages.add(dosage200KIU);
					}
				}

				if (dosages.isEmpty()) {
					dosages.addAll(Functions.answers(VitaminAServiceConcepts.DOSAGE));
				}

				// Vitamin A
				return dosages;
			} else if (ChildCareServiceTypes.DEWORMING.getConceptId().equals(serviceTypeConceptId)) {
				final List<Concept> dosages = new ArrayList<Concept>();
				final int ageInMonths = DateUtil.monthsBetween(form.getPatient().getBirthdate(), new Date());
				if (ageInMonths >= 12 && ageInMonths <= 24) {
					final Concept medication1 = Functions.conceptByIdOrName("Albendazole 200mg chewable, half-tablet");
					final Concept medication2 = Functions.conceptByIdOrName("Mebendazole 500mg chewable tablet");
					for (Concept medication : new Concept[] { medication1, medication2 }) {
						if (medication != null) {
							dosages.add(medication);
						}
					}
				} else if (ageInMonths >= 25 && ageInMonths <= 71) {
					final Concept medication1 = Functions.conceptByIdOrName("Albendazole 400mg chewable, tablet");
					final Concept medication2 = Functions.conceptByIdOrName("Mebendazole 500mg chewable tablet");
					for (Concept medication : new Concept[] { medication1, medication2 }) {
						if (medication != null) {
							dosages.add(medication);
						}
					}
				}

				if (dosages.isEmpty()) {
					dosages.addAll(Functions.answers(DewormingServiceConcepts.MEDICATION));
				}

				// Deworming
				return dosages;
			} else if (ChildCareServiceTypes.FERROUS_SULFATE.getConceptId().equals(serviceTypeConceptId)) {
				final List<Concept> dosages = new ArrayList<Concept>();
				final int ageInMonths = DateUtil.monthsBetween(form.getPatient().getBirthdate(), new Date());

				// determine if patient has low birthweight
				final boolean lowBirthWeight = ChildCareUtil.isLowBirthweightThreshold(form.getPatient());

				if (lowBirthWeight && ageInMonths >= 2 && ageInMonths < 6) {
					final Concept ironDrops = Functions.conceptByIdOrName("Iron drops 15mg/0.6mL, 30mL-bottle");
					if (ironDrops != null) {
						dosages.add(ironDrops);
					}
				} else if (ageInMonths >= 6 && ageInMonths < 24) {
					final Concept ironDrops = Functions.conceptByIdOrName("Iron drops 15mg/0.6mL, 30mL-bottle");
					if (ironDrops != null) {
						dosages.add(ironDrops);
					}
				} else if (ageInMonths >= 24 && ageInMonths < 72) {
					final Concept ironDrops = Functions.conceptByIdOrName("Iron syrup 30mg/15mL, 120mL-bottle");
					if (ironDrops != null) {
						dosages.add(ironDrops);
					}
				}

				if (dosages.isEmpty()) {
					dosages.addAll(Functions.answers(FerrousSulfateServiceConcepts.MEDICATION));
				}

				// Ferrous Sulfate
				return dosages;
			}
		}

		// medication not available
		return null;
	}

	@Override
	protected void postProcess(HttpServletRequest request, ChildCareConsultEntryForm form, ModelMap map, Encounter enc, BindingResult errors) {
		// validate this service record:
		final ServiceRecord sr = form.getServiceRecord();
		Date dateGiven = null;
		try {
			// is date valid?
			dateGiven = Context.getDateFormat().parse(sr.getDateGiven().getValueText());
			sr.getDateGiven().setValueDatetime(dateGiven);

			// date administered date must be on or after the patient's birth date and not in future
			PatientConsultEntryFormValidator.validateDateValue(form, sr.getDateGiven(), "serviceRecord.dateGiven.valueText", errors,
					DateValidationType.MUST_NOT_BE_IN_FUTURE, DateValidationType.ON_OR_AFTER_BIRTHDATE);
		} catch (Exception ex) {
			errors.rejectValue("serviceRecord.dateGiven.valueText", "chits.error.consult.invalid.date");
		}

		// is the dosage indicated?
		if (sr.getQuantityOrDosage().getValueCoded() == null) {
			if (sr.isVitaminAServiceType()) {
				// for vitamin a supplementation, it's called dosage
				errors.rejectValue("serviceRecord.quantityOrDosage.valueCoded", "chits.error.dosage.required");
			} else {
				// other service types call it 'medication'
				errors.rejectValue("serviceRecord.quantityOrDosage.valueCoded", "chits.error.medication.required");
			}
		} else {
			// store the value coded name into the text
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(sr.getQuantityOrDosage());
		}

		final int serviceTypeConceptId = sr.getServiceType().getValueCoded().getConceptId();
		if (ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION.getConceptId().equals(serviceTypeConceptId)) {
			// were remarks selected?
			if (sr.getRemarks().getValueCoded() == null) {
				errors.rejectValue("serviceRecord.remarks.valueCoded", "chits.error.remarks.required");
			} else {
				// store the value coded name into the text
				PatientConsultEntryFormValidator.setValueCodedIntoValueText(sr.getRemarks());
			}
		} else {
			// were remarks specified? (NOTE: not required for 'deworming' or 'ferrous sulfate')
			if (!sr.isDewormingServiceType() && !sr.isFerrousSulfateServiceType()) {
				if (StringUtils.isEmpty(sr.getRemarks().getValueText())) {
					errors.rejectValue("serviceRecord.remarks.valueText", "chits.error.remarks.required");
				}
			}
		}

		// store service type
		if (sr.getServiceType().getValueCoded() == null) {
			errors.rejectValue("serviceRecord.serviceType.valueCoded", "chits.error.service.type.required");
		} else {
			// store the value coded name into the text
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(sr.getServiceType());
		}

		// store service type
		if (sr.getServiceSource().getValueCoded() == null) {
			errors.rejectValue("serviceRecord.serviceSource.valueCoded", "chits.error.service.source.required");
		} else {
			// store the value coded name into the text
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(sr.getServiceSource());
		}

		if (!errors.hasErrors()) {
			// perform additional validation
			if (ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION.getConceptId().equals(sr.getServiceType().getValueCoded().getConceptId())) {
				if (ChildCareUtil.isRoutineRemarks(sr.getRemarks())) {
					// last routine issue should be at least 6 months previously and last therapeutic issue should be at least 1 month previously
					final Obs lastRoutineIssue = ChildCareUtil.findLastRoutineService(form.getPatient(), ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION);
					final Obs lastTherapeuticIssue = ChildCareUtil.findLastTherapeuticService(form.getPatient(),
							ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION);

					boolean showRoutineWarning = false;
					if (lastRoutineIssue != null) {
						final int lastRoutineMonthsElapsed = DateUtil.monthsBetween(ServiceUtil.serviceDateAdministered(lastRoutineIssue), dateGiven);
						showRoutineWarning |= lastRoutineMonthsElapsed < 6;
					}

					if (lastTherapeuticIssue != null) {
						final int lastTherapeuticMonthsElapsed = DateUtil.monthsBetween(ServiceUtil.serviceDateAdministered(lastTherapeuticIssue), dateGiven);
						showRoutineWarning |= lastTherapeuticMonthsElapsed < 1;
					}

					if (showRoutineWarning) {
						errors.rejectValue("serviceRecord.remarks.valueCoded", "chits.childcare.error.vitamin_a.routine.restriction");
					}
				} else if (ChildCareUtil.isTherapeuticRemarks(sr.getRemarks())) {
					final Obs lastVitaminAIssue = ChildCareUtil.findLastService(form.getPatient(), ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION);
					if (lastVitaminAIssue != null) {
						final int lastServiceMonthsElapsed = DateUtil.monthsBetween(ServiceUtil.serviceDateAdministered(lastVitaminAIssue), dateGiven);
						if (lastServiceMonthsElapsed < 1) {
							errors.rejectValue("serviceRecord.remarks.valueCoded", "chits.childcare.error.vitamin_a.therapeutic.restriction");
						}
					}
				}
			}
		}
	}

	/**
	 * The version object is the encounter instance
	 */
	@Override
	protected Auditable getVersionObject(ChildCareConsultEntryForm form) {
		// use the encounter instance as the version object since it's tricky using the service type record because there could be several with timestamps
		// manually entered
		return form.getEncounter();
	}

	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, ChildCareConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		// prep the observation group to contain all the information for one service
		final ServiceRecord sr = form.getServiceRecord();
		final Obs childCareServiceGroup = sr.getServiceType();
		PatientConsultEntryFormValidator.setValueCodedIntoValueText(childCareServiceGroup);

		// add the vaccination group to the 'obsToSave'
		obsToSave.add(childCareServiceGroup);

		// add the service data to the parent service group record
		childCareServiceGroup.addGroupMember(sr.getDateGiven());
		childCareServiceGroup.addGroupMember(sr.getQuantityOrDosage());
		childCareServiceGroup.addGroupMember(sr.getRemarks());
		childCareServiceGroup.addGroupMember(sr.getServiceSource());

		// add all data to the 'obsToSave'
		obsToSave.addAll(childCareServiceGroup.getGroupMembers());

		// add the service group parent record to the encounter for cascade saving
		enc.addObs(childCareServiceGroup);
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// return full page
		return "/module/chits/consults/childcare/ajaxAddChildCareServiceRecord";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/addChildCareServiceRecord.form?patientId=" + patientId + "&serviceRecord.serviceType.valueCoded="
				+ request.getParameter("serviceRecord.serviceType.valueCoded");
	}
}
