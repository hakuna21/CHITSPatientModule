<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<h3><spring:message code="chits.consult.notes.${type}.title" /></h3>

<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
<c:if test="${err != null}">
	<div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div>
</c:if>

<%-- NOTE: Display the form if and only if the patient queue encounter has already been initialized! --%>
<c:choose><c:when test="${not empty form.patientQueue.encounter}">
	<c:if test="${type eq 'COMPLAINT_NOTES'}">
	<div class="boxed">
		Search complaints: <input type="text" id="complaintsSearch" style="width: 60%" /> (type to search)
	</div>
	</c:if>

	<c:if test="${type eq 'DIAGNOSIS_NOTES'}">
	<div class="boxed">
		Search Diagnosis: <input type="text" id="diagnosesSearch" style="width: 60%" /> (type to search)
	</div>
	</c:if>

	<form:form modelAttribute="form" method="post" action="#" onsubmit="submitNotesForm(this, ${form.patient.patientId}); return false;">
		<form:hidden path="version" />
		<input type="hidden" name="type" value="${type}" />

		<c:if test="${type eq 'COMPLAINT_NOTES'}">
		<c:set var="complaints" value="${chits:observations(form.patientQueue.encounter, VisitConcepts.COMPLAINT)}" />
		<div class="indent" id="complaints"><span id="no-complaints">no entry for this visit</span></div>
		</c:if>

		<c:if test="${type eq 'DIAGNOSIS_NOTES'}">
		<c:set var="diagnoses" value="${chits:observations(form.patientQueue.encounter, VisitConcepts.DIAGNOSIS)}" />
		<div class="indent" id="diagnoses"><span id="no-diagnoses">no entry for this visit</span></div>
		</c:if>

		<div id="notesTemplate">
			<%-- NOTE: Only History, PE, Treatment plan have templates; also, treatment plan template has special handling --%>
			<c:if test="${type eq 'HISTORY_NOTES' or type eq 'PHYSICAL_EXAM_NOTES'}">
				<div class="boxed"><spring:message code="chits.consult.notes.${type}.template" />: <input type="text" /> (type to search)</div>
			</c:if>
			<spring:message code="chits.consult.notes.${type}.notes.header" /><br/>
			<spring:bind path="observationMap[${VisitNotesConceptSets[type].conceptId}].valueText">
			<textarea class="notes editable-notes" name="${status.expression}">${status.value}</textarea>
			<c:if test="${status.errorMessage != ''}"><br /><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
			<br/>
		</div>

		<%-- Treatment plan notes go together with diagnosis --%>
		<c:if test="${type eq 'DIAGNOSIS_NOTES'}">
		<div id="treatmentNotesTemplate">
			<div class="boxed"><spring:message code="chits.consult.notes.TREATMENT_PLAN_NOTES.template" />: <input type="text" /> (type to search)</div>
			<spring:message code="chits.consult.notes.TREATMENT_PLAN_NOTES.notes.header" /><br/>
			<spring:bind path="observationMap[${VisitNotesConceptSets.TREATMENT_NOTES.conceptId}].valueText">
				<textarea class="notes editable-notes" name="${status.expression}">${status.value}</textarea>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind><br/>
		</div>
		</c:if>
		
		<c:if test="${type eq 'DIAGNOSIS_NOTES'}">
		Rx<br/>
		<div class="boxed">
		<input type="text" id="drugsSearch" style="width: 60%" /> (type &gt;= 3 letters to search)
		</div>

		<div id="drugs">
			<table class="form" style="width: 95%;">
				<thead><tr><th>PRESCRIPTION</th><th>#</th><th>INSTRUCTIONS</th></tr></thead>
				<tbody>
				<c:forEach var="drugOrder" items="${form.drugOrders}" varStatus="i"><spring:bind path="drugOrders[${i.index}]">
					<tr class='drug-${drugOrder.drugId} drugorder'>
					<td><a href="javascript: $j('tr.drug-${drugOrder.drugId}').remove(); drugsUpdated()">[x]</a> ${drugOrder.name}
					<input type='hidden' name='drugOrders.drugId[]' value='${drugOrder.drugId}' /></td>
					<td><input type='text' size='2' name='drugOrders.quantity[]' value='${drugOrder.quantity}' /></td>
					<td><input type='text' size='24' name='drugOrders.instructions[]' value='${drugOrder.instructions}' /></td>
					</tr>
					<c:if test="${status.errorMessage != ''}">
					<tr><td colspan="3"><div class="error">${status.errorMessage}</div></td></tr>
					</c:if>
				</spring:bind></c:forEach>
				</tbody>
				<tfoot><tr id="no-drugs"><td colspan="3"><span>no entry for this visit</span></td></tr></tfoot>
			</table>
		</div>
		</c:if>

		<input type="submit" value="Save" />
	</form:form>
