<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Edit Users" otherwise="/login.htm" redirect="/admin/users/users.list" />

<spring:message var="pageTitle" code="chits.admin.manage.authorized.barangays" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:htmlInclude file="/dwr/interface/DWRBarangayService.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/LocationFinder.js" />

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />
<%@ include file="../../formStyle.jsp" %>
<style>
#manageAuthorizedBarangaysForm input[type=checkbox] { vertical-align: middle; }
#manageAuthorizedBarangaysForm { cursor:pointer; }
</style>
<%@ include file="../../pleaseWait.jsp" %>

<script>
var city = {}
var locationFinder = new LocationFinder();

$j(document).ready(function() {
	$j("input[name='barangayCode']").focus(function() {
		locationFinder.findBarangay(function(barangay) {
			$j("input[name='barangayCode']").val(barangay.barangayCode);
			$j("span#barangayName").html("(" + barangay.name + ")");
			$j("input[name='cityCode']").val(barangay.municipality.municipalityCode);
			$j("span#cityName").html("(" + barangay.municipality.name + ")");
			setTimeout( function() { $j("textarea[name='notes']").focus(); }, 0 );
		}, city)
	})

	$j("input[name='cityCode']").focus(function() {
		locationFinder.findMunicipality(function(municipality) {
			city = municipality
			$j("input[name='cityCode']").val(municipality.municipalityCode);
			$j("span#cityName").html("(" + municipality.name + ")");
			
			if (municipality.municipalityCode / 1000 != Math.floor($j("input[name='barangayCode']").val() / 1000)) {
				// barangay code doesn't match selected city
				$j("input[name='barangayCode']").val("");
				$j("span#barangayName").html("");
			}
			
			// setTimeout( function() { $j("input[name='barangayCode']").focus(); }, 0 );
		})
	})
})

function removeSelected() {
	$j("form#manageAuthorizedBarangaysForm input[type=checkbox]:checked").each(function() {
		var brgyCode = $j(this).val()
		$j("#authbrgy_" + brgyCode).remove()
	})
	
	if ($j("form#manageAuthorizedBarangaysForm input[type=checkbox]").size() == 0) {
		$j("#removeSelectedButton").hide()
	}
}

function addBarangay() {
	locationFinder.findBarangay(function(barangay) {
		var barangayCode = barangay.barangayCode
		var brgyName = barangay.name
		var cityName = barangay.municipality.name
		city = barangay.municipality

		if ($j("#authbrgy_" + barangayCode).size() > 0) {
			return;
		}

		var brgyDiv = $j('<div id="authbrgy_' + barangayCode + '"> '
			+ '<input type="checkbox" value="' + barangayCode + '" id="cb_' + barangayCode + '" />'
			+ '<input type="hidden" name="userBarangayCodes" value="' + barangayCode + '" />'
			+ '<label for="cb_' + barangayCode + '">' + barangayCode
			+ ' (' + brgyName + ', ' + cityName + ')'
			+ '</label></div>')
		$j("#authorized_barangays").append(brgyDiv)
		
		$j("#removeSelectedButton").show()		
	}, city)
}
</script>
<h2><spring:message code="chits.admin.manage.authorized.barangays"/></h2>	

<spring:hasBindErrors name="form">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<br />

<form:form modelAttribute="form" method="post" id="manageAuthorizedBarangaysForm" action="manageAuthorizedBarangays.form" cssClass="form" onsubmit="pleaseWaitDialog()">
	<table style="width: 700px" class="form">
		<spring:bind path="user">
		<tr>
			<th style="width: 100px">User</th>
			<th>
				<form:hidden path="${status.expression}" />
				${form.user.username} (${form.user.person.personName})
			</th>
		</tr>
		</spring:bind>
		<spring:bind path="userBarangayCodes">
		<tr>
			<td>Authorized<br/>Barangays</td>
			<td valign="middle">
				<div id="authorized_barangays">
				<c:forEach var="barangayCode" items="${status.value}">
					<div id="authbrgy_${barangayCode}">
						<input type="checkbox" value="${barangayCode}" id="cb_${barangayCode}" />
						<input type="hidden" name="${status.expression}" value="${barangayCode}" />
						
						<label for="cb_${barangayCode}">
							${barangayCode}
							<c:if test="${not empty barangays[barangayCode]}">
							(${barangays[barangayCode].name}, ${barangays[barangayCode].municipality.name})
							</c:if>
						</label>
					</div>
				</c:forEach>
				</div>
				<c:if test="${status.errorMessage != ''}"><br/><div class="error">${status.errorMessage}</div></c:if>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="button" id="addBarangayButton" value='Add Barangay' onclick="addBarangay()" />
				<input type="button" id="removeSelectedButton" value='Remove Selected' onclick="removeSelected()" <c:if test="${empty form.userBarangayCodes}">style="display:none"</c:if> />
			</td>
		</tr>
		</spring:bind>
	</table>
	<br/>
	<input type="submit" id="saveButton" value='<spring:message code="chits.admin.user.authorized.barangays.update"/>' />
	<input type="button" id="cancelButton" onClick="document.location.href='${pageContext.request.contextPath}/admin/users/user.form?userId=${form.user.userId}'" value='<spring:message code="chits.admin.cancel"/>' />
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
