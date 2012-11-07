<%@ page buffer="128kb"
%><%@ page import="org.openmrs.Obs"
%><%@ page import="org.openmrs.module.chits.mcprogram.MaternalCareProgramObs"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<%-- Prenatal Record chart --%>
<fieldset><legend><span>PRENATAL RECORD</span></legend>
<jsp:include page="chartfragments/prenatalRecord.jsp" />
<table class="full-width borderless">
<tbody><tr><td style="text-align: right"><input id="update-prenatal-record" type="button" value="Add" onclick='loadAjaxForm("addPrenatalVisitRecord.form?patientId=${form.patient.patientId}", "NEW PRENATAL CHECKUP RECORD", ${form.patient.patientId}, 570);' /></td></tr>
</tbody></table>
</fieldset>

<%-- Internal Examination Record chart --%>
<fieldset><legend><span>INTERNAL EXAMINATION RECORD</span></legend>
<jsp:include page="chartfragments/internalExaminationRecord.jsp" />
<table class="full-width borderless">
<tbody><tr><td style="text-align: right"><input id="update-internal-examination-record" type="button" value="Add" onclick='loadAjaxForm("addInternalExaminationRecord.form?patientId=${form.patient.patientId}", "NEW INTERNAL EXAMINATION RECORD", ${form.patient.patientId}, 360);' /></td></tr>
</tbody></table>
</fieldset>

<%-- Birth Plan Chart --%>
<jsp:include page="chartfragments/birthPlanChart.jsp" />

<%-- Delivery Report --%>
<jsp:include page="chartfragments/deliveryReport.jsp" />

<%-- Postpartum Record --%>
<c:set var="deliveryReportObs" value="${chits:observation(form.mcProgramObs.obs, MCDeliveryReportConcepts.DELIVERY_REPORT)}" />
<c:set var="postPartumDisabled" value="${empty deliveryReportObs.dateCreated}" />
<fieldset><legend><span>POST PARTUM RECORD</span><c:if test="${postPartumDisabled}"> (disabled)</c:if></legend>
<jsp:include page="chartfragments/postpartumRecord.jsp" />
<table class="full-width borderless">
<tbody><tr><td style="text-align: right"><input id="update-postpartum-record" type="button" <c:if test="${postPartumDisabled}">disabled="disabled" </c:if> value="Add" onclick='loadAjaxForm("addPostPartumVisitRecord.form?patientId=${form.patient.patientId}", "NEW POSTPARTUM CHECKUP RECORD", ${form.patient.patientId}, 370);' /></td></tr>
</tbody></table>
</fieldset>

<hr/>

<fieldset><legend><span>SERVICE RECORD</span></legend>
(Click on the date of service to view details)<br/>
<br/>

<%-- Tetanus Toxoid Vaccination --%>
<jsp:include page="../common/tetanusToxoidVaccinationChart.jsp" />

<c:set   var="tt1Obs" value="${chits:observation(form.patient, TetanusToxoidDateAdministeredConcepts.TT1)}"
/><c:set var="tt2Obs" value="${chits:observation(form.patient, TetanusToxoidDateAdministeredConcepts.TT2)}"
/><c:set var="tt3Obs" value="${chits:observation(form.patient, TetanusToxoidDateAdministeredConcepts.TT3)}"
/><c:set var="tt4Obs" value="${chits:observation(form.patient, TetanusToxoidDateAdministeredConcepts.TT4)}"
/><c:set var="tt5Obs" value="${chits:observation(form.patient, TetanusToxoidDateAdministeredConcepts.TT5)}"
/><c:if test="${empty tt1Obs.valueDatetime or empty tt2Obs.valueDatetime or empty tt3Obs.valueDatetime or empty tt4Obs.valueDatetime or empty tt5Obs.valueDatetime}">
<table class="full-width borderless">
<tbody><tr><td style="text-align: right"><input id="update-tetanus-service" type="button" value="Update" onclick='loadAjaxForm("addTetanusServiceRecord.form?patientId=${form.patient.patientId}", "TETANUS VACCINATION", ${form.patient.patientId}, 370);' /></td></tr>
</tbody></table>
</c:if>

<br/>

<%-- Iron Supplementation --%>
<jsp:include page="../common/ironSupplementationChart.jsp" />

<table class="full-width borderless">
<tbody><tr><td style="text-align: right"><input id="update-iron-supplementation-record" type="button" value="Update" onclick='loadAjaxForm("addMaternalCareIronSupplementationServiceRecord.form?patientId=${form.patient.patientId}", "Iron Supplementation", ${form.patient.patientId}, 470);' /></td></tr>
</tbody></table>

<%-- Vitamin A Supplementation --%>
<jsp:include page="../common/vitaminASupplementationChart.jsp" />

<table class="full-width borderless">
<tbody><tr><td style="text-align: right"><input id="update-vitamin-a-record" type="button" value="Update" onclick='loadAjaxForm("addMaternalCareVitaminAServiceRecord.form?patientId=${form.patient.patientId}", "Vitamin A", ${form.patient.patientId}, 370);' /></td></tr>
</tbody></table>

