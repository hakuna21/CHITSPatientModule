<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="spring" uri="/WEB-INF/taglibs/spring.tld" 
%><%@ taglib prefix="form" uri="/WEB-INF/taglibs/spring-form.tld"
%><%@ attribute name="path" required="true" type="java.lang.String"
%><%@ attribute name="id" required="false" type="java.lang.String"
%><%@ attribute name="rows" required="false" type="java.lang.String"
%><%@ attribute name="cssClass" required="false" type="java.lang.String"
%><spring:bind path="${path}"
	><form:textarea path="${status.expression}" cssStyle="width: 96%" id="${id}" rows="${rows}" cssClass="${cssClass}"
	/><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if
></spring:bind>