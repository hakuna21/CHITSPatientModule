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

<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}); return false;">
<form:hidden path="version" />
<input type="hidden" name="dropout" value="true" />
<input type="hidden" name="enrollInNew" value="false" />

<fieldset><legend><span>FAMILY PLANNING METHOD DROPOUT FORM</span></legend>

NOTE: The patient must first discontinue the current or considered method before enrolling in or selecting a new method.
<br/><br/>
<table id="dropout-family-planning-method" class="full-width borderless registration">
	<tr>
		<td class="label" colspan="2">Current/Considered method: <chits_tag:obsValue obs="${form.familyPlanningMethod.obs}" shortName="${true}" /></td>
	</tr><tr>
		<td class="label">Date of discontinuation*</td>
		<td class="label"><chits_tag:springInput path="familyPlanningMethod.observationMap[${FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT.conceptId}].valueText" onclick="showCalendar(this)" /></td>
	</tr><tr>
		<td class="label">Reason*</td>
		<td class="label"><chits_tag:springDropdown path="familyPlanningMethod.observationMap[${FPFamilyPlanningMethodConcepts.DROPOUT_REASON.conceptId}].valueCoded" select="select reason" answers="${chits:answers(FPFamilyPlanningMethodConcepts.DROPOUT_REASON)}" /><%--
			NOTE: Dropout reason is of type 'text', so validation errors would be bound to the 'valueText' value --%><chits_tag:springFieldError path="familyPlanningMethod.observationMap[${FPFamilyPlanningMethodConcepts.DROPOUT_REASON.conceptId}].valueText" /></td>
	</tr><tr>
		<td class="label" colspan="2">
			<fieldset><legend>Remarks</legend>
				<chits_tag:springTextArea path="familyPlanningMethod.observationMap[${FPFamilyPlanningMethodConcepts.REMARKS.conceptId}].valueText" />
			</fieldset>
		</td>
	</tr>
</table>
</fieldset>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" name="saveAndEnrollInNewMethodButton" onclick="this.form.enrollInNew.value = 'true'" value='Enroll in New Method' />
<input type="submit" name="saveAndExitButton" value='Save and Exit' />
<input type="button" name="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>