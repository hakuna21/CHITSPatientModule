<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>
<spring:message var="pageTitle" code="chits.PatientQueue.title" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/chits/patients/patientQueue.htm" />
<openmrs:portlet url="patientQueue" id="patientQueue" moduleId="chits"/>

<%@ include file="/WEB-INF/template/footer.jsp" %> 
