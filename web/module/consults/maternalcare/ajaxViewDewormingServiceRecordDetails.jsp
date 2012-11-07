<%@ page import="java.util.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><chits_tag:auditInfo obsGroup="${form.dewormingServiceRecord.obs}" />

<fieldset><legend><span>Deworming Service on: <fmt:formatDate value="${chits:observation(form.dewormingServiceRecord.obs, MCServiceRecordConcepts.DATE_ADMINISTERED).valueDatetime}" pattern="MMM d, yyyy" /></span></legend>

<table class="borderless full-width registration" id="dewormingServiceRecord">
<tbody>
<tr><td>Visit&nbsp;Type:</td><td><chits_tag:obsValue obs="${chits:observation(form.dewormingServiceRecord.obs, DewormingConcepts.VISIT_TYPE)}" /></td></tr>
<tr><td>Service&nbsp;Source:</td><td><chits_tag:obsValue obs="${chits:observation(form.dewormingServiceRecord.obs, DewormingConcepts.SERVICE_SOURCE)}" /></td></tr>
<tr><td>Administered&nbsp;by:</td><td>${form.dewormingServiceRecord.administeredBy.person.personName}</td></tr>
<tr><td>Remarks:</td><td><chits_tag:obsValue obs="${chits:observation(form.dewormingServiceRecord.obs, DewormingConcepts.REMARKS)}" /></td></tr>
</tbody>
</table>

</fieldset>
