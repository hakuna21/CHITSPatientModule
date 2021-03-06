<%@ page import="java.util.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><chits_tag:auditInfo obsGroup="${form.vitaminAServiceRecord.obs}" />

<fieldset><legend><span>Vitamin A Service on: <fmt:formatDate value="${chits:observation(form.vitaminAServiceRecord.obs, MCServiceRecordConcepts.DATE_ADMINISTERED).valueDatetime}" pattern="MMM d, yyyy" /></span></legend>

<table class="borderless full-width registration" id="vitaminAServiceRecord">
<tbody>
<tr><td>Visit&nbsp;Type:</td><td><chits_tag:obsValue obs="${chits:observation(form.vitaminAServiceRecord.obs, VitaminAConcepts.VISIT_TYPE)}" /></td></tr>
<tr><td>Service&nbsp;Source:</td><td><chits_tag:obsValue obs="${chits:observation(form.vitaminAServiceRecord.obs, VitaminAConcepts.SERVICE_SOURCE)}" /></td></tr>
<tr><td>Administered&nbsp;by:</td><td>${form.vitaminAServiceRecord.administeredBy.person.personName}</td></tr>
<tr><td>Remarks:</td><td><chits_tag:obsValue obs="${chits:observation(form.vitaminAServiceRecord.obs, VitaminAConcepts.REMARKS)}" /></td></tr>
</tbody>
</table>

</fieldset>
