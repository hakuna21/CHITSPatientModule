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
	attachCheckboxMediator('#smoking-history', '#no-of-sticks-per-day')
	attachCheckboxMediator('#smoking-history', '#duration-in-years')
	attachCheckboxMediator('#allergies', '#allergies-text')
	attachCheckboxMediator('#drug-intake', '#drug-intake-text')
})
</script>

<br/>
<form:form id="medical-history-registration-form" modelAttribute="form" method="post" cssClass="main-form">

<fieldset><legend><span><c:choose
	><c:when test="${not empty form.page}">Page 2: MEDICAL HISTORY (check all that apply)</c:when
	><c:otherwise>UPDATE MEDICAL HISTORY</c:otherwise></c:choose
></span></legend>
<form:hidden path="version" />
<form:hidden path="page" />

<table class="full-width borderless registration field">
<tr>
	<td>
		<fieldset><legend>HEENT</legend>
		<table>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.SEIZURE.conceptId}].valueCoded" label="Epilepsy/Convlusion/Seizure" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.HEADACHE.conceptId}].valueCoded" label="Severe headache/dizziness" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.BLURRING.conceptId}].valueCoded" label="Visual disturbance/blurring of vision" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.YELLOWISH_CONJUNCTIVE.conceptId}].valueCoded" label="Yellowish conjunctive" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.ENLARGED_THYROID.conceptId}].valueCoded" label="Enlarged thyroid" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend>CHEST/HEART</legend>
		<table>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.CHEST_PAIN.conceptId}].valueCoded" label="Severe chest pain" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.FATIGABILITY.conceptId}].valueCoded" label="Shortness of breath and easy fatiguability" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.BREAST_MASS.conceptId}].valueCoded" label="Breast/axillary masses" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.NIPPLE_BLOOD_DISCHARGE.conceptId}].valueCoded" label="Nipple discharges (blood)" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.NIPPLE_PUS_DISCHARGE.conceptId}].valueCoded" label="Nipple discharges (pus)" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.SBP_OVER140.conceptId}].valueCoded" label="Systolic of 140 &amp; above" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.DBP_OVER90.conceptId}].valueCoded" label="Diastolic of 90 &amp; above" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.FAMILY_HISTORY_OF_STROKES_ETC.conceptId}].valueCoded" label="Family history of CVA (strokes), hypertension, asthma, rheumatic heart disease" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend>ABDOMEN</legend>
		<table>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.ABDOMINAL_MASS.conceptId}].valueCoded" label="Mass in the abdomen" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.HISTORY_OF_GALLBLADDER.conceptId}].valueCoded" label="History of gallbladder disease" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.HISTORY_OF_LIVER_DISEASE.conceptId}].valueCoded" label="History of liver disease" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend>GENITAL</legend>
		<table>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.MASS_IN_UTERUS.conceptId}].valueCoded" label="Mass in the uterus" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.VAGINAL_DISCHARGE.conceptId}].valueCoded" label="Vaginal discharge" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.INTERMENSTRUAL_BLEEDING.conceptId}].valueCoded" label="Intermenstrual bleeding" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.POSTCOITAL_BLEEDING.conceptId}].valueCoded" label="Postcoital bleeding" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend>EXTREMITIES</legend>
		<table>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.SEVERE_VARICOSITIES.conceptId}].valueCoded" label="Severe varicosities" /></td></tr>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.EDEMA.conceptId}].valueCoded" label="Swelling or severe pain in the legs not related to injuries" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend>SKIN</legend>
		<table>
			<tr><td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.YELLOWISH_SKIN.conceptId}].valueCoded" label="Yellowish skin" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset><legend>HISTORY OF ANY OF THE FOLLOWING</legend>
		<table>
			<tr>
				<td class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.SMOKING_HISTORY.conceptId}].valueCoded" id="smoking-history" /></td>
				<td><label for="smoking-history">Smoker</label></td>
				<td class="label">
					<div class="indent">
						<label for="no-of-sticks-per-day">No. of sticks/day:</label>
						<chits_tag:springInput path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.SMOKING_STICKS_PER_DAY.conceptId}].valueText" size="1" cssStyle="width: 1em;" id="no-of-sticks-per-day" />
					</div>
				</td>
				<td class="label">
					<label for="duration-in-years">Duration (in years):</label>
					<chits_tag:springInput path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.SMOKING_YEARS.conceptId}].valueText" size="1" cssStyle="width: 1em;" id="duration-in-years" />
				</td>
			</tr><tr>
				<td class="label" colspan="4"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.ALLERGIES.conceptId}].valueCoded" id="allergies" label="Allergies" /></td>
			</tr><tr>
				<td colspan="4"><chits_tag:springInput path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.ALLERGIES.conceptId}].valueText" cssClass="full-width" id="allergies-text" /></td>
			</tr><tr>
				<td class="label" colspan="4"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.DRUG_INTAKE.conceptId}].valueCoded" id="drug-intake" label="Drug intake (anti-TB, anti-diabetic, anticonvulsant)" /></td></td>
			</tr><tr>
				<td colspan="4"><chits_tag:springInput path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.DRUG_INTAKE.conceptId}].valueText" cssClass="full-width" id="drug-intake-text" /></td>
			</tr><tr>
				<td colspan="4" class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.BLEEDING_TENDENCIES.conceptId}].valueCoded" label="Bleeding tendencies (nose, gums, etc.)" /></td>
			</tr><tr>
				<td colspan="4" class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.ANEMIA.conceptId}].valueCoded" label="Anemia" /></td>
			</tr><tr>
				<td colspan="4" class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.DIABETES.conceptId}].valueCoded" label="Diabetes" /></td>
			</tr><tr>
				<td colspan="4" class="label"><chits_tag:springCheckbox path="fpProgramObs.medicalHistoryInformation.observationMap[${FPMedicalHistoryConcepts.HYDATIDIFORM_MOLE.conceptId}].valueCoded" label="Hydatidiform mole (w/in the last 12 mos.)" /></td>
			</tr>
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