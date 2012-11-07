<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="fpRecords" value="${form.fpProgramObs.familyPlanningMethods}" />
<c:choose><c:when test="${not empty fpRecords}">

<table class="full-width form registration">
<thead><tr><th>Date<br/>Recorded</th><th>Method</th><th>Type</th><th>Date<br/>Given</th><th>QTY</th><th>Remarks</th></tr></thead>
<c:forEach var="fpRecord" items="${fpRecords}"><c:set var="records" value="${fpRecord.serviceRecords}" />
<c:if test="${not empty records}"><tbody>
<c:forEach var="record" items="${records}">
	<tr>
		<td><fmt:formatDate pattern="MM/dd/yy" value="${record.obs.obsDatetime}" /></td>
		<td><chits_tag:obsValue obs="${record.obs.obsGroup}" shortName="${true}" /></td>
		<td><chits_tag:obsValue obs="${chits:observation(record.obs, FPServiceDeliveryRecordConcepts.SUPPLY_SOURCE)}" shortName="${true}" /></td>
		<td><fmt:formatDate pattern="MM/dd/yy" value="${chits:observation(record.obs, FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED).valueDatetime}" /></td>
		<td><chits_tag:obsValue obs="${chits:observation(record.obs, FPServiceDeliveryRecordConcepts.SUPPLY_QUANTITY)}" /></td>
		<td><chits_tag:obsValue obs="${chits:observation(record.obs, FPServiceDeliveryRecordConcepts.REMARKS)}" /></td>
	</tr>
</c:forEach>
</tbody></c:if>
</c:forEach>
</table>

</c:when><c:otherwise>
<div class="indent">
	<em>No service delivery records</em>
</div>
</c:otherwise></c:choose>