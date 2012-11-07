function updateMCSection(patientId, onUpdate) {
	var handler
	$j.ajax({url: 'viewMaternalCareProgram.form?patientId=' + patientId + '&section=program-details', cache: false, success: handler = function (data) {
		$j('#program-details').empty().html(data)
		$j('#program-details input[type=button], #program-details input[type=submit]').button()
		if (onUpdate) {
			onUpdate()
		}
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function updateVisitsSection(patientId, onUpdate) {
	var handler
	$j.ajax({url: 'viewPatient.form?patientId=' + patientId + '&section=visit-details', cache: false, success: handler = function (data) {
		$j('#visit-details').empty().html(data)
		if (onUpdate) {
			onUpdate()
		}
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})

	updateMCSection(patientId)
}

function loadAjaxForm(formAction, titleText, patientId, width, divId) {
	if (width == undefined) { width = 580}
	if (divId == undefined) { divId = "generalForm" }
	$j("#" + divId).loadAndCenter("<h4>Loading, Please Wait...</h4>", {width:width,title:titleText})
	var handler
	$j.ajax({url: formAction, cache: false, success: handler = function (data) {
		$j('#' + divId).loadAndCenter(data)
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function buildPostData(form) {
	var postData = {}
	$j(form).find("input[type=checkbox]:checked, input[type=radio]:checked, input[type=text], input[type=hidden], select, textarea").each(function() {
		if (!$j(this).is(":disabled")) {
			postData[$j(this).attr('name')] = $j(this).val()
		}
	})
	
	return postData
}

function submitAjaxForm(form, patientId, onUpdate, divId) {
	if (divId == undefined) { divId = "generalForm" }
	var postData = buildPostData(form)
	pleaseWaitDialog()
	var handler
	$j.ajax({url: form.action, type: 'POST', data: postData, success: handler = function (data) {
		closePleaseWaitDialog()
		var div = $j('#' + divId).loadAndCenter(data)
		highlightErrors()
		
		if (onUpdate == undefined) {
			onUpdate = updateMCSection
		}

		if (div.find("div.openmrs_msg").size() > 0 && div.find("div.openmrs_error").size() == 0) {
			onUpdate(patientId, function() {
				div.dialog("close")
			})		
		}
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function updateObstericHistoryDetails(patientId, onUpdate) {
	$j("div.obstetric-history-details").html("<h3><center>Reloading, Please Wait...</center></h3>")
	var handler
	$j.ajax({url: 'ajaxObstetricHistoryDetails.form?patientId=' + patientId, cache: false, success: handler = function (data) {
		$j("div.obstetric-history-details").html(data)
		if (onUpdate) {
			onUpdate()
		}
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function addNewOBHistoryDetail(patientId) {
	loadAjaxForm("addObstetricHistoryDetail.form?patientId=" + patientId, "OB HISTORY DETAILS FORM", patientId, 900);
}

function editOBHistoryDetail(obstetricHistoryDetailObsId, patientId) {
	loadAjaxForm("editObstetricHistoryDetail.form?patientId=" + patientId + "&obstetricHistoryDetailObsId=" + obstetricHistoryDetailObsId, "EDIT OB HISTORY DETAILS", patientId, 900);
}