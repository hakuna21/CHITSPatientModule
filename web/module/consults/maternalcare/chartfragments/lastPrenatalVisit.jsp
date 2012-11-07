<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<fieldset><legend><span>Last Prenatal Visit<c:if test="${not empty lastPrenatalVisit}">: <fmt:formatDate value="${lastPrenatalVisit.visitDate}" pattern="MMM d, yyyy" /> (${fn:length(firstTrimesterVisits) + fn:length(secondTrimesterVisits) + fn:length(thirdTrimesterVisits)})</c:if></span></legend>
<c:choose><c:when test="${not empty lastPrenatalVisit}">
<chits_tag:prenatalVisitTable mcProgramObs="${form.mcProgramObs}" prenatalVisitRecord="${lastPrenatalVisit}" />
</c:when><c:otherwise>
<div class="indent">
	<em>none</em>
</div>
</c:otherwise></c:choose>
</fieldset>