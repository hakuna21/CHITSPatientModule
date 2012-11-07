<%@ page buffer="128kb"
%><%@ page import="java.util.Date"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:choose><c:when test="${not empty form.page}">
	<jsp:include page="fragmentRegistrationFormHeader.jsp" />
</c:when><c:otherwise>
	<jsp:include page="../fragmentAjaxUpdateHeader.jsp" />
</c:otherwise></c:choose>

<br/>
<form:form id="obstetrical-history-registration-form" modelAttribute="form" method="post" cssClass="main-form">

<fieldset><legend><span><c:choose
	><c:when test="${not empty form.page}">Page 4: OBSTETRICAL HISTORY</c:when
	><c:otherwise>UPDATE OBSTETRICAL HISTORY</c:otherwise></c:choose
></span></legend>
<form:hidden path="version" />
<form:hidden path="page" />

<table class="full-width borderless registration field">

<tr>
	<td>
		<fieldset><legend>Obstetric Score</legend>
		<table><tr>
			<td class="label"> G  </td><td><chits_tag:springInput path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.OBSTETRIC_SCORE_GRAVIDA.conceptId}].valueText" size="1" /></td>
			<td class="label"> P  </td><td><chits_tag:springInput path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.OBSTETRIC_SCORE_PARA.conceptId}].valueText" size="1" /></td>
			<td class="label"> (F </td><td><chits_tag:springInput path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.OBSTETRIC_SCORE_FT.conceptId}].valueText" size="1" /></td>
			<td class="label"> P  </td><td><chits_tag:springInput path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.OBSTETRIC_SCORE_PT.conceptId}].valueText" size="1" /></td>
			<td class="label"> A  </td><td><chits_tag:springInput path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.OBSTETRIC_SCORE_AM.conceptId}].valueText" size="1" /></td>
			<td class="label"> L) </td><td><chits_tag:springInput path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.OBSTETRIC_SCORE_LC.conceptId}].valueText" size="1" /></td>
		</tr></table>
		</fieldset>
	</td>
</tr><tr>
	<td>
		<fieldset>
		<table class="full-width registration field">
			<tr><td class="label">Date of last delivery (mm/dd/yyyy)</td><td><chits_tag:springInput path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.DATE_OF_LAST_DELIVERY.conceptId}].valueText" onclick="showCalendar(this)" /></td></tr>
			<tr><td class="label">Type of last delivery</td><td><chits_tag:springDropdown path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.TYPE_OF_LAST_DELIVERY.conceptId}].valueCoded" answers="${chits:answers(FPObstetricHistoryConcepts.TYPE_OF_LAST_DELIVERY)}" /></td></tr>
			<tr><td class="label">Previous menstrual period (mm/dd/yyyy)</td><td><chits_tag:springInput path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.PREVIOUS_MENSTRUAL_PERIOD.conceptId}].valueText" onclick="showCalendar(this)" /></td></tr>
			<tr><td class="label">Last menstrual period (mm/dd/yyyy)</td><td><chits_tag:springInput path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.LAST_MENSTRUAL_PERIOD.conceptId}].valueText" onclick="showCalendar(this)" /></td></tr>
			<tr><td class="label">Duration of bleeding (days)</td><td><chits_tag:springInput path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.DURATION_OF_BLEEDING.conceptId}].valueText" size="2" /></td></tr>
		</table>
		</fieldset>
	</td>
</tr><tr>
	<td class="character-of-bleeding">
		<fieldset><legend>Character of bleeding</legend>
		<table class="full-width registration field">
		<tr><td class="label"><div class="indent"><chits_tag:springRadiobutton path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.DYSMENORRHEA.conceptId}].valueCoded" value="${chits:concept(FPObstetricOptions.PAINLESS)}" label="painless" /></div></td></tr>
		<tr><td class="label"><div class="indent"><chits_tag:springRadiobutton path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.DYSMENORRHEA.conceptId}].valueCoded" value="${chits:concept(FPObstetricOptions.PAINFUL)}" label="painful" /></div></td></tr>
		<tr><td class="label"><div class="indent"><chits_tag:springRadiobutton path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.DYSMENORRHEA.conceptId}].valueCoded" value="" label="none" /><chits_tag:springFieldError path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.DYSMENORRHEA.conceptId}].valueCoded" /></div></td></tr>
		<tr><td class="label"><div class="indent"><hr/></div></td></tr>
		<tr><td class="label"><div class="indent"><chits_tag:springRadiobutton path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.AMOUNT_OF_BLEEDING.conceptId}].valueCoded" value="${chits:concept(FPObstetricOptions.LIGHT)}" label="light" /></div></td></tr>
		<tr><td class="label"><div class="indent"><chits_tag:springRadiobutton path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.AMOUNT_OF_BLEEDING.conceptId}].valueCoded" value="${chits:concept(FPObstetricOptions.MODERATE)}" label="moderate" /></div></td></tr>
		<tr><td class="label"><div class="indent"><chits_tag:springRadiobutton path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.AMOUNT_OF_BLEEDING.conceptId}].valueCoded" value="${chits:concept(FPObstetricOptions.HEAVY)}" label="heavy" /></div></td></tr>
		<tr><td class="label"><div class="indent"><chits_tag:springRadiobutton path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.AMOUNT_OF_BLEEDING.conceptId}].valueCoded" value="" label="none" /><chits_tag:springFieldError path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.AMOUNT_OF_BLEEDING.conceptId}].valueCoded" /></div></td></tr>
		<tr><td class="label"><div class="indent"><hr/></div></td></tr>
		<tr><td class="label"><div class="indent"><chits_tag:springRadiobutton path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.REGULARITY.conceptId}].valueCoded" value="${chits:concept(FPObstetricOptions.REGULAR)}" label="regular" /></div></td></tr>
		<tr><td class="label"><div class="indent"><chits_tag:springRadiobutton path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.REGULARITY.conceptId}].valueCoded" value="${chits:concept(FPObstetricOptions.IRREGULAR)}" label="irregular" /></div></td></tr>
		<tr><td class="label"><div class="indent"><chits_tag:springRadiobutton path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.REGULARITY.conceptId}].valueCoded" value="" label="none" /><chits_tag:springFieldError path="fpProgramObs.obstetricHistory.observationMap[${FPObstetricHistoryConcepts.REGULARITY.conceptId}].valueCoded" /></div></td></tr>
		</table>
		</fieldset>
	</td>
</tr>
</table>
</fieldset>

<c:if test="${not empty form.page}">
	<jsp:include page="fragmentWillSeePhysician.jsp" />
	<jsp:include page="fragmentRegistrationFormFooter.jsp" />
</c:if>

<br/>
<div class="full-width" style="text-align: right">
<input type="button" id="cancelButton" value='Cancel' onclick="cancelForm()" />
<c:choose><c:when test="${not empty form.page}">
<input type="submit" id="saveButton" value='Next Page' />
</c:when><c:otherwise>
<input type="submit" id="saveButton" value='Save' />
</c:otherwise></c:choose>
</div>
</form:form>