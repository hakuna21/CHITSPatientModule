<openmrs:htmlInclude file="/dwr/interface/DWRFemalePatientSearchService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRMalePatientSearchService.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />

<script>
function selectExistingMother() {
	var dlg = $j("#findFemalePatientDialog").dialog({width: 700,height:400,title:"Find Patient's Mother",modal:true});
	dlg.dialog('open');

	this.doSelectionHandler = function(index, data) {
		$j("span.newMotherName").html(data.personName)
		$j("input[name=newMotherId]").val(data.patientId)
		$j("input[name=newMotherId]").attr('checked', 'checked')
		$j("tr.motherDetails input, tr.motherDetails select").attr('disabled', 'disabled')
		$j("tr.motherDetails div.error").remove()

		dlg.dialog('close');
	}
	
	//searchHandler for the Search widget
	this.doPatientSearch = function(text, resultHandler, getMatchCount, opts) {
		DWRFemalePatientSearchService.findCountAndFemalePatients(text, opts.start, opts.length, getMatchCount, resultHandler);
	}

	new OpenmrsSearch("findFemalePatients", false, this.doPatientSearch, this.doSelectionHandler, 
		[	{fieldName:"identifier", header:omsgs.identifier},
			{fieldName:"givenName", header:omsgs.givenName},
			{fieldName:"middleName", header:omsgs.middleName},
			{fieldName:"familyName", header:omsgs.familyName},
			{fieldName:"age", header:omsgs.age},
			{fieldName:"gender", header:omsgs.gender},
			{fieldName:"birthdateString", header:omsgs.birthdate}
		], {  searchLabel: 'Mother\'s Name: ', minLength: 2 });
	$j("#findFemalePatientDialog input[type=text]").val("${form.patient.personName.familyName}").focus().keyup();
}

$j(document).ready(function() {
	$j("input[name=newMotherId]").click(function() {
		if ($j(this).is(":checked")) {			
			$j(this).removeAttr('checked')
			selectExistingMother();
		} else {
			$j("span.newMotherName").html("")
			$j("input[name=newMotherId]").val("");
			$j("tr.motherDetails input, tr.motherDetails select").removeAttr('disabled')
		}
	})
	
	<c:if test="${form.newMotherId ne null}">
	$j("tr.motherDetails input, tr.motherDetails select").attr('disabled', 'disabled')
	</c:if>
})

function selectExistingFather() {
	var dlg = $j("#findMalePatientDialog").dialog({width: 700,height:400,title:"Find Patient's Father",modal:true});
	dlg.dialog('open');

	this.doSelectionHandler = function(index, data) {
		$j("span.newFatherName").html(data.personName)
		$j("input[name=newFatherId]").val(data.patientId)
		$j("input[name=newFatherId]").attr('checked', 'checked')
		$j("tr.fatherDetails input, tr.fatherDetails select").attr('disabled', 'disabled')
		$j("tr.fatherDetails div.error").remove()

		dlg.dialog('close');
	}
	
	//searchHandler for the Search widget
	this.doPatientSearch = function(text, resultHandler, getMatchCount, opts) {
		DWRMalePatientSearchService.findCountAndMalePatients(text, opts.start, opts.length, getMatchCount, resultHandler);
	}

	new OpenmrsSearch("findMalePatients", false, this.doPatientSearch, this.doSelectionHandler, 
		[	{fieldName:"identifier", header:omsgs.identifier},
			{fieldName:"givenName", header:omsgs.givenName},
			{fieldName:"middleName", header:omsgs.middleName},
			{fieldName:"familyName", header:omsgs.familyName},
			{fieldName:"age", header:omsgs.age},
			{fieldName:"gender", header:omsgs.gender},
			{fieldName:"birthdateString", header:omsgs.birthdate}
		], {  searchLabel: 'Father\'s Name: ', minLength: 2 });
	$j("#findMalePatientDialog input[type=text]").val("${form.patient.personName.familyName}").focus().keyup();
}

function toggleFatherUnknown(cb) {
	if ($j(cb).is(":checked")) {
		$j("input[name=newFatherId], tr.fatherDetails input, tr.fatherDetails select, table.male tr.lookup").attr('disabled', 'disabled')
	} else {
		$j("input[name=newFatherId], tr.fatherDetails input, tr.fatherDetails select, table.male tr.lookup").removeAttr('disabled', 'disabled')
	}
}

$j(document).ready(function() {
	$j("input[name=newFatherId]").click(function() {
		if ($j(this).is(":checked")) {			
			$j(this).removeAttr('checked')
			selectExistingFather();
		} else {
			$j("span.newFatherName").html("")
			$j("input[name=newFatherId]").val("");
			$j("tr.fatherDetails input, tr.fatherDetails select").removeAttr('disabled')
		}
	})
	
	// toggle the 'father unknown' checkbox
	toggleFatherUnknown($j("#cbFatherUnknown").get())

	<c:if test="${form.newFatherId ne null}">
	$j("tr.fatherDetails input, tr.fatherDetails select").attr('disabled', 'disabled')
	</c:if>
})
</script>

