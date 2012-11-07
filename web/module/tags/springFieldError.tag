<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="spring" uri="/WEB-INF/taglibs/spring.tld" 
%><%@ attribute name="path" required="true" type="java.lang.String"
%><spring:bind path="${path}"
	><c:if test="${not empty status.errorMessage}"
		><div class="error">${status.errorMessage}</div>
	</c:if
></spring:bind>