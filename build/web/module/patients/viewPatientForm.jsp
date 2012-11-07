<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Edit Patients" otherwise="/login.htm" redirect="/module/chits/patients/foldersList.htm" />

<spring:message var="pageTitle" code="chits.Patient.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/dwr/interface/DWRFamilyFolderService.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />

<style>form { display: inline; }</style>

<script type="text/javascript">
function folderSearch() {
	var dlg = $j("#findFoldersDialog").dialog({width: 700,height:400,title:"Find Family Folder",modal:true});
	dlg.dialog('open');

	this.doSelectionHandler = function(index, data) {
		document.location = "../familyfolders/viewFolder.form?familyFolderId=" + data.id;
	}

	this.doFolderSearch = function(text, resultHandler, getMatchCount, opts) {
		DWRFamilyFolderService.findCountAndFamilyFolders('', text, opts.start, opts.length, getMatchCount, resultHandler);
	}

	new OpenmrsSearch("findFolders", false, doFolderSearch, doSelectionHandler, 
		[	{fieldName:"code", header:'Family ID'},
			{fieldName:"name", header:'Name'},
			{fieldName:"headOfTheFamily", header:'HOTF'},
			{fieldName:"address", header:'Address'},
			{fieldName:"barangayName", header:'Barangay'},
			{fieldName:"cityName", header:'City'},
			{fieldName:"notes", header:'Notes'}
		], { searchLabel: 'Folder ID or name: ', minLength: 2 });		
	<%-- TODO: The patient last name should be java script escaped! --%>
	$j("#findFoldersDialog input").val("<c:out value="${form.patient.personName.familyName}" />").focus().keyup();
}
</script>

<h2><spring:message code="chits.Patient.view"/></h2>	
<%@ include file="../formStyle.jsp" %>
<%@ include file="../pleaseWait.jsp" %>
<br />

<div style="display:none">
	<div id="findFoldersDialog">
		<div class="searchWidgetContainer" id="findFolders"></div>
		<b class="boxHeader">Find Family Folder</b><div class="box"><div class="searchWidgetContainer" id="findFolders"></div></div>
	</div>
</div>

