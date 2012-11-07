<%@ page import="java.util.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><chits_tag:auditInfo obsGroup="${form.tetanusServiceRecord.obs}" />

<fieldset><legend><span>Tetanus Service on: <fmt:formatDate value="${form.tetanusServiceRecord.dateAdministered}" pattern="MMM d, yyyy" /></span></legend>

<table class="borderless full-width registration" id="tetanusServiceRecord">
<tbody>
<tr><td>Visit&nbsp;Type:</td><td><chits_tag:obsValue obs="${chits:observation(form.tetanusServiceRecord.obs, TetanusToxoidRecordConcepts.VISIT_TYPE)}" /></td></tr>
<tr><td>Service&nbsp;Source:</td><td><chits_tag:obsValue obs="${chits:observation(form.tetanusServiceRecord.obs, TetanusToxoidRecordConcepts.SERVICE_SOURCE)}" /></td></tr>
<tr><td>Vaccine&nbsp;Type:</td><td><chits_tag:obsValue obs="${form.tetanusServiceRecord.obs}" /></td></tr>
<tr><td>Administered&nbsp;by:</td><td>${form.tetanusServiceRecord.obs.creator.person.personName}</td></tr>
<tr><td>Remarks:</td><td><chits_tag:obsValue obs="${chits:observation(form.tetanusServiceRecord.obs, TetanusToxoidRecordConcepts.REMARKS)}" /></td></tr>
</tbody>
</table>

</fieldset>
