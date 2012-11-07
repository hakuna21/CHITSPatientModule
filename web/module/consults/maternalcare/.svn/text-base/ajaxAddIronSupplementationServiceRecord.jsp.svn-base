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

<chits_tag:auditInfo obsGroup="${form.ironSupplementationServiceRecord.obs}" />

<br />
<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updateMCSection); return false;">
<form:hidden path="version" />

<fieldset><legend><span>Iron Supplementation</span></legend>
<table id="iron-supplementation-service-record" class="full-width borderless registration field">
	<tr>
		<td class="label">Date Given:</td>
		<td><chits_tag:springInput path="ironSupplementationServiceRecord.observationMap[${MCServiceRecordConcepts.DATE_ADMINISTERED.conceptId}].valueText" onclick="showCalendar(this)" id="ttDateAdministered" /></td>
	</tr><tr>
		<td class="label">Visit type:</td>
		<td><chits_tag:springDropdown path="ironSupplementationServiceRecord.observationMap[${IronSupplementationConcepts.VISIT_TYPE.conceptId}].valueCoded" answers="${chits:answers(IronSupplementationConcepts.VISIT_TYPE)}" select="Please Select" /></td>
	</tr><tr>
		<td class="label">Service Source:</td>
		<td><chits_tag:springDropdown path="ironSupplementationServiceRecord.observationMap[${IronSupplementationConcepts.SERVICE_SOURCE.conceptId}].valueCoded" answers="${chits:answers(IronSupplementationConcepts.SERVICE_SOURCE)}" select="Please Select" /></td>
	</tr><tr>
		<td class="label">Drug Formulary:</td>
		<td><chits_tag:springDropdown path="ironSupplementationServiceRecord.observationMap[${IronSupplementationConcepts.DRUG_FORMULARY.conceptId}].valueCoded" answers="${chits:answers(IronSupplementationConcepts.DRUG_FORMULARY)}" select="specify iron supplement given" /></td>
	</tr><tr>
		<td class="label">Quantity:</td>
		<td><chits_tag:springInput path="ironSupplementationServiceRecord.observationMap[${IronSupplementationConcepts.QUANTITY.conceptId}].valueText" /></td>
	</tr><tr>
		<td class="label">Administered by:</td>
		<td>
		<spring:bind path="ironSupplementationServiceRecord.administeredBy">
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
		<td><chits_tag:springTextArea path="ironSupplementationServiceRecord.observationMap[${IronSupplementationConcepts.REMARKS.conceptId}].valueText" rows="${5}"/></td>
	</tr>
</table>
</fieldset>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>