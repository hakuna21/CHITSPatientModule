<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="record" value="${form.fpProgramObs.familyInformation.obs}" />
<c:choose><c:when test="${record.obsId gt 0}">
<table class="form full-width chart" id="family-information-chart">
	<tr><td>Last update</td><td><fmt:formatDate pattern="d MMMM yyyy" value="${record.obsDatetime}" /></td></tr>
	<tr><td>Updated by</td><td>${record.creator.person.personName}</td></tr>
	<tr><td>Number of children</td><td><chits_tag:obsValue obs="${chits:observation(record, FPFamilyInformationConcepts.NUMBER_OF_CHILDREN)}" /></td></tr>
	<tr><td>Additional children desired</td><td><chits_tag:obsValue obs="${chits:observation(record, FPFamilyInformationConcepts.NMBR_OF_CHILDREN_DESIRED)}" /></td></tr>
	<tr><td>Planned interval</td><td><chits_tag:obsValue obs="${chits:observation(record, FPFamilyInformationConcepts.PLANNED_INTERVAL)}" /></td></tr>
	<tr><td>Educational Attainment</td><td>${chits:conceptByIdOrName(form.patient.attributeMap[MiscAttributes.EDUCATION].value).name.name}</td></tr>
	<tr><td>Occupation</td><td>${chits:conceptByIdOrName(form.patient.attributeMap[MiscAttributes.OCCUPATION].value).name.name}</td></tr>
	<tr><td>Partner</td><td><c:choose><c:when test="${not empty form.partner.id}">${form.partner.personName}</c:when><c:otherwise>Not specified</c:otherwise></c:choose></td></tr>
	<tr><td>Reason for practicing</td><td><chits_tag:obsValue obs="${chits:observation(record, FPFamilyInformationConcepts.REASON_FOR_PRACTICING)}" noData="<em>not specified</em>" /></td></tr>
</table>
</c:when><c:otherwise>
no data provided yet
</c:otherwise></c:choose>
