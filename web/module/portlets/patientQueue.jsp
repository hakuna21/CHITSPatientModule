<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="../formStyle.jsp" %>
<%@ include file="../pleaseWait.jsp" %>

<openmrs:htmlInclude file="/dwr/interface/DWRPatientService.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />

<style>
#patient-queue { width: 100%;}
table#patient-queue-entries { width: 100%; clear: both; }
table#patient-queue-entries td { vertical-align: middle; }
#patient-queue .dataTables_wrapper { min-height: 0px; height: auto !important; }
#add-to-patient-form { clear: both; }
td.patient-name { white-space: nowrap; }
</style>

<div id="patient-queue">
<h4>Patient Queue</h4>

<c:set var="enableQueueTime" value="${GlobalProperty[Constants.GP_ENABLE_QUEUE_TIME] eq 'true'}" />
<c:choose><c:when test="${not empty model.patientQueueEntries}">
<table id="patient-queue-entries"><thead>
	<tr>
		<th class="sortable">&nbsp;#&nbsp;</th>
		<th class="sortable">Name</th>
		<th class="sortable">Will see Physician</th>
		<c:if test="${enableQueueTime}">
		<th>Entered Queue</th>
		</c:if>
		<th>Started Consult</th>
		<c:if test="${enableQueueTime}">
		<th>Time in queue</th>
		</c:if>
		<th>Time in consult</th>
		<%--
		<th>&nbsp;</th>
		--%>
	</tr>
</thead><tbody>
<c:forEach items="${model.patientQueueEntries}" var="patientQueue" varStatus="i">
	<tr id="patient-${i.count}">
		<td>${i.count}</td>
		<td class="patient-name"><a href="${pageContext.request.contextPath}/module/chits/consults/viewPatient.form?patientId=${patientQueue.patient.patientId}">${patientQueue.patient.personName} (${patientQueue.patient.gender}, <chits:age birthdate="${patientQueue.patient.birthdate}" />)</a></td>
		<td><c:choose><c:when test="${patientQueue.patient.attributeMap[MiscAttributes.SEE_PHYSICIAN] eq Constants.FLAG_YES}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose></td>
		<c:if test="${enableQueueTime}">
		<td><fmt:formatDate pattern="hh:mm a" value="${patientQueue.enteredQueue}" /></td>
		</c:if>
		<td><c:if test="${patientQueue.consultStart ne null}"><fmt:formatDate pattern="hh:mm a" value="${patientQueue.consultStart}" /></c:if></td>
		<c:if test="${enableQueueTime}">
		<td><chits:elapsed since="${patientQueue.enteredQueue}" upto="${patientQueue.consultStart}" /></td>
		</c:if>
		<td><c:if test="${patientQueue.consultStart ne null}"><chits:elapsed since="${patientQueue.consultStart}" /></c:if></td>
		<%--
		<td><input type="button" onclick="javascript: removeFromQueue(${patientQueue.patient.patientId}, ${i.count})" value="X" /></td>
		--%>
	</tr>
</c:forEach>
</tbody></table>
</c:when><c:otherwise>
<em>[The patient queue is currently empty]</em>
</c:otherwise></c:choose>

<br/><br/>
<form name="add-to-patient-form" id="add-to-patient-form" onsubmit="pleaseWaitDialog()">
<input type="button" value="Add patient to queue" onclick="javascript: patientSearch()">
</form>
</div>

<div style="display:none">
	<div id="confirm-removal" title="Remove patient from queue?">
		<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>The patient will be removed from the queue. Are you sure?</p>
	</div>

	<div id="findPatientDialog">
		<b class="boxHeader">Search Patients</b><div class="box"><div class="searchWidgetContainer" id="findPatients"></div></div>
	</div>
	
	<div id="addToPatientQueue">
		<form action="${pageContext.request.contextPath}/module/chits/consults/addPatientToQueue.form" method="post" onsubmit="pleaseWaitDialog()">
			<input type="hidden" name="patientId" />
		</form>
	</div>
</div>

<script>
$j(document).ready(function() {
	var aoColumns = [];
	$j('#patient-queue-entries thead th').each(function(){
		aoColumns.push($j(this).hasClass('sortable') ? null : {"bSortable":false})
	});
	$j("#patient-queue-entries").dataTable({"bSort": true, "aoColumns":aoColumns});
});

function removeFromQueue(patientId, index) {
	$j( "#confirm-removal" ).dialog({
		resizable: false,
		height:160,
		modal: true,
		title: 'Remove "' + $j("#patient-" + index + " td.patient-name").text() + '"',
		buttons: {
			"Yes, remove from queue": function() {
				document.location.href = "${pageContext.request.contextPath}/module/chits/consults/endPatientConsult.form?patientId=" + patientId
			},
			Cancel: function() {
				$j( this ).dialog( "close" );
			}
		}
	});
}

function patientSearch() {
	var dlg = $j("#findPatientDialog").dialog({width: 700,height:400,title:"Find Patient",modal:true});
	dlg.dialog('open');

	<%-- Prevent double-posting in IE --%>
	var posted = {}
	this.doSelectionHandler = function(index, data) {
		if (data && data.patientId && !posted[data.patientId]) {
			posted[data.patientId] = true
			$j("#addToPatientQueue input[name=patientId]").val(data.patientId)
			$j("#addToPatientQueue form").submit()
		}
	}
	
	//searchHandler for the Search widget
	this.doPatientSearch = function(text, resultHandler, getMatchCount, opts) {
		DWRPatientService.findCountAndPatients(text, opts.start, opts.length, getMatchCount, resultHandler);
	}

	new OpenmrsSearch("findPatients", false, this.doPatientSearch, this.doSelectionHandler, 
		[	{fieldName:"identifier", header:omsgs.identifier},
			{fieldName:"givenName", header:omsgs.givenName},
			{fieldName:"middleName", header:omsgs.middleName},
			{fieldName:"familyName", header:omsgs.familyName},
			{fieldName:"age", header:omsgs.age},
			{fieldName:"gender", header:omsgs.gender},
			{fieldName:"birthdateString", header:omsgs.birthdate}
		], {  searchLabel: 'Patient\'s Name: ', minLength: 2 });
	$j("#findPatientDialog input").focus().keyup();
}
</script>