<%@ taglib prefix="fmt" uri="/WEB-INF/taglibs/fmt-rt.tld"
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ attribute name="bloodType" required="true" type="org.openmrs.Obs"
%><%@ attribute name="rhFactor" required="true" type="org.openmrs.Obs"
%><c:choose
><c:when test="${not empty bloodType.valueCoded}">${bloodType.valueCoded.name}, <c:choose
	><c:when test="${not empty rhFactor.valueCoded}">${rhFactor.valueCoded.name}</c:when
	><c:otherwise>RH factor unknown</c:otherwise></c:choose
></c:when><c:otherwise>unknown</c:otherwise></c:choose>