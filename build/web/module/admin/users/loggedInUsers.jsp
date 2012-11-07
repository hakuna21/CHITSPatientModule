<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Edit Users" otherwise="/login.htm" redirect="/module/chits/admin/audit/loggedInUsers.list?showOnlyLoggedInUsers=true" />

<spring:message var="pageTitle" code="chits.admin.audit.logged.in.users.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="../../formStyle.jsp" %>
<%@ include file="../../pleaseWait.jsp" %>

<h2><spring:message code="chits.admin.audit.logged.in.users.title"/></h2>	

<spring:hasBindErrors name="form">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<br />
<style>table.form td.nowrap { white-space: nowrap; }</style>
<c:set var="pageSize" value="${6}" />

<script>
function openPage(page) {
	$j('#loggedInUsersForm input[name=page]').val(page);
	$j('#loggedInUsersForm').submit();
}
</script>

<table class="form">
<tr><td style="border: 0">
<form action="loggedInUsers.list" method="get" class="form" id="loggedInUsersForm" onsubmit="pleaseWaitDialog()">
	<div style="float: right">
	<input type="checkbox" value="true" name="showOnlyLoggedInUsers"
		<c:if test="${param.showOnlyLoggedInUsers eq 'true'}">checked="checked"</c:if> id="showOnlyLoggedInUsers"
		onchange="$j(this.form).submit()" onclick="$j(this.form).submit()" />
	<input type="hidden" name="page" value="1" />
	<label for="showOnlyLoggedInUsers">Show only logged-in users</label>
	</div>

	Page:
	<c:forEach var="page" begin="${1}" end="${fn:length(userSessionData) div pageSize + (fn:length(userSessionData) mod pageSize gt 0 ? 1 : 0)}">
	<c:choose><c:when test="${page eq param.page}">
		${page}
	</c:when><c:otherwise>
		<a href="javascript: openPage(${page})">${page}</a>
	</c:otherwise></c:choose>
	</c:forEach>
	<table style="width: 700px" class="form">
	<tr>
		<th></th>
		<th>User</th>
		<th>IP Address</th>
		<th>Login Time<c:if test="${param.showOnlyLoggedInUsers ne 'true'}">&nbsp;/<br/>Logout Time</c:if></th>
		<th>Duration<c:if test="${param.showOnlyLoggedInUsers ne 'true'}">&nbsp;/<br/>(Session timed-out?)</c:if></th>
		<th>Browser User Agent</th>
	</tr>
	<c:choose><c:when test="${not empty userSessionData}">
	<c:choose><c:when test="${param.page ge 0}">
		<c:set var="begin" value="${(param.page - 1) * pageSize}" />
		<c:set var="end" value="${(param.page - 1) * pageSize + pageSize - 1}" />
	</c:when><c:otherwise>
		<c:set var="begin" value="${0}" />
		<c:set var="end" value="${pageSize - 1}" />
	</c:otherwise></c:choose>
	<c:forEach var="userSessionInfo" items="${userSessionData}" begin="${begin}" end="${end}" varStatus="i">
	<tr title="Session ID: ${userSessionInfo.sessionId}">
		<td>${begin + i.count}</td>
		<td class="nowrap">${chits:coalesce(userSessionInfo.user.username, userSessionInfo.user.systemId)}<br/>(${userSessionInfo.user.person.personName})</td>
		<td class="nowrap">${userSessionInfo.remoteAddress}</td>
		<td class="nowrap">
			<fmt:formatDate value="${userSessionInfo.loginTimestamp}" pattern="MMM dd" />,&nbsp;<fmt:formatDate value="${userSessionInfo.loginTimestamp}" pattern="hh:mm:ss a" />
			<c:if test="${not empty userSessionInfo.logoutTimestamp}">
			<br/><fmt:formatDate value="${userSessionInfo.logoutTimestamp}" pattern="MMM dd" />,&nbsp;<fmt:formatDate value="${userSessionInfo.logoutTimestamp}" pattern="hh:mm:ss a" />
			</c:if>
		</td>
		<td>
			<c:choose><c:when test="${userSessionInfo.loginTimestamp ne null}">
				<chits:elapsed since="${userSessionInfo.loginTimestamp}" upto="${userSessionInfo.logoutTimestamp}" />
			</c:when><c:otherwise>
				Not Available
			</c:otherwise></c:choose>
			<c:if test="${param.showOnlyLoggedInUsers ne 'true' and userSessionInfo.logoutTimestamp ne null}">
			/ (<c:choose><c:when test="${userSessionInfo.sessionTimedOut}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose>)
			</c:if>
		</td>
		<td>${userSessionInfo.userAgent}</td>
	</tr>
	</c:forEach>
	</c:when><c:otherwise>
	<tr>
		<td colspan="6">There are no matching records.</td>
	</tr>
	</c:otherwise></c:choose>
	</table>
	<br/>
	<input type="submit" value="Refresh" />
</form>
</td></tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %>
