<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />
<style>
#prenatal-leopolds-maneuver-findings td.label,
#prenatal-obstetric-examination td.label { width: 15em; }
#prenatal-new-medical-history td,
#danger-signs td { width: 50%; }
</style>

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

function submitForm(form) {
	return submitAjaxForm(form, ${form.patient.patientId}, updateMCSection);
}

function validateAndSubmit(form) {
	if ($j("#prenatal-new-medical-history input[type=checkbox]:checked, #danger-signs input[type=checkbox]:checked").size() > 0) {
		if (!$j("#needsToSeePhysician").is(":checked")) {
			<%-- Popup warning message about danger signs being present and must see physician flag not ticked --%>	
			$j("<div><h5>WARNING: Some danger signs have been indicated but 'THE PATIENT NEEDS TO SEE PHYSICIAN' checkbox has not been ticked."
					+ "<br/><br/>Are you sure you want to proceed without the patient seeing the physician?</h5></div>").dialog({
				resizable:true,width:400,height:'auto',modal:true,closeOnEscape:false,
				title:'WARNING: Danger Signs',
				buttons:{
					"Yes":function(){$j(this).dialog("close"); pleaseWaitDialog(); submitForm(form)},
					"Cancel":function(){$j(this).dialog("close")}
				}
			})
			
			return
		}
	}
	
	// go ahead and submit...
	submitForm(form)
}

$j(document).ready(function() {
	attachCheckboxMediator("#patient-other", "#patient-other-text")
	$j("#prenatal-new-medical-history input[type=checkbox], #danger-signs input[type=checkbox]").change(function() {
		if ($j(this).is(":checked")) {
			$j("#needsToSeePhysician").attr("checked", "checked")
		}
	})
})
</script>

<chits_tag:auditInfo obsGroup="${form.prenatalVisitRecord.obs}" />

<br />
<form:form modelAttribute="form" method="post" onsubmit="validateAndSubmit(this); return false;">
<form:hidden path="version" />
<table id="prenatal-visit-record" class="full-width borderless registration">
	<tr>
		<td>
			<table class="field full-width"><tr><td class="label">Prenatal Visit Date*</td>
			<td><spring:bind path="prenatalVisitRecord.observationMap[${MCPrenatalVisitRecordConcepts.VISIT_DATE.conceptId}].valueText">
			<form:input path="${status.expression}" id="prenatalVisitDate" htmlEscape="${true}" onclick="showCalendar(this)" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind></td>
			</tr>
			
			<tr>
			<td class="label">Visit type:*</td>
			<td><spring:bind path="prenatalVisitRecord.observationMap[${MCPrenatalVisitRecordConcepts.VISIT_TYPE.conceptId}].valueCoded">
			<form:select path="${status.expression}">
				<form:option value="">select</form:option>
				<c:forEach var="answer" items="${chits:answers(MCPrenatalVisitRecordConcepts.VISIT_TYPE)}">
				<form:option value="${answer.conceptId}">${answer.name}</form:option></c:forEach>
			</form:select>
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind></td>
			</tr>
			
			<tr><td class="label">Nutritionally at risk:</td>
			<td class="field"><spring:bind path="prenatalVisitRecord.observationMap[${MCPrenatalVisitRecordConcepts.NUTRITIONALLY_AT_RISK.conceptId}].valueCoded">
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
				<form:radiobutton path="${status.expression}" id="NARYes" value="${chits:trueConcept()}" /> <label for="NARYes">Yes</label>
				<form:radiobutton path="${status.expression}" id="NARNo" value="${chits:falseConcept()}" /> <label for="NARNo">No</label>
			</spring:bind></td>
			</tr>
		</td>
	</tr>
</table>

<fieldset><legend><span>Obstetric examination:</span></legend>
<table id="prenatal-obstetric-examination" class="full-width borderless registration">
	<tr>
		<td class="label">Fundic height (cm)</td>
		<spring:bind path="prenatalVisitRecord.obstetricExamination.observationMap[${MCObstetricExamination.FUNDIC_HEIGHT.conceptId}].valueText">
		<td>
			<form:input path="${status.expression}" htmlEscape="${true}" size="2" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<td class="label">FHR (beats/min)</td>
		<spring:bind path="prenatalVisitRecord.obstetricExamination.observationMap[${MCObstetricExamination.FHR.conceptId}].valueText">
		<td>
			<form:input path="${status.expression}" htmlEscape="${true}" size="2" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<td class="label">FHR Location</td>
		<td><chits_tag:springDropdown path="prenatalVisitRecord.obstetricExamination.observationMap[${MCObstetricExamination.FHR_LOCATION.conceptId}].valueCoded" select="not applicable" answers="${chits:answers(MCObstetricExamination.FHR_LOCATION)}" shortName="${true}" /></td>
	</tr>
	<tr>
		<td class="label">Fetal Presentation</td>
		<spring:bind path="prenatalVisitRecord.obstetricExamination.observationMap[${MCObstetricExamination.FETAL_PRESENTATION.conceptId}].valueCoded">
		<td>
			<form:select path="${status.expression}">
				<form:option value="">not applicable</form:option>
				<c:forEach var="answer" items="${chits:answers(MCObstetricExamination.FETAL_PRESENTATION)}">
				<form:option value="${answer.conceptId}">${answer.name}</form:option></c:forEach>
			</form:select>
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>
</fieldset>

