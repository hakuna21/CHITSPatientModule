<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />

<div id="registration-header">
	<div class="header">
		<h3>FAMILY PLANNING PROGRAM</h3>
		<h3>Registration Form</h3>
	</div>

	<br />
	<div class="header-note">IMPORTANT: There are seven pages of
		information that should be filled out before the patient may be
		officially enrolled in the Family Planning Program. Until the patient
		is enrolled, recording of services may not be done.</div>
</div>

<script>
function cancelForm() {
	document.location.href = 'viewPatient.form?patientId=${form.patient.patientId}'
}

$j(document).ready(function() {
	$j("form.main-form").submit(function() {
		 pleaseWaitDialog()
	})
})
</script>