<style>
#newMotherId, .newMotherName, #newFatherId, .newFatherName, #cbFatherUnknown, .fatherUnknown { vertical-align: middle; }
</style>

<table class="female full-width">
	<tr><th colspan="2" class="label">Mother's Information</th></tr>
	<tr class="lookup">
		<td class="label">
			<c:choose><c:when test="${form.mother.id gt 0}">
				Change Mother:
			</c:when><c:otherwise>
				Select Existing Mother Record:
			</c:otherwise></c:choose>
		</td>
		<td valign="middle">
			<input type="checkbox" name="newMotherId" value="${form.newMotherId}" id="newMotherId" <c:if test="${form.newMotherId ne null}">checked="checked"</c:if>/>
			<label for="newMotherId"><span class="newMotherName">${form.mother.personName}</span></label>
		</td>
	</tr>
	<tr class="motherDetails">
		<td class="label">Mother's First Name*</td>
		<td>
			<spring:bind path="mother.personName.givenName">
			<input name="${status.expression}" maxlength="64" size="24" value="${status.value}"/>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>	
	<tr class="motherDetails">
		<td class="label">Mother's Last Name*</td>
		<td>
			<spring:bind path="mother.personName.familyName">
			<input name="${status.expression}" maxlength="64" size="24" value="${status.value}"/>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>	
	<tr class="motherDetails">
		<td class="label">Number of Pregnancies (G)</td>
		<td>
			<spring:bind path="mother.attributeMap[${MiscAttributes.NUMBER_OF_PREGNANCIES}].value">
			<input name="${status.expression}" maxlength="4" size="6" value="${status.value}"/>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr class="motherDetails">
		<td class="label">Mother's occupation</td>
		<td>
			<spring:bind path="mother.attributeMap[${MiscAttributes.OCCUPATION}].value">
				<form:select path="${status.expression}">
					<option value="">Select Occupation</option>
					<c:forEach var="occupation" items="${chits:answers(OccupationConcepts.MOTHERS_OCCUPATION)}">
					<form:option value="${occupation.conceptId}" label="${occupation.name.name}" />
					</c:forEach>
				</form:select>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr class="motherDetails">
		<td class="label">Mother's education</td>
		<td>
			<spring:bind path="mother.attributeMap[${MiscAttributes.EDUCATION}].value">
				<form:select path="${status.expression}">
					<option value="">Select Education</option>
					<c:forEach var="education" items="${chits:answers(EducationConcepts.MOTHERS_EDUCATION)}">
					<form:option value="${education.conceptId}" label="${education.name.name}" />
					</c:forEach>
				</form:select>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
</table>
<br/>
<table class="male full-width">
	<tr><th colspan="2" class="label">Father's Information</th></tr>
	<c:if test="${not (form.father.id gt 0)}">
	<tr>
		<td class="label">Father is not known</td>
		<td valign="middle">
			<form:checkbox path="fatherUnknown" value="${true}" id="cbFatherUnknown" onchange="toggleFatherUnknown(this)" />
			<label for="cbFatherUnknown"><span class="fatherUnknown">Select if father is not known.</span></label>
		</td>
	</tr>
	</c:if>
	<tr class="lookup">
		<td class="label">
			<c:choose><c:when test="${form.father.id gt 0}">
				Change Father:
			</c:when><c:otherwise>
				Select Existing Father Record:
			</c:otherwise></c:choose>
		</td>
		<td valign="middle">
			<input type="checkbox" name="newFatherId" value="${form.newFatherId}" id="newFatherId" <c:if test="${form.newFatherId ne null}">checked="checked"</c:if>/>
			<label for="newFatherId"><span class="newFatherName">${form.father.personName}</span></label>
		</td>
	</tr>
	<tr class="fatherDetails">
		<td class="label">Father's First Name*</td>
		<td>
			<spring:bind path="father.personName.givenName">
			<input name="${status.expression}" maxlength="64" size="24" value="${status.value}"/>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>	
	<tr class="fatherDetails">
		<td class="label">Father's Last Name*</td>
		<td>
			<spring:bind path="father.personName.familyName">
			<input name="${status.expression}" maxlength="64" size="24" value="${status.value}"/>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>	
	<tr class="fatherDetails">
		<td class="label">Father's occupation</td>
		<td>
			<spring:bind path="father.attributeMap[${MiscAttributes.OCCUPATION}].value">
				<form:select path="${status.expression}">
					<option value="">Select Occupation</option>
					<c:forEach var="occupation" items="${chits:answers(OccupationConcepts.FATHERS_OCCUPATION)}">
					<form:option value="${occupation.conceptId}" label="${occupation.name.name}" />
					</c:forEach>
				</form:select>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr class="fatherDetails">
		<td class="label">Father's education</td>
		<td>
			<spring:bind path="father.attributeMap[${MiscAttributes.EDUCATION}].value">
				<form:select path="${status.expression}">
					<option value="">Select Education</option>
					<c:forEach var="education" items="${chits:answers(EducationConcepts.FATHERS_EDUCATION)}">
					<form:option value="${education.conceptId}" label="${education.name.name}" />
					</c:forEach>
				</form:select>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
</table>