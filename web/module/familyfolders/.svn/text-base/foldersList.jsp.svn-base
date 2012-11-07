<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/chits/familyfolders/foldersList.htm" />

<spring:message var="pageTitle" code="chits.FamilyFolder.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/dwr/interface/DWRFamilyFolderService.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />
<%@ include file="../formStyle.jsp" %>
<script type="text/javascript">
	var lastSearch;
	jQuery(document).ready(function() {
		new OpenmrsSearch("findFolders", false, doFolderSearch, doSelectionHandler, 
			[	{fieldName:"code", header:'Family ID'},
				{fieldName:"name", header:'Name'},
				{fieldName:"headOfTheFamily", header:'HOTF'},
				{fieldName:"address", header:'Address'},
				{fieldName:"barangayName", header:'Barangay'},
				{fieldName:"cityName", header:'City'},
				{fieldName:"notes", header:'Notes'}
			],
			{ searchLabel: 'Folder ID or name: ', minLength: 2 });
		
		//set the focus to the first input box on the page(in this case the text box for the search widget)
		var inputs = document.getElementsByTagName("input");
	    if(inputs[0]) {
	    	inputs[0].focus();
		}
	});

	function doSelectionHandler(index, data) {
		document.location = "viewFolder.form?familyFolderId=" + data.id;
	}

	//searchHandler for the Search widget
	function doFolderSearch(text, resultHandler, getMatchCount, opts) {
		lastSearch = text;
		DWRFamilyFolderService.findCountAndFamilyFolders('', text, opts.start, opts.length, getMatchCount, resultHandler);
	}
</script>

<h2><spring:message code="chits.FamilyFolder.search"/></h2>	

<spring:hasBindErrors name="familyFolder">
	<spring:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<spring:message code="${error.code}" text="${error.code}" arguments="${error.arguments}"/><br/>
		</c:forEach>
	</div>
</spring:hasBindErrors>

<br />

<div>
	<b class="boxHeader">Find Family Folder(s)</b>
	<div class="box">
		<div class="searchWidgetContainer" id="findFolders"></div>
	</div>
</div>

<%-- Direclty adding a family folder is no longer supported (folders can now only be created within the context of a patient
<p>
<a href="addFolder.form" >Add a new family folder</a>
</p>
--%>

<%@ include file="/WEB-INF/template/footer.jsp" %>
