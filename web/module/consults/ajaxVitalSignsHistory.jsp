<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"%>

<h4>Vital Signs</h4>

<h5>History</h5>
<table id="vital-signs-history" class="form">
	<thead>
		<tr>
			<th>Vital Signs</th>
			<th>Age Taken</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${encounters}" var="encounter">
		<c:forEach items="${chits:observations(encounter, VisitConcepts.VITAL_SIGNS)}" var="vitalSigns">
		<tr>
			<td><chits:vitalSigns vitalSigns="${vitalSigns}" /></td>
			<td><chits:age birthdate="${patient.birthdate}" on="${vitalSigns.obsDatetime}" /></td>
		</tr>
		</c:forEach>
		</c:forEach>
	</tbody>
</table>
<div class="buttons"><input type="button" onclick="javascript: notImplemented()" value="View Graph" /></div>
