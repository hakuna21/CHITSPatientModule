<%@ page buffer="128kb"
%><%@ page import="java.util.TreeSet,java.util.SortedSet"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/chits/patients/findPatient.htm" />

<spring:message var="pageTitle" code="chits.consult.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="../formStyle.jsp" %>

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/consults/main.css" />
<openmrs:htmlInclude file="/openmrs/dwr/interface/DWRCHITSConceptService.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.chits.helper.js" />

<h2><spring:message code="chits.consult.module"/></h2>
<br />
<%-- Display patient details --%>

<%
	SortedSet tabsWithError = new TreeSet();
	int currentTab = 0;
%>

<c:set var="showEndConsultTab" value="${form.patientQueue ne null and form.patientQueue.consultStart ne null}" />
<c:set var="showStartConsultTab" value="${form.patientQueue eq null or form.patientQueue.consultStart eq null}" />

<%@ include file="../pleaseWait.jsp" %>
<div id="visit-dialogs">
	<div id="anthropometricHistory" class="history"></div>
	<div id="vitalSignsHistory" class="history"></div>
	<div id="updateAnthropometricDataForm"></div>
	<div id="updateVitalSignsDataForm"></div>
	<div id="updateNotesForm"></div>
	<div id="generalForm"></div>
	<div id="subGeneralForm"></div>
	<div id="historyChart"></div>
	<div id="secondLevelGeneralForm"></div>
	<div id="confirmTemplate" title="Add Template?">
		<h4>Please review and confirm the template below:</h4>
		<textarea class="notes editable-notes" rows="14" cols="120" readonly="readonly"></textarea><br/>
	</div>
	<div id="consultHistory"></div>
	<chits_tag:programs patient="${form.patient}" />
	<div id="enrollInProgram" title="Select program to add patient to">
		<h4>Select the program to enroll the patient in:</h4>
		<form action="enrollInProgram.form" method="POST" onsubmit="pleaseWaitDialog()">
			<input type="hidden" name="patientId" value="${form.patient.patientId}" />
			<select name="program">
				<c:if test="${canEnrollInChildcare}"><option value="${ProgramConcepts.CHILDCARE}">Early Childhood Care and Development (ECCD)</option></c:if>
				<c:if test="${canEnrollInFamilyPlanning}"><option value="${ProgramConcepts.FAMILYPLANNING}">Family Planning (FP)</option></c:if>
				<c:if test="${canEnrollInMaternalCare}"><option value="${ProgramConcepts.MATERNALCARE}">Maternal Care (MC)</option></c:if>
			</select>
		</form>
	</div>
	<div id="newbornScreeningInfoForm"></div>
	<div id="findFemalePatientDialog">
		<b class="boxHeader">Search female Patients</b><div class="box"><div class="searchWidgetContainer" id="findFemalePatients"></div></div>
	</div>
	<div id="findMalePatientDialog">
		<b class="boxHeader">Search male Patients</b><div class="box"><div class="searchWidgetContainer" id="findMalePatients"></div></div>
	</div>
</div>

