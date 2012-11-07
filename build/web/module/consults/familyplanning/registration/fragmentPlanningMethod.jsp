<%@ page buffer="128kb"
%><%@ page import="java.util.Date"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:choose><c:when test="${not empty form.page}">
	<jsp:include page="fragmentRegistrationFormHeader.jsp" />
</c:when><c:otherwise>
	<jsp:include page="../fragmentAjaxUpdateHeader.jsp" />
</c:otherwise></c:choose>

<script>
$j(document).ready(function() {
	$j('#agree-to-family-planning-method').click(function() {
		if ($j(this).is(':checked')) {
			$j('#saveButton').button('enable')
		} else {
			$j('#saveButton').button('disable')
		}
	})
})
</script>

<br/>
<form:form id="family-planning-method-registration-form" modelAttribute="form" method="post" cssClass="main-form">

<fieldset><legend><span><c:choose
	><c:when test="${not empty form.page}">Last Page: FAMILY PLANNING METHOD</c:when
	><c:otherwise>FAMILY PLANNING METHOD ENROLLMENT FORM</c:otherwise></c:choose
></span></legend>
<form:hidden path="version" />
<form:hidden path="page" />

<table class="full-width borderless registration field">
<tr>
	<td>
		<fieldset>
		<table class="full-width registration field">
		<tr><td class="label">Date of enrollment* (mm/dd/yyyy)</td><td><chits_tag:springInput path="familyPlanningMethod.observationMap[${FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT.conceptId}].valueText" onclick="showCalendar(this)" /></td></tr>
		<c:choose><c:when test="${'F' eq form.patient.gender}">
		<tr><td class="label">Family Planning Method*</td><td><chits_tag:springDropdown path="familyPlanningMethod.obs.valueCoded" answers="${chits:members(FPMethodOptions.FEMALES)}" select="select method" /></td></tr>
		</c:when><c:otherwise>
		<tr><td class="label">Family Planning Method*</td><td><chits_tag:springDropdown path="familyPlanningMethod.obs.valueCoded" answers="${chits:members(FPMethodOptions.MALES)}" select="select method" /></td></tr>
		</c:otherwise></c:choose>
		<tr><td class="label">Client type*</td><td><chits_tag:springDropdown path="familyPlanningMethod.observationMap[${FPFamilyPlanningMethodConcepts.CLIENT_TYPE.conceptId}].valueCoded" answers="${chits:answers(FPFamilyPlanningMethodConcepts.CLIENT_TYPE)}" select="select client type" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>REMARKS</span></legend>
		<table class="full-width registration field">
		<tr><td style="text-align: justify">NOTE: You are required to state reason if permanent method is selected.</td></tr>
		<tr><td class="label"><chits_tag:springTextArea path="familyPlanningMethod.observationMap[${FPFamilyPlanningMethodConcepts.REMARKS.conceptId}].valueText" rows="${5}" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr>
</table>
</fieldset>

<c:choose><c:when test="${not empty form.page}">
	<div id="registration-footer">
		<br />
		<table><tr>
			<td><input type="checkbox" id="agree-to-family-planning-method" /></td>
			<td>
				<div style="text-align: justify;"><label for="agree-to-family-planning-method">
					The patient has signed her/his acknowledgement that the
					Physician/Nurse/Midwife of the clinic has fully explained the
					different methods available in family planning, and that s/he has
					freely chosen the mentioned being enrolled in above.
				</label></div>
			</td>
		</tr></table>
	</div>
	
	<br/>
	<div class="full-width" style="text-align: right">
	<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
	<input type="submit" id="saveButton" disabled="disabled" value='Save' />
	</div>
</c:when><c:otherwise>
	<br/>
	<div class="full-width" style="text-align: right">
	<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
	<input type="submit" id="saveButton" value='Save' />
	</div>
</c:otherwise></c:choose>

</form:form>
