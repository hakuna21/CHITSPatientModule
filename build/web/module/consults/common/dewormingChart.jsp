<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.obs.DatetimeObsMemberComparator"
%><%@ page import="org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCServiceRecordConcepts"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="dewormingRecords" value="${chits:filterByCodedValue(chits:observations(form.patient, MCServiceRecordConcepts.SERVICE_TYPE), MCServiceTypes.DEWORMING)}" /><%

	// sort by ascending 'visit date'
	final List details = (List) pageContext.findAttribute("dewormingRecords");
	Collections.sort(details, new DatetimeObsMemberComparator(MCServiceRecordConcepts.DATE_ADMINISTERED)); 

%><h3>Deworming Services</h3>
<table class="full-width form registration">
<thead><tr><th>DATE GIVEN</th><th>SERVICE SOURCE</th><th>REMARKS</th></tr></thead>
<c:choose><c:when test="${not empty dewormingRecords}"><c:forEach var="record" items="${dewormingRecords}">
<tbody>
	<tr><td><a href="#" onclick='loadAjaxForm("viewDewormingServiceRecord.form?patientId=${form.patient.patientId}&dewormingServiceRecordObsId=${record.obsId}", "VIEW DEWORMING SERVICE RECORD", ${form.patient.patientId}, 370); return false;'><fmt:formatDate pattern="MM/dd/yyyy" value="${chits:observation(record, MCServiceRecordConcepts.DATE_ADMINISTERED).valueDatetime}" /></a></td><td><chits_tag:obsValue obs="${chits:observation(record, DewormingConcepts.SERVICE_SOURCE)}" /></td><td><chits_tag:obsValue obs="${chits:observation(record, DewormingConcepts.REMARKS)}" noData="none" /></td></tr>
</tbody>
</c:forEach></c:when><c:otherwise>
<tfoot>
	<tr>
		<td colspan="4" style="text-align: center"><em>(no entered data)</em></td>
	</tr>
</tfoot>
</c:otherwise></c:choose>
</table>