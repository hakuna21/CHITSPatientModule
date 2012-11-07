<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<style>
td.label { width: 12em; }
th.label { text-align: left; }
table.delivery-info, table.delivery-info td, table.delivery-info th { border: 0px; }
table.delivery-info select, table.delivery-info textarea { width: 100%; }
table.delivery-info textarea { height: 5em; }
td.right-label { text-align: right; }
label { cursor: pointer; }
</style>

<h3>NEWBORN SCREENING RESULTS</h3>

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
<%-- Newborn Screening Results information --%>


<form:form modelAttribute="form" method="post" action="updateChildCareNewbornScreeningResults.form" onsubmit="submitAjaxForm(this, ${form.patient.patientId}); return false;">
<form:hidden path="version" />
<input type="hidden" name="patientId" value="${form.patient.patientId}" />

<table class="form full-width">
<c:forEach var="finding" items="${form.newbornScreeningResults}">
<tr>
	<td><input type="checkbox" id="finding_${finding.key.conceptId}" name="newbornScreeningResults[${finding.key.conceptId}]" value="true" <c:if test="${finding.value}">checked="checked"</c:if> /></td>
	<td><label for="finding_${finding.key.conceptId}">${finding.key.name.name}</label></td>
</tr>
</c:forEach>
</table>

<table class="delivery-info full-width">
	<tr>
		<td colspan="4"><strong>Child Care Remarks:</strong></td>
	</tr>
	<tr>
		<td colspan="4">
			<spring:bind path="observationMap[${ChildCareConcepts.CHILDCARE_REMARKS.conceptId}].valueText">
			<form:textarea path="${status.expression}" cssClass="full-width" />
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
</table>

<input type="submit" id="saveButton" value='Save' />
</form:form>


<%-- End of Newborn Screening Results --%>
</c:when><c:otherwise>
<%@ include file="ajaxFragmentStartConsult.jsp" %>
</c:otherwise></c:choose>
