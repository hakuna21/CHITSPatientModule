<%@page import="org.openmrs.module.chits.PatientConsultForm"
%><%@page import="org.openmrs.module.chits.PatientQueue"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>
<span>
	<strong>${form.patient.personName}</strong>
</span><span>
	FAMILY NUMBER: <c:choose><c:when test="${empty familyFolders}">[Not a member of any family]</c:when><c:otherwise><a href="../familyfolders/viewFolder.form?familyFolderId=${familyFolders[0].familyFolderId}">${familyFolders[0].code}</a></c:otherwise></c:choose>
	&nbsp;&nbsp;&nbsp;AGE: <chits:age birthdate="${form.patient.birthdate}" />
	&nbsp;&nbsp;&nbsp;SEX: ${form.patient.gender}
	&nbsp;&nbsp;&nbsp;CIVIL STATUS: 
		<c:choose><c:when test="${not empty form.patient.attributeMap['Civil Status']}">
			${chits:conceptByIdOrName(form.patient.attributeMap['Civil Status'].value).name}
		</c:when><c:otherwise>
			<spring:message  code="chits.field.has.no.data"/>
		</c:otherwise></c:choose>
</span><span>
	PATIENT ID: <a href="../patients/viewPatient.form?patientId=${form.patient.patientId}">${form.patient.patientIdentifier}</a>
	&nbsp;&nbsp;&nbsp;ADDRESS: <chits_tag:familyAddress /> 
</span><span>
	BIRTHDATE: <spring:bind path="form.patient.birthdate">${status.value}</spring:bind>
	&nbsp;&nbsp;&nbsp;PHILHEALTH ID No.:
	<c:choose><c:when test="${not empty form.patient.attributeMap['CHITS_PHILHEALTH']}">
		<spring:bind path="form.patient.attributeMap['CHITS_PHILHEALTH']">${status.value}</spring:bind>
		<c:if test="${not empty form.patient.attributeMap['CHITS_PHILHEALTH_EXPIRATION']}">
		&nbsp;&nbsp;&nbsp;EXPIRATION DATE: ${form.patient.attributeMap['CHITS_PHILHEALTH_EXPIRATION']}
		</c:if>
	</c:when><c:otherwise>
		<em>[Not enrolled]</em>
	</c:otherwise></c:choose>
</span>

<c:choose><c:when test="${not empty form.patientQueue and form.patientQueue.enteredQueue ne null}">
<span>
	<c:choose><c:when test="${not empty form.encounters and fn:length(form.encounters) gt 1}">
		<%-- NOTE:
				- encounters are sorted by descending date/time;
				- encounters[0] is the current visit
				- encounters[1] is the last visit
		--%>
		Total Visits: ${fn:length(form.encounters) - 1}
		&nbsp;&nbsp;&nbsp;LAST VISIT: ${form.encounters[1].encounterDatetime}
	</c:when><c:otherwise>
		<span class="alert">This is the patient's first visit</span>
	</c:otherwise></c:choose>
</span><span>
		ARRIVED: <fmt:formatDate value="${form.patientQueue.enteredQueue}" pattern="dd MMM yyyy, hh:mm a" />
		&nbsp;&nbsp;&nbsp;<chits:elapsed since="${form.patientQueue.enteredQueue}" /> elapsed
</span>
</c:when><c:otherwise>
<span>
	<em>[Patient is not in queue]</em>
</span>
</c:otherwise></c:choose>
