<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="fmt" uri="/WEB-INF/taglibs/fmt-rt.tld"
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ attribute name="enc" required="true" type="org.openmrs.Encounter"
%><%--

	Use consult start observation value date or encounter creation timestamp for start consult time, whichever is available

--%><c:set var="startTimestamp" value="${chits:coalesce(chits:observation(enc, VisitConcepts.CONSULT_START).valueDatetime, enc.dateCreated)}" /><%--

    Use consult end observation value date or notes creation timestamp for end consult time, whichever is available

--%><c:set var="endTimestamp" value="${chits:coalesce(chits:observation(enc, VisitConcepts.CONSULT_END).valueDatetime, chits:observation(enc, VisitConcepts.NOTES_NUMBER).dateCreated)}" /><%--

	In case we still didn't get an 'end' timestamp, try using the 'last changed' observation time of the encounter

--%><c:if test="${endTimestamp eq null}"><c:set var="endTimestamp" value="${chits:coalesce(enc.dateChanged, enc.dateCreated)}"/></c:if><%--	

	Print out the start time

--%><fmt:formatDate pattern="MM/dd/yyyy'<br/>'hh:mm a" value="${startTimestamp}" /> to<br/><%--

	Print out the end time

--%><fmt:formatDate pattern="MM/dd/yyyy'<br/>'hh:mm a" value="${endTimestamp}" /><br/><%--

	Print out the elapsed time in minutes

--%>(<fmt:formatNumber pattern="0.00" value="${(endTimestamp.time - startTimestamp.time) / 60000}" /> minutes)