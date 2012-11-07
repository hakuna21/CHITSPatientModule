<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ taglib prefix="fmt" uri="/WEB-INF/taglibs/fmt-rt.tld"
%><%@ attribute name="obsGroup" required="false" type="org.openmrs.Obs"
%><%@ attribute name="gravidaObs" required="false" type="org.openmrs.Obs"
%><%@ attribute name="paraObs" required="false" type="org.openmrs.Obs"
%><%@ attribute name="ftObs" required="false" type="org.openmrs.Obs"
%><%@ attribute name="ptObs" required="false" type="org.openmrs.Obs"
%><%@ attribute name="amObs" required="false" type="org.openmrs.Obs"
%><%@ attribute name="lcObs" required="false" type="org.openmrs.Obs"
%><%@ attribute name="gravidaOnly" required="false" type="java.lang.Boolean"
%><%@ attribute name="full" required="false" type="java.lang.Boolean"
%>G<c:set var="scoreObs" value="${chits:coalesce(gravidaObs, chits:observation(obsGroup, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_GRAVIDA))}" /><c:choose><c:when test="${not empty scoreObs.valueNumeric}"><fmt:formatNumber pattern="0" value="${scoreObs.valueNumeric}" /></c:when><c:otherwise>-</c:otherwise></c:choose
><c:if test="${not gravidaOnly}"
>P<c:set var="scoreObs" value="${chits:coalesce(paraObs, chits:observation(obsGroup, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PARA))}" /><c:choose><c:when test="${not empty scoreObs.valueNumeric}"><fmt:formatNumber pattern="0" value="${scoreObs.valueNumeric}" /></c:when><c:otherwise>-</c:otherwise></c:choose
></c:if
><c:if test="${full}"
>&nbsp;(<c:set var="scoreObs" value="${chits:coalesce(ftObs, chits:observation(obsGroup, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_FT))}" /><c:choose><c:when test="${not empty scoreObs.valueNumeric}"><fmt:formatNumber pattern="0" value="${scoreObs.valueNumeric}" /></c:when><c:otherwise>-</c:otherwise></c:choose
>,<c:set var="scoreObs" value="${chits:coalesce(ptObs, chits:observation(obsGroup, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_PT))}" /><c:choose><c:when test="${not empty scoreObs.valueNumeric}"><fmt:formatNumber pattern="0" value="${scoreObs.valueNumeric}" /></c:when><c:otherwise>-</c:otherwise></c:choose
>,<c:set var="scoreObs" value="${chits:coalesce(amObs, chits:observation(obsGroup, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_AM))}" /><c:choose><c:when test="${not empty scoreObs.valueNumeric}"><fmt:formatNumber pattern="0" value="${scoreObs.valueNumeric}" /></c:when><c:otherwise>-</c:otherwise></c:choose
>,<c:set var="scoreObs" value="${chits:coalesce(lcObs, chits:observation(obsGroup, MCObstetricHistoryConcepts.OBSTETRIC_SCORE_LC))}" /><c:choose><c:when test="${not empty scoreObs.valueNumeric}"><fmt:formatNumber pattern="0" value="${scoreObs.valueNumeric}" /></c:when><c:otherwise>-</c:otherwise></c:choose
>)</c:if>