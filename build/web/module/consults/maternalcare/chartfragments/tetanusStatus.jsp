<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="menstrualHistory" value="${form.mcProgramObs.menstrualHistory}" />
<c:set var="givenTetanusDose" value="${chits:observation(form.mcProgramObs.menstrualHistory.obs, MCMenstrualHistoryConcepts.GIVEN_TETANUS_DOSE)}"
/><c:set var="tetanusServiceInfo" value="${form.tetanusServiceInfo}"
/><c:set var="tetanusLegend"><c:choose
><c:when test="${tetanusServiceInfo.lastService eq TetanusToxoidDateAdministeredConcepts.TT1}"><span class="alert">TT1</span></c:when
><c:when test="${tetanusServiceInfo.lastService eq TetanusToxoidDateAdministeredConcepts.TT2}">TT1 &amp; TT2</c:when
><c:when test="${tetanusServiceInfo.lastService eq TetanusToxoidDateAdministeredConcepts.TT3}">TT3</c:when
><c:when test="${tetanusServiceInfo.lastService eq TetanusToxoidDateAdministeredConcepts.TT4}">TT4</c:when
><c:when test="${tetanusServiceInfo.lastService eq TetanusToxoidDateAdministeredConcepts.TT5}">TT5</c:when
><c:when test="${givenTetanusDose.valueCoded eq chits:trueConcept()}"><span class="alert">YES</span></c:when
><c:when test="${givenTetanusDose.valueCoded eq chits:falseConcept()}"><span class="alert">NONE</span></c:when
><c:otherwise><span class="alert">UNKNOWN</span></c:otherwise
></c:choose></c:set>

<fieldset><legend><span>Tetanus Status: ${tetanusLegend}</span></legend>
<table class="form full-width registration" id="tetanusStatus">
<tbody>
<tr><td style="width: 10em;">Last Service Date</td><td><fmt:formatDate value="${tetanusServiceInfo.lastServiceDate}" pattern="MMM d, yyyy" /></td></tr>
<tr><td style="width: 10em;">Next Service Date</td><td><fmt:formatDate value="${tetanusServiceInfo.nextServiceDate}" pattern="MMM d, yyyy" /></td></tr>
</tbody>
</table>
</fieldset>