<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="fmt" uri="/WEB-INF/taglibs/fmt-rt.tld"
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ attribute name="obsGroup" required="true" type="org.openmrs.Obs"
%><c:set var="createdByObs" value="${chits:observation(obsGroup, AuditConcepts.CREATED_BY)}" />
<c:if test="${createdByObs ne null}">
	<c:set var="modifiedByObs" value="${chits:observation(obsGroup, AuditConcepts.MODIFIED_BY)}" />
	<br/>Created on <fmt:formatDate pattern="MMM d, yyyy" value="${createdByObs.valueDatetime}" /> by ${createdByObs.creator.person.personName}
	<c:if test="${modifiedByObs ne null}">
	<br/>Last updated on <fmt:formatDate pattern="MMM d, yyyy" value="${modifiedByObs.valueDatetime}" /> by ${modifiedByObs.creator.person.personName}
	</c:if>
	<br/>
</c:if>