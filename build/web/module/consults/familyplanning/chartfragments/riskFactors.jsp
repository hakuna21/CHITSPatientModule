<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="record" value="${form.fpProgramObs.riskFactors.obs}" />
<c:choose><c:when test="${record.obsId gt 0}">
<table class="form full-width chart" id="risk-factors-chart">
	<tr><td>Last update: <fmt:formatDate pattern="d MMMM yyyy" value="${record.obsDatetime}" /> by ${record.creator.person.personName}</td></tr>
	<tr>
		<td>
			<ul>
				<c:if test="${chits:observation(record, FPRiskFactorsConcepts.MULTIPLE_PARTNERS).valueCoded eq chits:trueConcept()}"><li>With history of multiple partners</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.VAGINAL_DISCHARGE).valueCoded eq chits:trueConcept()}"><li>Unusual discharge from vagina</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.VAGINAL_ITCHING).valueCoded eq chits:trueConcept()}"><li>Itching or soreness in or around vagina</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.BURNING_SENSATION).valueCoded eq chits:trueConcept()}"><li>Pain or burning sensation</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.GENITAL_SORES).valueCoded eq chits:trueConcept()}"><li>Open sores anywhere in genital area</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.PENILE_DISCHARGE).valueCoded eq chits:trueConcept()}"><li>Pus coming from penis</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.GENITAL_SWELLING).valueCoded eq chits:trueConcept()}"><li>Swollen testicles or penis</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.HISTORY_OF_STI_TREATMENT).valueCoded eq chits:trueConcept()}"><li>Treated for STI in the past</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.DOMESTIC_VIOLENCE).valueCoded eq chits:trueConcept()}"><li>History of domestic violence</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.UNPLEASANT_RELATINOSHIP).valueCoded eq chits:trueConcept()}"><li>Unpleasant relationship with partner</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.PARTNER_DISAPPROVAL_VISIT).valueCoded eq chits:trueConcept()}"><li>Partner does not approve of the visit to FP clinic</li>
				</c:if><c:if test="${chits:observation(record, FPRiskFactorsConcepts.PARTNER_DISAPPROVAL_FP).valueCoded eq chits:trueConcept()}"><li>Partner disagrees to use FP</li>
				</c:if><c:choose><c:when test="${not empty chits:observation(record, FPRiskFactorsConcepts.DATE_REFERRED).valueDatetime}">
				<li>Date Referred: <fmt:formatDate pattern="MM/dd/yyyy" value="${chits:observation(record, FPRiskFactorsConcepts.DATE_REFERRED).valueDatetime}" />
					<c:if test="${chits:observation(record, FPRiskFactorsConcepts.DSWD).valueCoded eq chits:trueConcept()}"><div class="indent">DSWD</div></c:if>
					<c:if test="${chits:observation(record, FPRiskFactorsConcepts.WCPU).valueCoded eq chits:trueConcept()}"><div class="indent">WCPU</div></c:if>
					<c:if test="${chits:observation(record, FPRiskFactorsConcepts.NGO).valueCoded eq chits:trueConcept()}"><div class="indent">NGO</div></c:if>
					<c:if test="${chits:observation(record, FPRiskFactorsConcepts.SOCIAL_HYGIENE_CLINIC).valueCoded eq chits:trueConcept()}"><div class="indent">Social Hygiene Clinic</div></c:if>
					<c:if test="${chits:observation(record, FPRiskFactorsConcepts.OTHERS).valueCoded eq chits:trueConcept()}">
					<div class="indent">Others: ${chits:observation(record, FPRiskFactorsConcepts.OTHERS).valueText}</div>
					</c:if>
				</li>
				</c:when><c:otherwise>
				<li>Referred: No</li>
				</c:otherwise></c:choose>
			</ul>
		</td>
	</tr>
</table>
</c:when><c:otherwise>
no data provided yet
</c:otherwise></c:choose>
