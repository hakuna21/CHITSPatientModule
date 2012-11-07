<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Edit Patients" otherwise="/login.htm" redirect="/module/chits/patients/foldersList.htm" />

<spring:message var="pageTitle" code="chits.Patient.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery-imask-min.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRFamilyFolderService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRFemalePatientSearchService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRBarangayService.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/LocationFinder.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />
<%@ include file="../formStyle.jsp" %>
<%@ include file="../pleaseWait.jsp" %>

<script>
$j(document).ready(function() {
	$j("#CRN").iMask({type : 'fixed',mask : '<openmrs:globalProperty key="crn.mask" />',stripMask : false})
	$j("#TIN").iMask({type : 'fixed',mask : '<openmrs:globalProperty key="tin.mask" />',stripMask : false})
	$j("#SSS").iMask({type : 'fixed',mask : '<openmrs:globalProperty key="sss.mask" />',stripMask : false})
	$j("#GSIS").iMask({type : 'fixed',mask : '<openmrs:globalProperty key="gsis.mask" />',stripMask : false})
	$j("#PHILHEALTH").iMask({type : 'fixed',mask : '<openmrs:globalProperty key="philhealth.mask" />',stripMask : false})

	$j("input[name='existingMother']").change(function(){
		if ($j("input[name='existingMother']:checked").val() == "true") {
			$j('#createNewMother').hide()
		} else {
			$j('#createNewMother').show()
		}
	});
	
	$j("input[name='familyFolder.name']").change(function() {
		if ($j("input[name='patient.personName.familyName']").val() == "") {
			$j("input[name='patient.personName.familyName']").val($j(this).val())
		}
	})

	$j("input[name='patient.personName.familyName']").change(function() {
		if ($j("input[name='familyFolder.name']").val() == "") {
			$j("input[name='familyFolder.name']").val($j(this).val())
		}
	})

	$j("input[name='existingFolder']").change(function(){
		if ($j("input[name='existingFolder']:checked").val() == "true") {
			$j('#createNewFolder').hide()
		} else {
			$j('#createNewFolder').show()
		}
	});

	$j("input[name='hasPhilhealth']").change(function(){
		if ($j("input[name='hasPhilhealth']:checked").val() == "false") {
			$j('#philhealthInfo').hide()
		} else {
			$j('#philhealthInfo').show()
		}
	});

	<c:choose><c:when test="${not empty municipalities[form.familyFolder.cityCode].name}">
	var city = {
		name: "${municipalities[form.familyFolder.cityCode].name}",
		municipalityCode: "${form.familyFolder.cityCode}"
	};
	</c:when><c:otherwise>
	var city = undefined
	</c:otherwise></c:choose>
	var locationFinder = new LocationFinder();
	$j("input[name='familyFolder.barangayCode']").focus(function() {
		locationFinder.findBarangay(function(barangay) {
			$j("input[name='familyFolder.barangayCode']").val(barangay.barangayCode);
			$j("span#barangayName").html("(" + barangay.name + ")");
			$j("input[name='familyFolder.cityCode']").val(barangay.municipality.municipalityCode);
			$j("span#cityName").html("(" + barangay.municipality.name + ")");
			setTimeout( function() { $j("textarea[name='notes']").focus(); }, 0 );
		}, city)
	})

	$j("input[name='familyFolder.cityCode']").focus(function() {
		locationFinder.findMunicipality(function(municipality) {
			city = municipality
			$j("input[name='familyFolder.cityCode']").val(municipality.municipalityCode);
			$j("span#cityName").html("(" + municipality.name + ")");
			
			if (municipality.municipalityCode / 1000 != Math.floor($j("input[name='familyFolder.barangayCode']").val() / 1000)) {
				// barangay code doesn't match selected city
				$j("input[name='familyFolder.barangayCode']").val("");
				$j("span#barangayName").html("");
			}
			
			// setTimeout( function() { $j("input[name='familyFolder.barangayCode']").focus(); }, 0 );
		})
	})
})

