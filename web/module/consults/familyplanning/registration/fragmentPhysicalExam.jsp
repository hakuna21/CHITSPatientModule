<%@ page buffer="128kb"
%><%@ page import="java.util.Date"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:choose><c:when test="${not empty form.page}">
	<jsp:include page="fragmentRegistrationFormHeader.jsp" />
</c:when><c:otherwise>
	<jsp:include page="../fragmentAjaxUpdateHeader.jsp" />
</c:otherwise></c:choose>

<style>
table.breast-examination th, table.breast-examination td { background: #f8f8f8; text-align: center; }
table.breast-examination th.label,
table.breast-examination td.label { background: none; text-align: left; white-space: normal; }
</style>

<br/>
<form:form id="physical-examination-registration-form" modelAttribute="form" method="post" cssClass="main-form">

<fieldset><legend><span><c:choose
	><c:when test="${not empty form.page}">Page 5: PHYSICAL EXAMINATION</c:when
	><c:otherwise>UPDATE PHYSICAL EXAMINATION</c:otherwise></c:choose
></span></legend>
<form:hidden path="version" />
<form:hidden path="page" />

<table class="full-width borderless registration field">
<tr>
	<td>
		<fieldset>
		<table class="full-width registration field"><thead>
			<tr><th>CONJUNCTIVA</th><th>NECK</th></tr>
		</thead><tbody>
			<tr><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.PALE_CONJUNCTIVA.conceptId}].valueCoded" label="Pale" /></td><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.ENLARGED_THYROID.conceptId}].valueCoded" label="Enlarged Thyroid" /></td></tr>
			<tr><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.YELOWISH_CONJUNCTIVA.conceptId}].valueCoded" label="Yellowish" /></td><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.ENLARGED_LYMPH_NODES.conceptId}].valueCoded" label="Enlarged Lymph Nodes" /></td></tr>
		</tbody></table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>BREAST</span> (Please check all that apply)</legend>
		<table class="full-width registration field breast-examination"><thead>
			<tr><th class="label">&nbsp;</th><th colspan="2">Left Breast</th><th colspan="2">Right Breast</th></tr>
			<tr><th class="label">&nbsp;</th><th>OUTER</th><th>INNER</th><th>INNER</th><th>OUTER</th></tr>
		</thead><tbody>
			<tr><td class="label">Mass in Upper Lobe</td><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.MASS_UL_OLB.conceptId}].valueCoded" /></td><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.MASS_UL_ILB.conceptId}].valueCoded" /></td><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.MASS_UL_IRB.conceptId}].valueCoded" /></td><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.MASS_UL_ORB.conceptId}].valueCoded" /></td></tr>
			<tr><td class="label">Mass in Lower Lobe</td><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.MASS_LL_OLB.conceptId}].valueCoded" /></td><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.MASS_LL_ILB.conceptId}].valueCoded" /></td><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.MASS_LL_IRB.conceptId}].valueCoded" /></td><td><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.MASS_LL_ORB.conceptId}].valueCoded" /></td></tr>
			<tr><td class="label">Description of mass</td><td colspan="2"><chits_tag:springTextArea path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.MASS_ON_LEFT_DESCR.conceptId}].valueText" rows="${5}" /></td><td colspan="2"><chits_tag:springTextArea path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.MASS_ON_RIGHT_DESCR.conceptId}].valueText" rows="${5}" /></td></tr>
			<tr><td class="label">Nipple discharge</td><td colspan="2"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.NIPPLE_DISCHARGE_LB.conceptId}].valueCoded" /></td><td colspan="2"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.NIPPLE_DISCHARGE_RB.conceptId}].valueCoded" /></td></tr>
			<tr><td class="label">Skin-orange-peel or dimpling</td><td colspan="2"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.DIMPLING_LEFT.conceptId}].valueCoded" /></td><td colspan="2"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.DIMPLING_RIGHT.conceptId}].valueCoded" /></td></tr>
			<tr><td class="label">Enlarged Axillary Lymph Nodes</td><td colspan="2"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.ENLARGED_AXILLARY_LEFT_LYMPH_NODES.conceptId}].valueCoded" /></td><td colspan="2"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.ENLARGED_AXILLARY_RIGHT_LYMPH_NODES.conceptId}].valueCoded" /></td></tr>
		</tbody></table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>THORAX</span></legend>
		<table class="full-width registration field">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.ABNORMAL_HEART_SOUNDS.conceptId}].valueCoded" label="Abnormal Heart Sounds / Cardiac Rate" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.ABNORMAL_BREATH_SOUNDS.conceptId}].valueCoded" label="Abnormal Breath Sounds / Respiratory Rate" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>ABDOMEN</span></legend>
		<table class="full-width registration field">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.ENLARGED_LIVER.conceptId}].valueCoded" label="Enlarged Liver" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.ABDOMINAL_MASS.conceptId}].valueCoded" label="Mass" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.ABDOMINAL_TENDERNESS.conceptId}].valueCoded" label="Tenderness" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>EXTREMITIES</span></legend>
		<table class="full-width registration field">
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.EDEMA.conceptId}].valueCoded" label="Edema" /></td></tr>
		<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.VARICOSITIES.conceptId}].valueCoded" label="Varicosities" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend><span>OTHERS</span></legend>
		<table class="full-width registration field">
		<tr><td class="label"><chits_tag:springTextArea path="fpProgramObs.physicalExamination.observationMap[${FPPhysicalExaminationConcepts.OTHERS.conceptId}].valueText" rows="${5}" /></td></tr>
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