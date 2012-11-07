<%@ page buffer="128kb"
%><%@ include file="/WEB-INF/template/include.jsp"%><%@ include file="/WEB-INF/view/module/chits/messageAttributes.jsp"
%><%@ taglib prefix="chits_tag" tagdir="/WEB-INF/tags/module/chits" 
%>

<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/css/jquery.dataTables.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/css/TableTools_JUI.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/css/TableTools.css" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/js/TableTools.min.js" />
<openmrs:htmlInclude file="/moduleResources/chits/scripts/jquery.datatable/TableTools/js/ZeroClipboard.js" />
<script>
$j(document).ready(function() {
	$j("#masterListOfFamiliesReport").dataTable({"bSort": false,
		"sDom":'T<"clear">lfrtip',
		"oTableTools":{
			"sSwfPath":'${pageContext.request.contextPath}/moduleResources/chits/scripts/jquery.datatable/TableTools/swf/copy_cvs_xls_pdf.swf',
			"aButtons": [ "print", {"sExtends":"xls","sTitle":"Master list of families"}, "copy" ]
		}
	});

	$j("#generalForm").dialog("option", "width", 1000)
})
</script>

<style>
div.DTTT_container { vertical-align: middle; margin-left: 0.5em; margin-top: -4px;}
div.DTTT_container button { height: 28px; vertical-align: middle; }
div.DTTT_container button span { font-size: 10px; vertical-align: middle; }
#masterListOfFamiliesReport { border: 1px solid #aaa; }
</style>

<c:if test="${msg != null}">
	<div class="openmrs_msg">
		<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
	</div>
</c:if>
<c:if test="${err != null}">
	<span class="validationError"><div class="openmrs_error">
		<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
	</div></span>
</c:if>

<spring:hasBindErrors name="form">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>

<strong>MASTER LIST OF FAMILIES</strong><br/>
<br/>

<p><strong style="color: red">NOTE: Use 'CTRL+' to zoom.</strong></p>

<div id="reports">
	<div id="families">
		<table id="masterListOfFamiliesReport" class="form">
		<thead>
			<tr>
				<th>FAMILY NAME</th>
				<th>FAMILY ID</th>
				<th>ADDRESS</th>
				<th>BARANGAY</th>
				<th style="white-space:nowrap">HEAD OF THE FAMILY<br/>/ SEX / AGE</th>
				<th>PHILHEALTH ID</th>
				<th style="white-space:nowrap">MEMBERS / SEX / AGE</th>
				<th>DATE REGISTERED</th>
				<th>LAST UPDATED</th>
			</tr>
		</thead>
		<tbody><c:forEach var="folder" items="${folders}">
			<tr id="folder_${folder.familyFolderId}">
				<td>${folder.name}</td>
				<td>${folder.code}</td>
				<td>${chits:coalesce(folder.address, '-')}</td>
				<td>${barangays[folder.barangayCode].name}, ${barangays[folder.barangayCode].municipality.name}</td>
				<td><c:choose><c:when test="${folder.headOfTheFamily ne null}">${folder.headOfTheFamily.personName} / ${folder.headOfTheFamily.gender} / <chits:age birthdate="${folder.headOfTheFamily.birthdate}" /></c:when><c:otherwise>-</c:otherwise></c:choose></td>
				<td>${chits:coalesce(folder.headOfTheFamily.attributeMap[PhilhealthConcepts.CHITS_PHILHEALTH], '-')}
				<td><c:forEach var="member" items="${folder.patients}" varStatus="i">${i.count}) ${member.personName} / ${member.gender} / <chits:age birthdate="${member.birthdate}" /><br/> </c:forEach></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${folder.dateCreated}" /></td>
				<td><c:choose><c:when test="${folder.dateChanged ne null}"><fmt:formatDate pattern="MM/dd/yyyy" value="${folder.dateChanged}" /></c:when><c:otherwise>-</c:otherwise></c:choose></td>
			</tr>
		</c:forEach></tbody>
		</table>
	</div>
	
	<br/><br/><br/>
</div>

<form:form modelAttribute="form" method="post" action="masterListOfFamiliesReport.form" onsubmit="loadAjaxForm('masterListOfFamiliesReport.form', 'Master List of Families Report', 1000); return false;">
	<input type="submit" value="Reload" />
</form:form>
