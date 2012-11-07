<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<style>
td.label { width: 12em; }
th.label { text-align: left; }
</style>

<h3>PARENTS INFORMATION</h3>

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
<%-- Parents information --%>

<form:form modelAttribute="form" method="post" action="updateChildCareParentsInformation.form" onsubmit="submitAjaxForm(this, ${form.patient.patientId}); return false;">
<form:hidden path="version" />
<input type="hidden" name="patientId" value="${form.patient.patientId}" />

<%@ include file="editParentsInfo.jsp" %>
<br/>
<input type="submit" id="saveButton" value='Save' />
</form:form>
<%-- End of Parents information --%>
</c:when><c:otherwise>
<%@ include file="ajaxFragmentStartConsult.jsp" %>
</c:otherwise></c:choose>
