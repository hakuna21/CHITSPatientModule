<%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCObstetricHistoryDetailsConcepts"
%><%@ page import="org.openmrs.module.chits.obs.DatetimeObsMemberComparator"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%--
	NOTE: We find details over the patient instead of the obstetric history parent obs since
		  we want to display all items for this patient, not just the ones registered in this
		  program
--%><c:set var="details" value="${chits:observations(form.patient, MCObstetricHistoryDetailsConcepts.OBSTETRIC_HISTORY_DETAILS)}"
/><%

	// sort by ascending 'year of pregnancy'
	final List details = (List) pageContext.findAttribute("details");
	Collections.sort(details, new DatetimeObsMemberComparator(MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY)); 

%><table class="form full-width">
	<thead><th>G</th><th>Year</th><th>Sex</th><th>Method</th><th>Outcome</th></thead>
	<tbody><c:choose><c:when test="${not empty details}"><c:forEach var="detail" items="${details}"
	><c:set var="outcomes" value="${chits:observations(detail, MCPregnancyOutcomeConcepts.PREGNANCY_OUTCOME)}" />
	<tr><c:set var="yearObs" value="${chits:observation(detail, MCObstetricHistoryDetailsConcepts.YEAR_OF_PREGNANCY)}" />
		<td rowspan="${fn:length(outcomes)}"><a onclick="editOBHistoryDetail(${detail.obsId}, ${form.patient.patientId}); return false" style="cursor:pointer"><fmt:formatNumber pattern="0" value="${chits:observation(detail, MCObstetricHistoryDetailsConcepts.GRAVIDA).valueNumeric}" /></a></td>
		<td rowspan="${fn:length(outcomes)}"><fmt:formatDate pattern="yyyy" value="${yearObs.valueDatetime}" /><c:if test="${not empty yearObs.valueNumeric}">/<fmt:formatNumber value="${yearObs.valueNumeric}" pattern="00" /></c:if></td><c:forEach var="outcome" items="${outcomes}" varStatus="i"
		><c:if test="${i.index gt 0}">
	<%= "</tr><tr>" %></c:if>
		<td><chits_tag:obsValue shortName="${true}" obs="${chits:observation(outcome, MCPregnancyOutcomeConcepts.SEX)}" /></td>
		<td><chits_tag:obsValue shortName="${true}" obs="${chits:observation(outcome, MCPregnancyOutcomeConcepts.METHOD)}" /></td>
		<td><c:choose><c:when test="${outcome.valueGroupId gt 0}"><a href="${pageContext.request.contextPath}/module/chits/patients/viewPatient.form?patientId=${outcome.valueGroupId}"><chits_tag:obsValue shortName="${true}" obs="${chits:observation(outcome, MCPregnancyOutcomeConcepts.OUTCOME)}" /></a></c:when><c:otherwise><chits_tag:obsValue shortName="${true}" obs="${chits:observation(outcome, MCPregnancyOutcomeConcepts.OUTCOME)}" /></c:otherwise></c:choose></td></c:forEach>
	</tr></c:forEach>
	</c:when><c:otherwise>
	<tr>
		<td colspan="5" style="text-align:center"><strong><em>none</em></strong></td>
	</tr>
	</c:otherwise></c:choose></tbody>
</table>
