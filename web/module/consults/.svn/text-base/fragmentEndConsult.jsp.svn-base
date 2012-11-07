<%@ include file="/WEB-INF/template/include.jsp"%>
<h4>Click the End Consult button below to end the patient's consult</h4>

<form name="endConsult" action="endPatientConsult.form" onsubmit="pleaseWaitDialog()">
	<input type="hidden" name="patientId" value="${form.patient.patientId}" />
	<input type="submit" name="endConsult"
		value="<spring:message code="chits.Patient.end.consult.button"/>" />
</form>

<br />
<em> NOTE: Ending the patient's consult creates a record of how
	long the patient's consult took. This is vital for gathering accurate
	consult estimation times.</em>
