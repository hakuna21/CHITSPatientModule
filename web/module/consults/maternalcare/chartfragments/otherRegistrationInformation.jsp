<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="menstrualHistory" value="${form.mcProgramObs.menstrualHistory}" />
<fieldset><legend><span>OTHER REGISTRATION INFORMATION</span></legend>
<h3>Menstrual History</h3>
<c:set var="durationObs" value="${chits:observation(menstrualHistory.obs, MCMenstrualHistoryConcepts.DURATION)}" />
<c:set var="intervalObs" value="${chits:observation(menstrualHistory.obs, MCMenstrualHistoryConcepts.INTERVAL)}" />
<div class="indent">
	<table class="borderless full-width registration" id="menstrual-history">
	<tbody>
	<tr><td>Menarche (years old):</td><td><chits_tag:obsValue obs="${chits:observation(menstrualHistory.obs, MCMenstrualHistoryConcepts.AGE_OF_MENARCHE)}" /></td></tr>
	<tr><td>Duration (days):</td><td><chits_tag:obsValue obs="${durationObs}" /></td></tr>
	<tr><td>Interval (days):</td><td><chits_tag:obsValue obs="${intervalObs}" /></td></tr>
	<tr><td>Flow:</td><td><chits_tag:obsValue obs="${chits:observation(menstrualHistory.obs, MCMenstrualHistoryConcepts.FLOW)}" /></td></tr>
	<tr><td>Dysmenorrhea:</td><td><c:choose><c:when test="${chits:observation(menstrualHistory.obs, MCMenstrualHistoryConcepts.DYSMENORRHEA).valueCoded eq chits:trueConcept()}">Yes</c:when><c:otherwise>None</c:otherwise></c:choose></td></tr>
	</tbody>
	</table>
</div>

<h3>Past / Present Medical History</h3>
<div class="indent">
	<chits_tag:checklist items="${form.mcProgramObs.patientMedicalHistory.obs.groupMembers}" otherConcept="${MCMedicalHistoryConcepts.OTHERS}" />
</div>

<h3>Family Medical History</h3>
<div class="indent">
	<chits_tag:checklist items="${form.mcProgramObs.familyMedicalHistory.obs.groupMembers}" otherConcept="${MCMedicalHistoryConcepts.OTHERS}" />
</div>

<c:set var="personalHistory" value="${form.mcProgramObs.personalHistory}" />
<h3>Personal and Social History</h3>
<div class="indent">
	<table class="borderless full-width registration" id="personal-and-social-history">
	<tbody>
	<tr><td>Occupation:</td><td>${chits:conceptByIdOrName(form.patient.attributeMap[MiscAttributes.OCCUPATION].value).name.name}</td></tr>
	<c:choose><c:when test="${chits:observation(personalHistory.obs, MCPersonalHistoryConcepts.SMOKING_HISTORY).valueCoded eq chits:trueConcept()}">
	<tr><td>Smoker:</td><td>Yes</td></tr>
	<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Sticks per day</td><td><chits_tag:obsValue obs="${chits:observation(personalHistory.obs, MCPersonalHistoryConcepts.SMOKING_STICKS_PER_DAY)}" /></td></tr>
	<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Years smoking</td><td><chits_tag:obsValue obs="${chits:observation(personalHistory.obs, MCPersonalHistoryConcepts.SMOKING_YEARS)}" /></td></tr>
	</c:when><c:otherwise>
	<tr><td>Smoker:</td><td>No</td></tr>
	</c:otherwise></c:choose>
	<c:choose><c:when test="${chits:observation(personalHistory.obs, MCPersonalHistoryConcepts.ILLICIT_DRUG_USE).valueCoded eq chits:trueConcept()}">
	<tr><td>Illicit drug use:</td><td>Yes</td></tr>
	<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Drugs taken:</td><td><chits_tag:obsValue obs="${chits:observation(personalHistory.obs, MCPersonalHistoryConcepts.ILLICIT_DRUG_USE_DETAILS)}" /></td></tr>
	</c:when><c:otherwise>
	<tr><td>Illicit drug use:</td><td>No</td></tr>
	</c:otherwise></c:choose>
	<c:choose><c:when test="${chits:observation(personalHistory.obs, MCPersonalHistoryConcepts.ALCOHOLIC_INTAKE).valueCoded eq chits:trueConcept()}">
	<tr><td>Alcoholic drinker:</td><td>Yes</td></tr>
	<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Frequency of drinking / Type:</td><td><chits_tag:obsValue obs="${chits:observation(personalHistory.obs, MCPersonalHistoryConcepts.ALCOHOLIC_INTAKE_DETAILS)}" /></td></tr>
	</c:when><c:otherwise>
	<tr><td>Alcoholic drinker:</td><td>No</td></tr>
	</c:otherwise></c:choose>
	</tbody>
	</table>
</div>

<h3>Family Planning History</h3>
<div class="indent">
	<em>no data</em>
</div>
</fieldset>