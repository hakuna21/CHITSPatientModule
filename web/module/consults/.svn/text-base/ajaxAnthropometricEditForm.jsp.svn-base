<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<h3>UPDATE ANTHROPOMETRIC MEASUREMENTS</h3>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.timeentry/jquery.timeentry.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.timeentry/jquery.timeentry.js" />
<script>
$j(document).ready(function() {
	$j('input[name=timestampTime]').timeEntry({spinnerImage:'${pageContext.request.contextPath}/moduleResources/chits/scripts/jquery.timeentry/spinnerText.png',spinnerSize: [30, 20, 8]});
})
</script>

<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
<c:if test="${err != null}">
	<div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div>
</c:if>

<%-- NOTE: Display the form if and only if the patient queue encounter has already been initialized! --%>
<c:choose><c:when test="${not empty form.patientQueue.encounter}">
	<form:form modelAttribute="form" method="post" action="#" onsubmit="submitAnthropometricDataForm(this, ${form.patient.patientId}); return false;">
		<form:hidden path="version" />
		<spring:bind path="timestampDate">
		Date:   <input name="${status.expression}" onFocus="showCalendar(this,125)" value="${status.value}"/>
		Time:   <input type="text" id="timestampTime" name="timestampTime" size="10" value="${form.timestampTime}" />	
		<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
		</spring:bind><br/>

		<div style="font-size: 9px">* - Required Field</div>
		<table style="width: 100%;" class="form">
			<spring:bind path="observationMap[${VisitConcepts.HEIGHT_CM.conceptId}].valueText">
			<tr>
				<td>Enter New <c:choose><c:when test="${form.patient.age lt 2}">Length</c:when><c:otherwise>Height</c:otherwise></c:choose>: </td>
				<td><input name="${status.expression}" maxlength="4" size="6" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
				<td>cm.</td>
			</tr>
			</spring:bind>
			<spring:bind path="observationMap[${VisitConcepts.WEIGHT_KG.conceptId}].valueText">
			<tr>
				<td>Enter New Weight: </td>
				<td><input name="${status.expression}" maxlength="4" size="6" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
				<td>kg.</td>
			</tr>
			</spring:bind>
			<spring:bind path="observationMap[${VisitConcepts.WAIST_CIRC_CM.conceptId}].valueText">
			<tr>
				<td>Enter New Waist Circumference: </td>
				<td><input name="${status.expression}" maxlength="4" size="6" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
				<td>cm.</td>
			</tr>
			</spring:bind>
			<spring:bind path="observationMap[${VisitConcepts.HIP_CIRC_CM.conceptId}].valueText">
			<tr>
				<td>Enter New Hip Circumference: </td>
				<td><input name="${status.expression}" maxlength="4" size="6" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
				<td>cm.</td>
			</tr>
			</spring:bind>
			<spring:bind path="observationMap[${VisitConcepts.HEAD_CIRC_CM.conceptId}].valueText">
			<tr>
				<td>Enter New Head Circumference: </td>
				<td><input name="${status.expression}" maxlength="4" size="6" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
				<td>cm.</td>
			</tr>
			</spring:bind>
			<spring:bind path="observationMap[${VisitConcepts.CHEST_CIRC_CM.conceptId}].valueText">
			<tr>
				<td>Enter New Chest Circumference: </td>
				<td><input name="${status.expression}" maxlength="4" size="6" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
				<td>cm.</td>
			</tr>
			</spring:bind>
		</table>
		<input type="submit" name="action" id="saveButton" value='Save' />
	</form:form>
</c:when><c:otherwise>
<%@ include file="ajaxFragmentStartConsult.jsp" %>
</c:otherwise></c:choose>
