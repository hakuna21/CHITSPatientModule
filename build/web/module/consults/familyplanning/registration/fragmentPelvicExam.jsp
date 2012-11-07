<%@ page buffer="128kb"
%><%@ page import="java.util.Date"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:choose><c:when test="${not empty form.page}">
	<jsp:include page="fragmentRegistrationFormHeader.jsp" />
</c:when><c:otherwise>
	<jsp:include page="../fragmentAjaxUpdateHeader.jsp" />
</c:otherwise></c:choose>

<script>
$j(document).ready(function() {
	attachCheckboxMediator('#vaginal-discharge', '#vaginal-discharge-text')
	attachCheckboxMediator('#cervical-discharge', '#cervical-discharge-text')

	checkboxesAsRadios('#cervix-color-options')
	checkboxesAsRadios('#cervix-consistency-options')
	checkboxesAsRadios('#cervix-position-options')
	checkboxesAsRadios('#uterus-size-options')
	
	$j('#adnexa-tenderness, #adnexa-mass').click(function() {
		if ($j(this).is(":checked")) {
			$j('#adnexa-normal').removeAttr('checked')
		}
	})

	$j('#adnexa-normal').click(function() {
		if ($j(this).is(":checked")) {
			$j('#adnexa-tenderness, #adnexa-mass').removeAttr('checked')
		}
	})
})
</script>

<br/>
<form:form id="pelvic-examination-registration-form" modelAttribute="form" method="post" cssClass="main-form">

<fieldset><legend><span><c:choose
	><c:when test="${not empty form.page}">Page 6: PELVIC EXAMINATION</c:when
	><c:otherwise>UPDATE PELVIC EXAMINATION</c:otherwise></c:choose
></span></legend>
<form:hidden path="version" />
<form:hidden path="page" />

<table class="full-width borderless registration field">
<tr>
	<td>
		<fieldset><legend><span>PERINEUM</span></legend>
		<table class="full-width registration field">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.SCARS.conceptId}].valueCoded" label="Scars" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.PERINEUM_WARTS.conceptId}].valueCoded" label="Warts" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.REDDISH.conceptId}].valueCoded" label="Reddish" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.PERINEUM_LACERATION.conceptId}].valueCoded" label="Laceration" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>VAGINA</span></legend>
		<table class="full-width registration field">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.VAGINA_CONGESTED.conceptId}].valueCoded" label="Congested" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.BARTHOLINS_CYST.conceptId}].valueCoded" label="Bartholin's cyst" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.VAGINA_WARTS.conceptId}].valueCoded" label="Warts" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.SKENES_GLAND.conceptId}].valueCoded" label="Skene's Gland" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.VAGINAL_DISCHARGE.conceptId}].valueCoded" label="Discharge" id="vaginal-discharge" /> <chits_tag:springInput path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.VAGINAL_DISCHARGE.conceptId}].valueText" id="vaginal-discharge-text" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.VAGINAL_RECTOCOELE.conceptId}].valueCoded" label="Rectocoele" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.CYTOCOELE.conceptId}].valueCoded" label="Cytocoele" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>CERVIX</span></legend>
		<table class="full-width registration field">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.CERVIX_CONGESTED.conceptId}].valueCoded" label="Congested" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.ERODED.conceptId}].valueCoded" label="Eroded" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.CERVICAL_DISCHARGE.conceptId}].valueCoded" label="Discharge" id="cervical-discharge" /> <chits_tag:springInput path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.CERVICAL_DISCHARGE.conceptId}].valueText" id="cervical-discharge-text" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.POLYPS.conceptId}].valueCoded" label="Polyps/Cyst" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.CERVICAL_LACERATION.conceptId}].valueCoded" label="Laceration" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>Color</span></legend>
		<table class="full-width registration field" id="cervix-color-options">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.CERVIX_PINKISH.conceptId}].valueCoded" label="Pinkish" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.CERVIX_BLUISH.conceptId}].valueCoded" label="Bluish" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>Consistency</span></legend>
		<table class="full-width registration field" id="cervix-consistency-options">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.CERVIX_FIRM.conceptId}].valueCoded" label="Firm" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.CERVIX_SOFT.conceptId}].valueCoded" label="Soft" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>UTERUS POSITION</span></legend>
		<table class="full-width registration field" id="cervix-position-options">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.UTERUS_MID.conceptId}].valueCoded" label="Mid" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.UTERUS_ANTEFLEXED.conceptId}].valueCoded" label="Anteflexed" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.UTERUS_RETROFLEXED.conceptId}].valueCoded" label="Retroflexed" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>UTERUS SIZE</span></legend>
		<table class="full-width registration field" id="uterus-size-options">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.NORMAL_UTERUS.conceptId}].valueCoded" label="Normal" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.SMALL_UTERUS.conceptId}].valueCoded" label="Small" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.LARGE_UTERUS.conceptId}].valueCoded" label="Large" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>UTERUS MASS</span></legend>
		<table class="full-width registration field">
		<tr><td class="label">Uterine Depth (for Intended IUD Users, in cm.) <chits_tag:springInput path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.UTERINE_DEPTH.conceptId}].valueText" /></td></tr>
		<tr><td><div class="alert">NOTE: IUD cannot be inserted for uterine depth of less than 6cm and more than 8cm</div></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>ADNEXA</span></legend>
		<table class="full-width registration field">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.NORMAL_ADNEXA.conceptId}].valueCoded" label="Normal" id="adnexa-normal" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.ADNEXA_WITH_MASSES.conceptId}].valueCoded" label="Mass" id="adnexa-mass" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.ADNEXA_WITH_TENDERNESS.conceptId}].valueCoded" label="Tenderness" id="adnexa-tenderness" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>OTHERS</span></legend>
		<table class="full-width registration field">
		<tr><td class="label"><chits_tag:springTextArea path="fpProgramObs.pelvicExamination.observationMap[${FPPelvicExaminationConcepts.OTHERS.conceptId}].valueText" rows="${5}" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr>
</table>
</fieldset>

<c:if test="${not empty form.page}">
	<jsp:include page="fragmentWillSeePhysician.jsp" />
	<jsp:include page="fragmentRegistrationFormFooter.jsp" />
</c:if>

<br/>
<div class="full-width" style="text-align: right">
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
<c:choose><c:when test="${not empty form.page}">
<input type="submit" id="saveButton" value='Next Page' />
</c:when><c:otherwise>
<input type="submit" id="saveButton" value='Save' />
</c:otherwise></c:choose>
</div>
</form:form>