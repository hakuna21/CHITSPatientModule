<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Concepts" otherwise="/login.htm" redirect="/module/chits/admin/icd10/uploadICD10Codes.form" />

<spring:message var="pageTitle" code="chits.admin.icd10.upload.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<script>
function verifyAndConfirm() {
	var filename = $j("form#form input[name='file']").val()
	if (filename.length < 4 || filename.substring(filename.length - 4).toLowerCase() != ".csv") {
		$j("#confirm-non-csv-upload").dialog({
			resizable:false,height:190,modal:true,
			title:'Proceed with upload?',
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
			if (data && data['uploadICD10CodesInfo']) {
				$j("span.processedCounter").html(data['uploadICD10CodesInfo'])
			}
		}})
	}

	updateInterval = setInterval(updateFunction, 1000)
	$j("<div><h3>Records processed: <span class='processedCounter'>[unknown]</span>...<br/><br/>Please Wait...</h3>").dialog({title:'Uploading...',height:'auto',width:'700px',modal:'true',close:function(){
		clearInterval(updateInterval)
	}})
}

<c:if test="${chits_session_data['uploadICD10CodesInfo'] ne null}">
$j(document).ready(function(){
	showProcessing()
})
</c:if>
</script>

<h2><spring:message code="chits.admin.icd10.upload" /></h2>	

<spring:hasBindErrors name="form">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<div style="display:none">
	<div id="confirm-non-csv-upload" title="Proceed with upload?">
		<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>The file being uploaded does not appear to be a CSV file (doesn't have a '.csv' extension).<br/></br>Proceed with upload anyway?</p>
	</div>
</div>

<form:form commandName="form" enctype="multipart/form-data" method="post" onsubmit="showProcessing()">
<table>
	<tr>
		<td>Upload ICD10 codes:</td>
		<td>
			<spring:bind path="file">
				<input type="file" size="40" name="${status.expression}" /> (*.csv)<br/>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>

			<span class="guide">
				Uploaded file must be in CSV format (preferably saved from MS Excel) containing two columns:
				<br/><br/>e.g. (for ICD10 symptom codes):
					<table>
						<tr><td>CODE</td><td>SIGN/SYMPTOMS</td></tr>
						<tr><td>R10</td><td>Abdominal and Pelvic Pain</td></tr>
						<tr><td>R10.0</td><td>Acute Abdomen</td></tr>
						<tr><td>R10.1</td><td>Pain Localized to upper abdomen</td></tr>
						<tr><td colspan="2">...</td></tr>
					</table>
				<br/><br/>e.g. (for ICD10 diagnosis codes):
					<table>
						<tr><td>CODE</td><td>DIAGNOSIS</td></tr>
						<tr><td>120</td><td>Angina</td></tr>
						<tr><td>120.0</td><td>Unstable angina</td></tr>
						<tr><td>120.1</td><td>Angina pectoris with documented spasm</td></tr>
						<tr><td colspan="2">...</td></tr>
					</table>
			</span>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="button" value="Upload ICD10 codes" onclick="return verifyAndConfirm()" />
		</td>
	</tr>
</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
