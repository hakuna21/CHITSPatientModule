<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/module/chits/reports/queries.htm" />

<spring:message var="pageTitle" code="chits.reports.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.chits.helper.js" />
<%@ include file="../formStyle.jsp" %>
<%@ include file="../pleaseWait.jsp" %>

<style>
table.form th { font-size: 7pt; }
table.form td { font-size: 7pt; }
</style>
<script>
function loadAjaxForm(formAction, titleText, width) {
	if (width == undefined) { width = 580}
	$j("#generalForm").loadAndCenter("<h4>Loading, Please Wait...</h4>", {'width':width,'title':titleText,'closeOnEscape':false})
	var handler
	$j.ajax({url: formAction, cache: false, success: handler = function (data) {
		$j('#generalForm').loadAndCenter(data)
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function submitAjaxForm(form) {
	var postData = {}
	$j(form).find("input[type=text], input[type=hidden], select, textarea").each(function() {
		if (!$j(this).is(":disabled")) {
			postData[$j(this).attr('name')] = $j(this).val()
		}
	})

	pleaseWaitDialog()
	var handler
	$j.ajax({url: form.action, type: 'POST', data: postData, cache: false, success: handler = function (data) {
		closePleaseWaitDialog()
		var div = $j('#generalForm').loadAndCenter(data, {closeOnEscape:false})
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

$j(document).ready(function() {
	$j("#queries").tabs({selected:1})
})
</script>

<h2><spring:message code="chits.reports.title"/></h2>	

<div id="queries">
	<ul>
		<li><a href="#targetClientList">Target Client List</a></li>
		<li><a href="#summaryReports">Summary Reports</a></li>
		<li><a href="#graphs">Graphs</a></li>
	</ul>

	<div id="targetClientList">
		<em><strong>[This section is under construction]</strong></em>
	</div>

	<div id="summaryReports">
		<h3>Summary Reports</h3>
		<ol>
			<li><a href="javascript:void(0)" onclick="loadAjaxForm('dailyServicesReport.form', 'Generate Daily Service Report', 260)">Daily Service Report</a></li>
			<li><a href="javascript:void(0)" onclick="loadAjaxForm('newlyRegisteredPatientsReport.form', 'Generate Newly Registered Patients Report', 260)">Newly Registered Patients</a></li>
		</ol>
		
		<h3>Registries</h3>
		<ol>
			<li>Patient Registry
				<ul>
				<li><a href="javascript:void(0)" onclick="loadAjaxForm('masterListOfPatientsReport.form', 'Master List of Patients Report', 1000)">Master List of Patients</a></li>
				<li>List of Patients per Barangay</li>
				</ul>
			</li>
			<li>Family Registry
				<ul>
				<li><a href="javascript:void(0)" onclick="loadAjaxForm('masterListOfFamiliesReport.form', 'Master List of Families Report', 1000)">Master List of Families</a></li>
				<li>List of Families per Barangay</li>
				</ul>
			</li>
			<li>Household Registry
				<ul>
				<li>List of Households per Barangay</li>
				</ul>
			</li>
		</ol>
	</div>

	<div id="graphs">
		<em><strong>[This section is under construction]</strong></em>
	</div>
</div>

<div style="display:none">
	<div id="generalForm"></div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>