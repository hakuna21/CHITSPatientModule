<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<h3 style="margin-bottom: 0px;">CHILD CARE SERVICES</h3>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.tablescroll/jquery.tablescroll.js" />
<script>
function loadAddServiceRecordForm(serviceType, serviceTypeId) {
	$j("#secondLevelGeneralForm").loadAndCenter("<h4>Loading, Please Wait...</h4>", {width:380,title:'Add Service: '+serviceType})
	$j.ajax({url:'addChildCareServiceRecord.form?patientId=${form.patient.patientId}&serviceRecord.serviceType.valueCoded=' + serviceTypeId, cache: false, success: function (data) {
		$j('#secondLevelGeneralForm').loadAndCenter(data)
		highlightErrors()
	}})
}

function updateServicesDialog(patientId, onUpdate) {
	$j.ajax({url: 'viewChildCareServiceRecords.form?patientId=' + patientId, cache: false, success: function (data) {
		$j('#generalForm').loadAndCenter(data)
		if (onUpdate) {
			onUpdate()
		}
	}})
}

function submitAddServiceRecordForm(form, patientId) {
	var postData = {'patientId': patientId}
	$j(form).find("input, select, textarea").each(function() {
		if (!$j(this).is(":disabled")) {
			postData[$j(this).attr('name')] = $j(this).val()
		}
	})

	pleaseWaitDialog()
	$j.post(form.action, postData, function (data) {
		closePleaseWaitDialog()
		var div = $j('#secondLevelGeneralForm').loadAndCenter(data)
		highlightErrors()
		if (div.find("div.openmrs_msg").size() > 0 && div.find("div.openmrs_error").size() == 0) {
			updateServicesDialog(patientId, function() {
				updateECCDSection(patientId, function() {
						div.dialog("close")
				})		
			})
		}
	})
}

<%-- Global variable containing the child care services version --%>
var updateChildCareServicesVersion = ${form.version}
$j(document).ready(function() {
	 $j('#renderedVitaminAServices').tableScroll({height:94});
	 $j('#renderedFerrousSulfateServices').tableScroll({height:94});
	 $j('#renderedDewormingServices').tableScroll({height:94});
	 $j('#renderedDentalServices').tableScroll({height:94});
})
</script>

