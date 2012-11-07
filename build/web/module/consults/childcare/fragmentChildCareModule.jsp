<%@ include file="/WEB-INF/template/include.jsp"%>

<h3>Early Childhood Care and Development (ECCD) Program Regular Access Screen</h3>

<p>
The Child Care Program is intended for children below 6 years old (71
months or less). Once enrolled, the child may not be un-enrolled from
the program until s/he reaches 6 years old.
</p>

<p>
This module complies with the recommended health care for children under
the Early Childhood Care and Development (ECCD) program of the World
Health Organization (WHO).
</p>

<c:choose><c:when test="${form.programConcluded}">
	<h4 class="alert">This patient's Child Care program has already been concluded</h4>
</c:when><c:otherwise>
	<c:if test="${Constants.FLAG_YES eq form.patient.attributeMap[MiscAttributes.SEE_PHYSICIAN].value}">
		<h4 class="alert">THIS PATIENT NEEDS TO SEE THE PHYSICIAN</h4>
	</c:if>
</c:otherwise></c:choose>

<h4>Weight for Age</h4>
[Chart is under construction]

<h4>Length for Age</h4>
[Chart is under construction]

<h4>Weight for Length</h4>
[Chart is under construction]
