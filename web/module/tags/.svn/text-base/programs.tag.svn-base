<%@ tag import="org.openmrs.Patient"
%><%@ tag import="org.openmrs.module.chits.PatientConsultForm"
%><%@ tag import="org.openmrs.module.chits.fpprogram.FamilyPlanningUtil"
%><%@ tag import="org.openmrs.module.chits.mcprogram.MaternalCareUtil"
%><%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><%@ taglib prefix="openmrs" uri="/WEB-INF/taglibs/openmrs.tld" 
%><%@ taglib prefix="chits" uri="/WEB-INF/taglibs/chits-rt.tld" 
%><%@ attribute name="patient" required="true" type="org.openmrs.Patient" %>

<c:if test="${not programs_tag_processed}">
<openmrs:hasPrivilege privilege="View Programs">
<%-- Determine which programs the patient is enrolled in; store results in the request scope --%>
<c:if var="concludedInChildcare" test="${chits:getPatientState(patient, ProgramConcepts.CHILDCARE, ChildCareProgramStates.CLOSED) ne null}" scope="request" />
<c:if var="enrolledInChildcare" test="${not concludedInChildcare and chits:isInProgram(patient, ProgramConcepts.CHILDCARE)}" scope="request" />
<c:if var="canEnrollInChildcare" test="${not (enrolledInChildcare or concludedInChildcare) and patient.age lt ChildCareConstants.CHILDCARE_MAX_AGE}" scope="request" />

<c:if var="concludedInFamilyPlanning" test="<%= FamilyPlanningUtil.isProgramConcludedFor(patient) %>" scope="request" />
<c:if var="enrolledInFamilyPlanning" test="${chits:isInProgram(patient, ProgramConcepts.FAMILYPLANNING)}" scope="request" />
<% request.setAttribute("canEnrollInFamilyPlanning", FamilyPlanningUtil.canEnrollInFamilyPlanning(patient)); %>

<c:if var="concludedInMaternalCare" test="${not empty chits:filterByCodedValue(chits:observations(form.patient, MCPatientConsultStatus.STATUS), MaternalCareProgramStates.ENDED)}" scope="request" />
<c:if var="enrolledInMaternalCare" test="${chits:isInProgram(patient, ProgramConcepts.MATERNALCARE)}" scope="request" />
<% request.setAttribute("canEnrollInMaternalCare", MaternalCareUtil.canEnrollInMaternalCare(patient)); %>
<%-- Add other programs above as needed --%>

<%-- For future programs, this logic should be changed to account for other programs that the patient may enroll in --%>
<c:if var="enrolledInAny" test="${false or enrolledInChildcare or enrolledInFamilyPlanning or enrolledInMaternalCare}" scope="request" />
<c:if var="enrollableInAny" test="${false or canEnrollInChildcare or canEnrollInFamilyPlanning or canEnrollInMaternalCare}" scope="request" />
<c:if var="concludedInAny" test="${false or concludedInChildcare or concludedInFamilyPlanning or concludedInMaternalCare}" scope="request" />
</openmrs:hasPrivilege>
<c:set var="programs_tag_processed" value="${true}" scope="request" />
</c:if>