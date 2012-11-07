<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="noData"><em class="alert">N/A</em></c:set>

<c:set var="vitalSignsObs" value="${chits:observation(form.encounter, VisitConcepts.VITAL_SIGNS)}" />
<c:set var="sbpObs" value="${chits:observation(vitalSignsObs, VisitConcepts.SBP)}" />
<c:set var="dbpObs" value="${chits:observation(vitalSignsObs, VisitConcepts.DBP)}" />
<c:set var="pulse" value="${chits:observation(vitalSignsObs, VisitConcepts.PULSE)}" />
<c:set var="rr" value="${chits:observation(vitalSignsObs, VisitConcepts.RESPIRATORY_RATE)}" />
<c:set var="weightObs" value="${chits:observation(form.encounter, VisitConcepts.WEIGHT_KG)}" />
<c:set var="tempObs" value="${chits:observation(vitalSignsObs, VisitConcepts.TEMPERATURE_C)}" />

<fieldset><legend><span>FAMILY PLANNING PROGRAM CHART</span></legend>
<table class="form full-width" id="fpChart">
	<tr><th>BP</th><th>HR</th><th>RR</th><th>Weight<br/>(Kg)</th><th>Temp<br/>(in &deg;C)</th></tr>
	<tr>
		<td>
			<c:choose><c:when test="${not empty sbpObs.valueNumeric and not empty dbpObs.valueNumeric}">
				<chits_tag:bloodPressure sbp="${sbpObs}" dbp="${dbpObs}" />
			</c:when><c:otherwise>${noData}</c:otherwise></c:choose>
		</td>
		<td>
			<c:choose><c:when test="${not empty pulse.valueNumeric}">
				<chits_tag:pulse pulse="${pulse}" />
			</c:when><c:otherwise>${noData}</c:otherwise></c:choose>
		</td>
		<td>
			<c:choose><c:when test="${not empty rr.valueNumeric}">
				<chits_tag:respiratoryRate rate="${rr}" />
			</c:when><c:otherwise>${noData}</c:otherwise></c:choose>
		<td>
			<c:choose><c:when test="${not empty weightObs.valueNumeric}">
				<chits:weight obs="${weightObs}" noEnteredDataText="${noData}" />
			</c:when><c:otherwise>${noData}</c:otherwise></c:choose>
		</td>
		<td <c:if test="${tempObs.valueNumeric ge GlobalProperty[ChildCareConstants.GP_HIGH_TEMPERATURE_WARNING]}">class="alert"</c:if>>
			<c:choose><c:when test="${not empty tempObs.valueNumeric}">
				<chits:temp obs="${tempObs}" noEnteredDataText="${noData}" />
			</c:when><c:otherwise>${noData}</c:otherwise></c:choose>
		</td>
	</tr>
</table>

<%-- Add registration information to lower part of chart --%>
<jsp:include page="registrationInformation.jsp" />

</fieldset>