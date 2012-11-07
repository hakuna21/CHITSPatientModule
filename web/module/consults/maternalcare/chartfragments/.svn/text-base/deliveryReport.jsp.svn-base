<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="deliveryReportObs" value="${chits:observation(form.mcProgramObs.obs, MCDeliveryReportConcepts.DELIVERY_REPORT)}" />
<div class="indent">
<c:choose><c:when test="${form.mcProgramObs.readOnly}">
	<a href="#" style="color: #44A; font-weight: bold;" onclick='loadAjaxForm("viewDeliveryReport.form?patientId=${form.patient.patientId}&maternalCareProgramObsId=${form.mcProgramObs.obs.obsId}", "VIEW DELIVERY REPORT", ${form.patient.patientId}, 900); return false; '>
		DELIVERY REPORT
		(<c:choose><c:when test="${not empty deliveryReportObs.dateCreated}"><fmt:formatDate value="${deliveryReportObs.dateCreated}" pattern="MMM d, yyyy" /></c:when><c:otherwise><em>not yet filled</em></c:otherwise></c:choose>)
	</a>
</c:when><c:otherwise>
	<a href="#" style="color: #44A; font-weight: bold;" onclick='loadAjaxForm("editDeliveryReport.form?patientId=${form.patient.patientId}", "EDIT DELIVERY REPORT", ${form.patient.patientId}, 900); return false; '>
		DELIVERY REPORT
		(<c:choose><c:when test="${not empty deliveryReportObs.dateCreated}"><fmt:formatDate value="${deliveryReportObs.dateCreated}" pattern="MMM d, yyyy" /></c:when><c:otherwise><em>not yet filled</em></c:otherwise></c:choose>)
	</a>
</c:otherwise></c:choose>
</div>