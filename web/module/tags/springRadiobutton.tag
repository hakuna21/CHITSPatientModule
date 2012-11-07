<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ taglib prefix="spring" uri="/WEB-INF/taglibs/spring.tld" 
%><%@ taglib prefix="form" uri="/WEB-INF/taglibs/spring-form.tld"
%><%@ attribute name="path" required="true" type="java.lang.String"
%><%@ attribute name="label" required="true" type="java.lang.String"
%><%@ attribute name="value" required="true" type="java.lang.Object"
%><%@ attribute name="id" required="false" type="java.lang.String"
%><%@ attribute name="disabled" required="false" type="java.lang.Boolean"
%><c:choose><c:when test="${empty id}"
	><c:set var="idCounter" scope="session" value="${idCounter + 1}"
	/><c:set var="idTag" value="rb-${idCounter}"
/></c:when><c:otherwise
	><c:set var="idTag" value="${id}"
/></c:otherwise></c:choose
><spring:bind path="${path}"
	><form:radiobutton path="${status.expression}" id="${idTag}" value="${value}" disabled="${disabled}"
	/>&nbsp;<label for="${idTag}">${label}</label></spring:bind>