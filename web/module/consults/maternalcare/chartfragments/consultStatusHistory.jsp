<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%>

<fieldset><legend>Consult Status History</legend>
<table id="status-history" class="form full-width borderless registration">
<thead><tr><th>DATE OF<br/>ACTIVATION</th><th>CONSULT<br/>STATUS</th><th>REMARKS</th><th>CHANGED BY</th></tr></thead>
<tbody><c:forEach var="status" items="${form.mcProgramObs.allPatientConsultStatus}">
<tr>
	<td><fmt:formatDate pattern="MMM d, yyyy" value="${chits:observation(status.obs, MCPatientConsultStatus.DATE_OF_CHANGE).valueDatetime}" /></td>
	<td><c:set var="codedStatus" value="${chits:observation(status.obs, MCPatientConsultStatus.STATUS).valueCoded}"
		/><c:choose
			><c:when test="${codedStatus eq chits:concept(MaternalCareProgramStates.ACTIVE)}">ACTIVE</c:when
			><c:when test="${codedStatus eq chits:concept(MaternalCareProgramStates.REFERRED)}">REFERRED</c:when
			><c:when test="${codedStatus eq chits:concept(MaternalCareProgramStates.ADMITTED)}">ADMITTED</c:when
			><c:when test="${codedStatus eq chits:concept(MaternalCareProgramStates.ENDED)}">ENDED</c:when
			><c:otherwise><span class="alert">UNKNOWN</span></c:otherwise
		></c:choose
	></td>
	<td><chits_tag:obsValue obs="${chits:observation(status.obs, MCPatientConsultStatus.REMARKS)}" /></td>
	<td>${status.obs.creator.personName}</td>
</tr>
</c:forEach></tbody>
</table>
</fieldset>