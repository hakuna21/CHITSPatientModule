<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

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
function cancelObstetricHistoryDetail() {
	$j("#generalForm").dialog('close')
}

function deleteObstetricHistoryDetail(form) {
	$j("<div><h5 class='alert'>Are you sure you want to delete this record?</h5></div>").dialog({
		resizable:true,width:400,height:'auto',modal:true,closeOnEscape:false,
		title:'Delete record?',
		buttons:{
			"Yes, delete":function(){$j(this).dialog("close"); $j("form#deleteOBH").get(0).onsubmit()},
			"Cancel":function(){$j(this).dialog("close")}
		}
	})
}

function removeOutcome(i) {
	alert("remove outcome at index: " + i)
}

var nextIndex = ${fn:length(form.obstetricHistoryDetail.outcomes)};
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

<c:set var="registeredState" value="${chits:findPatientProgramState(form.mcProgramObs.patientProgram, MaternalCareProgramStates.REGISTERED)}" />
<c:if test="${registeredState ne null}">
<br/>MCP Registration No. <fmt:formatNumber pattern="0000000" value="${registeredState.id}" /><br/>
</c:if>

<chits_tag:auditInfo obsGroup="${form.obstetricHistoryDetail.obs}" />

<div style="display: none">
<form:form modelAttribute="form" id="deleteOBH" method="post" action="editObstetricHistoryDetail.form?patientId=${form.patient.patientId}&obstetricHistoryDetailObsId=${form.obstetricHistoryDetail.obs.obsId}&delete=1" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updateObstericHistoryDetails); return false;">
	<form:hidden path="version" />
</form:form>
</div>

<br />
<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updateObstericHistoryDetails); return false;">
<form:hidden path="version" />
<table id="obstetric-history-detail" class="full-width borderless registration">
	<tr>
		<td>
			<table class="field full-width"><tr><td class="label">Year of Pregnancy/Delivery*</td>
			<td><chits_tag:springInput path="obstetricHistoryDetail.observationMap[${MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY.conceptId}].valueText" size="4" /></td>
			<td class="label">Month<spring:bind path="obstetricHistoryDetail.observationMap[${MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY.conceptId}].valueNumeric">
			<form:select path="${status.expression}">
				<form:option value="">Choose</form:option>
				<form:option value="${1.0}">January</form:option>
				<form:option value="${2.0}">February</form:option>
				<form:option value="${3.0}">March</form:option>
				<form:option value="${4.0}">April</form:option>
				<form:option value="${5.0}">May</form:option>
				<form:option value="${6.0}">June</form:option>
				<form:option value="${7.0}">July</form:option>
				<form:option value="${8.0}">August</form:option>
				<form:option value="${9.0}">September</form:option>
				<form:option value="${10.0}">October</form:option>
				<form:option value="${11.0}">November</form:option>
				<form:option value="${12.0}">December</form:option>
			</form:select>
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
			</tr><tr>
			
			<td class="label">Place of Delivery*</td>
			<td><chits_tag:springDropdown path="obstetricHistoryDetail.observationMap[${MCObstetricHistoryDetailsConcepts.PLACE_OF_DELIVERY.conceptId}].valueCoded" select="select location" answers="${chits:answers(MCObstetricHistoryDetailsConcepts.PLACE_OF_DELIVERY)}" /></td>			
			<td rowspan="3" class="alert" style="vertical-align: top;">
				* if with more than one pregnancy in the same year
			</td></tr>
			
			<tr><td class="label">Gravida*</td>
			<td><chits_tag:springInput path="obstetricHistoryDetail.observationMap[${MCObstetricHistoryDetailsConcepts.GRAVIDA.conceptId}].valueText" size="2" /></td></tr>

			<td class="label">Assisted by*</td>
			<td><chits_tag:springDropdown path="obstetricHistoryDetail.observationMap[${MCObstetricHistoryDetailsConcepts.DELIVERY_ASSISTANT.conceptId}].valueCoded" answers="${chits:answers(MCObstetricHistoryDetailsConcepts.DELIVERY_ASSISTANT_ANSWERS)}" select="select delivery assistant" /></td>
			</table>
		</td>
	</tr>
</table>

