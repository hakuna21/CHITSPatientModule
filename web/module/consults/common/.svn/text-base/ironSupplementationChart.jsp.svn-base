<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.obs.DatetimeObsMemberComparator"
%><%@ page import="org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCServiceRecordConcepts"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="ironSupplementationRecords" value="${chits:filterByCodedValue(chits:observations(form.patient, MCServiceRecordConcepts.SERVICE_TYPE), MCServiceTypes.FERROUS_SULFATE)}" /><%

	// sort by ascending 'visit date'
	final List details = (List) pageContext.findAttribute("ironSupplementationRecords");
	Collections.sort(details, new DatetimeObsMemberComparator(MCServiceRecordConcepts.DATE_ADMINISTERED)); 

%><h3>Iron Supplementation</h3>
<table class="full-width form registration">
<thead><tr><th>DATE GIVEN</th><th>SERVICE SOURCE</th><th>REMARKS</th></tr></thead>
<c:choose><c:when test="${not empty ironSupplementationRecords}"><c:forEach var="record" items="${ironSupplementationRecords}">
<tbody>
	<tr><td><a href="#" onclick='loadAjaxForm("viewIronSupplementationServiceRecord.form?patientId=${form.patient.patientId}&ironSupplementationServiceRecordObsId=${record.obsId}", "VIEW IRON SUPPLEMENTATION SERVICE RECORD", ${form.patient.patientId}, 470); return false;'><fmt:formatDate pattern="MM/dd/yyyy" value="${chits:observation(record, MCServiceRecordConcepts.DATE_ADMINISTERED).valueDatetime}" /></a></td><td><chits_tag:obsValue obs="${chits:observation(record, IronSupplementationConcepts.SERVICE_SOURCE)}" /></td><td><chits_tag:obsValue obs="${chits:observation(record, IronSupplementationConcepts.REMARKS)}" noData="none" /></td></tr>
</tbody>
</c:forEach></c:when><c:otherwise>
<tfoot>
	<tr>
		<td colspan="4" style="text-align: center"><em>(no entered data)</em></td>
	</tr>
</tfoot>
</c:otherwise></c:choose>
</table>
