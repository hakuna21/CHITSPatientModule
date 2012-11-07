<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<jsp:include page="fragmentRegistrationFormHeader.jsp" />

<script>
$j(document).ready(function() {
	attachCheckboxMediator('#patient-other', '#patient-other-text')
	attachCheckboxMediator('#family-other', '#family-other-text')
	attachCheckboxMediator('#smoking-history', '#no-of-sticks-per-day')
	attachCheckboxMediator('#illicit-drug-use', '#illicit-drug-use-details')
	attachCheckboxMediator('#alcoholic-drinker', '#alcoholic-drinker-detials');
	
	// attachCheckIfTextChanged('#smoking-history', '#no-of-sticks-per-day')
	// attachCheckIfTextChanged('#smoking-history', '#duration-in-years')
	// attachCheckIfTextChanged('#illicit-drug-use', '#illicit-drug-use-details')
	// attachCheckIfTextChanged('#alcoholic-drinker', '#alcoholic-drinker-detials');
})

function warnIfDangerSignsPresent(form) {
	<%-- Check for any indicated danger signs --%>	
	var dangerSignsCount = 
		$j("#patient-medical-history input[type=checkbox]:checked,"
	 	+ "#family-medical-history input[type=checkbox]:checked,"
	 	+ "#social-medical-history input[type=checkbox]:checked").size()
	if (dangerSignsCount == 0) {
		return true
	}
	
	<%-- Popup warning message about danger signs before allowing to proceed --%>	
	$j("<div><h5>WARNING: Some danger signs have been indicated<br/><br/>The patient needs to be referred after the prenatal checkup.</h5></div>").dialog({
		resizable:true,width:400,height:'auto',modal:true,closeOnEscape:false,
		title:'WARNING: Danger Signs',
		buttons:{
			"OK":function(){$j(this).dialog("close"); pleaseWaitDialog(); form.submit()},
			"Cancel":function(){$j(this).dialog("close")}
		}
	})
	<%-- Danger signs present, don't allow to proceed without warning the user first --%>	
	return false
}
</script>

<br />
<div class="full-width" style="text-align: right">Page 2/2</div>

<form:form id="other-history-registration-form" modelAttribute="form" method="post" onsubmit="pleaseWaitDialog()">
<form:hidden path="version" />
<form:hidden path="page" />

