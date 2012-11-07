<%@ page buffer="128kb"
%><%@ page import="org.openmrs.Obs"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<script>
function verifyAndConfirmUnenrollment() {
	$j(".confirmUnenrollment").dialog({
		resizable:false,width:300,modal:true,
		title:'Confirm un-enrollment',
		buttons:{
			"Un-enroll":function(){$j("form.unenroll").submit()},
			"Cancel":function(){$j(this).dialog("close")}
		}
	})
}
</script>

<%-- FAMILY PLANNING METHOD USED --%>
<fieldset><legend><span>FAMILY PLANNING METHOD USED</span></legend>
<jsp:include page="chartfragments/familyPlanningMethodUsed.jsp" />
<table class="full-width borderless">
<tbody><tr><td style="text-align: right"><input id="update-familiy-planning-method-used" type="button" value="Update" onclick='loadAjaxForm("updateFamilyPlanningMethod.form?patientId=${form.patient.patientId}", "UPDATE FAMILY PLANNING METHOD USED", ${form.patient.patientId}, 570);' /></td></tr>
</tbody></table>
</fieldset>

<%-- SERVICE DELIVERY RECORD --%>
<fieldset><legend><span>SERVICE DELIVERY RECORD</span></legend>
<jsp:include page="chartfragments/serviceDeliveryRecords.jsp" />
<table class="full-width borderless">
<tbody><tr><td style="text-align: right"><input id="update-service-delivery-record" type="button" value="Update" onclick='loadAjaxForm("addServiceDeliveryRecord.form?patientId=${form.patient.patientId}", "ADD SERVICE DELIVERY RECORD", ${form.patient.patientId}, 570);' <c:if test="${form.fpProgramObs.latestFamilyPlanningMethod.droppedOut}">disabled='disabled'</c:if> /></td></tr>
</tbody></table>
</fieldset>

<%-- FP Chart update history --%>
<div class="indent">
<br/>
<h3>FP CHART UPDATE HISTORY</h3>

<div class="indent">
<c:set var="records" value="${chits:reverse(chits:observations(form.fpProgramObs.obs, FPFamilyInformationConcepts.FAMILY_INFORMATION))}" />
<c:if test="${fn:length(records) gt 1}">
<fieldset><legend><span>Family Information</span></legend>
	<ul><c:forEach var="record" items="${records}" varStatus="i"><c:if test="${i.index gt 0}">
		<li><a href="#" onclick='loadAjaxForm("viewFamilyPlanningFamilyInformation.form?patientId=${form.patient.patientId}&familyInformationObsId=${record.obsId}", "VIEW FAMILY INFORMATION", ${form.patient.patientId}, 360); return false;'><fmt:formatDate pattern="d MMMM, yyyy" value="${record.obsDatetime}" /><c:if test="${i.count eq fn:length(records)}"> (initial)</c:if></a></li>
	</c:if></c:forEach></ul>
</fieldset>
</c:if>

<c:set var="records" value="${chits:reverse(chits:observations(form.fpProgramObs.obs, FPMedicalHistoryConcepts.MEDICAL_HISTORY))}" />
<c:if test="${fn:length(records) gt 1}">
<fieldset><legend><span>Medical History</span></legend>
	<ul><c:forEach var="record" items="${records}" varStatus="i"><c:if test="${i.index gt 0}">
		<li><a href="#" onclick='loadAjaxForm("viewFamilyPlanningMedicalHistory.form?patientId=${form.patient.patientId}&medicalHistoryObsId=${record.obsId}", "VIEW MEDICAL HISTORY", ${form.patient.patientId}, 360); return false;'><fmt:formatDate pattern="d MMMM, yyyy" value="${record.obsDatetime}" /><c:if test="${i.count eq fn:length(records)}"> (initial)</c:if></a></li>
	</c:if></c:forEach></ul>
</fieldset>
</c:if>

<c:set var="records" value="${chits:reverse(chits:observations(form.fpProgramObs.obs, FPRiskFactorsConcepts.RISK_FACTORS))}" />
<c:if test="${fn:length(records) gt 1}">
<fieldset><legend><span>Risk Factors</span></legend>
	<ul><c:forEach var="record" items="${records}" varStatus="i"><c:if test="${i.index gt 0}">
		<li><a href="#" onclick='loadAjaxForm("viewFamilyPlanningRiskFactors.form?patientId=${form.patient.patientId}&riskFactorsObsId=${record.obsId}", "VIEW RISK FACTORS", ${form.patient.patientId}, 360); return false;'><fmt:formatDate pattern="d MMMM, yyyy" value="${record.obsDatetime}" /><c:if test="${i.count eq fn:length(records)}"> (initial)</c:if></a></li>
	</c:if></c:forEach></ul>
</fieldset>
</c:if>

<c:set var="records" value="${chits:reverse(chits:observations(form.fpProgramObs.obs, FPObstetricHistoryConcepts.OBSTETRIC_HISTORY))}" />
<c:if test="${fn:length(records) gt 1}">
<fieldset><legend><span>Obstetrical History</span></legend>
	<ul><c:forEach var="record" items="${records}" varStatus="i"><c:if test="${i.index gt 0}">
		<li><a href="#" onclick='loadAjaxForm("viewFamilyPlanningObstetricHistory.form?patientId=${form.patient.patientId}&obstetricHistoryObsId=${record.obsId}", "VIEW OBSTETRIC HISTORY", ${form.patient.patientId}, 360); return false;'><fmt:formatDate pattern="d MMMM, yyyy" value="${record.obsDatetime}" /><c:if test="${i.count eq fn:length(records)}"> (initial)</c:if></a></li>
	</c:if></c:forEach></ul>
