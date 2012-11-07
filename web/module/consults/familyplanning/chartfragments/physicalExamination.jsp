<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="record" value="${form.fpProgramObs.physicalExamination.obs}" />
<c:choose><c:when test="${record.obsId gt 0}">
<table class="form full-width chart" id="physical-examination-chart">
	<tr><td>Last update: <fmt:formatDate pattern="d MMMM yyyy" value="${record.obsDatetime}" /> by ${record.creator.person.personName}</td></tr>
	<tr>
		<td>
			<ul>
				<c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.PALE_CONJUNCTIVA).valueCoded eq chits:trueConcept()}"><li>Pale conjunctiva</li>
				</c:if><c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.YELOWISH_CONJUNCTIVA).valueCoded eq chits:trueConcept()}"><li>Yellowish conjunctiva</li>
				</c:if><c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.ENLARGED_THYROID).valueCoded eq chits:trueConcept()}"><li>Enlarged Thyroid</li>
				</c:if><c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.ENLARGED_LYMPH_NODES).valueCoded eq chits:trueConcept()}"><li>Enlarged Lymph Nodes</li>
				</c:if><c:set var="UOL" value="${chits:observation(record, FPPhysicalExaminationConcepts.MASS_UL_OLB).valueCoded eq chits:trueConcept()}"
				/><c:set      var="UIL" value="${chits:observation(record, FPPhysicalExaminationConcepts.MASS_UL_ILB).valueCoded eq chits:trueConcept()}"
				/><c:set      var="LOL" value="${chits:observation(record, FPPhysicalExaminationConcepts.MASS_LL_OLB).valueCoded eq chits:trueConcept()}"
				/><c:set      var="LIL" value="${chits:observation(record, FPPhysicalExaminationConcepts.MASS_LL_ILB).valueCoded eq chits:trueConcept()}"
				/><c:set var="NDL" value="${chits:observation(record, FPPhysicalExaminationConcepts.NIPPLE_DISCHARGE_LB).valueCoded eq chits:trueConcept()}"
				/><c:set var="DL" value="${chits:observation(record, FPPhysicalExaminationConcepts.DIMPLING_LEFT).valueCoded eq chits:trueConcept()}"
				/><c:set var="EALLN" value="${chits:observation(record, FPPhysicalExaminationConcepts.ENLARGED_AXILLARY_LEFT_LYMPH_NODES).valueCoded eq chits:trueConcept()}"
				/><c:if test="${UOL or UIL or LOL or LIL or NDL or DL or EALLN}">
				<li><fieldset><legend>Left Breast</legend><c:set var="descrLB" value="${chits:observation(record, FPPhysicalExaminationConcepts.MASS_ON_LEFT_DESCR).valueText}" />
					<ul>
						<c:if test="${UOL}"><li>Mass(es) in Outer Upper Lobe</li></c:if>
						<c:if test="${UIL}"><li>Mass(es) in Inner Upper Lobe</li></c:if>
						<c:if test="${LIL}"><li>Mass(es) in Inner Lower Lobe</li></c:if>
						<c:if test="${LOL}"><li>Mass(es) in Outer Lower Lobe</li></c:if>
						<c:if test="${NDL}"><li>Nipple discharge</li></c:if>
						<c:if test="${DL}"><li>Skin-orange-peel or dimpling</li></c:if>
						<c:if test="${EALLN}"><li>Enlarged Axillary Lymph Nodes</li></c:if>
					</ul>
					<c:if test="${not empty descrLB}">Description of mass: ${descrLB}</c:if>
					</fieldset>
				</li>
				</c:if><c:set var="UIR" value="${chits:observation(record, FPPhysicalExaminationConcepts.MASS_UL_IRB).valueCoded eq chits:trueConcept()}"
				/><c:set      var="UOR" value="${chits:observation(record, FPPhysicalExaminationConcepts.MASS_UL_ORB).valueCoded eq chits:trueConcept()}"
				/><c:set      var="LIR" value="${chits:observation(record, FPPhysicalExaminationConcepts.MASS_LL_IRB).valueCoded eq chits:trueConcept()}"
				/><c:set      var="LOR" value="${chits:observation(record, FPPhysicalExaminationConcepts.MASS_LL_ORB).valueCoded eq chits:trueConcept()}"
				/><c:set var="NDR" value="${chits:observation(record, FPPhysicalExaminationConcepts.NIPPLE_DISCHARGE_RB).valueCoded eq chits:trueConcept()}"
				/><c:set var="DR" value="${chits:observation(record, FPPhysicalExaminationConcepts.DIMPLING_RIGHT).valueCoded eq chits:trueConcept()}"
				/><c:set var="EARLN" value="${chits:observation(record, FPPhysicalExaminationConcepts.ENLARGED_AXILLARY_RIGHT_LYMPH_NODES).valueCoded eq chits:trueConcept()}"
				/><c:if test="${UOR or UIR or LOR or LIR or NDR or DR or EARLN}">
				<li><fieldset><legend>Right Breast</legend><c:set var="descrRB" value="${chits:observation(record, FPPhysicalExaminationConcepts.MASS_ON_RIGHT_DESCR).valueText}" />
					<ul>
						<c:if test="${UOR}"><li>Mass(es) in Outer Upper Lobe</li></c:if>
						<c:if test="${UIR}"><li>Mass(es) in Inner Upper Lobe</li></c:if>
						<c:if test="${LIR}"><li>Mass(es) in Inner Lower Lobe</li></c:if>
						<c:if test="${LOR}"><li>Mass(es) in Outer Lower Lobe</li></c:if>
						<c:if test="${NDR}"><li>Nipple discharge</li></c:if>
						<c:if test="${DR}"><li>Skin-orange-peel or dimpling</li></c:if>
						<c:if test="${EARLN}"><li>Enlarged Axillary Lymph Nodes</li></c:if>
					</ul>
					<c:if test="${not empty descrRB}">Description of mass: ${descrRB}</c:if>
					</fieldset>
				</li>
				</c:if><c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.ABNORMAL_HEART_SOUNDS).valueCoded eq chits:trueConcept()}"><li>Abnormal Heart Sounds / Cardiac Rate</li>
				</c:if><c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.ABNORMAL_BREATH_SOUNDS).valueCoded eq chits:trueConcept()}"><li>Abnormal Breath Sounds / Respiratory Rate</li>
				</c:if><c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.ENLARGED_LIVER).valueCoded eq chits:trueConcept()}"><li>Enlarged Liver</li>
				</c:if><c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.ABDOMINAL_MASS).valueCoded eq chits:trueConcept()}"><li>Abdominal Mass</li>
				</c:if><c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.ABDOMINAL_TENDERNESS).valueCoded eq chits:trueConcept()}"><li>Abdominal Tenderness</li>
				</c:if><c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.EDEMA).valueCoded eq chits:trueConcept()}"><li>Edema of Extremities</li>
				</c:if><c:if test="${chits:observation(record, FPPhysicalExaminationConcepts.VARICOSITIES).valueCoded eq chits:trueConcept()}"><li>Varicosities</li>
				</c:if><c:set var="others" value="${chits:observation(record, FPPhysicalExaminationConcepts.OTHERS).valueText}" /><c:if test="${not empty others}">
				<li>Others: ${others}</li>
				</c:if>
			</ul>
		</td>
	</tr>
</table>
</c:when><c:otherwise>
no data provided yet
</c:otherwise></c:choose>
