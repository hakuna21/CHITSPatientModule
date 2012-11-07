<%@ page buffer="128kb"
%><%@ page import="java.util.Date"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:htmlInclude file="/dwr/interface/DWRFemalePatientSearchService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRMalePatientSearchService.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />

<c:choose><c:when test="${not empty form.page}">
	<jsp:include page="fragmentRegistrationFormHeader.jsp" />
</c:when><c:otherwise>
	<jsp:include page="../fragmentAjaxUpdateHeader.jsp" />
</c:otherwise></c:choose>

<c:choose><c:when test="${form.patient.gender eq 'F'}"><c:set var="partnerGender" value="Male" /></c:when><c:otherwise><c:set var="partnerGender" value="Female" /></c:otherwise></c:choose>
<script>
function selectExistingPartner() {
	var dlg = $j("#find${partnerGender}PatientDialog").dialog({width: 700,height:400,title:"Find Patient's ${partnerGender} Partner",modal:true});
	dlg.dialog('open');

	this.doSelectionHandler = function(index, data) {
		$j("span.newPartnerName").html(data.personName)
		$j("input[name=newPartnerId]").val(data.patientId)
		$j("input[name=newPartnerId]").attr('checked', 'checked')
		$j("tr.partnerDetails input, tr.partnerDetails select").attr('disabled', 'disabled')
		$j("tr.partnerDetails div.error").remove()

		dlg.dialog('close');
	}
	
	//searchHandler for the Search widget
	this.doPatientSearch = function(text, resultHandler, getMatchCount, opts) {
		DWR${partnerGender}PatientSearchService.findCountAnd${partnerGender}Patients(text, opts.start, opts.length, getMatchCount, resultHandler);
	}

	new OpenmrsSearch("find${partnerGender}Patients", false, this.doPatientSearch, this.doSelectionHandler, 
		[	{fieldName:"identifier", header:omsgs.identifier},
			{fieldName:"givenName", header:omsgs.givenName},
			{fieldName:"middleName", header:omsgs.middleName},
			{fieldName:"familyName", header:omsgs.familyName},
			{fieldName:"age", header:omsgs.age},
			{fieldName:"gender", header:omsgs.gender},
			{fieldName:"birthdateString", header:omsgs.birthdate}
		], {  searchLabel: 'Partner\'s Name: ', minLength: 2 });
	$j("#find${partnerGender}PatientDialog input[type=text]").val("${form.patient.personName.familyName}").focus().keyup();
}

function togglePartnerNotSpecified(cb) {
	if ($j(cb).is(":checked")) {
		$j("input[name=newPartnerId], tr.partnerDetails input, tr.partnerDetails select, table.partner tr.lookup").attr('disabled', 'disabled')
		$j("tr.partnerDetails div.error").remove()
	} else {
		$j("input[name=newPartnerId], tr.partnerDetails input, tr.partnerDetails select, table.partner tr.lookup").removeAttr('disabled', 'disabled')
	}
}

$j(document).ready(function() {
	$j("input[name=newPartnerId]").click(function() {
		if ($j(this).is(":checked")) {			
			$j(this).removeAttr('checked')
			selectExistingPartner();
		} else {
			$j("span.newPartnerName").html("")
			$j("input[name=newPartnerId]").val("");
			$j("tr.partnerDetails input, tr.partnerDetails select").removeAttr('disabled')
		}
	})
	
	// toggle the 'partner not specified' checkbox
	togglePartnerNotSpecified($j("#cbPartnerNotSpecified").get())

	<c:if test="${form.newPartnerId ne null}">
	$j("tr.partnerDetails input, tr.partnerDetails select").attr('disabled', 'disabled')
	</c:if>
})
</script>

<style>
#newPartnerId, .newPartnerName, #cbPartnerNotSpecified, .partnerNotSpecified { vertical-align: middle; }
</style>

<br/>
<form:form id="family-information-registration-form" modelAttribute="form" method="post" cssClass="main-form">

<fieldset><legend><span><c:choose
	><c:when test="${not empty form.page}">Page 1: FAMILY INFORMATION</c:when
	><c:otherwise>UPDATE FAMILY INFORMATION</c:otherwise></c:choose
