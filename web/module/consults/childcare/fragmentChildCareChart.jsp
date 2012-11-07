<%@ include file="/WEB-INF/template/include.jsp"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%>
<c:set var="updatable" value="${chits:getPatientState(form.patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.REGISTERED) ne null}" />
<chits_tag:immunizationStatus patient="${form.patient}" />
<chits_tag:childProtectedAtBirthStatus patient="${form.patient}" />

<style>
table.borderless td.label { width: 12em; }
div.update-button { width: 100%; text-align: right; }
div.program-chart { text-align: center; }
div.program-chart table.full-width { width: 98%; border: 1px solid #aaa; }
div.program-chart table.borderless { border: 0; }
div.program-chart div.update-button { width: 98%; }
em.service-warning { display: block; text-align: left; margin-bottom: 0.5em;}
table#eccdChart { border: 1px solid #ccc; }
table#eccdChart th { text-align: center; white-space: nowrap; }
</style>

<c:set var="noData"><em class="alert" style="white-space: nowrap;">still unknown</em></c:set>
<h3>ECCD PROGRAM CHART</h3>
<div class="program-chart full-width">
<table class="borderless full-width">
<c:set var="registeredState" value="${chits:getPatientState(form.patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.REGISTERED)}" />
<c:set var="weightObs" value="${chits:observation(form.patient, VisitConcepts.WEIGHT_KG)}" />
<c:set var="heightObs" value="${chits:observation(form.patient, VisitConcepts.HEIGHT_CM)}" />
<c:set var="tempObs" value="${chits:observation(form.patient, VisitConcepts.TEMPERATURE_C)}" />
<c:choose><c:when test="${registeredState eq null}">
<tr><td class="label">Registry ID:</td><td><tt class="alert">NOT YET REGISTERED</tt></td></tr>
<tr><td class="label">Date Registered:</td><td></td></tr>
</c:when><c:otherwise>
<tr><td class="label">Registry ID:</td><td><fmt:formatNumber pattern="00000" value="${registeredState.id}" /></td></tr>
<tr><td class="label">Date Registered:</td><td><fmt:formatDate value="${registeredState.startDate}" /> by ${registeredState.creator.personName}</td></tr>
</c:otherwise></c:choose>
<tr>
	<td colspan="2">
		<table class="form" id="eccdChart">
			<tr><th>AGE<br/>(in weeks)</th><th>Body Weight<br/>(in kg.)</th><th>Z-score</th><th>Temp<br/>(in &deg;C)</th></tr>
			<tr>
				<td><chits:age birthdate="${form.patient.birthdate}" noEnteredDataText="${noData}" weeksOnly="${true}" showClassification="${false}"/></td>
				<td>
					<chits:weight obs="${weightObs}" noEnteredDataText="${noData}" />
					<c:if test="${weightObs.obsDatetime ne null}">
					<span class="obsTaken">at <chits:age birthdate="${form.patient.birthdate}" noEnteredDataText="${noData}" on="${weightObs.obsDatetime}" monthsOnly="${true}" showClassification="${false}"/></span>
					</c:if>
				</td>
				<td><chits:weightForAge weight="${weightObs}" showValue="${false}" noEnteredDataText="${noData}" /></td>
				<td <c:if test="${tempObs.valueNumeric ge GlobalProperty[ChildCareConstants.GP_HIGH_TEMPERATURE_WARNING]}">class="alert"</c:if>>
					<chits:temp obs="${tempObs}" noEnteredDataText="${noData}" />
					<c:if test="${tempObs.obsDatetime ne null}">
					<span class="obsTaken">at <chits:age birthdate="${form.patient.birthdate}" noEnteredDataText="${noData}" on="${tempObs.obsDatetime}" monthsOnly="${true}" showClassification="${false}"/></span>
					</c:if>
				</td>
			</tr>
		</table>
	</td>
</tr>
<tr><td class="label">Immunization Status:</td><td><spring:message code="chits.childcare.ImmunizationStatus.${immunizationStatus}" text="chits.childcare.ImmunizationStatus.${immunizationStatus}" /></td></tr>
<%--
<tr><td class="label">Current age in Weeks:</td><td><chits:age birthdate="${form.patient.birthdate}" noEnteredDataText="${noData}" weeksOnly="${true}" showClassification="${false}"/></td></tr>
<tr>
	<td class="label">Body Weight:</td>
	<td>
		<chits:weight obs="${weightObs}" noEnteredDataText="${noData}" />
		<c:if test="${weightObs.obsDatetime ne null}">
		<span class="obsTaken">at <chits:age birthdate="${form.patient.birthdate}" noEnteredDataText="${noData}" on="${weightObs.obsDatetime}" monthsOnly="${true}" showClassification="${false}"/></span>
		</c:if>
	</td>
</tr>
--%>
<tr>
	<td class="label"><c:choose><c:when test="${form.patient.age lt 2}">Length</c:when><c:otherwise>Height</c:otherwise></c:choose>:</td>
	<td>
		<chits:height obs="${heightObs}" noEnteredDataText="${noData}" />
		<c:if test="${heightObs.obsDatetime ne null}">
		<span class="obsTaken">at <chits:age birthdate="${form.patient.birthdate}" noEnteredDataText="${noData}" on="${heightObs.obsDatetime}" monthsOnly="${true}" showClassification="${false}"/></span>
		</c:if>
	</td>
</tr>
<%--
<tr>
	<td class="label">Temperature (&deg;C):</td>
	<td <c:if test="${tempObs.valueNumeric ge GlobalProperty[ChildCareConstants.GP_HIGH_TEMPERATURE_WARNING]}">class="alert"</c:if>>
		<chits:temp obs="${tempObs}" noEnteredDataText="${noData}" />
		<c:if test="${tempObs.obsDatetime ne null}">
		<span class="obsTaken">at <chits:age birthdate="${form.patient.birthdate}" noEnteredDataText="${noData}" on="${tempObs.obsDatetime}" monthsOnly="${true}" showClassification="${false}"/></span>
		</c:if>
	</td>
</tr>
--%><%--
<tr><td class="label">BMI:</td><td><chits:bmi weight="${weightObs}" height="${heightObs}" birthdate="${form.patient.birthdate}"/></td></tr>
--%>
<tr><td class="label">Weight for Age:</td><td><chits:weightForAge weight="${weightObs}" showValue="${false}" noEnteredDataText="${noData}" /></td></tr>
<tr><td class="label">Weight for Length:</td><td><chits:weightForLength weight="${weightObs}" length="${heightObs}" showValue="${false}" noEnteredDataText="${noData}" /></td></tr>
<tr><td class="label">Height for Age:</td><td><chits:heightForAge height="${heightObs}" showValue="${false}" noEnteredDataText="${noData}" /></td></tr>
</table>
</div>

<hr/>

<h3>DELIVERY INFORMATION</h3>
<div class="program-chart full-width">
<c:set var="deliveryInfo" value="${chits:observation(form.patient, ChildCareConcepts.DELIVERY_INFORMATION)}" />
<c:set var="birthWeight" value="${chits:observation(deliveryInfo, ChildCareConcepts.BIRTH_WEIGHT)}" />
<table class="borderless full-width">
<c:if test="${deliveryInfo.creator ne null}">
<tr><td class="label">Last Modified By:</td><td>${deliveryInfo.creator.person.personName} on <fmt:formatDate pattern="MM/dd/yyyy" value="${deliveryInfo.dateCreated}" /></td></tr>
</c:if>
<tr><td class="label">Birth Weight:</td><td <c:if test="${birthWeight.valueNumeric lt GlobalProperty[ChildCareConstants.GP_LOW_BIRTHWEIGHT_VACCINATION_WARNING]}">class="alert"</c:if>><chits:weight obs="${birthWeight}" noEnteredDataText="${noData}" showDateTaken="${false}" showElapsedSinceTaken="${false}" /></td></tr>
<tr><td class="label">Birth Length:</td><td><chits:height obs="${chits:observation(deliveryInfo, ChildCareConcepts.BIRTH_LENGTH)}" noEnteredDataText="${noData}" showDateTaken="${false}" showElapsedSinceTaken="${false}" /></td></tr>
<tr><td class="label">Location of delivery:</td><td><chits:codedAnswer obs="${chits:observation(deliveryInfo, ChildCareConcepts.DELIVERY_LOCATION)}" noEnteredDataText="${noData}" /></td></tr>
<tr><td class="label">Method of delivery:</td><td><chits:codedAnswer obs="${chits:observation(deliveryInfo, ChildCareConcepts.METHOD_OF_DELIVERY)}" noEnteredDataText="${noData}" /></td></tr>
<tr><td class="label">Gestational Age at Birth (weeks):</td><td><chits:numericAnswer obs="${chits:observation(deliveryInfo, ChildCareConcepts.GESTATIONAL_AGE)}" noEnteredDataText="${noData}" /></td></tr>
<tr><td class="label">Birth Order:</td><td><chits:numericAnswer obs="${chits:observation(deliveryInfo, ChildCareConcepts.BIRTH_ORDER)}" noEnteredDataText="${noData}" /></td></tr>
<tr>
	<td class="label">Child Protected At Birth:</td>
	<td>
		<c:if test="${childProtectedAtBirthStatus ne ChildProtectedAtBirthStatus.MOTHER_NOT_LINKED}">
		<c:choose><c:when test="${childProtectedAtBirthStatus.protectedAtBirth}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose>
		</c:if>
		<c:if test="${childProtectedAtBirthStatus.warning}"><span class="alert" style="white-space: normal"><spring:message code="chits.childcare.ChildProtectedAtBirthStatus.${childProtectedAtBirthStatus}" text="chits.childcare.ChildProtectedAtBirthStatus.${childProtectedAtBirthStatus}" /></span></c:if>
	</td>
</tr>
<tr><td class="label">Date of Birth Registration:</td><td><chits:dateAnswer obs="${chits:observation(deliveryInfo, ChildCareConcepts.DOB_REGISTRATION)}" noEnteredDataText="${noData}" /></td></tr>
</table>

<br/>
<c:set var="screeningInfo" value="${chits:observation(form.patient, NewbornScreeningConcepts.SCREENING_INFORMATION)}" />
<table class="borderless">
<c:choose><c:when test="${chits:observation(screeningInfo, NewbornScreeningInformation.REPORT_DATE) eq null and form.patient.age ge 1}">
<tr><td>Newborn Screening Status:</td><td><span class="alert" style="white-space: nowrap;">NOT DONE</span></td></tr>
</c:when><c:otherwise>
<tr><td>Newborn Screening Status:</td><td><chits:codedAnswer obs="${chits:observation(screeningInfo, NewbornScreeningInformation.ACTION)}" noEnteredDataText="${noData}" /></td></tr>
<c:if test="${screeningInfo ne null}">
<c:set var="screeningDate" value="${chits:observation(screeningInfo, NewbornScreeningInformation.SCREENING_DATE).valueDatetime}" />
<tr><td style="padding-left: 2em;">Report Date:</td><td>${chits:observation(screeningInfo, NewbornScreeningInformation.REPORT_DATE).valueText}</td></tr>
<tr><td style="padding-left: 2em;">Age on Service:</td><td><chits:age birthdate="${form.patient.birthdate}" on="${screeningDate}" noEnteredDataText="${noData}" daysOnly="${true}" showClassification="${false}"/></td></tr>
<tr><td style="padding-left: 2em;">Actual date of service:</td><td><fmt:formatDate pattern="MM/dd/yyyy" value="${screeningDate}" /></td></tr>
</c:if>
</c:otherwise></c:choose>
</table>

<c:if test="${not form.programConcluded}">
	<div class="update-button">
		<input type="button" <c:if test="${!updatable}">disabled="disabled"</c:if> value="Update" onclick='javascript: loadAjaxForm("updateChildCareDeliveryInformation.form", "Update Delivery Information: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, 440)' />
	</div>
</c:if>
</div>

<hr/>

<h3>CHILDCARE REMARKS</h3>
<c:set var="childCareRemarksObs" value="${chits:observation(deliveryInfo, ChildCareConcepts.CHILDCARE_REMARKS)}" />
<c:set var="childCareRemarks" value="${childCareRemarksObs.valueText}" />
<c:set var="newbornFindings" value="${chits:observations(deliveryInfo, NewbornScreeningConcepts.RESULTS)}" />
<c:if test="${childCareRemarksObs.creator ne null}">
Last Modified By: ${childCareRemarksObs.creator.person.personName} on <fmt:formatDate pattern="MM/dd/yyyy" value="${childCareRemarksObs.dateCreated}" /><br/>
</c:if>
<c:choose><c:when test="${not empty childCareRemarks or not empty newbornFindings}">
	<c:if test="${not empty childCareRemarks}">${childCareRemarks}<br/></c:if>
	<c:if test="${not empty newbornFindings}"><span class="alert">Positive for: </span></c:if>
	<c:forEach var="finding" items="${newbornFindings}" varStatus="i">
		<c:if test="${i.index ne 0}">, </c:if>${finding.valueCoded.name.name}
	</c:forEach>
</c:when><c:otherwise>
	none
</c:otherwise></c:choose>
<c:if test="${not form.programConcluded}">
	<div class="update-button">
		<input type="button" <c:if test="${!updatable}">disabled="disabled"</c:if> value="Update" onclick='javascript: loadAjaxForm("updateChildCareNewbornScreeningResults.form", "Update Newborn Screening Results: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, 480)' />
	</div>
</c:if>
<hr/>

<h3>PARENTS' INFORMATION</h3>
<div class="program-chart full-width">
<table class="borderless full-width">

<c:choose><c:when test="${form.mother.patientId ne null}">
<tr><td class="label">Last Modified By:</td><td>${chits:coalesce(form.mother.changedBy.person.personName, form.mother.creator.person.personName)} on <fmt:formatDate pattern="MM/dd/yyyy" value="${chits:coalesce(form.mother.dateChanged, form.mother.dateCreated)}" /></td></tr>
<tr><td class="label">Mother's Name:</td><td>${form.mother.personName}</td></tr>
<tr><td class="label">Patient ID of Mother:</td><td><a href="../patients/viewPatient.form?patientId=${form.mother.patientId}">${form.mother.patientIdentifier.identifier}</a></td></tr>
<tr><td class="label">Occupation:</td><td>${chits:conceptByIdOrName(form.mother.attributeMap[MiscAttributes.OCCUPATION].value).name.name}</td></tr>
<tr><td class="label">No. of Pregnancies (G):</td><td>${chits:conceptByIdOrName(form.mother.attributeMap[MiscAttributes.NUMBER_OF_PREGNANCIES].value)}</td></tr>
<tr><td class="label">Educational Attainment:</td><td>${chits:conceptByIdOrName(form.mother.attributeMap[MiscAttributes.EDUCATION].value).name.name}</td></tr>
</c:when><c:otherwise>
<tr><td class="label">Mother's Name:</td><td>${noData}</td></tr>
</c:otherwise></c:choose>

<c:choose><c:when test="${form.father.patientId ne null}">
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td class="label">Father's Name:</td><td>${form.father.personName}</td></tr>
<tr><td class="label">Patient ID of Father:</td><td><a href="../patients/viewPatient.form?patientId=${form.father.patientId}">${form.father.patientIdentifier.identifier}</a></td></tr>
<tr><td class="label">Occupation:</td><td>${chits:conceptByIdOrName(form.father.attributeMap[MiscAttributes.OCCUPATION].value).name.name}</td></tr>
<tr><td class="label">Educational Attainment:</td><td>${chits:conceptByIdOrName(form.father.attributeMap[MiscAttributes.EDUCATION].value).name.name}</td></tr>
</c:when><c:otherwise>
<tr><td class="label">Father's Name:</td><td>${noData}</td></tr>
</c:otherwise></c:choose>

</table>
<c:if test="${not form.programConcluded}">
	<div class="update-button">
		<input type="button" <c:if test="${!updatable}">disabled="disabled"</c:if> value="Update" onclick='javascript: loadAjaxForm("updateChildCareParentsInformation.form", "Update Parents Information: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, 440)' />
	</div>
</c:if>
</div>

<hr/>

<h3>VACCINATION RECORD</h3>
<div class="program-chart full-width">
<table class="form full-width">
<tr><th>ANTIGEN</th><th>DATE GIVEN</th><th>HEALTH FACILITY</th><th>ADMINISTERED BY</th></tr>
<c:forEach var="vaccination" items="${chits:observations(form.patient, VaccinationConcepts.CHILDCARE_VACCINATION)}">
<c:set var="antigen" value="${chits:observation(vaccination, VaccinationConcepts.ANTIGEN)}" />
<c:set var="dateAdministered" value="${chits:observation(vaccination, VaccinationConcepts.DATE_ADMINISTERED)}" />
<c:set var="healthFacility" value="${chits:observation(vaccination, VaccinationConcepts.HEALTH_FACILITY)}" />
<tr>
	<td>
		<c:choose><c:when test="${antigen.valueCoded.conceptId ne ChildCareVaccinesConcepts.OTHERS.conceptId}">
			${antigen.valueCoded.name.name}
		</c:when><c:otherwise>
			${antigen.valueText}
		</c:otherwise></c:choose>
	</td>
	<td><fmt:formatDate pattern="MM/dd/yyyy" value="${dateAdministered.valueDatetime}" /><span class="obsTaken">at <chits:age birthdate="${form.patient.birthdate}" on="${dateAdministered.valueDatetime}" /></span></td>
	<td><chits_tag:obsValue obs="${healthFacility}" /></td>
	<td>${vaccination.creator.person.personName}</td>
</tr>
</c:forEach>
</table>
<c:if test="${not form.programConcluded}">
	<div class="update-button">
		<input type="button" <c:if test="${!updatable}">disabled="disabled"</c:if> value="Update" onclick='javascript: loadAjaxForm("updateChildCareVaccinationRecords.form", "Update Vaccination Records: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, 600)' />
	</div>
</c:if>
</div>

<hr/>

<h3>SERVICE RECORD</h3>
NOTE: Dental records may be updated from the Dental Module
<c:set var="vitaminAServices" value="${serviceStatus.services[chits:concept(ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION)]}" />
<c:set var="dewormingServices" value="${serviceStatus.services[chits:concept(ChildCareServiceTypes.DEWORMING)]}" />
<c:set var="ferrousSulfateServices" value="${serviceStatus.services[chits:concept(ChildCareServiceTypes.FERROUS_SULFATE)]}" />
<c:set var="dentalRecordServices" value="${serviceStatus.services[chits:concept(ChildCareServiceTypes.DENTAL_RECORD)]}" />
<div class="program-chart full-width">
<table class="form full-width">
<tr><th>SERVICES</th><th>LAST Service Date</th><th>Date Due</th></tr>
<tr>
	<td>VITAMIN&nbsp;A</td>
	<td>
		<c:choose><c:when test="${not empty vitaminAServices}">
		<c:set var="service" value="${vitaminAServices[0]}" />
			<span title="${chits:observation(service, ChildCareServicesConcepts.DOSAGE).valueCoded.name}, <chits_tag:obsValue obs="${chits:observation(service, ChildCareServicesConcepts.REMARKS)}" /> (${chits:observation(service, ChildCareServicesConcepts.SERVICE_SOURCE).valueCoded.name})">
			<fmt:formatDate pattern="dd MMM yyyy" value="${chits:observation(service, ChildCareServicesConcepts.DATE_ADMINISTERED).valueDatetime}" />
			</span>
		</c:when><c:otherwise>
			none
		</c:otherwise></c:choose>
	</td>
	<td>
		<chits_tag:serviceDue due="${serviceStatus.vitaminAServiceDueInfo}" /> 
	</td>
</tr>
<tr>
	<td>IRON</td>
	<td>
		<c:choose><c:when test="${not empty ferrousSulfateServices}">
		<c:set var="service" value="${ferrousSulfateServices[0]}" />
			<span title="${chits:observation(service, ChildCareServicesConcepts.DOSAGE).valueCoded.name}, <chits_tag:obsValue obs="${chits:observation(service, ChildCareServicesConcepts.REMARKS)}" /> (${chits:observation(service, ChildCareServicesConcepts.SERVICE_SOURCE).valueCoded.name})">
			<fmt:formatDate pattern="dd MMM yyyy" value="${chits:observation(service, ChildCareServicesConcepts.DATE_ADMINISTERED).valueDatetime}" />
			</span>
		</c:when><c:otherwise>
			none
		</c:otherwise></c:choose>
	</td>
	<td>
		<chits_tag:serviceDue due="${serviceStatus.ferrousSulfateServiceDueInfo}" /> 
	</td>
</tr>
<tr>
	<td>DEWORMING</td>
	<td>
		<c:choose><c:when test="${not empty dewormingServices}">
		<c:set var="service" value="${dewormingServices[0]}" />
			<span title="${chits:observation(service, ChildCareServicesConcepts.DOSAGE).valueCoded.name}, <chits_tag:obsValue obs="${chits:observation(service, ChildCareServicesConcepts.REMARKS)}" /> (${chits:observation(service, ChildCareServicesConcepts.SERVICE_SOURCE).valueCoded.name})">
			<fmt:formatDate pattern="dd MMM yyyy" value="${chits:observation(service, ChildCareServicesConcepts.DATE_ADMINISTERED).valueDatetime}" />
			</span>
		</c:when><c:otherwise>
			none
		</c:otherwise></c:choose>
	</td>
	<td>
		<chits_tag:serviceDue due="${serviceStatus.dewormingServiceDueInfo}" /> 
	</td>
</tr>
<tr>
	<td>DENTAL</td>
	<td>
		<c:choose><c:when test="${not empty dentalRecordServices}">
		<c:set var="service" value="${dentalRecordServices[0]}" />
			<span title="${chits:observation(service, ChildCareServicesConcepts.DOSAGE).valueCoded.name}, <chits_tag:obsValue obs="${chits:observation(service, ChildCareServicesConcepts.REMARKS)}" /> (${chits:observation(service, ChildCareServicesConcepts.SERVICE_SOURCE).valueCoded.name})">
			<fmt:formatDate pattern="dd MMM yyyy" value="${chits:observation(service, ChildCareServicesConcepts.DATE_ADMINISTERED).valueDatetime}" />
			</span>
		</c:when><c:otherwise>
			none
		</c:otherwise></c:choose>
	</td>
	<td>
		none 
	</td>
</tr>
</table>

<c:if test="${not form.programConcluded}">
	<c:forEach var="alert" items="${serviceStatus.vitaminAServiceAlerts}">
	<em class="service-warning alert"><spring:message code="${alert}" text="${alert}" /></em>
	</c:forEach>
	<c:forEach var="alert" items="${serviceStatus.dewormingAlerts}">
	<em class="service-warning alert"><spring:message code="${alert}" text="${alert}" /></em>
	</c:forEach>
	<c:forEach var="alert" items="${serviceStatus.ferrousSulfateAlerts}">
	<em class="service-warning alert"><spring:message code="${alert}" text="${alert}" /></em>
	</c:forEach>
</c:if>

<div class="update-button">
<c:choose><c:when test="${not form.programConcluded}">
	<input type="button" <c:if test="${!updatable}">disabled="disabled"</c:if> value="Update" onclick='javascript: loadAjaxForm("viewChildCareServiceRecords.form", "Update Service Records: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, 760)' />
</c:when><c:otherwise>
	<input type="button" value="View Details" onclick='javascript: loadAjaxForm("viewChildCareServiceRecords.form", "View Service Records: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, 760)' />
</c:otherwise></c:choose>
</div>
</div>

<hr/>

<h3>BREASTFEEDING</h3>
Exclusive breastfeeding done:
<c:set var="breastFeedingInfo" value="${chits:observation(form.patient, BreastFeedingConcepts.BREASTFEEDING_INFO)}" />
<div class="program-chart full-width">
<table class="form full-width">
<tr>
	<c:forEach var="month" items="${chits:members(BreastFeedingConcepts.BREASTFEEDING_INFO)}">
	<c:if test="${month.shortNames[0] ne null and fn:length(month.shortNames[0].name) le 3}">
	<th>${month.shortNames[0]}</th>
	</c:if>
	</c:forEach>
</tr>
<tr>
	<c:set var="exclusivelyBreastFed" value="${true}" />
	<c:set var="allMonthsSet" value="${true}" />
	<c:forEach var="month" items="${chits:members(BreastFeedingConcepts.BREASTFEEDING_INFO)}">
	<c:if test="${month.shortNames[0] ne null and fn:length(month.shortNames[0].name) le 3}">
	<td>
		<c:set var="breastFedOnMonth" value="${chits:observation(breastFeedingInfo, month)}" />
		<c:choose><c:when test="${not empty breastFedOnMonth.valueNumeric}">
			<c:if test="${breastFedOnMonth.valueNumeric ne 0}">Y</c:if>
			<c:if test="${breastFedOnMonth.valueNumeric eq 0}">X<c:set var="exclusivelyBreastFed" value="${false}" /></c:if>
		</c:when><c:otherwise>
			&nbsp;
			<c:set var="exclusivelyBreastFed" value="${false}" />
			<c:set var="allMonthsSet" value="${false}" />
		</c:otherwise></c:choose>
	</td>
	</c:if>
	</c:forEach>
</tr>
</table>
<c:if test="${allMonthsSet}">
	<c:choose><c:when test="${exclusivelyBreastFed}">
	<strong>Exclusively Breastfed</strong>
	</c:when><c:otherwise>
	<strong>Not Exclusively Breastfed</strong>
	</c:otherwise></c:choose>
</c:if>
<c:if test="${not form.programConcluded and not allMonthsSet}">
	<div class="update-button">
		<input type="button" <c:if test="${!updatable}">disabled="disabled"</c:if> value="Update" onclick='javascript: loadAjaxForm("updateChildCareBreastFeedingInformation.form", "Update Breastfeeding Information: ${form.patient.personName} (${form.patient.gender}, <chits:age birthdate="${form.patient.birthdate}" />)", ${form.patient.patientId}, 480)' />
	</div>
</c:if>
</div>