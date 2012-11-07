package org.openmrs.module.chits.web.controller.genconsults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Auditable;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.PatientConsultEntryForm.DrugOrderEntry;
import org.openmrs.module.chits.validator.PatientConsultEntryFormValidator;
import org.openmrs.module.chits.web.controller.BaseUpdatePatientConsultDataController;
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
 * Edits the patient's visit notes and (optionally) complaints / diagnoses.
 */
@Controller
@RequestMapping(value = "/module/chits/consults/updateVisitNotes.form")
public class UpdateVisitNotesController extends BaseUpdatePatientConsultDataController<PatientConsultEntryForm> implements Constants {
	/** Auto-wired order service */
	protected OrderService orderService;

	@Override
	@ModelAttribute("form")
	public PatientConsultEntryForm formBackingObject(HttpServletRequest request, //
			ModelMap model, //
			@RequestParam(required = true, value = "patientId") Integer patientId) throws ServletException {
		final PatientConsultEntryForm form = (PatientConsultEntryForm) super.formBackingObject(request, model, patientId);

		// get the notes type
		final VisitNotesConceptSets notesType = VisitNotesConceptSets.valueOf(request.getParameter("type"));

		// add the 'type' of notes
		model.addAttribute("type", notesType.toString());

		// get the encounter instance
		final Encounter enc = form.getPatientQueue() != null ? form.getPatientQueue().getEncounter() : null;

		// load the encounter observations that we need to edit into the form
		final Collection<CachedConceptId> editableConcepts = new ArrayList<CachedConceptId>();
		editableConcepts.add(notesType);
		if (notesType == VisitNotesConceptSets.DIAGNOSIS_NOTES) {
			// treatment plan notes always goes together with diagnosis notes
			editableConcepts.add(VisitNotesConceptSets.TREATMENT_NOTES);

			// add encounter orders
			final List<DrugOrderEntry> orders = new ArrayList<DrugOrderEntry>();
			form.setDrugOrders(orders);

			if (enc != null) {
				for (Order order : enc.getOrders()) {
					final DrugOrder drugOrder = orderService.getOrder(order.getOrderId(), DrugOrder.class);
					if (drugOrder != null) {
						final DrugOrderEntry doe = new DrugOrderEntry();
						doe.setDrugId(drugOrder.getDrug().getDrugId().toString());
						doe.setQuantity(drugOrder.getQuantity() != null ? drugOrder.getQuantity().toString() : "");
						doe.setInstructions(drugOrder.getInstructions() != null ? drugOrder.getInstructions() : "");
						doe.setName(drugOrder.getDrug().getConcept().getName().getName());
						orders.add(doe);
					}
				}
			}
		}

		// setup the form's observations for editing
		setupFormObservations(form, enc, editableConcepts);

		return form;
	}

