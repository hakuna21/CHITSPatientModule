<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<fieldset><legend><span>Last IE Results<c:if test="${not empty lastIERecord}">: <fmt:formatDate value="${lastIERecord.visitDate}" pattern="MMM d, yyyy" /></c:if></span></legend>
<c:choose><c:when test="${not empty lastIERecord}">
<chits_tag:internalExaminationRecordTable mcProgramObs="${form.mcProgramObs}" internalExaminationRecord="${lastIERecord}" />
</c:when><c:otherwise>
<div class="indent">
	<em>none</em>
</div>
</c:otherwise></c:choose>
</fieldset>