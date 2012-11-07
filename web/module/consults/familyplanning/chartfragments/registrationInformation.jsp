<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ page import="org.openmrs.module.chits.fpprogram.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %><%

	// determine if the date of next service is overdue
	final FamilyPlanningConsultEntryForm form = (FamilyPlanningConsultEntryForm) pageContext.findAttribute("form");
	final FamilyPlanningMethod fpm = form.getFpProgramObs().getLatestFamilyPlanningMethod();
	Date dateOfNextService = null;
	
	if (fpm != null) {
		if (!fpm.isDroppedOut() && !fpm.isPermanentMethod()) {
			dateOfNextService = fpm.getDateOfNextService();
			final Date today = DateUtil.stripTime(new Date());
			if (dateOfNextService != null && today.after(DateUtil.stripTime(dateOfNextService))) {
				pageContext.setAttribute("fpNextServiceOverdue", Boolean.TRUE);
			} else {
				pageContext.setAttribute("fpNextServiceOverdue", Boolean.FALSE);
			}
		}
		
		// NOTE: if 'date of next service is null', then the method does not specify a date of next service!
	} else {
		// if no family planning method registered yet, use system date of registration in case no data has been entered yet
		dateOfNextService = form.getFpProgramObs().getPatientProgram().getDateEnrolled();
	}

	// store date of next service
	pageContext.setAttribute("fpDateOfNextService", dateOfNextService);

	// store current family planning method
	pageContext.setAttribute("currentMethod", fpm);

%><c:set var="registeredState" value="${chits:findPatientProgramState(form.fpProgramObs.patientProgram, FamilyPlanningProgramStates.REGISTERED)}"
/><table class="full-width borderless registration">
<c:choose><c:when test="${registeredState eq null}">
<tr><td class="label">Registration No:</td><td><tt class="alert">NOT YET REGISTERED</tt></td></tr>
<tr><td class="label">Date Registered:</td><td></td></tr>
<tr><td class="label">Current Method:</td><td>none</td></tr>
</c:when><c:otherwise>
<tr><td class="label">Registration No:</td><td class="alert"><fmt:formatNumber pattern="0000000" value="${registeredState.id}" /></td></tr>
<tr><td class="label">Date Registered:</td><td><fmt:formatDate value="${registeredState.startDate}" /></td></tr>
<tr><td class="label">Current Method:</td><td><c:choose><c:when test="${not currentMethod.droppedOut}"><chits_tag:obsValue obs="${currentMethod.obs}" /></c:when><c:otherwise>none</c:otherwise></c:choose></td></tr>
<c:choose><c:when test="${not empty fpDateOfNextService}">
<tr><td class="label">Schedule of Next Service:</td><td><div<c:if test="${fpNextServiceOverdue}"> class="alert"</c:if>><fmt:formatDate pattern="MMMM d, yyyy" value="${fpDateOfNextService}" /></div></td></tr>
<c:if test="${currentMethod.methodShouldBeDropped}">
<tr><td colspan="2"><div class="alert">WARNING: Patient should be dropped out from <chits_tag:obsValue obs="${currentMethod.obs}" shortName="${true}" /></div></td></tr>
</c:if></c:when><c:otherwise>
<tr><td class="label">Schedule of Next Service:</td><td>N/A</td></tr>
</c:otherwise></c:choose>
</c:otherwise></c:choose>
</table>