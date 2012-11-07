<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.obs.DatetimeObsMemberComparator"
%><%@ page import="org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCServiceRecordConcepts"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="vitaminARecords" value="${chits:filterByCodedValue(chits:observations(form.patient, MCServiceRecordConcepts.SERVICE_TYPE), MCServiceTypes.VITAMIN_A_SUPPLEMENTATION)}" /><%

	// sort by ascending 'visit date'
	final List details = (List) pageContext.findAttribute("vitaminARecords");
	Collections.sort(details, new DatetimeObsMemberComparator(MCServiceRecordConcepts.DATE_ADMINISTERED)); 

%><h3>Vitamin A Supplementation</h3>
<table class="full-width form registration">
<thead><tr><th>DATE GIVEN</th><th>SERVICE SOURCE</th><th>REMARKS</th></tr></thead>
<c:choose><c:when test="${not empty vitaminARecords}"><c:forEach var="record" items="${vitaminARecords}">
<tbody>
	<tr><td><a href="#" onclick='loadAjaxForm("viewVitaminAServiceRecord.form?patientId=${form.patient.patientId}&vitaminAServiceRecordObsId=${record.obsId}", "VIEW VITAMIN A SERVICE RECORD", ${form.patient.patientId}, 370); return false;'><fmt:formatDate pattern="MM/dd/yyyy" value="${chits:observation(record, MCServiceRecordConcepts.DATE_ADMINISTERED).valueDatetime}" /></a></td><td><chits_tag:obsValue obs="${chits:observation(record, VitaminAConcepts.SERVICE_SOURCE)}" /></td><td><chits_tag:obsValue obs="${chits:observation(record, VitaminAConcepts.REMARKS)}" noData="none" /></td></tr>
</tbody>
</c:forEach></c:when><c:otherwise>
<tfoot>
	<tr>
		<td colspan="4" style="text-align: center"><em>(no entered data)</em></td>
	</tr>
</tfoot>
</c:otherwise></c:choose>
</table>