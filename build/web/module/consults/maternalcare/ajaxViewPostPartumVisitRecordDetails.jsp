<%@ page import="java.util.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><chits_tag:auditInfo obsGroup="${form.postPartumVisitRecord.obs}" />

<style>
#consultation-notes td.text { background-color: #f0f0f0; padding: 0.2em; padding-left: 1em; }
</style>

<table>
<tr>
	<td style="width: 50%; vertical-align: top;">

		<fieldset><legend>Post-partum Visit Record</legend>
		<table class="borderless full-width registration" id="postPartumVisitRecord">
		<tbody>
		<tr><td style="width: 10em;">Visit Date:</td><td><fmt:formatDate value="${form.postPartumVisitRecord.visitDate}" pattern="MMM d, yyyy" /></td></tr>
		<tr><td>Visit Type:</td><td><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.VISIT_TYPE)}" /></td></tr>
		</tbody>
		</table>
		</fieldset>
		
		<fieldset><legend>Physical Examination Findings:</legend>
		<table id="consultation-notes" class="full-width borderless registration">
			<tr><td>Breast:</td></tr><tr><td class="text"><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.BREAST_EXAM_FINDINGS)}" />&nbsp;</td></tr>
			<tr><td>Uterus:</td></tr><tr><td class="text"><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.UTERUS_EXAM_FINDINGS)}" />&nbsp;</td></tr>
			<tr><td>Vagninal Discharge:</td></tr><tr><td class="text"><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.VAGNIAL_EXAM_FINDINGS)}" />&nbsp;</td></tr>
			<tr><td>Laceration / episiotomy:</td></tr><tr><td class="text"><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.EPISIOTOMY_EXAM_FINDINGS)}" />&nbsp;</td></tr>
			<tr><td>Others:</td></tr><tr><td class="text"><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.OTHER_FINDINGS)}" />&nbsp;</td></tr>
		</table>
		</fieldset>
		
		<fieldset>
		<legend><span>Internal Examination</span></legend>
		<div class="postpartum-internal-examination-details" class="registration grouped-block">
			<jsp:include page="chartfragments/ajaxPostpartumInternalExaminations.jsp" />
		</div>
		</fieldset>

	</td><td style="width: 50%; vertical-align: top;">
		
		<fieldset><legend>Postpartum Events</legend>
		<chits_tag:checklist items="${form.postPartumVisitRecord.postPartumEvents.obs.groupMembers}" />
		</fieldset>
		
		<fieldset><legend>Breastfeeding</legend>
		<table class="full-width borderless registration">
		<tr>
			<td>Done within one hour after delivery</td>
			<td><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.BREASTFED_WITHIN_HOUR)}" /></td>
		</tr><tr>
			<td>Still EXCLUSIVELY breastfeeding</td>
			<td><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.BREASTFED_EXCLUSIVELY)}" /></td>
		</tr>
		</table>
		</fieldset>
		
		<fieldset><legend>Consultation notes:</legend>
		<table id="consultation-notes" class="full-width borderless registration">
			<tr><td>Advice given:</td></tr><tr><td class="text"><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.ADVICE_GIVEN)}" />&nbsp;</td></tr>
			<tr><td>Personal hygiene:</td></tr><tr><td class="text"><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.HYGIENE_NOTES)}" />&nbsp;</td></tr>
			<tr><td>Nutrition:</td></tr><tr><td class="text"><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.NUTRITION_NOTES)}" />&nbsp;</td></tr>
			<tr><td>Immunization:</td></tr><tr><td class="text"><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.IMMUNIZATION_NOTES)}" />&nbsp;</td></tr>
		</table>
		</fieldset>
		
		<fieldset><legend>Checkup Remarks:</legend>
		<table id="checkup-remarks" class="full-width borderless registration">
		<tr><td><pre><chits_tag:obsValue obs="${chits:observation(form.postPartumVisitRecord.obs, MCPostPartumVisitRecordConcepts.REMARKS)}" /></pre></td></tr>
		</table>
		</fieldset>

	</td>
</tr>
</table>