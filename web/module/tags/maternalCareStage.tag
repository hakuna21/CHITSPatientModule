<%@ tag import="org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCMaternityStage" 
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="spring" uri="/WEB-INF/taglibs/spring.tld"
%><%@ attribute name="mcProgramObs" required="true" type="org.openmrs.module.chits.mcprogram.MaternalCareProgramObs"
%><%@ attribute name="on" required="true" type="java.util.Date"
%><%
	final MCMaternityStage stage = mcProgramObs.getMaternityStageOn(on);
	if (stage != null) {		
		jspContext.setAttribute("msg", stage.getKey());
		%><spring:message code="${msg}" text="${msg}" /><%	
	} else {
		%>N/A<%
	}
%>