function patientMotherSearch() {
	var dlg = $j("#findPatientDialog").dialog({width: 700,height:400,title:"Find Patient's Mother",modal:true});
	dlg.dialog('open');

	this.doSelectionHandler = function(index, data) {
		$j("input#motherId").val(data.patientId);
		$j("input#motherName").val(data.personName);
		$j("#existingMotherOption").attr("checked", "checked");
		$j("#newMotherOption").removeAttr("checked");
		$j("#existingMotherOption").trigger("change");

		dlg.dialog('close');
	}
	
	//searchHandler for the Search widget
	this.doPatientSearch = function(text, resultHandler, getMatchCount, opts) {
		DWRFemalePatientSearchService.findCountAndFemalePatients(text, opts.start, opts.length, getMatchCount, resultHandler);
	}

	new OpenmrsSearch("findPatients", false, this.doPatientSearch, this.doSelectionHandler, 
		[	{fieldName:"identifier", header:omsgs.identifier},
			{fieldName:"givenName", header:omsgs.givenName},
			{fieldName:"middleName", header:omsgs.middleName},
			{fieldName:"familyName", header:omsgs.familyName},
			{fieldName:"age", header:omsgs.age},
			{fieldName:"gender", header:omsgs.gender},
			{fieldName:"birthdateString", header:omsgs.birthdate}
		], { searchLabel: 'Mother\'s Name: ', minLength: 2 });
	$j("#findPatientDialog input").val($j("input[name='patient.personName.familyName']").val()).focus().keyup();
}

