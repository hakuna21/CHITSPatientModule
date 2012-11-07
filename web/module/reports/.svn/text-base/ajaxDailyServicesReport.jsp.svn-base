<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.Obs"
%><%@ page import="org.openmrs.module.chits.mcprogram.MaternalCareProgramObs"
%><%@ page import="org.openmrs.Encounter"
%><%@ page import="org.openmrs.module.chits.mcprogram.MaternalCareUtil"
%><%@ include file="/WEB-INF/template/include.jsp"%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%>

<c:set var="dateRange"><spring:bind path="form.startDate">${status.value}</spring:bind> to <spring:bind path="form.endDate">${status.value}</spring:bind></c:set>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/css/jquery.dataTables.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/css/TableTools_JUI.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/css/TableTools.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/js/TableTools.min.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/js/ZeroClipboard.js" />
<script>
$j(document).ready(function() {
	$j("#consultsReportTable").dataTable({"bSort": false,
		"sDom":'T<"clear">lfrtip',
		"oTableTools":{
			"sSwfPath":'${pageContext.request.contextPath}/moduleResources/chits/scripts/jquery.datatable/TableTools/swf/copy_cvs_xls_pdf.swf',
			"aButtons": [ "print", {"sExtends":"xls","sTitle":"Daily Services Report - Consults - ${dateRange}"}, "copy" ]
		}
	});

	$j("#childcareReportTable").dataTable({"bSort": false,
		"sDom":'T<"clear">lfrtip',
		"oTableTools":{
			"sSwfPath":'${pageContext.request.contextPath}/moduleResources/chits/scripts/jquery.datatable/TableTools/swf/copy_cvs_xls_pdf.swf',
			"aButtons": [ "print", {"sExtends":"xls","sTitle":"Daily Services Report - ECCD - ${dateRange}"}, "copy" ]
		}
	});

	$j("#maternalcareReportTable").dataTable({"bSort": false,
		"sDom":'T<"clear">lfrtip',
		"oTableTools":{
			"sSwfPath":'${pageContext.request.contextPath}/moduleResources/chits/scripts/jquery.datatable/TableTools/swf/copy_cvs_xls_pdf.swf',
			"aButtons": [ "print", {"sExtends":"xls","sTitle":"Daily Services Report - Maternal Care"}, "copy" ]
		}
	});

	// $j("#reports").tabs()
	<c:if test="${not empty encounters}">
	$j("#generalForm").dialog("option", "width", 1000)
	</c:if>
})
</script>

<style>
div.DTTT_container { vertical-align: middle; margin-left: 0.5em; margin-top: -4px;}
div.DTTT_container button { height: 28px; vertical-align: middle; }
div.DTTT_container button span { font-size: 10px; vertical-align: middle; }
#consultsReportTable, #childcareReportTable, #maternalcareReportTable { border: 1px solid #aaa; }
span.vs { width: 150px; margin-bottom: 0.6em; display: block; }
div#reports ul#tabs li { display: inline; margin-right: 2em; }
</style>

<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
<c:if test="${err != null}">
	<span class="validationError"><div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div></span>
</c:if>

<spring:hasBindErrors name="form">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<c:choose><c:when test="${empty encounters}">

<form:form modelAttribute="form" method="post" action="dailyServicesReport.form" onsubmit="submitAjaxForm(this); return false;">
<h3><spring:message code="chits.reports.dailyservices.title" /></h3>

<h4>INCLUSIVE REPORT FOR THIS DATE</h4>

<table class="borderless">
	<spring:bind path="startDate">
	<tr><td>Start Date:<br/><i style="font-weight: normal; font-size: .8em;">(<spring:message code="general.format"/>: <openmrs:datePattern />)</i></td><td><form:input onclick="showCalendar(this)" path="${status.expression}" id="startDate" size="10" /><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td></tr>
	</spring:bind>
	<spring:bind path="endDate">
	<tr><td>End Date:<br/><i style="font-weight: normal; font-size: .8em;">(<spring:message code="general.format"/>: <openmrs:datePattern />)</i></td><td><form:input onclick="showCalendar(this)" path="${status.expression}" id="endDate" size="10" /><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td></tr>
	</spring:bind>
</table>

<br/>

<input type="submit" value="Generate" />
<input type="button" value="Cancel" onclick="javascript:$j('#generalForm').dialog('close')" />
</form:form>

</c:when><c:otherwise>

<strong>DAILY SERVICE REPORT</strong><br/>
REPORT DATE: <strong>${dateRange}</strong>
<br/>

<p><strong style="color: red">NOTE: Use 'CTRL+' to zoom.</strong></p>

