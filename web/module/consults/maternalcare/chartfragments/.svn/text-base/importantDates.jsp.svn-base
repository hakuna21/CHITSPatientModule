<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ page import="org.openmrs.module.chits.MaternalCareConsultEntryForm"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<fieldset><legend><span>Important Dates</span></legend>
<c:set scope="request" var="latestPrenatalVisits" value="${form.mcProgramObs.latestPrenatalVisits}"
/><c:set scope="request" var="firstTrimesterVisits" value="${latestPrenatalVisits[MCMaternityStage.FIRST_TRIMESTER]}"
/><c:set scope="request" var="secondTrimesterVisits" value="${latestPrenatalVisits[MCMaternityStage.SECOND_TRIMESTER]}"
/><c:set scope="request" var="thirdTrimesterVisits" value="${latestPrenatalVisits[MCMaternityStage.THIRD_TRIMESTER]}"
/><c:set scope="request" var="postpartumVisits" value="${form.mcProgramObs.postPartumVisits}"
/><table class="form full-width registration" id="importantDates">
<thead><tr><th>PERIOD</th><th>END DATE</th><th>LAST CHECK-UP</th></tr></thead>
<tbody><%
	final MaternalCareConsultEntryForm form = (MaternalCareConsultEntryForm) pageContext.findAttribute("form");
	final Date now = new Date();
	final Date endOfFirstTrimester = form.getMcProgramObs().getEndOfFirstTrimester();
	final Date endOfSecondTrimester = form.getMcProgramObs().getEndOfSecondTrimester();
	final Date endOfThirdTrimester = form.getMcProgramObs().getEndOfThirdTrimester();
	final Date endOfPostpartum = form.getMcProgramObs().getEndOfPostpartum(); %>
	<tr>
		<td>1st Trimester</td><td><fmt:formatDate value="<%= endOfFirstTrimester %>" pattern="MMM d, yyyy" /></td>
		<td><c:choose><c:when test="${not empty firstTrimesterVisits}"><fmt:formatDate value="${firstTrimesterVisits[0].visitDate}" pattern="MMM d, yyyy" /> (${fn:length(firstTrimesterVisits)})</c:when
			><c:otherwise><% if (endOfFirstTrimester.before(now)) { %>not seen<% } %></c:otherwise></c:choose></td>
	</tr><tr>
		<td>2nd Trimester</td><td><fmt:formatDate value="<%= endOfSecondTrimester %>" pattern="MMM d, yyyy" /></td>
		<td><c:choose><c:when test="${not empty secondTrimesterVisits}"><fmt:formatDate value="${secondTrimesterVisits[0].visitDate}" pattern="MMM d, yyyy" /> (${fn:length(secondTrimesterVisits)})</c:when
			><c:otherwise><% if (endOfSecondTrimester.before(now)) { %>not seen<% } %></c:otherwise></c:choose></td>
	</tr><tr>
		<td>3rd Trimester</td><td><fmt:formatDate value="<%= endOfThirdTrimester %>" pattern="MMM d, yyyy" /></td>
		<td><c:choose><c:when test="${not empty thirdTrimesterVisits}"><fmt:formatDate value="${thirdTrimesterVisits[0].visitDate}" pattern="MMM d, yyyy" /> (${fn:length(thirdTrimesterVisits)})</c:when
			><c:otherwise><% if (endOfThirdTrimester.before(now)) { %>not seen<% } %></c:otherwise></c:choose></td>
	</tr><tr>
		<td>Postpartum</td><td><fmt:formatDate value="<%= endOfPostpartum %>" pattern="MMM d, yyyy" /></td><td><c:if test="${not empty postpartumVisits}"><fmt:formatDate value="${postpartumVisits[0].visitDate}" pattern="MMM d, yyyy" /> (${fn:length(postpartumVisits)})</c:if></td>
	</tr>
</tbody>
</table>
</fieldset>