<form:form modelAttribute="form" method="get" action="editPatient.form" onsubmit="pleaseWaitDialog()">
	<table style="width: 700px" class="form">
		<tr>
			<th>Patient ID</th>
			<th>${form.patient.patientIdentifier}</th>
		</tr>
		<tr>
			<td>Record Type</td>
			<td>
			<c:choose><c:when test="${Constants.FLAG_YES eq form.patient.attributeMap[MiscAttributes.NON_PATIENT]}">
			Non Patient
			</c:when><c:otherwise>
			Patient
			</c:otherwise></c:choose>
			</td>
		</tr>
		<spring:bind path="form.patient.dateCreated">
		<tr>
			<td>Created By</td>
			<td>
				${form.patient.creator.person.personName} on ${status.value}
				<c:if test="${not empty form.patient.attributeMap[MiscAttributes.CREATED_ON]}">(device: ${form.patient.attributeMap[MiscAttributes.CREATED_ON]})</c:if>
			</td>
		</tr>
		</spring:bind>
		<spring:bind path="form.patient.dateChanged">
		<tr>
			<td>Modified By</td>
			<td>
				<c:if test="${not empty form.patient.dateChanged}">
					${form.patient.changedBy.person.personName} on ${status.value}
					<c:if test="${not empty form.patient.attributeMap[MiscAttributes.LAST_MODIFIED_ON]}">(device: ${form.patient.attributeMap[MiscAttributes.LAST_MODIFIED_ON]})</c:if>
				</c:if>
			</td>
		</tr>
		</spring:bind>
		<tr>
			<td>Family Folder</td>
			<td><c:choose><c:when test="${not empty familyFolders}"><c:forEach var="familyFolder" items="${familyFolders}">
				<a href="../familyfolders/viewFolder.form?familyFolderId=${familyFolder.id}">${familyFolder}</a>
				<c:if test="${familyFolder.headOfTheFamily eq form.patient}"><span title="Head of the family">(HOTF)</span></c:if>
			</c:forEach></c:when><c:otherwise>
				<spring:message  code="chits.Patient.no.family.folder"/>
			</c:otherwise></c:choose></td>
		</tr>
		<tr>
			<td>First Name</td>
			<td>${form.patient.personName.givenName}</td>
		</tr>
		<tr>
			<td>Middle Name</td>
			<td>
				<c:choose><c:when test="${not empty form.patient.personName.middleName}">
					${form.patient.personName.middleName}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
			<td>Surname</td>
			<td>${form.patient.personName.familyName}</td>
		</tr>
		<tr>
			<td>Suffix</td>
			<td>
				<c:choose><c:when test="${not empty form.patient.personName.familyNameSuffix}">
					${form.patient.personName.familyNameSuffix}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
			<td>Sex</td>
			<td>${form.patient.gender}</td>
		</tr>
		<spring:bind path="patient.birthdate">
		<tr>
			<td>Birth Date</td>
			<td>${status.value} (Age: <chits:age birthdate="${form.patient.birthdate}" />)</td>
		</tr>
		</spring:bind>
		<tr>
			<td>Civil Status</td>
			<td>
				<c:choose><c:when test="${not empty form.patient.attributeMap['Civil Status']}">
					${chits:conceptByIdOrName(form.patient.attributeMap['Civil Status'].value).name}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
			<td>Philhealth Status</td>
			<td>
				${philhealthStatus}
			</td>
		</tr>
		<tr>
			<td>4Ps</td>
			<td>
			<c:choose><c:when test="${Constants.FLAG_YES eq form.patient.attributeMap[MiscAttributes.FOUR_PS]}">
			Yes
			</c:when><c:otherwise>
			No
			</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
			<td>Cellphone</td>
			<td>${form.patient.attributeMap[PhoneAttributes.MOBILE_NUMBER].value}</td>
		</tr>
		<%--
		<tr>
			<td>Telephone</td>
			<td>${form.patient.attributeMap[PhoneAttributes.LANDLINE_NUMBER].value}</td>
		</tr>
		--%>
		<tr>
			<td>Local ID</td>
			<td>${form.patient.attributeMap[IdAttributes.LOCAL_ID].value}</td>
		</tr>
		<%--
		<c:if test="${not empty form.patient.id}">
		<tr>
			<td title="Local Patient Identification Number">LPIN</td>
			<td>
				<fmt:formatNumber value="${form.patient.id}" pattern="000000" />
				<%--
				<c:choose><c:when test="${not empty form.patient.attributeMap['CHITS_LPIN']}">
					${form.patient.attributeMap['CHITS_LPIN']}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
				-- % >
			</td>
		</tr>
		</c:if>
		--%>
		<tr>
			<td>Mother's name</td>
			<td>
				<c:choose><c:when test="${not empty form.mother.id}">
					<a href="viewPatient.form?patientId=${form.mother.id}">${form.mother.personName} (Age: <chits:age birthdate="${form.mother.birthdate}" />)</a>
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
			<td>CRN</td>
			<td>
				<c:choose><c:when test="${not empty form.patient.attributeMap['CHITS_CRN']}">
					${form.patient.attributeMap['CHITS_CRN']}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
			<td>TIN</td>
			<td>
				<c:choose><c:when test="${not empty form.patient.attributeMap['CHITS_TIN']}">
					${form.patient.attributeMap['CHITS_TIN']}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
			<td>SSS</td>
			<td>
				<c:choose><c:when test="${not empty form.patient.attributeMap['CHITS_SSS']}">
					${form.patient.attributeMap['CHITS_SSS']}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
			<td>GSIS</td>
			<td>
				<c:choose><c:when test="${not empty form.patient.attributeMap['CHITS_GSIS']}">
					${form.patient.attributeMap['CHITS_GSIS']}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
			<td>Record Status</td>
			<td>
				<c:choose><c:when test="${form.patient.dead}">
				D
				</c:when><c:otherwise>
				Active
				</c:otherwise></c:choose>
			</td>
		</tr>
	</table>

	<input type="hidden" name="patientId" value="${form.patient.id}" />
	<c:if test="${not form.patient.dead}">
	<input type="submit" name="action" id="editButton" value='<spring:message code="chits.Patient.view.edit.button"/>' />
	</c:if>
</form:form>

<c:if test="${not form.patient.dead}">
<form action="${pageContext.request.contextPath}/module/chits/consults/addPatientToQueue.form" method="post" onsubmit="pleaseWaitDialog()">
	<c:choose><c:when test="${form.patientQueue ne null}">
		<input type="button" name="action" id="viewConsultsButton" value='<spring:message code="chits.Patient.view.consults.button"/>' onclick="document.location.href='../consults/viewPatient.form?patientId=${form.patient.id}'" />
	</c:when><c:otherwise>
		<input type="hidden" name="patientId" value="${form.patient.id}" />
		<input type="submit" name="action" id="addPatientToQueue" value='<spring:message code="chits.Patient.add.to.queue.button"/>' />
	</c:otherwise></c:choose>

	<%--
	<input type="button" onClick="folderSearch()" value='<spring:message code="chits.Patient.view.family.folder.button"/>' />
	<c:if test="${form.patient.age gt 17}">
	<input type="button" onClick="notImplemented()" value='<spring:message code="chits.Patient.view.philhealth.button"/>' />
	</c:if>
	--%>
</form>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp" %>
