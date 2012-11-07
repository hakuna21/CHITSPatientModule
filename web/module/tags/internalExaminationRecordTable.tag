<%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%><%@ attribute name="mcProgramObs" required="true" type="org.openmrs.module.chits.mcprogram.MaternalCareProgramObs"
%><%@ attribute name="internalExaminationRecord" required="true" type="org.openmrs.module.chits.mcprogram.InternalExaminationRecord"
%><table class="borderless full-width registration" id="IEVisit">
<tbody>
<tr><td><c:choose><c:when test="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.EXTERNAL_GENITALIA).valueCoded.conceptId eq MCIEOptions.NORMAL.conceptId}">Normal</c:when><c:otherwise>${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.EXTERNAL_GENITALIA_TEXT).valueText}</c:otherwise></c:choose> external genitalia</td></tr>
<tr><td><chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.VAGINA)}" /> vagina</td></tr>
<tr><td>Cervix <chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.CERVIX_STATE)}" />, <chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.CERVIX_CONSISTENCY)}" /></td></tr>
<c:if test="${not empty chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.ENLARGED_TO).valueNumeric}">
<tr><td>Uterus enlarged to <chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.ENLARGED_TO)}" /> weeks AOG</td></tr>
</c:if>
<tr><td><chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.TENDERNESS)}" /> adnexal mass/tenderness</td></tr>
<tr><td><chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.PELVIMETRY)}" /> pelvimetry</td></tr>
<tr><td><chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.MEMBRANES)}" /></td></tr>
<c:if test="${not empty chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.FETAL_PRESENTATION).valueCoded}">
<tr><td>Presenting part: <chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.FETAL_PRESENTATION)}" />, <chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.FETAL_STATION)}" /></td></tr>
</c:if>
<tr><td><chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.BLOODY_SHOW)}" /> bloody show</td></tr>
<tr><td>Other findings and remarks: <chits_tag:obsValue obs="${chits:observation(internalExaminationRecord.obs, MCIERecordConcepts.REMARKS)}" noData="<em>none</em>" /></td></tr>
</tbody>
</table>