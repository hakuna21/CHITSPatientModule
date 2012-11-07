<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<%-- Maternal Care Program Chart --%>
<jsp:include page="chartfragments/maternalCareProgramChart.jsp" />

<br/>
<div class="indent">
Patient Consult Status: <a href="#" style="color: #44A; font-weight: bold;" onclick='loadAjaxForm("changePatientConsultStatus.form?patientId=${form.patient.patientId}", "UPDATE PATIENT CONSULT STATUS", ${form.patient.patientId}, 540); return false;'>${consultState}</a>
</div>

<%-- Referral Feedback Notes --%><%-- REFERRAL FORM HAS BEEN SCRAPPED FOR NOW
<jsp:include page="chartfragments/referralFeedbackNotes.jsp" />
--%>

<%-- Important Dates --%>
<jsp:include page="chartfragments/importantDates.jsp" />

<%-- Tetanus Status --%>
<jsp:include page="chartfragments/tetanusStatus.jsp" />

<%-- Danger Signs and Medical Conditions--%>
<jsp:include page="chartfragments/dangerSignsAndMedicalConditions.jsp" />

<%-- Last Prenatal Visit --%>
<jsp:include page="chartfragments/lastPrenatalVisit.jsp" />

<%-- Last IE Results --%>
<jsp:include page="chartfragments/lastIEResults.jsp" />

<%-- Other registration information --%>
<jsp:include page="chartfragments/otherRegistrationInformation.jsp" />
