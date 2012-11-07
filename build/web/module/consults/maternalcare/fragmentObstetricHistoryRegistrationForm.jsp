<%@ page buffer="128kb"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<jsp:include page="fragmentRegistrationFormHeader.jsp" />

<br />
<div class="full-width" style="text-align: right">Page 1/2</div>

<br/>
<form:form id="obstetric-history-registration-form" modelAttribute="form" method="post" onsubmit="pleaseWaitDialog()">
<%@ include file="chartfragments/obstetricHistoryForm.jsp" %>

<br/>
<div class="full-width" style="text-align: right">
<input type="button" id="cancelButton" value='Cancel' onclick="document.location.href='viewMaternalCareProgram.form?patientId=${form.patient.patientId}'" />
<input type="submit" id="saveButton" value='Next Page' />
</div>
</form:form>