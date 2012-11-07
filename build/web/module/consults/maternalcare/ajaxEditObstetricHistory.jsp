<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />

<div>
<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
</div>

<c:if test="${err != null}">
	<div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div>
</c:if>

<script>
function cancelSecondLevelGeneralForm() {
	$j("#secondLevelGeneralForm").dialog('close')
}
</script>

<chits_tag:auditInfo obsGroup="${form.mcProgramObs.obstetricHistory.obs}" />

<br/>
<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updateMCSection, 'secondLevelGeneralForm'); return false;">
<%@ include file="chartfragments/obstetricHistoryForm.jsp" %>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" value='Save' />
<input type="button" value='Cancel' onclick="cancelSecondLevelGeneralForm()" />
</div>
</form:form>