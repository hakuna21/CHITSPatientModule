<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"%>

<h4>ANTHROPOMETRIC HISTORY</h4>

<h5><c:choose><c:when test="${patient.age lt 2}">Length</c:when><c:otherwise>Height</c:otherwise></c:choose> and Weight Measurements</h5>
<table id="height-and-weight-measurements" class="form">
	<thead>
		<tr>
			<th>DATE ENTERED</th>
			<th><c:choose><c:when test="${patient.age lt 2}">LENGTH</c:when><c:otherwise>HEIGHT</c:otherwise></c:choose></th>
			<th>WEIGHT</th>
			<th>BMI</th>
			<th>CLASS</th>
			<th>AGE TAKEN</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${encountersByDate}" var="entry">
		<c:if test="${not empty entry.value[VisitConcepts.HEIGHT_CM.conceptId] or not empty entry.value[VisitConcepts.WEIGHT_KG.conceptId]}">
			<tr>
				<td><fmt:formatDate value="${entry.key}" pattern="MM/dd/yyyy" /></td>
				<td><chits:height obs="${entry.value[VisitConcepts.HEIGHT_CM.conceptId]}" showDateTaken="${false}" showElapsedSinceTaken="${false}" /></td>
				<td><chits:weight obs="${entry.value[VisitConcepts.WEIGHT_KG.conceptId]}" showDateTaken="${false}" showElapsedSinceTaken="${false}" /></td>
				<td><chits:bmi weight="${entry.value[VisitConcepts.WEIGHT_KG.conceptId]}" height="${entry.value[VisitConcepts.HEIGHT_CM.conceptId]}" birthdate="${patient.birthdate}" showValue="${true}" showClassification="${false}"/></td>
				<td><chits:bmi weight="${entry.value[VisitConcepts.WEIGHT_KG.conceptId]}" height="${entry.value[VisitConcepts.HEIGHT_CM.conceptId]}" birthdate="${patient.birthdate}" showValue="${false}" showClassification="${true}"/></td>
				<td><chits:age birthdate="${patient.birthdate}" on="${entry.value[VisitConcepts.HEIGHT_CM.conceptId].obsDatetime}" showClassification="${false}" /></td>
			</tr>
		</c:if>
		</c:forEach>
	</tbody>
</table>
<div class="buttons"><input type="button" onclick="javascript: notImplemented()" value="View Graph" /></div>

<h5>Circumference of Waist and Hip</h5>
<table id="circumference-of-waist-and-hip" class="form">
	<thead>
		<tr>
			<th>DATE TAKEN</th>
			<th>WAIST</th>
			<th>HIP</th>
			<th>W-H</th>
			<th>RISK</th>
			<th>AGE TAKEN</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${encountersByDate}" var="entry">
		<c:if test="${not empty entry.value[VisitConcepts.WAIST_CIRC_CM.conceptId] or not empty entry.value[VisitConcepts.HIP_CIRC_CM.conceptId]}">
			<tr>
				<td><fmt:formatDate value="${entry.key}" pattern="MM/dd/yyyy" /></td>
				<td><chits:circumference obs="${entry.value[VisitConcepts.WAIST_CIRC_CM.conceptId]}" showDateTaken="${false}" showElapsedSinceTaken="${false}" /></td>
				<td><chits:circumference obs="${entry.value[VisitConcepts.HIP_CIRC_CM.conceptId]}" showDateTaken="${false}" showElapsedSinceTaken="${false}" /></td>
				<td><chits:waistToHipRatio waistCircumference="${entry.value[VisitConcepts.WAIST_CIRC_CM.conceptId]}" hipCircumference="${entry.value[VisitConcepts.HIP_CIRC_CM.conceptId]}" birthdate="${patient.birthdate}" showValue="${true}" showClassification="${false}"/></td>
				<td><chits:waistToHipRatio waistCircumference="${entry.value[VisitConcepts.WAIST_CIRC_CM.conceptId]}" hipCircumference="${entry.value[VisitConcepts.HIP_CIRC_CM.conceptId]}" birthdate="${patient.birthdate}" showValue="${false}" showClassification="${true}"/></td>
				<td><chits:age birthdate="${patient.birthdate}" on="${entry.value[VisitConcepts.WAIST_CIRC_CM.conceptId].obsDatetime}" showClassification="${false}" /></td>
			</tr>
		</c:if>
		</c:forEach>
	</tbody>
</table>
<div class="buttons"><input type="button" onclick="javascript: notImplemented()" value="View Graph" /></div>

<h5>Circumference of Head and Chest</h5>
<table id="circumference-of-head-and-chest" class="form">
	<thead>
		<tr>
			<th>DATE TAKEN</th>
			<th>HEAD</th>
			<th>CHEST</th>
			<th>AGE TAKEN</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${encountersByDate}" var="entry">
		<c:if test="${not empty entry.value[VisitConcepts.HEAD_CIRC_CM.conceptId] or not empty entry.value[VisitConcepts.CHEST_CIRC_CM.conceptId]}">
			<tr>
				<td><fmt:formatDate value="${entry.key}" pattern="MM/dd/yyyy" /></td>
				<td><chits:circumference obs="${entry.value[VisitConcepts.HEAD_CIRC_CM.conceptId]}" showDateTaken="${false}" showElapsedSinceTaken="${false}" /></td>
				<td><chits:circumference obs="${entry.value[VisitConcepts.CHEST_CIRC_CM.conceptId]}" showDateTaken="${false}" showElapsedSinceTaken="${false}" /></td>
				<td><chits:age birthdate="${patient.birthdate}" on="${entry.value[VisitConcepts.CHEST_CIRC_CM.conceptId].obsDatetime}" showClassification="${false}" /></td>
			</tr>
		</c:if>
		</c:forEach>
	</tbody>
</table>
<div class="buttons"><input type="button" onclick="javascript: notImplemented()" value="View Graph" /></div>
