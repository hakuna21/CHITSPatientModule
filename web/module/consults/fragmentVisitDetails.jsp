<%@ page import="org.openmrs.Obs"
%><%@ page import="java.util.Date"
%><%@ page import="org.openmrs.module.chits.Util"
%><%@ page import="org.openmrs.module.chits.mcprogram.MaternalCareProgramObs"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<link href="${pageContext.request.contextPath}/moduleResources/chits/scripts/consults/visits-section.css?v=${deploymentTimestamp}" type="text/css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/moduleResources/chits/scripts/consults/visits-section.js?v=${deploymentTimestamp}" type="text/javascript" ></script>

<%-- Store a map of patient chart last taken observations --%>
<c:set var="patientChart" value="${form.lastTakenPatientChartObservations}" />

<table id="visits-section">
<tr><td class="left">
	<%-- LEFT COLUMN --%>
	<h3>VISIT DETAILS</h3>

	<openmrs:hasPrivilege privilege="View Programs">
	<chits_tag:programs patient="${form.patient}" />
	<h4>Programs Currently Enrolled In:</h4>
	<ul>
		<c:if test="${enrolledInChildcare}"><li><a href="viewChildCareProgram.form?patientId=${form.patient.patientId}">Early Childhood Care and Development (ECCD) Program</a></li></c:if>
		<c:if test="${enrolledInFamilyPlanning}"><li><a href="viewFamilyPlanningProgram.form?patientId=${form.patient.patientId}">Family Planning (FP) Program - <chits_tag:familyPlanningConsultStatus patient="${form.patient}" /></a></li></c:if>
		<c:if test="${enrolledInMaternalCare}"><li><a href="viewMaternalCareProgram.form?patientId=${form.patient.patientId}">Maternal Care (MC) Program - <chits_tag:maternalCareConsultStatus patient="${form.patient}" /></a></li></c:if>
		<c:if test="${!enrolledInAny}"><li>Not enrolled in any programs</li></c:if>
	</ul>

	<%-- Add a button to display the 'Enroll in New' button --%>
	<input type="button" <c:if test="${!enrollableInAny}">disabled="disabled"</c:if> onclick='javascript: enrollInNew("Enroll patient: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)")' value="Enroll in New" />
	</openmrs:hasPrivilege>

	<h4>CONSULT HISTORY</h4>
	<table id="consult-history" class="history">
	<tr><td>
		<center>
		General Consults
		<table id="general-visits" style="width: 200px;">
		<thead><tr><th>Visit Date</th></tr></thead><tbody>
		<c:forEach items="${form.encounters}" var="encounter"><c:set var="encounterDatetime" value="${encounter.encounterDatetime}" /><c:if test="${encounter ne form.patientQueue.encounter}">
			<tr><td>
				<fmt:formatDate value="${encounterDatetime}" pattern="EE, MMM d, yyyy" var="visitTimestamp" />
				<a href='javascript: loadConsultHistory("Consult History: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />): ${visitTimestamp} - <chits:elapsed since="${encounterDatetime}" /> ago", ${form.patient.patientId}, ${encounter.encounterId})'>${visitTimestamp}</a><br/>
				<span class="age-description"><chits:elapsed since="${encounterDatetime}" /> ago</span><br/>
				<c:set var="diagnoses" value="${chits:observations(encounter, VisitConcepts.DIAGNOSIS)}" />
				<c:if test="${not empty diagnoses}">
				<span class="illness-description">
						<c:forEach var="diagnosis" items="${diagnoses}" varStatus="i"><c:set var="icd10" value="${chits:mapping(diagnosis.valueCoded, 'ICD10')}" />
						<c:if test="${i.index ne 0}">, </c:if>${diagnosis.valueCoded.name.name}<c:if test="${not empty icd10}"> (${icd10})</c:if>
						</c:forEach>
				</span>
				</c:if>
			</td></tr>
		</c:if></c:forEach></tbody></table>
		</center>
	</td>
	<%--
	<td>
		<center>
		Special Consults

		<table id="special-visits">
		<thead><tr><th>Visit Date</th></tr></thead><tbody>
		<c:forEach items="${form.encounters}" var="encounter"><c:set var="encounterDatetime" value="${encounter.encounterDatetime}" />
			<tr><td>
				<fmt:formatDate value="${encounterDatetime}" pattern="EE, MMM d, yyyy" /><br/>
				<span class="age-description"><chits:elapsed since="${encounterDatetime}" /> ago</span>
			</td></tr>
		</c:forEach></tbody></table>
		</center>
	</td> --%>
	</tr>
	</table>

	<h4>Previous Programs:</h4>
	<ul>
		<c:if test="${concludedInChildcare}"><li><a href="viewChildCareProgram.form?patientId=${form.patient.patientId}">Early Childhood Care and Development (ECCD) Program</a></li></c:if>
		<c:if test="${concludedInFamilyPlanning}"><li><a href="#" onclick='loadHistoryChart("viewFamilyPlanningHistoryChart.form?patientId=${form.patient.patientId}", "VIEW FAMILY PLANNING HISTORY CHART", ${form.patient.patientId}, 1024); return false;'>Family Planning (FP) Program</a></li></c:if>
		<c:if test="${concludedInMaternalCare}"><c:forEach var="consultStatusObs" items="${chits:filterByCodedValue(chits:observations(form.patient, MCPatientConsultStatus.STATUS), MaternalCareProgramStates.ENDED)}"
		><c:set var="mcProgramObs" value="${consultStatusObs.obsGroup.obsGroup}" /><% 
			final MaternalCareProgramObs maternalCareProgramObs = new MaternalCareProgramObs((Obs) pageContext.findAttribute("mcProgramObs"));
			request.setAttribute("maternalCareProgramObs", maternalCareProgramObs);
		%>
		<li><em><a href="#" onclick='loadHistoryChart("viewMaternalCareHistoryChart.form?patientId=${form.patient.patientId}&maternalCareProgramObsId=${mcProgramObs.obsId}", "VIEW MATERNAL CARE HISTORY CHART", ${form.patient.patientId}, 1024); return false;'>maternal care program <chits_tag:obstetricScore obsGroup="${maternalCareProgramObs.obstetricHistory.obs}" /> <fmt:formatDate pattern="MM/dd/yyyy" value="${chits:findPatientProgramState(maternalCareProgramObs.patientProgram, MaternalCareProgramStates.REGISTERED).startDate}" /></a></em></li>
		</c:forEach></c:if>
		<c:if test="${!concludedInAny}"><li>Not previously enrolled in any programs</li></c:if>
	</ul>
	
	<c:set var="willSeePhysician" value="${Constants.FLAG_YES eq form.patient.attributeMap[MiscAttributes.SEE_PHYSICIAN].value}" />
	<br/><br/>
	<table style="width: 200px;"><tr><td>
	<form:form method="post" action="updateMustSeePhysicianFlag.form" onsubmit="pleaseWaitDialog()">
		<input type="hidden" name="patientId" value="${form.patient.patientId}" />
		<input type="radio" name="willSeePhysician" value="true" id="willSeePhysician" <c:if test="${willSeePhysician}">checked="checked"</c:if>/><label for="willSeePhysician">Will See Physician</label><br/>
		<input type="radio" name="willSeePhysician" value="false" id="willNotSeePhysician" <c:if test="${not willSeePhysician}">checked="checked"</c:if>/><label for="willNotSeePhysician">Will Not See Physician</label><br/>
		<input type="submit" value="Save" />
	</form:form>
	</td></tr></table>

    <br/>
    <h4>Patient Dashboard (other forms):</h4>
    <ul>
    <li><a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${form.patient.patientId}">Dashboard</a></li>
    </ul>

