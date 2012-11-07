$j(document).ready(function() {
	$j("#general-visits").dataTable({"bSort": false })
	$j("#special-visits").dataTable({"bSort": false })
	// $j("textarea.notes").attr('wrap', 'off')
	$j('input[type=button], input[type=submit]').button()
})

function updateVisitsSection(patientId, onUpdate) {
	var handler
	$j.ajax({url: 'viewPatient.form?patientId=' + patientId + '&section=visit-details', cache: false, success: handler = function (data) {
		$j('#visit-details').empty().html(data)
		if (onUpdate) {
			onUpdate()
		}
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function loadAnthropometricHistory(titleText, patientId) {
	$j("#anthropometricHistory").loadAndCenter("<h4>Loading, Please Wait...</h4>",{width: 500,title:titleText})
	var handler
	$j.ajax({url: 'viewPatientAnthropometricHistory.form?patientId=' + patientId, cache: false, success: handler = function (data) {
		$j('#anthropometricHistory').loadAndCenter(data)
		$j('#height-and-weight-measurements').dataTable({"bSort": false })
		$j('#circumference-of-waist-and-hip').dataTable({"bSort": false })
		$j('#circumference-of-head-and-chest').dataTable({"bSort": false })
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function loadVitalSignsHistory(titleText, patientId) {
	$j("#vitalSignsHistory").loadAndCenter("<h4>Loading, Please Wait...</h4>",{width:530,title:titleText})
	var handler
	$j.ajax({url: 'viewPatientVitalSignsHistory.form?patientId=' + patientId, cache: false, success: handler = function (data) {
		$j('#vitalSignsHistory').loadAndCenter(data)
		$j('#vital-signs-history').dataTable({"bSort": false })
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function loadConsultHistory(titleText, patientId, encounterId) {
	$j("#consultHistory").loadAndCenter("<h4>Loading, Please Wait...</h4>",{width:'98%',title:titleText})
	var handler
	$j.ajax({url: 'viewPatientHistoricalConsult.form?patientId=' + patientId + '&encounterId=' + encounterId, cache: false, success: handler = function (data) {
		$j('#consultHistory').loadAndCenter(data)
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function loadAnthropometricUpdateForm(titleText, patientId) {
	$j("#updateAnthropometricDataForm").loadAndCenter("<h4>Loading, Please Wait...</h4>",{width:400,title:titleText})
	var handler
	$j.ajax({url: 'updateAnthropometricData.form?patientId=' + patientId, cache: false, success: handler = function (data) {
		$j('#updateAnthropometricDataForm').loadAndCenter(data)
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function submitAnthropometricDataForm(form, patientId) {
	var postData = {'patientId': patientId}
	$j(form).find("input").each(function() {
		postData[$j(this).attr('name')] = $j(this).val()
	})

	pleaseWaitDialog()
	var handler
	$j.ajax({url: 'updateAnthropometricData.form', type: 'POST', data: postData, success: handler = function (data) {
		closePleaseWaitDialog()
		var div = $j('#updateAnthropometricDataForm').loadAndCenter(data)
		highlightErrors()
		updateVisitsSection(patientId, function() {
			if (div.find("div.openmrs_msg").size() > 0 && div.find("div.openmrs_error").size() == 0) {
				div.dialog("close")
			}
		})
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function loadVitalSignsUpdateForm(titleText, patientId) {
	$j("#updateVitalSignsDataForm").loadAndCenter("<h4>Loading, Please Wait...</h4>",{width:400,title:titleText})
	var handler
	$j.ajax({url: 'updateVitalSignsData.form?patientId=' + patientId, cache: false, success: handler = function (data) {
		$j('#updateVitalSignsDataForm').loadAndCenter(data)
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function submitVitalSignsDataForm(form, patientId) {
	var postData = {'patientId': patientId}
	$j(form).find("input").each(function() {
		postData[$j(this).attr('name')] = $j(this).val()
	})

	pleaseWaitDialog()
	var handler
	$j.ajax({url: 'updateVitalSignsData.form', type: 'POST', data: postData, success: handler = function (data) {
		closePleaseWaitDialog()
		var div = $j('#updateVitalSignsDataForm').loadAndCenter(data)
		highlightErrors()

		updateVisitsSection(patientId, function() {
			if (div.find("div.openmrs_msg").size() > 0 && div.find("div.openmrs_error").size() == 0) {
				div.dialog("close")
			}
		})
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function loadUpdateNotesForm(titleText, patientId, type) {
	$j("#updateNotesForm").loadAndCenter("<h4>Loading, Please Wait...</h4>",{width:500,title:titleText})
	var handler
	$j.ajax({url: 'updateVisitNotes.form?patientId=' + patientId + '&type=' + type, cache: false, success: handler = function (data) {
		$j('#updateNotesForm').loadAndCenter(data)
		$j('input[type=button], input[type=submit]').button()
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function submitNotesForm(form, patientId) {
	var postData = {'patientId': patientId}
	$j(form).find("input, textarea").each(function() {
		var name = $j(this).attr('name')
		if (name.match(/\[\]$/)) {
			if (postData[name] == undefined) { postData[name] = [] }
			postData[name].push($j(this).val())
		} else {
			postData[name] = $j(this).val()
		}
	})

	pleaseWaitDialog()
	var handler
	$j.ajax({url: 'updateVisitNotes.form', type: 'POST', data: postData, success: handler = function (data) {
		closePleaseWaitDialog()
		var div = $j('#updateNotesForm').loadAndCenter(data);
		highlightErrors()

		updateVisitsSection(patientId, function() {
			if (div.find("div.openmrs_msg").size() > 0 && div.find("div.openmrs_error").size() == 0) {
				div.dialog("close")
			}
		})
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function enrollInNew(titleText) {
	$j("#enrollInProgram").dialog({width: 340,height:'auto',title:titleText,modal:true,buttons:{
		"Enroll":function(){$j(this).find("form").submit()},
		"Cancel":function(){$j(this).dialog("close")}
	}}).dialog('open')
}

function loadSetConsultTimestampForm(patientId) {
	$j("#generalForm").loadAndCenter("<h4>Loading, Please Wait...</h4>",{width:400,title:'Update Consult Timestamp'})
	var handler
	$j.ajax({url: 'setConsultTimestamp.form?patientId=' + patientId, cache: false, success: handler = function (data) {
		$j('#generalForm').loadAndCenter(data)
		$j('input[type=button], input[type=submit]').button()
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function submitSetConsultTimestampForm(form, patientId) {
	var postData = {'patientId': patientId}
	$j(form).find("input").each(function() {
		var name = $j(this).attr('name')
		postData[name] = $j(this).val()
	})

	pleaseWaitDialog()
	var handler
	$j.ajax({url: 'setConsultTimestamp.form', type: 'POST', data: postData, success: handler = function (data) {
		closePleaseWaitDialog()
		var div = $j('#generalForm').loadAndCenter(data)
		highlightErrors()

		updateVisitsSection(patientId, function() {
			if (div.find("div.openmrs_msg").size() > 0 && div.find("div.openmrs_error").size() == 0) {
				div.dialog("close")
			}
		})
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function attachCheckboxMediator(checkboxId, elementToMediateId) {
	if ($j(checkboxId).is(':checked')) {
		$j(elementToMediateId).removeAttr('disabled')
	} else {
		$j(elementToMediateId).attr('disabled', 'disabled')
	}

	$j(checkboxId).change(function() {
		if ($j(this).is(':checked')) {
			$j(elementToMediateId).removeAttr('disabled').focus().select()
		} else {
			$j(elementToMediateId).attr('disabled', 'disabled')
		}
	})
}

function attachCheckIfTextChanged(checkboxId, textElementId) {
	$j(textElementId).change(function() {
		$j(checkboxId).attr('checked', true)
	})
}

function checkboxesAsRadios(section) {
	$j(section).find("input[type=checkbox]").click(function() {
		if ($j(this).is(":checked")) {
			var ticked = $j(this).get(0)
			$j(section).find("input[type=checkbox]").each(function() {
				if ($j(this).get(0) != ticked) {
					$j(this).removeAttr('checked')
				}
			})
		}
	})
}

function loadAjaxForm(formAction, titleText, patientId, width) {
	if (width == undefined) { width = 580}
	$j("#generalForm").loadAndCenter("<h4>Loading, Please Wait...</h4>", {width:width,title:titleText})
	var handler
	$j.ajax({url: formAction, cache: false, success: handler = function (data) {
		$j('#generalForm').loadAndCenter(data)
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}

function loadHistoryChart(formAction, titleText, patientId, width) {
	if (width == undefined) { width = 580}
	$j("#historyChart").loadAndCenter("<h4>Loading, Please Wait...</h4>", {width:width,title:titleText})
	var handler
	$j.ajax({url: formAction, cache: false, success: handler = function (data) {
		$j('#historyChart').loadAndCenter(data)
		highlightErrors()
	}, error: function(jqXHR) {handler(jqXHR.responseText)}})
}