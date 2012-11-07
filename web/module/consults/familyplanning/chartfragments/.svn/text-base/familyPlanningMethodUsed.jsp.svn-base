<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="records" value="${form.fpProgramObs.familyPlanningMethods}" />
<c:choose><c:when test="${not empty records}">
NOTE: Click on date link to view transaction record
<table class="full-width form registration">
<thead><tr><th>Method</th><th>Type<br/>of<br/>Client</th><th>Date<br/>Enrolled</th><th>Date<br/>Dropout</th><th>Dropout<br/>Reason</th></tr></thead>
<tbody>
<c:forEach var="record" items="${records}">
	<tr><c:set var="dropoutDate" value="${chits:observation(record.obs, FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT).valueDatetime}" />
		<td><chits_tag:obsValue obs="${record.obs}" shortName="${true}" /></td>
		<td><chits_tag:obsValue obs="${chits:observation(record.obs, FPFamilyPlanningMethodConcepts.CLIENT_TYPE)}" shortName="${true}" /></td>
		<td><a href="#" onclick='loadAjaxForm("viewFamilyPlanningMethod.form?patientId=${form.patient.patientId}&familyPlanningMethodObsId=${record.obs.obsId}&viewEnrollment=1", "VIEW FAMILY PLANNING ENROLLMENT DETAILS", ${form.patient.patientId}, 370); return false;'><fmt:formatDate pattern="MM/dd/yy" value="${chits:observation(record.obs, FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT).valueDatetime}" /></a></td>
		<td><c:if test="${not empty dropoutDate}"><a href="#" onclick='loadAjaxForm("viewFamilyPlanningMethod.form?patientId=${form.patient.patientId}&familyPlanningMethodObsId=${record.obs.obsId}&viewDropout=1", "VIEW FAMILY PLANNING DROPOUT DETAILS", ${form.patient.patientId}, 370); return false;'><fmt:formatDate pattern="MM/dd/yy" value="${dropoutDate}" /></a></c:if></td>
		<td><chits_tag:obsValue obs="${chits:observation(record.obs, FPFamilyPlanningMethodConcepts.DROPOUT_REASON)}" /></td>
	</tr>
</c:forEach>
</tbody>
</table>
</c:when><c:otherwise>
<div class="indent">
	<em>No family planning methods</em>
</div>
</c:otherwise></c:choose>