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

<table id="service-delivery-record" class="full-width borderless registration">
	<tr>
		<td class="label">Active FP Method:</td>
		<td><chits_tag:obsValue obs="${form.familyPlanningMethod.obs}" shortName="${true}" /></td>
	</tr><tr>
		<td class="label">Date of service (mm/dd/yyyy)</td>
		<td><chits_tag:obsValue obs="${chits:observation(form.serviceDeliveryRecord.obs, FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED)}" /></td>
	</tr><c:if test="${not form.familyPlanningMethod.permanentMethod}"><tr>
		<td class="label">Source of supply</td>
		<td><chits_tag:obsValue obs="${chits:observation(form.serviceDeliveryRecord.obs, FPServiceDeliveryRecordConcepts.SUPPLY_SOURCE)}" /></td>
	</tr><tr>
		<td class="label">Quantity (packs):</td>
		<td><chits_tag:obsValue obs="${chits:observation(form.serviceDeliveryRecord.obs, FPServiceDeliveryRecordConcepts.SUPPLY_QUANTITY)}" /></td>
	</tr></c:if><tr>
		<td class="label" colspan="2">
		<fieldset><legend>Remarks</legend>
			<chits_tag:obsValue obs="${chits:observation(form.serviceDeliveryRecord.obs, FPServiceDeliveryRecordConcepts.REMARKS)}" />
		</fieldset>
		</td>	
	</tr><c:if test="${not form.familyPlanningMethod.permanentMethod}"><tr>
		<td class="label" colspan="2">
		<div><br/><em>The delivery service record has been saved: Now please indicate the date of next service.</em></div>
		<fieldset><legend>Date of next service (mm/dd/yyyy)</legend>
			<chits_tag:springInput path="familyPlanningMethod.observationMap[${FPFamilyPlanningMethodConcepts.DATE_OF_NEXT_SERVICE.conceptId}].valueText" onclick="showCalendar(this)" />
		</fieldset>
		</td>	
	</tr></c:if>
</table>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>