<%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostpartumIERecordConcepts"
%><%@ page import="org.openmrs.module.chits.obs.DatetimeObsMemberComparator"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><c:set var="details" value="${chits:observations(form.mcProgramObs.obs, MCPostpartumIERecordConcepts.POSTDELIVERY_IE)}"
/><%

	// sort by ascending 'visit date'
	final List details = (List) pageContext.findAttribute("details");
	Collections.sort(details, new DatetimeObsMemberComparator(MCPostpartumIERecordConcepts.VISIT_DATE)); 

%><table class="form full-width">
	<thead><th>Date</th><th>Type</th><th>Action</th></thead>
	<tbody><c:choose><c:when test="${not empty details}"><c:forEach var="detail" items="${details}">
	<tr>
		<td><fmt:formatDate pattern="MM/dd/yyyy" value="${chits:observation(detail, MCPostpartumIERecordConcepts.VISIT_DATE).valueDatetime}" /></td>
		<td><c:choose><c:when test="${detail.valueCoded eq chits:trueConcept()}">Discharge IE</c:when><c:otherwise>Routine</c:otherwise></c:choose></td>
		<td><a href="#" onclick='loadAjaxForm("viewPostpartumInternalExaminationRecord.form?patientId=${form.patient.patientId}&postpartumInternalExaminationRecordObsId=${detail.obsId}", "VIEW INTERNAL EXAMINATION RECORD", ${form.patient.patientId}, 400, "subGeneralForm"); return false;'>view</a></td>
	</tr></c:forEach>
	</c:when><c:otherwise>
	<tr>
		<td colspan="3" style="text-align:center"><strong><em>none</em></strong></td>
	</tr>
	</c:otherwise></c:choose></tbody>
</table>