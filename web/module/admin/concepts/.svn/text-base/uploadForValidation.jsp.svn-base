<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Concepts" otherwise="/login.htm" redirect="/module/chits/admin/concepts/validateConcepts.form" />

<spring:message var="pageTitle" code="chits.admin.concepts.validate.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<script>
function verifyAndConfirm() {
	var filename = $j("form#form input[name='file']").val()
	if (filename.length < 4 || filename.substring(filename.length - 4).toLowerCase() != ".csv") {
		$j("#confirm-non-csv-upload").dialog({
			resizable:false,height:190,modal:true,
			title:'Proceed with validation?',
			buttons:{
				"Trust me, this is a CSV file":function(){$j("form#form").submit()},
				"Cancel":function(){$j(this).dialog("close")}
			}
		})
	} else {
		$j("form#form").submit()
	}
}

function showProcessing() {
	var updateInterval
	var updateFunction = function() {
		$j.ajax({url: '/openmrs/ws/rest/chits/session/data', method: "GET", cache: false, success: function (data) {
			if (data && data['uploadConceptsInfo']) {
				$j("span.processedCounter").html(data['uploadConceptsInfo'])
			}
		}})
	}

	updateInterval = setInterval(updateFunction, 1000)
	$j("<div><h3>Records processed: <span class='processedCounter'>[unknown]</span>...<br/><br/>Please Wait...</h3>").dialog({title:'Validating...',height:'auto',width:'700px',modal:'true',close:function(){
		clearInterval(updateInterval)
	}})
}

<c:if test="${chits_session_data['uploadConceptsInfo'] ne null}">
$j(document).ready(function(){
	showProcessing()
})
</c:if>
</script>

<style>
table.example td { white-space: normal; }
</style>

<h2><spring:message code="chits.admin.concepts.validate" /></h2>	

<spring:hasBindErrors name="form">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<div style="display:none">
	<div id="confirm-non-csv-upload" title="Proceed with validation?">
		<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>The file being uploaded for validation does not appear to be a CSV file (doesn't have a '.csv' extension).<br/></br>Proceed with upload anyway?</p>
	</div>
</div>

<form:form commandName="form" enctype="multipart/form-data" method="post" onsubmit="showProcessing()">
<table>
	<tr>
		<td>Validate Concepts:</td>
		<td>
			<spring:bind path="file">
				<input type="file" size="40" name="${status.expression}" /> (*.csv)<br/>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>

			<span class="guide">
				Uploaded file must be in CSV format (preferably saved from MS Excel) containing these columns:
				<br/><br/>e.g.
					<table class="example">
						<tr><td>Name</td><td>Synonyms</td><td>Short Name</td><td>Description (form)</td><td>Class</td><td>Datatype</td><td>Answers</td><td>Contained in Sets (form)</td></tr>
						<tr><td>Systolic Blood Pressure</td><td>Systolic [en]</td><td>SBP</td><td>Numeric input of a patient's systolic blood pressure measurement (taken with a manual cuff in either a sitting or standing position)</td><td>Finding</td><td>Numeric</td><td></td><td>Vital Signs</td></tr>
						<tr><td>Diastolic Blood Pressure</td><td>Diastolic [en]</td><td>DBP</td><td>Numeric input of a patient's diastolic blood pressure measurement (taken with a manual cuff in either a sitting or standing position)</td><td>Finding</td><td>Numeric</td><td></td><td>Vital Signs</td></tr>
						<tr><td colspan="7">...</td></tr>
					</table>
			</span>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="button" value="Validate Concepts" onclick="return verifyAndConfirm()" />
		</td>
	</tr>
</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
