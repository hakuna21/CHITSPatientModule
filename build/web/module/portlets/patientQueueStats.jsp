<%@ include file="/WEB-INF/template/include.jsp"
%><li><a href="${pageContext.request.contextPath}/module/chits/patients/patientQueue.htm">Patient<br/>Queue:</a> ${model.queueSize}</li>
<li>Average Consult time: <c:choose><c:when test="${model.aveConsultTime ne null}"><fmt:formatNumber pattern="0.#" value="${model.aveConsultTime / 60}" /> min</c:when><c:otherwise>[No Data]</c:otherwise></c:choose>
</li>