	@Override
	protected void preProcessEncounterObservations(HttpServletRequest request, PatientConsultEntryForm form, Encounter enc, Collection<Obs> obsToSave,
			Collection<Obs> obsToPurge) {
		if (VisitNotesConceptSets.COMPLAINT_NOTES.toString().equals(request.getParameter("type"))) {
			processConceptAnswers(request, enc, obsToPurge, VisitConcepts.COMPLAINT, "complaints[]");
		}

		if (VisitNotesConceptSets.DIAGNOSIS_NOTES.toString().equals(request.getParameter("type"))) {
			processConceptAnswers(request, enc, obsToPurge, VisitConcepts.DIAGNOSIS, "diagnoses[]");
		}
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public String handleSubmission(HttpSession httpSession, //
			HttpServletRequest request, //
			ModelMap model, //
			@ModelAttribute("form") PatientConsultEntryForm form, //
			BindingResult errors) {
		// pre-process the drug order parameters
		final String[] drugIds = request.getParameterValues("drugOrders.drugId[]");
		final String[] quantities = request.getParameterValues("drugOrders.quantity[]");
		final String[] instructions = request.getParameterValues("drugOrders.instructions[]");

		// store in model attribute
		final List<DrugOrderEntry> orders = new ArrayList<DrugOrderEntry>();
		form.setDrugOrders(orders);

		if (drugIds != null) {
			for (int i = 0; i < drugIds.length; i++) {
				final String drugId = drugIds[i];
				final String quantity = quantities != null && quantities.length > i ? quantities[i] : null;
				final String instruction = instructions != null && instructions.length > i ? instructions[i] : null;

				final DrugOrderEntry doe = new DrugOrderEntry();
				final Drug drug = conceptService.getDrug(drugId);
				doe.setDrugId(drugId);
				doe.setQuantity(quantity);
				doe.setInstructions(instruction);
				doe.setName(drug != null ? drug.getConcept().getName().getName() : "???");
				orders.add(doe);
			}
		}

		// dispatch to superclass
		return super.handleSubmission(httpSession, request, model, form, errors);
	}

	/**
	 * The version object is the encounter itself.
	 */
	@Override
	protected Auditable getVersionObject(PatientConsultEntryForm form) {
		return form.getEncounter();
	}

	@Override
	protected void postProcess(HttpServletRequest request, PatientConsultEntryForm form, ModelMap model, Encounter enc, BindingResult errors) {
		if (!VisitNotesConceptSets.DIAGNOSIS_NOTES.toString().equals(request.getParameter("type"))) {
			// no need to process drug orders
			return;
		}

		// These will replace the current encounter drug orders
		final List<DrugOrder> drugOrders = new ArrayList<DrugOrder>();

		// get the drug orders from the form
		final List<DrugOrderEntry> orders = form.getDrugOrders();

		if (orders != null && !orders.isEmpty()) {
			// find the 'Drug Order' order type
			OrderType orderType = null;
			for (OrderType ot : orderService.getAllOrderTypes()) {
				if (ot.getName().equalsIgnoreCase(DrugOrderConcepts.ORDER_TYPE_DRUG_ORDER.getConceptName())) {
					orderType = ot;
					break;
				}
			}

			if (orderType == null) {
				// default behavior: get order type with PK = 1
				orderType = orderService.getOrderType(1);
			}

			for (int i = 0; i < orders.size(); i++) {
				final DrugOrderEntry doe = orders.get(i);
				try {
					final int drugId = Integer.parseInt(doe.getDrugId());
					final int quantity = Integer.parseInt(doe.getQuantity());
					final String instruction = doe.getInstructions();

					if (quantity <= 0) {
						errors.rejectValue("drugOrders[" + i + "]", "chits.drugs.quantity.required");
					}

					if (StringUtils.isEmpty(instruction)) {
						errors.rejectValue("drugOrders[" + i + "]", "chits.drugs.instructions.required");
					}

					// get the concept of the drug
					final Drug drug = conceptService.getDrug(drugId);

					// add drug order
					final DrugOrder order = new DrugOrder();
					order.setDrug(drug);
					order.setPrn(Boolean.FALSE);
					order.setComplex(Boolean.FALSE);
					order.setQuantity(quantity);
					order.setInstructions(instruction);
					order.setOrderType(orderType);
					order.setConcept(drug.getConcept());
					order.setDiscontinued(Boolean.FALSE);
					order.setCreator(Context.getAuthenticatedUser());
					order.setDateCreated(new Date());
					order.setVoided(Boolean.FALSE);
					order.setPatient(enc.getPatient());
					order.setEncounter(enc);
					order.setUuid(UUID.randomUUID().toString());
					drugOrders.add(order);
				} catch (APIAuthenticationException ex) {
					// propagate authorization errors
					throw ex;
				} catch (Exception ex) {
					errors.rejectValue("drugOrders[" + i + "]", "chits.drugs.quantity.required");
				}
			}
		}

		if (!errors.hasErrors()) {
			// remove old orders
			final List<Order> toPurge = new ArrayList<Order>(enc.getOrders());

			// detach all orders
			enc.getOrders().clear();

			for (Order order : toPurge) {
				order.setEncounter(null);
				orderService.purgeOrder(order);
			}

			// add all orders to the encounter
			for (DrugOrder drugOrder : drugOrders) {
				orderService.saveOrder(drugOrder);
				enc.addOrder(drugOrder);
			}
		}
	}

	private void processConceptAnswers(HttpServletRequest request, Encounter enc, Collection<Obs> obsToPurge, CachedConceptId conceptQuestion,
			String answerIdsName) {
		// get the concept answers...
		final String[] answerConceptIds = request.getParameterValues(answerIdsName);
		final List<Concept> answerConcepts = new ArrayList<Concept>();
		if (answerConceptIds != null) {
			for (String answerConceptId : answerConceptIds) {
				answerConcepts.add(conceptService.getConcept(answerConceptId));
			}
		}

		// determine what concept answers to remove and what to add
		final List<Obs> currentObs = Functions.observations(enc, conceptQuestion);
		for (Obs obs : currentObs) {
			if (!answerConcepts.contains(obs.getValueCoded())) {
				// this concept answer was removed; add it to the list of observations to purge
				obsToPurge.add(obs);
			} else {
				// this concept answer is already in the encounter
				answerConcepts.remove(obs.getValueCoded());
			}
		}

		// whatever answers weren't found in the current set, add to the encounter
		for (Concept answer : answerConcepts) {
			final Obs answerObs = newObs(conceptQuestion, enc.getPatient());
			answerObs.setValueCoded(answer);
			PatientConsultEntryFormValidator.setValueCodedIntoValueText(answerObs);
			enc.addObs(answerObs);
		}
	}

	@Override
	protected String getInputPath(HttpServletRequest request) {
		// send to an ajax input page
		return "/module/chits/consults/ajaxUpdateNotesForm";
	}

	@Override
	protected String getReloadPath(HttpServletRequest request, Integer patientId) {
		// redirect to the controller for reloading
		return "redirect:/module/chits/consults/updateVisitNotes.form?patientId=" + patientId + "&type=" + request.getParameter("type");
	}

	@Autowired
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
}
