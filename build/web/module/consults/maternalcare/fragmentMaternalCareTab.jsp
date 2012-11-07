<%@ page import="java.util.Date"%>
<%@ page import="org.openmrs.module.chits.Util"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<link href="${pageContext.request.contextPath}/moduleResources/chits/css/maternal-care-section.css?v=${deploymentTimestamp}" type="text/css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/moduleResources/chits/scripts/consults/maternalcare/maternalcare-section.js?v=${deploymentTimestamp}" type="text/javascript" ></script>

<c:set var="consultState" value="${form.mcProgramObs.currentState}" scope="request" />
<c:set var="page" value="${form.page}" />
<table id="maternalcare-section">
<c:choose><c:when test="${not empty page}">
	<tr><td class="left"><%-- ONE COLUMN: REGISTRATION PAGE --%>
		<center><div style="margin: 0.5em; width: 80%">
		<jsp:include page="${page.jspPath}" />
		</div></center>
	</td></tr>
</c:when><c:otherwise>
	<tr><td class="left" style="padding: 0; margin: 0;"><%-- LEFT COLUMN: regular access screen --%>
		<jsp:include page="fragmentRegularAccessScreen.jsp" />
	</td><td class="right"><%-- RIGHT COLUMN: maternal care chart --%>
		<jsp:include page="fragmentMaternalCareChart.jsp" />
	</td></tr>
</c:otherwise></c:choose>
</table>

<c:set var="tabHasErrors" value="${false}" scope="request" />