</td><td class="right">
	<%-- RIGHT COLUMN --%>
	<h3>PATIENT CHART</h3>
	<table id="patient-chart" class="borderless">
		<tr><td class="label"><c:choose><c:when test="${form.patient.age lt 2}">Length</c:when><c:otherwise>Height</c:otherwise></c:choose></td><td><chits:height obs="${patientChart[VisitConcepts.HEIGHT_CM.conceptId]}" /></td></tr>
		<tr><td class="label">Weight</td><td><chits:weight obs="${patientChart[VisitConcepts.WEIGHT_KG.conceptId]}" /></td></tr>
		<tr><td class="label">Body&nbsp;Mass&nbsp;Index</td><td><chits:bmi weight="${patientChart[VisitConcepts.WEIGHT_KG.conceptId]}" height="${patientChart[VisitConcepts.HEIGHT_CM.conceptId]}" birthdate="${form.patient.birthdate}"/></td></tr>
		<tr><td colspan="2"></td></tr>
		<tr><td class="label">Waist&nbsp;Circumference</td><td><chits:circumference obs="${patientChart[VisitConcepts.WAIST_CIRC_CM.conceptId]}" /></td></tr>
		<tr><td class="label">Hip&nbsp;Circumference</td><td><chits:circumference obs="${patientChart[VisitConcepts.HIP_CIRC_CM.conceptId]}" /></td></tr>
		<tr><td class="label">Waist-Hip&nbsp;Ratio</td><td><chits:waistToHipRatio waistCircumference="${patientChart[VisitConcepts.WAIST_CIRC_CM.conceptId]}" hipCircumference="${patientChart[VisitConcepts.HIP_CIRC_CM.conceptId]}" birthdate="${form.patient.birthdate}" /></td></tr>
		<tr><td colspan="2"></td></tr>
		<tr><td class="label">Head&nbsp;Circumference</td><td><chits:circumference obs="${patientChart[VisitConcepts.HEAD_CIRC_CM.conceptId]}" /></td></tr>
		<tr><td class="label">Chest&nbsp;Circumference</td><td><chits:circumference obs="${patientChart[VisitConcepts.CHEST_CIRC_CM.conceptId]}" /></td></tr>
	</table>
	<input type="button" value="History" onclick='javascript: loadAnthropometricHistory("Anthropometric History: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId})' />
	<input type="button" value="Update" onclick='javascript: loadAnthropometricUpdateForm("Update Patient Anthropometric Data: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId})' />
	
	<h3>Vital Signs</h3>
	<table id="vital-signs" class="borderless">
		<c:set var="vitalSignsCount" value="${0}" /> 

		<c:forEach var="enc" items="${form.encounters}">
		<c:if test="${vitalSignsCount lt 2}">
			<c:set var="encVitalSigns" value="${chits:observations(enc, VisitConcepts.VITAL_SIGNS)}" />
			<c:if test="${not empty encVitalSigns}">
				<c:if test="${vitalSignsCount gt 0}">
					<tr><td><hr/></td></tr>
				</c:if>
				<c:set var="vitalSignsCount" value="${vitalSignsCount+1}" />
				<c:forEach items="${encVitalSigns}" var="vitalSigns">
					<tr><td><chits:vitalSigns vitalSigns="${vitalSigns}" /></td></tr>
				</c:forEach>
			</c:if>
		</c:if>
		</c:forEach>

		<c:if test="${vitalSignsCount eq 0 and empty form.encounters}">
		<tr><td>no entered data</td></tr>
		</c:if>
	</table>
	<input type="button" value="Archive" onclick='javascript: loadVitalSignsHistory("Vital Signs Archive: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId})' />
	<input type="button" value="Create new entry" onclick='javascript: loadVitalSignsUpdateForm("Enter Patient Vital Signs Data: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId})' />

	<br/><br/>
	<c:choose><c:when test="${not empty form.patientQueue.encounter.encounterDatetime}">
		<table class="notes-section"><tr><td class="label" valign="middle">
			<h4 style="margin: 0.1em;">Notes No.
			<fmt:formatNumber pattern="00000" value="${form.patientQueue.notesNumber}" /> - 
			<fmt:formatDate pattern="MM/dd/yyyy" value="${form.patientQueue.encounter.encounterDatetime}" />,
			<fmt:formatDate pattern="hh:mm a" value="${form.patientQueue.encounter.encounterDatetime}" />
			</h4>
			<form action="setFollowUpConsult.form?patient=${form.patient.patientId}" method="post" id="followUpConsultForm" style="margin: 0px" onsubmit="pleaseWaitDialog()">
				<input type="checkbox" id="followUpConsult" name="followUpConsult" value="true" onclick="$j(this.form).submit()" onchange="$j(this.form).submit()" <c:if test="${fn:endsWith(form.patientQueue.encounter.encounterType.name,'RETURN')}">checked="checked"</c:if>>
				<label for="followUpConsult" title="Tick to set follow-up status of visit">Follow-up Consult</label>
			</form>
		</td><td class="button">
			<input type="button" value="Update" onclick='javascript: loadSetConsultTimestampForm(${form.patient.patientId})' />
		</td></tr></table>
	</c:when><c:when test="${not empty form.patientQueue}">
		<h4 style="margin: 0.1em;">Notes No. [Consult not started]</h4>
	</c:when><c:otherwise>
		<h4 style="margin: 0.1em;">Notes No. [Patient not in queue]</h4>
	</c:otherwise></c:choose>

	<%-- Complaints Notes --%>
	<hr/>
	<table class="notes-section"><tr><td class="label">Complaints:</td><td class="button"><input type="button" value="Update" onclick='javascript: loadUpdateNotesForm("Complaints: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, "COMPLAINT_NOTES")' /></td></tr></table>
	<c:set var="complaints" value="${chits:observations(form.patientQueue.encounter, VisitConcepts.COMPLAINT)}" />
	<div class="indent">
		<c:choose><c:when test="${not empty complaints}">
		<c:forEach var="complaint" items="${complaints}" varStatus="i"><c:set var="icd10" value="${chits:mapping(complaint.valueCoded, 'ICD10')}" />
		<c:if test="${i.index ne 0}">, </c:if>${complaint.valueCoded.name.name}<c:if test="${not empty icd10}"> (${icd10})</c:if>
		</c:forEach>
		</c:when><c:otherwise>
		no entry for this visit
		</c:otherwise></c:choose>
	</div>
	
	<table class="notes-section"><tr><td class="label">Complaint Notes:</td><td class="button"></td></tr></table>
	<c:set var="notes" value="${chits:observation(form.patientQueue.encounter, VisitNotesConceptSets.COMPLAINT_NOTES)}" />
	<textarea class="notes" rows="2" readonly="readonly"><c:choose><c:when test="${not empty notes and notes.valueText ne ''}">${notes.valueText}</c:when><c:otherwise>no entry for this visit</c:otherwise></c:choose></textarea>

	<%-- History Notes --%>
	<hr/>
	<table class="notes-section"><tr><td class="label">History Notes:</td><td class="button"><input type="button" value="Update" onclick='javascript: loadUpdateNotesForm("History: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, "HISTORY_NOTES")' /></td></tr></table>

	<c:set var="notes" value="${chits:observation(form.patientQueue.encounter, VisitNotesConceptSets.HISTORY_NOTES)}" />
	<textarea class="notes" rows="5" readonly="readonly"><c:choose><c:when test="${not empty notes and notes.valueText ne ''}">${notes.valueText}</c:when><c:otherwise>no entry for this visit</c:otherwise></c:choose></textarea>
	
	<%-- Physical exam Notes --%>
	<hr/>
	<table class="notes-section"><tr><td class="label">Physical Exam:</td><td class="button"><input type="button" value="Update" onclick='javascript: loadUpdateNotesForm("Physical Exam: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, "PHYSICAL_EXAM_NOTES")' /></td></tr></table>

	<c:set var="notes" value="${chits:observation(form.patientQueue.encounter, VisitNotesConceptSets.PHYSICAL_EXAM_NOTES)}" />
	<textarea class="notes" rows="5" readonly="readonly"><c:choose><c:when test="${not empty notes and notes.valueText ne ''}">${notes.valueText}</c:when><c:otherwise>no entry for this visit</c:otherwise></c:choose></textarea>	
	
	<%-- Diagnosis Notes --%>
	<hr/>
	<table class="notes-section"><tr><td class="label">Diagnosis &amp; Treatment Plan:</td><td class="button"><input type="button" value="Update" onclick='javascript: loadUpdateNotesForm("Diagnosis: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, "DIAGNOSIS_NOTES")' /></td></tr></table>
	<c:set var="diagnoses" value="${chits:observations(form.patientQueue.encounter, VisitConcepts.DIAGNOSIS)}" />
	<div class="indent">
		<c:choose><c:when test="${not empty diagnoses}">
		<c:forEach var="diagnosis" items="${diagnoses}" varStatus="i"><c:set var="icd10" value="${chits:mapping(diagnosis.valueCoded, 'ICD10')}" />
		<c:if test="${i.index ne 0}">, </c:if>${diagnosis.valueCoded.name.name}<c:if test="${not empty icd10}"> (${icd10})</c:if> 
		</c:forEach>
		</c:when><c:otherwise>
		no entry for this visit
		</c:otherwise></c:choose>
	</div>

	<br/>Additional Information:<br/>
	<c:set var="notes" value="${chits:observation(form.patientQueue.encounter, VisitNotesConceptSets.DIAGNOSIS_NOTES)}" />
	<textarea class="notes" rows="2" readonly="readonly"><c:choose><c:when test="${not empty notes and notes.valueText ne ''}">${notes.valueText}</c:when><c:otherwise>no entry for this visit</c:otherwise></c:choose></textarea>

	Treatment Plan:<br/>
	<c:set var="notes" value="${chits:observation(form.patientQueue.encounter, VisitNotesConceptSets.TREATMENT_NOTES)}" />
	<textarea class="notes" rows="5" readonly="readonly"><c:choose><c:when test="${not empty notes and notes.valueText ne ''}">${notes.valueText}</c:when><c:otherwise>no entry for this visit</c:otherwise></c:choose></textarea>

	Rx<br/>
	<table class="form" style="width: 95%;">
		<thead><tr><th>PRESCRIPTION</th><th>#</th><th>INSTRUCTIONS</th></tr></thead>
		<c:choose><c:when test="${not empty drugOrders}"><tbody>
		<c:forEach var="drugOrder" items="${drugOrders}">
			<tr>
			<td>${drugOrder.drug.concept.name.name}</td>
			<td>${drugOrder.quantity}</td>
			<td>${drugOrder.instructions}</td>
			</tr>
		</c:forEach>
		</tbody></c:when><c:otherwise>
			<tfoot id="no-drugs"><tr><td colspan="3"><span>no entry for this visit</span></td></tr></tfoot>
		</c:otherwise></c:choose>
	</table>
</td></tr>
</table>

<c:set var="tabHasErrors" value="${false}" scope="request" />