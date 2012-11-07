<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<style>
td.label { width: 12em; }
th.label { text-align: left; }
table.delivery-info, table.delivery-info td, table.delivery-info th { border: 0px; }
table.delivery-info select, table.delivery-info textarea { width: 80%; }
table.delivery-info textarea { height: 5em; }
td.right-label { text-align: right; }
</style>

<h3>DELIVERY INFORMATION FORM</h3>
<openmrs:htmlInclude file="/moduleResources/chits/scripts/calendar.js" />

<div id="deliveryInfoMsg">
<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
</div>

<c:if test="${err != null}">
	<div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div>
</c:if>

<%-- NOTE: Display the form if and only if the patient queue encounter has already been initialized! --%>
<c:choose><c:when test="${not empty form.patientQueue.encounter}">
<%-- Delivery information --%>

<form:form modelAttribute="form" method="post" action="updateChildCareDeliveryInformation.form" onsubmit="submitAjaxForm(this, ${form.patient.patientId}); return false;">
<form:hidden path="version" />
<input type="hidden" name="patientId" value="${form.patient.patientId}" />

<table class="delivery-info full-width">
<%@ include file="editDeliveryInfo.jsp" %>
</table>

<br/>
<div id="newbornScreeningInfo">
<%@include file="fragmentNewbornScreeningInformation.jsp" %>
</div>

<br/>
<input type="submit" id="saveButton" value='Save' />
</form:form>
<%-- End of Delivery information --%>
</c:when><c:otherwise>
<%@ include file="ajaxFragmentStartConsult.jsp" %>
</c:otherwise></c:choose>