<fieldset>
<legend><span>Menstrual history</span></legend>
<table id="menstrual-history" class="full-width borderless registration">
	<tr>
		<spring:bind path="mcProgramObs.menstrualHistory.observationMap[${MCMenstrualHistoryConcepts.AGE_OF_MENARCHE.conceptId}].valueText">
		<td class="label">Age of Menarche</td>
		<td>
			<form:input path="${status.expression}" size="4" htmlEscape="${true}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		<spring:bind path="mcProgramObs.menstrualHistory.observationMap[${MCMenstrualHistoryConcepts.FLOW.conceptId}].valueText">
		<td class="label" rowspan="4" style="width: 60%;">
			Flow<br/>
			<form:textarea path="${status.expression}" cssStyle="width: 96%" rows="4" />
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	</spring:bind>
	<tr>
		<td class="label">Interval (days)</td>
		<spring:bind path="mcProgramObs.menstrualHistory.observationMap[${MCMenstrualHistoryConcepts.INTERVAL.conceptId}].valueText">
		<td>
			<form:input path="${status.expression}" size="4" htmlEscape="${true}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="mcProgramObs.menstrualHistory.observationMap[${MCMenstrualHistoryConcepts.DURATION.conceptId}].valueText">
		<td class="label">Duration (days)</td>
		<td>
			<form:input path="${status.expression}" size="4" htmlEscape="${true}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="mcProgramObs.menstrualHistory.observationMap[${MCMenstrualHistoryConcepts.DYSMENORRHEA.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="dysmenorrhea" value="${chits:trueConcept()}" /> <label for="dysmenorrhea">Dysmenorrhea</label>
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>Tetanus Status</span></legend>
<table id="tetanus-status" class="full-width borderless registration">
	<tr>
		<td class="label">
			Patient has been given tetanus dose before?*
		</td>
	</tr>
	<tr>
		<spring:bind path="mcProgramObs.menstrualHistory.observationMap[${MCMenstrualHistoryConcepts.GIVEN_TETANUS_DOSE.conceptId}].valueCoded">
		<td class="label">
			<form:radiobutton path="${status.expression}" id="tetanusYes" value="${chits:trueConcept()}" /> <label for="tetanusYes">Yes</label>
			<form:radiobutton path="${status.expression}" id="tetanusNo" value="${chits:falseConcept()}" /> <label for="tetanusNo">No</label>
			<form:radiobutton path="${status.expression}" id="tetanusUnknown" value="${chits:conceptByIdOrName('unknown')}" /> <label for="tetanusUnknown">Unknown</label>
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>Past / Present Medical History</span></legend>
<c:set var="idIndex" value="${0}" />
<table id="patient-medical-history" class="full-width borderless registration">
	<tr>
		<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.HYPERTENSION.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Hypertension</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.ASTHMA.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Bronchial Asthma</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.DIABETES.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Diabetes</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.TUBERCULOSIS.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Tuberculosis</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.HEART_DISEASE.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Heart Disease</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.ALLERGY.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Allergy</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.STI.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">STI</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.BLEEDING_DISORDERS.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Bleeding Disorders</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<td class="label" colspan="2">
			<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.OTHERS.conceptId}].valueCoded">
				<form:checkbox path="${status.expression}" id="patient-other" value="${chits:trueConcept()}" /> <label for="patient-other">Others</label>
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
			<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.OTHERS.conceptId}].valueText">
				<form:input path="${status.expression}" size="12" htmlEscape="${true}" id="patient-other-text" />
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
		<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.THYROID.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Thyroid (goiter)</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>Family Medical History</span></legend>
<table id="family-medical-history" class="full-width borderless registration">
	<tr>
		<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.HYPERTENSION.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Hypertension</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.ASTHMA.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Bronchial Asthma</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.DIABETES.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Diabetes</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.TUBERCULOSIS.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Tuberculosis</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.HEART_DISEASE.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Heart Disease</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.ALLERGY.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Allergy</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.STI.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">STI</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.BLEEDING_DISORDERS.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Bleeding Disorders</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<td class="label" colspan="2">
			<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.OTHERS.conceptId}].valueCoded">
				<form:checkbox path="${status.expression}" id="family-other" value="${chits:trueConcept()}" /> <label for="family-other">Others</label>
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
			<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.OTHERS.conceptId}].valueText">
				<form:input path="${status.expression}" size="12" htmlEscape="${true}" id="family-other-text" />
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
		<spring:bind path="mcProgramObs.familyMedicalHistory.observationMap[${MCMedicalHistoryConcepts.THYROID.conceptId}].valueCoded">
		<td class="label" colspan="2">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Thyroid (goiter)</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>Personal / Social History</span></legend>
