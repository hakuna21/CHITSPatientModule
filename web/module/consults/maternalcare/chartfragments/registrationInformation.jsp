<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="registeredState" value="${chits:findPatientProgramState(form.mcProgramObs.patientProgram, MaternalCareProgramStates.REGISTERED)}" />
<c:set var="obstetricHistory" value="${form.mcProgramObs.obstetricHistory}" />
<table class="full-width borderless registration">
<c:choose><c:when test="${registeredState eq null}">
<tr><td class="label">Registration No:</td><td><tt class="alert">NOT YET REGISTERED</tt></td></tr>
<tr><td class="label">Date Registered:</td><td></td></tr>
</c:when><c:otherwise>
<c:set var="lmpObs" value="${chits:observation(obstetricHistory.obs, MCObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD)}" />
<tr><td class="label">Registration No:</td><td class="alert"><fmt:formatNumber pattern="0000000" value="${registeredState.id}" /></td></tr>
<tr><td class="label">Date Registered:</td><td><fmt:formatDate value="${registeredState.startDate}" /></td></tr>
<tr><td class="label">Age at EDC:</td><td><chits:age birthdate="${form.patient.birthdate}" on="${form.mcProgramObs.estimatedDateOfConfinement}" /></td></tr>
<tr><td class="label">Risk Factor:</td><td>${form.riskFactors}</td></tr>
<tr><td class="label">Last Menstrual Period</td><td><chits_tag:obsValue obs="${lmpObs}" /></td></tr>
<tr><td class="label">Estimated Date of Confinement:</td><td><fmt:formatDate pattern="MM/dd/yyyy" value="${form.mcProgramObs.estimatedDateOfConfinement}" /></td></tr>
<tr><td class="label">Estimated Age of Gestation:</td><td><chits:age birthdate ="${lmpObs.valueDatetime}" weeksOnly="${true}" daysOnly="${true}" /></td></tr>
<tr><td class="label">Blood Type:</td><td><chits_tag:bloodType
	bloodType="${chits:observation(obstetricHistory.obs, MCObstetricHistoryConcepts.BLOOD_TYPE)}"
	rhFactor="${chits:observation(obstetricHistory.obs, MCObstetricHistoryConcepts.RHESUS_FACTOR)}" /></td></tr>
<tr><td class="label">Registered by:</td><td>${registeredState.creator.personName}</td></tr>
</c:otherwise></c:choose>
</table>