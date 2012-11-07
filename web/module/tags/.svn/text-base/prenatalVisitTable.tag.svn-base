<%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ attribute name="mcProgramObs" required="true" type="org.openmrs.module.chits.mcprogram.MaternalCareProgramObs"
%><%@ attribute name="prenatalVisitRecord" required="true" type="org.openmrs.module.chits.mcprogram.PrenatalVisitRecord"
%><table class="borderless full-width registration" id="prenatalVisitRecord">
<tbody>
<tr><td style="width: 10em;">Visit Type:</td><td><chits_tag:obsValue obs="${chits:observation(prenatalVisitRecord.obs, MCPrenatalVisitRecordConcepts.VISIT_TYPE)}" /></td></tr>
<tr><td>Trimester:</td><td><chits_tag:maternalCareStage mcProgramObs="${mcProgramObs}" on="${prenatalVisitRecord.visitDate}" /></td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td>Nutritionally at risk:</td><td><chits_tag:obsValue obs="${chits:observation(prenatalVisitRecord.obs, MCPrenatalVisitRecordConcepts.NUTRITIONALLY_AT_RISK)}" /></td></tr>
<tr><td>Fundic height (cm):</td><td><chits_tag:obsValue obs="${chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.FUNDIC_HEIGHT)}" noData="<em>no entries</em>" /></td></tr>
<tr><td>FHR (beats / minute):</td><td><chits_tag:obsValue obs="${chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.FHR)}" noData="<em>no entries</em>" /></td></tr>
<tr><td>Presentation:</td><td><chits_tag:obsValue obs="${chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.FETAL_PRESENTATION)}" noData="<em>no entries</em>" /></td></tr>
<tr><td>Leopold's Maneuver:</td><td><c:if test="${not prenatalVisitRecord.obstetricExamination.leopoldsManeuverPerformed}">not </c:if>done</td></tr>
<tr><td>Remarks:</td><td><chits_tag:obsValue obs="${chits:observation(prenatalVisitRecord.obs, MCPrenatalVisitRecordConcepts.REMARKS)}" noData="<em>no entries</em>" /></td></tr>
<c:if test="${not empty chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.FUNDAL_GRIP).valueText}">
<tr><td>Fundal Grip:</td><td><chits_tag:obsValue obs="${chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.FUNDAL_GRIP)}" noData="<em>no entries</em>" /></td></tr>
</c:if>
<c:if test="${not empty chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.UMBILICAL_GRIP).valueText}">
<tr><td>Umbilical Grip:</td><td><chits_tag:obsValue obs="${chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.UMBILICAL_GRIP)}" noData="<em>no entries</em>" /></td></tr>
</c:if>
<c:if test="${not empty chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.PAWLICKS_GRIP).valueText}">
<tr><td>Pawlick's Grip:</td><td><chits_tag:obsValue obs="${chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.PAWLICKS_GRIP)}" noData="<em>no entries</em>" /></td></tr>
</c:if>
<c:if test="${not empty chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.PELVIC_GRIP).valueText}">
<tr><td>Pelvic Grip:</td><td><chits_tag:obsValue obs="${chits:observation(prenatalVisitRecord.obstetricExamination.obs, MCObstetricExamination.PELVIC_GRIP)}" noData="<em>no entries</em>" /></td></tr>
</c:if>
<tr><td>Danger Signs:</td><td><chits_tag:checklist items="${prenatalVisitRecord.dangerSigns.obs.groupMembers}" /></td></tr>
<tr><td>New Medical Conditions:</td><td><chits_tag:checklist items="${prenatalVisitRecord.newMedicalConditions.obs.groupMembers}" otherConcept="${MCMedicalHistoryConcepts.OTHERS}" /></td></tr>
</tbody>
</table>