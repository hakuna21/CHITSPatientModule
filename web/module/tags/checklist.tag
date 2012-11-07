<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ attribute name="items" required="true" type="java.util.Collection"
%><%@ attribute name="otherConcept" required="false" type="org.openmrs.module.chits.CachedConceptId"
%><c:set var="count" value="${0}" /><c:if test="${not empty otherConcept}"><c:set var="otherConcept" value="${chits:concept(otherConcept)}" /></c:if
><c:forEach var="item" items="${items}"
><c:if test="${item.valueCoded eq chits:trueConcept() and not item.voided}"><c:if test="${count gt 0}">, </c:if
		><c:choose
			><c:when test="${item.concept eq otherConcept}">other</c:when
			><c:when test="${not empty item.concept.shortNames}">${item.concept.shortNames[0].name}</c:when
			><c:otherwise>${item.concept.name}</c:otherwise
		></c:choose><c:set var="count" value="${count+1}"
		/><c:if test="${item.concept eq otherConcept and not empty item.valueText}">: ${item.valueText}</c:if
></c:if
></c:forEach><c:if test="${count eq 0}"><em>none</em></c:if>