<style>
.tablescroll_head thead tr th { background-color: #ccc; border: 1px solid white; padding: -1px; }
td.date-given { width: 68px; }
td.medication { width: 226px; }
td.svc-source { width: 105px; }
td.hw-remarks { width: 200px; }
</style>

<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
<c:if test="${err != null}">
	<div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div>
</c:if>

<%-- Child care services information --%>

<c:set var="vitaminAServices" value="${serviceStatus.services[chits:concept(ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION)]}" />
<c:set var="dewormingServices" value="${serviceStatus.services[chits:concept(ChildCareServiceTypes.DEWORMING)]}" />
<c:set var="ferrousSulfateServices" value="${serviceStatus.services[chits:concept(ChildCareServiceTypes.FERROUS_SULFATE)]}" />
<c:set var="dentalRecordServices" value="${serviceStatus.services[chits:concept(ChildCareServiceTypes.DENTAL_RECORD)]}" />

<form name="dummy" action="#" onsubmit='$j("#generalForm").dialog("close"); return false;'>
<div style="width: 98%; text-align: center">
<table class="full-width">
<%-- <tr><td valign="top" style="width:50%;"> --%>
<tr><td valign="top" style="width:100%;">
	<h4>VITAMIN A SUPPLEMENTATION</h4>
	<table class="form full-width" id="renderedVitaminAServices">
	<thead>
	<tr><th>DATE GIVEN</th><th>DOSAGE</th><th>SERVICE SOURCE</th><th>REMARKS</th></tr>
	</thead><tbody>
	<c:choose><c:when test="${not empty vitaminAServices}">
		<c:forEach var="service" items="${vitaminAServices}">
		<tr>
			<td class="date-given"><fmt:formatDate pattern="MM/dd/yyyy" value="${chits:observation(service, ChildCareServicesConcepts.DATE_ADMINISTERED).valueDatetime}" /></td>
			<td class="medication">${chits:observation(service, ChildCareServicesConcepts.DOSAGE).valueCoded.name}</td>
			<td class="svc-source">${chits:observation(service, ChildCareServicesConcepts.SERVICE_SOURCE).valueCoded.name}</td>
			<td class="hw-remarks"><chits_tag:obsValue obs="${chits:observation(service, ChildCareServicesConcepts.REMARKS)}" /></td>
		</tr>
		</c:forEach>
	</c:when><c:otherwise>
		<tr><td colspan="4">none</td>
	</c:otherwise></c:choose>
	</tbody>
	</table>
	
	<c:forEach var="alert" items="${serviceStatus.vitaminAServiceAlerts}">
	<em class="service-warning alert"><spring:message code="${alert}" text="${alert}" /></em>
	</c:forEach>

	<div style="width: 100%; text-align: right;">
		<input type="button" value="Add Service" <c:if test="${not serviceStatus.vitaminAEnabled}">disabled="disabled"</c:if>
			onclick="loadAddServiceRecordForm('${chits:concept(ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION).name}', ${ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION.conceptId})" />
	</div>
<%-- </td><td valign="top" style="width:50%;"> --%>
</td></tr><tr><td valign="top" style="width:100%;">
	<h4>DEWORMING</h4>
	<table class="form full-width" id="renderedDewormingServices">
	<thead>
	<tr><th>DATE GIVEN</th><th>MEDICATION</th><th>SERVICE SOURCE</th><th>REMARKS</th></tr>
	</thead><tbody>
	<c:choose><c:when test="${not empty dewormingServices}">
		<c:forEach var="service" items="${dewormingServices}">
		<tr>
			<td class="date-given"><fmt:formatDate pattern="MM/dd/yyyy" value="${chits:observation(service, ChildCareServicesConcepts.DATE_ADMINISTERED).valueDatetime}" /></td>
			<td class="medication">${chits:observation(service, ChildCareServicesConcepts.DOSAGE).valueCoded.name}</td>
			<td class="svc-source">${chits:observation(service, ChildCareServicesConcepts.SERVICE_SOURCE).valueCoded.name}</td>
			<td class="hw-remarks"><chits_tag:obsValue obs="${chits:observation(service, ChildCareServicesConcepts.REMARKS)}" /></td>
		</tr>
		</c:forEach>
	</c:when><c:otherwise>
		<tr><td colspan="4">none</td>
	</c:otherwise></c:choose>
	</tbody>
	</table>

	<c:forEach var="alert" items="${serviceStatus.dewormingAlerts}">
	<em class="service-warning alert"><spring:message code="${alert}" text="${alert}" /></em>
	</c:forEach>

	<div style="width: 100%; text-align: right;">
		<input type="button" value="Add Service" <c:if test="${not serviceStatus.dewormingEnabled}">disabled="disabled"</c:if>
			onclick="loadAddServiceRecordForm('${chits:concept(ChildCareServiceTypes.DEWORMING).name}', ${ChildCareServiceTypes.DEWORMING.conceptId})" />
	</div>
<%-- </td></tr> --%>
<%-- <tr><td valign="top"> --%>
</td></tr><tr><td valign="top" style="width:100%;">
	<h4>FERROUS SULFATE</h4>
	<table class="form full-width" id="renderedFerrousSulfateServices">
	<thead>
	<tr><th>DATE GIVEN</th><th>MEDICATION</th><th>SERVICE SOURCE</th><th>REMARKS</th></tr>
	</thead><tbody>
	<c:choose><c:when test="${not empty ferrousSulfateServices}">
		<c:forEach var="service" items="${ferrousSulfateServices}">
		<tr>
			<td class="date-given"><fmt:formatDate pattern="MM/dd/yyyy" value="${chits:observation(service, ChildCareServicesConcepts.DATE_ADMINISTERED).valueDatetime}" /></td>
			<td class="medication">${chits:observation(service, ChildCareServicesConcepts.DOSAGE).valueCoded.name}</td>
			<td class="svc-source">${chits:observation(service, ChildCareServicesConcepts.SERVICE_SOURCE).valueCoded.name}</td>
			<td class="hw-remarks"><chits_tag:obsValue obs="${chits:observation(service, ChildCareServicesConcepts.REMARKS)}" /></td>
		</tr>
		</c:forEach>
	</c:when><c:otherwise>
		<tr><td colspan="4">none</td>
	</c:otherwise></c:choose>
	</tbody>
	</table>

	<c:forEach var="alert" items="${serviceStatus.ferrousSulfateAlerts}">
	<em class="service-warning alert"><spring:message code="${alert}" text="${alert}" /></em>
	</c:forEach>

	<div style="width: 100%; text-align: right;">
		<input type="button" value="Add Service" <c:if test="${not serviceStatus.ferrousSulfateEnabled}">disabled="disabled"</c:if>
			onclick="loadAddServiceRecordForm('${chits:concept(ChildCareServiceTypes.FERROUS_SULFATE).name}', ${ChildCareServiceTypes.FERROUS_SULFATE.conceptId})" />
	</div>
<%-- </td><td valign="top">  --%>
</td></tr><tr><td valign="top" style="width:100%;">
	<h4>DENTAL RECORD</h4>
	<table class="form full-width" id="renderedDentalServices">
	<thead>
	<tr><th>DATE GIVEN</th><th>SERVICE SOURCE</th><th>REMARKS</th></tr>
	</thead><tbody>
	<c:choose><c:when test="${not empty dentalRecordServices}">
		<c:forEach var="service" items="${dentalRecordServices}">
		<tr>
			<td class="date-given"><fmt:formatDate pattern="MM/dd/yyyy" value="${chits:observation(service, ChildCareServicesConcepts.DATE_ADMINISTERED).valueDatetime}" /></td>
			<td class="svc-source">${chits:observation(service, ChildCareServicesConcepts.SERVICE_SOURCE).valueCoded.name}</td>
			<td class="hw-remarks"><chits_tag:obsValue obs="${chits:observation(service, ChildCareServicesConcepts.REMARKS)}" /></td>
		</tr>
		</c:forEach>
	</c:when><c:otherwise>
		<tr><td colspan="3">none</td>
	</c:otherwise></c:choose>
	</tbody>
	</table>
	Entry and edit of dental records may be done only thru the Dental Module.
</td></tr>
</table>
</div>

<input type="submit" id="closeButton" value='Close' />
</form>

<%-- End of Child care services information --%>