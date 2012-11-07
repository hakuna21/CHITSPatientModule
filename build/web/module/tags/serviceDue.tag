<%@ taglib prefix="fmt" uri="/WEB-INF/taglibs/fmt-rt.tld"
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ attribute name="due" required="true" type="org.openmrs.module.chits.eccdprogram.ServiceDueInfo"
%><c:choose><c:when test="${due.type eq ServiceDueInfoType.NOT_DUE}">none</c:when
><c:when test="${due.type eq ServiceDueInfoType.DUE}"><fmt:formatDate pattern="dd MMM yyyy" value="${due.dueDate}" /></c:when
><c:when test="${due.type eq ServiceDueInfoType.OVERDUE}"><em class="alert"><fmt:formatDate pattern="dd MMM yyyy" value="${due.dueDate}" /></em></c:when
><c:when test="${due.type eq ServiceDueInfoType.NOT_ELIGIBLE}">not eligible for service</c:when
><c:otherwise>unknown</c:otherwise></c:choose>