<c:set var="idIndex" value="${0}" />
<table id="social-medical-history" class="full-width borderless registration">
	<tr>
		<td class="label" colspan="4">
			<label>Patient Occupation</label>
			<chits_tag:springConcpetIdDropdown path="patient.attributeMap[${MiscAttributes.OCCUPATION}].value" answers="${chits:answers(OccupationConcepts.OCCUPATION_MEMBERS)}" select="Select Occupation" />
		</td>
	</tr>
	<tr>
		<td class="label">
			<spring:bind path="mcProgramObs.personalHistory.observationMap[${MCPersonalHistoryConcepts.SMOKING_HISTORY.conceptId}].valueCoded">
				<form:checkbox path="${status.expression}" id="smoking-history" value="${chits:trueConcept()}" />			
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
		<td>
			<label for="smoking-history">Smoker</label>
		</td>
		<td class="label">
			<label for="no-of-sticks-per-day">No. of sticks/day:</label>
			<spring:bind path="mcProgramObs.personalHistory.observationMap[${MCPersonalHistoryConcepts.SMOKING_STICKS_PER_DAY.conceptId}].valueText">
				<form:input path="${status.expression}" size="1" cssStyle="width: 1em;" id="no-of-sticks-per-day" htmlEscape="${true}" />
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
		<td class="label">
			<label for="duration-in-years">Duration (in years):</label>
			<spring:bind path="mcProgramObs.personalHistory.observationMap[${MCPersonalHistoryConcepts.SMOKING_YEARS.conceptId}].valueText">
				<form:input path="${status.expression}" size="1" cssStyle="width: 1em;" id="duration-in-years" htmlEscape="${true}" />
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td class="label">
			<spring:bind path="mcProgramObs.personalHistory.observationMap[${MCPersonalHistoryConcepts.ALCOHOLIC_INTAKE.conceptId}].valueCoded">
				<form:checkbox path="${status.expression}" id="alcoholic-drinker" value="${chits:trueConcept()}" />			
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
		<td class="label" colspan="3">
			<label for="alcoholic-drinker">Alcoholic drinker</label>
		</td>
	</tr>
	<tr>
		<td></td>
		<td class="label" colspan="3">
			<spring:bind path="mcProgramObs.personalHistory.observationMap[${MCPersonalHistoryConcepts.ALCOHOLIC_INTAKE_DETAILS.conceptId}].valueText">
				Frequency of drinking / Type
				<form:input path="${status.expression}" id="alcoholic-drinker-detials" htmlEscape="${true}" />
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td class="label">
			<spring:bind path="mcProgramObs.personalHistory.observationMap[${MCPersonalHistoryConcepts.ILLICIT_DRUG_USE.conceptId}].valueCoded">
				<form:checkbox path="${status.expression}" id="illicit-drug-use" value="${chits:trueConcept()}" />			
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
		<td class="label" colspan="3">
			<label for="illicit-drug-use">Illicit drugs</label>
		</td>
	</tr>
	<tr>
		<td></td>
		<td class="label" colspan="3">
			<spring:bind path="mcProgramObs.personalHistory.observationMap[${MCPersonalHistoryConcepts.ILLICIT_DRUG_USE_DETAILS.conceptId}].valueText">
				Drugs taken:
				<form:input path="${status.expression}" id="illicit-drug-use-details" htmlEscape="${true}" />
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>Remarks</span></legend>
<table id="medical-history-remarks" class="full-width borderless registration">
	<tr>
		<spring:bind path="mcProgramObs.patientMedicalHistory.observationMap[${MCMedicalHistoryConcepts.REMARKS.conceptId}].valueText">
		<td class="label">
			<form:textarea path="${status.expression}" cssStyle="width: 96%" rows="5" />
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>
</fieldset>

<br/>
<table id="patient-needs-to-see-physician" class="full-width borderless registration">
	<tr>
		<spring:bind path="mcProgramObs.needsToSeePhysician">
		<td class="label">
			<form:checkbox path="${status.expression}" id="needsToSeePhysician" value="${chits:trueConcept()}" />
		</td><td>
			<label for="needsToSeePhysician">PATIENT NEEDS TO SEE PHYSICIAN</label>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="label">
			<span class="alert">All newly registered patients are required to see the physician.</span>
		</td>
	</tr>
</table>

<br/>
<div class="full-width" style="text-align: right">
<input type="button" id="cancelButton" value='Cancel' onclick="document.location.href='viewMaternalCareProgram.form?patientId=${form.patient.patientId}'" />
<input type="submit" id="saveButton" value='Save' onclick="return warnIfDangerSignsPresent(this.form);" />
</div>
</form:form>