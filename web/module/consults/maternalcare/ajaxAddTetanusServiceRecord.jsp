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

<chits_tag:auditInfo obsGroup="${form.tetanusServiceRecord.obs}" />

<br />
<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updateMCSection); return false;">
<form:hidden path="version" />

<fieldset><legend><span>Tetanus Vaccination</span></legend>
<table id="tetanus-service-record" class="full-width borderless registration field">
	<tr>
		<td class="label">Date Given:</td>
		<td><chits_tag:springInput path="tetanusServiceRecord.observationMap[${TetanusToxoidRecordConcepts.DATE_ADMINISTERED.conceptId}].valueText" onclick="showCalendar(this)" id="ttDateAdministered" /></td>
	</tr><tr>
		<td class="label">Visit type:</td>
		<td><chits_tag:springDropdown path="tetanusServiceRecord.observationMap[${TetanusToxoidRecordConcepts.VISIT_TYPE.conceptId}].valueCoded" answers="${chits:answers(TetanusToxoidRecordConcepts.VISIT_TYPE)}" select="Please Select" /></td>
	</tr><tr>
		<td class="label">Service Source:</td>
		<td><chits_tag:springDropdown path="tetanusServiceRecord.observationMap[${TetanusToxoidRecordConcepts.SERVICE_SOURCE.conceptId}].valueCoded" answers="${chits:answers(TetanusToxoidRecordConcepts.SERVICE_SOURCE)}" select="Please Select" /></td>
	</tr><tr>
		<td class="label">Vaccine Type:</td>
		<td>
		<spring:bind path="tetanusServiceRecord.obs.valueCoded">
			<form:select path="${status.expression}" id="${id}">
				<form:option value="${chits:concept(TetanusToxoidDoseType.TT1)}">TT1</form:option>				
				<form:option value="${chits:concept(TetanusToxoidDoseType.TT2)}">TT2</form:option>				
				<form:option value="${chits:concept(TetanusToxoidDoseType.TT3)}">TT3</form:option>				
				<form:option value="${chits:concept(TetanusToxoidDoseType.TT4)}">TT4</form:option>				
				<form:option value="${chits:concept(TetanusToxoidDoseType.TT5)}">TT5</form:option>				
			</form:select>
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</spring:bind>
		</td>
	</tr><tr>
		<td class="label">Administered by:</td>
		<td>
		<spring:bind path="tetanusServiceRecord.administeredBy">
			<form:select path="${status.expression}">
				<c:forEach var="user" items="${healthWorkers}">
				<form:option value="${user}" label="${user.person.personName}" />
				</c:forEach>
			</form:select>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
		</spring:bind>
		</td>
	</tr><tr>
		<td class="label">Remarks:</td>
		<td><chits_tag:springTextArea path="tetanusServiceRecord.observationMap[${TetanusToxoidRecordConcepts.REMARKS.conceptId}].valueText" rows="${5}"/></td>
	</tr>
</table>
</fieldset>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>