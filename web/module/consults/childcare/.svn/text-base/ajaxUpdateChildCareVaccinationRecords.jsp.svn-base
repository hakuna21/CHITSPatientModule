<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<h3>VACCINATIONS FOR UPDATE</h3>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />
<script>
function toggle(rowId) {
	var fields = $j("#" + rowId + " input[type=text], #" + rowId + " select")
	if ($j("#" + rowId + ' input[type=checkbox]:eq(0)').is(":checked")) {
		fields.attr('disabled', '')
		$j("#" + rowId + " input.calendar").val('${form.timestampDate}')
	} else {
		fields.attr('disabled', 'disabled')
	}
}

function confirmVaccines(form) {
	$j("<div><h5 class='alert'>Once saved, you will not be able to edit this anymore.</h5></div>").dialog({
		resizable:true,width:400,height:'auto',modal:true,
		title:'Have you reviewed the information?',
		buttons:{
			"Yes, I've reviewed all the information":function(){$j(this).dialog("close"); submitAjaxForm(form, ${form.patient.patientId})},
			"Cancel":function(){$j(this).dialog("close")}
		}
	})
}
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
<%-- Vaccination Information --%>


<form:form modelAttribute="form" method="post" action="updateChildCareVaccinationRecords.form" onsubmit="confirmVaccines(this); return false;">
<form:hidden path="version" />
<input type="hidden" name="patientId" value="${form.patient.patientId}" />

<c:set var="birthWeight" value="${chits:observation(form.patient, ChildCareConcepts.BIRTH_WEIGHT).valueNumeric}" />
<c:choose><c:when test="${birthWeight eq null}">
<span class="alert">WARNING: The child's birth weight is still unkonown...</span>
</c:when><c:when test="${birthWeight lt GlobalProperty[ChildCareConstants.GP_LOW_BIRTHWEIGHT_VACCINATION_WARNING]}">
<span class="alert">The child has a LOW BIRTH WEIGHT (${birthWeight} kg).</span>
</c:when></c:choose>
<table class="form full-width">
<tr>
	<th>&nbsp;</th>
	<th>ANTIGEN</th>
	<th>DATE GIVEN</th>
	<th>HEALTH FACILITY</th>
</tr>
<c:forEach var="vaccinationRecord" items="${form.vaccinationRecords}" varStatus="i">
<tr id="vaccinationRecord_${i.index}">
	<c:set var="disabled" value="${not vaccinationRecord.value.include}" />
	<c:set var="antigen" value="${vaccinationRecord.key}" />
	<td><form:checkbox id="antigen_${i.index}" onclick="toggle('vaccinationRecord_${i.index}')" path="vaccinationRecords[${antigen.conceptId}].include" value="true" /></td>
	<td>
		<c:choose><c:when test="${antigen.conceptId ne ChildCareVaccinesConcepts.OTHERS.conceptId}">
			<label for="antigen_${i.index}">${antigen.name}</label>
		</c:when><c:otherwise>
			<spring:bind path="vaccinationRecords[${antigen.conceptId}].antigen.valueText">
				<label for="antigen_${i.index}">Other: </label>
				<form:input path="${status.expression}" disabled="${disabled}" size="12" maxlength="64" />
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</c:otherwise></c:choose>
	</td>
	<spring:bind path="vaccinationRecords[${antigen.conceptId}].dateAdministered.valueText">
	<td>
		<form:input path="${status.expression}" onfocus="showCalendar(this)" cssClass="calendar" disabled="${disabled}" id="vrda_${i.index}" />
		<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
	</td>
	</spring:bind>
	<spring:bind path="vaccinationRecords[${antigen.conceptId}].healthFacility.valueCoded">
	<td>
		<form:select path="${status.expression}" disabled="${disabled}">
			<c:forEach var="healthFacility" items="${chits:answers(VaccinationConcepts.HEALTH_FACILITY)}">
			<form:option value="${healthFacility.conceptId}" label="${healthFacility.name.name}" />
			</c:forEach>
		</form:select>
	</td>
	</spring:bind>
</tr>
</c:forEach>
</table>

<spring:bind path="administeredBy">
	Administered By: 
	<form:select path="${status.expression}">
		<c:forEach var="user" items="${healthWorkers}">
		<form:option value="${user}" label="${user.person.personName}" />
		</c:forEach>
	</form:select>
	<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
</spring:bind>

<table class="form full-width">

</table>
<h4 class="alert">
Review all information recorded above before saving.<br/>
Once saved, you will not be able to edit this anymore.
</h4>
<input type="submit" id="saveButton" value='Save' />
</form:form>


<%-- End of Vaccination Information --%>
</c:when><c:otherwise>
<%@ include file="ajaxFragmentStartConsult.jsp" %>
</c:otherwise></c:choose>
