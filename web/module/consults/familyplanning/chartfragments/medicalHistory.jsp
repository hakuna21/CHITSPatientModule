<%@ page buffer="128kb"
%><%@ page import="java.util.*"
%><%@ page import="org.openmrs.module.chits.*"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ include file="/WEB-INF/template/include.jsp" %>

<c:set var="record" value="${form.fpProgramObs.medicalHistoryInformation.obs}" />
<c:choose><c:when test="${record.obsId gt 0}">
<table class="form full-width chart" id="medical-history-chart">
	<tr><td>Last update: <fmt:formatDate pattern="d MMMM yyyy" value="${record.obsDatetime}" /> by ${record.creator.person.personName}</td></tr>
	<tr>
		<td>
			<ul>
				<c:if test="${chits:observation(record, FPMedicalHistoryConcepts.SEIZURE).valueCoded eq chits:trueConcept()}"><li>Epilepsy/Convlusion/Seizure</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.HEADACHE).valueCoded eq chits:trueConcept()}"><li>Severe headache/dizziness</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.BLURRING).valueCoded eq chits:trueConcept()}"><li>Visual disturbance/blurring of vision</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.YELLOWISH_CONJUNCTIVE).valueCoded eq chits:trueConcept()}"><li>Yellowish conjunctive</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.ENLARGED_THYROID).valueCoded eq chits:trueConcept()}"><li>Enlarged thyroid</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.CHEST_PAIN).valueCoded eq chits:trueConcept()}"><li>Severe chest pain</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.FATIGABILITY).valueCoded eq chits:trueConcept()}"><li>Shortness of breath and easy fatiguability</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.BREAST_MASS).valueCoded eq chits:trueConcept()}"><li>Breast/axillary masses</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.NIPPLE_BLOOD_DISCHARGE).valueCoded eq chits:trueConcept()}"><li>Nipple discharges (blood)</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.NIPPLE_PUS_DISCHARGE).valueCoded eq chits:trueConcept()}"><li>Nipple discharges (pus)</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.SBP_OVER140).valueCoded eq chits:trueConcept()}"><li>Systolic of 140 &amp; above</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.DBP_OVER90).valueCoded eq chits:trueConcept()}"><li>Diastolic of 90 &amp; above</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.FAMILY_HISTORY_OF_STROKES_ETC).valueCoded eq chits:trueConcept()}"><li>Family history of CVA (strokes), hypertension, asthma, rheumatic heart disease</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.ABDOMINAL_MASS).valueCoded eq chits:trueConcept()}"><li>Mass in the abdomen</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.HISTORY_OF_GALLBLADDER).valueCoded eq chits:trueConcept()}"><li>History of gallbladder disease</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.HISTORY_OF_LIVER_DISEASE).valueCoded eq chits:trueConcept()}"><li>History of liver disease</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.MASS_IN_UTERUS).valueCoded eq chits:trueConcept()}"><li>Mass in the uterus</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.VAGINAL_DISCHARGE).valueCoded eq chits:trueConcept()}"><li>Vaginal discharge</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.INTERMENSTRUAL_BLEEDING).valueCoded eq chits:trueConcept()}"><li>Intermenstrual bleeding</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.POSTCOITAL_BLEEDING).valueCoded eq chits:trueConcept()}"><li>Postcoital bleeding</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.SEVERE_VARICOSITIES).valueCoded eq chits:trueConcept()}"><li>Severe varicosities</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.EDEMA).valueCoded eq chits:trueConcept()}"><li>Swelling or severe pain in the legs not related to injuries</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.YELLOWISH_SKIN).valueCoded eq chits:trueConcept()}"><li>Yellowish skin</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.BLEEDING_TENDENCIES).valueCoded eq chits:trueConcept()}"><li>Bleeding tendencies (nose, gums, etc.)</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.ANEMIA).valueCoded eq chits:trueConcept()}"><li>Anemia</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.DIABETES).valueCoded eq chits:trueConcept()}"><li>Diabetes</li>
				</c:if><c:if test="${chits:observation(record, FPMedicalHistoryConcepts.HYDATIDIFORM_MOLE).valueCoded eq chits:trueConcept()}"><li>Hydatidiform mole (w/in the last 12 mos.)</li>
				</c:if><c:choose><c:when test="${chits:observation(record, FPMedicalHistoryConcepts.SMOKING_HISTORY).valueCoded eq chits:trueConcept()}">
				<li>Smoker: Yes
					<div class="indent">Sticks per day: <chits_tag:obsValue obs="${chits:observation(record, FPMedicalHistoryConcepts.SMOKING_STICKS_PER_DAY)}" /></div>
					<div class="indent">Years smoking: <chits_tag:obsValue obs="${chits:observation(record, FPMedicalHistoryConcepts.SMOKING_YEARS)}" /></div>
				</li>
				</c:when><c:otherwise>
				<li>Smoker: No</li>
				</c:otherwise></c:choose>
				<c:choose><c:when test="${chits:observation(record, FPMedicalHistoryConcepts.ALLERGIES).valueCoded eq chits:trueConcept()}">
				<li>Allergies: ${chits:observation(record, FPMedicalHistoryConcepts.ALLERGIES).valueText}</li>
				</c:when><c:otherwise>
				<li>Allergies: none</li>
				</c:otherwise></c:choose>
				<c:choose><c:when test="${chits:observation(record, FPMedicalHistoryConcepts.DRUG_INTAKE).valueCoded eq chits:trueConcept()}">
				<li>Drug intake: ${chits:observation(record, FPMedicalHistoryConcepts.DRUG_INTAKE).valueText}</li>
				</c:when><c:otherwise>
				<li>Drug intake: none</li>
				</c:otherwise></c:choose>

			</ul>
		</td>
	</tr>
</table>
</c:when><c:otherwise>
no data provided yet
</c:otherwise></c:choose>
