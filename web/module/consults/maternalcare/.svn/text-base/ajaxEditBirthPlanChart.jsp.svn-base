<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

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

$j(document).ready(function() {
	attachCheckboxMediator('#mothers-needs-other', '#mothers-needs-other-text')
	attachCheckboxMediator('#childs-needs-other', '#childs-needs-other-text')
	attachCheckboxMediator('#expenses-other', '#expenses-other-text')
	attachCheckboxMediator('#expenses-newborn-screening', '#expenses-newborn-screening-text')
	attachCheckboxMediator('#expenses-birth-registration', '#expenses-birth-registration-text')
	attachCheckboxMediator('#expenses-delivery', '#expenses-delivery-text')
	attachCheckboxMediator('#expenses-medicine', '#expenses-medicine-text')
	attachCheckboxMediator('#expenses-transportation', '#expenses-transportation-text')
})
</script>

<c:set var="registeredState" value="${chits:findPatientProgramState(form.mcProgramObs.patientProgram, MaternalCareProgramStates.REGISTERED)}" />
<c:set var="createdByObs" value="${chits:observation(form.mcProgramObs.birthPlanChart.obs, AuditConcepts.CREATED_BY)}" />
<c:set var="modifiedByObs" value="${chits:observation(form.mcProgramObs.birthPlanChart.obs, AuditConcepts.MODIFIED_BY)}" />

<table class="form full-width">
<tbody>
	<tr>
		<td style="width: 35%">MCP Registration No. <fmt:formatNumber pattern="0000000" value="${registeredState.id}" /></td>
		<td style="width: 35%"><c:if test="${not empty createdByObs.valueDatetime}">First filled out on <fmt:formatDate pattern="MMM d, yyyy" value="${createdByObs.valueDatetime}" /></c:if></td>
		<td style="width: 30%"><c:if test="${not empty createdByObs.valueDatetime}">by ${createdByObs.creator.person.personName}</c:if></td>
	</tr><tr>
		<td>Registration Date: <fmt:formatDate pattern="MMM d, yyyy" value="${registeredState.startDate}" /></td>
		<td><c:if test="${not empty modifiedByObs.valueDatetime}">Last updated on <fmt:formatDate pattern="MMM d, yyyy" value="${modifiedByObs.valueDatetime}" /></c:if></td>
		<td><c:if test="${not empty modifiedByObs.valueDatetime}">by ${modifiedByObs.creator.person.personName}</c:if></td>
	</tr>
</tbody>
</table>

<br />
<form:form modelAttribute="form" id="birth-plan-chart-form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updateMCSection); return false;">
<form:hidden path="version" />

