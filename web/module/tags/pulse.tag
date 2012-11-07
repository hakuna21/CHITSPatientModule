<%@ taglib prefix="fmt" uri="/WEB-INF/taglibs/fmt-rt.tld"
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ attribute name="pulse" required="true" type="org.openmrs.Obs"
%><fmt:formatNumber pattern="0" value="${pulse.valueNumeric}"
/> <span class="obsTaken">Taken <fmt:formatDate pattern="MM/dd/yyyy" value="${pulse.obsDatetime}" /></span> <span class="obsElapsedSince">[<chits:elapsed since="${pulse.obsDatetime}" /> ago]</span>