function familyFolderSearch() {
	var dlg = $j("#findFolderDialog").dialog({width: 700,height:400,title:"Find Folder",modal:true});
	dlg.dialog('open');

	this.doSelectionHandler = function(index, data) {
		$j("input[name='familyFolderId']").val(data.id)
		$j("input#familyFolderName").val(data.code)
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
<h2><c:choose><c:when test="${empty form.patient.id}"><spring:message code="chits.Patient.add"/></c:when><c:otherwise><spring:message code="chits.Patient.update"/></c:otherwise></c:choose></h2>	

<spring:hasBindErrors name="form">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<br/>

<div style="display:none">
	<div id="findPatientDialog">
		<b class="boxHeader">Search female Patients</b><div class="box"><div class="searchWidgetContainer" id="findPatients"></div></div>
	</div>
</div>

<div style="display:none">
	<div id="findFolderDialog">
		<b class="boxHeader">Search folder</b><div class="box"><div class="searchWidgetContainer" id="findFolders"></div></div>
	</div>
</div>

<c:choose><c:when test="${empty form.patient.id}"><c:set var="action" value="addPatient.form" /></c:when><c:otherwise><c:set var="action" value="editPatient.form" /></c:otherwise></c:choose>
<form:form modelAttribute="form" method="post" id="patientForm" action="${action}" onsubmit="pleaseWaitDialog()">
	<form:hidden path="version" />
	<div style="font-size: 9px">
		<table class="borderless"><tr>
			<td>*</td><td>Required Field</td></tr>
			<td>**</td><td>Required only for 'Patient' record types</td></tr>
		</table>
	</div>
	<table style="width: 700px" class="form">
		<tr>
			<th style="width: 170px;">
				Patient ID
			</th>
			<th style="width: 580px;">
				${form.patient.patientIdentifier}
				<input type="hidden" name="patientId" value="${param.patientId}" />
			</th>
		</tr>
		<tr>
			<td>Record Type</td>
			<td>
			<spring:bind path="nonPatient">
				<form:select path="${status.expression}">
					<form:option value="true">Non Patient</form:option>
					<form:option value="false">Patient</form:option>
				</form:select>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
			</td>
		</tr>
		<tr>
			<td>Family Folder*</td>
			<td>
				<span>
					<spring:bind path="headOftheFamily">
						<input type="checkbox" id="headOfTheFamilyOption" <c:if test="${status.value}">checked="checked"</c:if> value="true" name="${status.expression}" /><label for="headOfTheFamilyOption">Head of the family</label>
					</spring:bind>
					<br/>
					<spring:bind path="existingFolder">
					<input type="hidden" name="familyFolderId" value="${familyFolders[0].id}" />
					<input type="radio" id="existingFolderOption" name="existingFolder" value="true" <c:if test="${form.existingFolder}"> checked="checked"</c:if>> <label for="existingFolderOption">Find existing record:</label>
					<input type="text" id="familyFolderName" readonly="readonly" value="${familyFolders[0]}" onFocus="familyFolderSearch()" />
					<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
					<br/>
					or 
					<br/>
					<input type="radio" id="newFolderOption" name="existingFolder" value="false" <c:if test="${!form.existingFolder}"> checked="checked"</c:if>> <label for="newFolderOption">Create a new record for the patient's folder</label>
					<div id="createNewFolder" <c:if test="${form.existingFolder}">style="display:none"</c:if>>
					<table>
						<spring:bind path="familyFolder.name">
						<tr>
							<td>Family Name*</td>
							<td><input name="${status.expression}" maxlength="64" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
						</tr>
						</spring:bind>
						<spring:bind path="familyFolder.address">
						<tr>
							<td>Address</td>
							<td><input name="${status.expression}" maxlength="64" size="64" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
						</tr>
						</spring:bind>
						<spring:bind path="familyFolder.cityCode">
						<tr>
							<td>City*</td>
							<td>
								<input name="${status.expression}" maxlength="9" size="12" value="${status.value}"/>
								<span id="cityName"><c:if test="${not empty municipalities[status.value].name}">(${municipalities[status.value].name})</c:if></span>
								<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
							</td>
						</tr>
						</spring:bind>
						<spring:bind path="familyFolder.barangayCode">
						<tr>
							<td>Barangay*</td>
							<td>
								<input name="${status.expression}" maxlength="9" size="12" value="${status.value}"/>
								<span id="barangayName"><c:if test="${not empty barangays[status.value].name}">(${barangays[status.value].name})</c:if></span>
								<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
							</td>
						</tr>
						</spring:bind>
						<spring:bind path="familyFolder.notes">
						<tr>
							<td>Notes</td>
							<td><textarea name="${status.expression}" rows="5" cols="30">${status.value}</textarea><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
						</tr>
						</spring:bind>
					</table></div>
				</span>
			</td>
		</tr>
		<spring:bind path="patient.personName.givenName">
		<tr>
			<td>First Name*</td>
			<td><input name="${status.expression}" maxlength="64" size="24" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<spring:bind path="patient.personName.middleName">
		<tr>
			<td>Middle Name</td>
			<td><input name="${status.expression}" maxlength="64" size="24" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<spring:bind path="patient.personName.familyName">
		<tr>
			<td>Surname*</td>
			<td><input name="${status.expression}" maxlength="64" size="24" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<spring:bind path="patient.personName.familyNameSuffix">
		<tr>
			<td>Suffix</td>
			<td><input name="${status.expression}" maxlength="64" size="24" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<spring:bind path="patient.gender">
		<tr>
			<td>Sex*</td>
			<td>
				<select name="${status.expression}">
					<option value="">--Please Choose--</option>
					<option value="M"<c:if test="${status.value == 'M'}"> selected</c:if>>Male</option>
					<option value="F"<c:if test="${status.value == 'F'}"> selected</c:if>>Female</option>
				</select>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</td>
		</tr>
		</spring:bind>
		<spring:bind path="patient.birthdate">
		<tr>
			<td>Birth Date**<br/><i style="font-weight: normal; font-size: .8em;">(<spring:message code="general.format"/>: <openmrs:datePattern />)</i></td>
			<td><input name="${status.expression}" onFocus="showCalendar(this,125)" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<spring:bind path="patient.attributeMap[Civil Status]">
		<tr>
			<td>Civil Status**</td>
			<td>
				<select name="Civil Status">
					<option value="">--Please Choose--</option>
					<c:forEach var="civilStatusAnswerConcept" items="${chits:answers(CivilStatusConcepts.CIVIL_STATUS)}">
					<option value="${civilStatusAnswerConcept.id}"<c:if test="${status.value.value eq civilStatusAnswerConcept.id}"> selected</c:if>>${civilStatusAnswerConcept.name}</option>
					</c:forEach>
				</select>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</td>
		</tr>
		</spring:bind>
		<tr>
			<td>Philhealth</td>
			<td>
				<input type="radio" id="noPhilhealthOption" name="hasPhilhealth" value="false" <c:if test="${!form.hasPhilhealth}"> checked="checked"</c:if>> <label for="noPhilhealthOption">Not enrolled</label><br/>
				or<br/>
				<input type="radio" id="withPhilhealthOption" name="hasPhilhealth" value="true" <c:if test="${form.hasPhilhealth}"> checked="checked"</c:if>> <label for="withPhilhealthOption">Enrolled</label><br/>
				<table id="philhealthInfo" <c:if test="${!form.hasPhilhealth}">style="display:none"</c:if>>
					<spring:bind path="patient.attributeMap[CHITS_PHILHEALTH]">
					<tr>
						<td>ID*</td>
						<td><input id="PHILHEALTH" name="CHITS_PHILHEALTH" maxlength="16" size="16" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
					</tr>					
					</spring:bind>
					<spring:bind path="patient.attributeMap[CHITS_PHILHEALTH_EXPIRATION]">
					<tr>
						<td>Expiration*</td>
						<td><input name="CHITS_PHILHEALTH_EXPIRATION" onFocus="showCalendar(this)" maxlength="16" size="16" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
					</tr>					
					</spring:bind>
					<spring:bind path="patient.attributeMap[CHITS_PHILHEALTH_SPONSOR]">
					<tr>
						<td>Sponsor*</td>
						<td>
							<select name="CHITS_PHILHEALTH_SPONSOR">
								<option value="">--Please Choose--</option>
								<c:forEach var="philhealthSponsorConcept" items="${chits:answers(PhilhealthSponsorConcepts.CHITS_PHILHEALTH_SPONSOR)}">
								<option value="${philhealthSponsorConcept.id}"<c:if test="${status.value.value eq philhealthSponsorConcept.id}"> selected</c:if>>${philhealthSponsorConcept.name}</option>
								</c:forEach>
							</select>
							<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
						</td>
					</tr>					
					</spring:bind>
				</table>				
			</td>
		</tr>
		<tr>
			<td>4Ps</td>
			<td>
			<spring:bind path="fourPs">
				<form:checkbox path="${status.expression}" value="${true}" id="cb4Ps" /><label for="cb4Ps">4Ps</label>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
			</td>
		</tr>
		<spring:bind path="patient.attributeMap[${PhoneAttributes.MOBILE_NUMBER}].value">
		<tr>
			<td>Cellphone</td>
			<td><input name="${status.expression}" maxlength="16" size="16" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<%--
		<spring:bind path="patient.attributeMap[${PhoneAttributes.LANDLINE_NUMBER}].value">
		<tr>
			<td>Telephone</td>
			<td><input name="${status.expression}" maxlength="16" size="16" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		--%>
		<spring:bind path="patient.attributeMap[${IdAttributes.LOCAL_ID}].value">
		<tr>
			<td>Local ID</td>
			<td><input name="${status.expression}" maxlength="16" size="16" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<%--
		<c:if test="${not empty form.patient.id}">
		<tr>
			<td title="Local Patient Identification Number">LPIN</td>
			<td>
				<fmt:formatNumber value="${form.patient.id}" pattern="000000" />
				<%--
				<c:choose><c:when test="${not empty form.patient.attributeMap[CHITS_LPIN]}">
					${form.patient.attributeMap[CHITS_LPIN]}
				</c:when><c:otherwise>
					<spring:message code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
				-- % >
			</td>
		</tr>
		</c:if>
		--%>
		<tr> 
			<td>Mother's name</td>
			<td>
				<span>
					<spring:bind path="mother.id">
						<input id="motherId" type="hidden" name="${status.expression}" value="${status.value}" />
						<input type="radio" id="existingMotherOption" name="existingMother" value="true" <c:if test="${form.existingMother}"> checked="checked"</c:if>> <label for="existingMotherOption">Find existing record:</label>
						<c:choose><c:when test="${not empty status.value}">
							<input id="motherName" value="${form.mother.personName}" readonly="readonly" onFocus="javascript:patientMotherSearch()" />
						</c:when><c:otherwise>
							<input id="motherName" value="" readonly="readonly" onFocus="javascript:patientMotherSearch()" />
						</c:otherwise></c:choose>
						<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
					</spring:bind>
					<br/>
					or 
					<br/>
					<input type="radio" id="newMotherOption" name="existingMother" value="false" <c:if test="${!form.existingMother}"> checked="checked"</c:if>> <label for="newMotherOption">Create a new record for the patient's mother</label>
					<div id="createNewMother" <c:if test="${form.existingMother}">style="display:none"</c:if>><table>
						<spring:bind path="mother.personName.givenName">
						<tr>
							<td>First Name*</td>
							<td><input name="${status.expression}" value="${status.value}" maxlength="64" size="24" /><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
						</spring:bind>
						<spring:bind path="mother.personName.familyName">
						<tr>
							<td>Surname*</td> 
							<td><input name="${status.expression}" value="${status.value}" maxlength="64" size="24" /><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
						</tr>
						</spring:bind>
					</table></div>
				</span>
			</td>
		</tr>
		<spring:bind path="patient.attributeMap[CHITS_CRN]">
		<tr>
			<td>CRN</td>
			<td><input id="CRN" name="CHITS_CRN" maxlength="15" size="20" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<spring:bind path="patient.attributeMap[CHITS_TIN]">
		<tr>
			<td>TIN</td>
			<td><input id="TIN" name="CHITS_TIN" maxlength="16" size="16" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<spring:bind path="patient.attributeMap[CHITS_SSS]">
		<tr>
			<td>SSS</td>
			<td><input id="SSS" name="CHITS_SSS" maxlength="16" size="16" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
		<spring:bind path="patient.attributeMap[CHITS_GSIS]">
		<tr>
			<td>GSIS</td>
			<td><input id="GSIS" name="CHITS_GSIS" maxlength="16" size="16" value="${status.value}"/><c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if></td>
		</tr>
		</spring:bind>
	</table>

	<c:choose><c:when test="${empty form.patient.id}">
		<input type="submit" name="action" id="saveButton" value='<spring:message code="chits.Patient.create"/>' />
		<c:choose><c:when test="${not empty param.familyFolderId}"><%-- Send back to family folder --%>
		<input type="button" name="action" id="cancelButton" onClick="document.location.href='../familyfolders/viewFolder.form?familyFolderId=${param.familyFolderId}'" value='<spring:message code="chits.Patient.cancel"/>' />
		</c:when><c:otherwise><%-- Send back to find patients page --%>
		<input type="button" name="action" id="cancelButton" onClick="document.location.href='findPatient.htm'" value='<spring:message code="chits.Patient.cancel"/>' />
		</c:otherwise></c:choose>
	</c:when><c:otherwise>
		<input type="submit" name="action" id="editButton" value='<spring:message code="chits.Patient.update"/>' />
		<input type="button" name="action" id="cancelButton" onClick="document.location.href='viewPatient.form?patientId=${form.patient.id}'" value='<spring:message code="chits.Patient.cancel"/>' />
	</c:otherwise></c:choose>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
