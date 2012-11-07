	<tr>
		<td>Birth Weight of Child (kg.): </td>
		<td>
			<spring:bind path="observationMap[${ChildCareConcepts.BIRTH_WEIGHT.conceptId}].valueText">
			<input name="${status.expression}" maxlength="6" size="4" value="${status.value}"/>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
		<td class="right-label">Birth Length of Child (cm.): </td>
		<td>
			<spring:bind path="observationMap[${ChildCareConcepts.BIRTH_LENGTH.conceptId}].valueText">
			<input name="${status.expression}" maxlength="6" size="4" value="${status.value}"/>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td>Location of delivery: </td>
		<td colspan="3">
			<spring:bind path="observationMap[${ChildCareConcepts.DELIVERY_LOCATION.conceptId}].valueCoded">
			<form:select path="${status.expression}">
				<option value="">Select Location</option>
				<c:forEach var="location" items="${chits:answers(ChildCareConcepts.DELIVERY_LOCATION)}">
				<form:option value="${location.conceptId}" label="${location.name.name}" />
				</c:forEach>
			</form:select>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td>Method of delivery: </td>
		<td colspan="3">
			<spring:bind path="observationMap[${ChildCareConcepts.METHOD_OF_DELIVERY.conceptId}].valueCoded">
			<form:select path="${status.expression}">
				<option value="">Select Method</option>
				<c:forEach var="methodOfDelivery" items="${chits:answers(ChildCareConcepts.METHOD_OF_DELIVERY)}">
				<form:option value="${methodOfDelivery.conceptId}" label="${methodOfDelivery.name.name}" />
				</c:forEach>
			</form:select>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td>Gestational Age at Birth (weeks): </td>
		<td>
			<spring:bind path="observationMap[${ChildCareConcepts.GESTATIONAL_AGE.conceptId}].valueText">
			<input name="${status.expression}" maxlength="4" size="4" value="${status.value}"/>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
		<td class="right-label">Birth Order: </td>
		<td>
			<spring:bind path="observationMap[${ChildCareConcepts.BIRTH_ORDER.conceptId}].valueText">
			<input name="${status.expression}" maxlength="4" size="4" value="${status.value}"/>
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td colspan="1">Date of Birth Registration: </td>
		<td colspan="3">
			<spring:bind path="observationMap[${ChildCareConcepts.DOB_REGISTRATION.conceptId}].valueText">
			<input name="${status.expression}" onFocus="showCalendar(this)" value="${status.value}" />
			<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</spring:bind>
		</td>
	</tr>