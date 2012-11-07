<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Edit Patients" otherwise="/login.htm" redirect="/module/chits/familyfolders/foldersList.htm" />

<spring:message var="pageTitle" code="chits.FamilyFolder.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRBarangayService.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/LocationFinder.js" />

<openmrs:htmlInclude file="/dwr/interface/DWRPatientService.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.chits.helper.js" />
<%@ include file="../formStyle.jsp" %>
<%@ include file="../pleaseWait.jsp" %>

<script>
$j(document).ready(function() {
	<c:choose><c:when test="${not empty municipalities[familyFolder.cityCode].name}">
	var city = {
		name: "${municipalities[familyFolder.cityCode].name}",
		municipalityCode: "${familyFolder.cityCode}"
	};
	</c:when><c:otherwise>
	var city = undefined
	</c:otherwise></c:choose>
	var locationFinder = new LocationFinder();
	$j("input[name='barangayCode']").focus(function() {
		locationFinder.findBarangay(function(barangay) {
			$j("input[name='barangayCode']").val(barangay.barangayCode);
			$j("span#barangayName").html("(" + barangay.name + ")");
			$j("input[name='cityCode']").val(barangay.municipality.municipalityCode);
			$j("span#cityName").html("(" + barangay.municipality.name + ")");
			setTimeout( function() { $j("textarea[name='notes']").focus(); }, 0 );
		}, city)
	})

	$j("input[name='cityCode']").focus(function() {
		locationFinder.findMunicipality(function(municipality) {
			city = municipality
			$j("input[name='cityCode']").val(municipality.municipalityCode);
			$j("span#cityName").html("(" + municipality.name + ")");
			
			if (municipality.municipalityCode / 1000 != Math.floor($j("input[name='barangayCode']").val() / 1000)) {
				// barangay code doesn't match selected city
				$j("input[name='barangayCode']").val("");
				$j("span#barangayName").html("");
			}
			
			// setTimeout( function() { $j("input[name='barangayCode']").focus(); }, 0 );
		})
	})
})

function transferSelectedPatients() {
	if ($j("form#familyFolderForm input[type='checkbox']:checked").size() == 0) {
		alert("Please select at least one member to transfer")
		return;
	}

	var data = ""
	$j("form#familyFolderForm input[type='checkbox']:checked").each(function() {
		data += "&patients=" + $j("form#familyFolderForm tr#" + $j(this).val() + " input[type=hidden]").val()
		// $j("form#familyFolderForm tr#" + $j(this).val()).remove()
	})
	
	$j('#transferMembers').loadAndCenter("<h4>Loading, Please Wait...</h4>", {width:600,title:"Transfer To Family Folder"})
	$j.ajax({url: 'transferMembersToFamilyFolder.form', cache: false, data: data, success: function (data) {
		$j('#transferMembers').loadAndCenter(data)
		highlightErrors()
	}})
}

function ajaxSubmitTransferForm(form) {
	var postData = {}
	$j(form).find("input[type=text], input[type=hidden], input[type=radio]:checked").each(function() {
		postData[$j(this).attr('name')] = $j(this).val()
	})

	pleaseWaitDialog()
	$j.post('transferMembersToFamilyFolder.form', postData, function (data) {
		closePleaseWaitDialog()
		var div = $j('#transferMembers').loadAndCenter(data)
		highlightErrors()
		
		if ($j("#transferMembers div.openmrs_msg").size() > 0) {
			$j('#transferMembers').dialog('close');
			$j("form#familyFolderForm input[type='checkbox']:checked").each(function() {
				$j("form#familyFolderForm tr#" + $j(this).val()).remove()
			})
		}
	})
}

function patientSearch() {
	var dlg = $j("#findPatientDialog").dialog({width: 700,height:400,title: "Add Member",modal: true});
	dlg.dialog('open');

	this.doSelectionHandler = function(index, data) {
		if ($j('#member_' + data.patientId).length == 0) {
			$j("table#patient_members").append($j(
				'<tr id="member_' + data.patientId + '" style="vertical-align: middle;">' +
				'<td style="text-align:center"><input type="checkbox" id="cb_' + data.patientId + '" value="member_' + data.patientId + '" style="vertical-align:middle"/></td>' +
				'<td style="text-align:center"><input type="radio" name="headOfTheFamily" value="' + data.patientId + '" title="Select to set as head of the family"></td>' +
				'<td><label for="cb_' + data.patientId + '">(' + data.gender + ', ' + (data.age ? data.age : 'no entered data') + ') ' + data.personName + ' <input type="hidden" name="patientIds[]" value="' + data.patientId + '" /></label></td>' +
				'<td><label for="cb_' + data.patientId + '">' + data.identifier + '</label></td>' +
				'</tr>'
			));
		}

		dlg.dialog('close');
	}
	
	//searchHandler for the Search widget
	this.doPatientSearch = function(text, resultHandler, getMatchCount, opts) {
		DWRPatientService.findCountAndPatients(text, opts.start, opts.length, getMatchCount, resultHandler);
	}

	new OpenmrsSearch("findPatients", false, this.doPatientSearch, this.doSelectionHandler, 
		[	{fieldName:"identifier", header:omsgs.identifier},
			{fieldName:"givenName", header:omsgs.givenName},
			{fieldName:"middleName", header:omsgs.middleName},
			{fieldName:"familyName", header:omsgs.familyName},
			{fieldName:"age", header:omsgs.age},
			{fieldName:"gender", header:omsgs.gender},
			{fieldName:"birthdateString", header:omsgs.birthdate}
		], {  searchLabel: 'Patient Name: ', minLength: 2 });
	$j("#findPatientDialog input").val($j("input[name='name']").val()).focus().keyup();
}
</script>
<h2><c:choose><c:when test="${empty familyFolder.id}"><spring:message code="chits.FamilyFolder.add"/></c:when><c:otherwise><spring:message code="chits.FamilyFolder.update"/></c:otherwise></c:choose></h2>	

