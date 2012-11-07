<%@ page import="java.util.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><chits_tag:auditInfo obsGroup="${form.ironSupplementationServiceRecord.obs}" />

<fieldset><legend><span>Iron Supplementation Service on: <fmt:formatDate value="${chits:observation(form.ironSupplementationServiceRecord.obs, MCServiceRecordConcepts.DATE_ADMINISTERED).valueDatetime}" pattern="MMM d, yyyy" /></span></legend>

<table class="borderless full-width registration" id="ironSupplementationServiceRecord">
<tbody>
<tr><td>Visit&nbsp;Type:</td><td><chits_tag:obsValue obs="${chits:observation(form.ironSupplementationServiceRecord.obs, IronSupplementationConcepts.VISIT_TYPE)}" /></td></tr>
<tr><td>Service&nbsp;Source:</td><td><chits_tag:obsValue obs="${chits:observation(form.ironSupplementationServiceRecord.obs, IronSupplementationConcepts.SERVICE_SOURCE)}" /></td></tr>
<tr><td>Drug&nbsp;Formulary:</td><td><chits_tag:obsValue obs="${chits:observation(form.ironSupplementationServiceRecord.obs, IronSupplementationConcepts.DRUG_FORMULARY)}" /></td></tr>
<tr><td>Quantity:</td><td><chits_tag:obsValue obs="${chits:observation(form.ironSupplementationServiceRecord.obs, IronSupplementationConcepts.QUANTITY)}" /></td></tr>
<tr><td>Administered&nbsp;by:</td><td>${form.ironSupplementationServiceRecord.administeredBy.person.personName}</td></tr>
<tr><td>Remarks:</td><td><chits_tag:obsValue obs="${chits:observation(form.ironSupplementationServiceRecord.obs, IronSupplementationConcepts.REMARKS)}" /></td></tr>
</tbody>
</table>

</fieldset>
