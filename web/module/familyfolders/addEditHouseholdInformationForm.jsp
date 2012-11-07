<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Edit Patients" otherwise="/login.htm" redirect="/module/chits/familyfolders/foldersList.htm" />

<spring:message var="pageTitle" code="chits.HouseholdInformation.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRFamilyFolderService.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />

<%@ include file="../formStyle.jsp" %>
<%@ include file="../pleaseWait.jsp" %>

<style>
#householdInformationForm table.borderless { margin-bottom: 0.5em; margin-top: 0.5em; }
</style>
<div style="display:none" id="family_address">${familyFolder.address}</div>
<div style="display:none" id="linked_address">${familyFolder.familiesSharingHousehold[0].address}</div>
<script>
var secondPost = false
function removeLink() {
	$j("#householdInformationForm input[name=linkToFamilyId]").val("0").removeAttr('disabled')
	var currentAddress = $j("#family_address").text()
	var linkedAddress = $j("#linked_address").text()
	if (confirm("WARNING: Family ${familyFolder.code}'s (${familyFolder.name}) address is "
			+ currentAddress + " while Family ${familyFolder.familiesSharingHousehold[0].code}'s (${familyFolder.familiesSharingHousehold[0].name}) address is "
			+ linkedAddress + ".  Are you sure you want to remove the ${familyFolder.familiesSharingHousehold[0].code} (${familyFolder.familiesSharingHousehold[0].name}) family from the household?")) {
		$j("#householdInformationForm").submit()
	}
}

