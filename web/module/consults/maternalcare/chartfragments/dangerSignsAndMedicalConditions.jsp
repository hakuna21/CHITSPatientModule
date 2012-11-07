<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="dangerSignsByVisit" value="${form.mcProgramObs.dangerSignsAndMedicalConditionsByVisitDate}" />
<fieldset><legend><span>Danger Signs / Medical Conditions<br/>Observed during this Pregnancy</span></legend>
<c:choose><c:when test="${not empty dangerSignsByVisit}">
	<table class="form full-width registration" id="danger-signs-by-visit-date">
	<thead><th>DATE</th><th>OBSERVATIONS</th></thead>
	<tbody><c:forEach var="visitDangerSigns" items="${dangerSignsByVisit}">
		<tr><td style="white-space: nowrap;"><fmt:formatDate value="${visitDangerSigns.key}" pattern="MMM d, yyyy" /></td><td><chits_tag:checklist items="${visitDangerSigns.value}" otherConcept="${MCMedicalHistoryConcepts.OTHERS}" /></td></tr>
	</c:forEach></tbody>
	</table>
</c:when><c:otherwise>
<div class="indent">
	<em>none</em>
</div>
</c:otherwise></c:choose>
</fieldset>