<div id="page-content">
	<div id="page-header">
		<jsp:include page="fragmentHeader.jsp" />
	</div>
	<div id="consult-tabs">
		<ul>
			<c:if test="${showStartConsultTab}">
			<li><a href="#start-consult">Start Consult</a></li>
			</c:if>
			<li><a href="#visit-details">Visit Details</a></li>
			<c:if test="${form.program ne null}">
			<li><a href="#program-details"><spring:message code="chits.program.${form.program}.title" /></a></li>
			</c:if>
			<%-- There's not enough space if a program tab exists, so only display the following tabs if there is no program tab! --%>
			<c:if test="${form.program eq null}">
			<li><a href="#special-consults">Special Consults</a></li>
			</c:if>
			<li><a href="#labs">Labs</a></li>
			<li><a href="#view-graphs">View Graphs</a></li>
			<li><a href="#dispense-drugs">Dispense Drugs</a></li>
			<li><a href="#set-appointments">Set Appointments</a></li>
			<c:if test="${showEndConsultTab}">
			<li><a href="#end-consult">End Consult</a></li>
			</c:if>
		</ul>

		<c:if test="${showStartConsultTab}">
		<c:set var="tabHasErrors" value="${false}" scope="request" />
		<div id="start-consult"><jsp:include page="fragmentStartConsult.jsp" /></div>
		<c:if test="${tabHasErrors}"><% tabsWithError.add(currentTab); %></c:if><% currentTab++; %>
		</c:if>

		<c:set var="tabHasErrors" value="${false}" scope="request" />
		<div id="visit-details">
			<jsp:include page="fragmentVisitDetails.jsp" />
		</div>
		<c:if test="${tabHasErrors}"><% tabsWithError.add(currentTab); %></c:if><% currentTab++; %>

		<c:if test="${form.program ne null}">
		<c:set var="tabHasErrors" value="${false}" scope="request" />
		<div id="program-details">
		<c:choose><c:when test="${form.program eq ProgramConcepts.CHILDCARE}">
			<jsp:include page="childcare/fragmentChildCareTab.jsp" />
		</c:when><c:when test="${form.program eq ProgramConcepts.FAMILYPLANNING}">
			<jsp:include page="familyplanning/fragmentFamilyPlanningTab.jsp" />
		</c:when><c:when test="${form.program eq ProgramConcepts.MATERNALCARE}">
			<jsp:include page="maternalcare/fragmentMaternalCareTab.jsp" />
		</c:when><c:otherwise>
			<em><strong>[Unknown Program]</strong></em>
		</c:otherwise></c:choose>
		<c:set var="programTab" value="<%= currentTab %>" />
		<c:if test="${tabHasErrors}"><% tabsWithError.add(currentTab); %></c:if><% currentTab++; %>
		</div>
		</c:if>


		<%-- There's not enough space if a program tab exists, so only display the following tabs if there is no program tab! --%>
		<c:if test="${form.program eq null}">
		<c:set var="tabHasErrors" value="${false}" scope="request" />
		<div id="special-consults"><jsp:include page="fragmentSpecialConsults.jsp" /></div>
		<c:if test="${tabHasErrors}"><% tabsWithError.add(currentTab); %></c:if><% currentTab++; %>
		</c:if>
		
		<c:set var="tabHasErrors" value="${false}" scope="request" />
		<div id="labs"><jsp:include page="fragmentLabs.jsp" /></div>
		<c:if test="${tabHasErrors}"><% tabsWithError.add(currentTab); %></c:if><% currentTab++; %>
		
		<c:set var="tabHasErrors" value="${false}" scope="request" />
		<div id="view-graphs"><jsp:include page="fragmentViewGraphs.jsp" /></div>
		<c:if test="${tabHasErrors}"><% tabsWithError.add(currentTab); %></c:if><% currentTab++; %>
		
		<c:set var="tabHasErrors" value="${false}" scope="request" />
		<div id="dispense-drugs"><jsp:include page="fragmentDispenseDrugs.jsp" /></div>
		<c:if test="${tabHasErrors}"><% tabsWithError.add(currentTab); %></c:if><% currentTab++; %>
		
		<c:set var="tabHasErrors" value="${false}" scope="request" />
		<div id="set-appointments"><jsp:include page="fragmentSetAppointments.jsp" /></div>
		<c:if test="${tabHasErrors}"><% tabsWithError.add(currentTab); %></c:if><% currentTab++; %>
		
		<c:if test="${showEndConsultTab}">
		<c:set var="tabHasErrors" value="${false}" scope="request" />
		<div id="end-consult"><jsp:include page="fragmentEndConsult.jsp" /></div>
		<c:if test="${tabHasErrors}"><% tabsWithError.add(currentTab); %></c:if><% currentTab++; %>
		</c:if>
	</div>
</div>

<%
	int displayTab = 0;
	if (!tabsWithError.isEmpty()) {
		displayTab = ((Number) tabsWithError.first()).intValue();
	}
%>
<script>
<c:choose><c:when test="${form.program ne null}">
	<%-- Display tab in error --%>
	$j(document).ready(function() {
		$j("#consult-tabs").tabs({selected:<c:out value="${programTab}" />})
	})
</c:when><c:otherwise>
	<%-- Force display of the program tab --%>
	$j(document).ready(function() {
		$j("#consult-tabs").tabs({selected:<%= displayTab %>})
	})
</c:otherwise></c:choose>
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>
