<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.timeentry/jquery.timeentry.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.timeentry/jquery.timeentry.js" />

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
function cancelSubForm() {
	$j("#subGeneralForm").dialog('close')
}

<c:if test="${not form.mcProgramObs.readOnly}">
$j(document).ready(function() {
	$j('#postpartum-ie-time').timeEntry({spinnerImage:'${pageContext.request.contextPath}/moduleResources/chits/scripts/jquery.timeentry/spinnerText.png',spinnerSize: [30, 20, 8]});
})
</c:if>
</script>

<chits_tag:auditInfo obsGroup="${form.postpartumInternalExaminationRecord.obs}" />

<br />
<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updatePostpartumIEDetails, 'subGeneralForm'); return false;">
<form:hidden path="version" />

<fieldset>
<table id="postpartum-internal-examination-record" class="full-width borderless registration field">
	<tr>
		<td class="label" colspan="4">
		<div style="text-align: center">
			<c:choose><c:when test="${not form.mcProgramObs.readOnly}">
				<chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.POSTDELIVERY_IE.conceptId}].valueCoded" value="${chits:falseConcept()}" label="Routine" />
				<chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.POSTDELIVERY_IE.conceptId}].valueCoded" value="${chits:trueConcept()}" label="Discharge IE" />
			</c:when><c:otherwise>
				<c:choose><c:when test="${form.postpartumInternalExaminationRecord.observationMap[MCPostpartumIERecordConcepts.POSTDELIVERY_IE.conceptId].valueCoded eq chits:trueConcept()}">
				Discharge IE
				</c:when><c:otherwise>
				Routine
				</c:otherwise></c:choose>
			</c:otherwise></c:choose>
		</div>
		</td>
	</tr><tr>
		<td class="label" colspan="2">
			Date*
			<chits_tag:springInput id="postpartum-ie-date" path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.VISIT_DATE.conceptId}].valueText" onclick="showCalendar(this)" />
		</td><td class="label" colspan="2">
			Time
			<chits_tag:springInput id="postpartum-ie-time" path="postpartumInternalExaminationRecord.visitTime" size="10" />
		</td>
	</tr><tr>
		<td class="label">Cervix State</td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.CERVIX_STATE.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.CLOSED)}" label="Closed" /></td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.CERVIX_STATE.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.OPEN)}" label="Open" /></td>
	</tr><tr>
		<td class="label">Uterus</td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.UTERUS.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.FIRM)}" label="Firm" /></td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.UTERUS.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.SOFT)}" label="Soft" /></td>
	</tr><tr>
		<td class="label">Bleeding</td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.BLEEDING.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.MINIMAL)}" label="Mimimal" /></td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.BLEEDING.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.MODERATE)}" label="Moderate" /></td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.BLEEDING.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.SEVERE)}" label="Severe" /></td>
	</tr><tr>
		<td class="label">Vaginal Discharge</td>
	</tr><tr>
		<td></td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.VAGINAL_DISCHARGE.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.NONE)}" label="None" /></td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.VAGINAL_DISCHARGE.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.MODERATE)}" label="Moderate" /></td>
	</tr><tr>
		<td></td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.VAGINAL_DISCHARGE.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.MINIMAL)}" label="Minimal" /></td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.VAGINAL_DISCHARGE.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.SEVERE)}" label="Severe" /></td>
	</tr><tr>
		<td class="label">Wound dehiscence</td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.WOUND_DEHISCENCE.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.PRESENT)}" label="Present" /></td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.WOUND_DEHISCENCE.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.ABSENT)}" label="Absent" /></td>
	</tr><tr>
		<td class="label">Sutures</td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.SUTURES.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.SUTURES_INTACT)}" label="Intact" /></td>
		<td class="label"><chits_tag:springRadiobutton path="postpartumInternalExaminationRecord.observationMap[${MCPostpartumIERecordConcepts.SUTURES.conceptId}].valueCoded" value="${chits:concept(MCIEOptions.SUTURES_NOT_INTACT)}" label="Not intact" /></td>
	</tr>
</table>
</fieldset>

<c:if test="${not form.mcProgramObs.readOnly}">
<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="addButton" value='Add' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelSubForm()" />
</div>
</c:if>
</form:form>

<c:if test="${form.mcProgramObs.readOnly}">
<script>
$j("#postpartum-internal-examination-record *").attr('readonly', 'readonly')
$j("#postpartum-internal-examination-record *").attr('disabled', 'disabled')
</script>
</c:if>