<spring:hasBindErrors name="familyFolder">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<br />

<div style="display:none">
	<div id="findPatientDialog">
		<b class="boxHeader">Find Patient(s)</b><div class="box"><div class="searchWidgetContainer" id="findPatients"></div></div>
	</div>
	<div id="findFolderDialog">
		<b class="boxHeader">Search folder</b><div class="box"><div class="searchWidgetContainer" id="findFolders"></div></div>
	</div>
	<div id="transferMembers"></div>
</div>

<form method="post" id="familyFolderForm" onsubmit="pleaseWaitDialog()">
	<input type="hidden" name="version" value="${familyFolder.version}" />
	<div style="font-size: 9px">* - Required Field</div>
	<table style="width: 700px" class="form">
		<tr>
			<th>Family ID</th>
			<th>${familyFolder.code}</th>
		</tr>
		<spring:bind path="familyFolder.name">
		<tr>
			<td>Family Name*</td>
			<td><input name="name" maxlength="64" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<spring:bind path="familyFolder.address">
		<tr>
			<td>Address</td>
			<td><input name="address" maxlength="64" size="64" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<spring:bind path="familyFolder.cityCode">
		<tr>
			<td>City*</td>
			<td>
				<input name="cityCode" maxlength="9" size="12" value="${status.value}"/>
				<span id="cityName"><c:if test="${not empty municipalities[status.value].name}">(${municipalities[status.value].name})</c:if></span>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</td>
		</tr>
		</spring:bind>
		<spring:bind path="familyFolder.barangayCode">
		<tr>
			<td>Barangay*</td>
			<td>
				<input name="barangayCode" maxlength="9" size="12" value="${status.value}"/>
				<span id="barangayName"><c:if test="${not empty barangays[status.value].name}">(${barangays[status.value].name})</c:if></span>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</td>
		</tr>
		</spring:bind>
		<spring:bind path="familyFolder.notes">
		<tr>
			<td>Notes</td>
			<td><textarea name="notes" rows="5" cols="30">${status.value}</textarea><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>

		<c:if test="${not empty familyFolder.id}">
		<tr>
			<td colspan="2">
				<%@ include file="fragmentHouseholdInformation.jsp" %>
				&nbsp;<input type="button" name="action" id="editHouseholdInformationButton" onclick="javascript:document.location.href='updateHouseholdInformation.form?familyFolderId=${familyFolder.id}'" value='<spring:message code="chits.HouseholdInformation.edit"/>' />
				<br/>&nbsp;
			</td>
		</tr>
		</c:if>

		<tr>
			<td colspan="2"><h3>Members:</h3>
			<div style="font-size: 9px">&nbsp;&nbsp;HOTF = Head of the family</div>
			<table id="patient_members">
				<tr>
					<th>&nbsp;</th>
					<th title="Head of the family">HOTF</th>
					<th style="width:80%">Name</th>
					<th>Identifier</th>
				</tr>
				<c:forEach var="patient" items="${familyFolder.patients}">
				<tr id="member_${patient.id}" style="vertical-align: middle;">
					<td style="text-align:center"><input type="checkbox" id="cb_${patient.id}" value="member_${patient.id}" style="vertical-align:middle"/></td>
					<td style="text-align:center"><input type="radio" name="headOfTheFamily" value="${patient.id}" title="Select to set as head of the family"<c:if test="${familyFolder.headOfTheFamily == patient}"> checked</c:if>></td>
					<td><label for="cb_${patient.id}">(${patient.gender}, <chits:age birthdate="${patient.birthdate}" />) ${patient.personName} <input type="hidden" name="patientIds[]" value="${patient.id}" /></label></td>
					<td><label for="cb_${patient.id}">${patient.patientIdentifier}</label></td>
				</tr>
				</c:forEach>
			</table>
			&nbsp;<input type="button" value="Transfer Selected" onclick="javascript:transferSelectedPatients()"> <input type="button" value="Add Member" onclick="javascript:patientSearch()">
			<br/>&nbsp;
			</td>
		</tr>
	</table>
	<br/>
	<c:choose><c:when test="${empty familyFolder.id}">
		<input type="submit" name="action" id="saveButton" value='<spring:message code="chits.FamilyFolder.create"/>' />
		<input type="button" name="action" id="cancelButton" onClick="document.location.href='foldersList.htm'" value='<spring:message code="chits.FamilyFolder.cancel"/>' />
	</c:when><c:otherwise>
		<input type="submit" name="action" id="editButton" value='<spring:message code="chits.FamilyFolder.update"/>' />
		<input type="button" name="action" id="cancelButton" onClick="document.location.href='viewFolder.form?familyFolderId=${familyFolder.id}'" value='<spring:message code="chits.FamilyFolder.cancel"/>' />
	</c:otherwise></c:choose>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
