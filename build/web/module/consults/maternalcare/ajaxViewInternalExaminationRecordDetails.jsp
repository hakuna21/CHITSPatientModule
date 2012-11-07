<%@ page import="java.util.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><chits_tag:auditInfo obsGroup="${form.internalExaminationRecord.obs}" />

<fieldset><legend><span>IE Record on: <fmt:formatDate value="${form.internalExaminationRecord.visitDate}" pattern="MMM d, yyyy" /></span></legend>
<chits_tag:internalExaminationRecordTable mcProgramObs="${form.mcProgramObs}" internalExaminationRecord="${form.internalExaminationRecord}" />
</fieldset>