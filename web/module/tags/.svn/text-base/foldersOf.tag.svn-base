<%@ tag import="org.openmrs.module.chits.CHITSService"
%><%@ tag import="org.openmrs.api.context.Context"
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ attribute name="patient" required="true" type="org.openmrs.Patient"
%><c:set var="folders" value="<%= Context.getService(CHITSService.class).getFamilyFoldersOf(patient.getPatientId()) %>" scope="request" />