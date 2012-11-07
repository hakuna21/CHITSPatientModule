<%@   tag import="org.openmrs.api.APIAuthenticationException"
%><%@ tag import="org.openmrs.module.chits.fpprogram.*"
%><%@ tag import="org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FamilyPlanningProgramStates"
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ attribute name="patient" required="true" type="org.openmrs.Patient"
%><%

	FamilyPlanningProgramStates status = null;
	FamilyPlanningMethod fpm = null;
	try {
		FamilyPlanningProgramObs fpProgramObs = new FamilyPlanningProgramObs(FamilyPlanningUtil.getObsForActiveFamilyPlanningProgramOrFail(patient));
		status = fpProgramObs.getCurrentState();

		// in case a method is already available
		fpm = fpProgramObs.getLatestFamilyPlanningMethod();
	} catch (APIAuthenticationException apiE) {
		jspContext.getOut().print("Not enrolled");
	}

%><c:out value="<%= status %>" /><%
	if (fpm != null && fpm.getObs().getValueCoded() != null) {
		%> (<chits_tag:obsValue obs="<%= fpm.getObs() %>" shortName="${true}" />)<%
	}
%><%
	if (fpm != null && fpm.isDroppedOut()) {
		%>; <%= fpm.getDropoutReason() %><%	
	}
%>