<fieldset><legend><span>BIRTH PLAN CHART</span></legend>
<table><tr><td style="width: 50%; vertical-align: top">
	<table id="birth-plan-chart" class="full-width borderless registration">
		<tr>
			<td class="label">Mother's name</td>
			<td>${form.patient.personName}</td>
			<td style="white-space: nowrap;">Age: <chits:age birthdate="${form.patient.birthdate}" on="${form.mcProgramObs.estimatedDateOfConfinement}" /></td>
		</tr><tr>
			<td class="label">Father's name</td>
			<td><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.FATHERS_NAME.conceptId}].valueText" /></td>
			<td style="white-space: nowrap;">Age: <chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.FATHERS_AGE.conceptId}].valueText" size="2" /></td>
		</tr><tr>
			<td class="label">Name of child</td>
			<td><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.CHILDS_NAME.conceptId}].valueText" /></td>
			<td class="label" style="white-space: nowrap;">Gravida: <chits_tag:obstetricScore obsGroup="${form.mcProgramObs.obstetricHistory.obs}" gravidaOnly="${true}" /></td>
		</tr><tr>
			<td class="label" colspan="3">Complete address: <chits_tag:familyAddress /><br/>&nbsp;</td>
		</tr><tr>
			<td class="label">Email address</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.EMAIL_ADDRESS.conceptId}].valueText" /></td>
		</tr><tr>
			<td class="label">Telephone Number</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.CONTACT_PHONE.conceptId}].valueText" /></td>
		</tr><tr>
			<td class="label">Cellphone number</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.CONTACT_CELL.conceptId}].valueText" /></td>
		</tr><tr>
			<td class="label">Name of planned birth attendant</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.BIRTH_ATTENDANT.conceptId}].valueText" /></td>
		</tr><tr>
			<td class="label">Intended place for delivery</td>
			<td colspan="2"><chits_tag:springDropdown path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.DELIVERY_LOCATION.conceptId}].valueCoded" answers="${chits:answers(MCBirthPlanConcepts.DELIVERY_LOCATION)}" /></td>
		</tr><tr>
			<td class="label">Intended mode of transporation</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.MODE_OF_TRANSPO.conceptId}].valueText" /></td>
		</tr><tr>
			<td class="label">Intended companion's name</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.DELIVERY_COMPANION.conceptId}].valueText" /></td>
		</tr><tr>
			<td class="label">Person tasked to take care of home</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.STAY_AT_HOME.conceptId}].valueText" /></td>
		</tr><tr>
			<td class="label">Intended blood donor's name</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.BLOOD_DONOR.conceptId}].valueText" /></td>
		</tr><tr>
			<td class="label">Person to contact in case of emergency</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.EMERGENCY_PERSON.conceptId}].valueText" /></td>
		</tr><tr>
			<td class="label">Telephone Number</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.EMERGENCY_PHONE.conceptId}].valueText" /></td>
		</tr><tr>
			<td class="label">Cellphone Number</td>
			<td colspan="2"><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.EMERGENCY_CELL.conceptId}].valueText" /></td>
		</tr>
	</table>
