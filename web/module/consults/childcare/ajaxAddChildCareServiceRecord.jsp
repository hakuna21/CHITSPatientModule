<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
<c:if test="${err != null}">
	<span class="validationError"><div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div></span>
</c:if>

<%-- NOTE: Display the form if and only if the patient queue encounter has already been initialized! --%>
<c:choose><c:when test="${not empty form.patientQueue.encounter}">
<%-- Child care services information --%>
<style>
#addServiceForm select {width:100%}
em.service-warning { display: block; text-align: left; margin-bottom: 0.5em;}
.validationError { width: 100%; white-space: normal; }
.validationError .error { white-space: normal; width: 100%; }
</style>
<script>
<%-- Use the global javascript variable defined in the update child care services dialog --%>
$j("#addServiceForm input[name=version]").val(updateChildCareServicesVersion)
</script>

<c:choose><c:when test="${chits:concept(ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION) eq form.serviceRecord.serviceType.valueCoded}">
	<h3>VITAMIN A SUPPLEMENTATION</h3>
	<c:forEach var="alert" items="${serviceStatus.vitaminAServiceAlerts}">
	<em class="service-warning alert"><spring:message code="${alert}" text="${alert}" /></em>
	</c:forEach>
</c:when><c:when test="${chits:concept(ChildCareServiceTypes.DEWORMING) eq form.serviceRecord.serviceType.valueCoded}">
	<h3>DEWORMING SERVICE</h3>
	<em class="service-warning alert">
		WARNING: Deworming medication must be administered on a FULL STOMACH.
		Do not administer if child is severely ill, has abdominal pain, diarrhea, or is
		severely malnourished.
	</em>
	<c:forEach var="alert" items="${serviceStatus.dewormingAlerts}">
	<em class="service-warning alert"><spring:message code="${alert}" text="${alert}" /></em>
	</c:forEach>
</c:when><c:when test="${chits:concept(ChildCareServiceTypes.FERROUS_SULFATE) eq form.serviceRecord.serviceType.valueCoded}">
	<h3>IRON SUPPLEMENTATION</h3>
	<c:forEach var="alert" items="${serviceStatus.ferrousSulfateAlerts}">
	<em class="service-warning alert"><spring:message code="${alert}" text="${alert}" /></em>
	</c:forEach>
</c:when></c:choose>

<script>
function confirmAddService(form, patientId) {
	$j("<div><h5 class='alert'>WARNING: The service record may not be erased once posted.</h5></div>").dialog({
		resizable:true,width:400,height:'auto',modal:true,closeOnEscape:false,
		title:'Have you reviewed the information?',
		buttons:{
			"Yes, I've reviewed all the information":function(){$j(this).dialog("close"); submitAddServiceRecordForm(form, patientId)},
			"Cancel":function(){$j(this).dialog("close")}
		}
	})
}

</script>

<form:form id="addServiceForm" modelAttribute="form" method="post" action="addChildCareServiceRecord.form" onsubmit="confirmAddService(this, ${form.patient.patientId}); return false;">
	<input type="hidden" name="version" />
	<form:hidden path="serviceRecord.serviceType.valueCoded" />
	<input type="hidden" name="patientId" value="${form.patient.patientId}" />
	<table class="borderless full-width">
		<%-- <tr><td colspan="2">* Required Field</td></tr> --%>
		<tr>
		<spring:bind path="serviceRecord.dateGiven.valueText">
			<td>Date Given*</td>
			<td>
				<c:set var="now">dateServiceGiven_<%= System.currentTimeMillis() %></c:set>
				<form:input path="${status.expression}" onfocus="showCalendar(this);" cssClass="calendar" id="${now}" />
				<c:if test="${status.errorMessage != ''}"><span class="validationError"><br/><div class="error">${status.errorMessage}</div></span></c:if>
			</td>
		</spring:bind>
		</tr>
		<tr>
		<spring:bind path="serviceRecord.quantityOrDosage.valueCoded">
			<td>
				<c:choose><c:when test="${form.serviceRecord.vitaminAServiceType}">
				Quantity/Dosage*
				</c:when><c:otherwise>
				Medication*
				</c:otherwise></c:choose>
			</td>
			<td>
				<form:select path="${status.expression}">
					<option value="">--Select--</option>
					<c:forEach var="medication" items="${medicationOpts}">
					<form:option value="${medication.conceptId}">${medication.name}</form:option>
					</c:forEach>
				</form:select>
				<c:if test="${status.errorMessage != ''}"><span class="validationError"><div class="error">${status.errorMessage}</div></span></c:if>
			</td>
		</spring:bind>
		</tr>
		<tr>
			<td>Remarks <c:if test="${not form.serviceRecord.dewormingServiceType}">*</c:if></td>
			<td>
			<c:choose><c:when test="${chits:concept(ChildCareServiceTypes.VITAMIN_A_SUPPLEMENTATION) eq form.serviceRecord.serviceType.valueCoded}">
				<spring:bind path="serviceRecord.remarks.valueCoded">
					<form:select path="${status.expression}">
						<option value="">--Select--</option>
						<c:forEach var="remarks" items="${remarksOpts}">
						<form:option value="${remarks.conceptId}">${remarks.name}</form:option>
						</c:forEach>
					</form:select>
					<c:if test="${status.errorMessage != ''}"><span class="validationError"><div class="error">${status.errorMessage}</div></span></c:if>
				</spring:bind>
			</c:when><c:otherwise>
				<spring:bind path="serviceRecord.remarks.valueText">
					<textarea name="${status.expression}" rows="5" cols="30">${status.value}</textarea>
					<c:if test="${status.errorMessage != ''}"><span class="validationError"><div class="error">${status.errorMessage}</div></span></c:if>
				</spring:bind>
			</c:otherwise></c:choose>
			</td>
		</tr>
		<tr>
		<spring:bind path="serviceRecord.serviceSource.valueCoded">
			<td>Service Source*</td>
			<td>
				<form:select path="${status.expression}">
					<option value="">Select Service Source</option>
					<c:forEach var="ss" items="${chits:answers(ChildCareServicesConcepts.SERVICE_SOURCE)}">
					<form:option value="${ss.conceptId}">${ss.name}</form:option>
					</c:forEach>
				</form:select>
				<c:if test="${status.errorMessage != ''}"><span class="validationError"><div class="error">${status.errorMessage}</div></span></c:if>
			</td>
		</spring:bind>
		</tr>
	</table>
	<br/>
	<input type="submit" value="Save" />
	<input type="button" value="Cancel" onclick="$j('#secondLevelGeneralForm').dialog('close')"/>
</form:form>

<%-- End of Child care services information --%>
</c:when><c:otherwise>
<%@ include file="ajaxFragmentStartConsult.jsp" %>
</c:otherwise></c:choose>