<fieldset><legend><span>Pregnancy outcome</span></legend>
<table id="pregnancy-outcomes" class="full-width registration form">
<thead><tr><th>Sex</th><th>Weight<br/>(kg)</th><th>Term</th><th>Method</th><th>Outcome</th><th>Breastfed<br/>w/in 1 Hr?</th></tr></thead>
<tbody id="pregnancy-outcome-body"><c:forEach var="outcome" items="${form.obstetricHistoryDetail.outcomes}" varStatus="i">
<tr id="outcome-${i.index}"><td>
	<chits_tag:springDropdown path="obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.SEX.conceptId}].valueCoded" answers="${chits:answers(MCPregnancyOutcomeConcepts.SEX_ANSWERS)}" select="undetermined" />
</td><td>
	<chits_tag:springInput path="obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.BIRTH_WEIGHT_OF_BABY_KG.conceptId}].valueText" size="4" />
</td><td>
	<chits_tag:springDropdown path="obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.TERM.conceptId}].valueCoded" answers="${chits:answers(MCPregnancyOutcomeConcepts.TERM_ANSWERS)}" select="specify term" />
</td><td>
	<chits_tag:springDropdown path="obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.METHOD.conceptId}].valueCoded" answers="${chits:answers(MCPregnancyOutcomeConcepts.METHOD_ANSWERS)}" select="select method" />
</td><td>
	<chits_tag:springDropdown path="obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.OUTCOME.conceptId}].valueCoded" answers="${chits:answers(MCPregnancyOutcomeConcepts.OUTCOME_ANSWERS)}" select="unknown" />
</td><td>
	<spring:bind path="obstetricHistoryDetail.outcomes[${i.index}].observationMap[${MCPregnancyOutcomeConcepts.BREASTFED_WITHIN_HOUR.conceptId}].valueCoded">
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
<br/>
<input type="button" onclick="addPregnancyOutcome()" value="Add another pregnancy outcome" />
</fieldset>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelObstetricHistoryDetail()" />
<c:if test="${not empty form.obstetricHistoryDetail.obs.obsId}">
<input type="button" id="deleteButton" value='Delete' onclick="deleteObstetricHistoryDetail(this.form)" />
</c:if>
</div>
</form:form>

<table style="display:none">
<tbody id="outcomeTemplate">
<tr id="outcome-0"><td>
	<select name="obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.SEX.conceptId}].valueCoded">
		<option value="">undetermined</option><c:forEach var="answer" items="${chits:answers(MCPregnancyOutcomeConcepts.SEX_ANSWERS)}">
		<option value="${answer.conceptId}">${answer.name}</option></c:forEach>
	</select>
</td><td>
	<input type="text" name="obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.BIRTH_WEIGHT_OF_BABY_KG.conceptId}].valueText" size="4" />
</td><td>
	<select name="obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.TERM.conceptId}].valueCoded">
		<option value="">specify term</option><c:forEach var="answer" items="${chits:answers(MCPregnancyOutcomeConcepts.TERM_ANSWERS)}">
		<option value="${answer.conceptId}">${answer.name}</option></c:forEach>
	</select>
</td><td>
	<select name="obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.METHOD.conceptId}].valueCoded">
		<option value="">select method</option><c:forEach var="answer" items="${chits:answers(MCPregnancyOutcomeConcepts.METHOD_ANSWERS)}">
		<option value="${answer.conceptId}">${answer.name}</option></c:forEach>
	</select>
</td><td>
	<select name="obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.OUTCOME.conceptId}].valueCoded">
		<option value="">unknown</option><c:forEach var="answer" items="${chits:answers(MCPregnancyOutcomeConcepts.OUTCOME_ANSWERS)}">
		<option value="${answer.conceptId}">${answer.name}</option></c:forEach>
	</select>
</td><td>
	<select name="obstetricHistoryDetail.outcomes[0].observationMap[${MCPregnancyOutcomeConcepts.BREASTFED_WITHIN_HOUR.conceptId}].valueCoded">
		<option value="">Select</option>
		<option value="${chits:trueConcept()}">Yes</option>
		<option value="${chits:falseConcept()}">No</option>
	</select>
</td></tr>
</tbody></table>