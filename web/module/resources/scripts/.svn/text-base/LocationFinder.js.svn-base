function LocationFinder() {
	this.brgyHtml = "<div style='display: none'>\n"
		+ "<h6>NOTE: For best results, enter a city before searching for the barangay.</h6>"
	    + "	<table>\n"
	    + "	<tr class='city'><td>City / Municipality:</td><td><input type='text' class='autocomplete'/></td><td><label>[Please select]</label></td></tr>\n"
	    + "	<tr class='barangay'><td>Barangay:</td><td><input type='text' class='autocomplete'/></td><td><label>[Please select]</label></td></tr>\n"
	    + "	</table>\n"
	    + "</div>\n";
	this.cityHtml = "<div style='display: none'>\n"
	    + "	<table>\n"
	    + "	<tr class='city'><td>City / Municipality:</td><td><input type='text' class='autocomplete'/></td><td><label>[Please select]</label></td></tr>\n"
	    + "	</table>\n"
	    + "</div>\n";

	var instance = this;
	this.city = undefined;
	this.barangay = undefined;
	this.findBarangay = function(callback, initialCity) {
		this.city = undefined;
		this.barangay = undefined;

		var brgyDlg = jQuery(this.brgyHtml).dialog({
			width: 580,
			title: "Find Barangay",
			modal: true,
			buttons: {
				"OK": function() {
					if (instance.barangay) {
						jQuery(this).dialog('close')
						if (callback) { callback(instance.barangay) }
					} else {
						alert('Please choose a barangay from the list first.');
					}	
				}
			}
		})
		brgyDlg.dialog('open');
		jQuery(brgyDlg).find(".city input").focus();
		
		jQuery(brgyDlg).find(".city input").autocomplete({
			minLength: 2,
			delay: 600,
			source: function(request, response) {
				DWRBarangayService.findMunicipalities(0, 0, request.term, function(municipalities) {
					jQuery.each(municipalities, function() {
						this.label = this.name + ", " + this.province.name + ", " + this.province.region.name;
						this.value = this.name;
					});

					response(municipalities);
				});
			},
			select: function(event, ui) {
				instance.city = ui.item;
				jQuery(brgyDlg).find(".city label").html(instance.city.municipalityCode);
				setTimeout( function() { jQuery(brgyDlg).find(".barangay input").focus() }, 0 );
			} 
		});

		jQuery(brgyDlg).find(".barangay input").autocomplete({
			minLength: 2,
			delay: 600,
			source: function(request, response) {
				DWRBarangayService.findBarangays(0, 0, instance.city ? instance.city.municipalityCode : 0, request.term, function(barangays) {
					jQuery.each(barangays, function() {
						this.label = this.name + ", " + this.municipality.name, + this.municipality.province.name + ", " + this.municipality.province.region.name;
						this.value = this.name;
					});

					response(barangays);
				});
			},
			select: function(event, ui) {
				instance.barangay = ui.item;
				instance.city = ui.item.municipality;
				jQuery(brgyDlg).find(".city label").html(instance.city.municipalityCode);
				jQuery(brgyDlg).find(".city input").attr('value', instance.city.name);
				jQuery(brgyDlg).find(".barangay label").html(instance.barangay.barangayCode);
				brgyDlg.dialog('close')	
				if (callback) { callback(instance.barangay) }
			}
		});
		
		if (initialCity) {
			this.city = initialCity; 
			jQuery(brgyDlg).find(".city input").val(this.city.name);
			jQuery(brgyDlg).find(".city label").html(this.city.municipalityCode);
			setTimeout( function() { jQuery(brgyDlg).find(".barangay input").focus() }, 0 );
		}
	}

	this.findMunicipality = function(callback) {
		this.city = undefined;
		this.barangay = undefined;
		
		var cityDlg = jQuery(this.cityHtml).dialog({
			width: 580,
			title: "Find City / Municipality",
			modal: true,
			buttons: {
				"OK": function() {
					if (instance.city) {
						jQuery(this).dialog('close')	
						if (callback) { callback(instance.city) }
					} else {
						alert('Please choose a city / municipality from the list first.');
					}
				}
			}
		})
		cityDlg.dialog('open');
		jQuery(cityDlg).find(".city input").focus();
		jQuery(cityDlg).find(".city input").autocomplete({
			minLength: 2,
			delay: 1000,
			source: function(request, response) {
				DWRBarangayService.findMunicipalities(0, 0, request.term, function(municipalities) {
					jQuery.each(municipalities, function() {
						this.label = this.name + ", " + this.province.name + ", " + this.province.region.name;
						this.value = this.name;
					});

					response(municipalities);
				});
			},
			select: function(event, ui) {
				instance.city = ui.item;
				jQuery(cityDlg).find(".city label").html(instance.city.municipalityCode);
				cityDlg.dialog('close')	
				if (callback) { callback(instance.city) }
			}
		});
	}
}
