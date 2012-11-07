<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />

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
	attachCheckboxMediator('#external-genitalia-other', '#external-genitalia-other-text')
})
</script>

<chits_tag:auditInfo obsGroup="${form.internalExaminationRecord.obs}" />

<br />
<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}, updateMCSection); return false;">
<form:hidden path="version" />

<fieldset>
<table id="internal-examination-record" class="full-width borderless registration">
	<tr>
		<td>
			<table class="field"><tr><td class="label">IE Date*</td>
			<td><spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.VISIT_DATE.conceptId}].valueText">
			<form:input path="${status.expression}" id="ieVisitDate" htmlEscape="${true}" onclick="showCalendar(this)" />
			<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind></td>
			</tr></table>
		</td>
	</tr><tr>
		<td>
			<table class="field"><tr><td class="label" rowspan="2" style="vertical-align: top;">External genitalia:</td>
				<td>
				<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.EXTERNAL_GENITALIA.conceptId}].valueCoded">
					<form:radiobutton path="${status.expression}" id="external-genitalia-normal" value="${chits:concept(MCIEOptions.NORMAL)}" /> <label for="external-genitalia-normal">Normal</label>
					<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
				</spring:bind>
				</td>
			</tr><tr>
				<td>
				<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.EXTERNAL_GENITALIA.conceptId}].valueCoded">
					<form:radiobutton path="${status.expression}" id="external-genitalia-other" value="${chits:concept(MCIEOptions.OTHERS)}" /> <label for="external-genitalia-other">Others</label>
				</spring:bind>
				<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.EXTERNAL_GENITALIA_TEXT.conceptId}].valueText">
					<form:input path="${status.expression}" size="12" htmlEscape="${true}" id="external-genitalia-other-text" />
					<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
				</spring:bind>
				</td>
			</tr></table>
		</td>
	</tr><tr>
		<td>
			<table class="field"><tr>
				<td class="label">Vagina:</td>
				<td class="field">
					<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.VAGINA.conceptId}].valueCoded">
						<form:radiobutton path="${status.expression}" id="NULLIPAROUS" value="${chits:concept(MCIEOptions.NULLIPAROUS)}" /> <label for="NULLIPAROUS">Nulliparous</label>
						<form:radiobutton path="${status.expression}" id="PAROUS" value="${chits:concept(MCIEOptions.PAROUS)}" /> <label for="PAROUS">Parous</label>
						<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
				</td>
			</tr></table>
		</td>
	</tr><tr>
		<td>
			<table class="field"><tr>
				<td class="label">Cervix state:</td>
				<td class="field">
					<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.CERVIX_STATE.conceptId}].valueCoded">
						<form:radiobutton path="${status.expression}" id="cervix-state-closed" value="${chits:concept(MCIEOptions.CLOSED)}" /> <label for="cervix-state-closed">Closed</label>
						<form:radiobutton path="${status.expression}" id="cervix-state-open" value="${chits:concept(MCIEOptions.OPEN)}" /> <label for="cervix-state-open">Open</label>
						<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
				</td>
			</tr></table>
		</td>
	</tr><tr>
		<td>
			<table class="field"><tr>
				<td class="label">Cervix consistency:</td>
				<td class="field">
					<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.CERVIX_CONSISTENCY.conceptId}].valueCoded">
						<form:radiobutton path="${status.expression}" id="cervix-consistency-closed" value="${chits:concept(MCIEOptions.FIRM)}" /> <label for="cervix-consistency-closed">Firm</label>
						<form:radiobutton path="${status.expression}" id="cervix-consistency-open" value="${chits:concept(MCIEOptions.SOFT)}" /> <label for="cervix-consistency-open">Soft</label>
						<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
				</td>
			</tr></table>
		</td>
	</tr><tr>
		<td>
			<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.ENLARGED_TO.conceptId}].valueText">
			<table class="field"><tr>
				<td class="label">Uterus:</td>
				<td class="label">Enlarged to:</td>
				<td class="field"><form:input path="${status.expression}" htmlEscape="${true}" size="2" /></td>
				<td class="label">weeks AOG</td>
			</tr>
			<c:if test="${not empty status.errorMessage}">
			<tr>
				<td colspan="4"><div class="error">${status.errorMessage}</div></td>
			</tr>
			</c:if>
			</table>
			</spring:bind>
		</td>
	</tr><tr>
		<td>
			<table class="field"><tr>
				<td class="label">Adnexal Mass / Tenderness:</td>
				<td class="field">
					<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.TENDERNESS.conceptId}].valueCoded">
						<form:radiobutton path="${status.expression}" id="adnexal-mass-positive" value="${chits:concept(MCIEOptions.POSITIVE)}" /> <label for="adnexal-mass-positive">(+)</label>
						<form:radiobutton path="${status.expression}" id="adnexal-mass-negative" value="${chits:concept(MCIEOptions.NEGATIVE)}" /> <label for="adnexal-mass-negative">(-)</label>
						<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
				</td>
			</tr></table>
		</td>
	</tr><tr>
		<td>
			<table class="field"><tr>
				<td class="label">Masses / locations:</td>
				<td class="field">
					<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.MASSES_LOCATIONS.conceptId}].valueText">
						<form:input path="${status.expression}" htmlEscape="${true}" />
						<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
				</td>
			</tr></table>
		</td>
	</tr><tr>
		<td>
			<table class="field"><tr>
				<td class="label">Pelvimetry:</td>
				<td class="field">
					<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.PELVIMETRY.conceptId}].valueCoded">
						<form:radiobutton path="${status.expression}" id="pelvimetry-adequate" value="${chits:concept(MCIEOptions.ADEQUATE)}" /> <label for="pelvimetry-adequate">Adequate</label>
						<form:radiobutton path="${status.expression}" id="pelvimetry-inadequate" value="${chits:concept(MCIEOptions.INADEQUATE)}" /> <label for="pelvimetry-inadequate">Inadequate</label>
						<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
				</td>
			</tr></table>
		</td>
	</tr><tr>
		<td>
			<table class="field"><tr>
				<td class="label">Membranes:</td>
				<td class="field">
					<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.MEMBRANES.conceptId}].valueCoded">
						<form:radiobutton path="${status.expression}" id="membranes-intact" value="${chits:concept(MCIEOptions.INTACT)}" /> <label for="membranes-intact">Intact</label>
						<form:radiobutton path="${status.expression}" id="membranes-ruptured" value="${chits:concept(MCIEOptions.RUPTURED)}" /> <label for="membranes-ruptured">Ruptured</label>
						<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
				</td>
			</tr></table>
		</td>
	</tr><tr>
		<td>
			<table class="field"><tr>
				<td class="label">Fetal Presentation:</td>
			</tr><tr>
				<td class="field">
					<div class="indent">
						Position:
						<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.FETAL_PRESENTATION.conceptId}].valueCoded">
							<form:select path="${status.expression}">
								<form:option value="">select position</form:option>
								<c:forEach var="answer" items="${chits:answers(MCIERecordConcepts.FETAL_PRESENTATION)}">
								<form:option value="${answer.conceptId}">${answer.name}</form:option></c:forEach>
							</form:select>
							<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
						</spring:bind>

						<br/>
						Station:
						<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.FETAL_STATION.conceptId}].valueCoded">
							<form:select path="${status.expression}">
								<form:option value="">Select station</form:option>
								<c:forEach var="answer" items="${chits:answers(MCIERecordConcepts.FETAL_STATION)}">
								<form:option value="${answer.conceptId}">${answer.name}</form:option></c:forEach>
							</form:select>
							<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
						</spring:bind>
					</div>
				</td>
			</tr></table>
		</td>
	</tr><tr>
		<td>
			<table class="field"><tr>
				<td class="label">Bloody Show:</td>
				<td class="field">
					<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.BLOODY_SHOW.conceptId}].valueCoded">
						<form:radiobutton path="${status.expression}" id="bloody-show-positive" value="${chits:concept(MCIEOptions.POSITIVE)}" /> <label for="bloody-show-positive">(+)</label>
						<form:radiobutton path="${status.expression}" id="bloody-show-negative" value="${chits:concept(MCIEOptions.NEGATIVE)}" /> <label for="bloody-show-negative">(-)</label>
						<c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
				</td>
			</tr></table>
		</td>
	</tr>
</table>
</fieldset>

<fieldset>
<legend><span>Remarks:</span></legend>
<table id="internal-examination-remarks" class="full-width borderless registration">
	<tr>
		<spring:bind path="internalExaminationRecord.observationMap[${MCIERecordConcepts.REMARKS.conceptId}].valueText">
		<td class="label">
			<form:textarea path="${status.expression}" cssStyle="width: 96%" rows="5" />
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
		</td>
		</spring:bind>
	</tr>
</table>
</fieldset>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>