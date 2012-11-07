<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>
<table>
<tr><td valign="top" style="width: 33%">
	<%-- Prenatal Record chart --%>
	<fieldset><legend><span>PRENATAL RECORD</span></legend>
	<jsp:include page="chartfragments/prenatalRecord.jsp" />
	</fieldset>
	
	<%-- Internal Examination Record chart --%>
	<fieldset><legend><span>INTERNAL EXAMINATION RECORD</span></legend>
	<jsp:include page="chartfragments/internalExaminationRecord.jsp" />
	</fieldset>
	
	<%-- Birth Plan Chart --%>
	<jsp:include page="chartfragments/birthPlanChart.jsp" />
	
	<%-- Delivery Report --%>
	<jsp:include page="chartfragments/deliveryReport.jsp" />
	
	<%-- Postpartum Record --%>
	<fieldset><legend><span>POST PARTUM RECORD</span></legend>
	<jsp:include page="chartfragments/postpartumRecord.jsp" />
	</fieldset>
		
	<fieldset><legend><span>SERVICE RECORD</span></legend>
	(Click on the date of service to view details)<br/>
	<br/>
	
	<%-- Tetanus Toxoid Vaccination --%>
	<jsp:include page="../common/tetanusToxoidVaccinationChart.jsp" />
	
	<br/>
	
	<%-- Iron Supplementation --%>
	<jsp:include page="../common/ironSupplementationChart.jsp" />
	
	<%-- Vitamin A Supplementation --%>
	<jsp:include page="../common/vitaminASupplementationChart.jsp" />
	
	<%-- Deworming --%>
	<jsp:include page="../common/dewormingChart.jsp" />
	
	<%-- Dental Chart --%>
	<jsp:include page="../common/dentalChart.jsp" />
	</fieldset>
		
</td><td valign="top" style="width: 33%">
	
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
	</tbody></table>
	</fieldset>

	<%-- Main Registration information --%>
	<fieldset><legend><span>MATERNAL CARE PROGRAM CHART</span></legend>
	<jsp:include page="chartfragments/registrationInformation.jsp" />
	</fieldset>

	<%-- Referral Feedback Notes --%>
	<jsp:include page="chartfragments/referralFeedbackNotes.jsp" />

	<%-- Important Dates --%>
	<jsp:include page="chartfragments/importantDates.jsp" />
	
	<%-- Patient consult status history --%>
	<jsp:include page="chartfragments/consultStatusHistory.jsp" />
	
	<%-- Tetanus Status --%>
	<jsp:include page="chartfragments/tetanusStatus.jsp" />
	
</td><td valign="top" style="width: 33%">

	<%-- Danger Signs and Medical Conditions--%>
	<jsp:include page="chartfragments/dangerSignsAndMedicalConditions.jsp" />
	
	<%-- Last Prenatal Visit --%>
	<jsp:include page="chartfragments/lastPrenatalVisit.jsp" />
	
	<%-- Last IE Results --%>
	<jsp:include page="chartfragments/lastIEResults.jsp" />
	
	<%-- Other registration information --%>
	<jsp:include page="chartfragments/otherRegistrationInformation.jsp" />
</td></tr>
</table>