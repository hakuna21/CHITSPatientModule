<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/css/jquery.dataTables.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/css/TableTools_JUI.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/css/TableTools.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/js/TableTools.min.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/js/ZeroClipboard.js" />
<script>
$j(document).ready(function() {
	$j("#masterListOfPatientsReport").dataTable({"bSort": false,
		"sDom":'T<"clear">lfrtip',
		"oTableTools":{
			"sSwfPath":'${pageContext.request.contextPath}/moduleResources/chits/scripts/jquery.datatable/TableTools/swf/copy_cvs_xls_pdf.swf',
			"aButtons": [ "print", {"sExtends":"xls","sTitle":"Master list of patients"}, "copy" ]
		}
	});

	$j("#generalForm").dialog("option", "width", 1000)
})
</script>

<style>
div.DTTT_container { vertical-align: middle; margin-left: 0.5em; margin-top: -4px;}
div.DTTT_container button { height: 28px; vertical-align: middle; }
div.DTTT_container button span { font-size: 10px; vertical-align: middle; }
#masterListOfPatientsReport { border: 1px solid #aaa; }
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

<strong>MASTER LIST OF PATIENTS</strong><br/>
<br/>

<p><strong style="color: red">NOTE: Use 'CTRL+' to zoom.</strong></p>

<div id="reports">
	<div id="patients">
		<table id="masterListOfPatientsReport" class="form">
		<thead>
			<tr>
				<th>PATIENT NAME</th>
				<th>PATIENT ID</th>
				<th>BIRTHDATE</th>
				<th>SEX</th>
				<th>AGE</th>
				<th>CIVIL STATUS</th>
				<th>PHILHEALTH ID</th>
				<th>PHILHEALTH STATUS</th>
				<th>FAMILY ID</th>
				<th>DATE REGISTERED</th>
				<th>LAST UPDATED</th>
			</tr>
		</thead>
		<tbody><c:forEach var="patient" items="${patients}"><chits_tag:foldersOf patient="${patient}" />
			<tr id="patient_${patient.patientId}">
				<td>${patient.personName}</td>
				<td>${patient.patientIdentifier}</td>
				<td><c:choose><c:when test="${not empty patient.birthdate}"><fmt:formatDate pattern="MM/dd/yyyy" value="${patient.birthdate}" /></c:when><c:otherwise>-</c:otherwise></c:choose></td>
				<td>${patient.gender}</td>
				<td><c:choose><c:when test="${not empty patient.birthdate}"><chits:age birthdate="${patient.birthdate}" /></c:when><c:otherwise>-</c:otherwise></c:choose></td>
				<td>${chits:coalesce(chits:conceptByIdOrName(patient.attributeMap[CivilStatusConcepts.CIVIL_STATUS.conceptName].value).name, '-')}</td>
				<td>${chits:coalesce(patient.attributeMap[PhilhealthConcepts.CHITS_PHILHEALTH], '-')}</td>
				<td><chits_tag:philhealthStatus patient="${patient}" />${philhealthStatus}</td>
				<c:choose><c:when test="${not empty folders}">
				<td>${folders[0].code}</td>
				</c:when><c:otherwise>
				<td>-</td>
				</c:otherwise></c:choose>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${patient.dateCreated}" /></td>
				<td><c:choose><c:when test="${patient.dateChanged ne null}"><fmt:formatDate pattern="MM/dd/yyyy" value="${patient.dateChanged}" /></c:when><c:otherwise>-</c:otherwise></c:choose></td>
			</tr>
		</c:forEach></tbody>
		</table>
	</div>
	
	<br/><br/><br/>
</div>

<form:form modelAttribute="form" method="post" action="masterListOfPatientsReport.form" onsubmit="loadAjaxForm('masterListOfPatientsReport.form', 'Master List of Patients Report', 1000); return false;">
	<input type="submit" value="Reload" />
</form:form>
