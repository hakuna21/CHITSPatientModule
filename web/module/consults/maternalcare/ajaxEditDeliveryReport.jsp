<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.timeentry/jquery.timeentry.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.timeentry/jquery.timeentry.js" />

<c:if test="${not form.mcProgramObs.readOnly}">
<script>
$j(document).ready(function() {
	$j('#time-initiated-breastfeeding').timeEntry({spinnerImage:'${pageContext.request.contextPath}/moduleResources/chits/scripts/jquery.timeentry/spinnerText.png',spinnerSize: [30, 20, 8]});
	if ($j('#time-initiated-breastfeeding').val() == '') {
		$j('#time-initiated-breastfeeding').val('00:00AM')
	}
})
</script>
</c:if>

<div>
<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
</div>

<c:if test="${err != null}">
	<div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div>
</c:if>

<script>
function cancelDeliveryReport() {
	$j("#generalForm").dialog('close')
}

function removeOutcome(i) {
	alert("remove outcome at index: " + i)
}

var nextIndex = ${fn:length(form.mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes)};
function addPregnancyOutcome() {
	var html = $j("#outcomeTemplate").html()
	html = html.replace(/outcome-\d+\"/g, 'outcome-' + nextIndex + '"')
	html = html.replace(/outcomes\d+\./g, 'outcomes' + nextIndex + '.')
	html = html.replace(/outcomes\[\d+\]/g, 'outcomes[' + nextIndex + ']')
	html = html.replace(/Outcome\(\d+\)/g, 'Outcome(' + nextIndex + ')')
	nextIndex++
	
	$j("#pregnancy-outcome-body").append($j(html))
}
</script>

<chits_tag:auditInfo obsGroup="${form.mcProgramObs.deliveryReport.obs}" />

<br />
<form:form modelAttribute="form" id="delivery-plan-chart-form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updateMCSection); return false;">
<form:hidden path="version" />
<fieldset><legend>DELIVERY RECORD</legend>
<table id="delivery-report" class="full-width borderless registration">
	<tr>
		<td class="label" style="width: 15%;">Delivery date</td>
		<td><chits_tag:springInput path="mcProgramObs.deliveryReport.observationMap[${MCDeliveryReportConcepts.DELIVERY_DATE.conceptId}].valueText" onclick="showCalendar(this)" /></td>
	</tr><tr>
		<td class="label">Location of delivery</td>
		<td><chits_tag:springDropdown path="mcProgramObs.deliveryReport.obstetricHistoryDetail.observationMap[${MCObstetricHistoryDetailsConcepts.PLACE_OF_DELIVERY.conceptId}].valueCoded" answers="${chits:answers(MCObstetricHistoryDetailsConcepts.PLACE_OF_DELIVERY)}" /></td>
	</tr><tr>
		<td class="label">Birth Attendant</td>
		<td><chits_tag:springDropdown path="mcProgramObs.deliveryReport.observationMap[${MCDeliveryReportConcepts.BIRTH_ATTENDANT.conceptId}].valueCoded" answers="${chits:answers(MCObstetricHistoryDetailsConcepts.DELIVERY_ASSISTANT_ANSWERS)}" /></td>
	</tr>
</table>

<fieldset><legend>Pregnancy outcome</legend>
<table id="pregnancy-outcomes" class="full-width registration form">
<thead><tr><th>Sex</th><th>Weight<br/>(kg)</th><th>Term</th><th>Method</th><th>Outcome</th><th>Breastfed<br/>w/in 1 Hr?</th></tr></thead>
<tbody id="pregnancy-outcome-body"><c:forEach var="outcome" items="${form.mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes}" varStatus="i">
<tr id="outcome-${i.index}"><td>
	<chits_tag:springDropdown path="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.SEX.conceptId}].valueCoded" answers="${chits:answers(MCPregnancyOutcomeConcepts.SEX_ANSWERS)}" />
</td><td>
	<chits_tag:springInput path="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.BIRTH_WEIGHT_OF_BABY_KG.conceptId}].valueText" size="4" />
</td><td>
	<chits_tag:springDropdown path="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.TERM.conceptId}].valueCoded" answers="${chits:answers(MCPregnancyOutcomeConcepts.TERM_ANSWERS)}" />
</td><td>
	<chits_tag:springDropdown path="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.METHOD.conceptId}].valueCoded" answers="${chits:answers(MCPregnancyOutcomeConcepts.METHOD_ANSWERS)}" />
</td><td>
	<chits_tag:springDropdown path="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.OUTCOME.conceptId}].valueCoded" answers="${chits:answers(MCPregnancyOutcomeConcepts.OUTCOME_ANSWERS)}" />
</td><td>
	<spring:bind path="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.BREASTFED_WITHIN_HOUR.conceptId}].valueCoded">
	<form:select path="${status.expression}">
		<form:option value="">Select</form:option>
		<form:option value="${chits:trueConcept()}">Yes</form:option>
		<form:option value="${chits:falseConcept()}">No</form:option>
	</form:select>
	<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
	</spring:bind>
</td></tr>
</c:forEach></tbody>
</table>
<c:if test="${not form.mcProgramObs.readOnly}">
<br/>
<input type="button" onclick="addPregnancyOutcome()" value="Add another pregnancy outcome" />
</c:if>

<br/><br/>

<table class="borderless">
<tr>
	<td>Date and Time<br/>initiated Breastfeeding</td>
	<td>
		<chits_tag:springInput path="mcProgramObs.deliveryReport.observationMap[${MCDeliveryReportConcepts.DATE_INITIATED_BREASTFEEDING.conceptId}].valueText" onclick="showCalendar(this)" size="8" />
		<chits_tag:springInput path="mcProgramObs.deliveryReport.observationMap[${MCDeliveryReportConcepts.TIME_INITIATED_BREASTFEEDING.conceptId}].valueText" id="time-initiated-breastfeeding" size="8"/>
	</td>
</tr>
</table>

</fieldset>

<fieldset><legend>FINAL OB SCORE</legend>
<table id="final-ob-score"  class="form registration field">
<thead>
<tr>
	<th>&nbsp;</th><th>G</th><th>P</th><th>F</th><th>P</th><th>A</th><th>L</th>
</tr>
</thead>
<tbody>
<tr>
	<td>Before Delivery</td>
	<td><fmt:formatNumber pattern="0" value="${chits:observation(form.mcProgramObs.obstetricHistory.obs, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_GRAVIDA).valueNumeric}" /></td>
	<td><fmt:formatNumber pattern="0" value="${chits:observation(form.mcProgramObs.obstetricHistory.obs, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PARA).valueNumeric}" /></td>
	<td><fmt:formatNumber pattern="0" value="${chits:observation(form.mcProgramObs.obstetricHistory.obs, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_FT).valueNumeric}" /></td>
	<td><fmt:formatNumber pattern="0" value="${chits:observation(form.mcProgramObs.obstetricHistory.obs, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PT).valueNumeric}" /></td>
	<td><fmt:formatNumber pattern="0" value="${chits:observation(form.mcProgramObs.obstetricHistory.obs, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_AM).valueNumeric}" /></td>
	<td><fmt:formatNumber pattern="0" value="${chits:observation(form.mcProgramObs.obstetricHistory.obs, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_LC).valueNumeric}" /></td>
</tr><tr>
	<td>After Delivery</td>
	<td><chits_tag:springInput path="mcProgramObs.deliveryReport.obstetricHistoryDetail.observationMap[${MCObstetricHistoryDetailsConcepts.GRAVIDA.conceptId}].valueText" size="2" /></td>
	<td><chits_tag:springInput path="mcProgramObs.deliveryReport.observationMap[${MCDeliveryReportConcepts.OBSTETRIC_SCORE_PARA.conceptId}].valueText" size="2" /></td>
	<td><chits_tag:springInput path="mcProgramObs.deliveryReport.observationMap[${MCDeliveryReportConcepts.OBSTETRIC_SCORE_FT.conceptId}].valueText" size="2" /></td>
	<td><chits_tag:springInput path="mcProgramObs.deliveryReport.observationMap[${MCDeliveryReportConcepts.OBSTETRIC_SCORE_PT.conceptId}].valueText" size="2" /></td>
	<td><chits_tag:springInput path="mcProgramObs.deliveryReport.observationMap[${MCDeliveryReportConcepts.OBSTETRIC_SCORE_AM.conceptId}].valueText" size="2" /></td>
	<td><chits_tag:springInput path="mcProgramObs.deliveryReport.observationMap[${MCDeliveryReportConcepts.OBSTETRIC_SCORE_LC.conceptId}].valueText" size="2" /></td>
</tr>
</tbody>
</table>
</fieldset>

</fieldset>

<c:if test="${not form.mcProgramObs.readOnly}">
<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelDeliveryReport()" />
</div>
</c:if>
</form:form>

<c:choose><c:when test="${form.mcProgramObs.readOnly}">
<script>
$j("#delivery-plan-chart-form *").attr('readonly', 'readonly')
$j("#delivery-plan-chart-form *").attr('disabled', 'disabled')
</script>
</c:when><c:otherwise>
<table style="display:none">
<tbody id="outcomeTemplate">
<tr id="outcome-0"><td>
	<select name="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.SEX.conceptId}].valueCoded">
		<option value="">Select</option><c:forEach var="answer" items="${chits:answers(MCPregnancyOutcomeConcepts.SEX_ANSWERS)}">
		<option value="${answer.conceptId}">${answer.name}</option></c:forEach>
	</select>
</td><td>
	<input type="text" name="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.BIRTH_WEIGHT_OF_BABY_KG.conceptId}].valueText" size="4" />
</td><td>
	<select name="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.TERM.conceptId}].valueCoded">
		<option value="">Select</option><c:forEach var="answer" items="${chits:answers(MCPregnancyOutcomeConcepts.TERM_ANSWERS)}">
		<option value="${answer.conceptId}">${answer.name}</option></c:forEach>
	</select>
</td><td>
	<select name="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.METHOD.conceptId}].valueCoded">
		<option value="">Select</option><c:forEach var="answer" items="${chits:answers(MCPregnancyOutcomeConcepts.METHOD_ANSWERS)}">
		<option value="${answer.conceptId}">${answer.name}</option></c:forEach>
	</select>
</td><td>
	<select name="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.OUTCOME.conceptId}].valueCoded">
		<option value="">Select</option><c:forEach var="answer" items="${chits:answers(MCPregnancyOutcomeConcepts.OUTCOME_ANSWERS)}">
		<option value="${answer.conceptId}">${answer.name}</option></c:forEach>
	</select>
</td><td>
	<select name="mcProgramObs.deliveryReport.obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.BREASTFED_WITHIN_HOUR.conceptId}].valueCoded">
		<option value="">Select</option>
		<option value="${chits:trueConcept()}">Yes</option>
		<option value="${chits:falseConcept()}">No</option>
	</select>
</td></tr>
</tbody></table>
</c:otherwise></c:choose>