></span></legend>
<form:hidden path="version" />
<form:hidden path="page" />
<table class="full-width borderless registration field">
	<tr>
		<td>
			<fieldset><legend>Patient Information</legend>
				<table><tr>
					<td>Patient's education</td>
					<td><chits_tag:springConcpetIdDropdown path="patient.attributeMap[${MiscAttributes.EDUCATION}].value" select="select education" answers="${chits:answers(EducationConcepts.EDUCATION)}" /></td>
				</tr><tr>
					<td>Patient's occupation</td>
					<td><chits_tag:springConcpetIdDropdown path="patient.attributeMap[${MiscAttributes.OCCUPATION}].value" select="select occupation" answers="${chits:answers(OccupationConcepts.OCCUPATION_MEMBERS)}" /></td>
				</tr></table>
			</fieldset>
		</td>
	</tr><tr>
		<td>
			<fieldset><legend>Partner's Information</legend>
				<table class="partner full-width">
				<tr>
					<td class="label">Partner is not known</td>
					<td valign="middle">
						<form:checkbox path="partnerNotSpecified" value="${true}" id="cbPartnerNotSpecified" onchange="togglePartnerNotSpecified(this)" />
						<label for="cbPartnerNotSpecified"><span class="partnerNotSpecified">Select if partner is not specified.</span></label>
					</td>
				</tr><tr class="lookup">
					<td class="label"><c:choose><c:when test="${form.partner.id gt 0}">Change Partner:</c:when><c:otherwise>Select Existing Partner Record:</c:otherwise></c:choose></td>
					<td valign="middle">
						<input type="checkbox" name="newPartnerId" value="${form.newPartnerId}" id="newPartnerId" <c:if test="${form.newPartnerId ne null}">checked="checked"</c:if> />
						<label for="newPartnerId"><span class="newPartnerName">${form.partner.personName}</span></label>
					</td>
				</tr><tr class="partnerDetails">
					<td class="label">Partner's First Name*</td>
					<td><chits_tag:springInput path="partner.personName.givenName" size="24" /></td>
				</tr><tr class="partnerDetails">
					<td class="label">Partner's Last Name*</td>
					<td><chits_tag:springInput path="partner.personName.familyName" size="24" /></td>
				</tr><tr class="partnerDetails">
					<td>Partner's education</td>
					<td><chits_tag:springConcpetIdDropdown path="partner.attributeMap[${MiscAttributes.EDUCATION}].value" select="select education" answers="${chits:answers(EducationConcepts.EDUCATION)}" /></td>
				</tr><tr class="partnerDetails">
					<td>Partner's occupation</td>
					<td><chits_tag:springConcpetIdDropdown path="partner.attributeMap[${MiscAttributes.OCCUPATION}].value" select="select occupation" answers="${chits:answers(OccupationConcepts.OCCUPATION_MEMBERS)}" /></td>
				</tr></table>
			</fieldset>
		</td>
	</tr><tr>
		<td>
			<br/><c:set var="now" value="<%= new Date() %>" />
			<table class="registration full-width">
			<tr>
				<td class="label" style="width: 1em;">Average monthly family income:</td>
				<td style="white-space: nowrap;">P&nbsp;<chits_tag:springInput path="fpProgramObs.familyInformation.familyFolder.averageFamilyIncome" /></td>
			</tr><tr>
				<td class="label">* Number of children (as of <fmt:formatDate pattern="MMM d, yyyy" value="${now}" />):</td>
				<td><chits_tag:springInput path="fpProgramObs.familyInformation.observationMap[${FPFamilyInformationConcepts.NUMBER_OF_CHILDREN.conceptId}].valueText" /></td>
			</tr><tr>
				<td class="label">* Number of additional children desired:</td>
				<td><chits_tag:springInput path="fpProgramObs.familyInformation.observationMap[${FPFamilyInformationConcepts.NMBR_OF_CHILDREN_DESIRED.conceptId}].valueText" /></td>
			</tr><tr>
				<td class="label">Planned interval of children (in years):</td>
				<td><chits_tag:springInput path="fpProgramObs.familyInformation.observationMap[${FPFamilyInformationConcepts.PLANNED_INTERVAL.conceptId}].valueText" /></td>
			</tr><tr>
				<td class="label" colspan="2">
					<fieldset><legend>Reason for Practicing Family Planning:</legend>
					<chits_tag:springTextArea path="fpProgramObs.familyInformation.observationMap[${FPFamilyInformationConcepts.REASON_FOR_PRACTICING.conceptId}].valueText" rows="${5}" />
					</fieldset>
				</td>
			</tr></table>
		</td>
	</tr>
</table>
</fieldset>

<c:if test="${not empty form.page}">
	<jsp:include page="fragmentRegistrationFormFooter.jsp" />
</c:if>

<br/>
<div class="full-width" style="text-align: right">
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
<c:choose><c:when test="${not empty form.page}">
<input type="submit" id="saveButton" value='Next Page' />
</c:when><c:otherwise>
<input type="submit" id="saveButton" value='Save' />
</c:otherwise></c:choose>
</div>
</form:form>