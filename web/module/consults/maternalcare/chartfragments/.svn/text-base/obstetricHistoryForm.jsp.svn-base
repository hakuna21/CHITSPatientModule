<%-- NOTE: This fragment is designed to be included with the jsp include directive
           instead of the include action since it does not have the necessary headers
--%>
<fieldset>
<form:hidden path="version" />
<form:hidden path="page" />
<table class="full-width borderless registration">
	<tr>
		<td>
			<table class="field"><tr><td class="label">Last Menstrual Period*</td>
			<td><spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD.conceptId}].valueText">
			<form:input path="${status.expression}" id="lastMenstrualPeriod" onfocus="showCalendar(this)" htmlEscape="${true}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind></td></tr></table>
		</td>
	</tr>
	<tr>
		<td>
			<table class="field"><tr><td class="label">LMP Remarks:</td>
			<td><spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.LMP_REMARKS.conceptId}].valueText">
			<form:input path="${status.expression}" htmlEscape="${true}" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind></td></tr></table>
		</td>
	</tr>
	<tr>
		<td>
			<table class="field"><tr><td class="label">Blood Type:</td>
			<td><spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.BLOOD_TYPE.conceptId}].valueCoded">
				<form:select path="${status.expression}">
					<option value="">select type</option>
					<c:forEach var="bloodType" items="${chits:answers(MCObstetricHistoryConcepts.BLOOD_TYPE)}">
					<form:option value="${bloodType.conceptId}">${bloodType.name}</form:option>
					</c:forEach>
				</form:select>
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind></td>
			<td class="label">RH Factor:</td>
			<td><chits_tag:springDropdown path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.RHESUS_FACTOR.conceptId}].valueCoded" answers="${chits:answers(MCObstetricHistoryConcepts.RHESUS_FACTOR)}" /></td></tr></table>
		</td>
	</tr>
	<tr>
		<td>
			<div class="grouped-block">
				<strong>Pregnancy Test</strong>

				<table class="field"><tr><td class="label">Date Performed:</td>
				<td><spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.PREGNANCY_TEST_DATE_PERFORMED.conceptId}].valueText">
				<form:input path="${status.expression}" id="datePerformed" onfocus="showCalendar(this)" htmlEscape="${true}" />
				<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
				</spring:bind></td></tr></table>

				<spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.PREGNANCY_TEST_RESULT.conceptId}].valueCoded">
				<table class="field">
					<tr><td class="label">Result: <c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></td></tr>
					<tr><td><form:radiobutton path="${status.expression}" id="ptPositive" value="${chits:concept(MCPregnancyTestResultsOptions.POSITIVE)}" /> <label for="ptPositive">Positive</label></td></tr>
					<tr><td><form:radiobutton path="${status.expression}" id="ptNegative" value="${chits:concept(MCPregnancyTestResultsOptions.NEGATIVE)}" /> <label for="ptNegative">Negative</label></td></tr>
				</table>
				</spring:bind>
			</div>
		</td>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>OBSTETRIC SCORE:</span></legend>
<table class="field borderless full-width registration">
	<tr>
		<td class="label">G*:</td>
		<td><spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.OBSTETRIC_SCORE_GRAVIDA.conceptId}].valueText"><form:input path="${status.expression}" htmlEscape="${true}" size="1" /><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></spring:bind></td>

		<td class="label">P*:</td>
		<td><spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PARA.conceptId}].valueText"><form:input path="${status.expression}" htmlEscape="${true}" size="1" /><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></spring:bind></td>
	</tr>
	<tr>
		<td class="label">F:</td>
		<td><spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.OBSTETRIC_SCORE_FT.conceptId}].valueText"><form:input path="${status.expression}" htmlEscape="${true}" size="1" /><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></spring:bind></td>

		<td class="label">P:</td>
		<td><spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PT.conceptId}].valueText"><form:input path="${status.expression}" htmlEscape="${true}" size="1" /><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></spring:bind></td>

		<td class="label">A:</td>
		<td><spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.OBSTETRIC_SCORE_AM.conceptId}].valueText"><form:input path="${status.expression}" htmlEscape="${true}" size="1" /><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></spring:bind></td>

		<td class="label">L:</td>
		<td><spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.OBSTETRIC_SCORE_LC.conceptId}].valueText"><form:input path="${status.expression}" htmlEscape="${true}" size="1" /><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></spring:bind></td>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>OBSTETRIC HISTORY</span></legend>
<table class="field borderless full-width registration">
	<spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.HISTORY_PREV_CSECTION.conceptId}].valueCoded">
	<tr><td><form:checkbox path="${status.expression}" id="histPrevCSection" value="${chits:trueConcept()}" /></td><td><label for="histPrevCSection">History of previous caesarian</label><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></td></tr>
	</spring:bind>

	<spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.HISTORY_3OR_MORE_MISCARRIAGES.conceptId}].valueCoded">
	<tr><td><form:checkbox path="${status.expression}" id="hist3OrMoreMiscarriages" value="${chits:trueConcept()}" /></td><td><label for="hist3OrMoreMiscarriages">History of 3 or more consecutive miscarriages or stillborn baby</label><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></td></tr>
	</spring:bind>

	<spring:bind path="mcProgramObs.obstetricHistory.observationMap[${MCObstetricHistoryConcepts.HISTORY_OF_POSTPARTUM_HEMORRHAGE.conceptId}].valueCoded">
	<tr><td><form:checkbox path="${status.expression}" id="histOfPostpatumHemorrhage" value="${chits:trueConcept()}" /></td><td><label for="histOfPostpatumHemorrhage">History of postpartum hemorrhage</label><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if></td></tr>
	</spring:bind>
</table>
</fieldset>

<fieldset>
<legend><span>Obstetric history details</span></legend>
<div class="obstetric-history-details" class="registration grouped-block">
	<jsp:include page="ajaxObstetricHistoryDetails.jsp" />
</div>

<div class="full-width" style="text-align: right">
<input type="button" id="addNewButton" value='Add New' onclick="addNewOBHistoryDetail(${form.patient.patientId})" />
</div>
</fieldset>