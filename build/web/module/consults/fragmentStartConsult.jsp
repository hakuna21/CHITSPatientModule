<%@ include file="/WEB-INF/template/include.jsp"%>
<h4>Click the Start Consult button below to start the patient's
	consult</h4>

<form name="startConsult" action="startPatientConsult.form" method="POST" onsubmit="pleaseWaitDialog()">
	<input type="hidden" name="patientId" value="${form.patient.patientId}" />
	<input type="submit" name="startConsult"
		value="<spring:message code="chits.Patient.start.consult.button"/>" />
</form>

<br />
<em> NOTE: Starting the patient's consult starts the timer for
	measuring how long the patient's consult took. This is vital for
	gathering accurate consult estimation times.</em>