function familyFolderSearch() {
	var dlg = $j("#findFolderDialog").dialog({width: 700,height:400,title:"Find Family",modal:true});
	dlg.dialog('open');

	secondPost = false;
	this.doSelectionHandler = function(index, data) {
		if (secondPost) {
			return;
		}
		
		secondPost = true;
		if ($j("#row_" + data.id).size() == 0) {
			$j("#householdInformationForm input[name=linkToFamilyId]").val(data.id).removeAttr('disabled')

			var currentAddress = $j("#family_address").text()
			if (currentAddress == data.address || confirm("WARNING: Family ${familyFolder.code}'s (${familyFolder.name}) address is "
					+ currentAddress + " while Family " + data.code + "'s (" + data.name + ") address is "
					+ data.address + ".  Are you sure they live in the same household?")) {
				$j("#householdInformationForm").submit()
			}
		} else {
			dlg.dialog('close');
		}
		setTimeout(secondPost = false, 100)
	}

	this.doFolderSearch = function(text, resultHandler, getMatchCount, opts) {
		DWRFamilyFolderService.findCountAndFamilyFolders('${familyFolder.barangayCode}', text, opts.start, opts.length, getMatchCount, resultHandler);
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
}
</script>
<h2><spring:message code="chits.HouseholdInformation.edit"/>: ${familyFolder.code}</h2>	

<spring:hasBindErrors name="familyFolder">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<br />

<div style="display:none">
	<div id="findFolderDialog">
		<b class="boxHeader">Search folder</b><div class="box"><div class="searchWidgetContainer" id="findFolders"></div></div>
	</div>
</div>

<form:form modelAttribute="familyFolder" method="post" action="updateHouseholdInformation.form" id="householdInformationForm" onsubmit="pleaseWaitDialog()">
	<form:hidden path="version" />
	<form:hidden path="familyFolderId" />
	<input type="hidden" name="linkToFamilyId" disabled="disabled" />
	<table style="width: 700px" class="form">
		<spring:bind path="householdInformation.accessToWaterSupply">
		<tr>
			<th colspan="2"><h3>Environmental Sanitation</h3></th>
		</tr>
		<tr>
			<th>Access to Improved or Safe Water Supply</th>
			<td>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="accessToWaterSupply1" value="L1" /></td><td><label for="accessToWaterSupply1">Level I (Point Source) - Household water source is a protected well, developed spring, or a rain water system with an outlet but without a distribution system</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="accessToWaterSupply2" value="L2" /></td><td><label for="accessToWaterSupply2">Level II (Communal Faucet System or Standpost) - Household water supply is composed of a source, reservoir, a piped distribution network and communal faucets located not more than 25 meters from the farthest house</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="accessToWaterSupply3" value="L3" /></td><td><label for="accessToWaterSupply3">Level III (Waterworks System) - Household water supply is composed of a source, a reservoir, a piped distributor network and household taps</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="accessToWaterSupply4" value="NONE" /></td><td><label for="accessToWaterSupply4">None in the above mentioned water supplies</label></td></tr></table>
			</td>
		</tr>
		</spring:bind>
		<spring:bind path="householdInformation.toiletFacility">
		<tr>
			<th>Household Toilet Facility</th>
			<td>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="toiletFacility1" value="L1" /></td><td><label for="toiletFacility1">Level I - No water or minimal use of water is necessary to wash the waste into the receiving space</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="toiletFacility2" value="L2" /></td><td><label for="toiletFacility2">Level II - Household toilet facility of water carriage type and water-sealed-and-flush type, with septic tank/vault disposal facilities</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="toiletFacility3" value="L3" /></td><td><label for="toiletFacility3">Level III - Household toilet facility water carriage type is connected to septic tanks and/or to sewerage system to treatment plant</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="toiletFacility4" value="NONE" /></td><td><label for="toiletFacility4">None in the abovementioned toilet facilities</label></td></tr></table>
			</td>
		</tr>
		</spring:bind>
		<spring:bind path="householdInformation.toiletLocation">
		<tr>
			<th>Household Toilet Location</th>
			<td>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="toiletLocation1" value="WITHIN_HOUSEHOLD" /></td><td><label for="toiletLocation1">Within the household</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="toiletLocation2" value="OUTHOUSE" /></td><td><label for="toiletLocation2">Outhouse</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="toiletLocation3" value="PUBLIC_FACILITY" /></td><td><label for="toiletLocation3">Public facility</label></td></tr></table>
			</td>
		</tr>
		</spring:bind>
		<spring:bind path="householdInformation.disposalOfSolidWaste">
		<tr>
			<th>Disposal of Solid Waste</th>
			<td>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="disposalOfSolidWaste1" value="COMPOSTING" /></td><td><label for="disposalOfSolidWaste1">Composting</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="disposalOfSolidWaste2" value="BURYING" /></td><td><label for="disposalOfSolidWaste2">Burying</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="disposalOfSolidWaste3" value="CITY_SYSTEM" /></td><td><label for="disposalOfSolidWaste3">City/municipality system storage, collection and disposal</label></td></tr></table>
				<table class="borderless"><tr><td><form:radiobutton path="${status.expression}" id="disposalOfSolidWaste4" value="NONE" /></td><td><label for="disposalOfSolidWaste4">None</label></td></tr></table>
			</td>
		</tr>
		</spring:bind>
	</table>

	<table class="borderless">
		<tr>
		<spring:bind path="householdInformation.dateFirstInspected">
			<td>
			Date first inspected<br/><i style="font-weight: normal; font-size: .8em;">(<spring:message code="general.format"/>: <openmrs:datePattern />)</i>
			</td><td>
			<input name="${status.expression}" onFocus="showCalendar(this,125)" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</td>
		</spring:bind>
		<spring:bind path="householdInformation.reinspectionDate">
			<td>
			Re-inspection Date<br/><i style="font-weight: normal; font-size: .8em;">(<spring:message code="general.format"/>: <openmrs:datePattern />)</i>
			</td><td>
			<input name="${status.expression}" onFocus="showCalendar(this,125)" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</td>
		</spring:bind>
		</tr>
	</table>

	<table style="width: 700px" class="form">
	<tr><td>
		<c:choose><c:when test="${empty familyFolder.familiesSharingHousehold}">
			<h3>Families sharing this household: <span id="no_other_families">none</span></h3>
			&nbsp;<input type="button" value="Find Family" onclick="familyFolderSearch()"/>
		</c:when><c:otherwise>
			<input type="button" value="Remove Link" onclick="removeLink()" style="float:right; margin-top: 0.5em;" />
			<h3>Families sharing this household:</h3>
		</c:otherwise></c:choose>

		<table id="other_families" class="form">
			<c:forEach var="folder" items="${familyFolder.familiesSharingHousehold}">
			<tr id="row_${folder.id}"><td>
				<label for="cb_${folder.id}">${folder.code} (${folder.name})</label>
			</td></tr>
			</c:forEach>
		</table>
	</td></tr>
	</table>
	<br/>&nbsp;
	<input type="submit" name="action" id="saveButton" value='Save' />
	<input type="button" name="action" id="cancelButton" onClick="document.location.href='viewFolder.form?familyFolderId=${familyFolder.familyFolderId}'" value='Cancel' />
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
