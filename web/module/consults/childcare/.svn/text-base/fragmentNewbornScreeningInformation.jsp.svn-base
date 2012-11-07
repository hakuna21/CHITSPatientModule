<c:choose><c:when test="${chits:observation(chits:observation(form.patient, NewbornScreeningConcepts.SCREENING_INFORMATION), NewbornScreeningInformation.ACTION).valueCoded.conceptId ne StatusConcepts.CLOSED.conceptId}">
<a href="#" style="color: #44c;" onclick='javascript: loadNewbornScreeningInfoForm(${form.patient.patientId}); return false;'>Newborn Screening Information</a>
</c:when><c:otherwise>
<u>Newborn Screening Information</u>
</c:otherwise></c:choose>

<div class="indent">
<c:set var="screeningInfo" value="${chits:observation(form.patient, NewbornScreeningConcepts.SCREENING_INFORMATION)}" />
<c:choose><c:when test="${not empty screeningInfo}">
<table class="borderless">
	<tr>
		<td>Report Date:</td>
		<td>${chits:observation(screeningInfo, NewbornScreeningInformation.REPORT_DATE).valueText}</td>
	</tr>
	<tr>
		<td>Newborn Screening Date:</td>
		<td>${chits:observation(screeningInfo, NewbornScreeningInformation.SCREENING_DATE).valueText}</td>
	</tr>
	<tr>
		<td>Action:</td>
		<td>${chits:observation(screeningInfo, NewbornScreeningInformation.ACTION).valueCoded.name}</td>
	</tr>
</table>
</c:when><c:otherwise>
No newborn screening information
</c:otherwise></c:choose>
</div>

<%-- Display historical info --%>
<c:set var="history" value="${chits:observations(form.patient, NewbornScreeningConcepts.SCREENING_INFORMATION)}" />
<c:forEach var="i" begin="${2}" end="${fn:length(history)}">
<c:set var="screeningInfo" value="${history[fn:length(history) - i]}" />
${chits:observation(screeningInfo, NewbornScreeningInformation.SCREENING_DATE).valueText} - <chits:codedAnswer obs="${chits:observation(screeningInfo, NewbornScreeningInformation.ACTION)}" /><br/>
</c:forEach>