</fieldset>
</c:if>

<c:set var="records" value="${chits:reverse(chits:observations(form.fpProgramObs.obs, FPPhysicalExaminationConcepts.PHYSICAL_EXAMINATION))}" />
<c:if test="${fn:length(records) gt 1}">
<fieldset><legend><span>Physical Examination</span></legend>
	<ul><c:forEach var="record" items="${records}" varStatus="i"><c:if test="${i.index gt 0}">
		<li><a href="#" onclick='loadAjaxForm("viewFamilyPlanningPhysicalExamination.form?patientId=${form.patient.patientId}&physicalExamObsId=${record.obsId}", "VIEW PHYSICAL EXAMINATION", ${form.patient.patientId}, 360); return false;'><fmt:formatDate pattern="d MMMM, yyyy" value="${record.obsDatetime}" /><c:if test="${i.count eq fn:length(records)}"> (initial)</c:if></a></li>
	</c:if></c:forEach></ul>
</fieldset>
</c:if>

<c:set var="records" value="${chits:reverse(chits:observations(form.fpProgramObs.obs, FPPelvicExaminationConcepts.PELVIC_EXAMINATION))}" />
<c:if test="${fn:length(records) gt 1}">
<fieldset><legend><span>Pelvic Examination</span></legend>
	<ul><c:forEach var="record" items="${records}" varStatus="i"><c:if test="${i.index gt 0}">
		<li><a href="#" onclick='loadAjaxForm("viewFamilyPlanningPelvicExamination.form?patientId=${form.patient.patientId}&pelvicExamObsId=${record.obsId}", "VIEW PELVIC EXAMINATION", ${form.patient.patientId}, 360); return false;'><fmt:formatDate pattern="d MMMM, yyyy" value="${record.obsDatetime}" /><c:if test="${i.count eq fn:length(records)}"> (initial)</c:if></a></li>
	</c:if></c:forEach></ul>
</fieldset>
</c:if>
</div>

</div>

<c:if test="${not form.fpProgramObs.readOnly and not form.programConcluded}">
<c:choose><c:when test="${'F' eq form.patient.gender and form.patient.age ge 50}">
	<div class="female-patient-over-50" style="display:none">
		<fieldset><legend><span>Patient has reached the age of 50</span></legend>
			<div class="alert" style="text-align: justify">
				${form.patient.personName} has reached the age of 50, and is not
				entitled to receive services under the Family Planning Program.
			</div>
			
			<br/>
			<div style="text-align: justify">
				You may update information on the patient&apos;s chart, but only
				for transactions made before her birthday, or YOU MAY OPT TO
				UN-ENROLL THE PATIENT FROM THE FAMILY PLANNING PROGRAM.
			</div> 
		</fieldset>
		<br/>
		<div style="text-align: center; white-space: nowrap;">
			<input type="button" id="editChartButton" value='EDIT CHART' onclick='$j("div.female-patient-over-50").dialog("close")' />
			<input type="button" id="unEnrollButton" value='UN-ENROLL' onclick="verifyAndConfirmUnenrollment()" />
			<input type="button" id="cancelButton" value='CANCEL' onclick='document.location.href = "viewPatient.form?patientId=${form.patient.patientId}"' />
		</div>
	</div>

	<div style="display:none" class="confirmUnenrollment">
		<h5>Un-enroll from the family planning program?</h5>
		<form class="unenroll" action="unenrollFromFamilyPlanningProgram.form" method="POST" onsubmit="pleaseWaitDialog()">
			<input type="hidden" name="patientId" value="${form.patient.patientId}" />
		</form>
	</div>

	<script>
	$j(document).ready(function() {
		$j("div.female-patient-over-50").dialog({title:'Un-Enroll From Program?',width:300,modal:true}).dialog("open")
	})
	</script>
</c:when><c:when test="${form.fpProgramObs.warnAboutMaternalCareEnrollment}">
<%-- don't re-display warning for AJAX updates --%>
<c:if test="${param.section ne 'program-details'}">
	<div class="enrolled-in-mc" style="display:none">
		<fieldset><legend><span>Patient is enrolled in maternal care</span></legend>
			<div class="alert" style="text-align: justify">
				${form.patient.personName} is enrolled in the Maternal Care Program,
				and is therefore not entitled to receive services under the Family
				Planning Program at this time.
			</div>
			
			<br/>
			<div style="text-align: justify">
				You may update information on the patient&apos;s chart, but only
				for transactions made before her enrollment in the Maternal
				Care Program.
			</div> 
		</fieldset>
		<br/>
		<div style="text-align: center; white-space: nowrap;">
			<input type="button" id="editChartButton" value='EDIT CHART' onclick='$j("div.enrolled-in-mc").dialog("close")' />
			<input type="button" id="cancelButton" value='CANCEL' onclick='document.location.href = "viewPatient.form?patientId=${form.patient.patientId}"' />
		</div>
	</div>

	<script>
	$j(document).ready(function() {
		$j("div.enrolled-in-mc").dialog({title:'Enrolled in Maternal Care',width:300,modal:true}).dialog("open")
	})
	</script>
</c:if>
</c:when></c:choose>
</c:if>