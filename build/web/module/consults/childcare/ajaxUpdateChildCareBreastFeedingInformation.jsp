<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<h3>BREASTFEEDING INFORMATION</h3>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />

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
<%-- Breastfeeding information --%>

<strong>IMPORTANT:</strong>
Answer yes only if the child has been confirmed to be exclusively breastfed for the entire month. 
<form:form modelAttribute="form" method="post" action="updateChildCareBreastFeedingInformation.form" onsubmit="submitAjaxForm(this, ${form.patient.patientId}); return false;">
<form:hidden path="version" />
<input type="hidden" name="patientId" value="${form.patient.patientId}" />

<table class="form full-width">
<tr>
	<th>PERIOD COVERED</th>
	<th>BREASTFED EXCLUSIVELY?</th>
</tr>
<c:forEach var="breastFedOnMonth" items="${form.breastFeedingInformation}" varStatus="monthNumber">
<spring:bind path="breastFeedingInformation[${breastFedOnMonth.key}].valueText">
<tr>
	<td>${breastFedOnMonth.key.name}</td>
	<td>
		<c:choose><c:when test="${breastFedOnMonth.value.valueNumeric ne null}">
			<c:if test="${breastFedOnMonth.value.valueNumeric ne 0}">Yes</c:if>
			<c:if test="${breastFedOnMonth.value.valueNumeric eq 0}">No</c:if>
		</c:when><c:otherwise>
			<c:if test="${monthNumber.index lt ageInMonths}">
			<form:select path="${status.expression}">
				<option value="">Please select</option>
				<form:option value="true">Yes</form:option>
				<form:option value="false">No</form:option>
			</form:select>
			</c:if>
		</c:otherwise></c:choose>
	</td>
</tr>
</spring:bind>
</c:forEach>
</table>

<input type="submit" id="saveButton" value='Save' />
</form:form>


<%-- End of Breastfeeding Results --%>
</c:when><c:otherwise>
<%@ include file="ajaxFragmentStartConsult.jsp" %>
</c:otherwise></c:choose>