</c:when><c:otherwise>
<%@ include file="ajaxFragmentStartConsult.jsp" %>
</c:otherwise></c:choose>

<script>
<c:if test="${type eq 'COMPLAINT_NOTES'}">
function complaintsUpdated() {
	if ($j("input[name='complaints[]']").size() > 0) {
		$j("#no-complaints").hide();
	} else {
		$j("#no-complaints").show();
	}
}

function addComplaint(conceptId, label) {
	$j("div#complaints").append(
		"<span class='concept-" + conceptId + "'>" +
		"<a href=\"javascript: $j('span.concept-" + conceptId + "').remove(); complaintsUpdated()\">[x]</a> " + label +
		"<input type='hidden' name='complaints[]' value='" + conceptId + "' /></span> "
	)
	complaintsUpdated()
}
</c:if>

<c:if test="${type eq 'DIAGNOSIS_NOTES'}">
function diagnosesUpdated() {
	if ($j("input[name='diagnoses[]']").size() > 0) {
		$j("#no-diagnoses").hide()
	} else {
		$j("#no-diagnoses").show()
	}
}

function addDiagnosis(conceptId, label) {
	$j("div#diagnoses").append(
		"<span class='concept-" + conceptId + "'>" +
		"<a href=\"javascript: $j('span.concept-" + conceptId + "').remove(); diagnosesUpdated()\">[x]</a> " + label +
		"<input type='hidden' name='diagnoses[]' value='" + conceptId + "' /></span> "
	)
	diagnosesUpdated()
}

function drugsUpdated() {
	if ($j("tr.drugorder").size() > 0) {
		$j("#no-drugs").hide()
	} else {
		$j("#no-drugs").show()
	}
}

function addDrug(drugId, label) {
	$j("div#drugs table tbody").append(
		"<tr class='drug-" + drugId + " drugorder'>" +
		"<td><a href=\"javascript: $j('tr.drug-" + drugId + "').remove(); drugsUpdated()\">[x]</a> " + label +
		"<input type='hidden' name='drugOrders.drugId[]' value='" + drugId + "' /></td>" +
		"<td><input type='text' size='2' name='drugOrders.quantity[]' value='' /></td>" +
		"<td><input type='text' size='24' name='drugOrders.instructions[]' value='' /></td>" +
		"</tr>"
	)
	
	drugsUpdated()
	$j("#updateNotesForm").attr({ scrollTop: $j("#updateNotesForm").attr("scrollHeight") })
}
</c:if>

function autocompleteNotes(selector, conceptId) {
	$j(selector).each(function() {
		var inputEl = $j(this).find("input")
		var textEl = $j(this).find("textarea")
		inputEl.autocomplete({minLength:0,delay: 200,source: function(request, response) {
				DWRCHITSConceptService.findICD10ConceptSetMembers(conceptId, request.term, function(concepts) {
					$j.each(concepts, function() { this.label = this.name; this.value = this.name })
					response(concepts.length > 0 ? concepts : [{label:'No matching results for: ' + request.term, value: ''}])
				})
			}, select: function(event, ui) { if (ui.item.conceptId > 0) {
				var plsWaitDialog = $j("<div><h4>Loading template... Please Wait...</h4>").dialog({title:'Uploading...',height:'auto',width:'auto',modal:'true'})
				$j.ajax({url: 'getNotesTemplate.form?conceptId=' + ui.item.conceptId, cache: false, success: function (data) {
					plsWaitDialog.dialog('close')
					$j("#confirmTemplate textarea").val(data)
					$j("#confirmTemplate").dialog({width:'500px',height:'auto',modal:true,buttons:{
						"Add to notes":function(){textEl.val(textEl.val() + data); $j(this).dialog("close")},
						"Cancel":function(){$j(this).dialog("close")}
					}}).dialog('open')
				}})
				inputEl.val("")
				return false;
			}}
		}).click(function() {
			inputEl.autocomplete('search', inputEl.val());
		})
	})
}

