<%@tag import="org.openmrs.api.APIAuthenticationException"%>
<%@tag import="org.openmrs.module.chits.mcprogram.MaternalCareUtil"%>
<%@tag import="org.openmrs.module.chits.mcprogram.MaternalCareProgramObs"%>
<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ attribute name="patient" required="true" type="org.openmrs.Patient"
%><%
	try {
		final MaternalCareProgramObs mcProgramObs = new MaternalCareProgramObs(MaternalCareUtil.getObsForActiveMaternalCareProgramOrFail(patient));
		jspContext.getOut().print(mcProgramObs.getCurrentState());
	} catch (APIAuthenticationException apiE) {
		jspContext.getOut().print("Not enrolled");
	}
%>