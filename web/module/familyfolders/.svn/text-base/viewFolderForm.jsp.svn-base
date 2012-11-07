<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Edit Patients" otherwise="/login.htm" redirect="/module/chits/familyfolders/foldersList.htm" />

<spring:message var="pageTitle" code="chits.FamilyFolder.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="../formStyle.jsp" %>
<%@ include file="../pleaseWait.jsp" %>

<h2><spring:message code="chits.FamilyFolder.view"/></h2>	

<br />
<form method="get" action="editFolder.form" id="editFolderForm" onsubmit="pleaseWaitDialog()">
	<table style="width: 700px" class="form">
		<tr>
			<th>Family ID</th>
			<th>${familyFolder.code}</th>
		</tr>
		<tr>
			<td>Family Name</td>
			<td>${familyFolder.name}</td>
		</tr>
		<spring:bind path="familyFolder.dateCreated">
		<tr>
			<td>Created By</td>
			<td>${familyFolder.creator.person.personName} on ${status.value}</td>
		</tr>
		</spring:bind>
		<spring:bind path="familyFolder.dateChanged">
		<tr>
			<td>Modified By</td>
			<td>
				<c:if test="${not empty familyFolder.dateChanged}">
					${familyFolder.changedBy.person.personName} on ${status.value}
				</c:if>
			</td>
		</tr>
		</spring:bind>
		<tr>
			<td>Address</td>
			<td>
				<c:choose><c:when test="${not empty familyFolder.address}">
					${familyFolder.address}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
			<td>City</td>
			<td>
				<c:choose><c:when test="${not empty familyFolder.cityCode}">
					${familyFolder.cityCode}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
				<span id="cityName"><c:if test="${not empty municipalities[familyFolder.cityCode].name}">(${municipalities[familyFolder.cityCode].name})</c:if></span>
			</td>
		</tr>
		<tr>
			<td>Barangay</td>
			<td>
				<c:choose><c:when test="${not empty familyFolder.barangayCode}">
					${familyFolder.barangayCode}
				</c:when><c:otherwise>
					<spring:message  code="chits.field.has.no.data"/>
				</c:otherwise></c:choose>
				<span id="barangayName"><c:if test="${not empty barangays[familyFolder.barangayCode].name}">(${barangays[familyFolder.barangayCode].name})</c:if></span>
			</td>
		</tr>
		<tr>
			<td>Notes</td>
			<td>${familyFolder.notes}</td>
		</tr>
		<tr>
			<td colspan="2">
				<%@ include file="fragmentHouseholdInformation.jsp" %>
			</td>
		</tr>
		<tr>
			<td colspan="2">Members:
			<c:choose><c:when test="${not empty familyFolder.patients}">
				<div style="font-size: 9px">&nbsp;&nbsp;HOTF = Head of the family</div>
				<table id="patient_members">
					<tr>
						<th title="Head of the family">HOTF</th>
						<th style="width:80%">Name</th>
						<th>Identifier</th>
					</tr><c:forEach var="patient" items="${familyFolder.patients}">
					<tr id="member_${patient.id}" style="vertical-align: middle;">
						<td style="text-align:center"><c:if test="${familyFolder.headOfTheFamily == patient}">&#x2713;</c:if></td>
						<td><a href="../patients/viewPatient.form?patientId=${patient.id}">(${patient.gender}, <chits:age birthdate="${patient.birthdate}" />) ${patient.personName}</a></td>
						<td><a href="../patients/viewPatient.form?patientId=${patient.id}">${patient.patientIdentifier}</a></td>
					</tr>
				</c:forEach></table>
			</c:when><c:otherwise>
				<spring:message code="chits.FamilyFolder.no.patient.members"/>
			</c:otherwise></c:choose>
			</td>
		</tr>
	</table>

	<input type="hidden" name="familyFolderId" value="${familyFolder.id}" />
	<input type="button" name="action" id="createButton" onclick="javascript:document.location.href='../patients/addPatient.form?familyFolderId=${familyFolder.id}'" value='<spring:message code="chits.Patient.create"/>' />
	<input type="submit" name="action" id="editButton" value='<spring:message code="chits.FamilyFolder.edit"/>' />
	<input type="button" name="action" id="editHouseholdInformationButton" onclick="javascript:document.location.href='updateHouseholdInformation.form?familyFolderId=${familyFolder.id}'" value='<spring:message code="chits.HouseholdInformation.edit"/>' />
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
