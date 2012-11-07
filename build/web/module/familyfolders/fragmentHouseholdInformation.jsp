<table class="form" id="household_sanitation">
	<tr><th colspan="2">Household Sanitation</th></tr>
	<spring:bind path="familyFolder.householdInformation.dateCreated">
	<c:if test="${not empty status.value}">
	<tr>
		<td>Created By</td>
		<td>${familyFolder.householdInformation.creator.person.personName} on ${status.value}</td>
	</tr>
	</c:if>
	</spring:bind>
	<spring:bind path="familyFolder.householdInformation.dateChanged">
	<c:if test="${not empty status.value}">
	<tr>
		<td>Modified By</td>
		<td>${familyFolder.householdInformation.changedBy.person.personName} on ${status.value}</td>
	</tr>
	</c:if>
	</spring:bind>
	<tr>
		<td>Access to improved or safe water supply</td>
		<td><spring:message code="chits.environemnt.sanitation.watersupply.${chits:coalesce(familyFolder.householdInformation.accessToWaterSupply, 'no_data')}" /></td>
	</tr>
	<tr>
		<td>Household Toilet Facility</td>
		<td><spring:message code="chits.environemnt.sanitation.toiletfacility.${chits:coalesce(familyFolder.householdInformation.toiletFacility, 'no_data')}" /></td>
	</tr>
	<tr>
		<td>Household Toilet Location</td>
		<td><spring:message code="chits.environemnt.sanitation.toiletlocation.${chits:coalesce(familyFolder.householdInformation.toiletLocation, 'no_data')}" /></td>
	</tr>
	<tr>
		<td>Disposal of Solid Waste</td>
		<td><spring:message code="chits.environemnt.sanitation.disposalofsolidwaste.${chits:coalesce(familyFolder.householdInformation.disposalOfSolidWaste, 'no_data')}" /></td>
	</tr>
	<spring:bind path="familyFolder.householdInformation.dateFirstInspected">
	<tr>
		<td>Date First Inspected<br/>(<openmrs:datePattern />)</td>
		<td>${status.value}<c:if test="${empty status.value}">no data</c:if></td>
	</tr>
	</spring:bind>
	<spring:bind path="familyFolder.householdInformation.reinspectionDate">
	<tr>
		<td>Re-inspection Date<br/>(<openmrs:datePattern />)</td>
		<td>${status.value}<c:if test="${empty status.value}">no data</c:if></td>
	</tr>
	</spring:bind>
	<tr>
		<td>Families sharing the household</td>
		<td>
			<c:set var="sharedWith" value="${familyFolder.familiesSharingHousehold}" />
			<c:choose><c:when test="${not empty sharedWith}">
				<c:forEach var="folder" items="${sharedWith}" varStatus="i">
					${folder.code} (${folder.name})<c:if test="${i.count lt fn:length(sharedWith)}">,</c:if>
				</c:forEach>
			</c:when><c:otherwise>
				none
			</c:otherwise></c:choose>
		</td>
	</tr>
</table>