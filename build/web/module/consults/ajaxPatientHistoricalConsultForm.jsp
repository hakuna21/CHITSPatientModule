<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"%>

<link href="${pageContext.request.contextPath}/moduleResources/chits/scripts/consults/visits-section.css?v=${deploymentTimestamp}" type="text/css" rel="stylesheet" />
<script>
$j(document).ready(function() {
	// $j("textarea.notes").attr('wrap', 'off')
	$j('input[type=button], input[type=submit]').button()
})
</script>

<style>
table.visits-history { border-left: 1px solid #aaf; border-top: 1px solid #aaf; }
table.visits-history tr { margin: 0px; padding: 0px; } 
table.visits-history td { border-right: 1px solid #aaf; border-bottom: 1px solid #aaf; padding: 0.5em; margin: 0px; }
</style>

<c:choose><c:when test="${form.encounter eq form.patientQueue.encounter}">
<%-- This is the current visit, so use the notes number from the patient queue --%>
<c:set var="notesNumber" value="${form.patientQueue.notesNumber}" />
</c:when><c:otherwise>
<%-- This is a past visit, so use the notes number from the past visit information --%>
<c:set var="notesNumber" value="${chits:observation(form.encounter, VisitConcepts.NOTES_NUMBER).valueNumeric}" />
</c:otherwise></c:choose>

<h4>
	Notes No. <fmt:formatNumber pattern="00000" value="${notesNumber}" />
	<c:if test="${not empty form.encounter.encounterDatetime}"> -
		<fmt:formatDate pattern="MM/dd/yyyy" value="${form.encounter.encounterDatetime}" />,
		<fmt:formatDate pattern="hh:mm a" value="${form.encounter.encounterDatetime}" />
	</c:if>
</h4>
<table id="visits-history-section" class="visits-history full-width">
<tr>
	<td rowspan="2" valign="top" width="25%">
		<%-- Complaints Notes --%>
		<h4>Complaints:</h4>
		<c:set var="complaints" value="${chits:observations(form.encounter, VisitConcepts.COMPLAINT)}" />
		<div class="indent">
			<c:choose><c:when test="${not empty complaints}">
			<c:forEach var="complaint" items="${complaints}" varStatus="i"><c:set var="icd10" value="${chits:mapping(complaint.valueCoded, 'ICD10')}" />
			<c:if test="${i.index ne 0}">, </c:if>${complaint.valueCoded.name.name}<c:if test="${not empty icd10}"> (${icd10})</c:if>
			</c:forEach>
			</c:when><c:otherwise>
			no entry for this visit
			</c:otherwise></c:choose>
		</div>
		
		<h4>Complaint Notes:</h4>
		<c:set var="notes" value="${chits:observation(form.encounter, VisitNotesConceptSets.COMPLAINT_NOTES)}" />
		<textarea class="notes editable-notes" readonly="readonly"><c:choose><c:when test="${not empty notes and notes.valueText ne ''}">${notes.valueText}</c:when><c:otherwise>no entry for this visit</c:otherwise></c:choose></textarea>
	</td>
	<td valign="top" width="25%">
		<%-- History Notes --%>
		<h4>History Notes:</h4>
		<c:set var="notes" value="${chits:observation(form.encounter, VisitNotesConceptSets.HISTORY_NOTES)}" />
		<textarea class="notes editable-notes" style="height: 10em;" readonly="readonly"><c:choose><c:when test="${not empty notes and notes.valueText ne ''}">${notes.valueText}</c:when><c:otherwise>no entry for this visit</c:otherwise></c:choose></textarea>
	</td>
	<td rowspan="2" valign="top" width="25%">
		<%-- Diagnosis Notes --%>
		<h4>Diagnosis:</h4>
		<c:set var="diagnoses" value="${chits:observations(form.encounter, VisitConcepts.DIAGNOSIS)}" />
		<div class="indent">
			<c:choose><c:when test="${not empty diagnoses}">
			<c:forEach var="diagnosis" items="${diagnoses}" varStatus="i"><c:set var="icd10" value="${chits:mapping(diagnosis.valueCoded, 'ICD10')}" />
			<c:if test="${i.index ne 0}">, </c:if>${diagnosis.valueCoded.name.name}<c:if test="${not empty icd10}"> (${icd10})</c:if>
			</c:forEach>
			</c:when><c:otherwise>
			no entry for this visit
			</c:otherwise></c:choose>
		</div>
	
		<h4>Additional Information:</h4>
		<c:set var="notes" value="${chits:observation(form.encounter, VisitNotesConceptSets.DIAGNOSIS_NOTES)}" />
		<textarea class="notes editable-notes" readonly="readonly"><c:choose><c:when test="${not empty notes and notes.valueText ne ''}">${notes.valueText}</c:when><c:otherwise>no entry for this visit</c:otherwise></c:choose></textarea>

	</td>
	<td rowspan="2" valign="top" width="25%">
		<h4>Treatment Plan:</h4>
		<c:set var="notes" value="${chits:observation(form.encounter, VisitNotesConceptSets.TREATMENT_NOTES)}" />
		<textarea class="notes editable-notes" readonly="readonly"><c:choose><c:when test="${not empty notes and notes.valueText ne ''}">${notes.valueText}</c:when><c:otherwise>no entry for this visit</c:otherwise></c:choose></textarea>

		Rx<br/>
		<table class="form" style="width: 95%;">
			<thead><tr><th>PRESCRIPTION</th><th>#</th><th>INSTRUCTIONS</th></tr></thead>
			<c:choose><c:when test="${not empty drugOrders}"><tbody>
			<c:forEach var="drugOrder" items="${drugOrders}">
				<tr>
				<td>${drugOrder.drug.concept.name.name}</td>
				<td>${drugOrder.quantity}</td>
				<td>${drugOrder.instructions}</td>
				</tr>
			</c:forEach>
			</tbody></c:when><c:otherwise>
				<tfoot id="no-drugs"><tr><td colspan="3"><span>no entry for this visit</span></td></tr></tfoot>
			</c:otherwise></c:choose>
		</table>
	</td>
</tr><tr>
	<td valign="top" width="25%">
		<%-- Physical exam Notes --%>
		<h4>Physical Exam:</h4>
		<c:set var="notes" value="${chits:observation(form.encounter, VisitNotesConceptSets.PHYSICAL_EXAM_NOTES)}" />
		<textarea class="notes editable-notes" style="height: 10em;" readonly="readonly"><c:choose><c:when test="${not empty notes and notes.valueText ne ''}">${notes.valueText}</c:when><c:otherwise>no entry for this visit</c:otherwise></c:choose></textarea>	
	</td>
</tr>
</table>