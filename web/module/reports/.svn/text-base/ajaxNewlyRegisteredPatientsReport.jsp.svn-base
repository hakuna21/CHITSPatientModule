<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%>

<c:set var="dateRange"><spring:bind path="form.startDate">${status.value}</spring:bind> to <spring:bind path="form.endDate">${status.value}</spring:bind></c:set>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/css/jquery.dataTables.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/css/TableTools_JUI.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/css/TableTools.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/js/TableTools.min.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/js/ZeroClipboard.js" />
<script>
$j(document).ready(function() {
	$j("#newlyRegisteredPatients").dataTable({"bSort": false,
		"sDom":'T<"clear">lfrtip',
		"oTableTools":{
			"sSwfPath":'${pageContext.request.contextPath}/moduleResources/chits/scripts/jquery.datatable/TableTools/swf/copy_cvs_xls_pdf.swf',
			"aButtons": [ "print", {"sExtends":"xls","sTitle":"Newly Registered Patients - ${dateRange}"}, "copy" ]
		}
	});

	<c:if test="${not empty patients}">
	$j("#generalForm").dialog("option", "width", 1000)
	</c:if>
})
</script>

<style>
div.DTTT_container { vertical-align: middle; margin-left: 0.5em; margin-top: -4px;}
div.DTTT_container button { height: 28px; vertical-align: middle; }
div.DTTT_container button span { font-size: 10px; vertical-align: middle; }
#newlyRegisteredPatients { border: 1px solid #aaa; }
</style>

<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
<c:if test="${err != null}">
	<span class="validationError"><div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div></span>
</c:if>

<spring:hasBindErrors name="form">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<c:choose><c:when test="${empty patients}">

<form:form modelAttribute="form" method="post" action="newlyRegisteredPatientsReport.form" onsubmit="submitAjaxForm(this); return false;">
<h3><spring:message code="chits.reports.newlyregisteredpatients.title" /></h3>

<h4>INCLUSIVE REPORT FOR THIS DATE</h4>

<table class="borderless">
	<spring:bind path="startDate">
	<tr><td>Start Date:<br/><i style="font-weight: normal; font-size: .8em;">(<spring:message code="general.format"/>: <openmrs:datePattern />)</i></td><td><form:input onclick="showCalendar(this)" path="${status.expression}" id="startDate" size="10" /><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td></tr>
	</spring:bind>
	<spring:bind path="endDate">
	<tr><td>End Date:<br/><i style="font-weight: normal; font-size: .8em;">(<spring:message code="general.format"/>: <openmrs:datePattern />)</i></td><td><form:input onclick="showCalendar(this)" path="${status.expression}" id="endDate" size="10" /><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td></tr>
	</spring:bind>
</table>

<br/>

<input type="submit" value="Generate" />
<input type="button" value="Cancel" onclick="javascript:$j('#generalForm').dialog('close')" />
</form:form>

</c:when><c:otherwise>

<strong>NEWLY REGISTERED PATIENTS</strong><br/>
REPORT DATE: <strong>${dateRange}</strong>
<br/>

<p><strong style="color: red">NOTE: Use 'CTRL+' to zoom.</strong></p>

<div id="reports">
	<div id="patients">
		<table id="newlyRegisteredPatients" class="form">
		<thead>
			<tr>
				<th>DATE REGISTERED</th>
				<th>PATIENT ID</th>
				<th>NAME</th>
				<th>BIRTHDATE / AGE</th>
				<th>SEX</th>
				<th>CIVIL STATUS</th>
				<th>FAMILY ID</th>
				<th>PHILHEALTH ID</th>
				<th>PHILHEALTH STATUS</th>
			</tr>
		</thead>
		<tbody><c:forEach var="patient" items="${patients}"><chits_tag:foldersOf patient="${patient}" />
			<tr id="patient_${patient.patientId}">
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${patient.dateCreated}" /></td>
				<td>${patient.patientIdentifier}</td>
				<td>${patient.personName}</td>
				<td><c:choose><c:when test="${not empty patient.birthdate}"><fmt:formatDate pattern="MM/dd/yyyy" value="${patient.birthdate}" /> / <chits:age birthdate="${patient.birthdate}" on="${patient.dateCreated}" /></c:when><c:otherwise>-</c:otherwise></c:choose></td>
				<td>${patient.gender}</td>
				<td>${chits:coalesce(chits:conceptByIdOrName(patient.attributeMap[CivilStatusConcepts.CIVIL_STATUS.conceptName].value).name, '-')}</td>
				<c:choose><c:when test="${not empty folders}">
				<td>${folders[0].code}</td>
				</c:when><c:otherwise>
				<td>-</td>
				</c:otherwise></c:choose>
				<td>${chits:coalesce(patient.attributeMap[PhilhealthConcepts.CHITS_PHILHEALTH], '-')}</td>
				<td><chits_tag:philhealthStatus patient="${patient}" />${philhealthStatus}</td>
			</tr>
		</c:forEach></tbody>
		</table>
	</div>
	
	<br/><br/><br/>
</div>

<form:form modelAttribute="form" method="post" action="newlyRegisteredPatientsReport.form" onsubmit="submitAjaxForm(this); return false;">
	<form:hidden path="startDate" />
	<form:hidden path="endDate" />
	<input type="submit" value="Reload" />
</form:form>

</c:otherwise></c:choose>
