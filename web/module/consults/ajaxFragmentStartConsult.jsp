<%@ page buffer="128kb"
%><br/><br/>
<form name="startConsult" action="startPatientConsult.form" method="POST" onsubmit="pleaseWaitDialog()">
	<input type="hidden" name="patientId" value="${form.patient.patientId}" />
	<input type="submit" name="startConsult" value="<spring:message code="chits.Patient.start.consult.button"/>" />
</form>