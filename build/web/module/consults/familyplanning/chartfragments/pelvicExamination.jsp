<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="record" value="${form.fpProgramObs.pelvicExamination.obs}" />
<c:choose><c:when test="${record.obsId gt 0}">
<table class="form full-width chart" id="pelvic-examination-chart">
	<tr><td>Last update: <fmt:formatDate pattern="d MMMM yyyy" value="${record.obsDatetime}" /> by ${record.creator.person.personName}</td></tr>
	<tr>
		<td>
			<ul>
				<c:if test="${chits:observation(record, FPPelvicExaminationConcepts.SCARS).valueCoded eq chits:trueConcept()}"><li>Perineum scars</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.PERINEUM_WARTS).valueCoded eq chits:trueConcept()}"><li>Perineum warts</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.REDDISH).valueCoded eq chits:trueConcept()}"><li>Reddish perineum </li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.PERINEUM_LACERATION).valueCoded eq chits:trueConcept()}"><li>Perineum Laceration</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.VAGINA_CONGESTED).valueCoded eq chits:trueConcept()}"><li>Congested vagina</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.BARTHOLINS_CYST).valueCoded eq chits:trueConcept()}"><li>Bartholin's cyst</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.VAGINA_WARTS).valueCoded eq chits:trueConcept()}"><li>Vaginal Warts</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.SKENES_GLAND).valueCoded eq chits:trueConcept()}"><li>Skene's Gland</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.VAGINAL_DISCHARGE).valueCoded eq chits:trueConcept()}"><li>With vaginal Discharge: ${chits:observation(record, FPPelvicExaminationConcepts.VAGINAL_DISCHARGE).valueText}</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.VAGINAL_RECTOCOELE).valueCoded eq chits:trueConcept()}"><li>Rectocoele present</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.CYTOCOELE).valueCoded eq chits:trueConcept()}"><li>Cytocoele present</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.CERVIX_CONGESTED).valueCoded eq chits:trueConcept()}"><li>Congested cervix</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.ERODED).valueCoded eq chits:trueConcept()}"><li>Eroded cervix</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.CERVICAL_DISCHARGE).valueCoded eq chits:trueConcept()}"><li>With cervical Discharge ${chits:observation(record, FPPelvicExaminationConcepts.CERVICAL_DISCHARGE).valueText}</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.POLYPS).valueCoded eq chits:trueConcept()}"><li>With Cervical polyps/cyst</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.CERVICAL_LACERATION).valueCoded eq chits:trueConcept()}"><li>With cervical Laceration</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.CERVIX_PINKISH).valueCoded eq chits:trueConcept()}"><li>Pinkish cervix</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.CERVIX_BLUISH).valueCoded eq chits:trueConcept()}"><li>Bluish cervix</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.CERVIX_FIRM).valueCoded eq chits:trueConcept()}"><li>Firm uterus consistency</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.CERVIX_SOFT).valueCoded eq chits:trueConcept()}"><li>Soft uterus consistency</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.UTERUS_MID).valueCoded eq chits:trueConcept()}"><li>Mid uterus position</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.UTERUS_ANTEFLEXED).valueCoded eq chits:trueConcept()}"><li>Anteflexed uterus position</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.UTERUS_RETROFLEXED).valueCoded eq chits:trueConcept()}"><li>Retroflexed uterus position</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.NORMAL_UTERUS).valueCoded eq chits:trueConcept()}"><li>Normal uterus size</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.SMALL_UTERUS).valueCoded eq chits:trueConcept()}"><li>Small uterus size</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.LARGE_UTERUS).valueCoded eq chits:trueConcept()}"><li>Large uterus size</li>
				</c:if><c:set var="UD" value="${chits:observation(record, FPPelvicExaminationConcepts.UTERINE_DEPTH)}" /><c:if test="${not empty UD.valueText}"><li>Uterine depth: <chits_tag:obsValue obs="${UD}" /> cm.</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.NORMAL_ADNEXA).valueCoded eq chits:trueConcept()}"><li>Normal adnexa</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.ADNEXA_WITH_MASSES).valueCoded eq chits:trueConcept()}"><li>With adnexa mass</li>
				</c:if><c:if test="${chits:observation(record, FPPelvicExaminationConcepts.ADNEXA_WITH_TENDERNESS).valueCoded eq chits:trueConcept()}"><li>With adnexa tenderness</li>
				</c:if><c:set var="others" value="${chits:observation(record, FPPelvicExaminationConcepts.OTHERS).valueText}" /><c:if test="${not empty others}">
				<li>Others: ${others}</li>
				</c:if>
			</ul>
		</td>
	</tr>
</table>
</c:when><c:otherwise>
no data provided yet
</c:otherwise></c:choose>