<div id="reports">
	<ul id="tabs">
		<li><a href="#consults">CONSULTS</a></li>
		<li><a href="#childCare">ECCD</a></li>
		<li><a href="#maternalCare">MATERNAL CARE</a></li>
	</ul>

	<div id="consults">
		<h4>CONSULTS</h4>
		<table id="consultsReportTable" class="form">
		<thead>
			<tr>
				<th>PATIENT ID</th>
				<th>PATIENT NAME / SEX / AGE</th>
				<th>CONSULT DATE / ELAPSED TIME</th>
				<th>ADDRESS</th>
				<th>BRGY</th>
				<th>FAMILY ID</th>
				<th>PHILHEALTH ID</th>
				<th>VITAL SIGNS</th>
				<th>COMPLAINTS</th>
				<th>DIAGNOSIS</th>
				<th>TREATMENT</th>
			</tr>
		</thead>
		<tbody><c:forEach var="enc" items="${encounters}"><chits_tag:foldersOf patient="${enc.patient}" />
			<tr id="consults_enc_${enc.encounterId}">
				<td>${enc.patient.patientIdentifier}</td>
				<td>${enc.patient.personName} / ${enc.patient.gender} / <chits:age birthdate="${enc.patient.birthdate}" on="${enc.encounterDatetime}" /></td>
				<td><chits_tag:encounterDuration enc="${enc}" /></td>
				<c:choose><c:when test="${not empty folders}">
				<td>${chits:coalesce(folders[0].address, '-')}</td>
				<td>${barangays[folders[0].barangayCode].name}, ${barangays[folders[0].barangayCode].municipality.name}</td>
				<td>${folders[0].code}</td>
				</c:when><c:otherwise>
				<td>-</td>
				<td>-</td>
				<td>-</td>
				</c:otherwise></c:choose>
				<td>${chits:coalesce(enc.patient.attributeMap[PhilhealthConcepts.CHITS_PHILHEALTH], '-')}</td>
				<td><c:set var="encVitalSigns" value="${chits:observations(enc, VisitConcepts.VITAL_SIGNS)}" /><c:choose
					><c:when test="${not empty encVitalSigns}"><c:forEach var="vitalSigns" items="${encVitalSigns}"><span class="vs"><chits:vitalSigns vitalSigns="${vitalSigns}" showElapsedSinceTaken="${false}" showObsDate="${false}" /></span> </c:forEach
					></c:when><c:otherwise>-</c:otherwise></c:choose
				></td>
				<td><c:set var="complaints" value="${chits:observations(enc, VisitConcepts.COMPLAINT)}"
					/><c:choose><c:when test="${not empty complaints}"><c:forEach var="complaint" items="${complaints}" varStatus="i"><c:set var="icd10" value="${chits:mapping(complaint.valueCoded, 'ICD10')}"
						/><c:if test="${i.index ne 0}">, </c:if>${complaint.valueCoded.name.name}<c:if test="${not empty icd10}"> (${icd10})</c:if
					></c:forEach></c:when><c:otherwise>-</c:otherwise></c:choose
				></td>
				<td><c:set var="diagnoses" value="${chits:observations(enc, VisitConcepts.DIAGNOSIS)}"
					/><c:choose><c:when test="${not empty diagnoses}"><c:forEach var="diagnosis" items="${diagnoses}" varStatus="i"><c:set var="icd10" value="${chits:mapping(diagnosis.valueCoded, 'ICD10')}"
					/><c:if test="${i.index ne 0}">, </c:if>${diagnosis.valueCoded.name.name}<c:if test="${not empty icd10}"> (${icd10})</c:if
					></c:forEach></c:when><c:otherwise>-</c:otherwise></c:choose
				></td>
				<td><chits_tag:drugOrders enc="${enc}"
					/>${chits:observation(enc, VisitNotesConceptSets.TREATMENT_NOTES).valueText}<c:choose><c:when test="${not empty drugOrders}"
						><c:forEach var="drugOrder" items="${drugOrders}">${drugOrder.drug.concept.name.name} x ${drugOrder.quantity}, ${drugOrder.instructions}<br/></c:forEach
					></c:when><c:otherwise><span>-</span></c:otherwise></c:choose
				></td>
			</tr>
		</c:forEach></tbody>
		</table>
	</div>

	<div id="childCare">
		<br/><br/>
		<h4>ECCD</h4>
		<table id="childcareReportTable" class="form">
		<thead>
			<tr>
				<th>PATIENT ID</th>
				<th>PATIENT NAME / SEX / AGE</th>
				<th>ADDRESS</th>
				<th>BRGY</th>
				<th>FAMILY ID</th>
				<th>PHILHEALTH ID</th>
				<th>VITAL SIGNS</th>
				<th>VACCINE(S) GIVEN</th>
				<th>SERVICE(S) GIVEN</th>
			</tr>
		</thead>
		<tbody><c:forEach var="enc" items="${encounters}"><c:set var="vaccines" value="${chits:observations(enc, VaccinationConcepts.CHILDCARE_VACCINATION)}" /><c:set var="services" value="${chits:observations(enc, ChildCareServicesConcepts.CHILDCARE_SERVICE_TYPE)}" /><c:if test="${(not empty vaccines) or (not empty services)}"><chits_tag:foldersOf patient="${enc.patient}" />
			<tr id="eccd_enc_${enc.encounterId}">
				<td>${enc.patient.patientIdentifier}</td>
				<td>${enc.patient.personName} / ${enc.patient.gender} / <chits:age birthdate="${enc.patient.birthdate}" on="${enc.encounterDatetime}" /></td>
				<c:choose><c:when test="${not empty folders}">
				<td>${chits:coalesce(folders[0].address, '-')}</td>
				<td>${barangays[folders[0].barangayCode].name}, ${barangays[folders[0].barangayCode].municipality.name}</td>
				<td>${folders[0].code}</td>
				</c:when><c:otherwise>
				<td>-</td>
				<td>-</td>
				<td>-</td>
				</c:otherwise></c:choose>
				<td>${chits:coalesce(enc.patient.attributeMap[PhilhealthConcepts.CHITS_PHILHEALTH], '-')}</td>
				<td><c:set var="encVitalSigns" value="${chits:observations(enc, VisitConcepts.VITAL_SIGNS)}" /><c:choose><c:when test="${not empty encVitalSigns}"
					><c:forEach var="vitalSigns" items="${encVitalSigns}"><span class="vs"><chits:vitalSigns vitalSigns="${vitalSigns}" showElapsedSinceTaken="${false}" showObsDate="${false}" /></span> </c:forEach
					></c:when><c:otherwise>-</c:otherwise></c:choose
				></td>
				<td><c:forEach var="vaccine" items="${vaccines}" varStatus="i"
					><c:if test="${i.index ne 0}">, </c:if>${chits:observation(vaccine, VaccinationConcepts.ANTIGEN).valueCoded.name}</c:forEach
				></td>
				<td><c:forEach var="service" items="${services}" varStatus="i"
					><c:if test="${i.index ne 0}">, </c:if>${service.valueCoded.name}</c:forEach
				></td>
			</tr>
		</c:if></c:forEach></tbody>
		</table>
	</div>

	<div id="maternalCare">
		<br/><br/>
		<h4>MATERNAL CARE</h4>
		<table id="maternalcareReportTable" class="form">
		<thead>
			<tr>
				<th>PATIENT ID</th>
				<th>PATIENT NAME / SEX / AGE</th>
				<th>AOG<br/>(wks)</th>
				<th>POSTPARTUM WK</th>
				<th>ADDRESS</th>
				<th>BRGY</th>
				<th>FAMILY ID</th>
				<th>PHILHEALTH ID</th>
				<th>VITAL SIGNS</th>
				<th>VISIT SEQ.</th>
				<th>VACCINE(S) GIVEN</th>
				<th>SERVICE(S) GIVEN</th>
			</tr>
		</thead>
		<tbody><c:forEach var="enc" items="${encounters}"><%
					// Get all possible services rendered:

					// clear the mcProgramObs attribute
					pageContext.removeAttribute("mcProgramObs");

					final Encounter enc = (Encounter) pageContext.findAttribute("enc");
					final Obs mcObs = MaternalCareUtil.getObsForMaternalCareProgram(enc.getPatient(), enc.getDateCreated());
					if (mcObs != null) {
						final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(mcObs);
						pageContext.setAttribute("mcProgramObs", mcProgramObs);

						// store all encounter observations in a list
						pageContext.setAttribute("encObs", new ArrayList<Obs>(enc.getAllObs()));
						
						// store the visit sequence number
						pageContext.setAttribute("visitSeqNo", mcProgramObs.getVisitSequenceNumber(enc.getDateCreated()));
					}
				%><c:if test="${not empty mcProgramObs}"
				 ><c:set var="services" value="${chits:observations(enc, MCServiceRecordConcepts.SERVICE_TYPE)}"
				/><c:set var="vaccines" value="${chits:observations(enc, TetanusToxoidRecordConcepts.VACCINE_TYPE)}"
				/><c:set var="internalExams" value="${chits:filterByCodedValue(encObs, MCIERecordConcepts.INTERNAL_EXAMINATION)}"
				/><c:set var="postpartumIEs" value="${chits:filterByCodedValue(encObs, MCPostpartumIERecordConcepts.POSTDELIVERY_IE)}"
				/><c:set var="postpartumVisits" value="${chits:filterByCodedValue(encObs, MCPostPartumVisitRecordConcepts.POSTPARTUM_VISIT_RECORD)}"
				/><c:set var="prenatalVisits" value="${chits:filterByCodedValue(encObs, MCPrenatalVisitRecordConcepts.PRENATAL_VISIT_RECORD)}"
				/><c:if test="${(not empty services) or (not empty vaccines) or (not empty internalExams) or (not empty postpartumIEs) or (not empty postpartumVisits) or (not empty prenatalVisits)}"><chits_tag:foldersOf patient="${enc.patient}"
				/><c:set var="obstetricHistory" value="${mcProgramObs.obstetricHistory}"
				/><c:set var="lmpObs" value="${chits:observation(obstetricHistory.obs, MCObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD)}"
				/>
			<tr id="mcp_enc_${enc.encounterId}">
				<td>${enc.patient.patientIdentifier}</td>
				<td>${enc.patient.personName} / ${enc.patient.gender} / <chits:age birthdate="${enc.patient.birthdate}" on="${enc.encounterDatetime}" /></td>
				<td><chits:age birthdate ="${lmpObs.valueDatetime}" weeksOnly="${true}" daysOnly="${true}" on="${enc.encounterDatetime}" /></td>
				<c:choose><c:when test="${not empty chits:observation(mcProgramObs.obs, MCDeliveryReportConcepts.DELIVERY_REPORT)}"><td><chits:age birthdate ="${mcProgramObs.deliveryReport.deliveryDate}" weeksOnly="${true}" daysOnly="${true}" on="${enc.encounterDatetime}" /></td></c:when><c:otherwise><td>-</td></c:otherwise></c:choose>
				<c:choose><c:when test="${not empty folders}">
				<td>${chits:coalesce(folders[0].address, '-')}</td>
				<td>${barangays[folders[0].barangayCode].name}, ${barangays[folders[0].barangayCode].municipality.name}</td>
				<td>${folders[0].code}</td>
				</c:when><c:otherwise>
				<td>-</td>
				<td>-</td>
				<td>-</td>
				</c:otherwise></c:choose>
				<td>${chits:coalesce(enc.patient.attributeMap[PhilhealthConcepts.CHITS_PHILHEALTH], '-')}</td>
				<td><c:set var="encVitalSigns" value="${chits:observations(enc, VisitConcepts.VITAL_SIGNS)}" /><c:choose><c:when test="${not empty encVitalSigns}"
					><c:forEach var="vitalSigns" items="${encVitalSigns}"><span class="vs"><chits:vitalSigns vitalSigns="${vitalSigns}" showElapsedSinceTaken="${false}" showObsDate="${false}" /></span> </c:forEach
					></c:when><c:otherwise>-</c:otherwise></c:choose
				></td>
				<td>${visitSeqNo}</td>
				<td><c:forEach var="vaccine" items="${vaccines}" varStatus="i"
					><c:if test="${i.index ne 0}">, </c:if><c:choose
						><c:when test="${vaccine.valueCoded.conceptId eq TetanusToxoidDoseType.TT1.conceptId}">TT1</c:when
						><c:when test="${vaccine.valueCoded.conceptId eq TetanusToxoidDoseType.TT2.conceptId}">TT2</c:when
						><c:when test="${vaccine.valueCoded.conceptId eq TetanusToxoidDoseType.TT3.conceptId}">TT3</c:when
						><c:when test="${vaccine.valueCoded.conceptId eq TetanusToxoidDoseType.TT4.conceptId}">TT4</c:when
						><c:when test="${vaccine.valueCoded.conceptId eq TetanusToxoidDoseType.TT5.conceptId}">TT5</c:when
						><c:otherwise><chits_tag:obsValue obs="${vaccine}" /></c:otherwise
					></c:choose></c:forEach
				></td>
				<td><c:set var="first" value="${true}" /><c:forEach var="service" items="${services}"
					><c:if test="${not first}">, </c:if><c:set var="first" value="${false}" /><chits_tag:obsValue obs="${service}" /></c:forEach
					><c:if test="${not empty prenatalVisits}"><c:if test="${not first}">, </c:if><c:set var="first" value="${false}" />Prenatal Visit</c:if
					><c:if test="${not empty internalExams}"><c:if test="${not first}">, </c:if><c:set var="first" value="${false}" />Prenatal IE</c:if
					><c:if test="${not empty postpartumVisits}"><c:if test="${not first}">, </c:if><c:set var="first" value="${false}" />Postpartum Visit</c:if
					><c:if test="${not empty postpartumIEs}"><c:if test="${not first}">, </c:if><c:set var="first" value="${false}" />Postpartum IE</c:if
				></td>
			</tr>
		</c:if></c:if></c:forEach></tbody>
		</table>
	</div>
	
	<br/><br/><br/>
</div>

<form:form modelAttribute="form" method="post" action="dailyServicesReport.form" onsubmit="submitAjaxForm(this); return false;">
	<form:hidden path="startDate" />
	<form:hidden path="endDate" />
	<input type="submit" value="Reload" />
</form:form>

</c:otherwise></c:choose>
