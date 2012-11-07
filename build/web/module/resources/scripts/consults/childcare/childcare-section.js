$j(document).ready(function() {
	$j('input[type=button], input[type=submit]').button()
})

function updateECCDSection(patientId, onUpdate) {
	var handler
	$j.ajax({url: 'viewChildCareProgram.form?patientId=' + patientId + '&section=program-details', cache: false, success: handler = function (data) {
		$j('#program-details').empty().html(data)
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
	
	updateECCDSection(patientId)
}

function loadAjaxForm(formAction, titleText, patientId, width) {
	if (width == undefined) { width = 580}
	$j("#generalForm").loadAndCenter("<h4>Loading, Please Wait...</h4>", {width:width,title:titleText})
	var handler
	$j.ajax({url: formAction + '?patientId=' + patientId, cache: false, success: handler = function (data) {
		$j('#generalForm').loadAndCenter(data)
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function submitAjaxForm(form, patientId) {
	var postData = {'patientId': patientId}
	$j(form).find("input[type=checkbox]:checked, input[type=text], input[type=hidden], select, textarea").each(function() {
		if (!$j(this).is(":disabled")) {
			postData[$j(this).attr('name')] = $j(this).val()
		}
	})

	pleaseWaitDialog()
	var handler
	$j.ajax({url: form.action, type: 'POST', data: postData, success: handler = function (data) {
		closePleaseWaitDialog()
		var div = $j('#generalForm').loadAndCenter(data)
		highlightErrors()
		updateECCDSection(patientId, function() {
			if (div.find("div.openmrs_msg").size() > 0 && div.find("div.openmrs_error").size() == 0) {
				div.dialog("close")
			}
		})		
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function submitAjaxServicesForm(form, patientId) {
	var postData = {'patientId': patientId}
	$j(form).find("input[type=checkbox]:checked, input[type=text], input[type=hidden], select, textarea").each(function() {
		if (!$j(this).is(":disabled")) {
			postData[$j(this).attr('name')] = $j(this).val()
		}
	})

	pleaseWaitDialog()
	var handler
	$j.ajax({url: form.action, type: 'POST', data: postData, success: handler = function (data) {
		closePleaseWaitDialog()
		var div = $j('#generalForm').loadAndCenter(data)
		highlightErrors()
		updateECCDSection(patientId, function() {
			if (div.find("div.openmrs_msg").size() > 0 && div.find("div.openmrs_error").size() == 0) {
				// div.dialog("close")
			}
		})		
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function loadNewbornScreeningInfoForm(patientId) {
	$j("#newbornScreeningInfoForm").loadAndCenter("<h4>Loading, Please Wait...</h4>", {width:500,title:'Update Newborn Screening Information'})
	var handler
	$j.ajax({url: 'updateChildCareNewbornScreeningInformation.form?patientId=' + patientId, cache: false, success: handler = function (data) {
		$j('#newbornScreeningInfoForm').loadAndCenter(data)
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function submitNewbornScreeningInfo(form, patientId) {
	var postData = {'patientId': patientId}
	$j(form).find("input[type=text], input[type=hidden], select, textarea").each(function() {
		if (!$j(this).is(":disabled")) {
			postData[$j(this).attr('name')] = $j(this).val()
		}
	})

	pleaseWaitDialog()
	var handler
	$j.ajax({url: form.action, type: 'POST', data: postData, success: handler = function (data) {
		closePleaseWaitDialog()
		var div = $j('#newbornScreeningInfoForm').loadAndCenter(data)
		highlightErrors()
		updateECCDSection(patientId, function() {
			if (div.find("div.openmrs_msg").size() > 0 && div.find("div.openmrs_error").size() == 0) {
				$j("#newbornScreeningInfo").html($j("#updatedNewbornScreeningInfo").html())
				$j("#deliveryInfoMsg").html($j("#screeningInfoMsg").html())
				div.dialog("close")
			}
		})
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}