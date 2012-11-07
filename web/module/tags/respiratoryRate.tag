<%@ taglib prefix="fmt" uri="/WEB-INF/taglibs/fmt-rt.tld"
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ attribute name="rate" required="true" type="org.openmrs.Obs"
%><fmt:formatNumber pattern="0" value="${rate.valueNumeric}"
/> <span class="obsTaken">Taken <fmt:formatDate pattern="MM/dd/yyyy" value="${rate.obsDatetime}" /></span> <span class="obsElapsedSince">[<chits:elapsed since="${rate.obsDatetime}" /> ago]</span>