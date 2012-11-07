<%@ taglib prefix="fmt" uri="/WEB-INF/taglibs/fmt-rt.tld"
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ attribute name="obs" required="true" type="org.openmrs.Obs"
%><%@ attribute name="shortName" required="false" type="java.lang.Boolean"
%><%@ attribute name="noData" required="false" type="java.lang.String"
%><c:choose><c:when test="${obs.concept.datatype.boolean}"><c:choose><c:when test="${obs.valueCoded eq chits:trueConcept()}">Yes</c:when><c:when test="${obs.valueCoded eq chits:falseConcept()}">No</c:when><c:otherwise>Unknown</c:otherwise></c:choose></c:when
><c:when test="${not empty obs.valueCoded.name}"><c:choose><c:when test="${shortName eq true and not empty obs.valueCoded.shortNames}">${obs.valueCoded.shortNames[0].name}</c:when><c:otherwise>${obs.valueCoded.name}</c:otherwise></c:choose></c:when
><c:when test="${not empty obs.valueNumeric}"><fmt:formatNumber pattern="#,##0.##" value="${obs.valueNumeric}" /></c:when
><c:when test="${not empty obs.valueDatetime}"><fmt:formatDate pattern="MM/dd/yyyy" value="${obs.valueDatetime}" /></c:when
><c:when test="${not empty obs.valueText}">${obs.valueText}</c:when
><c:otherwise>${noData}</c:otherwise></c:choose>