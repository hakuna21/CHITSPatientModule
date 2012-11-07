<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ taglib prefix="spring" uri="/WEB-INF/taglibs/spring.tld" 
%><%@ taglib prefix="form" uri="/WEB-INF/taglibs/spring-form.tld"
%><%@ attribute name="path" required="true" type="java.lang.String"
%><%@ attribute name="label" required="false" type="java.lang.String"
%><%@ attribute name="id" required="false" type="java.lang.String"
%><c:choose><c:when test="${empty id}"
	><c:set var="idCounter" scope="session" value="${idCounter + 1}"
	/><c:set var="idTag" value="cb-${idCounter}"
/></c:when><c:otherwise
	><c:set var="idTag" value="${id}"
/></c:otherwise></c:choose

><spring:bind path="${path}"
	><form:checkbox path="${status.expression}" id="${idTag}" value="${chits:trueConcept()}"
	/><c:if test="${not empty label}"> <label for="${idTag}">${label}</label></c:if><c:if test="${not empty status.errorMessage}"
	><div class="error">${status.errorMessage}</div>
	</c:if
></spring:bind>