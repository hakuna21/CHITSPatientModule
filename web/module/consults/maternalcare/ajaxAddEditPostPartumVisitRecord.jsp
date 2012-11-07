<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />

<div>
<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
</div>

<c:if test="${err != null}">
	<div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div>
</c:if>

<script>
function cancelForm() {
	$j("#generalForm").dialog('close')
}

function updatePostpartumIEDetails(patientId, onUpdate) {
	$j("div.postpartum-internal-examination-details").html("<h3><center>Reloading, Please Wait...</center></h3>")
	$j.ajax({url: 'ajaxPostpartumInternalExaminations.form?patientId=' + patientId + "&mcProgramObsId=${form.mcProgramObs.obs.obsId}", cache: false, success: function (data) {
		$j("div.postpartum-internal-examination-details").html(data)
		if (onUpdate) {
			onUpdate()
		}
	}})
}
</script>

<chits_tag:auditInfo obsGroup="${form.postPartumVisitRecord.obs}" />

<c:set var="vitalSignsObs" value="${chits:observation(form.encounter, VisitConcepts.VITAL_SIGNS)}" />
<c:set var="sbpObs" value="${chits:observation(vitalSignsObs, VisitConcepts.SBP)}" />
<c:set var="dbpObs" value="${chits:observation(vitalSignsObs, VisitConcepts.DBP)}" />

<br />
<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updateMCSection); return false;">
<form:hidden path="version" />

<fieldset>
<table id="post-partum-visit-record" class="full-width borderless registration field">
	<tr>
		<td class="label">Checkup date</td>
		<td><chits_tag:springInput path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.VISIT_DATE.conceptId}].valueText" onclick="showCalendar(this)" /></td>
	</tr><tr>
		<td class="label">Visit type</td>
		<td><chits_tag:springDropdown path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.VISIT_TYPE.conceptId}].valueCoded" answers="${chits:answers(MCPostPartumVisitRecordConcepts.VISIT_TYPE)}" /></td>
	</tr><tr>
		<td class="label">Blood pressure:</td>
		<td><chits_tag:bloodPressure sbp="${sbpObs}" dbp="${dbpObs}" /></td>
	</tr>
</table>
</fieldset>

<fieldset><legend>Physical Examination Findings:</legend>
<table id="physical-exam-findings" class="full-width borderless registration">
	<tr><td class="label">Breast:</td></tr><tr><td><chits_tag:springInput path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.BREAST_EXAM_FINDINGS.conceptId}].valueText" cssClass="full-width" /></td></tr>
	<tr><td class="label">Uterus:</td></tr><tr><td><chits_tag:springInput path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.UTERUS_EXAM_FINDINGS.conceptId}].valueText" cssClass="full-width" /></td></tr>
	<tr><td class="label">Vaginal Discharge:</td></tr><tr><td><chits_tag:springInput path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.VAGNIAL_EXAM_FINDINGS.conceptId}].valueText" cssClass="full-width" /></td></tr>
	<tr><td class="label">Laceration / episiotomy:</td></tr><tr><td><chits_tag:springInput path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.EPISIOTOMY_EXAM_FINDINGS.conceptId}].valueText" cssClass="full-width" /></td></tr>
	<tr><td class="label">Others:</td></tr><tr><td><chits_tag:springTextArea path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.OTHER_FINDINGS.conceptId}].valueText" cssClass="full-width" /></td></tr>
</table>
</fieldset>

<fieldset>
<legend><span>Internal Examination</span></legend>
<div class="postpartum-internal-examination-details" class="registration grouped-block">
	<jsp:include page="chartfragments/ajaxPostpartumInternalExaminations.jsp" />
</div>

<div class="full-width" style="text-align: right">
<input type="button" id="addNewButton" value='Add New' onclick='loadAjaxForm("addPostpartumInternalExaminationRecord.form?patientId=${form.patient.patientId}", "NEW INTERNAL EXAMINATION RECORD", ${form.patient.patientId}, 400, "subGeneralForm");' />
</div>
</fieldset>

