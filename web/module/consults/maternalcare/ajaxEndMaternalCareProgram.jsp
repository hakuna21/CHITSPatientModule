<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />

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
function cancelForm() {
	$j("#generalForm").dialog('close')
}
</script>

<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updateMCSection); return false;">
<form:hidden path="version" />

<fieldset><legend>END MATERNAL CARE PROGRAM</legend>
Please indicate the reason for ending the maternal care program.
<table id="end-maternal-care-program" class="full-width borderless registration">
	<tr><td class="label"><chits_tag:springRadiobutton path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REASON_FOR_ENDING.conceptId}].valueCoded" label="Patient completed maternal care" value="${chits:concept(MCReasonForEndingMCProgram.COMPLETED)}" /></td></tr>
	<tr><td class="label"><chits_tag:springRadiobutton path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REASON_FOR_ENDING.conceptId}].valueCoded" label="Patient moved out to new location" value="${chits:concept(MCReasonForEndingMCProgram.MOVED)}" /></td></tr>
	<tr><td class="label"><chits_tag:springRadiobutton path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REASON_FOR_ENDING.conceptId}].valueCoded" label="Cannot be located" value="${chits:concept(MCReasonForEndingMCProgram.GONE)}" /></td></tr>
	<tr><td class="label"><chits_tag:springRadiobutton path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REASON_FOR_ENDING.conceptId}].valueCoded" label="Maternal death" value="${chits:concept(MCReasonForEndingMCProgram.DECEASED)}" /></td></tr>
	<tr><td class="label"><chits_tag:springRadiobutton path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REASON_FOR_ENDING.conceptId}].valueCoded" label="Non-maternal causes of death" value="${chits:concept(MCReasonForEndingMCProgram.DECEASED_NONMATERNAL)}" /></td></tr>
	<tr><td class="label"><chits_tag:springRadiobutton path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REASON_FOR_ENDING.conceptId}].valueCoded" label="Abortion" value="${chits:concept(MCReasonForEndingMCProgram.HAD_ABORTION)}" /></td></tr>
	<tr><td class="label"><chits_tag:springRadiobutton path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REASON_FOR_ENDING.conceptId}].valueCoded" label="Early termination of pregnancy" value="${chits:concept(MCReasonForEndingMCProgram.EARLY_TERMINATION)}" /></td></tr>
	<tr><td class="label"><chits_tag:springRadiobutton path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REASON_FOR_ENDING.conceptId}].valueCoded" label="H-mole" value="${chits:concept(MCReasonForEndingMCProgram.DIAGNOSED_HMOLE)}" /></td></tr>
	<tr><td class="label"><chits_tag:springRadiobutton path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REASON_FOR_ENDING.conceptId}].valueCoded" label="Others (Please specify under remarks)" value="${chits:concept(MCReasonForEndingMCProgram.OTHER)}" /></td></tr>
	<tr><td><chits_tag:springFieldError path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REASON_FOR_ENDING.conceptId}].valueCoded" /></td></tr>
</table>
</fieldset>

<fieldset><legend>Remarks:</legend>
<table id="remarks" class="full-width borderless registration">
<tr><td><chits_tag:springTextArea path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REMARKS.conceptId}].valueText" /></td></tr>
</table>
</fieldset>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="confirmButton" value='Confirm' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>