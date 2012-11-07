<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>
<br/>
<div style="text-align: justify">
Reminder: Kindly refer to PHYSICIAN any checked (/) findings prior to provision of any method for further evaluation.
</div>

<br/>
<div class="full-width" style="text-align: left">
<table id="patient-needs-to-see-physician" class="borderless registration">
<tr>
	<spring:bind path="fpProgramObs.needsToSeePhysician">
	<td class="label">
		<form:checkbox path="${status.expression}" id="needsToSeePhysician" value="${chits:trueConcept()}" disabled="${status.value eq true}" />
	</td><td>
		<label for="needsToSeePhysician">Will see physician</label>
		<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
	</td>
	</spring:bind>
</tr>
</table>
</div>