<fieldset><legend>Postpartum events:</legend>
<table id="post-partum-events" class="full-width borderless registration">
	<tr><td><chits_tag:springCheckbox path="postPartumVisitRecord.postPartumEvents.observationMap[${MCPostPartumEventsConcepts.VAGINAL_INFECTION.conceptId}].valueCoded" label="With vaginal infection" /></td></tr>
	<tr><td><chits_tag:springCheckbox path="postPartumVisitRecord.postPartumEvents.observationMap[${MCPostPartumEventsConcepts.VAGINAL_BLEEDING.conceptId}].valueCoded" label="With vaginal bleeding" /></td></tr>
	<tr><td><chits_tag:springCheckbox path="postPartumVisitRecord.postPartumEvents.observationMap[${MCPostPartumEventsConcepts.FEVER_OVER_38.conceptId}].valueCoded" label="With fever &gt; 38&deg;C" /></td></tr>
	<tr><td><chits_tag:springCheckbox path="postPartumVisitRecord.postPartumEvents.observationMap[${MCPostPartumEventsConcepts.PALLOR.conceptId}].valueCoded" label="With pallor" /></td></tr>
	<tr><td><chits_tag:springCheckbox path="postPartumVisitRecord.postPartumEvents.observationMap[${MCPostPartumEventsConcepts.CORD_NORMAL.conceptId}].valueCoded" label="Baby&apos;s cord OK" /></td></tr>
</table>
</fieldset>

<fieldset><legend>Breastfeeding</legend>
<table id="breastfeeding" class="full-width borderless registration">
	<tr><td><chits_tag:springCheckbox path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.BREASTFED_WITHIN_HOUR.conceptId}].valueCoded" label="Done within one hour after delivery" /></td></tr>
	<tr><td><chits_tag:springCheckbox path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.BREASTFED_EXCLUSIVELY.conceptId}].valueCoded" label="Still EXCLUSIVELY breastfeeding" /></td></tr>
</table>
</fieldset>

<fieldset><legend>Consultation notes:</legend>
<table id="consultation-notes" class="full-width borderless registration">
	<tr><td class="label">Advice given:</td></tr><tr><td><chits_tag:springInput path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.ADVICE_GIVEN.conceptId}].valueText" cssClass="full-width" /></td></tr>
	<tr><td class="label">Personal hygiene:</td></tr><tr><td><chits_tag:springInput path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.HYGIENE_NOTES.conceptId}].valueText" cssClass="full-width" /></td></tr>
	<tr><td class="label">Nutrition:</td></tr><tr><td><chits_tag:springInput path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.NUTRITION_NOTES.conceptId}].valueText" cssClass="full-width" /></td></tr>
	<tr><td class="label">Immunization:</td></tr><tr><td><chits_tag:springInput path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.IMMUNIZATION_NOTES.conceptId}].valueText" cssClass="full-width" /></td></tr>
</table>
</fieldset>

<c:if test="${not form.mcProgramObs.readOnly}">
<fieldset><legend>Family planning method:</legend>
<table id="enroll-in-fp" class="full-width borderless registration">
	<tr><td class="label">
			<input type="checkbox" value="true" name="fpAfterSave" id="fp-after-save" /><label for="fp-after-save">Access method enrollment form</label>
			<br/><br/>
			<c:choose><c:when test="${chits:isInProgram(form.patient, ProgramConcepts.FAMILYPLANNING)}">
				<em style="white-space: normal;">This will take you to the FP method enrollment form after saving</em>
			</c:when><c:otherwise>
				<em style="white-space: normal;">This will take you to the FP registration pages after saving the form</em>
			</c:otherwise></c:choose>
	</td></tr>
</table>
</fieldset>
</c:if>

<fieldset><legend>Checkup Remarks:</legend>
<table id="checkup-remarks" class="full-width borderless registration">
<tr><td><chits_tag:springTextArea path="postPartumVisitRecord.observationMap[${MCPostPartumVisitRecordConcepts.REMARKS.conceptId}].valueText" /></td></tr>
</table>
</fieldset>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>