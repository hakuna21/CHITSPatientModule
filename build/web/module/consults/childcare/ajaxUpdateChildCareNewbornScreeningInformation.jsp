<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<h3>UPDATE NEWBORN SCREENING INFORMATION</h3>

<c:if test="${msg != null}">
<div id="screeningInfoMsg">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</div>

<div id="updatedNewbornScreeningInfo" style="display: none;">
<%@include file="fragmentNewbornScreeningInformation.jsp" %>
</div>
</c:if>
<c:if test="${err != null}">
	<div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div>
</c:if>

<form:form modelAttribute="form" method="post" action="updateChildCareNewbornScreeningInformation.form" onsubmit="submitNewbornScreeningInfo(this, ${form.patient.patientId}); return false;">
<input type="hidden" name="patientId" value="${form.patient.patientId}" />

<table class="borderless">
	<tr>
		<td>Report Date:</td>
		<td>
			<spring:bind path="observationMap[${NewbornScreeningInformation.REPORT_DATE.conceptId}].valueText">
				<form:input path="${status.expression}" onfocus="showCalendar(this)" cssClass="calendar" id="report_date" />
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td>Newborn Screening Date:</td>
		<td>
			<spring:bind path="observationMap[${NewbornScreeningInformation.SCREENING_DATE.conceptId}].valueText">
				<form:input path="${status.expression}" onfocus="showCalendar(this)" cssClass="calendar" id="screening_date" />
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td>Action:</td>
		<td>
			<spring:bind path="observationMap[${NewbornScreeningInformation.ACTION.conceptId}].valueCoded">
			<form:select path="${status.expression}">
				<form:option value="">Select Action</form:option>
				<c:forEach var="action" items="${chits:answers(NewbornScreeningInformation.ACTION)}">
				<form:option value="${action.conceptId}">${action.name}</form:option>
				</c:forEach>
			</form:select>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
</table>

<input type="submit" id="updateButton" value='Update' />
</form:form>
