<%@ tag import="java.util.List"
%><%@ tag import="org.openmrs.module.chits.FamilyFolder"
%><%@ tag import="org.openmrs.module.chits.PhilhealthUtil"
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ attribute name="patient" required="true" type="org.openmrs.Patient"
%><chits_tag:foldersOf patient="${patient}" /><c:set var="philhealthStatus" value='<%= PhilhealthUtil.getPhilhealthStatus(patient, (List<FamilyFolder>) request.getAttribute("folders")) %>' scope="request" />