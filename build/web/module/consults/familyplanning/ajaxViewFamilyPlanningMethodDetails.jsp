<%@ page import="java.util.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp"
%>

<fieldset><table class="borderless full-width form registration chart">
<c:choose><c:when test="${param.viewEnrollment ne null}">
	<tr>
		<td>Method</td>
		<td><chits_tag:obsValue obs="${form.familyPlanningMethod.obs}" /></td>
	</tr><tr>
		<td>Enrollment Date</td>
		<td><fmt:formatDate pattern="d MMM yyyy" value="${chits:observation(form.familyPlanningMethod.obs, FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT).valueDatetime}" /></td>
	</tr><tr>
		<td>Client type</td>
		<td><chits_tag:obsValue obs="${chits:observation(form.familyPlanningMethod.obs, FPFamilyPlanningMethodConcepts.CLIENT_TYPE)}" /></td>
	</tr><tr>
		<td>Remarks</td>
		<td><chits_tag:obsValue obs="${chits:observation(form.familyPlanningMethod.obs, FPFamilyPlanningMethodConcepts.REMARKS)}" /></td>
	</tr>
</c:when><c:otherwise>
	<tr>
		<td>Method</td>
		<td><chits_tag:obsValue obs="${form.familyPlanningMethod.obs}" /></td>
	</tr><tr>
		<td>Dropout Date</td>
		<td><fmt:formatDate pattern="d MMM yyyy" value="${chits:observation(form.familyPlanningMethod.obs, FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT).valueDatetime}" /></td>
	</tr><tr>
		<td>Reason</td>
		<td><chits_tag:obsValue obs="${chits:observation(form.familyPlanningMethod.obs, FPFamilyPlanningMethodConcepts.DROPOUT_REASON)}" /></td>
	</tr><tr>
		<td>Remarks</td>
		<td><chits_tag:obsValue obs="${chits:observation(form.familyPlanningMethod.obs, FPFamilyPlanningMethodConcepts.REMARKS)}" /></td>
	</tr>
</c:otherwise></c:choose>
</table></fieldset>