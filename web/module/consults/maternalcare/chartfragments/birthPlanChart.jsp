<%@ page buffer="128kb"
%><%@ page import="java.util.Date"
%><%@ page import="org.openmrs.module.chits.DateUtil"
%><%@ page import="org.openmrs.module.chits.MaternalCareConsultEntryForm"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="birthPlanChartObs" value="${chits:observation(form.mcProgramObs.obs, MCBirthPlanConcepts.BIRTH_PLAN)}" />
<div class="indent">
<c:choose><c:when test="${form.mcProgramObs.readOnly or form.mcProgramObs.birthPlanChartReadOnly}">
	<a href="#" style="color: #44A; font-weight: bold;" onclick='loadAjaxForm("viewBirthPlanChart.form?patientId=${form.patient.patientId}&maternalCareProgramObsId=${form.mcProgramObs.obs.obsId}", "VIEW BIRTH PLAN CHART", ${form.patient.patientId}, 880); return false; '>
		BIRTH PLAN CHART
		(<c:choose><c:when test="${not empty birthPlanChartObs.dateCreated}"><fmt:formatDate value="${birthPlanChartObs.dateCreated}" pattern="MMM d, yyyy" /></c:when><c:otherwise><em>not yet started</em></c:otherwise></c:choose>)
	</a>
</c:when><c:otherwise>
<%
	final MaternalCareConsultEntryForm form = (MaternalCareConsultEntryForm) pageContext.findAttribute("form"); 
	final Date today = DateUtil.stripTime(new Date());
	final boolean chartOverdue = today.after(form.getMcProgramObs().getEndOfFirstTrimester());
	request.setAttribute("chartOverdue", Boolean.valueOf(chartOverdue));
%>
	<a href="#" style="color: #44A; font-weight: bold;" onclick='loadAjaxForm("editBirthPlanChart.form?patientId=${form.patient.patientId}", "EDIT BIRTH PLAN CHART", ${form.patient.patientId}, 880); return false; '>
		BIRTH PLAN CHART
		(<c:choose><c:when test="${not empty birthPlanChartObs.dateCreated}"><fmt:formatDate value="${birthPlanChartObs.dateCreated}" pattern="MMM d, yyyy" /></c:when><c:otherwise><em<c:if test="${chartOverdue}"> class="alert"</c:if>>not yet started</em></c:otherwise></c:choose>)
	</a>
</c:otherwise></c:choose>
</div>