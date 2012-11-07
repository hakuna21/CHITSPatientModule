<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp" %>

<div>
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

<script>
function cancelForm() {
	$j("#generalForm").dialog('close')
}
</script>

<form:form modelAttribute="form" method="post" onsubmit="submitAjaxForm(this, ${form.patient.patientId}); return false;">
<form:hidden path="version" />

<fieldset><legend><span>FAMILY PLANNING METHOD UPDATE FORM</span></legend>

This patient is a learning user of a natural family planning method.
Please click on the desired action then PROCEED.

<br/><br/>
<table id="update-family-planning-method" class="full-width borderless registration">
	<tr>
		<td class="label"><input type="radio" name="updateMethod" id="change-to-acceptor" checked="checked" value="changeToAcceptor"><label for="change-to-acceptor">Change client type to acceptor</label></td>
	</tr>
	<c:if test="${form.familyPlanningMethod.obs.obsId gt 0}">
	<tr>
		<td class="label"><input type="radio" name="updateMethod" id="discontinue" value="discontinue"><label for="discontinue">Discontinue the method</label></td>
	</tr>
	</c:if>
</table>
</fieldset>

<br/>
<div class="full-width" style="text-align: right">
<input type="submit" id="saveButton" value='Save' />
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
</div>
</form:form>