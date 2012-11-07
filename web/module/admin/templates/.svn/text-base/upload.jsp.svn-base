<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Concepts" otherwise="/login.htm" redirect="/module/chits/admin/templates/uploadTemplates.form" />

<spring:message var="pageTitle" code="chits.admin.templates.upload.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<script>
function verifyAndConfirm() {
	var filename = $j("form#form input[name='file']").val()
	if (filename.length < 4 || filename.substring(filename.length - 4).toLowerCase() != ".zip") {
		$j("#confirm-non-zip-upload").dialog({
			resizable:false,height:190,modal:true,
			title:'Proceed with upload?',
			buttons:{
				"Trust me, this is a ZIP file":function(){$j("form#form").submit()},
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
			if (data && data['uploadTemplatesInfo']) {
				$j("span.processedCounter").html(data['uploadTemplatesInfo'])
			}
		}})
	}

	updateInterval = setInterval(updateFunction, 1000)
	$j("<div><h3>Templates processed: <span class='processedCounter'>[unknown]</span>...<br/><br/>Please Wait...</h3>").dialog({title:'Uploading...',height:'auto',width:'700px',modal:'true',close:function(){
		clearInterval(updateInterval)
	}})
}

<c:if test="${chits_session_data['uploadTemplatesInfo'] ne null}">
$j(document).ready(function(){
	showProcessing()
})
</c:if>
</script>

<h2><spring:message code="chits.admin.templates.upload" /></h2>	

<spring:hasBindErrors name="form">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<div style="display:none">
	<div id="confirm-non-zip-upload" title="Proceed with upload?">
		<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>The file being uploaded does not appear to be a ZIP file (doesn't have a '.zip' extension).<br/></br>Proceed with upload anyway?</p>
	</div>
</div>

<form:form commandName="form" enctype="multipart/form-data" method="post" onsubmit="showProcessing()">
<table>
	<tr>
		<td>Upload Note Templates ZIP file:</td>
		<td>
			<spring:bind path="file">
				<input type="file" size="40" name="${status.expression}" /> (*.zip)<br/>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>

			<span class="guide">
				Uploaded file must be in ZIP format containing this exact folder structure:
				<br/><br/>e.g.:
					<ul>
						<li>Templates
							<ul>
								<li>History Templates
									<ul>
										<li>Adult Family History.txt <em><strong>(this
													is a sample only...)</strong></em>
										<li>...</em>
									</ul>
								<li>Complaint Templates
									<ul>
										<li>...</em>
									</ul>
								<li>Diagnosis Templates
									<ul>
										<li>...</em>
									</ul>
								<li>Physical Exam Templates
									<ul>
										<li>...</em>
									</ul>
								<li>Treatment Plan Templates
									<ul>
										<li>...</em>
									</ul>
							</ul>
					</ul> <br /> <br />NOTE: Only the text files in those specific
					folders will be processed. The name of the text file will
					correspond to the name of the template that will appear in the
					drop-down list of templates for each corresponding category.
			</span>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="button" value="Upload Note Templates" onclick="return verifyAndConfirm()" />
		</td>
	</tr>
</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
