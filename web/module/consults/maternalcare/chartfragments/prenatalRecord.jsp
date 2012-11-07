<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="prenatalVisitRecords" value="${form.mcProgramObs.prenatalVisits}" />
<c:choose><c:when test="${not empty prenatalVisitRecords}">
<table class="full-width form registration">
<thead><tr><th>VISIT/<br/>No.</th><th>DATE</th><th>PERIOD</th><th>DONE BY</th></tr></thead>
<tbody>
<c:forEach var="visitRecord" items="${prenatalVisitRecords}" varStatus="i">
	<tr><c:set var="lastPrenatalVisit" value="${visitRecord}" scope="request" /><c:set var="visitDate" value="${visitRecord.visitDate}" />
		<td>${i.count}</td>
		<td><a href="#" onclick='loadAjaxForm("viewPrenatalVisitRecord.form?patientId=${form.patient.patientId}&prenatalVisitRecordObsId=${visitRecord.obs.obsId}", "VIEW PRENATAL CHECKUP RECORD", ${form.patient.patientId}, 370); return false;'><fmt:formatDate pattern="MM/dd/yyyy" value="${visitDate}" /></a></td>
		<td><chits_tag:maternalCareStage mcProgramObs="${form.mcProgramObs}" on="${visitDate}" /></td>
		<td><c:choose><c:when test="${form.mcProgramObs.readOnly}"
			>${visitRecord.obs.creator.person.personName}</c:when><c:otherwise
			><%-- <a href="#" onclick='loadAjaxForm("editPrenatalVisitRecord.form?patientId=${form.patient.patientId}&prenatalVisitRecordObsId=${visitRecord.obs.obsId}", "EDIT PRENATAL CHECKUP RECORD", ${form.patient.patientId}, 570); return false;'> --%>${visitRecord.obs.creator.person.personName}<%-- </a> --%></c:otherwise></c:choose
		></td>
	</tr>
</c:forEach>
</tbody>
</table>
</c:when><c:otherwise>
<div class="indent">
	<em>No prenatal visits recorded</em>
</div>
</c:otherwise></c:choose>