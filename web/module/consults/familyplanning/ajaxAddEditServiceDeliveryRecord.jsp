<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />

<table class="full-width">
	<tr>
		<td colspan="2"><h1 style="text-align: center; margin: 0px;">FAMILY PLANNING PROGRAM</h1></td>
	</tr><tr>
		<td colspan="2"><h2 style="text-align: center; margin: 0px;">Service Delivery Form</h2></td>
	</tr>
</table>

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

<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}); return false;">
<form:hidden path="version" />

<table id="service-delivery-record" class="full-width borderless field registration">
	<tr>
		<td class="label">Active FP Method:</td>
		<td><chits_tag:obsValue obs="${form.familyPlanningMethod.obs}" shortName="${true}" /></td>
	</tr><tr>
		<td class="label">Date of service (mm/dd/yyyy)</td>
		<td><chits_tag:springInput path="serviceDeliveryRecord.observationMap[${FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED.conceptId}].valueText" onclick="showCalendar(this)" /></td>
	</tr><c:if test="${not form.familyPlanningMethod.permanentMethod}"><tr>
		<td class="label">Source of supply</td>
		<td><chits_tag:springDropdown path="serviceDeliveryRecord.observationMap[${FPServiceDeliveryRecordConcepts.SUPPLY_SOURCE.conceptId}].valueCoded" answers="${chits:answers(FPServiceDeliveryRecordConcepts.SUPPLY_SOURCE)}" select="select source" /></td>
	</tr><tr>
		<td class="label">Quantity (packs):</td>
		<td><chits_tag:springInput path="serviceDeliveryRecord.observationMap[${FPServiceDeliveryRecordConcepts.SUPPLY_QUANTITY.conceptId}].valueText" /></td>
	</tr></c:if><tr>
		<td class="label" colspan="2">
		<fieldset><legend>Remarks</legend>
			<chits_tag:springTextArea path="serviceDeliveryRecord.observationMap[${FPServiceDeliveryRecordConcepts.REMARKS.conceptId}].valueText" />
		</fieldset>
		</td>	
	</tr>
</table>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>