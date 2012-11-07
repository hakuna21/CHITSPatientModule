<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="record" value="${form.fpProgramObs.obstetricHistory.obs}" />
<c:choose><c:when test="${record.obsId gt 0}">
<table class="form full-width chart" id="obstetric-history-chart">
	<tr><td>Last update: <fmt:formatDate pattern="d MMMM yyyy" value="${record.obsDatetime}" /> by ${record.creator.person.personName}</td></tr>
	<tr><td>Obstetric Score: <chits_tag:obstetricScore obsGroup="${record}" full="${true}" /></td></tr>
	<tr><td>date of last delivery: <fmt:formatDate pattern="d MMMM, yyyy" value="${chits:observation(record, FPObstetricHistoryConcepts.DATE_OF_LAST_DELIVERY).valueDatetime}" /></td></tr>
	<tr><td>type of last delivery: <chits_tag:obsValue obs="${chits:observation(record, FPObstetricHistoryConcepts.TYPE_OF_LAST_DELIVERY)}" /></td></tr>
	<tr><td>previous menstrual period: <fmt:formatDate pattern="d MMMM, yyyy" value="${chits:observation(record, FPObstetricHistoryConcepts.PREVIOUS_MENSTRUAL_PERIOD).valueDatetime}" /></td></tr>
	<tr><td>last menstrual period: <fmt:formatDate pattern="d MMMM, yyyy" value="${chits:observation(record, FPObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD).valueDatetime}" /></td></tr>
	<tr><td>duration of bleeding (days): <chits_tag:obsValue obs="${chits:observation(record, FPObstetricHistoryConcepts.DURATION_OF_BLEEDING)}" /></td></tr>
	<tr><td>character of bleeding: 
			<c:choose><c:when test="${chits:observation(record, FPObstetricHistoryConcepts.REGULARITY).valueCoded eq chits:concept(FPObstetricOptions.IRREGULAR)}">irregular</c:when><c:otherwise>regular</c:otherwise></c:choose>,
			<c:choose><c:when test="${chits:observation(record, FPObstetricHistoryConcepts.DYSMENORRHEA).valueCoded eq chits:trueConcept()}">painful</c:when><c:otherwise>painless</c:otherwise></c:choose>,
			<chits_tag:obsValue obs="${chits:observation(record, FPObstetricHistoryConcepts.AMOUNT_OF_BLEEDING)}" />
		</td>
	</tr>
</table>
</c:when><c:otherwise>
no data provided yet
</c:otherwise></c:choose>
