<%@ tag import="java.util.List"
%><%@ tag import="org.openmrs.module.chits.eccdprogram.ChildCareUtil"
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld"
%><%@ attribute name="patient" required="true" type="org.openmrs.Patient"
%><c:set var="childProtectedAtBirthStatus" value="<%= ChildCareUtil.getCPABStatus(patient) %>" scope="request" />