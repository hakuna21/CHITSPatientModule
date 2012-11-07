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

function endingProgram(form) {
	if ($j("#ended-option").is(":checked")) {
		<%-- ending: open up the 'end maternal care program' dialog --%>
		loadAjaxForm("endMaternalCareProgram.form?patientId=${form.patient.patientId}", "END MATERNAL CARE PROGRAM", ${form.patient.patientId}, 470)
		return true;
	} else {
		<%-- not ending --%>
		return false;
	}
}
</script>

<form:form modelAttribute="form" method="post" onsubmit="if (!endingProgram(this)) { submitAjaxForm(this, ${form.patient.patientId}, updateMCSection); } return false;">
<form:hidden path="version" />
<c:set var="currentState" value= "${form.mcProgramObs.currentState}" />
<fieldset><legend>Update Patient Consult Status</legend>
<table id="update-patient-consult-status" class="full-width borderless registration">
	<tr>
		<td class="label">Date of Status Change*</td>
		<td><chits_tag:springInput path="patientConsultStatus.observationMap[${MCPatientConsultStatus.DATE_OF_CHANGE.conceptId}].valueText" onclick="showCalendar(this)" /></td>
	</tr><tr>
		<td class="label">Current Consult Status:</td>
		<td><chits_tag:maternalCareConsultStatus patient="${form.patient}" /></td>
	</tr><tr>
		<td class="label" colspan="2">Changing Consult Status to*:</td>
	</tr>
	<tr><td class="label" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;<chits_tag:springRadiobutton path="patientConsultStatus.status" label="Active" value="${MaternalCareProgramStates.ACTIVE}" disabled="${currentState eq MaternalCareProgramStates.ACTIVE}" /></td></tr>
	<tr><td class="label" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;<chits_tag:springRadiobutton path="patientConsultStatus.status" label="Referred" value="${MaternalCareProgramStates.REFERRED}" disabled="${currentState eq MaternalCareProgramStates.REFERRED or currentState eq MaternalCareProgramStates.ADMITTED}" /></td></tr>
	<tr><td class="label" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;<chits_tag:springRadiobutton path="patientConsultStatus.status" label="Admitted" value="${MaternalCareProgramStates.ADMITTED}" disabled="${currentState eq MaternalCareProgramStates.ADMITTED}" /></td></tr>
	<tr>
		<td class="label" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;<chits_tag:springRadiobutton path="patientConsultStatus.status" label="Ended" value="${MaternalCareProgramStates.ENDED}" id="ended-option" /> <span class="alert">Warning! Selecting "Ended" will close the patient's Maternal Record.</span>
		<spring:bind path="patientConsultStatus.status"><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></spring:bind>
		</td>
	</tr>
</table>
</fieldset>

<fieldset><legend>Update Patient Consult Status</legend>
<table id="remarks" class="full-width borderless registration">
<tr><td><chits_tag:springTextArea path="patientConsultStatus.observationMap[${MCPatientConsultStatus.REMARKS.conceptId}].valueText" /></td></tr>
</table>
</fieldset>

<%-- Add patient consult status history chart --%>
<jsp:include page="chartfragments/consultStatusHistory.jsp" />

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>
