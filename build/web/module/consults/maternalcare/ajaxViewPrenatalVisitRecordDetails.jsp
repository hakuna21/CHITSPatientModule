<%@ page import="java.util.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><chits_tag:auditInfo obsGroup="${form.prenatalVisitRecord.obs}" />

<fieldset><legend><span>Prenatal Visit on: <fmt:formatDate value="${form.prenatalVisitRecord.visitDate}" pattern="MMM d, yyyy" /></span></legend>
<chits_tag:prenatalVisitTable mcProgramObs="${form.mcProgramObs}" prenatalVisitRecord="${form.prenatalVisitRecord}" />
</fieldset>

<c:if test="${form.prenatalVisitRecord.obstetricExamination.leopoldsManeuverPerformed}">
<fieldset><legend><span>Leopold's Maneuver Findings:</span></legend>
<table id="prenatal-leopolds-maneuver-findings" class="full-width borderless registration">
	<tr>
		<td style="width: 10em;">Fundal Grip</td>
		<td><chits_tag:obsValue obs="${form.prenatalVisitRecord.obstetricExamination.observationMap[MCObstetricExamination.FUNDAL_GRIP.conceptId]}" /></td>
	</tr><tr>
		<td>Umbilical Grip</td>
		<td><chits_tag:obsValue obs="${form.prenatalVisitRecord.obstetricExamination.observationMap[MCObstetricExamination.UMBILICAL_GRIP.conceptId]}" /></td>
	</tr><tr>
		<td>Pawlick's Grip</td>
		<td><chits_tag:obsValue obs="${form.prenatalVisitRecord.obstetricExamination.observationMap[MCObstetricExamination.PAWLICKS_GRIP.conceptId]}" /></td>
	</tr><tr>
		<td>Pelvic Grip</td>
		<td><chits_tag:obsValue obs="${form.prenatalVisitRecord.obstetricExamination.observationMap[MCObstetricExamination.PELVIC_GRIP.conceptId]}" /></td>
	</tr>
</table>
</fieldset>
</c:if>

<fieldset><legend><span>Danger Signs</span></legend>
<chits_tag:checklist items="${form.prenatalVisitRecord.dangerSigns.obs.groupMembers}" />
</fieldset>

<fieldset><legend><span>New Medical Conditions</span></legend>
<chits_tag:checklist items="${form.prenatalVisitRecord.newMedicalConditions.obs.groupMembers}" otherConcept="${MCMedicalHistoryConcepts.OTHERS}" />
</fieldset>
