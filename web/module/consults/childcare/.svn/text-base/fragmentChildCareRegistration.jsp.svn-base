<%@ include file="/WEB-INF/template/include.jsp"%>

<h3>Child Care Registration Form</h3>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />

<strong>IMPORTANT: </strong>
You need to fill up this form before you may proceed to include the
child in the Child Care Program. Once enrolled, the child may not be
removed from the program until s/he reaches 6 years old.

<c:if test="${chits:getPatientState(form.patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.REGISTERED) eq null}">
<h4 class="alert">An infant/child on her/his first visit should be seen by the physician.</h4>
</c:if>

<form:form modelAttribute="form" method="post" id="patientForm" action="submitChildCareRegistration.form" onsubmit="pleaseWaitDialog()">
<form:hidden path="version" />
<input type="hidden" name="patientId" value="${form.patient.patientId}" />

<spring:bind path="timestampDate">
Date Registered* <i style="font-weight: normal; font-size: .8em;">(<openmrs:datePattern />)</i>
<input name="${status.expression}" onFocus="showCalendar(this)" value="${status.value}"/>
<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
</spring:bind><br/>

<style>
td.label { width: 12em; }
th.label { text-align: left; }
table.delivery-info, table.delivery-info td, table.delivery-info th { border: 0px; }
table.delivery-info select, table.delivery-info textarea { width: 80%; }
table.delivery-info textarea { height: 5em; }
td.right-label { text-align: right; }
</style>


<%@ include file="editParentsInfo.jsp" %>

<table class="delivery-info full-width">
<%@ include file="editDeliveryInfo.jsp" %>
	<tr>
		<td colspan="4"><strong>Child Care Remarks:</strong></td>
	</tr>
	<tr>
		<td colspan="4">
			<spring:bind path="observationMap[${ChildCareConcepts.CHILDCARE_REMARKS.conceptId}].valueText">
			<form:textarea path="${status.expression}" />
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
</table>
<input type="submit" value="Save" />
</form:form>