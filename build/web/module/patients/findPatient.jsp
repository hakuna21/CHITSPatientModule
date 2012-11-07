<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/chits/patients/findPatient.htm" />

<spring:message var="pageTitle" code="findPatient.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<h2><spring:message code="Patient.search"/></h2>	

<br />

<%--
<openmrs:portlet id="findPatient" url="findPatient" parameters="size=full|postURL=viewPatient.form|showIncludeVoided=false|viewType=shortEdit|hideAddNewPatient=true" />
Need more control over the find patient logic (control over search, specifically), so instead of the portlet we insert the HTML directly and change the search functions 
--%>

<openmrs:htmlInclude file="/dwr/interface/DWRPatientSearchService.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />
<%@ include file="../formStyle.jsp" %>
<%@ include file="../pleaseWait.jsp" %>

<div class='portlet' id='findPatient'>
	<script type="text/javascript">
		var lastSearch;
		$j(document).ready(function() {
			new OpenmrsSearch("findPatients", false, doPatientSearch, doSelectionHandler, 
				[	{fieldName:"identifier", header:omsgs.identifier},
					{fieldName:"givenName", header:omsgs.givenName},
					{fieldName:"middleName", header:omsgs.middleName},
					{fieldName:"familyName", header:omsgs.familyName},
					{fieldName:"age", header:omsgs.age},
					{fieldName:"gender", header:omsgs.gender},
					{fieldName:"birthdateString", header:omsgs.birthdate},
				],
				{ searchLabel: 'Patient Name:', minLength: 2 });
			
			//set the focus to the first input box on the page(in this case the text box for the search widget)
			var inputs = document.getElementsByTagName("input");
		    if(inputs[0]) {
		    	inputs[0].focus();
		    }
		});
	
		function doSelectionHandler(index, data) {
			$j("#viewPatientForm input[name=patientId]").val(data.patientId)
			$j("#viewPatientForm").submit();
		}
	
		//searchHandler for the Search widget
		function doPatientSearch(text, resultHandler, getMatchCount, opts) {
			lastSearch = text;
			DWRPatientSearchService.findCountAndPatients(text, opts.start, opts.length, getMatchCount, resultHandler);
		}
	</script>

	<div>
		<b class="boxHeader">Find Patient(s)</b>
		<div class="box">
			<div class="searchWidgetContainer" id="findPatients"></div>
		</div>
	</div>
	<p/>
	<p/>	
</div>

<form name="viewPatient" id="viewPatientForm" action="viewPatient.form" onsubmit="pleaseWaitDialog()">
	<input type="hidden" name="patientId" />
</form>

<p><a href="addPatient.form">Add Patient</a></p>
<openmrs:extensionPoint pointId="org.openmrs.findPatient" type="html" />

<%@ include file="/WEB-INF/template/footer.jsp" %>
