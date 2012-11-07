<%@ page import="java.util.Date"%>
<%@ page import="org.openmrs.module.chits.Util"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<link href="${pageContext.request.contextPath}/moduleResources/chits/css/family-planning-section.css?v=${deploymentTimestamp}" type="text/css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/moduleResources/chits/scripts/consults/familyplanning/familyplanning-section.js?v=${deploymentTimestamp}" type="text/javascript" ></script>

<c:set var="page" value="${form.page}" />
<table id="familyplanning-section">
<c:choose><c:when test="${not empty page}">
	<tr><td class="left"><%-- ONE COLUMN: REGISTRATION PAGE --%>
		<center><div style="margin: 0.5em; width: 80%">
		<jsp:include page="registration/${page.jspPath}" />
		</div></center>
	</td></tr>
</c:when><c:otherwise>
	<tr><td class="left" style="padding: 0; margin: 0;"><%-- LEFT COLUMN: regular access screen --%>
		<jsp:include page="fragmentRegularAccessScreen.jsp" />
	</td><td class="right"><%-- RIGHT COLUMN: family planning chart --%>
		<jsp:include page="fragmentFamilyPlanningChart.jsp" />
	</td></tr>
</c:otherwise></c:choose>
</table>

<c:set var="tabHasErrors" value="${false}" scope="request" />