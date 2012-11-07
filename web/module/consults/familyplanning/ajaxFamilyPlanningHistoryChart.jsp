<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>
<link href="${pageContext.request.contextPath}/moduleResources/chits/css/family-planning-section.css?v=${deploymentTimestamp}" type="text/css" rel="stylesheet" />
<table class="full-width">
<tr><td valign="top" style="width: 50%">

	<%-- FAMILY PLANNING METHOD USED --%>
	<fieldset><legend><span>FAMILY PLANNING METHOD USED</span></legend>
	<jsp:include page="chartfragments/familyPlanningMethodUsed.jsp" />
	</fieldset>
	
	<%-- SERVICE DELIVERY RECORD --%>
	<fieldset><legend><span>SERVICE DELIVERY RECORD</span></legend>
	<jsp:include page="chartfragments/serviceDeliveryRecords.jsp" />
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
	
</td><td valign="top">

	<%-- Family Planning Program Chart --%>
	<fieldset><legend><span>FAMILY PLANNING PROGRAM CHART</span></legend>
	<jsp:include page="chartfragments/registrationInformation.jsp" />
	</fieldset>

	<%-- Family Information --%>
	<fieldset><legend><span>Family Information</span></legend>
	<jsp:include page="chartfragments/familyInformation.jsp" />
	</fieldset>
	
	<%-- Medical history --%>
	<fieldset><legend><span>Medical History</span></legend>
	<jsp:include page="chartfragments/medicalHistory.jsp" />
	</fieldset>
	
	<%-- Risk Factors --%>
	<fieldset><legend><span>Risk Factors</span></legend>
	<jsp:include page="chartfragments/riskFactors.jsp" />
	</fieldset>
	
	<%-- Obstetric History --%>
	<fieldset><legend><span>Obstetric History</span></legend>
	<jsp:include page="chartfragments/obstetricHistory.jsp" />
	</fieldset>
	
	<%-- Physical Examination --%>
	<fieldset><legend><span>Physical Examination</span></legend>
	<jsp:include page="chartfragments/physicalExamination.jsp" />
	</fieldset>
	
	<%-- Pelvic Examination --%>
	<fieldset><legend><span>Pelvic Examination</span></legend>
	<jsp:include page="chartfragments/pelvicExamination.jsp" />
	</fieldset>

</td></tr>
</table>