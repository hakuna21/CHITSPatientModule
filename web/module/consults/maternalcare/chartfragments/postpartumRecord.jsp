<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="deliveryReportObs" value="${chits:observation(form.mcProgramObs.obs, MCDeliveryReportConcepts.DELIVERY_REPORT)}" />
<c:set var="postPartumVisitRecords" value="${form.mcProgramObs.postPartumVisits}" />
<c:choose><c:when test="${not empty postPartumVisitRecords}">
<table class="full-width form registration">
<thead><tr><th>VISIT/<br/>No.</th><th>DATE</th><th>PERIOD</th><th>DONE BY</th></tr></thead>
<tbody>
<c:forEach var="visitRecord" items="${postPartumVisitRecords}" varStatus="i">
	<tr><c:set var="lastPostPartumVisit" value="${visitRecord}" scope="request" /><c:set var="visitDate" value="${visitRecord.visitDate}" />
		<td>${i.count}</td>
		<td><a href="#" onclick='loadAjaxForm("viewPostPartumVisitRecord.form?patientId=${form.patient.patientId}&postPartumVisitRecordObsId=${visitRecord.obs.obsId}", "VIEW POST-PARTUM CHECKUP RECORD", ${form.patient.patientId}, 600); return false;'><fmt:formatDate pattern="MM/dd/yyyy" value="${visitDate}" /></a></td>
		<td><chits_tag:maternalCareStage mcProgramObs="${form.mcProgramObs}" on="${visitDate}" /></td>
		<td><c:choose><c:when test="${form.mcProgramObs.readOnly}"
			>${visitRecord.obs.creator.person.personName}</c:when><c:otherwise
			><%-- <a href="#" onclick='loadAjaxForm("editPostPartumVisitRecord.form?patientId=${form.patient.patientId}&postPartumVisitRecordObsId=${visitRecord.obs.obsId}", "EDIT POST-PARTUM CHECKUP RECORD", ${form.patient.patientId}, 370); return false;'> --%>${visitRecord.obs.creator.person.personName}<%-- </a> --%></c:otherwise></c:choose
		></td>
	</tr>
</c:forEach>
</tbody>
</table>
</c:when><c:otherwise>
<div class="indent">
	<em>No post-partum visits recorded</em>
</div>
</c:otherwise></c:choose>