<fieldset><legend><span>Leopold's Maneuver Findings:</span></legend>
<table id="prenatal-leopolds-maneuver-findings" class="full-width borderless registration">
	<tr>
		<td class="label">Fundal Grip</td>
		<spring:bind path="prenatalVisitRecord.obstetricExamination.observationMap[${MCObstetricExamination.FUNDAL_GRIP.conceptId}].valueText">
		<td>
			<form:input path="${status.expression}" htmlEscape="${true}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<td class="label">Umbilical Grip</td>
		<spring:bind path="prenatalVisitRecord.obstetricExamination.observationMap[${MCObstetricExamination.UMBILICAL_GRIP.conceptId}].valueText">
		<td>
			<form:input path="${status.expression}" htmlEscape="${true}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<td class="label">Pawlick's Grip</td>
		<spring:bind path="prenatalVisitRecord.obstetricExamination.observationMap[${MCObstetricExamination.PAWLICKS_GRIP.conceptId}].valueText">
		<td>
			<form:input path="${status.expression}" htmlEscape="${true}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<td class="label">Pelvic Grip</td>
		<spring:bind path="prenatalVisitRecord.obstetricExamination.observationMap[${MCObstetricExamination.PELVIC_GRIP.conceptId}].valueText">
		<td>
			<form:input path="${status.expression}" htmlEscape="${true}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>Danger Signs</span></legend>
<c:set var="idIndex" value="${0}" />
<table id="danger-signs" class="full-width borderless registration">
	<tr>
		<spring:bind path="prenatalVisitRecord.dangerSigns.observationMap[${MCDangerSignsConcepts.SEVERE_HEADACHE.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Severe Headache</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="prenatalVisitRecord.dangerSigns.observationMap[${MCDangerSignsConcepts.VAGINAL_BLEEDING.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Vaginal Bleeding</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="prenatalVisitRecord.dangerSigns.observationMap[${MCDangerSignsConcepts.DIZZINESS.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Dizziness</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="prenatalVisitRecord.dangerSigns.observationMap[${MCDangerSignsConcepts.FEVER.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Fever</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="prenatalVisitRecord.dangerSigns.observationMap[${MCDangerSignsConcepts.BLURRING_OF_VISION.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Blurring of vision</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="prenatalVisitRecord.dangerSigns.observationMap[${MCDangerSignsConcepts.EDEMA.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Edema</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>Report of New Medical Conditions</span></legend>
<table id="prenatal-new-medical-history" class="full-width borderless registration">
	<tr>
		<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.HYPERTENSION.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Hypertension</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.ASTHMA.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Bronchial Asthma</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.DIABETES.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Diabetes</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.TUBERCULOSIS.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Tuberculosis</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.HEART_DISEASE.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Heart Disease</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.ALLERGY.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Allergy</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.STI.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">STI</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
		<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.BLEEDING_DISORDERS.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Bleeding Disorders</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
	<tr>
		<td class="label">
			<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.OTHERS.conceptId}].valueCoded">
				<form:checkbox path="${status.expression}" id="patient-other" value="${chits:trueConcept()}" /> <label for="patient-other">Others</label>
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
			<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.OTHERS.conceptId}].valueText">
				<form:input path="${status.expression}" size="12" htmlEscape="${true}" id="patient-other-text" />
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
		<spring:bind path="prenatalVisitRecord.newMedicalConditions.observationMap[${MCMedicalHistoryConcepts.THYROID.conceptId}].valueCoded">
		<td class="label">
			<form:checkbox path="${status.expression}" id="history-${idIndex}" value="${chits:trueConcept()}" /> <label for="history-${idIndex}">Thyroid (goiter)</label><c:set var="idIndex" value="${idIndex+1}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>Other findings and remarks:</span></legend>
<table id="medical-history-remarks" class="full-width borderless registration">
	<tr>
		<spring:bind path="prenatalVisitRecord.observationMap[${MCPrenatalVisitRecordConcepts.REMARKS.conceptId}].valueText">
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
			<label for="needsToSeePhysician">THE PATIENT NEEDS TO SEE PHYSICIAN</label>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>