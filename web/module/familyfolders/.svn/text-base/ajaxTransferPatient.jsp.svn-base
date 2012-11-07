<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<openmrs:require privilege="Edit Patients" otherwise="/login.htm" redirect="/module/chits/patients/foldersList.htm" />

<h3>Transfer Member<c:if test="${fn:length(form.patients) gt 1}">s</c:if>:
	<c:forEach var="patient" items="${form.patients}" varStatus="i"><c:if test="${i.index ne 0 and fn:length(form.patients) gt 2}">, </c:if>
	<c:if test="${i.index eq fn:length(form.patients) - 1 and fn:length(form.patients) gt 1}">and</c:if>
	${patient.personName}</c:forEach>
</h3>

<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
<c:if test="${err != null}">
	<div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div>
</c:if>

<script>
$j(document).ready(function() {
	$j("input[name='existingFolder']").change(function(){
		if ($j("input[name='existingFolder']:checked").val() == "true") {
			$j('#createNewFolder').hide()
		} else {
			$j('#createNewFolder').show()
		}
	});

	var city = undefined
	var locationFinder = new LocationFinder();
	$j("input[name='newFolder.barangayCode']").focus(function() {
		locationFinder.findBarangay(function(barangay) {
			$j("input[name='newFolder.barangayCode']").val(barangay.barangayCode);
			$j("span#ajaxBarangayName").html("(" + barangay.name + ")");
			$j("input[name='newFolder.cityCode']").val(barangay.municipality.municipalityCode);
			$j("span#ajaxCityName").html("(" + barangay.municipality.name + ")");
		}, city)
	})

	$j("input[name='newFolder.cityCode']").focus(function() {
		locationFinder.findMunicipality(function(municipality) {
			city = municipality
			$j("input[name='newFolder.cityCode']").val(municipality.municipalityCode);
			$j("span#ajaxCityName").html("(" + municipality.name + ")");
			
			if (municipality.municipalityCode / 1000 != Math.floor($j("input[name='newFolder.barangayCode']").val() / 1000)) {
				// barangay code doesn't match selected city
				$j("input[name='newFolder.barangayCode']").val("");
				$j("span#ajaxBarangayName").html("");
			}
		})
	})
})

function familyFolderSearch() {
	var dlg = $j("#findFolderDialog").dialog({width: 700,height:400,title:"Find Folder",modal:true});
	dlg.dialog('open');

	this.doSelectionHandler = function(index, data) {
		$j("input[name=transferTo]").val(data.id)
		$j("input[name=familyFolderName]").val(data.code)
		$j("#existingFolderOption").attr("checked", "checked");
		$j("#newFolderOption").removeAttr("checked");
		$j("#existingFolderOption").trigger("change");

		dlg.dialog('close');
	}

	this.doFolderSearch = function(text, resultHandler, getMatchCount, opts) {
		DWRFamilyFolderService.findCountAndFamilyFolders('', text, opts.start, opts.length, getMatchCount, resultHandler);
	}

	new OpenmrsSearch("findFolders", false, this.doFolderSearch, this.doSelectionHandler, 
			[	{fieldName:"code", header:'Family ID'},
				{fieldName:"name", header:'Name'},
				{fieldName:"headOfTheFamily", header:'HOTF'},
				{fieldName:"address", header:'Address'},
				{fieldName:"barangayName", header:'Barangay'},
				{fieldName:"cityName", header:'City'},
				{fieldName:"notes", header:'Notes'}
			], { searchLabel: 'Folder ID or name: ', minLength: 2 });
		$j("#findFolderDialog input").val($j("input[name='patient.personName.familyName']").val()).focus().keyup();
}
</script>

<spring:hasBindErrors name="form">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<form:form modelAttribute="form" method="post" id="patientForm" action="transferMembersToFamilyFolder.form" onsubmit="ajaxSubmitTransferForm(this); return false;">
	<c:forEach items="${form.patients}" varStatus="i">
		<spring:bind path="patients[${i.index}]"><form:hidden path="${status.expression}" /></spring:bind>
	</c:forEach>

	<div style="font-size: 9px">
		<table class="borderless"><tr>
			<td>*</td><td>Required Field</td></tr>
		</table>
	</div>
	<table style="width: 100%" class="form">
		<tr>
			<td valign="top" style="width:80px">Transfer to<br/>Family Folder</td>
			<td>
				<span>
					<spring:bind path="transferTo">
						<form:hidden path="${status.expression}" />
					</spring:bind>
					<spring:bind path="existingFolder">
						<form:radiobutton cssStyle="vertical-align:middle" id="existingFolderOption" path="${status.expression}" value="${true}" /> <label style="vertical-align:middle" for="existingFolderOption">Find existing record:</label>
						<input type="text" name="familyFolderName" readonly="readonly" value="${form.transferTo}" onFocus="familyFolderSearch()" />
						<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
					<br/>
					or 
					<br/>
					<spring:bind path="existingFolder">
						<form:radiobutton cssStyle="vertical-align:middle" id="newFolderOption" path="${status.expression}" value="${false}" /> <label style="vertical-align:middle" for="newFolderOption">Create a new record for the patient's folder</label>
					</spring:bind>

					<div id="createNewFolder" <c:if test="${form.existingFolder}">style="display:none"</c:if>>
					<table class="form" style="border:1px solid #ccc">
						<spring:bind path="newFolder.name">
						<tr>
							<td>Family Name*</td>
							<td><form:input path="${status.expression}" maxlength="64" /><c:if test="${status.errorMessage != ''}" ><br/><div class="error">${status.errorMessage}</div></c:if></td>
						</tr>
						</spring:bind>
						<spring:bind path="newFolder.address">
						<tr>
							<td>Address</td>
							<td><form:input path="${status.expression}" maxlength="64" size="64" /><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
						</tr>
						</spring:bind>
						<spring:bind path="newFolder.cityCode">
						<tr>
							<td>City*</td>
							<td>
								<form:input path="${status.expression}" maxlength="9" size="12" />
								<span id="ajaxCityName"><c:if test="${not empty municipalities[status.value].name}">(${municipalities[status.value].name})</c:if></span>
								<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
							</td>
						</tr>
						</spring:bind>
						<spring:bind path="newFolder.barangayCode">
						<tr>
							<td>Barangay*</td>
							<td>
								<form:input path="${status.expression}" maxlength="9" size="12" />
								<span id="ajaxBarangayName"><c:if test="${not empty barangays[status.value].name}">(${barangays[status.value].name})</c:if></span>
								<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
							</td>
						</tr>
						</spring:bind>
						<spring:bind path="newFolder.notes">
						<tr>
							<td>Notes</td>
							<td><textarea name="${status.expression}" rows="5" cols="30">${status.value}</textarea><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
						</tr>
						</spring:bind>
					</table></div>
				</span>
			</td>
		</tr>
	</table>

	<br/>
	<input type="submit" name="action" id="saveButton" value='Transfer Members' />
</form:form>
