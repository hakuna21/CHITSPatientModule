<%@page import="java.util.Date"%>
<%@ page import="org.openmrs.module.chits.Util"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<link href="${pageContext.request.contextPath}/moduleResources/chits/scripts/consults/visits-section.css?v=${deploymentTimestamp}" type="text/css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/moduleResources/chits/scripts/consults/childcare/childcare-section.js?v=${deploymentTimestamp}" type="text/javascript" ></script>

<table id="childcare-section">
<tr><td class="left" style="width: 45%;">
	<%-- LEFT COLUMN --%>
	<c:choose><c:when test="${chits:getPatientState(form.patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.REGISTERED) eq null}">
		<%-- Not yet registered! --%>
		<jsp:include page="fragmentChildCareRegistration.jsp" />
	</c:when><c:otherwise>
		<%-- Already registered! --%>
		<jsp:include page="fragmentChildCareModule.jsp" />
	</c:otherwise></c:choose>
</td><td class="right">
	<%-- RIGHT COLUMN --%>
	<jsp:include page="fragmentChildCareChart.jsp" />
</td></tr>
</table>

<c:set var="tabHasErrors" value="${false}" scope="request" />

<c:if test="${form.patient.age ge ChildCareConstants.CHILDCARE_MAX_AGE and chits:getPatientState(form.patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.CLOSED) eq null}">

<div style="display:none" id="concludeProgramDlg">
<form:form action="concludeChildCareProgram.form" onsubmit="pleaseWaitDialog()">
	<input type="hidden" name="patient" value="${form.patient.patientId}" />
	This patient is already 6 years old, and is hence not eligible to receive services under the Child Care Program.
	<br/><br/>
	You may now end this program, but please make sure that you have all records updated propertly.
	<br/><br/>
	Once closed, you may not change anything in the charts anymore.  All recorded information will only be viewable.

	<h3>WOULD YOU LIKE TO CONCLUDE THIS PROGRAM?</h3>
</form:form>
</div>
<script>
$j(document).ready(function() {
	$j("#concludeProgramDlg").dialog({
		resizable:true,width:400,height:'auto',modal:true,title:'Conclude this program?',
		buttons:{
			"Yes":function(){$j("#concludeProgramDlg form").submit(); $j(this).dialog("close");},
			"No":function(){$j(this).dialog("close")}
		}
	})
})
</script>
</c:if>