function autocompleteICD10Concepts($input, icd10Type, onSelect) {
	$input.autocomplete({minLength:1,delay: 200,source: function(request, response) {
		DWRCHITSConceptService.findICD10Concepts(icd10Type, request.term, function(concepts) {
				$j.each(concepts, function() { this.label = this.name + (this.icd10 ? " (" + this.icd10 + ")" : ""); this.value = this.label })
				response(concepts.length > 0 ? concepts : [{label:'No matching results for: ' + request.term, value: ''}])
			})
		}, select: function(event, ui) {
			if (ui.item.conceptId > 0) {
				if ($j("span.concept-" + ui.item.conceptId).size() == 0) { onSelect(ui.item.conceptId, ui.item.label) }
			}
			$input.val("")
			return false;
		}
	})
}

function autocompleteDrugs($input, onSelect) {
	$input.autocomplete({minLength:3,delay: 200,source: function(request, response) {
		DWRCHITSConceptService.findDrugs(request.term, function(drugs) {
				$j.each(drugs, function() { this.label = this.name; this.value = this.label })
				response(drugs.length > 0 ? drugs : [{label:'No matching results for: ' + request.term, value: ''}])
			})
		}, select: function(event, ui) {
			if (ui.item.drugId > 0) {
				if ($j("tr.drug-" + ui.item.drugId).size() == 0) { onSelect(ui.item.drugId, ui.item.label) }
			}
			return false;
		}
	})
}

$j(document).ready(function() {
	autocompleteNotes("div#notesTemplate", ${VisitNotesConceptSets[type].conceptId})

	<c:if test="${type eq 'COMPLAINT_NOTES'}">
	autocompleteICD10Concepts($j("input#complaintsSearch"), "Symptoms", addComplaint)
	
	<c:forEach var="complaint" items="${complaints}" varStatus="i"><c:set var="icd10" value="${chits:mapping(complaint.valueCoded, 'ICD10')}" />
	addComplaint(${complaint.valueCoded.conceptId}, "${complaint.valueCoded.name.name}<c:if test='${not empty icd10}'> (${icd10})</c:if>")
	</c:forEach>
	</c:if>

	<c:if test="${type eq 'DIAGNOSIS_NOTES'}">
	autocompleteNotes("div#treatmentNotesTemplate", ${VisitNotesConceptSets.TREATMENT_NOTES.conceptId})
	autocompleteICD10Concepts($j("input#diagnosesSearch"), "Diagnoses", addDiagnosis)
	autocompleteDrugs($j("input#drugsSearch"), addDrug)

	<c:forEach var="diagnosis" items="${diagnoses}" varStatus="i"><c:set var="icd10" value="${chits:mapping(diagnosis.valueCoded, 'ICD10')}" />
	addDiagnosis(${diagnosis.valueCoded.conceptId}, "${diagnosis.valueCoded.name.name}<c:if test='${not empty icd10}'> (${icd10})</c:if>")
	</c:forEach>
	</c:if>

	<c:if test="${type eq 'DIAGNOSIS_NOTES'}">
	drugsUpdated()
	</c:if>
	
	// $j("textarea.notes").attr('wrap', 'off')
	$j("input[type=text]").click(function(){$j(this).select()})
})
</script>
<c:if test="${type eq 'DIAGNOSIS_NOTES'}">
<style>
div#treatmentNotesTemplate textarea.editable-notes, div#notesTemplate textarea.editable-notes { height: 9em; }
</style>
</c:if>