</td><td style="width: 50%; vertical-align: top">
	<fieldset><legend>Mother's needs</legend>
	<table id="mothers-needs" class="full-width borderless registration">
		<tr>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.mothersNeeds.observationMap[${MCMothersNeedsConcepts.SANITARY_NAPKIN.conceptId}].valueCoded" label="Sanitary napkin" /></td>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.mothersNeeds.observationMap[${MCMothersNeedsConcepts.CASH.conceptId}].valueCoded" label="Extra cash" /></td>
		</tr><tr>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.mothersNeeds.observationMap[${MCMothersNeedsConcepts.ALCOHOL.conceptId}].valueCoded" label="Alcohol" /></td>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.mothersNeeds.observationMap[${MCMothersNeedsConcepts.TOWEL.conceptId}].valueCoded" label="Towel" /></td>
		</tr><tr>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.mothersNeeds.observationMap[${MCMothersNeedsConcepts.CLOTHES.conceptId}].valueCoded" label="Extra clothes" /></td>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.mothersNeeds.observationMap[${MCMothersNeedsConcepts.SANDALS.conceptId}].valueCoded" label="Sandals" /></td>
		</tr><tr>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.mothersNeeds.observationMap[${MCMothersNeedsConcepts.BLANKET.conceptId}].valueCoded" label="Blanket" /></td>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.mothersNeeds.observationMap[${MCMothersNeedsConcepts.TOILET_PAPER.conceptId}].valueCoded" label="Toilet Paper" /></td>
		</tr><tr>
			<td style="white-space: nowrap;" colspan="2">
				<chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.mothersNeeds.observationMap[${MCMothersNeedsConcepts.OTHER.conceptId}].valueCoded" label="Others (please specify)" id="mothers-needs-other" />
				<chits_tag:springInput path="mcProgramObs.birthPlanChart.mothersNeeds.observationMap[${MCMothersNeedsConcepts.OTHER.conceptId}].valueText" id="mothers-needs-other-text" />
			</td>
		</tr>
	</table>
	</fieldset>

	<fieldset><legend>Baby's needs</legend>
	<table id="childs-needs" class="full-width borderless registration">
		<tr>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.childsNeeds.observationMap[${MCChildsNeedsConcepts.CLOTHES.conceptId}].valueCoded" label="Baby clothes" /></td>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.childsNeeds.observationMap[${MCChildsNeedsConcepts.MITTENS.conceptId}].valueCoded" label="Mittens" /></td>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.childsNeeds.observationMap[${MCChildsNeedsConcepts.BLANKET.conceptId}].valueCoded" label="Blanket" /></td>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.childsNeeds.observationMap[${MCChildsNeedsConcepts.SOAP.conceptId}].valueCoded" label="Soap" /></td>
		</tr><tr>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.childsNeeds.observationMap[${MCChildsNeedsConcepts.DIAPER.conceptId}].valueCoded" label="Diaper" /></td>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.childsNeeds.observationMap[${MCChildsNeedsConcepts.BOTTLE.conceptId}].valueCoded" label="Bottles" /></td>
			<td><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.childsNeeds.observationMap[${MCChildsNeedsConcepts.SAFETY_PIN.conceptId}].valueCoded" label="Safety pin" /></td>
		</tr><tr>
			<td colspan="4" style="white-space: nowrap;">
				<chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.childsNeeds.observationMap[${MCChildsNeedsConcepts.OTHER.conceptId}].valueCoded" label="Others (please specify)" id="childs-needs-other" />
				<chits_tag:springInput path="mcProgramObs.birthPlanChart.childsNeeds.observationMap[${MCChildsNeedsConcepts.OTHER.conceptId}].valueText" id="childs-needs-other-text" />
			</td>
		</tr>
	</table>
	</fieldset>

	<fieldset><legend>Birth expenses (indicate amount saved per field provided)</legend>
	<table id="birth-expenses" class="full-width borderless registration">
		<tr>
			<td style="white-space: nowrap;"><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.NEWBORN_SCREENING.conceptId}].valueCoded" label="Newborn Screening" id="expenses-newborn-screening" /></td>
			<td><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.NEWBORN_SCREENING_COSTS.conceptId}].valueText" id="expenses-newborn-screening-text" /></td>
		</tr><tr>
			<td style="white-space: nowrap;"><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.BIRTH_REGISTRATION.conceptId}].valueCoded" label="Birth Registration" id="expenses-birth-registration" /></td>
			<td><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.BIRTH_REGISTRATION_COSTS.conceptId}].valueText" id="expenses-birth-registration-text" /></td>
		</tr><tr>
			<td style="white-space: nowrap;"><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.DELIVERY.conceptId}].valueCoded" label="Payment for childbirth" id="expenses-delivery" /></td>
			<td><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.DELIVERY_COSTS.conceptId}].valueText" id="expenses-delivery-text" /></td>
		</tr><tr>
			<td style="white-space: nowrap;"><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.MEDICINE.conceptId}].valueCoded" label="Medicine" id="expenses-medicine" /></td>
			<td><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.MEDICINE_COSTS.conceptId}].valueText" id="expenses-medicine-text" /></td>
		</tr><tr>
			<td style="white-space: nowrap;"><chits_tag:springCheckbox path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.TRANSPORTATION.conceptId}].valueCoded" label="Transportation" id="expenses-transportation" /></td>
			<td><chits_tag:springInput path="mcProgramObs.birthPlanChart.observationMap[${MCBirthPlanConcepts.TRANSPORTATION_COSTS.conceptId}].valueText" id="expenses-transportation-text" /></td>
		</tr>
	</table>
	</fieldset>
</td></table>
</fieldset>

<c:if test="${not form.mcProgramObs.readOnly}">
<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</c:if>
</form:form>

<c:if test="${form.mcProgramObs.readOnly}">
<script>
$j("#birth-plan-chart-form *").attr('readonly', 'readonly')
$j("#birth-plan-chart-form *").attr('disabled', 'disabled')
</script>
</c:if>