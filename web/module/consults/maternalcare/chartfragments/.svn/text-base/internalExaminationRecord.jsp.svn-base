<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="ieRecords" value="${form.mcProgramObs.internalExaminationRecords}" />
<c:set var="postpartumIERecords" value="${chits:observations(form.mcProgramObs.obs, MCPostpartumIERecordConcepts.POSTDELIVERY_IE)}" />

<c:if test="${not empty ieRecords}">
<table class="full-width form registration">
<thead><tr><th>DATE</th><th>PERIOD</th><th>Performed by</th></tr></thead>
<tbody>
<c:forEach var="ieRecord" items="${ieRecords}" varStatus="i">
	<tr><c:set var="lastIERecord" value="${ieRecord}" scope="request" /><c:set var="visitDate" value="${ieRecord.visitDate}" />
		<td><a href="#" onclick='loadAjaxForm("viewInternalExaminationRecord.form?patientId=${form.patient.patientId}&internalExaminationRecordObsId=${ieRecord.obs.obsId}", "VIEW INTERNAL EXAMINATION RECORD", ${form.patient.patientId}, 360); return false;'><fmt:formatDate pattern="MM/dd/yyyy" value="${visitDate}" /></a></td>
		<td><chits_tag:maternalCareStage mcProgramObs="${form.mcProgramObs}" on="${visitDate}" /></td>
		<td><c:choose><c:when test="${form.mcProgramObs.readOnly}"
			>${ieRecord.obs.creator.person.personName}</c:when><c:otherwise
			><%-- <a href="#" onclick='loadAjaxForm("editInternalExaminationRecord.form?patientId=${form.patient.patientId}&internalExaminationRecordObsId=${ieRecord.obs.obsId}", "EDIT INTERNAL EXAMINATION RECORD", ${form.patient.patientId}, 360); return false;'> --%>${ieRecord.obs.creator.person.personName}<%-- </a> --%></c:otherwise></c:choose
		></td>
	</tr>
</c:forEach>
</tbody>
</table>
</c:if>

<c:if test="${not empty postpartumIERecords}">
<fieldset><legend><span>POSTPARTUM</span></legend>
<jsp:include page="ajaxPostpartumInternalExaminations.jsp" />
</fieldset>
</c:if>

<c:if test="${empty ieRecords and empty postpartumIERecords}">
<div class="indent">
	<em>No internal examination records recorded</em>
</div>
</c:if>