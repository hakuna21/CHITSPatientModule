<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="spring" uri="/WEB-INF/taglibs/spring.tld" 
%><%@ taglib prefix="form" uri="/WEB-INF/taglibs/spring-form.tld"
%><%@ attribute name="path" required="true" type="java.lang.String"
%><%@ attribute name="size" required="false" type="java.lang.Integer"
%><%@ attribute name="id" required="false" type="java.lang.String"
%><%@ attribute name="onclick" required="false" type="java.lang.String"
%><%@ attribute name="cssClass" required="false" type="java.lang.String"
%><%@ attribute name="cssStyle" required="false" type="java.lang.String"
%><spring:bind path="${path}"
	><form:input path="${status.expression}" htmlEscape="${true}" size="${size}" id="${id}" onclick="${onclick}" cssClass="${cssClass}" cssStyle="${cssStyle}"
	/><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if
></spring:bind>