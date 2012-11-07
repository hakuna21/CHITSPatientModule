<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="fmt" uri="/WEB-INF/taglibs/fmt-rt.tld"
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ attribute name="sbp" required="true" type="org.openmrs.Obs"
%><%@ attribute name="dbp" required="true" type="org.openmrs.Obs"
%><c:if test="${not empty sbp.valueNumeric and not empty dbp.valueNumeric}"><fmt:formatNumber pattern="0" value="${sbp.valueNumeric}" />/<fmt:formatNumber pattern="0" value="${dbp.valueNumeric}"
/> <span class="obsTaken">Taken <fmt:formatDate pattern="MM/dd/yyyy" value="${sbp.obsDatetime}" /></span> <span class="obsElapsedSince">[<chits:elapsed since="${sbp.obsDatetime}" /> ago]</span></c:if>