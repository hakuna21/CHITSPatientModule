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
	attachCheckboxMediator('#referred-other', '#referred-other-text')
})
</script>

<br/>
<form:form id="risk-factors-registration-form" modelAttribute="form" method="post" cssClass="main-form">

<fieldset><legend><span><c:choose
	><c:when test="${not empty form.page}">Page 3: RISK FACTORS</c:when
	><c:otherwise>UPDATE RISK FACTORS</c:otherwise></c:choose
></span></legend>
<form:hidden path="version" />
<form:hidden path="page" />

<table class="full-width borderless registration field">
<tr>
	<td>
		<fieldset><legend>STI RISK</legend>
		<table>
		<c:choose><c:when test="${'F' eq form.patient.gender}">
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.MULTIPLE_PARTNERS.conceptId}].valueCoded" label="With history of multiple partners" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.VAGINAL_DISCHARGE.conceptId}].valueCoded" label="Unusual discharge from vagina" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.VAGINAL_ITCHING.conceptId}].valueCoded" label="Itching or soreness in or around vagina" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.BURNING_SENSATION.conceptId}].valueCoded" label="Pain or burning sensation" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.HISTORY_OF_STI_TREATMENT.conceptId}].valueCoded" label="Treated for STI in the past" /></td></tr>
		</c:when><c:otherwise>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.MULTIPLE_PARTNERS.conceptId}].valueCoded" label="With history of multiple partners" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.BURNING_SENSATION.conceptId}].valueCoded" label="Pain or burning sensation" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.GENITAL_SORES.conceptId}].valueCoded" label="Open sores anywhere in genital area" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.PENILE_DISCHARGE.conceptId}].valueCoded" label="Pus coming from penis" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.GENITAL_SWELLING.conceptId}].valueCoded" label="Swollen testicles or penis" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.HISTORY_OF_STI_TREATMENT.conceptId}].valueCoded" label="Treated for STI in the past" /></td></tr>
		</c:otherwise></c:choose>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend>RISKS FOR VIOLENCE</legend>
		<table>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.DOMESTIC_VIOLENCE.conceptId}].valueCoded" label="History of domestic violence" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.UNPLEASANT_RELATINOSHIP.conceptId}].valueCoded" label="Unpleasant relationship with partner" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.PARTNER_DISAPPROVAL_VISIT.conceptId}].valueCoded" label="Partner does not approve of the visit to FP clinic" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.PARTNER_DISAPPROVAL_FP.conceptId}].valueCoded" label="Partner disagrees to use FP" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend>Referred to (check all that apply)</legend>
		<table>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.DSWD.conceptId}].valueCoded" label="DSWD" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.WCPU.conceptId}].valueCoded" label="WCPU" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.NGO.conceptId}].valueCoded" label="NGO" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.SOCIAL_HYGIENE_CLINIC.conceptId}].valueCoded" label="Social Hygiene Clinic" /></td></tr>
			<tr>
				<td class="label">
					<chits_tag:springCheckbox path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.OTHERS.conceptId}].valueCoded" label="Others (specify)" id="referred-other" />
					<chits_tag:springInput path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.OTHERS.conceptId}].valueText" id="referred-other-text" />
				</td>
			</tr>
			<tr><td class="label">Date referred (mm/dd/yyyy): <chits_tag:springInput path="fpProgramObs.riskFactors.observationMap[${FPRiskFactorsConcepts.DATE_REFERRED.conceptId}].valueText" onclick="showCalendar(this)" /></td></tr>
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