<%-- Deworming --%>
<jsp:include page="../common/dewormingChart.jsp" />

<table class="full-width borderless">
<tbody><tr><td style="text-align: right"><input id="update-deworming-record" type="button" value="Update" onclick='loadAjaxForm("addMaternalCareDewormingServiceRecord.form?patientId=${form.patient.patientId}", "Deworming", ${form.patient.patientId}, 370);' /></td></tr>
</tbody></table>

<%-- Dental Chart --%>
<jsp:include page="../common/dentalChart.jsp" />

</fieldset>

<hr/>

<fieldset><legend><span>OBSTETRIC DATA</span></legend>
<div>
	Obstetric Score: <chits_tag:obstetricScore obsGroup="${form.mcProgramObs.obstetricHistory.obs}" full="${true}" />
	<br/>Obstetric history details
</div>
<div class="obstetric-history-details" class="registration grouped-block">
	<jsp:include page="ajaxObstetricHistoryDetails.jsp" />
</div>
<table class="full-width borderless">
<tbody><tr>
	<td>NOTE: <chits_tag:obsValue obs="${chits:observation(form.mcProgramObs.obstetricHistory.obs, MCObstetricHistoryConcepts.PREGNANCY_TEST_RESULT)}" /></td>
	<td style="text-align: right"><input id="update-obstetric-history-record" type="button" value="Edit" onclick="loadAjaxForm('editObstetricHistory.form?patientId=${form.patient.patientId}', 'EDIT OBSTETRIC HISTORY', ${form.patient.patientId}, 430, 'secondLevelGeneralForm')" /></td></tr>
</tbody></table>
</fieldset>

<fieldset><legend><span>MATERNAL CARE HISTORY CHARTS</span></legend>
<div class="indent">
<ul>
	<c:set var="endedPatientConsults" value="${chits:filterByCodedValue(chits:observations(form.patient, MCPatientConsultStatus.STATUS), MaternalCareProgramStates.ENDED)}"
	/><c:choose><c:when test="${not empty endedPatientConsults}"
		><c:forEach var="consultStatusObs" items="${endedPatientConsults}"
			><c:set var="mcProgramObs" value="${consultStatusObs.obsGroup.obsGroup}" /><% 
				final MaternalCareProgramObs maternalCareProgramObs = new MaternalCareProgramObs((Obs) pageContext.findAttribute("mcProgramObs"));
				request.setAttribute("maternalCareProgramObs", maternalCareProgramObs);
			%><li><em><a href="#" onclick='loadHistoryChart("viewMaternalCareHistoryChart.form?patientId=${form.patient.patientId}&maternalCareProgramObsId=${mcProgramObs.obsId}", "VIEW MATERNAL CARE HISTORY CHART", ${form.patient.patientId}, 1024); return false;'>maternal care program <chits_tag:obstetricScore obsGroup="${maternalCareProgramObs.obstetricHistory.obs}" /> <fmt:formatDate pattern="MM/dd/yyyy" value="${chits:findPatientProgramState(maternalCareProgramObs.patientProgram, MaternalCareProgramStates.REGISTERED).startDate}" /></a></em></li>
		</c:forEach></c:when><c:otherwise>
		<li><em>Not previously enrolled in maternal care program</em></li>
		</c:otherwise></c:choose>
</ul>
</div>
</fieldset>

<br/>

<c:choose><c:when test="${not form.mcProgramObs.readOnly and not form.programConcluded and form.openSystempPromptedConclusion}">
	<div class="system-prompted-conclusion" style="display:none">
		<fieldset><legend><span>System Prompted Conclusion</span></legend>
			The postpartum stage ended on <fmt:formatDate pattern="MMM d, yyyy" value="${form.postpartumStageEndDate}" />.
			<br/><br/>
			You may now conclude the program after making sure that all data about the patient's maternal care has been entered.
			<br/><br/>
			Would you like to END THE PROGRAM NOW OR LATER?
		</fieldset>
		<br/>
		<div style="text-align: center; white-space: nowrap;">
			<input type="button" id="endNowButton" value='END NOW' onclick='$j("div.system-prompted-conclusion").dialog("close"); loadAjaxForm("endMaternalCareProgram.form?patientId=${form.patient.patientId}", "END MATERNAL CARE PROGRAM", ${form.patient.patientId}, 470)' />
			<input type="button" id="endLaterButton" value='END LATER' onclick='$j("div.system-prompted-conclusion").dialog("close")' />
		</div>
	</div>

	<script>
	$j(document).ready(function() {
		$j("div.system-prompted-conclusion").dialog({title:'End Program?',width: 300}).dialog("open")
	})
	</script>
</c:when><c:when test="${not form.mcProgramObs.readOnly and not form.programConcluded and form.deliveryReportNeeded}">
	<script>
	$j(document).ready(function() {
		loadAjaxForm("editDeliveryReport.form?patientId=${form.patient.patientId}", "EDIT DELIVERY REPORT", ${form.patient.patientId}, 900)
	})
	</script>
</c:when></c:choose>