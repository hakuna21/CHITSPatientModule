<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ taglib prefix="spring" uri="/WEB-INF/taglibs/spring.tld" 
%><%@ taglib prefix="form" uri="/WEB-INF/taglibs/spring-form.tld"
%><%@ attribute name="path" required="true" type="java.lang.String"
%><%@ attribute name="answers" required="true" type="java.util.Collection"
%><%@ attribute name="select" required="false" type="java.lang.String"
%><%@ attribute name="id" required="false" type="java.lang.String"
%><%--

	NOTE: The main difference between a springDropdown and a springConcpetIdDropdown is that
		  the springConcpetIdDropdown assumes the answers are concepts and uses the answer
		  concept's ID as the option key instead of the answer itself.

--%><spring:bind path="${path}"
	><form:select path="${status.expression}" id="${id}"
		><form:option value="">${chits:coalesce(select, 'select')}</form:option
		><c:forEach var="answer" items="${answers}"
			><form:option value="${answer.conceptId}">${answer.name}</form:option
		></c:forEach
	></form:select><c:if test="${not empty status.errorMessage}"><div class="error">${status.errorMessage}</div></c:if
></spring:bind>